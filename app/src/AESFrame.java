import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/** 
  * This class specifies JFrame etc. that is used to build the GUI
  *
  * @author Bardan Putra Prananto
  * @author Syukri Mullia Adil Perkasa
  * @version 24.04.2016
  */

public class AESFrame extends JFrame implements ActionListener {
    JTextField sourceField, keyField, resultField;
    JButton sourceButton, keyButton, resultButton, encryptBtn, decryptBtn;
    String sourceFilePath, keyFilePath, resultFilePath;

    public AESFrame() throws Exception {
        super("Simple XTS-AES");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 300);
        initComponents();
        setLocationRelativeTo(null);
        setResizable(false);
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }

    public void initComponents() {
        // wrapper panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(400, 60));
        JLabel title = new JLabel("Simple XTS-AES");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("Consolas", 1, 30));
        titlePanel.add(title);

        // left side panel, for aligntment only
        JPanel sidePanel1 = new JPanel();
        sidePanel1.setPreferredSize(new Dimension(20, 300));
        
        // right side panel, for aligntment only
        JPanel sidePanel2 = new JPanel();
        sidePanel2.setPreferredSize(new Dimension(50, 300));

        // encrypt decrypt button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(400, 50));
        encryptBtn = new JButton("Encrypt!");
        decryptBtn = new JButton("Decrypt!");
        encryptBtn.addActionListener(this);
        decryptBtn.addActionListener(this);
        buttonPanel.add(encryptBtn);
        buttonPanel.add(decryptBtn);

        // panel for values, this is actually the main panel
        JPanel panelValues = new JPanel();
        GridLayout gl = new GridLayout(3,1);
        gl.setHgap(20);
        gl.setVgap(5);
        panelValues.setLayout(gl);

        // panel for source label, field, and button
        JPanel sourcePanel = new JPanel();
        sourcePanel.setLayout(new BorderLayout());

        // panel for key label, field, and button
        JPanel keyPanel = new JPanel();
        keyPanel.setLayout(new BorderLayout());
        
        // panel for result label, field, and button
        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BorderLayout());

        // create source label
        JPanel sourceLabelPanel = new JPanel();
        sourceLabelPanel.setPreferredSize(new Dimension(80,20));
        JLabel sourceLabel = new JLabel("Source file:");
        sourceLabelPanel.add(sourceLabel);

        // create key label
        JPanel keyLabelPanel = new JPanel();
        keyLabelPanel.setPreferredSize(new Dimension(80,20));
        JLabel keyLabel = new JLabel("Key file:");
        keyLabelPanel.add(keyLabel);

        // create result label
        JPanel resultLabelPanel = new JPanel();
        resultLabelPanel.setPreferredSize(new Dimension(80,20));
        JLabel resultLabel = new JLabel("Target file:");
        resultLabelPanel.add(resultLabel);

        // create source text field
        JPanel sourceFieldPanel = new JPanel();
        sourceField = new JTextField(20);
        sourceField.setEditable(false);
        sourceField.setBackground(Color.WHITE);
        sourceFieldPanel.add(sourceField);

        // create key text field
        JPanel keyFieldPanel = new JPanel();
        keyField = new JTextField(20);
        keyField.setEditable(false);
        keyField.setBackground(Color.WHITE);
        keyFieldPanel.add(keyField);

        // create result text field
        JPanel resultFieldPanel = new JPanel();
        resultField = new JTextField(20);
        resultField.setEditable(false);
        resultField.setBackground(Color.WHITE);
        resultFieldPanel.add(resultField);

        // create source chooser button
        JPanel sourceButtonPanel = new JPanel();
        sourceButton = new JButton("browse");
        sourceButton.setPreferredSize(new Dimension(80,20));
        sourceButton.addActionListener(this);
        sourceButtonPanel.add(sourceButton);

        // create key chooser button
        JPanel keyButtonPanel = new JPanel();
        keyButton = new JButton("browse");
        keyButton.setPreferredSize(new Dimension(80,20));
        keyButton.addActionListener(this);
        keyButtonPanel.add(keyButton);

        // create result chooser button
        JPanel resultButtonPanel = new JPanel();
        resultButton = new JButton("browse");
        resultButton.setPreferredSize(new Dimension(80,20));
        resultButton.addActionListener(this);
        resultButtonPanel.add(resultButton);

        // add source label, field, and button to its panel
        sourcePanel.add(sourceLabelPanel, BorderLayout.WEST); 
        sourcePanel.add(sourceFieldPanel, BorderLayout.CENTER); 
        sourcePanel.add(sourceButtonPanel, BorderLayout.EAST);

        // add key label, field, and button to its panel
        keyPanel.add(keyLabelPanel, BorderLayout.WEST); 
        keyPanel.add(keyFieldPanel, BorderLayout.CENTER); 
        keyPanel.add(keyButtonPanel, BorderLayout.EAST);

        // add result label, field, and button to its panel
        resultPanel.add(resultLabelPanel, BorderLayout.WEST); 
        resultPanel.add(resultFieldPanel, BorderLayout.CENTER); 
        resultPanel.add(resultButtonPanel, BorderLayout.EAST);

        // add 3 panels above to main panel
        panelValues.add(sourcePanel);
        panelValues.add(keyPanel);
        panelValues.add(resultPanel);

        // add all panels above to the wrapper panel
        mainPanel.add(panelValues, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(sidePanel1, BorderLayout.WEST);
        mainPanel.add(sidePanel2, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // add wrapper panel to the frame
        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (event.getSource() == sourceButton) {
            File workingDirectory = new File(System.getProperty("user.dir"));
            JFileChooser fileChooser = new JFileChooser(workingDirectory);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File theFile = fileChooser.getSelectedFile();
                sourceFilePath = theFile.getPath();
                sourceField.setText(sourceFilePath);
            }
        } else if (event.getSource() == keyButton) {
            File workingDirectory = new File(System.getProperty("user.dir"));
            JFileChooser fileChooser = new JFileChooser(workingDirectory);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File theFile = fileChooser.getSelectedFile();
                keyFilePath = theFile.getPath();
                keyField.setText(keyFilePath);
            }
        } else if (event.getSource() == resultButton) {
            File workingDirectory = new File(System.getProperty("user.dir"));
            JFileChooser fileChooser = new JFileChooser(workingDirectory);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File theFile = fileChooser.getSelectedFile();
                resultFilePath = theFile.getPath();
                resultField.setText(resultFilePath);
            }
        } else if (event.getSource() == encryptBtn) {
            if (sourceFilePath == null || keyFilePath == null || resultFilePath == null) {
                JOptionPane.showMessageDialog(null, "Please specify source, key, and result files.");
            } else  {
                try {
                    XTS myXTS = new XTS(sourceFilePath, resultFilePath, keyFilePath);
                    myXTS.setupEncrypt();
                    JOptionPane.showMessageDialog(null, "Successfully encrypted selected file!");
                } catch (AESException e) {
                    JOptionPane.showMessageDialog(null, e);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Identified error(s) occurs.");
                }
            }
        } else if (event.getSource() == decryptBtn) {
            if (sourceFilePath == null || keyFilePath == null || resultFilePath == null) {
                JOptionPane.showMessageDialog(null, "Please specify result, key, and result files.");
            } else {
                try {
                    XTS XTS2 = new XTS(resultFilePath, sourceFilePath, keyFilePath);
                    XTS2.setupDecrypt();
                    JOptionPane.showMessageDialog(null, "Successfully decrypted selected file!");
                } catch (AESException e) {
                    JOptionPane.showMessageDialog(null, e);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "Identified error(s) occurs.");
                }
            }
        }
    }
}
