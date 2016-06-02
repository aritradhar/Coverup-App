package com.ethz.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JLabel;
import java.awt.FlowLayout;

import javax.swing.SwingConstants;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TableVerify {

	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static TableChecker tableChecker;
	private JTextField txtQq;

	public static boolean set = false;
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
			
		EventQueue.invokeLater(new Runnable() {
			public void run() 
			{
				try {

					TableVerify window = new TableVerify();
					window.frame.setVisible(true);
				} 
				catch (Exception e) 
				{
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
	@SuppressWarnings("static-access")
	public TableVerify() throws NoSuchAlgorithmException, SQLException {

		this.tableChecker = new TableChecker();
		tableChecker.loadtableData();

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws NoSuchAlgorithmException 
	 */
	private void initialize() throws NoSuchAlgorithmException 
	{
		
		frame = new JFrame();
		frame.setTitle("Firefox cache extractor");
		frame.setBounds(100, 100, 783, 510);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);;
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		scrollPane.setColumnHeaderView(panel_1);

		txtQq = new JTextField();
		txtQq.setToolTipText("");
		txtQq.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(txtQq);
		txtQq.setColumns(25);

		JButton btnLoadMessage;

		JButton btnSetServerPk = new JButton("Set Server PK");
		panel_1.add(btnSetServerPk);
		
		JLabel lblNewLabel = new JLabel("Using no PK");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		//lblNewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel_1.add(lblNewLabel);
		
		btnSetServerPk.addActionListener(new ActionListener() 
		{
			
			public void actionPerformed(ActionEvent e) 
			{

				String pkText = txtQq.getText();
				if(pkText == null || pkText.length() == 0)
				{
					lblNewLabel.setText("PK not set");
				}
				else
				{
					try
					{
						tableChecker.setPK(pkText);
						lblNewLabel.setText("PK set : " + Base64.getUrlEncoder().encodeToString(tableChecker.ServerpublicKey));
					}
					catch(Exception ex)
					{
						lblNewLabel.setText("Invalid PK");
					}

				}
			}
		});
		
		

		if(tableChecker == null)
		{
			System.err.println("NULL app");
		}
		//app.verifyMessage();

		btnLoadMessage = new JButton("Load Table");

		btnLoadMessage.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				try 
				{
					tableChecker.loadtableData();
				} 
				catch (SQLException e1) 
				{
					e1.printStackTrace();
				}
				textArea.setText(tableChecker.tableJson);
			}
		});
		panel.add(btnLoadMessage);

		JButton btnLoadSignature = new JButton("Load Signature");
		btnLoadSignature.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				try {
					tableChecker.loadtableData();
				} catch ( SQLException e1) {
					e1.printStackTrace();
				}
				textArea.setText(tableChecker.signature);
			}
		});
		panel.add(btnLoadSignature);

		JButton btnVerifySignature = new JButton("Verify Signature");
		btnVerifySignature.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				try {
					tableChecker.verifyMessage();

					if(tableChecker.verifyResult)
						textArea.setText("Verify success");

					else
						textArea.setText("Invalid signature");

				} 
				catch(NullPointerException ex)
				{
					textArea.setText("Error! PK not set");
				}
				catch (Exception e1) 
				{
					e1.printStackTrace();
					textArea.setText("Exception happened in signature verification\n-------------------\n"+e1.getClass()+ " " +e1.getMessage());
				}



			}
		});
		panel.add(btnVerifySignature);

	}

}
