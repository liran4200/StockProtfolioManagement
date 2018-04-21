package Storage;

import java.util.Scanner;

public class BidInfo extends TradingActionInfo{

	public BidInfo(Integer id, String share, Integer price, int amount) {
		super(id, share, price, amount);
	}
	
	public BidInfo(Scanner s) {
		super(s);
	}
}
