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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.swing.JEditorPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.ethz.app.env.ENV;
import com.ethz.app.rep.RepeatedDatabaseCheck;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Aritra
 *
 */
public class CovertBrowserTree {

	protected Shell shell;
	private Text text;

	private List<String> urlList; 
	private Text portText;
	private int port;
	private ProxyServer ps;
	private boolean serverClosed;
	public static Set<String> sliceIdSet = new HashSet<>();;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			System.setProperty("http.proxySet", "true");
			System.setProperty("http.proxyHost", "127.0.0.1");
			System.setProperty("http.proxyPort", "9700");
			System.setProperty("https.proxyHost", "127.0.0.1");
			System.setProperty("https.proxyPort", "9700");

			CovertBrowserTree window = new CovertBrowserTree();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public CovertBrowserTree() {
		this.urlList = new ArrayList<>();
		this.ps = null;
		this.serverClosed = false;
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
		createContents();
		Image small = new Image(display,"assets//hb.jpg");
		shell.setImage(small);    

		Button backButton = new Button(shell, SWT.NONE);
		backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		backButton.setBounds(10, 61, 35, 30);
		backButton.setText("<-");

		Button forwardButton = new Button(shell, SWT.NONE);
		forwardButton.setBounds(52, 61, 35, 30);
		forwardButton.setText("->");
		
		Tree tree = new Tree(shell, SWT.BORDER);
		tree.setBounds(10, 97, 303, 849);
		
		 for (int loopIndex0 = 0; loopIndex0 < 10; loopIndex0++) {
		      TreeItem treeItem0 = new TreeItem(tree, 0);
		      treeItem0.setText("Level 0 Item " + loopIndex0);
		      for (int loopIndex1 = 0; loopIndex1 < 10; loopIndex1++) {
		        TreeItem treeItem1 = new TreeItem(treeItem0, 0);
		        treeItem1.setText("Level 1 Item " + loopIndex1);
		        for (int loopIndex2 = 0; loopIndex2 < 10; loopIndex2++) {
		          TreeItem treeItem2 = new TreeItem(treeItem1, 0);
		          treeItem2.setText("Level 2 Item " + loopIndex2);
		        }
		      }
		    }
		
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
		shell.setSize(1459, 1003);
		shell.setText("Covert Browser");

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent paramDisposeEvent) {
				if(serverClosed)
				{
					try {
						ps.stopServer();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}
		});
		
		Browser browser = new Browser(shell, SWT.NONE);

		browser.setBounds(319, 97, 1112, 849);
		browser.setJavascriptEnabled(false);
		
		//browser.setUrl("C:\\Users\\Aritra\\workspace_Mars_new\\UndergroundApp\\a.htm");
		//browser.setUrl("http://forum.codecall.net/topic/57029-simple-java-web-browser/");

		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(10, 3, 1404, 52);

		Label lbllinks = new Label(composite, SWT.NONE);
		lbllinks.setBounds(1211, 20, 90, 20);
		lbllinks.setText("#links");
		
		/*
		browser.execute("Components.utils.import(\"resource://gre/modules/Services.jsm\");");
		browser.execute("Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch).setIntPref(\"network.proxy.type\", 1);");
		browser.execute("Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch).setIntPref(\"network.proxy.http_port\",9700);");
		browser.execute("Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch).setIntPref(\"network.proxy.http\",\"127.0.0.1\");");
		*/
		
		Combo combo = new Combo(shell, SWT.READ_ONLY);
		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try
				{
					String sliceIdStr = combo.getItem(combo.getSelectionIndex());
					//TODO for testing only
					//long sliceIdL = CovertHTMLUtils.sliceMap.get(sliceIdStr);
					long sliceIdL = Long.parseLong(combo.getItem(combo.getSelectionIndex()));
					try {
						byte[] assembledDataBytes = CovertBrowserUtility.assembleSlices(sliceIdL);
						
						//String assembledData = new String(assembledDataBytes, StandardCharsets.UTF_8);
						String assembledData = new String(assembledDataBytes, StandardCharsets.UTF_8);
						browser.setText(assembledData);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Something went wrong as always : \n" + ex.toString());
					messageBox.setText("Eror");
					messageBox.open();
					
					browser.setText("<html><body><h1>Something went really wrong</h1></body></html>");
				}

			}
		});
		combo.setBounds(1012, 61, 271, 28);

		Button btnAvailableData = new Button(shell, SWT.NONE);
		btnAvailableData.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				combo.removeAll();
				String[] sliceIds = CovertBrowserUtility.getLocalSliceIds();
				for(String sliceId : sliceIds)
					combo.add(sliceId);
			}
		});
		btnAvailableData.setBounds(1306, 61, 108, 30);
		btnAvailableData.setText("Available data");

		browser.addLocationListener(new LocationListener() {

			@Override
			public void changing(LocationEvent paramLocationEvent) {
				//System.out.println(paramLocationEvent.location);
				//urlList.add(paramLocationEvent.location);
				lbllinks.setText(new Integer(sliceIdSet.size()).toString());
			}

			@Override
			public void changed(LocationEvent paramLocationEvent) {
				// TODO Auto-generated method stub

			}
		});


		text = new Text(composite, SWT.BORDER);
		text.setBounds(315, 17, 544, 26);
		text.setText("http://127.0.0.1:9700");

		Button btnGo = new Button(composite, SWT.NONE);
		btnGo.setEnabled(false);
		btnGo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
			}
		});
		btnGo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {

				if(text.getText() != null && text.getText().length() > 0)
				{
					browser.setUrl(text.getText());
				}
			}
		});

		btnGo.setBounds(865, 15, 90, 30);
		btnGo.setText("Go");

		portText = new Text(composite, SWT.BORDER);
		portText.setBounds(10, 14, 87, 26);
		portText.setText("9700");

		Button btnSetPort = new Button(composite, SWT.NONE);
		btnSetPort.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnSetPort.setBounds(103, 14, 120, 30);
		btnSetPort.setText("Set port + proxy");

		Button btnStop = new Button(composite, SWT.NONE);
		btnStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {

				try {
					ps.stopServer();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		btnStop.setBounds(229, 15, 70, 30);
		btnStop.setText("Stop");

		Button btnLoadCovertStart = new Button(composite, SWT.NONE);
		btnLoadCovertStart.setEnabled(false);

		btnLoadCovertStart.setBounds(974, 15, 167, 30);
		btnLoadCovertStart.setText("Load covert start page");

		Button btnDispatch = new Button(composite, SWT.NONE);
		btnDispatch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sliceIdSet.size() == 0)
				{
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Nothing to dispatch");
					messageBox.setText("Message");
					messageBox.open();
				}
				else if(RepeatedDatabaseCheck.stored_droplet_counter < ENV.DISPACTH_REQUEST_THRESHOLD)
				{
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Stored droplet count less than threshold value. Unsafe!");
					messageBox.setText("Message");
					messageBox.open();
				}
				else
				{
					try {					

						FileOutputStream fw = new FileOutputStream(ENV.APP_STORAGE_LOC + ENV.DELIM + 
								ENV.APP_STORAGE_SLICE_ID_FILES_LOC + ENV.DELIM + ENV.APP_STORAGE_SLICE_ID_FILE);
						byte[] out = new byte[Long.BYTES * sliceIdSet.size()];
						int tillNow = 0;
						for(String sliceId : sliceIdSet)
						{
							byte[] sliceBytes = ByteBuffer.allocate(Long.BYTES).putLong(Long.parseLong(sliceId)).array();
							System.arraycopy(sliceBytes, 0, out, tillNow, sliceBytes.length);
							tillNow += sliceBytes.length;
						}
						fw.write(out);
						fw.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					};
					
					synchronized(this)
					{
						RepeatedDatabaseCheck.stored_droplet_counter = 0;
					}
					
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Slice ids dispatched in local storage");
					messageBox.setText("Message");
					messageBox.open();

					sliceIdSet.clear();
					lbllinks.setText(new Integer(sliceIdSet.size()).toString());
				}
			}
		});
		btnDispatch.setBounds(1314, 15, 90, 30);
		btnDispatch.setText("Dispatch");


		btnSetPort.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent arg0) {

				port = Integer.parseInt(portText.getText());
				btnGo.setEnabled(true);

				ProxyServer.setProxy(port);
				try {
					ps = new ProxyServer(port);
				} catch (IOException e) {
					e.printStackTrace();
				}
				ps.startServer();
				serverClosed = true;
				btnLoadCovertStart.setEnabled(true);

			}
		});	

		//html gen
		btnLoadCovertStart.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent paramSelectionEvent) {

				String sliceFile = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_SLICE_TABLE_LOC + ENV.DELIM + ENV.APP_STORAGE_SLICE_TABLE;
				if(!new File(sliceFile).exists())
				{
					MessageBox messageBox = new MessageBox(shell ,SWT.ERROR);
					messageBox.setMessage("Slice table not exists in APP_DATA");
					messageBox.setText("Error");
					messageBox.open();
				}
				else
				{
					String sliceStartPageHtml = ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_COVERT_BROWSER_START_PAGE;
					try {
						CovertHTMLUtils.covertHTMLStartPageGenerator(sliceStartPageHtml, sliceFile, port);
					} catch (IOException e) {
						e.printStackTrace();
					}

					File sliceStartPage = new File(sliceStartPageHtml);
					String fullLocation = sliceStartPage.getAbsolutePath();
					browser.setUrl(fullLocation);
				}

			}
		});
	}
}
