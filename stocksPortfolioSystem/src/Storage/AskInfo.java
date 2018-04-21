package Storage;

import java.util.Scanner;

public class AskInfo extends TradingActionInfo{

	public AskInfo(Integer id, String share, Integer price, int amount) {
		super(id, share, price, amount);
	}
	
	public AskInfo(Scanner s) {
		super(s);
	}
}
