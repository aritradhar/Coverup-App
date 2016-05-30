package com.ethz.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;

public class AssembleFrame {

	JFrame frame;

	public static String JSONDirPath;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					AssembleFrame window = new AssembleFrame();
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
	public AssembleFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 832, 635);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.SOUTH);
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);

		JTextArea textArea = new JTextArea();
		textArea.setForeground(Color.BLACK);;
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);
		
		JPanel panel_1 = new JPanel();
		scrollPane.setColumnHeaderView(panel_1);
		
		JLabel lblNewLabel = new JLabel("JSON folder not selected");
		panel_1.add(lblNewLabel);
		
		
		JButton btnAssemble = new JButton("Assemble");
		btnAssemble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				
				 new ShowDirectoryDialog().run();
				 lblNewLabel.setText("JSON folder : " + JSONDirPath);
				 //System.out.println(JSONDirPath);
				
			}
		});
		panel.add(btnAssemble);
		
		JButton btnDisplay = new JButton("Display");
		panel.add(btnDisplay);
	}

}
