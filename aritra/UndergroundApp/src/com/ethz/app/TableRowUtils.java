package com.ethz.app;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.TableView.TableCell;

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
					//table.getColumn("Progress").setCellRenderer(new ProgressCellRender());
					
					try {
						
						JSONObject fountainTableRowSpecific =  TableChecker.URL_JSON_TABLE_MAP.get(urlString);
						//get the fountain specific seed and set it to the assembler scope
						String seedStr = fountainTableRowSpecific.getString("seed");
						
						AssembleFrame window = new AssembleFrame(urlString);
						window.setSeed(seedStr);
						window.frame.setVisible(true);
							
						
						//table.getColumn("Progress").setCellRenderer(new ProgressCellRender_1(65, row, column + 1));
						
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
					}
				}
			});
        	
        	table.getModel().setValueAt(ValRow.progress_map.get(urlString), row, column + 1);
        	
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

class ValRow
{
	public static volatile Map<String, Integer> progress_map = new HashMap<>();
}

class ProgressCellRender_1 extends JProgressBar implements TableCellRenderer {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
		int progress = 0;
		
        if (value instanceof Float)
        {
            progress = Math.round(((Float) value) * 100f);
        } 
        else if (value instanceof Integer) 
        {
        	progress = (int) value;
        }
        
        setStringPainted(true);
        setValue(progress);
        return this;
    }
}