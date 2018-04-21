package Trading;

import java.io.PrintWriter;
import java.util.Scanner;

public abstract class TradingAction {
	protected Integer id;
	protected String shareName;
	protected Integer price;
	protected int amount;
	
	public TradingAction(Integer id ,String share, Integer price, int amount) {
		super();
		this.id = id;
		this.shareName = share;
		this.price = price;
		this.amount = amount;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShare() {
		return shareName;
	}
	public void setShare(String share) {
		this.shareName = share;
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
