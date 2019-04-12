/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.security.auth.login.LoginException;

import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.rpc.RpcSession;

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
	 * @return the logged in users identity
	 */
	User loginWithResponse(final byte[] response) throws SecurityException;
	
	/**
	 * @param userId the user identifier (may not be {@code null}) 
	 * @param password the user's password (may not be {@code null})
	 * @return the object representing the logged in user
	 * @throws LoginException if the user identifier and/or the password was given incorrectly
	 */
	void authenticate(String userId, String password) throws LoginException;

	/**Returns with a map of all connected users, keyed by session ID.*/
	Map<Long, String> getConnectedSessionInfo();

	/**
	 * @param userIds the ID of the users to disconnect
	 * @param callback the callback to invoke for each successful disconnect operation
	 * @return <code>true</code> if disconnecting succeeded, <code>false</code> otherwise
	 */
	void disconnectSessions(List<String> userIds, Consumer<RpcSession> callback);
	
	/**Sets the non-administrator user login flag. {@code true} if the login is enabled, otherwise {@code false}.*/
	void enableLogins(boolean loginEnabled);

	/**Returns {@code true} if non-administrator login is currently allowed to the managed repositories, otherwise returns with {@code false}.*/
	boolean isLoginEnabled();

}