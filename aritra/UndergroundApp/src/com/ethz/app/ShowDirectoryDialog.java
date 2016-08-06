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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ShowDirectoryDialog {
	  /**
	   * Runs the application
	   */
	
	public static String dirName;
	
	  public void display() {
	    Display display = new Display();
	    Shell shell = new Shell(display, SWT.ON_TOP);
	    shell.setText("Directory Browser");
	    createContents(shell);
	    shell.pack();
	    shell.open();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	  }

	  /**
	   * Creates the window contents
	   * 
	   * @param shell the parent shell
	   */
	  private void createContents(final Shell shell) {
	    shell.setLayout(new GridLayout(8, true));
	    new Label(shell, SWT.NONE).setText("Directory:");

	    // Create the text box extra wide to show long paths
	    final Text text = new Text(shell, SWT.BORDER);
	    GridData data = new GridData(GridData.FILL_HORIZONTAL);
	    data.horizontalSpan = 5;
	    text.setLayoutData(data);

	    // Clicking the button will allow the user
	    // to select a directory
	    Button button = new Button(shell, SWT.PUSH);
	    Button button1 = new Button(shell, SWT.PUSH);
	    button1.setText("OK");
	    button.setText("Browse...");
	    button.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        DirectoryDialog dlg = new DirectoryDialog(shell);

	        // Set the initial filter path according
	        // to anything they've selected or typed in
	        dlg.setFilterPath(text.getText());
	        
	        // Change the title bar text
	        dlg.setText("Select JSON dir");

	        // Customizable message displayed in the dialog
	        dlg.setMessage("Select a directory");

	        // Calling open() will open and run the dialog.
	        // It will return the selected directory, or
	        // null if user cancels
	        String dir = dlg.open();
	        if (dir != null) {
	          // Set the text box to the new selection
	          text.setText(dir);
	          dirName = dir;
	        }
	      }
	    });
	    button1.addSelectionListener(new SelectionAdapter() {
		      public void widgetSelected(SelectionEvent event) {
		    	  
		    	  AssembleFrame.JSONDirPath = dirName;
		    	 // System.out.println(AssembleFrame.JSONDirPath);
		    	  shell.dispose();
		      }
		      });
	  }

	  /**
	   * The application entry point
	   * 
	   * @param args the command line arguments
	   */
	  public static void main(String[] args) {
	    new ShowDirectoryDialog().display();
	  }
	}