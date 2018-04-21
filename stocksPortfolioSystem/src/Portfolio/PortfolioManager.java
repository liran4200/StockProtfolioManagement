package Portfolio;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import exchange.api.ExchangeManager;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import Storage.BondInfo;
import Storage.ShareInfo;
import Storage.StockInfo;
import Storage.StorageManager;
import Storage.TransactionDetailsInfo;
import auth.api.WrongSecretException;
import bank.api.BankManager;
import bank.api.DoesNotHaveThisAssetException;
import bank.api.InternalServerErrorException;
import bank.api.NotEnoughAssetException;
import bank.api.Transaction;
import exchange.api.ExchangeManager;

public class PortfolioManager {

	final String IPSERVER = "13.59.120.241";
	final String IPBANK = "13.59.120.241";

	private ArrayList<Share> shares;
	private LinkedList<TransactionDetails> transactionsHistory;
	private ExchangeManager exchange;
	private BankManager bank;
	private TraderAccount account;
	private StorageManager manager;

	private DateTimeFormatter formmater = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	/**Creates protfolio manager 
	 * @throws FileNotFoundException **/
	public PortfolioManager() throws IOException,MalformedURLException
	, RemoteException, NotBoundException, FileNotFoundException {
		this.exchange = (ExchangeManager) Naming.lookup("rmi://"+IPSERVER+"/Exchange");
		account = new TraderAccount();
		transactionsHistory = new LinkedList<TransactionDetails>();
		shares = new ArrayList<Share>();
		manager = new StorageManager();
		bank = (BankManager) Naming.lookup("rmi://"+IPBANK+"/Bank");
		load();
	}
	private void load() throws FileNotFoundException {
		loadTransactionsDetails();
		loadShares();
	}

	public void loadTransactionsDetails() throws FileNotFoundException {
		LinkedList<TransactionDetailsInfo> transactionInfoList = new LinkedList<>();

		transactionInfoList = manager.loadTransactionsDetails();	
		Share tempShare;

		for(TransactionDetailsInfo t : transactionInfoList){
			ShareInfo s = t.getShareInfo();
			if(s instanceof StockInfo)
				tempShare = new Stock(s.getName(),s.getPrice());
			else
				tempShare = new Bond(s.getName(), s.getPrice());

			if(t.isOwned())
				transactionsHistory.add(new TransactionDetails(tempShare, t.getBuyingDate()));
			else
				transactionsHistory.add(new TransactionDetails(tempShare, t.getBuyingDate(), t.getSellingDate(), t.getSellingPrice()));
		}
	}

	public void loadShares() throws FileNotFoundException {
		Share tempShare;
		ArrayList<ShareInfo> shareInfoList = new ArrayList<>();
		
		shareInfoList = manager.loadOwnedShares();

		for(ShareInfo share : shareInfoList){
			if(share instanceof StockInfo)
				tempShare = new Stock(share.getName(), share.getPrice());
			else
				tempShare = new Bond(share.getName(), share.getPrice());

			shares.add(tempShare);
		}
	}

	/**Updates share list and buying details
	 * @throws FileNotFoundException **/
	public void updateBuying(String name, Integer amount, Integer price) throws FileNotFoundException {
		for(int i=0;i<amount;i++){
			addShare(name, price);
			addTransactionDetails(shares.get(shares.size()-1));
		}
		save();
	}

	/**Add share to the list**/
	private void addShare(String name, Integer price){
		this.shares.add(new Stock(name, price)); 
	}

	/**add share transcation to the history**/
	private void addTransactionDetails(Share share){
		this.transactionsHistory.addFirst(new TransactionDetails(share, LocalDateTime.now()));
	}
	/**Returns String of History since specific date**/
	
	public String showHistorySince(LocalDateTime date) {
		StringBuilder sb = new StringBuilder("\nHistory since date: "+ date.format(formmater )+"\n");
		for(TransactionDetails t : transactionsHistory){
			if(date.compareTo(t.getBuyingDate())<=0)
				sb.append(t + "\n");
		}
		return sb.toString();
	}
	/**Returns String of History by specific name**/
	public String showHistoryByShareName(String name){
		StringBuilder sb = new StringBuilder("\nHistory of "+name+"\n");
		for(TransactionDetails t : transactionsHistory){
			if(t.getShare().getName().equals(name)){
				sb.append(t +"\n");
			}
		}

		return sb.toString();
	}

	/**Updates share list and selling details**/
	public void updateSelling(String name,Integer amount, Integer price) throws Exception {
		checkShareAmount(name, amount);
		int count =0 ;
		int i=0;
		while(i <shares.size()){
			if(shares.get(i).getName().equals(name)){
				count++;
				updateSellingShare(shares.remove(i).getId(), price);	
				if(count == amount)
					break;
			}else
				i++;
		}	
		save();
	}


	private void updateSellingShare(int id, Integer price) {
		for(TransactionDetails t:transactionsHistory){
			if(!t.isSold())
				if(t.getShare().getId() == id){
					t.setSellingDate(LocalDateTime.now());
					t.setSellingPrice(price);
				}
		}
	}

	private void checkShareAmount(String name,Integer amount) throws Exception{
		int count=0;
		for(int i=0;i<shares.size(); i ++){
			if(shares.get(i).getName().equals(name))
				count++;
		}

		if(count < amount.intValue()){
			throw new Exception("There are only "+count+" "+name+" shares");
		}
		else if(count==0)
			throw new Exception("There is not such share");
	}

	/**Calculates profit in percent
	 * @throws Exception **/
	public float averageProfitCalc(String name) throws Exception{
		checkShareAmount(name, 1);

		float profit, average;
		int count=0, pricesSum=0;

		for(int i=0;i<count;i++){
			if(shares.get(i).getName().equals(name)){
				count++;
				pricesSum+=shares.get(i).getPrice();
			}
		}
		average = pricesSum/count;
		profit = getCurrentPrice(name)/average;

		profit*=100;
		return  profit;		
	}

	/**Get the current lowest price of stock in market, searches by name **/
	public int getCurrentPrice(String name) throws RemoteException{

		Map<Integer, Integer> map = exchange.getDemand(name);
		Set<Integer> set = map.keySet();
		int min = set.iterator().next();


		for(Integer price:set){
			if(min > price)
				min = price;
		}

		return min;
	}
	/**Returns String of owned Shares**/
	public String showOwnedShares(){
		return shares.toString();
	}

	/**Returns String of Transaction history**/
	public String showTransationHistory(){
		StringBuilder sb= new StringBuilder();
		for(TransactionDetails t:transactionsHistory)
			sb.append(t+"\n");

		return sb.toString();
	}
	
	public boolean isExist(String name) {
		for (Share share : shares) {
			if(share.name.equals(name))
				return true;
		}
		
		return false;
	}

	public int getBankBalance() throws RemoteException, WrongSecretException, DoesNotHaveThisAssetException, InternalServerErrorException {
		
		Integer amountOfNIS = bank.getQuantityOfAsset(getSecret(), getAccountNumber(), "NIS");
		return amountOfNIS.intValue();
		
		
		
	}
	
	
	public int getShareAmount(String name){
		int count=0;
		for(int i=0;i<shares.size(); i ++){
			if(shares.get(i).getName().equals(name))
				count++;
		}
		return count;
	}
	
	public int getStocksAmount(){
		return shares.size();
	}
	
	
	
	/**Returns String of Selling shares**/
	public String getSellingShares() {
		StringBuilder sb = new StringBuilder("\nHistory of Selling shares\n");
		for(TransactionDetails t : transactionsHistory){
			if(t.isSold())
				sb.append(t+"\n");
		}
		return sb.toString();
	}

	public String getSecret(){
		return account.getSECRET();
	}

	public Integer getAccountNumber(){
		return account.getACCOUNT_NUM();
	}

	public String getName(){
		return account.getName();
	}

	private void save() throws FileNotFoundException{
		saveTransactionsDetails();
		saveShares();
	}
	private void saveShares() throws FileNotFoundException {
		ArrayList<ShareInfo> list= new ArrayList<>();
		ShareInfo shareInfo;
		for(Share s : shares){
			if(s instanceof Stock)
				shareInfo = new StockInfo(s.getName(),s.getPrice());
			else
				shareInfo = new BondInfo(s.getName(),s.getPrice());
			list.add(shareInfo);
		}
		manager.storeOwnedShares(list);
	}
	private void saveTransactionsDetails() throws FileNotFoundException {
		LinkedList<TransactionDetailsInfo> list = new LinkedList<>();
		ShareInfo tempShareInfo;

		for(TransactionDetails t :transactionsHistory){
			if(t.getShare() instanceof Stock){
				tempShareInfo = new StockInfo(t.getShare().getName(),t.getShare().getPrice());
			}
			else{
				tempShareInfo = new BondInfo(t.getShare().getName(),t.getShare().getPrice());
			}

			list.add( new TransactionDetailsInfo(tempShareInfo,t.getBuyingDate()
					,t.getSellingDate(),t.getSellingPrice()));
		}

		manager.storeTransactionsDetails(list);

	}
}
