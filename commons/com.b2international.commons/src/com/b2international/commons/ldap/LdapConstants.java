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
package com.b2international.commons.ldap;

/**
 * Holds configuration property constants for LDAP.
 *
 */
public abstract class LdapConstants {

	public static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";

	// ============= basic connection constants  =============
	public static final String JAAS_CONFIG_NAME_FOR_LDAP = "LDAP";

	public static final String USER_PROVIDER_KEY = "userProvider";

	public static final String BIND_USER = "bindDnUser";

	public static final String BIND_PASS = "bindDnPassword";

	public static final String USE_SSL_KEY = "useSSL";

	public static final String USE_POOL = "usePool";

	// ============= search base constants =============
	public static final String PERMISSIONS_BASE = "permissionsBase";

	public static final String SNOW_OWL_BASE = "snowOwlBase";

	// ============= query constants =============
	public static final String USER_FILTER_KEY = "userFilter";

	public static final String ALL_USER_FILTER_KEY = "allUser";

	public static final String ROLE_FILTER_KEY = "roleFilter";

	public static final String PERMISSIONS_FOR_ROLE_QUERY = "permissionsForRoleQuery";

	public static final String PERMISSION_CLASS_QUERY = "permissionClassQuery";

	// ============= place holder constants =============
	public static final String ROLE_NAME_PLACEHOLDER = "\\{roleName\\}";

	public static final String USER_NAME_PLACEHOLDER = "\\{userName\\}";

	public static final String USER_DN_PLACEHOLDER = "\\{userDn\\}";

	public static final String PERMISSION_ID_PLACEHOLDER = "\\{permissionId\\}";

	// ============= attributes =============
	public static final String ATTRIBUTE_UID = "uid";

	public static final String ATTRIBUTE_COMMON_NAME = "cn";

	public static final String ATTRIBUTE_PERMISSION_ID = "permissionId";

	public static final String ATTRIBUTE_PASSWORD = "userPassword";

	private LdapConstants() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}