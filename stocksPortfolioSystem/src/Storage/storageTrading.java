package Storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import Trading.Ask;
import Trading.Bid;
import Trading.TradingAction;

public class storageTrading {
	
	public final String OPEN_BIDS_FILE_NAME="openBids.txt";
	public final String OPEN_ASKS_FILE_NAME="openAsks.txt";

	private File fileBids;
	private File fileAsks;

	public storageTrading() throws IOException{
		fileBids = new File(OPEN_BIDS_FILE_NAME);
		fileAsks = new File(OPEN_ASKS_FILE_NAME);

		if (!fileBids.exists())
			Files.createFile(Paths.get(OPEN_BIDS_FILE_NAME));
		
		if (!fileAsks.exists())
			Files.createFile(Paths.get(OPEN_ASKS_FILE_NAME));
	}
	
	public void storeTradingActionList(List<TradingActionInfo> list, boolean isBid) throws FileNotFoundException {
		PrintWriter out;
		if(isBid)
			out = new PrintWriter(fileBids);
		else
			out = new PrintWriter(fileAsks);
		out.println(list.size());
		for(TradingActionInfo action : list) {
			action.saveToFile(out);
		}
		out.close();
	}
	
	public List<TradingActionInfo> loadTradingActionList(boolean isBid) throws FileNotFoundException {
		List<TradingActionInfo> list = new ArrayList<TradingActionInfo>();	
		Scanner scanner;
		
		if(isBid)
			scanner = new Scanner(new FileInputStream(fileBids));
		else
			scanner = new Scanner(new FileInputStream(fileAsks));

		if(!scanner.hasNext()){
			scanner.close();
			return list;
		}
		int numOfLoads=scanner.nextInt();
		scanner.nextLine();
		while (numOfLoads>0) {
			if(isBid){
				list.add(new BidInfo(scanner));
			}else{
				list.add(new AskInfo(scanner));
			}
			numOfLoads--;
		}
		scanner.close();
		return list;
	}
}
