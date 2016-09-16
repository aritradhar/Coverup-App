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

package com.ethz.app.rep;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Base64;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFileChooser;
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

import com.ethz.app.TableVerify;
import com.ethz.app.env.ENV;

public class DataBasePollPresetPK extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9079243269473511003L;
	public JFrame frame;
	private ScheduledThreadPoolExecutor executor;
	private static String databaseFileLocation;
	public static volatile int pollingRate = 1000;
	public JLabel progressLabel;
	public static int progress = 0;
	
	JFileChooser chooser;

	//test main.
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		//UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	

		EventQueue.invokeLater(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					DataBasePollPresetPK dPool = new DataBasePollPresetPK("90I1INgfeam-0JwxP2Vfgw9eSQGQjz3WxLO1wu1n8Cg=");
					dPool.frame.setVisible(true);
				} 
				catch (Exception e) 
				{
					e.printStackTrace();
				}
			}
		});
	}

	
	public void stopPoll()
	{
		executor.shutdown();
		frame.dispose();
	}
	
	public void setProgressLabel(JLabel progressLabel)
	{
		this.progressLabel = progressLabel;
	}
	
	public DataBasePollPresetPK(String serverPublicKey) {

		RepeatedDatabaseCheck.ServerPublickey = Base64.getUrlDecoder().decode(serverPublicKey);

		frame = new JFrame();
		frame.setTitle("Database polling");
		frame.setBounds(100, 100, 783, 510);
		//frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


		/*frame.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			@Override
			public void windowClosing(java.awt.event.WindowEvent windowEvent) {
				try
				{
					executor.shutdown();
				}
				catch(Exception ex)
				{

				}
			}
		});*/

		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.GREEN);;
		textArea.setBackground(Color.BLACK);
		Font font = new Font("Consolas", Font.PLAIN, 18);
		textArea.setFont(font);
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

		scrollPane.setViewportView(textArea);

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);


		Runnable myRunnable = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try 
				{

					RepeatedDatabaseCheck t = new RepeatedDatabaseCheck(DataBasePollPresetPK.databaseFileLocation);
					progress %= 4;
					progressLabel.setText(new String(new char[]{ENV.PROGRESS_SYMB[progress++]}));
					//System.out.println(DataBasePoll.databaseFileLocation);
					textArea.append("\n".concat(t.messaage.toString()));
				} 
				catch(NullPointerException ex)
				{
					ex.printStackTrace();
					textArea.append("\n".concat("Table not exists"));		
				}

				catch(RuntimeException ex)
				{
					ex.printStackTrace();
					textArea.append("\n".concat(ex.getMessage()));

				}
				catch (Exception e) 
				{
					e.printStackTrace();
					textArea.append("\n".concat("Some other Problem!"));						
				}

			}
		};
		//Taking an instance of class contains your repeated method.

		executor = new ScheduledThreadPoolExecutor(10);
		executor.scheduleAtFixedRate(myRunnable, 0, pollingRate, TimeUnit.MILLISECONDS);

	}

}
