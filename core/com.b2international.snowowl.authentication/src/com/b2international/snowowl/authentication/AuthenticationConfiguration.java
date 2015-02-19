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
package com.b2international.snowowl.authentication;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Authentication module configuration. Use to configure authentication related
 * configuration parameter of the application.
 * <p>
 * Example:
 * <pre>
 * authentication:
 *   type: LDAP
 * </pre>
 * </p>
 * 
 * <i>TODO later FILE, LDAP, and other authentication configuration could be polymorphicly created based on type</i>
 * @since 3.4
 */
public class AuthenticationConfiguration {

	@NotEmpty
	private String type = "PROP_FILE";
	
	private boolean adminParty = false;

	@JsonProperty
	public String getType() {
		return type;
	}

	@JsonProperty
	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty
	public boolean isAdminParty() {
		return adminParty;
	}

	@JsonProperty
	public void setAdminParty(boolean adminParty) {
		this.adminParty = adminParty;
	}
	
}