package Storage;

import java.io.PrintWriter;
import java.util.Scanner;

public class BondInfo extends ShareInfo {

	protected String name;
	protected Integer price;
	
	public BondInfo(Scanner s) {
		super(s);
	}
	public BondInfo(String name, Integer price) {
		super(name,price);
	}
	
	public void writeInto(PrintWriter pw) {
		pw.println(false);
		super.writeInto(pw);
	}
}
