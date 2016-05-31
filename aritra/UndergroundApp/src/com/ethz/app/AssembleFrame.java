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
import javax.swing.JProgressBar;

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
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException 
	{
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());	

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try 
				{
					AssembleFrame window = new AssembleFrame();
					window.frame.setVisible(true);
				} 
				catch (Exception e) 
				{
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
		frame.setTitle("Fountain assemble");
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
		JProgressBar progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		btnDisplay.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{

				if(JSONDirPath == null)
					JOptionPane.showMessageDialog(frame, "JSON path not set!");

				else
				{
					File[] files =  new File(JSONDirPath).listFiles();
					
					Glass glass = null;

					try
					{
						int counter = 0;
						for(File file : files)
						{
							//System.out.println(file.getAbsoluteFile());
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
								
								//initialize glass only once
								if(glass == null)
									glass = new Glass(job.getInt("num_chunks"));
								
								glass.addDroplet(d);

								counter++;
								if(glass.isDone())
								{	
									//TODO: this size has to be dynamic depends on the data size which is to be preshared in the table
									//byte[] decodedData = new byte[1000];

									//for(int i = 0; i < Glass.chunks.length; i++)
									//	System.arraycopy(Glass.chunks[i], 0, decodedData, i * 100, 100);

									byte[] decodedData = glass.getDecodedData();
									
									JOptionPane.showMessageDialog(frame, "Decoding success\nDroplet utilized : " + counter + ", Total Droplets : " + files.length);
									
									//textArea.setText(Base64.getUrlEncoder().encodeToString(decodedData));
									textArea.setText(new String(decodedData));
									progressBar.setValue(100);
									break;
								}	

								//JOptionPane.showMessageDialog(frame, "Assemble success!!!");
							} 
							catch (FileNotFoundException e1) 
							{
								e1.printStackTrace();
							} 
							catch (IOException e1) 
							{
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
						if(!glass.isDone())
						{
							JOptionPane.showMessageDialog(frame, "Not enought droplets yet!");
							byte[][] partialChunks = Glass.chunks;
							StringBuffer stb = new StringBuffer();
							
							int completed = 0, s_size = 0;
							boolean flag = false;
							
							for(byte b[] : partialChunks)
							{
								//total += b.length;
								if(b == null)
								{								
									stb.append("<_______missing chunk______>");
								}
								else
								{
									flag = true;
									s_size = b.length;
									completed += b.length;
									stb.append(new String(b));
								}
							}
							
							textArea.setText(stb.toString());
							if(flag)
								progressBar.setValue((100 * completed)/(s_size * partialChunks.length));
							else
								progressBar.setValue(0);
							
							//System.out.println((completed * 100)/(s_size * partialChunks.length));
						}
						
					}
					catch(Exception ex)
					{
						JOptionPane.showMessageDialog(frame, "Bad input!!");
					}


				}
			}
		});
		panel.add(btnDisplay);
		panel.add(progressBar);
	}

}
