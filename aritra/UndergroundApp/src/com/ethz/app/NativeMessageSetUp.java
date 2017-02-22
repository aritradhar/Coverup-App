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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.ethz.app.env.ENV;

/**
 * @author Aritra
 *
 */
public class NativeMessageSetUp {


	public static void setUp(JFrame frame)
	{
		JFileChooser choose = new JFileChooser();
		choose.setDialogTitle("Choose native_comm.json file");
		choose.addChoosableFileFilter(new FileNameExtensionFilter("json files", "json"));
		choose.showDialog(frame, "Open file");
		String jsonFilePath = null;
		try {
			jsonFilePath = choose.getSelectedFile().getCanonicalPath();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		if(System.getProperty("os.name").startsWith("Windows"))
			setUpWindows(frame, jsonFilePath);
		
		else if(System.getProperty("os.name").startsWith("Linux"))
			setUpLinux(frame, jsonFilePath);
		
		else
			setUpMac(frame, jsonFilePath);
		
	}

	public static void setUpWindows(JFrame frame, String jsonFilePath)
	{
		if(!ENV.isAdmin())
			JOptionPane.showMessageDialog(frame, "Not administrator. Run with administrator", 
					"Not administrator", JOptionPane.ERROR_MESSAGE);				 
		
		Process p = null;
		try 
		{
			p = Runtime.getRuntime().exec("REG ADD \"HKEY_CURRENT_USER\\SOFTWARE\\Mozilla\\NativeMessagingHosts\\native_comm\" "
					+ "/ve /d \""+ jsonFilePath + "\" /F");
		} 
		catch (IOException e1) 
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
			return;
		}

		BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = null; 
		StringBuffer stb = new StringBuffer();
		
		try 
		{
			while ((line = input.readLine()) != null)
				stb.append(line + "\n");
		} 
		catch (IOException e1) 
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
			return;
		}


		try 
		{
			p.waitFor();
		} catch (InterruptedException e2)
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
		}

		JOptionPane.showMessageDialog(frame, stb.toString(), "Execution result", JOptionPane.INFORMATION_MESSAGE);
	}

	public static void setUpLinux(JFrame frame, String jsonFilePath)
	{
		File file = new File(System.getenv("HOME") + "/.mozilla/native-messaging-hosts");
		if(!file.exists())
		{
			JOptionPane.showMessageDialog(frame, "Firefox installation directory not found", "Firefox directory missing",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			Files.copy(new File(jsonFilePath).toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			JOptionPane.showMessageDialog(frame, "Operation executed successfully", "Execution result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public static void setUpMac(JFrame frame, String jsonFilePath)
	{
		File file = new File("/Library/Application Support/Mozilla/NativeMessagingHosts");
		if(!file.exists())
		{
			JOptionPane.showMessageDialog(frame, "Firefox installation directory not found", "Firefox directory missing", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		try
		{
			Files.copy(new File(jsonFilePath).toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			JOptionPane.showMessageDialog(frame, "Operation executed successfully", "Execution result", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", "Execution result", 
					JOptionPane.ERROR_MESSAGE);
		}
	}
}
