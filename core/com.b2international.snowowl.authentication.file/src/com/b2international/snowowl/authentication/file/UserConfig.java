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
package com.b2international.snowowl.authentication.file;

import java.util.Collection;
import java.util.Collections;

import com.b2international.snowowl.core.users.Role;
import com.b2international.snowowl.core.users.User;

/**
 * Configures a user that can be authenticated into the system.
 * 
 * @since 3.7
 */
public class UserConfig {

	private String username;
	private String password;
	private Collection<Role> roles = Collections.emptySet();
	
	public String getPassword() {
		return password;
	}
	
	public Collection<Role> getRoles() {
		return roles;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRoles(Collection<Role> roles) {
		this.roles = roles;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public User toUser() {
		return new User(getUsername(), getPassword());
	}
	
}