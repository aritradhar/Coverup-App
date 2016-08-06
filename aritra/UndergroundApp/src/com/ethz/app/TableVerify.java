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

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import com.ethz.app.env.ENV;
import com.ethz.app.rep.DataBasePollPresetPK;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import javax.swing.SwingConstants;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import java.awt.Component;
import javax.swing.Box;

/**
 * Underground application entry point
 * @author Aritra
 *
 */
public class TableVerify {

	private JTable table;

	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static TableChecker tableChecker;
	private JTextField txtQq;
	JFileChooser chooser;
	public int databasePollingRate;
	public static boolean startPolling = false;
	public String pkText;
	public String modifiedCacheLocation;

	private DataBasePollPresetPK dPool;
	
	public static boolean set = false;
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	

		EventQueue.invokeLater(new Runnable() {
			public void run() 
			{
				try 
				{
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
	//@SuppressWarnings("static-access")
	public TableVerify() throws NoSuchAlgorithmException, SQLException {

		TableVerify.tableChecker = new TableChecker();

		try
		{
			TableVerify.tableChecker.loadtableData();
		}
		catch(Exception ex)
		{
			chooser = new JFileChooser(); 
			chooser.setCurrentDirectory(new java.io.File("."));
			chooser.setDialogTitle("Defalt derectory discovery fail. Select Firefox cache dir");
			chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			chooser.setAcceptAllFileFilterUsed(false);  
			chooser.setAcceptAllFileFilterUsed(false);  

			if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) 
			{ 		
				String path = chooser.getSelectedFile().getAbsolutePath();
				TableVerify.tableChecker.loadtableData(path);
			}
		}	

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws NoSuchAlgorithmException 
	 */
	private void initialize() throws NoSuchAlgorithmException 
	{
		frame = new JFrame();
		frame.setTitle("Server Fountain Table");
		frame.setBounds(100, 100, 823, 848);
		ImageIcon frameIcon = new ImageIcon("assets//doge.png");
		frame.setIconImage(frameIcon.getImage());
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//menubar
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);

		JMenuItem mntmCacheLocation = new JMenuItem("Set Cache Location");
		mntmCacheLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				chooser = new JFileChooser(); 
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select Firefox cache dir");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				chooser.setAcceptAllFileFilterUsed(false);  

				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) 
				{ 		
					modifiedCacheLocation = chooser.getSelectedFile().getAbsolutePath();
				}
			}
		});
		mnSettings.add(mntmCacheLocation);

		JMenuItem mntmSetPollingRate = new JMenuItem("Set Polling Rate");
		mntmSetPollingRate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				String pollingRateString = (String)JOptionPane.showInputDialog(
						frame,
						"Set Database polling rate (ms), Default = 1000 ms, current = " + DataBasePollPresetPK.pollingRate + " ms"
								+ "\nRestart polling o take effect",
						"Polling rate",
						JOptionPane.PLAIN_MESSAGE,
						null,
						null,
						"1000");

				if(pollingRateString == null)
				{
					JOptionPane.showMessageDialog(frame, "Choosing  Default = 1000 ms polling rate");
					DataBasePollPresetPK.pollingRate = 1000;
				}
				else
				{
					try
					{
						DataBasePollPresetPK.pollingRate = Integer.parseInt(pollingRateString);
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(frame, "Invalid polling rate, Choosing  Default = 1000 ms polling rate");
						DataBasePollPresetPK.pollingRate = 1000;
					}
				}
			}
		});
		mnSettings.add(mntmSetPollingRate);
		
		JMenuItem mntmShowPollingWindow = new JMenuItem("Show Polling Window");
		mntmShowPollingWindow.setEnabled(false);
		
		mntmShowPollingWindow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				dPool.frame.setVisible(true);
			}
		});
		mnSettings.add(mntmShowPollingWindow);
		
		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);
		
		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JOptionPane.showMessageDialog(frame, ENV.ABOUT_MESSAGE, "about", JOptionPane.INFORMATION_MESSAGE, frameIcon);
			}
		});
		mnHelp.add(mntmAbout);
		
		///

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);	

		txtQq = new JTextField("90I1INgfeam-0JwxP2Vfgw9eSQGQjz3WxLO1wu1n8Cg=");
		txtQq.setToolTipText("");
		txtQq.setHorizontalAlignment(SwingConstants.LEFT);
		panel_1.add(txtQq);
		txtQq.setColumns(25);

		JButton btnSetServerPk = new JButton("Set Server PK");
		panel_1.add(btnSetServerPk);

		JLabel lblNewLabel = new JLabel("Using no PK");
		lblNewLabel.setHorizontalAlignment(SwingConstants.LEFT);

		panel_1.add(lblNewLabel);

		btnSetServerPk.addActionListener(new ActionListener() 
		{

			public void actionPerformed(ActionEvent e) 
			{

				pkText = txtQq.getText();
				if(pkText == null || pkText.length() == 0)
				{
					lblNewLabel.setText("PK not set");
				}
				else
				{
					try
					{
						TableVerify.tableChecker.setPK(pkText);
						lblNewLabel.setText("PK set : " + Base64.getUrlEncoder().encodeToString(tableChecker.ServerpublicKey));
					}
					catch(Exception ex)
					{
						lblNewLabel.setText("Invalid PK");
					}

				}
			}
		});

		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);

		/*JPanel panelMid = new JPanel();
		frame.getContentPane().add(panelMid, BorderLayout.CENTER);*/



		DefaultTableModel model = new DefaultTableModel() { 
			private static final long serialVersionUID = 1L;
			String[] col = {"source","URL", "View Data", "Progress"};

			@Override 
			public int getColumnCount() { 
				return col.length; 
			} 

			@Override 
			public String getColumnName(int index) { 
				return col[index]; 
			} 
			
			@Override
			public boolean isCellEditable(int row, int column) {
				if(column != 2)
					return false;
				else
					return true;
				
			}

		};
		
		table = new JTable(model);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		frame.getContentPane().add(scrollPane);

		JButton btnLoadMessage;
		JButton btnDumpTable = new JButton("Dump Table");
		btnDumpTable.setEnabled(false);

		table.setVisible(false);

		if(TableVerify.tableChecker == null)
		{
			System.err.println("NULL app");
		}
		JLabel progressLabel = new JLabel("|");

		//TODO
		JButton btnStartPolling = new JButton("Start Polling");
		btnStartPolling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(!startPolling)
				{
					if(pkText == null || pkText.length() == 0)
					{
						JOptionPane.showMessageDialog(frame, "Server public key is not set");
						return;
					}
					
					//polling queue
					EventQueue.invokeLater(new Runnable() 
					{
						public void run() 
						{
							try 
							{
								dPool = new DataBasePollPresetPK(pkText);
								dPool.setProgressLabel(progressLabel);
								mntmShowPollingWindow.setEnabled(true);
								//dPool.frame.setVisible(true);
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
							}
						}
					});
					btnStartPolling.setText("Stop Polling");
					startPolling = true;

				}
				else
				{
					
					dPool.stopPoll();
					
					mntmShowPollingWindow.setEnabled(false);
					btnStartPolling.setText("Start Polling");
					startPolling = false;
					
					
				}


			}
		});
		
		
		
		panel.add(progressLabel);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut_1);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut);
		panel.add(btnStartPolling);
		//app.verifyMessage();

		btnLoadMessage = new JButton("Load Table");
		panel.add(btnLoadMessage);


		JButton btnVerifySignature = new JButton("Verify Signature");
		btnVerifySignature.setEnabled(false);
		panel.add(btnVerifySignature);


		btnLoadMessage.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				//try to reload from the database in case there is any update
				try 
				{	
					if(!ENV.EXPERIMENTAL)
					{
						if(modifiedCacheLocation == null)
							TableVerify.tableChecker.loadtableData();
						else
							TableVerify.tableChecker.loadtableData(modifiedCacheLocation);
					}
					else
					{
						if(modifiedCacheLocation == null)
							tableChecker.loadtableDataMultipleProvider();
						else
							tableChecker.loadtableDataMultipleProvider(modifiedCacheLocation);
					}
				} 
				catch (SQLException e1) 
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame,
							"Houston we have a problem.",
							"Error",
							JOptionPane.ERROR_MESSAGE);

					return;
				}

				String[] urls = tableChecker.getURLsFromTable();

				Object[][] tableModelData = new Object[urls.length][4];

				int i = 0;
				for(String url : urls)
				{
					tableModelData[i][0] = (TableChecker.URL_SOURCE_TABLE_MAP.containsKey(url)) ? TableChecker.URL_SOURCE_TABLE_MAP.get(url) : ":P";
					tableModelData[i][1] = url;
					tableModelData[i][2] = "Go";
					tableModelData[i][3] = null;
					i++;
				}


				//DefaultTableModel model = (DefaultTableModel) table.getModel();


				model.setDataVector(tableModelData, new Object[]{"source","URL", "View Data", "Progress"});

				table.getColumn("Progress").setCellRenderer(new ProgressCellRender_1());
				
				//System.out.println(table.getValueAt(0, 0));
				table.getColumn("View Data").setCellRenderer(new ButtonRenderer());
				table.getColumn("View Data").setCellEditor(new ButtonEditor(new JCheckBox(), table));
				
			
				table.setVisible(true);

				btnVerifySignature.setEnabled(true);
				btnDumpTable.setEnabled(true);
				/*for(String url : urls)
					toProject.append(url).append("\n");

				textArea.setText(toProject.toString());*/
			}
		});


		btnVerifySignature.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {



				if(!ENV.EXPERIMENTAL)
				{
					try {
						boolean ret = tableChecker.verifyMessage();

						if(!ret)
						{
							JOptionPane.showMessageDialog(frame, "Error! PK not set");
							return;
						}

						if(tableChecker.verifyResult)
							JOptionPane.showMessageDialog(frame, "Table verification successful!!");

						else
							JOptionPane.showMessageDialog(frame, "Table verification fail!!");

					} catch(NullPointerException ex) {
						JOptionPane.showMessageDialog(frame, "Error! PK not set");
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Exception happened in signature verification");
					}
				}

				else
				{
					try {
						boolean ret = tableChecker.verifyMessageMultipleProvider();
						if(!ret)
						{
							JOptionPane.showMessageDialog(frame, "Error! PK not set");
							return;
						}

						List<String> failedSigOriginKeys = TableChecker.verifyMessageList();

						if(failedSigOriginKeys.size() == 0)
							JOptionPane.showMessageDialog(frame, "Table verification successful!!");
						else
						{
							StringBuffer stb = new StringBuffer();
							for(String key : failedSigOriginKeys)
								stb.append(key).append("\n");

							JOptionPane.showMessageDialog(frame, "Table verification fail for keys : \n" + stb.toString());
						}

					} catch (NullPointerException e1) {
						JOptionPane.showMessageDialog(frame, "Error! PK not set");
						e1.printStackTrace();
					}catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Exception happened in signature verification");
					}

				}

			}
		});



		btnDumpTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(!ENV.EXPERIMENTAL)
				{
					String dump = tableChecker.tableDumpJson;
					FileWriter fwTableDump = null;
					try {
						fwTableDump = new FileWriter(ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_TABLE_DUMP);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Error in file");
						e1.printStackTrace();
					}
					try {
						fwTableDump.append(dump);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(frame, "Error in file");					
						e1.printStackTrace();
					}
					try {
						fwTableDump.close();

						JOptionPane.showMessageDialog(frame, "Table dumped @ " + ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_TABLE_DUMP);

					} catch (IOException e1) {

						JOptionPane.showMessageDialog(frame, "Error in closing file!");
						e1.printStackTrace();
					}

				}

				else
				{
					List<String[]> dumpList = tableChecker.multipleProviderRows;

					for(String[] dumpRow : dumpList)
					{
						String dump = dumpRow[0];
						FileWriter fwTableDump = null;
						try {
							fwTableDump = new FileWriter(ENV.APP_STORAGE_LOC + 
									ENV.DELIM + ENV.APP_STORAGE_TABLE_MULTIPLE_PROVIDER_DUMP + "_" + dumpRow[1].replaceAll("\\.", "_").replaceAll(":", "_") + ENV.APP_JSON_EXTENSION);
						} catch (IOException e1) {
							JOptionPane.showMessageDialog(frame, "Error in file");
							e1.printStackTrace();
						}
						try {
							fwTableDump.append(dump);
						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Dump error");
						}
						try {
							fwTableDump.close();

						} catch (IOException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Close error");
						}
					}

					JOptionPane.showMessageDialog(frame, "Tables dumped");
				}

			}
		});
		panel.add(btnDumpTable);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut_2);

	}

}
