package com.ethz.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JScrollBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class AppWindow {

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static App app;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AppWindow window = new AppWindow();
					window.app = new App();
					app.extractMessageFireFox();
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
	public AppWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Firefox cache extractor");
		frame.setBounds(100, 100, 1223, 848);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane.setViewportView(textArea);
		
		
		JButton btnLoadMessage = new JButton("Load Message");
		btnLoadMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(app.message);
			}
		});
		panel.add(btnLoadMessage);
		
		JButton btnLoadSignature = new JButton("Load Signature");
		btnLoadSignature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textArea.setText(app.signatureString);
			}
		});
		panel.add(btnLoadSignature);
		
		JButton btnVerifySignature = new JButton("Verify Signature");
		btnVerifySignature.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(app.verifyResult)
				{
					textArea.setText("Verify success");
				}
				else
				{
					textArea.setText("Invalid signature");
				}
			}
		});
		panel.add(btnVerifySignature);
		
		
	}

}
