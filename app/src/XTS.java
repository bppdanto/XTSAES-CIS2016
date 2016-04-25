import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

/** 
  * This class represents XTS-AES encryption and decryption
  * with 256-bit key
  *
  * @author Bardan Putra Prananto
  * @author Syukri Mullia Adil Perkasa
  * @version 24.04.2016
  */

public class XTS{

    private static final int SIZE_OF_LONG = 8;
    private String PLAIN_ADDR;
    private String CIPHER_ADDR;
    private String KEY_ADDR;
    private static int BLOCK_SIZE = 16;                     //128-bits (16-bytes)
    private static int KEY_LENGTH_HEX = 64;                 //256-bits (32-bytes)
    private static String i = "fedcba98765432100123456789abcdef";
    private static int NUMBER_OF_THREAD = 100;
    private byte[] tweak = null;

    /**
     * Create a new XTS instance.
     *
     */
    public XTS(String PLAIN_ADDR, String CIPHER_ADDR, String KEY_ADDR) {
        this.PLAIN_ADDR = PLAIN_ADDR;
        this.CIPHER_ADDR = CIPHER_ADDR;
        this.KEY_ADDR = KEY_ADDR;
    }

    public void setupEncrypt() throws IOException,AESException{
        BufferedReader br = new BufferedReader(new FileReader(KEY_ADDR));
        String key = br.readLine();
        if (key.length() != KEY_LENGTH_HEX)
            throw new AESException("Key length is not 64 bytes.");
        String key1 = key.substring(0,KEY_LENGTH_HEX/2);
        //System.out.println(key1.length());
        String key2 = key.substring(KEY_LENGTH_HEX/2,KEY_LENGTH_HEX);
        //System.out.println(key2.length());
        RandomAccessFile plainFile = new RandomAccessFile(PLAIN_ADDR,"r");
        RandomAccessFile cipherFile = new RandomAccessFile(CIPHER_ADDR,"rw");
        cipherFile.setLength(0);
        startXTSEncrypt(plainFile,cipherFile,key1,key2);
        // System.out.println("Plain File Size: "+plainFile.length());
        // System.out.println("Cipher File Size: "+cipherFile.length());
        // System.out.println("Encrypt Done");
        // System.out.println("");
        plainFile.close();
        cipherFile.close();
        tweak = null;
    }

    public void setupDecrypt() throws IOException,AESException{
        BufferedReader br = new BufferedReader(new FileReader(KEY_ADDR));
        String key = br.readLine();
        if (key.length() != KEY_LENGTH_HEX)
            throw new AESException("Key length is not 64 bytes.");
        RandomAccessFile keyFile = new RandomAccessFile(KEY_ADDR,"r");
        String key1 = key.substring(0,KEY_LENGTH_HEX/2);
        //System.out.println(key1.length());
        String key2 = key.substring(KEY_LENGTH_HEX/2,KEY_LENGTH_HEX);
        //System.out.println(key2.length());
        RandomAccessFile plainFile = new RandomAccessFile(PLAIN_ADDR,"rw");
        RandomAccessFile cipherFile = new RandomAccessFile(CIPHER_ADDR,"r");
        plainFile.setLength(0);
        startXTSDecrypt(plainFile,cipherFile,key1,key2);
        // System.out.println("Plain File Size: "+plainFile.length());
        // System.out.println("Cipher File Size: "+cipherFile.length());
        // System.out.println("Decrypt Done");
        // System.out.println("");
        plainFile.close();
        cipherFile.close();
        tweak = null;
    }

    public void startXTSEncrypt(RandomAccessFile plain, RandomAccessFile cipher, String key1, String key2)
            throws IOException, AESException{
        long fileSize = plain.length();
        //System.out.println(fileSize);
        final int NUMBER_OF_BLOCKS = (int)(fileSize / BLOCK_SIZE);
        //System.out.println(NUMBER_OF_BLOCKS);
        final int BYTES_REMAINDER = (int)(fileSize % BLOCK_SIZE);
        byte[][] plainBytes = new byte[NUMBER_OF_BLOCKS+1][BLOCK_SIZE];
        plainBytes[NUMBER_OF_BLOCKS] = new byte[BYTES_REMAINDER];
        for (int ii = 0; ii < plainBytes.length; ii++){
            plain.read(plainBytes[ii]);
        }
        //System.out.println("");
        byte[][] cipherBytes = new byte[NUMBER_OF_BLOCKS+1][BLOCK_SIZE];
        AES aes = new AES();
        aes.setInitialState(i); aes.setKey(key2); aes.encrypt();
        tweak = aes.getResult();
        for(int ii = 0; ii < NUMBER_OF_BLOCKS-1;ii++){
            encryptBlock(cipherBytes[ii],plainBytes[ii],key1,tweak);
            tweak = multiplyTweakByA(tweak);
        }

        // block m-1
        boolean isExistRemainder = true;
        if(BYTES_REMAINDER == 0){
            encryptBlock(cipherBytes[NUMBER_OF_BLOCKS-1],plainBytes[NUMBER_OF_BLOCKS-1],key1,tweak);
            cipherBytes[NUMBER_OF_BLOCKS] = new byte[0];
            isExistRemainder = false;
        } else {
            byte[] cc = new byte[BLOCK_SIZE];
            //System.out.println(NUMBER_OF_BLOCKS);
            encryptBlock(cc, plainBytes[NUMBER_OF_BLOCKS-1], key1, tweak);
            cipherBytes[NUMBER_OF_BLOCKS] = new byte[BYTES_REMAINDER];
            System.arraycopy(cc, 0, cipherBytes[NUMBER_OF_BLOCKS], 0, BYTES_REMAINDER);
            int cpLength = 16 - BYTES_REMAINDER;
            byte[] cp = new byte[cpLength];
            int xx = cc.length - 1;
            int yy = cp.length - 1;
            while (cpLength != 0) {
                cp[yy--] = cc[xx--];
                cpLength--;
            }
            byte[] pm = new byte[BLOCK_SIZE];
            for (int ii = 0; ii < BYTES_REMAINDER; ii++) {
                pm[ii] = plainBytes[NUMBER_OF_BLOCKS][ii];
            }
            for (int ii = BYTES_REMAINDER; ii < BLOCK_SIZE; ii++) {
                pm[ii] = cp[ii - BYTES_REMAINDER];
            }
            tweak = multiplyTweakByA(tweak);
            encryptBlock(cipherBytes[NUMBER_OF_BLOCKS-1],pm,key1,tweak);
        }
        int count = 0;
        for(int ii = 0; ii < cipherBytes.length; ii++){
            count++;
            cipher.write(cipherBytes[ii]);
            if(ii == cipherBytes.length-2 && !isExistRemainder) break;
        }
    }

    public void startXTSDecrypt(RandomAccessFile plain, RandomAccessFile cipher, String key1, String key2)
            throws IOException, AESException{
        long fileSize = cipher.length();
        final int NUMBER_OF_BLOCKS = (int)(fileSize / BLOCK_SIZE);
        final int BYTES_REMAINDER = (int)(fileSize % BLOCK_SIZE);
        byte[][] cipherBytes = new byte[NUMBER_OF_BLOCKS+1][BLOCK_SIZE];
        cipherBytes[NUMBER_OF_BLOCKS] = new byte[BYTES_REMAINDER];
        for (int ii = 0; ii < cipherBytes.length; ii++){
            cipher.read(cipherBytes[ii]);
        }
        byte[][] plainBytes = new byte[NUMBER_OF_BLOCKS+1][BLOCK_SIZE];
        AES aes = new AES();
        aes.setInitialState(i); aes.setKey(key2); aes.encrypt();
        tweak = aes.getResult();
        for(int ii = 0; ii < NUMBER_OF_BLOCKS-1;ii++){
            decryptBlock(cipherBytes[ii],plainBytes[ii],key1,tweak);
            tweak = multiplyTweakByA(tweak);
        }

        // block m-1
        if(BYTES_REMAINDER == 0){
            decryptBlock(cipherBytes[NUMBER_OF_BLOCKS-1],plainBytes[NUMBER_OF_BLOCKS-1],key1,tweak);
            plainBytes[NUMBER_OF_BLOCKS] = new byte[0];
        } else {
            byte[] pp = new byte[BLOCK_SIZE];
            byte[] tweakLast = Arrays.copyOf(tweak,tweak.length);
            tweak = multiplyTweakByA(tweak);
            decryptBlock(cipherBytes[NUMBER_OF_BLOCKS-1], pp, key1, tweak);
            plainBytes[NUMBER_OF_BLOCKS] = new byte[BYTES_REMAINDER];
            System.arraycopy(pp, 0, plainBytes[NUMBER_OF_BLOCKS], 0, BYTES_REMAINDER);
            int cpLength = 16 - BYTES_REMAINDER;
            byte[] cp = new byte[cpLength];
            int xx = pp.length - 1;
            int yy = cp.length - 1;
            while (cpLength != 0) {
                cp[yy--] = pp[xx--];
                cpLength--;
            }
            byte[] cm = new byte[BLOCK_SIZE];
            for (int ii = 0; ii < BYTES_REMAINDER; ii++) {
                cm[ii] = cipherBytes[NUMBER_OF_BLOCKS][ii];
            }
            for (int ii = BYTES_REMAINDER; ii < BLOCK_SIZE; ii++) {
                cm[ii] = cp[ii - BYTES_REMAINDER];
            }
            tweak = Arrays.copyOf(tweakLast,tweakLast.length);
            decryptBlock(cm,plainBytes[NUMBER_OF_BLOCKS-1],key1,tweak);
        }
        for(int ii = 0; ii < plainBytes.length; ii++){
            plain.write(plainBytes[ii]);
        }
    }

    private void encryptBlock(byte[] cipher, byte[] plain, String key, byte[] tweak)
            throws AESException{
        AES aes = new AES();
        byte[] pp = new byte[BLOCK_SIZE];
        for (int ii = 0; ii < BLOCK_SIZE; ii++){
            pp[ii] = (byte)(plain[ii] ^ tweak[ii]);
        }
        aes.setInitialState(AESUtil.bytesToHex(pp)); aes.setKey(key); aes.encrypt();
        byte[] cc = aes.getResult();
        for (int ii = 0; ii < BLOCK_SIZE; ii++){
            cipher[ii] = (byte)(cc[ii] ^ tweak[ii]);
        }
    }

    private void decryptBlock(byte[] cipher, byte[] plain, String key, byte[] tweak)
            throws AESException{
        AES aes = new AES();
        byte[] cc = new byte[BLOCK_SIZE];
        for (int ii = 0; ii < BLOCK_SIZE; ii++){
            cc[ii] = (byte)(cipher[ii] ^ tweak[ii]);
        }
        aes.setInitialState(AESUtil.bytesToHex(cc)); aes.setKey(key); aes.decrypt();
        byte[] pp = aes.getResult();
        for (int ii = 0; ii < BLOCK_SIZE; ii++){
            plain[ii] = (byte)(pp[ii] ^ tweak[ii]);
        }
    }
    /**
     * Multiplication of two polynomials over the binary field
     * GF(2) modulo x^128 + x^7 + x^2 + x + 1, where GF stands for Galois Field.
     *
     * @param bytes The tweak value which is a primitive element of GF(2^128)
     * @return Returns the result of the multiplication as a byte array
     */
    private byte[] multiplyTweakByA(final byte[] bytes)
    {
        /*long whiteningLo = AESUtil.loadInt64LE(tweak, 0);
        long whiteningHi = AESUtil.loadInt64LE(tweak, SIZE_OF_LONG);

        // Multiplication of two polynomials over the binary field
        // GF(2) modulo x^128 + x^7 + x^2 + x + 1, where GF stands for Galois Field.
        int finalCarry = 0 == (whiteningHi & 0x8000000000000000L) ? 0 : 135;

        whiteningHi <<= 1;
        whiteningHi |= whiteningLo >>> 63;
        whiteningLo <<= 1;
        whiteningLo ^= finalCarry;

        AESUtil.storeInt64LE(whiteningLo, tweak, 0);
        AESUtil.storeInt64LE(whiteningHi, tweak, SIZE_OF_LONG);

        return tweak;*/
        byte[] result = new byte[bytes.length];
        boolean isCarry = bytes[0] < 0;
        for(int i = 0; i < bytes.length; i++){
            result[i] = (byte) (bytes[i] << 1);

            if(i < bytes.length - 1 && bytes[i + 1] < 0){
                result[i] = (byte) (result[i] ^ 1);
            }
        }
        if(isCarry){
            result[bytes.length - 1] = (byte) (result[bytes.length - 1] ^ 135);
        }
        return result;
    }
}