package com.ethz.app;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.json.JSONObject;

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
            //setText("Selected");
        } else {
            setForeground(table.getForeground());
            setBackground(UIManager.getColor("Button.background"));
            
            //setText((value == null) ? "" : value.toString());
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
    private int column;
    private JTable table;
    
    public ButtonEditor(JCheckBox checkBox, JTable table) {
        super(checkBox);
        this.table = table;
        
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	//button.setText("Selected");
            	fireEditingStopped();
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
    	
    	this.row = row;
    	this.column = column;
    	
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
        	String urlString = table.getValueAt(row, 1).toString();
			
        	//test
        	//System.out.println(urlString);
        	
			//TableHandler.insertDataToTable("index-CCC", linkStr);
			//JOptionPane.showMessageDialog(button, "Database insert success!!");
        	
        	//call the assemble window from here
        	EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						
						JSONObject fountainTableRowSpecific =  TableChecker.URL_JSON_TABLE_MAP.get(urlString);
						//get the fountain specific seed and set it to the assembler scope
						String seedStr = fountainTableRowSpecific.getString("seed");
						
						AssembleFrame window = new AssembleFrame(urlString);
						window.setSeed(seedStr);
						
						window.frame.setVisible(true);
						
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			});
        	
        	
        	
        }
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        //button.setText("Selected");
        return super.stopCellEditing();
    }

    @Override
    protected void fireEditingStopped() {
    	//button.setText("Selected");
        super.fireEditingStopped();
    }
}

class ProgressCellRenderer implements TableCellRenderer, ActionListener {

    JProgressBar bar = new JProgressBar();
    //Timer timer = new Timer(100, this);
    JTable table;
    int column;
    
    public ProgressCellRenderer(JTable table) {
    	this.table = table;
        bar.setValue(0);
        bar.setStringPainted(true);
       // timer.start();
    }
    
    public void setProgressValue(int row, int column, int value)
    {
    	this.bar.setValue(value);
    }

    @Override
    public JProgressBar getTableCellRendererComponent(JTable table,
        Object value, boolean isSelected, boolean hasFocus, int row, int column) {
    	this.column = column;
        return bar;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TableModel model = table.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            table.getModel().setValueAt(0, row, column);
        }
    }
}