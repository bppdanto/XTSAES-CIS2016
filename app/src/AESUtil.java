/** 
  * This class specifies some utilities that is used for XTS-AES encryption/decryption
  *
  * @author Bardan Putra Prananto
  * @author Syukri Mullia Adil Perkasa
  * @version 24.04.2016
  */

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

    public static final String bytesToHex(final byte[] bytes)
    {
        if (bytes == null)
            return null;
        else
        {
            int length = bytes.length;
            String hexBytes = "";
            for (int i = 0; i < length; i++)
            {
                if ((bytes[i] & 0xFF) < 16)
                {
                    hexBytes += "0";
                    hexBytes += Integer.toHexString(bytes[i] & 0xFF);
                }
                else
                    hexBytes += Integer.toHexString(bytes[i] & 0xFF);
            }

            return hexBytes;
        }
    }

    /**
     * Convert a String of hex characters to a byte array.
     *
     * @param hexBytes The string to convert
     * @return A byte array
     */
    public static final byte[] hexToBytes(final String hexBytes)
    {
        if (hexBytes == null | hexBytes.length() < 2)
            return null;
        else
        {
            int length = hexBytes.length() / 2;
            byte[] buffer = new byte[length];
            for (int i = 0; i < length; i++)
                buffer[i] = (byte) Integer.parseInt(hexBytes.substring(i * 2, i * 2 + 2), 16);

            return buffer;
        }
    }

    /**
     * Convert a byte size into a human readable format. 1024 bytes = 1 KB 1024
     * * 1024 = 1 MB ...
     *
     * @param bytes The byte size to convert.
     * @return Returns the given byte size in a human readable format
     */
    public static final String humanReadableByteCount(final long bytes)
    {
        final int unit = 1024;
        if (bytes < unit)
            return bytes + " B";

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + ("");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    /**
     * Check if two byte arrays are equal.
     *
     * @param array1 The first array
     * @param array2 the second array
     * @return Returns true if the arrays are equal otherwise false
     */
    public static final boolean arraysAreEqual(final byte[] array1, final byte[] array2)
    {
        if (array1.length != array2.length)
            return false;

        for (int i = 0; i < array1.length; i++)
        {
            if (array1[i] != array2[i])
                return false;
        }

        return true;
    }

    /**
     * Convert a big-endian byte array into a 32-bit integer value.
     *
     * @param bytes The byte array to convert
     * @param offset The offset inside the array
     * @return Returns a 16-bit integer value representing the byte array
     */
    public final static short loadInt16BE(byte[] bytes, int offset)
    {
        return (short) (((bytes[offset]     & 0xff) << 8) |
                (bytes[offset + 1] & 0xff));
    }

    /**
     * Convert a little-endian byte array into a 32-bit integer value
     *
     * @param bytes The byte array to convert
     * @param offSet The offset inside the array
     * @return Returns a 32-bit integer value representing the byte array
     */
    public final static int loadInt32LE(final byte[] bytes, int offSet)
    {
        return   (bytes[offSet + 3]         << 24) |
                ((bytes[offSet + 2] & 0xff) << 16) |
                ((bytes[offSet + 1] & 0xff)  << 8) |
                (bytes[offSet]     & 0xff);
    }

    /**
     * Convert a big-endian byte array into a 32-bit integer value
     *
     * @param bytes The byte array to convert
     * @param offSet The offset inside the array
     * @return Returns a 32-bit integer value representing the byte array
     */
    public final static int loadInt32BE(byte[] bytes, int offSet)
    {
        return  (bytes[offSet]             << 24) |
                ((bytes[offSet + 1] & 0xff) << 16) |
                ((bytes[offSet + 2] & 0xff)  << 8) |
                (bytes[offSet + 3] & 0xff);
    }

    /**
     * Convert a little-endian byte array into a 64-bit integer value (long)
     *
     * @param bytes The byte array to convert
     * @param offSet The offset inside the array
     * @return Returns a 64-bit integer value representing the byte array
     */
    public final static long loadInt64LE(final byte[] bytes, int offSet)
    {
        return (loadInt32LE(bytes, offSet) & 0x0ffffffffL) |
                ((long) loadInt32LE(bytes, offSet + 4) << 32);
    }

    /**
     * Convert a big-endian byte array into a 64-bit integer value (long)
     *
     * @param bytes The byte array to convert
     * @param offSet The offset inside the array
     * @return Returns a 64-bit integer value representing the byte array
     */
    public final static long loadInt64BE(byte[] bytes, int offSet)
    {
        return (loadInt32BE(bytes, offSet + 4) & 0x0ffffffffL) |
                ((long) loadInt32BE(bytes, offSet) << 32);
    }

    /**
     * Convert a 32-bit integer value into a little-endian byte array
     *
     * @param value The integer to convert
     * @param bytes The byte array to store the converted value
     * @param offSet The offset in the output byte array
     */
    public final static void storeInt32LE(int value, byte[] bytes, int offSet)
    {
        bytes[offSet]     = (byte) (value);
        bytes[offSet + 1] = (byte) (value >>> 8);
        bytes[offSet + 2] = (byte) (value >>> 16);
        bytes[offSet + 3] = (byte) (value >>> 24);
    }

    /**
     * Convert a 32-bit integer value into a big-endian byte array
     *
     * @param value The integer value to convert
     * @param bytes The byte array to store the converted value
     * @param offSet The offset in the output byte array
     */
    public final static void storeInt32BE(int value, byte[] bytes, int offSet)
    {
        bytes[offSet + 3] = (byte) (value);
        bytes[offSet + 2] = (byte) (value >>> 8);
        bytes[offSet + 1] = (byte) (value >>> 16);
        bytes[offSet]     = (byte) (value >>> 24);
    }

    /**
     * Convert a 64-bit integer value (long) into a little-endian byte array
     *
     * @param value The long value to convert
     * @param bytes The byte array to store the converted value
     * @param offSet The offset in the output byte array
     */
    public final static void storeInt64LE(long value, byte[] bytes, int offSet)
    {
        storeInt32LE((int) (value >>> 32), bytes, offSet + 4);
        storeInt32LE((int) (value       ), bytes, offSet);
    }
}