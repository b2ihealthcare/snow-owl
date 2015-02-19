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
package com.b2international.snowowl.core.users;

import java.io.Serializable;

import com.b2international.commons.StringUtils;
import com.google.common.base.Preconditions;

/**
 * Stores user credentials, which includes the user's name, the password and the user's roles.
 * 
 */
public class User implements Serializable {
	
	private static final long serialVersionUID = 4244266575286614086L;
	
		
	private final String userName;
	private String password;

	public User(final String userName, final String password) {
		this.userName = userName;
		this.password = password;
		
		Preconditions.checkNotNull(this.userName, "Username cannot be null.");
		Preconditions.checkNotNull(this.password, "Password cannot be null.");
		Preconditions.checkArgument(!StringUtils.isEmpty(this.userName), "Username cannot be empty.");
		Preconditions.checkArgument(!StringUtils.isEmpty(this.password), "Password cannot be empty for user for ID '" + userName + "'.");
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return userName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	
	
}