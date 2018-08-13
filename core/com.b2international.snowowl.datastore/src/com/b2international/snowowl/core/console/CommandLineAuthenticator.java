/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.osgi.framework.console.CommandInterpreter;

import com.b2international.commons.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

/**
 * Authentication support for OSGi console.
 *
 */
public class CommandLineAuthenticator {

	private String username;

	public boolean authenticate(final CommandInterpreter interpreter) {
		
		Preconditions.checkNotNull(interpreter);
		interpreter.print("Impersonate operation as: ");
		
		username = readUsername();
		
		if (StringUtils.isEmpty(username)) {
			interpreter.println("Impersonating user name should be specified.");
			return false;
		} else {
			
			//XXX removed due to missing configuration for JAAS when using LDAP
//			if (null == ApplicationContext.getInstance().getService(IUserManager.class).getUser(username)) {
//				interpreter.println("Cannot impersonate non-existing user '" + username + "'.");
//				return false;
//			}
			
			return true;
		}
		
	}
	
	/**
	 * Returns with the user name if the authentication was successful, otherwise returns with {@code null}.
	 * @return the user ID.
	 */
	public String getUsername() {
		return username;
	}

	private String readUsername() {

		BufferedReader reader = null;
		String reply = null;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in));
			reply = reader.readLine();
		} catch (final IOException e) {
			System.out.println("Error while reading username.");
		} finally {
			//intentionally not closed
		}
		
		return Strings.nullToEmpty(reply);
 	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Strings.nullToEmpty(username);
	}
	
}