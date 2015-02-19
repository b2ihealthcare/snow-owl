/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.commons.encoding;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAUtils {
	
	public final static String RSA_ALGORITHM_KEY = "RSA";
	
	
	public static KeyPair generateRSAKeyPair() {
		
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM_KEY);
			kpg.initialize(2048);
			KeyPair keyPair = kpg.genKeyPair();			
			return keyPair;
		} catch (NoSuchAlgorithmException e) {
			//should not happen
			throw new RuntimeException("Unknown encryption algorithm provided: "+RSA_ALGORITHM_KEY, e);
		}
	}
	
	public static byte[] rsaEncrypt(byte[] data, PublicKey publicKey) throws Exception{
		  Cipher cipher = Cipher.getInstance("RSA");
		  cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		  byte[] cipherData = cipher.doFinal(data);
		  return cipherData;
	}
	
	
	public static byte[] rsaDecrypt(byte[] encryptedData, PrivateKey privateKey) throws Exception {
		  Cipher cipher = Cipher.getInstance("RSA");
		  cipher.init(Cipher.DECRYPT_MODE, privateKey);
		  byte[] cipherData = cipher.doFinal(encryptedData);
		  return cipherData;
	}

}