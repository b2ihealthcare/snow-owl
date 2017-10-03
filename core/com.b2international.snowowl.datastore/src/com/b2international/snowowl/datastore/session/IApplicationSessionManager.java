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

import java.security.PublicKey;

import javax.security.auth.login.LoginException;

import com.b2international.commons.Pair;

/**
 * The service interface for the application-wide session manager.
 * 
 */
public interface IApplicationSessionManager {

	String KEY_USER_ID = "userId";
	String KEY_USER_ROLES = "userRoles";
	String KEY_IS_AUTHENTICATED = "isAuthenticated";
	String KEY_RANDOM_BYTES = "randomBytes";
	String KEY_SERVER_PRIVATE_KEY = "serverPrivateKey";
	String KEY_SESSION_ID = "sessionId";

	/**
	 * Asks the server for a one-time access token, which includes:
	 * <ul>
	 * <li>an encrypted random sequence of bytes which can be decrypted with the <b>private pair</b> of the specified client public key;
	 * <li>the server's public key, which can be used to encode the answer (the decrypted random sequence concatenated with the user's password).
	 * </ul>  
	 * 
	 * @param userId
	 * @param clientPublicKey
	 * @return
	 */
	AccessToken requestToken(final String userId, final PublicKey clientPublicKey);
			
	/**
	 * Sends the server an encrypted response to provide identification for the user. The sequence to send contains the last random sequence
	 * received via {@link #requestToken(String, PublicKey)}, concatenated with the user's password characters. The resulting byte sequence
	 * is encrypted with the server's <i>public key</i>, also received with a previous {@link AccessToken}.
	 * <p>
	 * If the server does not accept the sequence, a new access token will need to be requested and a SecurityException runtime exception will be thrown.
	 * 
	 * @param response
	 */
	void loginWithResponse(final byte[] response) throws SecurityException;
	
	/**
	 * @param userId the user identifier (may not be {@code null}) 
	 * @param password the user's password (may not be {@code null})
	 * @return the object representing the logged in user
	 * @throws LoginException if the user identifier and/or the password was given incorrectly
	 */
	void authenticate(String userId, String password) throws LoginException;

	/**Returns with an iterable of user ID with session ID of all connected sessions.*/
	Iterable<Pair<String, String>> getConnectedSessionInfo();

	/**Sets the non-administrator user login flag. {@code true} if the login is enabled, otherwise {@code false}.*/
	void enableLogins(boolean loginEnabled);

	/**Returns {@code true} if non-administrator login is currently allowed to the managed repositories, otherwise returns with {@code false}.*/
	boolean isLoginEnabled();
}