package com.ethz.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Scanner;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;




//////////////////////////////

public class TableView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TableView frame = new TableView();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public TableView() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		
		
		if(QueryMake.query.size() != 0)
			QueryMake.query = new HashSet<>();
		
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Table viewer");
		setBounds(100, 100, 640, 601);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panelTop = new JPanel();
		contentPane.add(panelTop, BorderLayout.NORTH);
		
		JPanel panelMid = new JPanel();
		contentPane.add(panelMid, BorderLayout.CENTER);		
		
		JPanel paneDown = new JPanel();
		contentPane.add(paneDown, BorderLayout.SOUTH);
		table = new JTable(0, 2);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		
		JScrollPane scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		panelMid.add(scrollPane);
		
		
		JButton btnNewButton = new JButton("Get Table");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				//table = new JTable(0, 2);
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				
				//model.setDataVector(new Object[][]{{"Link1", "select"},
                 //   {"Link2", "select"}}, new Object[]{"Link", "Select"});
				

				BufferedReader br = null;
				try {
					br = new BufferedReader(new FileReader("Tabledata.txt"));
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				StringBuffer stb =new StringBuffer();
				String st = null;
				
				try {
					while((st = br.readLine()) != null)
					{
						stb.append(st);
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				String jsonString = stb.toString();
						
				model.setDataVector(GetTable.getTableData(jsonString), new Object[]{"Link", "Select", "Flag"});
				
				//System.out.println(table.getValueAt(0, 0));
				
				table.getColumn("Select").setCellRenderer(new ButtonRenderer());
				table.getColumn("Select").setCellEditor(new ButtonEditor(new JCheckBox(), table));
		        
			}
		});

		paneDown.add(btnNewButton);	
		
		JButton btnNewButton_1 = new JButton("Execute Insert");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				StringBuffer qry = new StringBuffer();
				for(String s : QueryMake.query)
					qry.append(s).append("\n");
				
				JOptionPane.showConfirmDialog(contentPane, qry);
			}
		});
		paneDown.add(btnNewButton_1);
	}

}