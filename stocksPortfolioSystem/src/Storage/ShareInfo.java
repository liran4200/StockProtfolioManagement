package Storage;

import java.io.PrintWriter;
import java.util.Scanner;

public abstract class ShareInfo {
	
	protected String name;
	protected Integer price;
	
	public ShareInfo(Scanner s){
		this.name = s.nextLine();
		this.price = s.nextInt();
		s.nextLine();
	}
	
	public ShareInfo(String name , Integer price){
		this.name = name ;
		this.price = price;
	}
	
	public String getName(){
		return name;
	}
	
	public Integer getPrice(){
		return price;
	}
	
	public void writeInto(PrintWriter pw) {
		pw.println(this.name);
		pw.println(this.price);
	}
	
}
