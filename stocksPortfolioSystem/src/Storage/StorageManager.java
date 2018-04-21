package Storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

public class StorageManager {
	final static String TRANSACTION_FILE ="trans.txt";
	final static String OWNED_SHARES_FILE ="shares.txt";
	
	private File transactionFile;
	private File ownedSharesFile;
	
	public StorageManager() throws IOException {
		transactionFile = new File(TRANSACTION_FILE);
		ownedSharesFile = new File(OWNED_SHARES_FILE);
		
		if (!transactionFile.exists())
			Files.createFile(Paths.get(TRANSACTION_FILE));
		
		if (!ownedSharesFile.exists())
			Files.createFile(Paths.get(OWNED_SHARES_FILE));
	}
	
	public void storeTransactionsDetails(LinkedList<TransactionDetailsInfo> list) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(transactionFile);
		pw.println(list.size());
		for(TransactionDetailsInfo t : list){
			t.writeInto(pw);
		}
		
		pw.close();
	}
	
	public LinkedList<TransactionDetailsInfo> loadTransactionsDetails() throws FileNotFoundException{
		Scanner s = new Scanner(transactionFile);
		LinkedList<TransactionDetailsInfo> list = new LinkedList<>();
		if(s.hasNext())
		{
			int size = s.nextInt();
			s.nextLine(); // enter line
			for( int i= 0; i < size ; i ++){
				list.add(new TransactionDetailsInfo(s));
			}
		}
		s.close();
		return list;
	}
	
	public void storeOwnedShares(ArrayList<ShareInfo> list) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(ownedSharesFile);
		pw.println(list.size());
		for(ShareInfo t : list){
			t.writeInto(pw);
		}
		pw.close();
	}
	
	public ArrayList<ShareInfo> loadOwnedShares() throws FileNotFoundException{
		Scanner s = new Scanner(ownedSharesFile);
		ArrayList<ShareInfo> list = new ArrayList<>();
		if(s.hasNext()){
			
			int size = s.nextInt();
			s.nextLine(); // enter line
			boolean type;
			for( int i= 0; i < size ; i ++){
				type=s.nextBoolean();
				s.nextLine();
				if(type) //true is stock, false is bond
					list.add(new StockInfo(s));
				else
					list.add(new BondInfo(s));
			}
		}
		s.close();
		return list;	
	}
	
}
