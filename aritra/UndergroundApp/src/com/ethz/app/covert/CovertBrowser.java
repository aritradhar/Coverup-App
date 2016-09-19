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

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Label;

/**
 * @author Aritra
 *
 */
public class CovertBrowser {

	protected Shell shell;
	private Text text;

	private List<String> urlList; 
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			CovertBrowser window = new CovertBrowser();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	public CovertBrowser() {
		this.urlList = new ArrayList<>();
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(1442, 917);
		shell.setText("Covert Browser");
		
		Browser browser = new Browser(shell, SWT.NONE);
		
		browser.setBounds(10, 61, 1404, 786);
		browser.setJavascriptEnabled(true);
		//browser.setUrl("http://forum.codecall.net/topic/57029-simple-java-web-browser/");
		
		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(10, 3, 1404, 52);
		
		Label lbllinks = new Label(composite, SWT.NONE);
		lbllinks.setBounds(985, 20, 70, 20);
		lbllinks.setText("#links");
		
		
		browser.addLocationListener(new LocationListener() {
			
			@Override
			public void changing(LocationEvent paramLocationEvent) {
				// TODO Auto-generated method stub
				System.out.println(paramLocationEvent.location);
				urlList.add(paramLocationEvent.location);
				lbllinks.setText(new Integer(urlList.size()).toString());
			}
			
			@Override
			public void changed(LocationEvent paramLocationEvent) {
				// TODO Auto-generated method stub
				
			}
		});
	
	
		text = new Text(composite, SWT.BORDER);
		text.setBounds(283, 14, 544, 26);
		
		Button btnGo = new Button(composite, SWT.NONE);
		btnGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {
				
				if(text.getText() != null && text.getText().length() > 0)
				{
					browser.setUrl(text.getText());
				}
			}
		});
		
		btnGo.setBounds(842, 12, 90, 30);
		btnGo.setText("Go");
		
		
	}
}
