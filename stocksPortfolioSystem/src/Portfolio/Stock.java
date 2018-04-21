package Portfolio;

public class Stock extends Share{
	
	public Stock( String name, Integer price) {
		super(name, price);
	}
	
	public Stock(Share share) {
		super(share);
	}

	@Override
	public String toString() {
		return "Stock: "+super.toString();
	}
	
}
