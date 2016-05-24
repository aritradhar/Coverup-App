package com.ethz.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashSet;

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


class ButtonRenderer extends JButton implements TableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ButtonRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            setForeground(table.getSelectionForeground());
            setBackground(table.getSelectionBackground());
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
        }
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

class ButtonEditor extends DefaultCellEditor {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected JButton button;
    private String label;
    private boolean isPushed;
    private int row;
    private JTable table;

    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
    	this.row = row;
    	
        if (isSelected) 
        {
        	
            button.setForeground(table.getSelectionForeground());
            button.setBackground(table.getSelectionBackground());
        } 
        else 
        {
            button.setForeground(table.getForeground());
            button.setBackground(table.getBackground());
        }
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        if (isPushed) {
        	//edit here to set the marker to the firefox database
            //JOptionPane.showMessageDialog(button, row + " "+ label + " pressed and link = " + table.getValueAt(row, 0));
            try {
            	
            	String linkStr = table.getValueAt(row, 0).toString();
            	
				TableHandler.insertDataToTable("index-CCC", linkStr);
				JOptionPane.showMessageDialog(button, "Database insert success!!");
				
				QueryMake.insert(linkStr);
				
			} 
            catch (SQLException e) 
            {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(button, "Database insert fail!!" + table.getValueAt(row, 0).toString());
				e.printStackTrace();
			}
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
        super.fireEditingStopped();
    }
}

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
				
				model.setDataVector(new Object[][]{{"Link1", "select"},
                    {"Link2", "select"}}, new Object[]{"Link", "Select"});
				
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
					qry.append(s);
				
				JOptionPane.showConfirmDialog(contentPane, qry);
			}
		});
		paneDown.add(btnNewButton_1);
	}

}