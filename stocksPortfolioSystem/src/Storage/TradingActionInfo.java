package Storage;

import java.io.PrintWriter;
import java.util.Scanner;

import Trading.TradingAction;

public class TradingActionInfo {
	
	private Integer id;
	private String shareName;
	private Integer price;
	private int amount;
	
	
	public TradingActionInfo(Integer id ,String share, Integer price, int amount) {
		super();
		this.id = id;
		this.shareName = share;
		this.price = price;
		this.amount = amount;
	}
	
	public TradingActionInfo(Scanner s) {
		super();
		this.id = s.nextInt();
		s.nextLine();
		this.shareName = s.nextLine();
		this.price = s.nextInt();
		s.nextLine();
		this.amount = s.nextInt();
		s.nextLine();
	}
	
	public void saveToFile(PrintWriter out){
		out.println(id);
		out.println(shareName);
		out.println(price);
		out.println(amount);
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShareName() {
		return shareName;
	}

	public void setShareName(String shareName) {
		this.shareName = shareName;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
}
