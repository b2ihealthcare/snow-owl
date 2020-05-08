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

import com.b2international.commons.TokenReplacer;
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
	@NotEmpty private String roleBaseDn = "{baseDn}";
	@NotEmpty private String bindDn = "cn=admin,dc=snowowl,dc=b2international,dc=com";
	@NotEmpty private String bindDnPassword;
	@NotEmpty private String userFilter = "(objectClass={userObjectClass})";
	@NotEmpty private String roleFilter = "(objectClass={roleObjectClass})";
	
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
	
	public String getBindDn() {
		return bindDn;
	}
	
	/**
	 * @param rootDn
	 * @deprecated - replaced by {@link #setBindDn(String)}, will be removed in 8.0
	 */
	public void setRootDn(String rootDn) {
		setBindDn(rootDn);
	}
	
	public void setBindDn(String bindDn) {
		this.bindDn = bindDn;
	}
	
	public String getBindDnPassword() {
		return bindDnPassword;
	}
	
	/**
	 * @param rootDnPassword
	 * @deprecated - replaced by {@link #setBindDnPassword(String)}, will be removed in 8.0
	 */
	public void setRootDnPassword(String rootDnPassword) {
		setBindDnPassword(rootDnPassword);
	}
	
	public void setBindDnPassword(String bindDnPassword) {
		this.bindDnPassword = bindDnPassword;
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

	public String getRoleBaseDn() {
		return new TokenReplacer().register("baseDn", baseDn).substitute(roleBaseDn);
	}

	public void setRoleBaseDn(String roleBaseDn) {
		this.roleBaseDn = roleBaseDn;
	}

	public String getUserFilter() {
		return new TokenReplacer().register("userObjectClass", userObjectClass).substitute(userFilter);
	}

	public void setUserFilter(String userFilter) {
		this.userFilter = userFilter;
	}

	public String getRoleFilter() {
		return new TokenReplacer().register("roleObjectClass", roleObjectClass).substitute(roleFilter);
	}

	public void setRoleFilter(String roleFilter) {
		this.roleFilter = roleFilter;
	}
	
	
	
}
