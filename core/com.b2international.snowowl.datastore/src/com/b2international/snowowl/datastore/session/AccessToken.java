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
package com.b2international.snowowl.datastore.session;

import java.io.Serializable;
import java.security.PublicKey;

/**
 * 
 */
public class AccessToken implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final PublicKey serverPublicKey;
	private final byte[] encryptedRandomBytes;

	/**
	 * 
	 * @param serverPublicKey
	 * @param encryptedRandomBytes
	 */
	public AccessToken(PublicKey serverPublicKey, byte[] encryptedRandomBytes) {
		this.serverPublicKey = serverPublicKey;
		this.encryptedRandomBytes = encryptedRandomBytes;
	}
	
	/**
	 * @return the public key of the server to encode a response sequence with
	 */
	public PublicKey getServerPublicKey() {
		return serverPublicKey;
	}
	
	/**
	 * @return the random byte sequence to include with the response
	 */
	public byte[] getEncryptedRandomBytes() {
		return encryptedRandomBytes;
	}
}