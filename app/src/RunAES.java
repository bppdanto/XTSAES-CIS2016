import javax.swing.*;
import java.io.IOException;

public class RunAES {
	public static void main(String[] args) throws IOException,AESException {
		try {
			AESFrame frame = new AESFrame();
		} catch (AESException e) {
			JOptionPane.showMessageDialog(null, e);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Identified error(s) occurs.");
		}
	}
}