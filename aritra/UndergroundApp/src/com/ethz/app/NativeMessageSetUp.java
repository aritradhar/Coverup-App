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
import java.nio.charset.StandardCharsets;
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

		if(ENV.isWindows)
			setUpWindows(frame, jsonFilePath);

		else if(ENV.isLinux)
			setUpLinux(frame, jsonFilePath);

		else
			setUpMac(frame, jsonFilePath);

	}
	
	public static String firefoxRegCommand = "REG ADD \"HKEY_CURRENT_USER\\SOFTWARE\\Mozilla\\NativeMessagingHosts\\native_comm\" ";
	public static String chromeRegCommand = "REG ADD \"HKCU\\Software\\Google\\Chrome\\NativeMessagingHosts\\native_comm\" ";
	
	public static JSONObject makeNativeJson(String jsonFilePath) throws IOException
	{
		String jsonString = new String(Files.readAllBytes(new File(jsonFilePath).toPath()), StandardCharsets.UTF_8);
		JSONObject jsonRead = new JSONObject(jsonString);
		
		JSONObject jObject = new JSONObject();
		jObject.put("name", jsonRead.getString("name"));
		jObject.put("description", jsonRead.getString("description"));
		jObject.put("path", jsonFilePath.replaceAll("native_comm.json", "native_ext.bat"));
		jObject.put("type", jsonRead.getString("type"));
		/*
		JSONArray jArray = new JSONArray();
		jArray.put("chrome-extension://hdcigkkjdbihcfppnomipaadklmofhjl//");
		jArray.put("chrome-extension://dcgbplpkphamfmgclhmmdmnkdhhjbdbb//");
		*/
		jObject.put("allowed_extensions", jsonRead.get("allowed_extensions"));
		
		return jObject;
	}
	
	public static void setUpWindows(JFrame frame, String jsonFilePath) throws IOException
	{
		String regCommand = null;
		if(AppMain.selectedPrimaryBrowser.equals(ENV.BROWSER_CHROME) || AppMain.selectedPrimaryBrowser.equals(ENV.BROWSER_NATIVE_MESSAGE)) 
			regCommand = chromeRegCommand;
		else
			regCommand = firefoxRegCommand;
		
		if(!ENV.isAdmin())
			JOptionPane.showMessageDialog(frame, "Not administrator. Run with administrator", 
					"Not administrator", JOptionPane.ERROR_MESSAGE);				 

		Process p = null;
		try 
		{
			p = Runtime.getRuntime().exec(regCommand + "/ve /d \""+ jsonFilePath + "\" /F");
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
		JSONObject jObject = makeNativeJson(jsonFilePath);

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
			fw.append("\"" + pythonPath.getCanonicalPath() + "\" \"" + jsonFilePath.replaceAll("native_comm.json", "native-messaging-example-host") + "\"");
			fw.flush();
			fw.close();
			JOptionPane.showMessageDialog(frame, stb.toString(), "Execution result", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	
	public static final String linuxChromeNativePathSystem = "/etc/opt/chrome/native-messaging-hosts";
	public static final String linuxChromeNativePathUser = System.getenv("HOME") + ".config/chromium/NativeMessagingHosts/";
	
	/**
	 * Only written for Chrome, Firefox excluded
	 * @param frame
	 * @param jsonFilePath
	 * @throws IOException
	 */
	public static void setUpLinux(JFrame frame, String jsonFilePath) throws IOException
	{
		File file = new File(linuxChromeNativePathSystem);
		if(!file.exists())
			file.mkdir();
		file = null;
		file = new File(linuxChromeNativePathUser);
		if(!file.exists())
			file.mkdir();
		try
		{
			FileWriter fwS = new FileWriter(linuxChromeNativePathSystem + "/native_comm.json");
			FileWriter fwU = new FileWriter(linuxChromeNativePathUser + "/native_comm.json");
			
			JSONObject jObject = makeNativeJson(jsonFilePath);
			
			fwS.write(jObject.toString(2));
			fwS.flush();
			fwS.close();
			
			fwU.write(jObject.toString(2));
			fwU.flush();
			fwU.close();
			JOptionPane.showMessageDialog(frame, "Operation executed successfully", "Execution result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
		}
	}
 
	public static final String macChromeNativePathSystem = " /Library/Google/Chrome/NativeMessagingHosts";
	public static final String macChromeNativePathUser = "~/Library/Application Support/Google/Chrome/NativeMessagingHosts";
	
	public static void setUpMac(JFrame frame, String jsonFilePath) throws IOException
	{
		File file = new File(macChromeNativePathSystem);
		if(!file.exists())
			file.mkdir();
		file = null;
		file = new File(macChromeNativePathUser);
		if(!file.exists())
			file.mkdir();
		try
		{
			FileWriter fwS = new FileWriter(macChromeNativePathSystem + "/native_comm.json");
			FileWriter fwU = new FileWriter(macChromeNativePathUser + "/native_comm.json");
			
			JSONObject jObject = makeNativeJson(jsonFilePath);
			
			fwS.write(jObject.toString(2));
			fwS.flush();
			fwS.close();
			
			fwU.write(jObject.toString(2));
			fwU.flush();
			fwU.close();
			JOptionPane.showMessageDialog(frame, "Operation executed successfully", "Execution result", JOptionPane.INFORMATION_MESSAGE);
		}
		catch(IOException ex)
		{
			JOptionPane.showMessageDialog(frame, "Operation failed. Please follow instruction from CoverUp.tech", 
					"Execution result", JOptionPane.ERROR_MESSAGE);
		}
	}
}
