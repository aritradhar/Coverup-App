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
package com.ethz.app;

import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.ethz.app.env.ENV;
import com.sun.prism.paint.Color;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import java.awt.event.ActionListener;
import java.sql.Timestamp;
import java.text.AttributedString;
import java.util.Date;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.awt.GridLayout;

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
	 */
	public ChatApp() {
		this.userName = "Anonymous";
		this.dispatchStr = new StringBuffer();
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 468, 645);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTextPane chatChatPane = new JTextPane();
		JScrollPane jsp = new JScrollPane(chatChatPane);
		chatChatPane.setEditable(false);
		frame.getContentPane().add(jsp, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		chatText = new JTextField();
		chatText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent keyEvent) {
				
				if(keyEvent.getKeyCode() == KeyEvent.VK_ENTER)
				{
					Date date = new Date();
					if(chatText.getText() != null && chatText.getText().length() > 0)
					{
						if(chatChatPane.getText() == null)
						{
							
							String chatMsg = userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
							dispatchStr.append(chatText.getText()).append("\n");
							chatChatPane.setText(chatMsg);
							chatText.setText("");
						}
						else
						{			
							String chatMsg = chatChatPane.getText() + userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
							dispatchStr.append(chatText.getText()).append("\n");
							chatChatPane.setText(chatMsg);
							chatText.setText("");
						}
					}
				}
			}
		});
		panel.add(chatText);
		chatText.setColumns(20);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				Date date = new Date();
				if(chatText.getText() != null && chatText.getText().length() > 0)
				{
					if(chatChatPane.getText() == null)
					{						
						String chatMsg = userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
						dispatchStr.append(chatText.getText()).append("\n");
						chatChatPane.setText(chatMsg);
						chatText.setText("");
					}
					else
					{
						String chatMsg = chatChatPane.getText() + userName + " [" + new Timestamp(date.getTime()).toString() + "] : " + chatText.getText() + "\n";
						dispatchStr.append(chatText.getText()).append("\n");
						chatChatPane.setText(chatMsg);
						chatText.setText("");
					}
				}
			}
		});
		panel.add(btnSend);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);
		panel_1.setLayout(new GridLayout(0, 5, 0, 0));
		
		JComboBox<String> comboBox = new JComboBox<>();
		comboBox.setMaximumRowCount(100);
		panel_1.add(comboBox);
		
		JButton btnPopulate = new JButton("Populate");
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
				
				if(chatChatPane.getText() != null)
				{
					String stringToDispatch = dispatchStr.toString();
					dispatchStr = new StringBuffer();
					chatChatPane.setText(chatChatPane.getText() + "-------- Dispatched --------\n");
					
					String chatDispatchLoc = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_LOC + ENV.DELIM + ENV.APP_STORAGE_CHAT_DISPATCH_LOC;
					File file = new File(chatDispatchLoc);
					int fileNum = file.listFiles().length;
					try {
						FileOutputStream fwBin = new FileOutputStream(chatDispatchLoc + ENV.DELIM + fileNum + ".txt");
						fwBin.write(stringToDispatch.getBytes(StandardCharsets.UTF_8));
						fwBin.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				
			}
		});
		panel_1.add(btnDispatch);
	}

}
