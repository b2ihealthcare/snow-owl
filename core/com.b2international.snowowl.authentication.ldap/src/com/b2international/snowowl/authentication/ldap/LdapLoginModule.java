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
package com.b2international.snowowl.authentication.ldap;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.commons.ldap.LdapConstants;
import com.b2international.commons.ldap.LdapHelper;
import com.b2international.snowowl.authentication.AbstractLoginModule;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * JAAS login module, which uses an LDAP directory for authenticating users and determining their roles. This
 * implementation is only meant to be used inside an Equinox OSGi container, which has its internal JAAS-based
 * authentication framework. Login modules are to be registered through the
 * {@code org.eclipse.equinox.security.loginModule} extension point.
 * <p>
 * Authentication is based on a clear-text password (LDAP simple authentication mechanism).
 * </p>
 * <p>
 * This implementation does not use SSL by default because of the additional configuration involved in doing that. You
 * can enable SSL by adding {@code useSSL=true} to the JAAS configuration file. Using SSL requires the LDAP server to
 * have a trusted certificate. This can be a certificate issued by a well known Certification Authority, or a
 * certificate that is manually added to the local KeyStore. See this blog post for details on how to do the latter: <a
 * href="http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target">
 * http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target </a>
 * </p>
 * <h3>Configuration options</h3>
 * <ul>
 * <li>extensionId: the ID of the {@code org.eclipse.equinox.security.loginModule} extension associated with this login
 * module.</li>
 * <li>useSSL: use SSL connections to the LDAP server, {@code true} or {@code false} (false by default).</li>
 * <li>usePool: use built-in LDAP connection pool for searching, {@code true} or {@code false} (false by default).</li>
 * <li>userProvider: the LDAP URL (<a href="http://www.ietf.org/rfc/rfc2255.txt">RFC 2255</a>) of the LDAP directory
 * that stores user and role entries.</li>
 * <li>snowOwlBase: the search base to use when searching for users and roles</li>
 * <li>bindDnUser: the connecting user's DN used for performing search</li>
 * <li>bindDnPassword: the connecting user's password used for performing search</li>
 * <li>allUser: the search filter for finding all registered users in the LDAP directory.</li>
 * <li>userFilter: the search filter for finding a user's entry in the LDAP directory. The <code>{userName}</code> token
 * will be substituted with the supplied username.</li>
 * <li>roleFilter: the search filter for finding the role entries for a given user in the LDAP directory. The
 * <code>{userDn}</code> token will be substituted with the user's distinguished name (DN).</li>
 * <li>permissionsBase: the search base to use when searching for permissions</li>
 * <li>permissionsForRoleQuery: the search filter for finding a single role in the LDAP directory by name. The
 * <code>{roleName}</code> token will be substituted with role's name.</li>
 * <li>permissionClassQuery: the search filter for finding a single permission in the LDAP directory by its identifier.
 * The <code>{permissionId}</code> token will be substituted with the identifier of the permission to look for.</li>
 * </ul>
 * <h3>Sample JAAS configuration</h3>
 * <pre>
 * LDAP {
 *  org.eclipse.equinox.security.auth.module.ExtensionLoginModule required
 *  extensionId="com.b2international.snowowl.authentication.ldapLoginModule"
 *  useSSL=false
 *  usePool=false
 *  userProvider="ldap://localhost:10389/"
 *  snowOwlBase="dc=snowowl,dc=b2international,dc=com"
 *  bindDnUser="uid=admin,ou=system"
 *  bindDnPassword="secret"
 *  allUser="(objectClass=inetOrgPerson)"
 *  userFilter="(&(objectClass=inetOrgPerson)(uid={userName}))"
 *  roleFilter="(&(objectClass=role)(uniqueMember={userDn}))"
 *  permissionsBase="dc=permissions,dc=snowowl,dc=b2international,dc=com"
 *  permissionsForRoleQuery="(&(objectClass=role)(cn={roleName}))"
 *  permissionClassQuery="(&(objectClass=permission)(permissionid={permissionId}))"
 * };
 * </pre>
 * 
 */
public class LdapLoginModule extends AbstractLoginModule {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapLoginModule.class);

	private String userFilter;
	private String snowOwlBase;

	@Override
	public void initialize(final Subject subject, final CallbackHandler callbackHandler, final Map<String, ?> sharedState, final Map<String, ?> options) {
		LOGGER.info("Initializing LDAP login module.");
		super.initialize(subject, callbackHandler, sharedState, options);
		this.userFilter = (String) checkNotNull(options.get(LdapConstants.USER_FILTER_KEY), "User filter expression is null.");
		this.snowOwlBase = Strings.nullToEmpty((String) options.get(LdapConstants.SNOW_OWL_BASE));
	}

	@Override
	protected void doLogin(final Pair<String, String> userNameAndPassword) throws LoginException {
		InitialLdapContext systemContext = null;

		try {

			systemContext = LdapHelper.createLdapContext();
			final String userDN = findUserDN(systemContext, userNameAndPassword.getA());
			authenticateUser(userNameAndPassword, userDN);

		} catch (final NamingException e) {
			throw new LoginException("Cannot bind to LDAP server.\n" + e.toString());
		} finally {
			LdapHelper.closeLdapContext(systemContext);
		}
	}

	private void authenticateUser(final Pair<String, String> userNameAndPassword, final String userDN) throws LoginException {
		InitialLdapContext userContext = null;

		try {
			userContext = LdapHelper.createLdapContext(userDN, userNameAndPassword.getB());
		} catch (final NamingException e) {
			throw new FailedLoginException("Incorrect user name or password.\n" + e.toString());
		} finally {
			LdapHelper.closeLdapContext(userContext);
		}
	}

	private String findUserDN(final DirContext context, final String username) throws LoginException {
		Preconditions.checkNotNull(context, "Directory context is null.");
		Preconditions.checkNotNull(username, "Username is null.");

		final String userFilterWithUsername = userFilter.replaceAll(LdapConstants.USER_NAME_PLACEHOLDER, username);

		NamingEnumeration<SearchResult> searchResultEnumeration = null;

		try {

			searchResultEnumeration = context.search(snowOwlBase, userFilterWithUsername, LdapHelper.createSearchControls(1));
			final List<SearchResult> searchResults = ImmutableList.copyOf(Iterators.forEnumeration(searchResultEnumeration));

			if (searchResults.size() != 1) {
				throw new FailedLoginException("Incorrect user name or password.");
			}

			final SearchResult searchResult = Iterables.getOnlyElement(searchResults);
			return searchResult.getNameInNamespace();

		} catch (final NamingException e) {
			throw new LoginException("Error when finding user DN.\n" + e.toString());
		} finally {
			LdapHelper.closeNamingEnumeration(searchResultEnumeration);
		}
	}
}