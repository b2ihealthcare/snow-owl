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
package com.b2international.snowowl.core.login;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.annotation.Nullable;

/**
 * @since 3.1
 */
public class LoginModel {

	private PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);
	// authentication
	private String userName = "";
	private String password = "";
	private boolean rememberMe = false;
	private final LoginConfiguration authenticationConfiguration;

	public LoginModel() {
		this(null);
	}

	public LoginModel(@Nullable LoginConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
		if (authenticationConfiguration != null) {
			this.rememberMe = authenticationConfiguration.isRemember();
			if (this.rememberMe) {
				this.userName = authenticationConfiguration.getUserName();
				this.password = authenticationConfiguration.getPassword();
			}
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		changeSupport.removePropertyChangeListener(propertyName, listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		changeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		String oldUserName = this.userName;
		this.userName = userName;
		firePropertyChange("userName", oldUserName, userName);
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		String oldPassword = this.password;
		this.password = password;
		firePropertyChange("password", oldPassword, password);
	}

	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		boolean oldRememberMe = this.rememberMe;
		this.rememberMe = rememberMe;
		firePropertyChange("rememberMe", oldRememberMe, rememberMe);
	}

	public void flush() {
		authenticationConfiguration.setUserCredential(getUserName(), getPassword(), isRememberMe());
	}

}