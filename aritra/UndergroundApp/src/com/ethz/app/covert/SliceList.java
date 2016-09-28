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

/**
 * @author Aritra
 *
 */
public class SliceList extends Shell {

	
	private Set<Long> displeaySet;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			SliceList shell = new SliceList(display, null);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public SliceList(Display display, Set<Long> displaySet) {
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
	    for (Long sliceId : displeaySet) {
	      list.add(sliceId.toString());
	    }

	    Text text = new Text(this, SWT.BORDER);
	    text.setBounds(71, 373, 160, 25);
	    
	    Button btnDelete = new Button(this, SWT.NONE);
	    btnDelete.setBounds(100, 404, 90, 30);
	    btnDelete.setText("Delete");

	    list.addSelectionListener(new SelectionListener() {
	      public void widgetSelected(SelectionEvent event) 
	      {
	        int[] selectedItems = list.getSelectionIndices();
	        String outString = "";
	        for (int loopIndex = 0; loopIndex < selectedItems.length; loopIndex++)
	          outString += selectedItems[loopIndex] + " ";
	        text.setText("Selected Items: " + outString);
	      }

		@Override
		public void widgetDefaultSelected(SelectionEvent e) 
		{			
		}
	    });

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
