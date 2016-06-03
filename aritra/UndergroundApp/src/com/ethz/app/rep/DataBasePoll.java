package com.ethz.app.rep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.DefaultCaret;

public class DataBasePoll extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9079243269473511003L;
	private JFrame frame;
	private JTextField txtQq;
	private ScheduledThreadPoolExecutor executor;
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
		
		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					DataBasePoll dPool = new DataBasePoll();
					dPool.frame.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}


	public DataBasePoll() {
		
		frame = new JFrame();
		frame.setTitle("Database polling");
		frame.setBounds(100, 100, 783, 510);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);;
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		 
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
		
		JButton btnSetServerPk = new JButton("Set Server PK");
		panel_1.add(btnSetServerPk);
		
		JLabel lblNewLabel = new JLabel("Using no PK");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);
		//lblNewLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		panel_1.add(lblNewLabel);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JButton btnNewButton_1 = new JButton("Stop");
		btnNewButton_1.setEnabled(false);
		
		JButton btnNewButton = new JButton("Start Polling");
		btnNewButton.setEnabled(false);
		
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
						RepeatedDatabaseCheck.ServerPublickey = Base64.getUrlDecoder().decode(pkText);
						lblNewLabel.setText("PK set : " + Base64.getUrlEncoder().encodeToString(RepeatedDatabaseCheck.ServerPublickey));
						btnNewButton.setEnabled(true);
					}
					catch(Exception ex)
					{
						lblNewLabel.setText("Invalid PK");
					}

				}
			}
		});
		
		
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				Runnable myRunnable = new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try 
						{
							/*txtQq.setText
							(
									txtQq.getText().concat
									(
											new RepeatedDatabaseCheck().messaage.toString()
									)
							);*/
							
							RepeatedDatabaseCheck t = new RepeatedDatabaseCheck();
							textArea.append("\n".concat(t.messaage.toString()));
						} 
						
						catch(RuntimeException ex)
						{
							ex.printStackTrace();
							textArea.append("\n".concat(ex.getMessage()));
							
						}
						catch (SQLException e) 
						{
							e.printStackTrace();
							textArea.append("\n".concat("ran into database problem"));		
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
							textArea.append("\n".concat("Some other shit!"));						
						}
						
					}
				};
			    //Taking an instance of class contains your repeated method.

				executor = new ScheduledThreadPoolExecutor(10);
				executor.scheduleAtFixedRate(myRunnable, 0, 1000, TimeUnit.MILLISECONDS);
				
				btnNewButton_1.setEnabled(true);
				btnNewButton.setEnabled(false);
				
			}
		});
		panel.add(btnNewButton);
		
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				executor.shutdown();
				btnNewButton_1.setEnabled(false);
				btnNewButton.setEnabled(true);
			}
		});
		panel.add(btnNewButton_1);
		

	}

}
