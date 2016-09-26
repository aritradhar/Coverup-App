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

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JTextField;

import org.eclipse.swt.widgets.Text;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JEditorPane;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.awt.event.ActionEvent;

/**
 * @author Aritra
 *
 */
public class CovertBrowserN {

	private JFrame frame;
	private JTextField textField;
	private JTextField txtHttp;

	private Text portText;
	private int port;
	
	private ProxyServer ps;
	private boolean serverClosed;
	public static Set<String> sliceIdSet = new HashSet<>();;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CovertBrowserN window = new CovertBrowserN();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public CovertBrowserN() {
		
		this.ps = null;
		this.serverClosed = false;
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 1206, 973);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Covert Browser");
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		
		JButton btnStartProxy = new JButton("Start Proxy");
		btnStartProxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
			}
		});
		panel.add(btnStartProxy);
		
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				if(serverClosed)
				{
					try {
						ps.stopServer();
					} catch (IOException e1) {

						e1.printStackTrace();
					}
				}
			}
		});
		panel.add(btnStop);
		
		txtHttp = new JTextField();
		txtHttp.setText("http://127.0.0.1:9700");
		panel.add(txtHttp);
		txtHttp.setColumns(10);
		
		JButton btnGo = new JButton("Go");
		panel.add(btnGo);
		
		JButton btnCovertPage = new JButton("Covert Page");
		panel.add(btnCovertPage);
		
		JLabel lbllinks = new JLabel("#links");
		panel.add(lbllinks);
		
		JButton btnDispatch = new JButton("DIspatch");
		panel.add(btnDispatch);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.SOUTH);
		
		JComboBox<String> jCombo = new JComboBox<>();
		panel_1.add(jCombo);
		
		JButton btnLoadData = new JButton("Load data");
		panel_1.add(btnLoadData);
		
		JEditorPane editorPane = new JEditorPane();
		frame.getContentPane().add(editorPane, BorderLayout.CENTER);
	}

}
