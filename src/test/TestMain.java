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
package test;

import java.util.List;

import org.whispersystems.libsignal.IdentityKeyPair;
import org.whispersystems.libsignal.InvalidKeyException;
import org.whispersystems.libsignal.state.PreKeyRecord;
import org.whispersystems.libsignal.state.SignedPreKeyRecord;
import org.whispersystems.libsignal.util.KeyHelper;

/**
 * @author Aritra
 *
 */
public class TestMain {
	
	public static void main(String[] args) throws InvalidKeyException {
		
		IdentityKeyPair    identityKeyPair = KeyHelper.generateIdentityKeyPair();
		int                registrationId  = KeyHelper.generateRegistrationId(false);
		List<PreKeyRecord> preKeys         = KeyHelper.generatePreKeys(0, 100);
		SignedPreKeyRecord signedPreKey    = KeyHelper.generateSignedPreKey(identityKeyPair, 5);
		
		System.out.println();
	}

}
