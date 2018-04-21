package Storage;

import java.io.PrintWriter;
import java.util.Scanner;

public class StockInfo extends ShareInfo{

	public StockInfo(Scanner s) {
		super(s);
	}
	
	public StockInfo(String name, Integer price) {
		super(name,price);
	}
	
	public void writeInto(PrintWriter pw) {
		pw.println(true);
		super.writeInto(pw);
	}
}
