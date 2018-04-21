package Storage;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


public class TransactionDetailsInfo {

	private ShareInfo share; 
	private boolean type; // true- stock , false- bond
	private LocalDateTime buyingDate;
	private boolean isOwned; // true - owned , false -sold: meaning null
	private LocalDateTime sellingDate;
	private Integer sellingPrice;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	

	public TransactionDetailsInfo(Scanner s ) {
		this.type = s.nextBoolean();
		s.nextLine(); // enter line 
		if(type)
			this.share = new StockInfo(s); 
		else
			this.share = new BondInfo(s);
		this.buyingDate = LocalDateTime.parse(s.nextLine(), formatter);
		
		this.isOwned = s.nextBoolean();
		s.nextLine(); // enter line 
		
		if(isOwned){
			this.sellingDate = LocalDateTime.parse(s.nextLine(), formatter);
			this.sellingPrice = s.nextInt();
			s.nextLine();
		}else
		{
			this.sellingDate = null;
			this.sellingPrice = 0;
		}	
	}
	
	public TransactionDetailsInfo(ShareInfo share,LocalDateTime buyingDate 
			,LocalDateTime sellingDate,Integer sellingPrice) {
		this.share = share;
		this.buyingDate = buyingDate;
		this.sellingDate =sellingDate;
		this.sellingPrice = sellingPrice;
	}

	public boolean isOwned() {
		return isOwned;
	}

	public LocalDateTime getBuyingDate() {
		return buyingDate;
	}

	public LocalDateTime getSellingDate() {
		return sellingDate;
	}

	public ShareInfo getShareInfo() {
		return share;
	}

	public Integer getSellingPrice() {
		return sellingPrice;
	}

	public void writeInto(PrintWriter pw){
		share.writeInto(pw);
		pw.println(buyingDate.format(formatter));
		
		if(sellingDate == null)
			pw.println(false);
		else{
			pw.println(true);
			pw.println(sellingDate.format(formatter)+"\n"+sellingPrice);
		}
	}


}
