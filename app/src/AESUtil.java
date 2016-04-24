public class AESUtil {
	
	public static final char[] HEX_DIGITS = {
        '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'
    };

	public static boolean isHex(String hex) {
        int len = hex.length();
		int i = 0;
		char ch;

		while (i < len) {
		     ch = hex.charAt(i++);
		     if (! ((ch >= '0' && ch <= '9') || (ch >= 'A' && ch <= 'F') ||
		           (ch >= 'a' && ch <= 'f'))) return false;
		}
		return true;
    }

    public static String xorHex(String hex1, String hex2) {
    	int dec1 = Integer.parseInt(hex1, 16);
    	String bin1 = Integer.toBinaryString(dec1);

    	int dec2 = Integer.parseInt(hex2, 16);
    	String bin2 = Integer.toBinaryString(dec2);

    	while (bin1.length() < 8) {
    			bin1 = "0" + bin1;
    	}
    	while (bin2.length() < 8) {
    			bin2 = "0" + bin2;
    	}

    	String result = "";
    	for (int i = 0; i < bin1.length(); i++) {
    		if (bin1.charAt(i) == bin2.charAt(i)) {
				result += "0";
			} else {
				result += "1";
			}
    	}

    	String hexReturn = Integer.toHexString(Integer.parseInt(result, 2));
    	if (hexReturn.length() < 2) {
    		hexReturn = "0" + hexReturn;
    	}
    	// System.out.println("XORing " + bin1 + " (" + hex1 + ") with " + bin2 + "(" + hex2 + ") = " + result + "(" + hexReturn + ")");
    	return hexReturn;
    }

    public static int hexDigit2Int(char ch) {
        if (ch >= '0' && ch <= '9')
            return ch - '0';
        if (ch >= 'A' && ch <= 'F')
            return ch - 'A' + 10;
        if (ch >= 'a' && ch <= 'f')
            return ch - 'a' + 10;

        return(0);	// any other char is treated as 0
    }

    public static byte[] hex2Byte(String hex) {
        int len = hex.length();
        byte[] buf = new byte[((len + 1) / 2)];

        int i = 0, j = 0;
        if ((len % 2) == 1)
            buf[j++] = (byte) hexDigit2Int(hex.charAt(i++));

        while (i < len) {
            buf[j++] = (byte) ((hexDigit2Int(hex.charAt(i++)) << 4) |
                                hexDigit2Int(hex.charAt(i++)));
        }
        return buf;
    }

    public static byte[] hexArray2ByteArray(String[] hex) {
        byte[] result = new byte[hex.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = hexDigit2Byte(hex[i]);
        }
        return result;
    }
    
    public static byte hexDigit2Byte(String hex) {
        return (byte) Integer.parseInt(hex, 16);
    }

    public static String byte2Hex(byte b) {
        return String.format("%02X", b);
    }
}