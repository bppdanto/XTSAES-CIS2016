public class TestAES {
	public static void main(String[] args) throws Exception {
		String plaintext = 
			// "0123456789abcdeffedcba9876543210"; // plaintext contoh di slide
			// "014BAF2278A69D331D5180103643E99A"; // 3
			// "54776F204F6E65204E696E652054776F";
			"fedcba98765432100123456789abcdef";
		String key = 
			// "0f1571c947d9e8590cb7add6af7f6798"; // key contoh di slide
			// "E8E9EAEBEDEEEFF0F2F3F4F5F7F8F9FA"; // 3
			// "5468617473206D79204B756E67204675";
			"014baf2278a69d331d5180103643e99a";

		AES myAES = new AES();
		myAES.setInitialState(plaintext);
		myAES.setKey(key);

		myAES.encrypt();
		// myAES.decrypt();

		// AESFrame frame = new AESFrame();
	}
}