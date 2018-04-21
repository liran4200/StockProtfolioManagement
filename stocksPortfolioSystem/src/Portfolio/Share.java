package Portfolio;

public abstract class  Share {
	
	private static int  idGen=1;
	
	protected int id;
	protected String name;
	protected Integer price;
	
	public Share(String name,Integer price){
		this.name = name;
		this.price = price;
		this.id = idGen++;
	}
	
	public Share(Share share) {
		this.id = share.getId();
		this.name = share.getName();
		this.price = share.getPrice();
	}
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public Integer getPrice(){
		return price;
	}
	
	public void setPrice(Integer newPrice){
		this.price = newPrice;
	}
	@Override
	public String toString() {
		return this.name+", "+"Price: "+this.price;
	}
}
