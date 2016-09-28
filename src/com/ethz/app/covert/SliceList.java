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
package com.ethz.app.covert;

import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;

/**
 * @author Aritra
 *
 */
public class SliceList extends Shell {

	
	private Set<String> displeaySet;

	/**
	 * Create the shell.
	 * @param display
	 */
	public SliceList(Display display, Set<String> displaySet) {
		super(display, SWT.SHELL_TRIM);
		this.displeaySet = displaySet;
		createContents();
	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("Slice link list");
		setSize(319, 511);
		
		List list = new List(this, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
	    list.setBounds(0, 20, 298, 347);
	    for (String sliceName : displeaySet) {
	      list.add(sliceName);
	    }

	    Text text = new Text(this, SWT.BORDER);
	    text.setBounds(71, 373, 160, 25);
	    
	    Button btnDelete = new Button(this, SWT.NONE);
	    btnDelete.addSelectionListener(new SelectionAdapter() {
	    	@Override
	    	public void widgetSelected(SelectionEvent e) {
	    		
	    		String outString = list.getItem(list.getSelectionIndex());
	    		displeaySet.remove(outString);
	    		
	    		list.removeAll();
	    		for (String sliceName : displeaySet) {
	    		      list.add(sliceName);
	    		    }
	    	}
	    });
	    btnDelete.setBounds(100, 404, 90, 30);
	    btnDelete.setText("Delete");

	    list.addSelectionListener(new SelectionListener() {
	    	public void widgetSelected(SelectionEvent event) 
	    	{
	    		String outString = list.getItem(list.getSelectionIndex());
	    		text.setText("Selected Items: " + outString);
	    	}

	    	@Override
	    	public void widgetDefaultSelected(SelectionEvent e) {			
	    	}
	    });

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
