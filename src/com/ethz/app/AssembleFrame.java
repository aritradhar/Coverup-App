package com.ethz.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.json.JSONObject;

import com.ethz.fountain.Droplet;
import com.ethz.fountain.Glass;

public class AssembleFrame {

	JFrame frame;
	
	JFileChooser chooser;

	public static String JSONDirPath;
	/**
	 * Launch the application.
	 * @throws UnsupportedLookAndFeelException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	
		
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

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


		JButton btnAssemble = new JButton("Locate");
		btnAssemble.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				
				chooser = new JFileChooser(); 
				chooser.setCurrentDirectory(new java.io.File("."));
				chooser.setDialogTitle("Select JSON dir");
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
						
				chooser.setAcceptAllFileFilterUsed(false);  
				
				if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) 
				{ 		
					JSONDirPath = chooser.getSelectedFile().getAbsolutePath();
					lblNewLabel.setText("JSON folder : " + JSONDirPath);
				}
				else 
				{
					lblNewLabel.setText("JSON folder not selected" + JSONDirPath);
					
				}
			}
		});
		panel.add(btnAssemble);

		JButton btnDisplay = new JButton("Assemble");
		btnDisplay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if(JSONDirPath == null)
					JOptionPane.showMessageDialog(frame, "JSON path not set!");

				else
				{
					File[] files =  new File(JSONDirPath).listFiles();

					Glass glass = new Glass(10);
					for(File file : files)
					{

						System.out.println(file.getAbsoluteFile());
						BufferedReader br = null;
						try 
						{
							br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
							StringBuffer stb = new StringBuffer();
							String st = "";

							while((st = br.readLine()) != null)
								stb.append(st);

							JSONObject job = new JSONObject(stb.toString());

							Droplet d = new Droplet(Base64.getUrlDecoder().decode(job.get("data").toString()), job.getLong("seed"), job.getInt("num_chunks"));
							glass.addDroplet(d);

							if(glass.isDone())
							{	
								byte[] decodedData = new byte[1000];

								for(int i = 0; i < Glass.chunks.length; i++)
									System.arraycopy(Glass.chunks[i], 0, decodedData, i * 100, 100);

								textArea.setText(Base64.getUrlEncoder().encodeToString(decodedData));

								System.err.println("Decoding SUCCESS");
								break;
							}	

							//JOptionPane.showMessageDialog(frame, "Assemble success!!!");
						} 
						catch (FileNotFoundException e1) 
						{
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						finally 
						{
							try {

								br.close();
							} 
							catch (IOException e1) 
							{
								e1.printStackTrace();
							}
						}
					}

				}
			}
		});
		panel.add(btnDisplay);
	}

}
