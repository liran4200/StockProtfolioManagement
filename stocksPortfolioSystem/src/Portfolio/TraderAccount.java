package Portfolio;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class TraderAccount {
	
	private final String SECRET = "UmVLhJ";
	private final int ACCOUNT_NUM = 104;
	private final String TRADER_NAME = "jojo";


	/**Create trader account**/
	public TraderAccount() throws MalformedURLException, RemoteException, NotBoundException {
	}

	/**Returns account number **/
	public int getACCOUNT_NUM() {
		return ACCOUNT_NUM;
	}

	/**Returns secret number **/
	public String getSECRET() {
		return SECRET;
	}
	/**Returns trader name **/
	public String getName(){
		return TRADER_NAME;
	}

}
