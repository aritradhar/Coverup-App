package com.ethz.app;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

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
        	String urlString = table.getValueAt(row, 0).toString();
			
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
					} catch (Exception e) {
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