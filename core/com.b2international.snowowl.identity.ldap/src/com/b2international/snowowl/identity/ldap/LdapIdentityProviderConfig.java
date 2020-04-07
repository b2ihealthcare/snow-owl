/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity.ldap;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.core.identity.IdentityProviderConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @since 5.11
 */
@JsonTypeName("ldap")
public class LdapIdentityProviderConfig implements IdentityProviderConfig {

	@NotEmpty private String uri;
	@NotEmpty private String baseDn = "dc=snowowl,dc=b2international,dc=com";
	@NotEmpty private String rootDn = "cn=admin,dc=snowowl,dc=b2international,dc=com";
	@NotEmpty private String rootDnPassword;
	// Customizable objectClasses
	@NotEmpty private String userObjectClass = "inetOrgPerson";
	@NotEmpty private String roleObjectClass = "groupOfUniqueNames";
	
	// Customizable attributes
	@NotEmpty private String userIdProperty = "uid";
	@NotEmpty private String permissionProperty = "description";
	@NotEmpty private String memberProperty = "uniqueMember";
	
	private boolean connectionPoolEnabled = false;
	
	public String getBaseDn() {
		return baseDn;
	}
	
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	
	public String getUri() {
		return uri;
	}
	
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	public String getRootDn() {
		return rootDn;
	}
	
	public void setRootDn(String rootDn) {
		this.rootDn = rootDn;
	}
	
	public String getRootDnPassword() {
		return rootDnPassword;
	}
	
	public void setRootDnPassword(String rootDnPassword) {
		this.rootDnPassword = rootDnPassword;
	}

	public String getUserIdProperty() {
		return userIdProperty;
	}
	
	public void setUserIdProperty(String userIdProperty) {
		this.userIdProperty = userIdProperty;
	}
	
	@JsonProperty("usePool")
	public boolean isConnectionPoolEnabled() {
		return connectionPoolEnabled;
	}
	
	@JsonProperty("usePool")
	public void setConnectionPoolEnabled(boolean connectionPoolEnabled) {
		this.connectionPoolEnabled = connectionPoolEnabled;
	}
	
	public String getUserObjectClass() {
		return userObjectClass;
	}
	
	public void setUserObjectClass(String userObjectClass) {
		this.userObjectClass = userObjectClass;
	}
	
	public String getRoleObjectClass() {
		return roleObjectClass;
	}
	
	public void setRoleObjectClass(String roleObjectClass) {
		this.roleObjectClass = roleObjectClass;
	}
	
	public String getMemberProperty() {
		return memberProperty;
	}
	
	public void setMemberProperty(String memberProperty) {
		this.memberProperty = memberProperty;
	}
	
	public String getPermissionProperty() {
		return permissionProperty;
	}
	
	public void setPermissionProperty(String permissionProperty) {
		this.permissionProperty = permissionProperty;
	}
	
}
