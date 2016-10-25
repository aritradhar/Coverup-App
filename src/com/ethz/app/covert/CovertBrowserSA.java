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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.ethz.app.env.ENV;
import com.ethz.app.poll.RepeatedDatabaseCheck;
import com.ethz.tree.Node;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Aritra
 *
 */
public class CovertBrowserSA {

	public Shell shell;
	private Text text;
	public static Set<String> sliceNameSet = new HashSet<>();
	private com.ethz.tree.Tree sliceTree;
	private TreeItem treeItem0;
	private Set<String> exploredTree;
	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			CovertBrowserSA window = new CovertBrowserSA();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public CovertBrowserSA() {
		this.exploredTree = new HashSet<>();
	}


	/**
	 * Open the window.
	 */
	public void open() {


		Display display = Display.getDefault();
		createContents();
		Image small = new Image(display,"assets//hb.jpg");
		shell.setImage(small);    

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
	private void createContents() {

		shell = new Shell();
		shell.setSize(1459, 1003);
		shell.setText("Heavy breathing: Covert Browser");

		shell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent paramDisposeEvent) {

			}
		});

		Browser browser = new Browser(shell, SWT.NONE);

		browser.setBounds(275, 55, 1156, 891);
		browser.setJavascriptEnabled(true);	


		Composite composite = new Composite(shell, SWT.NONE);
		composite.setBounds(10, 3, 1404, 46);

		Label lbllinks = new Label(composite, SWT.NONE);

		lbllinks.setBounds(1082, 13, 74, 20);
		lbllinks.setText("#links");


		Tree tree = new Tree(shell, SWT.BORDER);
		tree.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				try
				{
					//String sliceIdStr = tree.getItem(tree.getSelectionIndex());
					//TODO for testing only
					//long sliceIdL = CovertHTMLUtils.sliceMap.get(sliceIdStr);

					String selectedtext = tree.getSelection()[0].getText();
					Node selectedNode = sliceTree.nodeMap.get(selectedtext);

					if(!selectedtext.equals("ROOT"))
					{
						if(!CovertBrowserUtility.checkSliceFolder(selectedNode.sliceId))
						{
							sliceNameSet.add(selectedNode.sliceName);
							lbllinks.setText(new Integer(sliceNameSet.size()).toString());
						}
					}

					if(!selectedNode.isLeaf())
					{	
						if(!exploredTree.contains(selectedtext))
						{
							exploredTree.add(selectedtext);
							for(Node child : selectedNode.children)
							{
								TreeItem treeItemInnter = new TreeItem(treeItem0, 0);
								treeItemInnter.setText(child.sliceName);
								if(!CovertBrowserUtility.checkSliceFolder(child.sliceId))
									treeItemInnter.setForeground(tree.getDisplay().getSystemColor(SWT.COLOR_RED));
							}
						}
					}

					if(tree.getSelection()[0].getText() == "ROOT")
						return;

					long sliceIdL = selectedNode.sliceId;
					try {
						byte[] assembledDataBytes = CovertBrowserUtility.assembleSlices(sliceIdL);

						if(assembledDataBytes == null)
						{
							browser.setText("<html><body><h1>Not locatated in disk</h1></body></html>");
							return;
						}

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

				}
			}
		});
		tree.setBounds(10, 58, 259, 888);

		//browser.setUrl("C:\\Users\\Aritra\\workspace_Mars_new\\UndergroundApp\\a.htm");
		//browser.setUrl("http://forum.codecall.net/topic/57029-simple-java-web-browser/");


		/*
		browser.execute("Components.utils.import(\"resource://gre/modules/Services.jsm\");");
		browser.execute("Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch).setIntPref(\"network.proxy.type\", 1);");
		browser.execute("Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch).setIntPref(\"network.proxy.http_port\",9700);");
		browser.execute("Components.classes[\"@mozilla.org/preferences-service;1\"].getService(Components.interfaces.nsIPrefBranch).setIntPref(\"network.proxy.http\",\"127.0.0.1\");");
		 */

		browser.addLocationListener(new LocationListener() {

			@Override
			public void changing(LocationEvent event) {

				//System.out.println(event.location);
				if(!event.location.contains("127.0.0.1") && !event.location.contains("about:blank"))
				{	
					sliceNameSet.add(event.location);
					lbllinks.setText(new Integer(sliceNameSet.size()).toString());
					browser.setText("<html><body><h1>Add requested link  </h1><br>" + event.location +"</body></html>");

					event.doit = false;
				}
				//event.location = "http://127.0.0.1:9070";
				//System.out.println("bla");
				//event.doit = false;
				//urlList.add(paramLocationEvent.location);
				//lbllinks.setText(new Integer(sliceIdSet.size()).toString());
			}

			@Override
			public void changed(LocationEvent paramLocationEvent) {
				// TODO Auto-generated method stub
			}
		});


		text = new Text(composite, SWT.BORDER);
		text.setBounds(315, 10, 544, 26);
		text.setText("http://");

		Button btnGo = new Button(composite, SWT.NONE);
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

		btnGo.setBounds(865, 8, 90, 30);
		btnGo.setText("Go");

		/*Button btnLoadCovertStart = new Button(composite, SWT.NONE);
		btnLoadCovertStart.setEnabled(false);

		btnLoadCovertStart.setBounds(974, 8, 167, 30);
		btnLoadCovertStart.setText("Load covert start page");
		 */

		Button btnDispatch = new Button(composite, SWT.NONE);
		btnDispatch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sliceNameSet.size() == 0)
				{
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Nothing to dispatch");
					messageBox.setText("Message");
					messageBox.open();
				}
				else if(RepeatedDatabaseCheck.stored_droplet_counter < ENV.DISPACTH_REQUEST_THRESHOLD)
				{
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Stored droplet count less than threshold value. Unsafe! (" + 
							RepeatedDatabaseCheck.stored_droplet_counter + "<" + ENV.DISPACTH_REQUEST_THRESHOLD + ")");

					messageBox.setText("Message");
					messageBox.open();
				}
				else
				{
					try {					

						FileOutputStream fw = new FileOutputStream(ENV.APP_STORAGE_LOC + ENV.DELIM + 
								ENV.APP_STORAGE_SLICE_ID_FILES_LOC + ENV.DELIM + ENV.APP_STORAGE_SLICE_ID_FILE);
						byte[] out = new byte[Long.BYTES * sliceNameSet.size()];
						int tillNow = 0;
						for(String sliceName : sliceNameSet)
						{
							long sliceId = sliceTree.nodeMap.get(sliceName).sliceId;
							byte[] sliceBytes = ByteBuffer.allocate(Long.BYTES).putLong(sliceId).array();
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

					sliceNameSet.clear();
					lbllinks.setText(new Integer(sliceNameSet.size()).toString());
				}
			}
		});
		btnDispatch.setBounds(1288, 8, 106, 30);
		btnDispatch.setText("Dispatch");

		Button btnLoadCovertSite = new Button(composite, SWT.NONE);
		btnLoadCovertSite.setBounds(10, 10, 165, 26);
		btnLoadCovertSite.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				String sliceTableDataJSON = null;
				try {
					sliceTableDataJSON = CovertBrowserUtility.getSliceTable();
				} catch (IOException e1) {
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Something went wrong as always : \n" + e1.toString());
					messageBox.setText("Error");
					messageBox.open();
				}

				try
				{
					sliceTree = new com.ethz.tree.Tree(sliceTableDataJSON);
					tree.removeAll();
					exploredTree.clear();
					treeItem0 = new TreeItem(tree, 0);
					treeItem0.setText("ROOT");
				}
				catch(Exception ex)
				{
					MessageBox messageBox = new MessageBox(shell ,SWT.ICON_INFORMATION);
					messageBox.setMessage("Slice tree file possibly empty : \n" + ex.toString());
					messageBox.setText("Error");
					messageBox.open();
				}
			}
		});
		btnLoadCovertSite.setText("Load Covert site tree");

		Button backButton = new Button(composite, SWT.NONE);
		backButton.setBounds(235, 8, 35, 30);
		backButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				browser.back();
			}
		});
		backButton.setText("<-");

		Button forwardButton = new Button(composite, SWT.NONE);
		forwardButton.setBounds(276, 8, 35, 30);
		forwardButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browser.forward();
			}
		});
		forwardButton.setText("->");

		Button btnModify = new Button(composite, SWT.NONE);
		btnModify.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				btnModify.setEnabled(false);
				try {
					Display display = Display.getDefault();
					SliceList shell = new SliceList(display, sliceNameSet);
					shell.open();
					shell.layout();
					while (!shell.isDisposed()) {
						if (!display.readAndDispatch()) {
							display.sleep();
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				finally {
					btnModify.setEnabled(true);
					lbllinks.setText(new Integer(sliceNameSet.size()).toString());
				}
			}
		});
		btnModify.setBounds(1181, 8, 90, 30);
		btnModify.setText("Modify");
	}
}