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
package com.b2international.snowowl.datastore.cdo;

import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Arrays;

import com.b2international.commons.encoding.RSAUtils;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.session.AccessToken;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.identity.domain.User;
import com.google.common.base.Charsets;

/**
 * Client side utility class for user authentication.
 * 
 */
final class Authenticator {
	
	private final IApplicationSessionManager sessionManager;
	
	private final String userName;
	private final String password;
	private final KeyPair clientKeyPair;
			
	public Authenticator(final IApplicationSessionManager sessionManager, final String userName, final String password) {
		this.sessionManager = sessionManager;
		this.userName = userName;
		this.password = password;		
		this.clientKeyPair = RSAUtils.generateRSAKeyPair();
	}

	public User authenticate() throws SnowowlServiceException {
		try {
			
			final AccessToken accessToken = sessionManager.requestToken(userName, clientKeyPair.getPublic());
			final byte[] encryptedRandomBytes = accessToken.getEncryptedRandomBytes();
			final PublicKey serverPublicKey = accessToken.getServerPublicKey();
			
			final byte[] decryptedRandomBytes = RSAUtils.rsaDecrypt(encryptedRandomBytes, clientKeyPair.getPrivate());
			final byte[] passwordBytes = password.getBytes(Charsets.UTF_8);
			
			final byte[] responseBytes = Arrays.copyOf(decryptedRandomBytes, decryptedRandomBytes.length + passwordBytes.length);
			System.arraycopy(passwordBytes, 0, responseBytes, decryptedRandomBytes.length, passwordBytes.length);
			
			final byte[] encryptedResponseBytes = RSAUtils.rsaEncrypt(responseBytes, serverPublicKey);
			
			return sessionManager.loginWithResponse(encryptedResponseBytes);
		} catch (final Exception e) {
			throw new SnowowlServiceException("Authentication failed!", e);
		}
	}
}