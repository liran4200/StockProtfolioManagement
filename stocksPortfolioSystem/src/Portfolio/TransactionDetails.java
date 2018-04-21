package Portfolio;
import java.time.LocalDateTime;

public class TransactionDetails {

	private Share share; 
	private LocalDateTime buyingDate;
	private LocalDateTime sellingDate;
	private Integer sellingPrice;

	/**Creates Transaction Details**/
	public TransactionDetails(Share share, LocalDateTime buyingDate){
		if(share instanceof Stock)
			this.share = new Stock(share);
		else
			this.share = new Bond(share);
		
		this.buyingDate = buyingDate;
		this.sellingDate = null;
		this.sellingPrice = 0;
	}
	
	public TransactionDetails(Share share, LocalDateTime buyingDate,LocalDateTime sellingDate,Integer sellingPrice) {
		this(share, buyingDate);
		this.sellingDate = sellingDate;
		this.sellingPrice=sellingPrice;
	}
	

	/**Returns the Share of the Transaction**/
	public Share getShare() {
		return share;
	}
	/**Set selling date**/
	public void setSellingDate(LocalDateTime sellingDate) {
		this.sellingDate = sellingDate;
	}

	/**Set selling price**/
	public void setSellingPrice(Integer sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	/**Returns Transaction Details**/
	@Override
	public String toString() {
		if(sellingDate == null)
			return share + ", Transcation date: "+ buyingDate;
		else
			return share + ", Transcation date: "+ buyingDate + "Selling date: " 
			+ sellingDate +" Selling price: "+sellingPrice;
	}

	/**Returns date Of buying**/
	public LocalDateTime getBuyingDate() {
		return buyingDate;
	}
	/**Returns date Of selling**/
	public LocalDateTime getSellingDate() {
		return sellingDate;
	}

	/**Returns selling price**/
	public Integer getSellingPrice() {
		return sellingPrice;
	}

	/**Returns true if share sold , else false**/
	public boolean isSold() {
		return (this.sellingDate != null);
	}
}
