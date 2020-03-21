/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.concurrent.TimeUnit;

import com.b2international.commons.exceptions.NotFoundException;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.identity.request.UserRequests;
import com.b2international.snowowl.eventbus.IEventBus;
import com.google.common.base.Strings;

/**
 * Authentication support for OSGi console.
 *
 */
public class CommandLineAuthenticator {

	private final IEventBus bus;
	private User user;

	public CommandLineAuthenticator(IEventBus bus) {
		this.bus = bus;
	}
	
	public boolean authenticate(final CommandLineStream out) {
		out.print("Impersonate operation as: ");
		
		final String username = readUsername();
		
		if (Strings.isNullOrEmpty(username)) {
			out.println("Impersonating user name should be specified.");
			return false;
		} else {
			try {
				user = UserRequests.prepareGet(username).buildAsync().execute(bus).getSync(1, TimeUnit.MINUTES);
				return true;
			} catch (NotFoundException e) {
				out.println("Cannot impersonate non-existing user '%s'.", username);
				return false;
			}
		}
		
	}
	
	public User getUser() {
		return user;
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

	@Override
	public String toString() {
		return user == null ? "N/A" : user.getUsername();
	}

}