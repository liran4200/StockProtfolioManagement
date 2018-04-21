package Interface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;

import javax.naming.spi.DirStateFactory.Result;

import Portfolio.PortfolioManager;
import Trading.TradingManager;
import auth.api.WrongSecretException;
import bank.api.DoesNotHaveThisAssetException;
import bank.api.InternalServerErrorException;
import bank.api.NotEnoughAssetException;
import exchange.api.DoesNotHaveThisStockException;
import exchange.api.InternalExchangeErrorException;
import exchange.api.NoSuchAccountException;
import exchange.api.NotEnoughMoneyException;
import exchange.api.NotEnoughStockException;
import exchange.api.StockNotTradedException;

public class PortfolioManagerApp {

	private Scanner scanner;
	private TradingManager tradingManager;
	private PortfolioManager portfolioManager;
	
	public PortfolioManagerApp() throws NoSuchAccountException, WrongSecretException, InternalExchangeErrorException, IOException, Exception{
		scanner = new Scanner(System.in);
		//
		portfolioManager = new PortfolioManager();
		tradingManager = new TradingManager(portfolioManager);
		
	}
	
	public void run() {
		mainMenu();
	}
	
	private void mainMenu(){
		
		while (true) {
			showMainMenu();
			int choise = selectOperation(1, 3);
	
			switch(choise) {
			case 1: tradeStocks(); break;
			case 2: manageAccount(); break;
			case 3: System.exit(0);
			default: System.out.println("Invalid choice.");
			}
		}
	}
	

/* ****************************************************************************************
 * ***************************** Trading methods ******************************************
 * **************************************************************************************** */
	
	
	private void tradeStocks() {
		showTradeStocksMenu();
		int choise = selectOperation(1, 4);
		
		switch(choise) {
		case 1: buyStock(); break;
		case 2: sellStock(); break;
		case 3: viewOpenTrades(); break;
		case 4: mainMenu();
		default: System.out.println("Invalid choice.");
		}	
	}

	private void sellStock() {
		String stockName;
		int numOfStocks=portfolioManager.getStocksAmount();
		if(numOfStocks<1){
			System.out.println("You dont have any stocks yet, buy some");
			mainMenu();
			return;
		}
		
		System.out.println("Owned shares:");
		System.out.println(portfolioManager.showOwnedShares());	
		while (true) {
			scanner.nextLine(); //clean buffer
			System.out.println("\nEnter A stock name you wish to sell or x to return to main menu");
			stockName = scanner.nextLine();
			if (portfolioManager.isExist(stockName))
				break;
			else if(stockName.equals("x")){
				showMainMenu();
				return;
			}else
				System.out.println("no such stock, try again");
		}
		showSellStocksMenu();
		int choise2=selectOperation(1, 2);
			try {
				switch(choise2) {
				case 1: sellAtSpecificPrice(stockName); break;
				case 2: sellStock(); break;
				default: 
					System.out.println("Invalid choice.");
				}
			}catch(Exception err) {
				System.out.println("Error: " + err.getMessage());
			}
	}
			
	private void sellAtSpecificPrice(String stockName) {
		int sockAmount=portfolioManager.getShareAmount(stockName);
		System.out.println("please enter the amout that you would like to sell:");
		int amount=selectOperation(0, sockAmount);
		if(amount==0){
			showMainMenu();
			return;
		}
		System.out.println("please enter the price:");
		Integer price = scanner.nextInt();
		try {
			tradingManager.placeAsk(stockName, amount, price);
			System.out.println("Bid placed!");
		} catch (RemoteException e) {
			System.out.println("Remote Exception");
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found Exception");
		} catch (DoesNotHaveThisAssetException e) {
			System.out.println("Does Not Have This Asset Exception");
		} catch (NotEnoughAssetException e) {
			System.out.println("Not Enough Asset Exception");
		} catch (WrongSecretException e) {
			System.out.println("Wrong Secret Exception");
		} catch (InternalServerErrorException e) {
			System.out.println("Internal Server Error Exception");
		} catch (InterruptedException e) {
			System.out.println("Interrupted Exception");
		} catch (NoSuchAccountException e) {
			System.out.println("No Such Account Exception");
		} catch (NotEnoughStockException e) {
			System.out.println("Not Enough Stocks");
		} catch (StockNotTradedException e) {
			System.out.println("Stock Not Traded");
		} catch (InternalExchangeErrorException e) {
			System.out.println("Internal Exchange Error Exception");
		} catch (DoesNotHaveThisStockException e) {
			System.out.println("Does Not Have This Stock");
		}
	}

	private void buyStock() {
		showBuyStocksMenu();
		int choise=selectOperation(1, 3);
		
		try {
			switch(choise) {
			case 1: searchSpecificStock(); break;
			case 2: choseFromAllStocks(); break;
			case 3: tradeStocks(); break;
			default: System.out.println("Invalid choice.");
			}
		} catch(Exception err) {
			System.out.println("Error: " + err.getMessage());
		}
	}

	private void searchSpecificStock() throws RemoteException {
		String name;
		
		while (true) {
			System.out.println("Please enter a stock name:");
			scanner.nextLine(); //clean buffer
			name = scanner.nextLine();
		
			//search for a specific stock at the stocksExchange market
			Map<Integer, Integer> result = tradingManager.searchSpecificStock(name);
			if (!result.isEmpty()) {
				System.out.println(result);
				break;
			}
			else {
				System.out.println("this stock is not exist, try again");
			}
		}
		
		searchSpecificStockMenu();
		int choise=selectOperation(1, 3);
		
		try {
			switch(choise) {
			case 1: buyAtASpecificprice(name); break;
			case 2: searchSpecificStock(); break;
			case 3: mainMenu(); break;
			default: System.out.println("Invalid choice.");
			}
		} catch(Exception err) {
			System.out.println("Error: " + err.getMessage());
		}
	}

	private void buyAtASpecificprice(String assetName)  {
		System.out.println("Please enter the amount");
		int amount = scanner.nextInt();
		
		System.out.println("Please enter the price");
		int price = scanner.nextInt();
		try {
			tradingManager.placeBid(assetName, amount, price);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DoesNotHaveThisAssetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughAssetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongSecretException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalServerErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAccountException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughStockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StockNotTradedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InternalExchangeErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotEnoughMoneyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void choseFromAllStocks() {
		try {
			Set<String> stocksNames = tradingManager.getStockExchange().getStockNames();
			Map<Integer, Integer> supply = new HashMap<>();
			//iterate all stocks and print supply table
			for (String stockName : stocksNames) {
				supply = tradingManager.getStockExchange().getSupply(stockName);
				printSuplyForStockName(stockName, supply);	
			}
			
			chooseStockFromSctocksList(stocksNames);
			
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void chooseStockFromSctocksList(Set<String> stocksNames) {
		boolean correctName = false;

		do {
			System.out.println("Please enter a stock name:");
			String stockName = scanner.nextLine();
		
			if (stocksNames.contains(stockName)) { 
				buyAtASpecificprice(stockName);
				correctName = true;
			}
			else {
				System.out.println("no such stock, try again");
				correctName = false;
			} 
		} while (!correctName);
	}

	private void printSuplyForStockName(String stockName, Map<Integer, Integer> supply) {
		System.out.println("stock name: " + stockName);
		
		Set<Integer> amounts = supply.keySet();
		
		//print supply for each amount for stock 
		for (Integer amount : amounts) {
			System.out.print("price: " + amount + " ; ");
			System.out.print("amount: " + supply.get(amount).toString() + "\n");
		}	
	}

	private void searchSpecificStockMenu() {
		System.out.println("1 - Buy at a specific price\n"
						 + "2 - Search for another stock\n"
						 + "3 - return to the main menu.");		
	}

	private void showBuyStocksMenu() {
		System.out.println("1 - Search for a specific stock\n"
						 + "2 - chose from all stocks\n"
						 + "3 - Back.");		
	}

	private void viewOpenTrades() {
	
		System.out.println("Open Bids:");
		tradingManager.printOpenBid();
		
		System.out.println("Open Asks:");
		tradingManager.printOpenAsk();
		
		tradeStocks();
	}

	

/* ****************************************************************************************
 * ***************************** Manage account methods ***********************************
 * **************************************************************************************** */
	
	
	private void manageAccount() {
		showManageAccountMenu();
		
		int choise = selectOperation(1, 3);
		
		switch(choise) {
		case 1: showAccountBalance(); break;
		case 2: ownedSharesDetails(); break;
		case 3: mainMenu(); break;
		default: System.out.println("Invalid choice.");
		}	
	}
	
	private void showAccountBalance() {
		int amountOfNIS = 0;

		try {
			amountOfNIS = portfolioManager.getBankBalance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Current Account balance: " + amountOfNIS + " NIS\n");	
		
		manageAccount();
		
	}
	
	private void ownedSharesDetails() {
		showOwnedSharesMenu();
		
		int choise = selectOperation(1, 3);
		
		switch(choise) {
		case 1: showAllShares(); break;
		case 2: historyOfTransactions(); break; 
		case 3: manageAccount(); break;
		default: System.out.println("Invalid choice.");
		}
	}
	
	private void showAllShares() {
		String ownedShares = portfolioManager.showOwnedShares();
		
		if (ownedShares.equals("[]"))
			System.out.println("you don't have shares, buy some..");
		
		else {
			System.out.println("All shares: ");
			System.out.println(portfolioManager.showOwnedShares() + "\n");
		}
		ownedSharesDetails();
	}
	
	private void historyOfTransactions() {
		String transactionHistory = portfolioManager.showTransationHistory();
		if (transactionHistory.equals(""))
			System.out.println("no history to show\n");
		
		else {
			System.out.println("History of transactions: ");
			System.out.println(portfolioManager.showTransationHistory() + "\n");
		}
		
		ownedSharesDetails();
	}

	



/* ****************************************************************************************
 * *********************************    Menus    ******************************************
 * **************************************************************************************** */





	private void showMainMenu() {
		System.out.println("1 - Trade stocks\n"
						 + "2 - Account management\n"
						 + "3 - Exit.");
	}

	private void showSellStocksMenu() {
		System.out.println("1 - Sell at a specific price\n"
						 + "2 - choose a different stock.");
	}

	private void showTradeStocksMenu() {
		System.out.println("1 - Buy stock\n"
				 + "2 - Sell stock\n"
				 + "3 - View open trades\n"
				 + "4 - Back.");
	}
	
	private void showManageAccountMenu() {
		System.out.println("1 - Bank Account balance\n"
				 + "2 - owned share details\n"
				 + "3 - Back\n");	
	}
	
	private void showOwnedSharesMenu() {
		System.out.println("1 - See all your assets\n"
				 + "2 - See transaction history\n"
				 + "3 - Back");		
	}
	
	private Integer selectOperation(int min, int max) {
		Integer code = scanner.nextInt();
		while (code < min || code > max)
		{
			System.out.println("Invalid choice. Try again ("+min+"-"+max+")");
			code = scanner.nextInt();
		}
		return code;
	}
	
	
	
	
	public static void main(String[] args) throws NoSuchAccountException, WrongSecretException, InternalExchangeErrorException, IOException, Exception {
		(new PortfolioManagerApp()).run();	
		
	}
}





