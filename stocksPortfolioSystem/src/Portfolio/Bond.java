package Portfolio;

public class Bond extends Share {

	public Bond(String name, Integer price) {
		super(name, price);
	}

	public Bond(Share share) {
		super(share);
	}
	@Override
	public String toString() {
		return "Bond: "+super.toString();
	}
}
