package Trading;

import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import auth.api.WrongSecretException;
import bank.api.BankManager;
import exchange.api.ExchangeManager;
import exchange.api.InternalExchangeErrorException;
import exchange.api.NoSuchAccountException;
import exchange.api.Order;
import Portfolio.*;

public class CheckForUpdates implements Runnable{
	private TradingManager tm;
	private ExchangeManager stockExchange;
	private PortfolioManager portfolio;
	private  List</*idWrap*/Integer> openOrdersPast;
	private  List<Integer> openOrdersCurrent;
	private boolean isRunning;
	
	public CheckForUpdates(TradingManager tm) throws RemoteException, NoSuchAccountException, WrongSecretException, InternalExchangeErrorException {
		this.tm = tm;
		portfolio=tm.getPortfolio();
		stockExchange = tm.getStockExchange();
		isRunning=true;
		openOrdersPast=stockExchange.getOpenOrders(portfolio.getSecret(),portfolio.getAccountNumber());
	}

	@Override
	public void run() {
		while (isRunning) {
			try {
				openOrdersCurrent=stockExchange.getOpenOrders(portfolio.getSecret(),portfolio.getAccountNumber());
				for(Integer pastOrderId : openOrdersPast){
					Integer currentOrderId=null;
					for(Integer id : openOrdersCurrent){//find
						if(id.intValue()==pastOrderId.intValue())
							currentOrderId=id;
					}
					Order orderPast=stockExchange.getOrderDetails(portfolio.getSecret(), portfolio.getAccountNumber(), pastOrderId);
					if(currentOrderId==null){//an order as been ended
						tm.updatePortfolio(orderPast.getStockName(), orderPast.getAmount(),orderPast.getPrice(),orderPast.getKind());
					}else{
						Order orderCurrent=stockExchange.getOrderDetails(portfolio.getSecret(), portfolio.getAccountNumber(), currentOrderId);
						if(!orderCurrent.getAmount().equals(orderPast.getAmount())){//an amount as changed
							tm.updatePortfolio(orderPast.getStockName(), orderPast.getAmount()-orderCurrent.getAmount(),orderPast.getPrice(),orderPast.getKind());
						}
					}
				}
				openOrdersPast=openOrdersCurrent;
				Thread.sleep(1000);
				
			} catch (RemoteException | NoSuchAccountException | WrongSecretException | InternalExchangeErrorException e) {
				e.printStackTrace();
			} catch (InterruptedException i) {
				stopTread();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void stopTread(){
		isRunning=false;
	}
	
}
