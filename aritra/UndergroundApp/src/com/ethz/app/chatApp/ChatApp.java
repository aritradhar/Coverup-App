//*************************************************************************************
//*********************************************************************************** *
//author Aritra Dhar 																* *
//PhD Researcher																  	* *
//ETH Zurich													   				    * *
//Zurich, Switzerland															    * *
//--------------------------------------------------------------------------------- * * 
///////////////////////////////////////////////// 									* *
//This program is meant to do world domination... 									* *
///////////////////////////////////////////////// 									* *
//*********************************************************************************** *
//*************************************************************************************
package com.ethz.app.chatApp;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONObject;
import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;

import com.ethz.app.env.ENV;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JProgressBar;

/**
 * @author Aritra
 *
 */
public class ChatApp {

	private JFrame frame;
	private JTextField chatText;
	private JTextField txtUsername;
	private String userName;
	private StringBuffer dispatchStr;
	private JTextField txtRemotePublicKey;
	private JTextPane chatChatPane;
	private JComboBox<String> oldChatLogBox;
	private String currentRemoteAddressInFocus;
	private JButton btnSend;

	private byte[] myPublicKey, myPrivateKey;
	private String myPublicAddress;
	
	public Map<String, byte[]> addresskeyMap;
	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ChatApp window = new ChatApp();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws Exception 
	 */
	public ChatApp() throws Exception {

		keyFileGen();

		this.addresskeyMap = new HashMap<>();
		this.userName = "Anonymous";
		this.oldChatLogBox = new JComboBox<>();
		this.btnSend = new JButton("Send");
		this.btnSend.setEnabled(false);
		this.chatChatPane = new JTextPane();

		this.chatText = new JTextField();
		this.chatText.setEnabled(false);


		oldChatLogBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {

					if(currentRemoteAddressInFocus == null)
						currentRemoteAddressInFocus = oldChatLogBox.getSelectedItem().toString();

					else if(dispatchStr.length() > 0)
					{
						int i = JOptionPane.showConfirmDialog(frame, "dispatch String is not empty. Ok will not dispatche it. But will appear in logs");	
						if(i == 0)
						{
							//save the not dispatched marker to the log file
							try {
								saveChatToFile(currentRemoteAddressInFocus, "--------Not Dispatched --------\n", true);
							} catch (IOException e1) {
								e1.printStackTrace();
							}


							String oldChats = LoadChat(oldChatLogBox.getSelectedItem().toString());
							chatChatPane.setText(oldChats);
							currentRemoteAddressInFocus = oldChatLogBox.getSelectedItem().toString();
							dispatchStr = new StringBuffer("");
						}
					}
					else
					{
						String oldChats = LoadChat(oldChatLogBox.getSelectedItem().toString());
						chatChatPane.setText(oldChats);
						currentRemoteAddressInFocus = oldChatLogBox.getSelectedItem().toString();
						dispatchStr = new StringBuffer("");
					}


				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});

		//populate the combo bo at initialization
		if(!btnSend.isEnabled())
			btnSend.setEnabled(true);

		if(!chatText.isEnabled())
			chatText.setEnabled(true);

		oldChatLogBox.removeAllItems();
		Set<String> retAddresses = this.populateAddressKey();
		
		if(retAddresses == null)
		{
			List<String> files = getOldChatPks();
			for(String address : files)
				oldChatLogBox.addItem(address);
		}
		else
		{
			for(String address : retAddresses)
				oldChatLogBox.addItem(address);
		}
		////////////////////////////////////////

		this.dispatchStr = new StringBuffer("");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				
				if(dispatchStr.length() > 0)
				{
					int i = JOptionPane.showConfirmDialog(frame, "Dispatch string is not empty. Ok - close ");	
					if(i == 0 || i == 2)
					{
						//save the not dispatched marker to the log file
						try {
							saveChatToFile(currentRemoteAddressInFocus, "--------Not Dispatched --------\n", true);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						frame.dispose();
					}
				}
				else
					frame.dispose();
			}
		});
		frame.setBounds(100, 100, 667, 632);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		
		JScrollPane jsp = new JScrollPane(chatChatPane);
		chatChatPane.setEditable(false);
		frame.getContentPane().add(jsp, BorderLayout.CENTER);

		JPanel panel_2 = new JPanel();
		jsp.setColumnHeaderView(panel_2);
		panel_2.setLayout(new BoxLayout(panel_2, BoxLayout.X_AXIS));

		txtRemotePublicKey = new JTextField();
		panel_2.add(txtRemotePublicKey);
		txtRemotePublicKey.setText("remote public key");
		txtRemotePublicKey.setColumns(10);

		JButton setRemotePublicKeyBtn = new JButton("Add Remote PK");
		panel_2.add(setRemotePublicKeyBtn);
		
		JProgressBar progressBar = new JProgressBar();
		panel_2.add(progressBar);
		setRemotePublicKeyBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(txtRemotePublicKey.getText() != null && txtRemotePublicKey.getText().length() > 0)
				{
					if(exists(txtRemotePublicKey.getText()))
						JOptionPane.showMessageDialog(frame, "Entered address already exists");			

					else
					{

						if(currentRemoteAddressInFocus == null)
							currentRemoteAddressInFocus = txtRemotePublicKey.getText();


						else if(dispatchStr.length() > 0)
						{
							int i = JOptionPane.showConfirmDialog(frame, "dispatch String is not empty. Ok will not dispatche it. But will appear in logs");	
							if(i == 0)
							{
								if(!btnSend.isEnabled())
									btnSend.setEnabled(true);

								if(!chatText.isEnabled())
									chatText.setEnabled(true);

								//save the not dispatched marker to the log file
								try {
									saveChatToFile(currentRemoteAddressInFocus, "--------Not Dispatched --------\n", true);
								} catch (IOException e1) {
									e1.printStackTrace();
								}

								currentRemoteAddressInFocus =  txtRemotePublicKey.getText();
								dispatchStr = new StringBuffer("");
							}
						}
						else
						{
							if(!btnSend.isEnabled())
								btnSend.setEnabled(true);


							if(!chatText.isEnabled())
								chatText.setEnabled(true);

							makeNewChatDir(txtRemotePublicKey.getText());
							currentRemoteAddressInFocus = txtRemotePublicKey.getText();
							dispatchStr = new StringBuffer("");
						}
					}
				}
			}
		});

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));


		chatText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {

				if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Date date = new Date();
					if(chatText.getText() != null && chatText.getText().length() > 0)
					{
						String chatMsg = null;
						if(chatChatPane.getText() == null)
						{						
							chatMsg = userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
							dispatchStr.append(chatMsg);
							chatChatPane.setText(chatMsg);
							chatText.setText("");					
						}
						else
						{
							String currentChatMessage =  userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
							chatMsg = chatChatPane.getText() + currentChatMessage;
							dispatchStr.append(currentChatMessage);
							chatChatPane.setText(chatMsg);
							chatText.setText("");
						}

						try {
							saveChatToFile(currentRemoteAddressInFocus, chatMsg, false);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}			
				}
			}
		});
		panel.add(chatText);
		chatText.setColumns(20);


		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				Date date = new Date();
				if(chatText.getText() != null && chatText.getText().length() > 0)
				{
					String chatMsg = null;
					if(chatChatPane.getText() == null)
					{						
						chatMsg = userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
						dispatchStr.append(chatMsg);
						chatChatPane.setText(chatMsg);
						chatText.setText("");


					}
					else
					{
						String currentChatMessage =  userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
						chatMsg = chatChatPane.getText() + currentChatMessage;
						dispatchStr.append(currentChatMessage);
						chatChatPane.setText(chatMsg);
						chatText.setText("");

					}

					try {
						saveChatToFile(currentRemoteAddressInFocus, chatMsg, false);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		panel.add(btnSend);


		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new GridLayout(0, 5, 0, 0));

		this.oldChatLogBox.setMaximumRowCount(100);
		panel_1.add(this.oldChatLogBox);

		JButton btnPopulate = new JButton("Populate");
		btnPopulate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(!btnSend.isEnabled())
					btnSend.setEnabled(true);

				if(!chatText.isEnabled())
					chatText.setEnabled(true);

				oldChatLogBox.removeAllItems();
				List<String> files = getOldChatPks();
				for(String pk : files)
					oldChatLogBox.addItem(pk);

			}
		});
		panel_1.add(btnPopulate);

		txtUsername = new JTextField();
		txtUsername.setText("userName");
		panel_1.add(txtUsername);
		txtUsername.setColumns(10);

		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(txtUsername.getText() != null && txtUsername.getText().trim().length() > 0)
					userName = txtUsername.getText().trim();
			}
		});
		panel_1.add(btnSet);

		JButton btnDispatch = new JButton("Dispatch");
		btnDispatch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(dispatchStr.length() > 0)
				{		
					boolean dispatch = false;
					//dispatch the string to local file storage
					try {
						dispatch = dispatchChat(dispatchStr.toString());
					} catch (IOException | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException 
							| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e1) {
						e1.printStackTrace();
					}
					if(dispatch)
					{
						chatChatPane.setText(chatChatPane.getText() + "-------- Dispatched --------\n");
						try {
							saveChatToFile(currentRemoteAddressInFocus, "-------- Dispatched --------\n", true);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						dispatchStr = new StringBuffer("");
					}
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "Nothing to dispatch");
				}
			}
		});
		panel_1.add(btnDispatch);
	}


	private List<String> getOldChatPks()
	{
		String oldChatLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOG_LOC;
		File oldChatLocFile = new File(oldChatLoc);
		List<String> filesStr = new ArrayList<>();

		for(File file : oldChatLocFile.listFiles())
			filesStr.add(file.getName());

		return filesStr;
	}


	private String LoadChat(String address) throws IOException
	{
		String oldChatLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + 
				ENV.DELIM + ENV.APP_STORAGE_CHAT_LOG_LOC +  ENV.DELIM + address + ENV.DELIM + ENV.APP_STORAGE_CHAT_REPO_FILE;

		if(!new File(oldChatLoc).exists())
			return new String();

		BufferedReader br = new BufferedReader(new FileReader(oldChatLoc));
		StringBuffer stb = new StringBuffer("");
		String str = null;

		while((str = br.readLine()) != null)
			stb.append(str).append("\n");
		br.close();
		return stb.toString();
	}

	/**
	 * Append chats
	 * @param address
	 * @param chat
	 * @throws IOException
	 */
	private void saveChatToFile(String address, String chat, boolean append) throws IOException
	{
		String saveChatDirLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + 
				ENV.DELIM + ENV.APP_STORAGE_CHAT_LOG_LOC +  ENV.DELIM + address;
		
		if(!new File(saveChatDirLoc).exists())
			new File(saveChatDirLoc).mkdir();
		
		String saveChatLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + 
				ENV.DELIM + ENV.APP_STORAGE_CHAT_LOG_LOC +  ENV.DELIM + address + ENV.DELIM + ENV.APP_STORAGE_CHAT_REPO_FILE;

		FileWriter fw = new FileWriter(saveChatLoc, append);
		fw.append(chat);
		fw.flush();
		fw.close();
	}

	private boolean exists(String address)
	{
		String addressLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + 
				ENV.DELIM + ENV.APP_STORAGE_CHAT_LOG_LOC +  ENV.DELIM + address;

		return new File(addressLoc).exists();
	}

	private void makeNewChatDir(String address)
	{
		String saveChatDirLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + 
				ENV.DELIM + ENV.APP_STORAGE_CHAT_LOG_LOC +  ENV.DELIM + address + ENV.DELIM + ENV.APP_STORAGE_CHAT_REPO_FILE;

		new File(saveChatDirLoc).mkdir();
	}

	/**
	 * True - ok
	 * False - do nothing
	 * @param stringToDispatch
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 */
	private boolean dispatchChat(String stringToDispatch) throws IOException, NoSuchAlgorithmException, 
	InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException
	{
		String chatDispatchLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_DISPATCH_LOC + ENV.DELIM + currentRemoteAddressInFocus;
		if(!new File(chatDispatchLoc).exists())
		{
			new File(chatDispatchLoc).mkdir();
		}

		chatDispatchLoc = chatDispatchLoc + ENV.DELIM + ENV.APP_STORAGE_CHAT_DISPATCH_FILE;
		String encChatDispatchLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + ENV.DELIM + 
				ENV.APP_STORAGE_CHAT_DISPATCH_LOC + ENV.DELIM + currentRemoteAddressInFocus + ENV.DELIM + ENV.APP_STORAGE_ENC_CHAT_DISPATCH_FILE;
		
		File chatDispatchLocFile = new File(chatDispatchLoc);

		if(chatDispatchLocFile.exists())
		{
			int op = JOptionPane.showConfirmDialog(frame, "Earlier dispach is not removed, Overwrite?. OK - overwrite, No - Append");

			//overwrite
			if(op == 0)
			{
				try {	
					
					FileOutputStream fwBin = new FileOutputStream(chatDispatchLoc);
					fwBin.write(stringToDispatch.getBytes(StandardCharsets.UTF_8));
					fwBin.flush();
					fwBin.close();
					
					// 0		  1		 2	   3		4
					//R_adder | S_addr | iv | len | enc_Data | sig (on 0|1|2|3|4)
					
					FileOutputStream fwEncbin = new FileOutputStream(encChatDispatchLoc);
					byte[] receiverPublicAddress = Base64.getUrlDecoder().decode(currentRemoteAddressInFocus);
					byte[] receiverPublicKey = this.addresskeyMap.get(currentRemoteAddressInFocus);
					byte[] senderAddressBytes = Base64.getUrlDecoder().decode(myPublicAddress);
					byte[] sharedSecret = Curve25519.getInstance(Curve25519.BEST).calculateAgreement(receiverPublicKey, myPrivateKey);
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					byte[] hashedSharedSecret = md.digest(sharedSecret);
					byte[] aesKey = new byte[hashedSharedSecret.length / 2];
					byte[] aesIV = new byte[hashedSharedSecret.length / 2];
					System.arraycopy(hashedSharedSecret, 0, aesKey, 0, aesKey.length);
					new SecureRandom().nextBytes(aesIV);
					
					SecretKey key = new SecretKeySpec(aesKey, "AES");
					Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(aesIV));
					
					byte[] encData = cipher.doFinal(stringToDispatch.getBytes(StandardCharsets.UTF_8));
					byte[] encDatalenBytes = ByteBuffer.allocate(Integer.BYTES).putInt(encData.length).array();
					
					//S_addr | iv | len | enc_Data 
					byte[] toSign = new byte[receiverPublicAddress.length + senderAddressBytes.length + aesIV.length + encDatalenBytes.length + encData.length];
					System.arraycopy(receiverPublicAddress, 0, toSign, 0, receiverPublicAddress.length);
					System.arraycopy(senderAddressBytes, 0, toSign, receiverPublicAddress.length, senderAddressBytes.length);
					System.arraycopy(aesIV, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length, aesIV.length);
					System.arraycopy(encDatalenBytes, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length + aesIV.length, encDatalenBytes.length);
					System.arraycopy(encData, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length + aesIV.length + encDatalenBytes.length, encData.length);
					
					md.reset();
					byte[] hashedToSign = md.digest(toSign);
					byte[] signature = Curve25519.getInstance(Curve25519.BEST).calculateSignature(myPrivateKey, hashedToSign);
					
					byte[] toWrite = new byte[toSign.length + signature.length];
					System.arraycopy(toSign, 0, toWrite, 0, toSign.length);
					System.arraycopy(signature, 0, toWrite, toSign.length, signature.length);
					
					fwEncbin.write(toWrite);
					fwEncbin.close();
					
					chatChatPane.setText(chatChatPane.getText() + "-------- Dispatch Overwritten --------\n");
					saveChatToFile(currentRemoteAddressInFocus, "-------- Dispatch Overwritten --------\n", true);
		
					dispatchStr = new StringBuffer("");			
					return true;
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				return false;
			}
			//append
			else if(op == 1)
			{
				try {	
					FileOutputStream fwBin = new FileOutputStream(chatDispatchLoc, true);
					fwBin.write(stringToDispatch.getBytes(StandardCharsets.UTF_8));
					fwBin.flush();
					fwBin.close();
					
					// 0		  1		 2		 3
					//R_adder | S_addr | iv | enc_Data | sig (on 0|1|2|3)
					
					FileOutputStream fwEncbin = new FileOutputStream(encChatDispatchLoc);
					byte[] receiverPublicAddress = Base64.getUrlDecoder().decode(currentRemoteAddressInFocus);
					byte[] receiverPublicKey = this.addresskeyMap.get(currentRemoteAddressInFocus);
					byte[] senderAddressBytes = Base64.getUrlDecoder().decode(myPublicAddress);
					byte[] sharedSecret = Curve25519.getInstance(Curve25519.BEST).calculateAgreement(receiverPublicKey, myPrivateKey);
					MessageDigest md = MessageDigest.getInstance("SHA-256");
					byte[] hashedSharedSecret = md.digest(sharedSecret);
					byte[] aesKey = new byte[hashedSharedSecret.length / 2];
					byte[] aesIV = new byte[hashedSharedSecret.length / 2];
					System.arraycopy(hashedSharedSecret, 0, aesKey, 0, aesKey.length);
					new SecureRandom().nextBytes(aesIV);
					
					SecretKey key = new SecretKeySpec(aesKey, "AES");
					Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(aesIV));
					
					byte[] encData = cipher.doFinal(stringToDispatch.getBytes(StandardCharsets.UTF_8));
					
					//S_addr | iv | enc_Data |
					byte[] toSign = new byte[receiverPublicAddress.length + senderAddressBytes.length + aesIV.length + encData.length];
					System.arraycopy(receiverPublicAddress, 0, toSign, 0, receiverPublicAddress.length);
					System.arraycopy(senderAddressBytes, 0, toSign, receiverPublicAddress.length, senderAddressBytes.length);
					System.arraycopy(aesIV, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length, aesIV.length);
					System.arraycopy(encData, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length + aesIV.length, encData.length);
					
					md.reset();
					byte[] hashedToSign = md.digest(toSign);
					byte[] signature = Curve25519.getInstance(Curve25519.BEST).calculateSignature(myPrivateKey, hashedToSign);
					
					byte[] toWrite = new byte[toSign.length + signature.length];
					System.arraycopy(toSign, 0, toWrite, 0, toSign.length);
					System.arraycopy(signature, 0, toWrite, toSign.length, signature.length);
					
					fwEncbin.write(toWrite);
					fwEncbin.close();
					
					
					dispatchStr = new StringBuffer("");
					return true;

				} catch (IOException e1) {
					e1.printStackTrace();

				}
				return false;
			}
			//signal nothing
			else
				return false;	
		}
		//just write
		else
		{
			try {	
				FileOutputStream fwBin = new FileOutputStream(chatDispatchLoc);
				fwBin.write(stringToDispatch.getBytes(StandardCharsets.UTF_8));
				fwBin.flush();
				fwBin.close();
				
				// 0		  1		 2		 3
				//R_adder | S_addr | iv | enc_Data | sig (on 0|1|2|3)
				
				FileOutputStream fwEncbin = new FileOutputStream(encChatDispatchLoc);
				byte[] receiverPublicAddress = Base64.getUrlDecoder().decode(currentRemoteAddressInFocus);
				byte[] receiverPublicKey = this.addresskeyMap.get(currentRemoteAddressInFocus);
				byte[] senderAddressBytes = Base64.getUrlDecoder().decode(myPublicAddress);
				byte[] sharedSecret = Curve25519.getInstance(Curve25519.BEST).calculateAgreement(receiverPublicKey, myPrivateKey);
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				byte[] hashedSharedSecret = md.digest(sharedSecret);
				byte[] aesKey = new byte[hashedSharedSecret.length / 2];
				byte[] aesIV = new byte[hashedSharedSecret.length / 2];
				System.arraycopy(hashedSharedSecret, 0, aesKey, 0, aesKey.length);
				new SecureRandom().nextBytes(aesIV);
				
				SecretKey key = new SecretKeySpec(aesKey, "AES");
				Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(aesIV));
				
				byte[] encData = cipher.doFinal(stringToDispatch.getBytes(StandardCharsets.UTF_8));
				
				//S_addr | iv | enc_Data |
				byte[] toSign = new byte[receiverPublicAddress.length + senderAddressBytes.length + aesIV.length + encData.length];
				System.arraycopy(receiverPublicAddress, 0, toSign, 0, receiverPublicAddress.length);
				System.arraycopy(senderAddressBytes, 0, toSign, receiverPublicAddress.length, senderAddressBytes.length);
				System.arraycopy(aesIV, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length, aesIV.length);
				System.arraycopy(encData, 0, toSign, receiverPublicAddress.length + senderAddressBytes.length + aesIV.length, encData.length);
				
				md.reset();
				byte[] hashedToSign = md.digest(toSign);
				byte[] signature = Curve25519.getInstance(Curve25519.BEST).calculateSignature(myPrivateKey, hashedToSign);
				
				byte[] toWrite = new byte[toSign.length + signature.length];
				System.arraycopy(toSign, 0, toWrite, 0, toSign.length);
				System.arraycopy(signature, 0, toWrite, toSign.length, signature.length);
				
				fwEncbin.write(toWrite);
				fwEncbin.close();
				
				
				dispatchStr = new StringBuffer("");
				return true;

			} catch (IOException e1) {

				e1.printStackTrace();
				return false;
			}
		}
	}

	private void keyFileGen() throws IOException, NoSuchAlgorithmException
	{
		File keyFile = new File(ENV.APP_STORAGE_CHAT_KEY_FILE);
		if(!keyFile.exists())
		{
			Curve25519KeyPair keyPair = Curve25519.getInstance(Curve25519.BEST).generateKeyPair();
			this.myPrivateKey = keyPair.getPrivateKey();
			this.myPublicKey = keyPair.getPublicKey();

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hasheddPk = md.digest(myPublicKey);
			byte[] publicAddressBytes = Arrays.copyOf(hasheddPk, ENV.PUBLIC_ADDRESS_LEN);
			this.myPublicAddress = Base64.getUrlEncoder().encodeToString(publicAddressBytes);
			
			JSONObject jObject = new JSONObject();
			jObject.put("pk", Base64.getUrlEncoder().encodeToString(myPublicKey));
			jObject.put("sk", Base64.getUrlEncoder().encodeToString(myPrivateKey));
			jObject.put("address", this.myPublicAddress);

			FileWriter fw = new FileWriter(ENV.APP_STORAGE_CHAT_KEY_FILE);
			fw.write(jObject.toString(2));
			fw.close();
		}
		else
		{
			BufferedReader br = new BufferedReader(new FileReader(ENV.APP_STORAGE_CHAT_KEY_FILE));
			StringBuffer stb = new StringBuffer();
			String str = null;

			while((str = br.readLine()) != null)
				stb.append(str);
			br.close();

			JSONObject jObject = new JSONObject(stb.toString());
			this.myPublicKey = Base64.getUrlDecoder().decode(jObject.getString("pk"));
			this.myPrivateKey = Base64.getUrlDecoder().decode(jObject.getString("sk"));
			this.myPublicAddress = jObject.getString("address");

		}
	}
	
	/**
	 * Return list of public address from the local storage. Also populate the internal table of address and public key.
	 * @return
	 * @throws Exception
	 */
	public Set<String> populateAddressKey() throws Exception
	{
		File addresskeyFile = new File(ENV.APP_STORAGE_PUBLIC_KEY_LIST);
		if(!addresskeyFile.exists())
		{
			JOptionPane.showMessageDialog(frame, "Public key list file not found");
			return null;
		}
		
		BufferedReader br = new BufferedReader(new FileReader(ENV.APP_STORAGE_PUBLIC_KEY_LIST));
		String str = null;
		while((str = br.readLine()) != null)
		{
			if(str.length() == 0)
				continue;
				
			byte[] pk = Base64.getUrlDecoder().decode(str);
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashedPk = md.digest(pk);
			byte[] publicAddressBytes = Arrays.copyOf(hashedPk, ENV.PUBLIC_ADDRESS_LEN);
			String address = Base64.getUrlEncoder().encodeToString(publicAddressBytes);
			
			this.addresskeyMap.put(address, pk);
		}
		br.close();
		
		return this.addresskeyMap.keySet();
	}
}
