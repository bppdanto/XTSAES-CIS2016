/** 
  * This class used as exception handler of AES encryption/decryption
  *
  * @author Bardan Putra Prananto
  * @author Syukri Mullia Adil Perkasa
  * @version 24.04.2016
  */
public class AESException extends Exception {
	
	public AESException() {
		super();
	}

	public AESException(String msg) {
		super(msg);
	}
}