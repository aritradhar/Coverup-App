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
import java.io.FileWriter;
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

import com.ethz.app.env.ENV;
import com.ethz.fountain.Droplet;
import com.ethz.fountain.Fountain;
import com.ethz.fountain.Glass;
import com.ethz.ugs.compressUtil.CompressUtil;

import javax.swing.JProgressBar;

public class AssembleFrame {

	JFrame frame;

	JFileChooser chooser;
	public static String JSONDirPath;
	String urlString;
	boolean independent;
	
	public static boolean glassDone = false;
	
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
		this.independent = true;
		initialize();
	}
	
	public AssembleFrame(String urlString) {
		this.urlString = urlString;
		
		JSONObject jObject = TableChecker.URL_JSON_TABLE_MAP.get(urlString);
		String dropletDirID = jObject.getString("dropletLoc");
		JSONDirPath = ENV.APP_STORAGE_LOC + ENV.DELIM + dropletDirID;
		
		this.independent = false;
		System.out.println(JSONDirPath);
		
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
		
		if(!independent)
			lblNewLabel.setText("JSON folder : " + JSONDirPath);
		
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
							
							if(file.getName().contains(ENV.APP_STORAGE_DROPLET_URL) || file.getName().contains(ENV.APP_STORAGE_COMPLETE_DATA))
								continue;
							
							//System.out.println(file.getAbsoluteFile());
							BufferedReader br = null;
							try 
							{
								br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
								StringBuffer stb = new StringBuffer();
								String st = "";

								while((st = br.readLine()) != null)
									stb.append(st);

								JSONObject jObject = new JSONObject(stb.toString());

								
								Droplet d = new Droplet(Base64.getUrlDecoder().decode(jObject.get("data").toString()), Base64.getUrlDecoder().decode(jObject.get("seed").toString()), jObject.getInt("num_chunks"));
								
								//initialize glass only once
								if(glass == null)
									glass = new Glass(jObject.getInt("num_chunks"));
								
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
									glassDone = true;
									
									//put this information in APP_STORAGE_LOC
									String dropletUrlFileName =  JSONDirPath + ENV.DELIM + ENV.APP_STORAGE_DROPLET_URL;
									BufferedReader brUrl = new BufferedReader(new FileReader(dropletUrlFileName));
									String stTemp = null, fountainUrl = null;
									while((stTemp = brUrl.readLine()) != null)
									{
										fountainUrl = stTemp;
									}
									brUrl.close();
									
									FileWriter compl_fw = new FileWriter(ENV.APP_STORAGE_LOC + ENV.DELIM + ENV.APP_STORAGE_COMPLETED_DROPLET_FILE, true);
									compl_fw.append(fountainUrl + "\n");
									compl_fw.close();
									
									File completeDataFile = new File(JSONDirPath + ENV.DELIM + ENV.APP_STORAGE_COMPLETE_DATA);

									if(!completeDataFile.exists())
									{
										FileWriter data_fw = new FileWriter(JSONDirPath + ENV.DELIM + ENV.APP_STORAGE_COMPLETE_DATA);
										data_fw.append(new String(decodedData));
										data_fw.close();
									}
									
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
					catch(NullPointerException ex)
					{
						ex.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Droplet dir missing!!");
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						JOptionPane.showMessageDialog(frame, "Bad input!!");
					}


				}
			}
		});
		panel.add(btnDisplay);
		panel.add(progressBar);
		
		JButton decompressButton = new JButton("Decompress");
		
		if(!ENV.COMPRESSION_SUPPORT)
			decompressButton.setEnabled(false);
		
		decompressButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if(!glassDone)
					JOptionPane.showMessageDialog(frame, "Not enought droplets yet!");
				
				else
				{
					byte[] compressedData = textArea.getText().getBytes();
					byte[] decompressedData = null;
					try 
					{
						decompressedData = CompressUtil.deCompress(compressedData);
					} 
					catch (IOException e1) 
					{			
						e1.printStackTrace();
					}
					textArea.setText(new String(decompressedData));
				}
			}
		});
		panel.add(decompressButton);
	}

}
