package Trading;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import Interface.PortfolioManagerApp;
import Storage.TradingActionInfo;
import Storage.storageTrading;
import auth.api.WrongSecretException;
import bank.api.BankManager;
import bank.api.DoesNotHaveThisAssetException;
import bank.api.InternalServerErrorException;
import bank.api.NotEnoughAssetException;
import exchange.api.DoesNotHaveThisStockException;
import exchange.api.ExchangeManager;
import exchange.api.InternalExchangeErrorException;
import exchange.api.NoSuchAccountException;
import exchange.api.NotEnoughMoneyException;
import exchange.api.NotEnoughStockException;
import exchange.api.Order;
import exchange.api.StockNotTradedException;
import Portfolio.*;

public class TradingManager {

	private final static String BANK_SERVER_ADRESS = "rmi://13.59.120.241/Bank";
	private final static String EXCHANGE_SYS_SERVER_ADRESS = "rmi://13.59.120.241/Exchange";
	
	private final static String NIS = "NIS";
	private final static Integer STOCK_EXCHANGE_NUMBER = 3373;
	
	private BankManager bank;
	private ExchangeManager stockExchange;
	private LinkedList<TradingAction> openBids;
	private LinkedList<TradingAction> openAsks;
	private Thread checkUpdatesThread;
	private storageTrading storage;
	private PortfolioManager portfolio;

	public TradingManager(PortfolioManager portfolio) throws NotBoundException, NoSuchAccountException, WrongSecretException, InternalExchangeErrorException, IOException,Exception {
		this.bank = (BankManager) Naming.lookup(BANK_SERVER_ADRESS);
		this.stockExchange = (ExchangeManager) Naming.lookup(EXCHANGE_SYS_SERVER_ADRESS);	
		this.portfolio=portfolio;
		storage = new storageTrading();
		initOpenAsksAndBids();
		startThread();
	}

	public void placeBid(String assetName, Integer amount,Integer price) throws RemoteException, DoesNotHaveThisAssetException, NotEnoughAssetException, WrongSecretException, InternalServerErrorException, InterruptedException, NoSuchAccountException, NotEnoughStockException, StockNotTradedException, InternalExchangeErrorException, NotEnoughMoneyException, FileNotFoundException {
		bank.transferAssets(portfolio.getSecret(), portfolio.getAccountNumber(), STOCK_EXCHANGE_NUMBER , NIS, price);
		Thread.sleep(1000);
		Integer bidId=stockExchange.placeBid(portfolio.getSecret(), portfolio.getAccountNumber(), assetName, amount, price);
		Bid bidPlaced=new Bid(bidId, assetName, price, amount);
		openBids.add(bidPlaced);
		save(openBids,true);		
	}

	public void placeAsk(String assetName, Integer amount,Integer price) throws RemoteException, WrongSecretException, NoSuchAccountException, NotEnoughStockException, StockNotTradedException, DoesNotHaveThisStockException, InternalExchangeErrorException, FileNotFoundException, DoesNotHaveThisAssetException, NotEnoughAssetException, InternalServerErrorException, InterruptedException{
		Integer askId=stockExchange.placeAsk(portfolio.getSecret(), portfolio.getAccountNumber(), assetName, amount, price);
		Ask askPlaced=new Ask(askId, assetName, price, amount);
		openAsks.add(askPlaced);
		save(openAsks,false);
	}

	public void printOpenBid(){
		for(TradingAction b: openBids){
			System.out.println(b.toString());
		}
	}

	public void printOpenAsk(){
		for(TradingAction a: openAsks){
			System.out.println(a.toString());
		}
	}

	private void initOpenAsksAndBids() throws RemoteException, NoSuchAccountException, WrongSecretException, InternalExchangeErrorException, FileNotFoundException,Exception {
		//load list from stock exchange
		List<Integer> openOrders=stockExchange.getOpenOrders(portfolio.getSecret(), portfolio.getAccountNumber());
		openBids = new LinkedList<>();
		openAsks = new LinkedList<>();
		for(Integer id: openOrders){
			Order currentOrder=stockExchange.getOrderDetails(portfolio.getSecret(), portfolio.getAccountNumber(), id);
			if(isBid(currentOrder)){
				openBids.add(new Bid(id, currentOrder.getStockName(), currentOrder.getPrice(), currentOrder.getAmount()));
			}else{
				openAsks.add(new Ask(id, currentOrder.getStockName(), currentOrder.getPrice(), currentOrder.getAmount()));
			}
		}
		//load list from files
		List<TradingAction> oldOpenBids=load(true);
		List<TradingAction> oldOpenAsk=load(false);
		//compare the two lists and update the portfolio
		for(TradingAction t: oldOpenBids){
			TradingAction temp=findBidInOpenBids(t.getId());
			if(temp==null){//if it is not found then update portfolio
				updatePortfolio(t.getShare(), t.getAmount(),t.getPrice(),Order.BID);
			}else{
				if(temp.getAmount()!=t.getAmount()){//if it is found then check if amount has changed
					updatePortfolio(t.getShare(), t.getAmount()-temp.getAmount(),t.getPrice(),Order.BID);
				}
			}
		}
		for(TradingAction t: oldOpenAsk){
			TradingAction temp=findAskInOpenAsks(t.getId());
			if(temp==null){//if it is not found then update portfolio
				updatePortfolio(t.getShare(), t.getAmount(),t.getPrice(),Order.ASK);
			}else{
				if(temp.getAmount()!=t.getAmount()){//if it is found then check if amount has changed
					updatePortfolio(t.getShare(), t.getAmount()-temp.getAmount(),t.getPrice(),Order.ASK);
				}
			}
		}
	}

	public boolean isBid(Order o){
		return o.getKind().equals(Order.BID);
	}

	public void cancelOrder(Integer orderId, boolean isBid) throws FileNotFoundException, RemoteException, WrongSecretException, NoSuchAccountException, InternalExchangeErrorException{
		updateOrderAmount(orderId, 0, isBid);
	}

	public void updateOrderAmount(Integer orderId, Integer amount, boolean isBid) throws FileNotFoundException, RemoteException, WrongSecretException, NoSuchAccountException, InternalExchangeErrorException{
		Order orderToUpdate;
		TradingAction tempTradingAction;
		orderToUpdate = stockExchange.getOrderDetails(portfolio.getSecret(), portfolio.getAccountNumber(), orderId);
		orderToUpdate.setAmount(amount);
		if(isBid){
			tempTradingAction=findBidInOpenBids(orderId);
			tempTradingAction.setAmount(amount);
			save(openBids,true);
		}else{
			tempTradingAction=findAskInOpenAsks(orderId);
			tempTradingAction.setAmount(amount);
			save(openAsks,false);
		}
	}

	private TradingAction findBidInOpenBids(Integer id){
		for(TradingAction b: openBids){//find the old bid in list
			if(b.getId().equals(id))
				return b;
		}
		return null;
	}

	private TradingAction findAskInOpenAsks(Integer id){
		for(TradingAction a: openAsks){//find the old bid in list
			if(a.getId().equals(id))
				return a;
		}
		return null;
	}

	public void updatePortfolio(String shareName, Integer amount, Integer price, String kind) throws Exception{
		if (kind.equals(Order.BID))
			portfolio.updateBuying(shareName, amount, price);
		else portfolio.updateSelling(shareName, amount, price);
	}

	private void stopThread(){
		checkUpdatesThread.interrupt();
	}

	private void startThread() throws RemoteException, NoSuchAccountException, WrongSecretException, InternalExchangeErrorException{
		checkUpdatesThread=new Thread(new CheckForUpdates(this));
		checkUpdatesThread.start();
	}

	public BankManager getBank() {
		return bank;
	}

	public ExchangeManager getStockExchange() {
		return stockExchange;
	}

	public LinkedList<TradingAction> getOpenBids() {
		return openBids;
	}

	public LinkedList<TradingAction> getOpenAsk() {
		return openAsks;
	}

	public PortfolioManager getPortfolio() {
		return portfolio;
	}

	public void setPortfolio(PortfolioManager portfolio) {
		this.portfolio = portfolio;
	}
	
	public Map<Integer, Integer> searchSpecificStock(String stockName) throws RemoteException{
		return stockExchange.getSupply(stockName);
	}

	private void save(List<TradingAction> toSave, boolean isBid) throws FileNotFoundException {
		List<TradingActionInfo> list = new LinkedList<TradingActionInfo>();
		for(TradingAction t : toSave) {
			list.add(new TradingActionInfo(t.getId(), t.getShare(), t.getPrice(), t.getAmount()));
		}
		storage.storeTradingActionList(list,isBid);
	}

	private List<TradingAction> load(boolean isBid) throws FileNotFoundException {
		List<TradingActionInfo> list = storage.loadTradingActionList(isBid);
		List<TradingAction> listToReturn = new LinkedList<>();		
		if(isBid){
			for(TradingActionInfo temp : list)
				listToReturn.add(new Bid(temp.getId(), temp.getShareName(), temp.getPrice(), temp.getAmount()));
		}else{
			for(TradingActionInfo temp : list)
				listToReturn.add(new Ask(temp.getId(), temp.getShareName(), temp.getPrice(), temp.getAmount()));
		}
		return listToReturn;
	}
}
