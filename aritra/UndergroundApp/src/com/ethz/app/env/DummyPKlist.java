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
package com.ethz.app.env;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

import org.whispersystems.curve25519.Curve25519;
import org.whispersystems.curve25519.Curve25519KeyPair;

/**
 * @author Aritra
 *
 */
public class DummyPKlist {

	public static void main(String[] args) throws IOException {
		
		FileWriter fw = new FileWriter(ENV.APP_STORAGE_PUBLIC_KEY_LIST);
		for(int i = 0; i < 20; i++)
		{
			Curve25519KeyPair keyPair = Curve25519.getInstance(Curve25519.BEST).generateKeyPair();
			byte[] privateKey = keyPair.getPrivateKey();
			fw.append(Base64.getUrlEncoder().encodeToString(privateKey) + "\n");
		}
		fw.close();
	}
}
