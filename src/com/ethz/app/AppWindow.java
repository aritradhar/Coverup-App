package com.ethz.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class AppWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static App app;
	private JTextField textField;

	public static boolean set = false;
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					AppWindow window = new AppWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws NoSuchAlgorithmException 
	 * @throws SQLException 
	 */
	public AppWindow() throws NoSuchAlgorithmException, SQLException {

		this.app = new App();
		app.extractMessageFireFox();

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws NoSuchAlgorithmException 
	 */
	private void initialize() throws NoSuchAlgorithmException {
		frame = new JFrame();
		frame.setTitle("Firefox cache extractor");
		frame.setBounds(100, 100, 1223, 848);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);;
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);

		JPanel panel_1 = new JPanel();
		scrollPane.setColumnHeaderView(panel_1);

		textField = new JTextField();
		panel_1.add(textField);
		textField.setColumns(10);

		JTextPane textPane = new JTextPane();
		panel_1.add(textPane);

		JButton btnLoadMessage;

		JButton btnSetServerPk = new JButton("Set Server PK");
		btnSetServerPk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String pkText = textField.getText();
				if(pkText == null || pkText.length() == 0)
				{
					textPane.setText("PK not set");
				}
				else
				{
					try
					{
						app.setPK(pkText);
						textPane.setText("PK set");
					}
					catch(Exception ex)
					{
						textPane.setText("Invalid");
					}

				}
			}
		});
		panel_1.add(btnSetServerPk);

		if(app == null)
		{
			System.err.println("NULL app");
		}
		//app.verifyMessage();

		btnLoadMessage = new JButton("Load Message");

		btnLoadMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(app.message);
			}
		});
		panel.add(btnLoadMessage);

		JButton btnLoadSignature = new JButton("Load Signature");
		btnLoadSignature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(app.signatureString);
			}
		});
		panel.add(btnLoadSignature);

		JButton btnVerifySignature = new JButton("Verify Signature");
		btnVerifySignature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {
					app.verifyMessage();

					if(app.verifyResult)
						textArea.setText("Verify success");

					else
						textArea.setText("Invalid signature");

				} 
				catch (Exception e1) 
				{
					e1.printStackTrace();

					textArea.setText("Exception happened in signature verification");
				}



			}
		});
		panel.add(btnVerifySignature);


	}

}
