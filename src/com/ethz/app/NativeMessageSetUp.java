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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import com.ethz.app.env.ENV;

import javafx.stage.FileChooser;

/**
 * @author Aritra
 *
 */
public class NativeMessageSetUp {


	public static void setUp(JFrame frame) throws IOException
	{
		JFileChooser choose = new JFileChooser(".");
		choose.setDialogTitle("Choose native_comm.json file");
		choose.addChoosableFileFilter(new FileNameExtensionFilter("json files", "json"));
		int res = choose.showDialog(frame, "Open file");
		String jsonFilePath = null;

		if(res == JFileChooser.CANCEL_OPTION)
			return;

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

	public static void setUpWindows(JFrame frame, String jsonFilePath) throws IOException
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

		FileWriter fw = new FileWriter(jsonFilePath);
		JSONObject jObject = new JSONObject();
		jObject.put("name", "native_comm");
		jObject.put("description", "Chrome Native Messaging API Example Host");
		jObject.put("path", jsonFilePath.replaceAll("native_comm.json", "native_ext.bat"));
		jObject.put("type", "stdio");
		JSONArray jArray = new JSONArray();
		jArray.put("SecureExtension@example.com");
		jObject.put("allowed_extensions", jArray);

		fw.write(jObject.toString(2));
		fw.flush();
		fw.close();

		JFileChooser choose = new JFileChooser(".");
		choose.setDialogTitle("Locate python.exe file (python27)");
		int res = choose.showDialog(frame, "Open file");
		if(res == JFileChooser.APPROVE_OPTION)
		{
			File pythonPath = choose.getSelectedFile();
			fw = new FileWriter(jsonFilePath.replaceAll("native_comm.json", "native_ext.bat"));
			fw.append("@echo off\n");
			fw.append("\"" + pythonPath.getCanonicalPath() + "\" \"" + jsonFilePath.replaceAll("native_comm.json", "native_ext.py") + "\"");
			fw.flush();
			fw.close();
			JOptionPane.showMessageDialog(frame, stb.toString(), "Execution result", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public static void setUpLinux(JFrame frame, String jsonFilePath) throws IOException
	{
		File file = new File(System.getenv("HOME") + "/.mozilla/native-messaging-hosts");
		if(!file.exists())
			file.mkdir();
			
		try
		{
			FileWriter fw = new FileWriter(System.getenv("HOME") + "/.mozilla/native-messaging-hosts/native_comm.json");
			JSONObject jObject = new JSONObject();
			jObject.put("name", "native_comm");
			jObject.put("description", "Chrome Native Messaging API Example Host");
			jObject.put("path", jsonFilePath.replaceAll("native_comm.json", "native_ext.py"));
			jObject.put("type", "stdio");
			JSONArray jArray = new JSONArray();
			jArray.put("SecureExtension@example.com");
			jObject.put("allowed_extensions", jArray);
			fw.write(jObject.toString(2));
			fw.flush();
			fw.close();
			JOptionPane.showMessageDialog(frame, "Operation executed successfully", "Execution result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
		}
	}
 
	public static void setUpMac(JFrame frame, String jsonFilePath) throws IOException
	{
		File file = new File("/Library/Application Support/Mozilla/NativeMessagingHosts");
		if(!file.exists())
			file.mkdir();		
		
		try
		{
			FileWriter fw = new FileWriter(System.getenv("HOME") + "/.mozilla/native-messaging-hosts/native_comm.json");
			JSONObject jObject = new JSONObject();
			jObject.put("name", "native_comm");
			jObject.put("description", "Chrome Native Messaging API Example Host");
			jObject.put("path", jsonFilePath.replaceAll("native_comm.json", "native_ext.py"));
			jObject.put("type", "stdio");
			JSONArray jArray = new JSONArray();
			jArray.put("SecureExtension@example.com");
			jObject.put("allowed_extensions", jArray);
			fw.write(jObject.toString(2));
			fw.flush();
			fw.close();
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
