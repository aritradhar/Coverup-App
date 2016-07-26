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

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;

import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;

import com.ethz.app.env.ENV;
import com.ethz.app.rep.DataBasePoll;
import com.sun.javafx.applet.ExperimentalExtensions;

import javax.swing.JLabel;
import javax.swing.JOptionPane;



import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;

public class TableVerify {
	
	private JTable table;

	public JFrame frame;

	/**
	 * Launch the application.
	 */
	public static TableChecker tableChecker;
	private JTextField txtQq;
	JFileChooser chooser;
	
	public String modifiedCacheLocation;
	
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
	@SuppressWarnings("static-access")
	public TableVerify() throws NoSuchAlgorithmException, SQLException {

		this.tableChecker = new TableChecker();
		
		try
		{
			tableChecker.loadtableData();
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
				tableChecker.loadtableData(path);
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
		frame.setBounds(100, 100, 666, 584);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.NORTH);	
		
		txtQq = new JTextField();
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
			
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JPanel panelMid = new JPanel();
		frame.getContentPane().add(panelMid, BorderLayout.CENTER);
		
		table = new JTable(0, 2);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelMid.add(scrollPane);

		JButton btnLoadMessage;
		JButton btnDumpTable = new JButton("Dump Table");
		btnDumpTable.setEnabled(false);
		
		

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
				//try to reload from the database in case there is any update
				try 
				{	
					if(!ENV.EXPERIMENTAL)
					{
						if(modifiedCacheLocation == null)
							tableChecker.loadtableData();
						else
							tableChecker.loadtableData(modifiedCacheLocation);
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
					
					
				}
				
				String[] urls = tableChecker.getURLsFromTable();
				
				Object[][] tableModelData = new Object[urls.length][2];
				
				int i = 0;
				for(String url : urls)
				{
					tableModelData[i][0] = url;
					tableModelData[i][1] = "Go";
					i++;
				}
				
				
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				//model.setDataVector(GetTable.getTableData(jsonString), new Object[]{"Link", "Select", "Flag"});
				
				model.setDataVector(tableModelData, new Object[]{"URL", "Droplet Progress"});
				
				//System.out.println(table.getValueAt(0, 0));
				
				table.getColumn("Droplet Progress").setCellRenderer(new ButtonRenderer());
				table.getColumn("Droplet Progress").setCellEditor(new ButtonEditor(new JCheckBox(), table));
				
				
				btnDumpTable.setEnabled(true);
				/*for(String url : urls)
					toProject.append(url).append("\n");
				
				textArea.setText(toProject.toString());*/
			}
		});
		
		JButton btnSetCache = new JButton("Set Cache");
		btnSetCache.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				chooser = new JFileChooser(); 
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select Firefox cache dir");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

				chooser.setAcceptAllFileFilterUsed(false);  
				chooser.setAcceptAllFileFilterUsed(false);  

				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) 
				{ 		
					modifiedCacheLocation = chooser.getSelectedFile().getAbsolutePath();
				}
			}
		});
		panel.add(btnSetCache);
		panel.add(btnLoadMessage);
		
		JButton btnVerifySignature = new JButton("Verify Signature");
		btnVerifySignature.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				try {
					tableChecker.verifyMessage();

					if(tableChecker.verifyResult)
						JOptionPane.showMessageDialog(frame, "Table verification successful!!");

					else
						JOptionPane.showMessageDialog(frame, "Table verification fail!!");

				} 
				catch(NullPointerException ex)
				{
					JOptionPane.showMessageDialog(frame, "Error! PK not set");
				}
				catch (Exception e1) 
				{
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame, "Exception happened in signature verification");
				}



			}
		});
		panel.add(btnVerifySignature);
		
		
		btnDumpTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(!ENV.EXPERIMENTAL)
				{
					String dump = tableChecker.tableDumpJson;
					FileWriter fwTableDump = null;
					try {
						fwTableDump = new FileWriter(ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_TABLE_DUMP);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						fwTableDump.append(dump);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					try {
						fwTableDump.close();

						JOptionPane.showMessageDialog(frame, "Table dumped @ " + ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_TABLE_DUMP);

					} catch (IOException e1) {
						// TODO Auto-generated catch block
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
							fwTableDump = new FileWriter(ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_TABLE_MULTIPLE_PROVIDER_DUMP + "_" + dumpRow[1] + ENV.APP_JSON_EXTENSION);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							fwTableDump.append(dump);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Dump error");
						}
						try {
							fwTableDump.close();

						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							JOptionPane.showMessageDialog(frame, "Close error");
						}
					}
					
					JOptionPane.showMessageDialog(frame, "Tables dumped");
				}
				
			}
		});
		panel.add(btnDumpTable);

	}

}
