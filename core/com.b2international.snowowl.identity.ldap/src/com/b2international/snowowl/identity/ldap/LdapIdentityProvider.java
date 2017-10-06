/*
 * Copyright 2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.domain.Permission;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.domain.Users;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

/**
 * IdentityProvider implementation, which uses an LDAP directory for authenticating users and determining their roles.
 * <p>
 * Authentication is based on a clear-text password (LDAP simple authentication mechanism).
 * </p>
 * <p>
 * This implementation does not support SSL connections to the LDAP server yet. 
 * 
 * TODO: add SSL support
 * You can enable SSL by using <code>ldaps://</code> as protocol in the {@link LdapIdentityProviderConfig}. 
 * Using SSL requires the LDAP server to have a trusted certificate. This can be a certificate issued by a well known Certification Authority, or a
 * certificate that is manually added to the local KeyStore. See this blog post for details on how to do the latter: <a
 * href="http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target">
 * http://nodsw.com/blog/leeland/2006/12/06-no-more-unable-find-valid-certification-path-requested-target </a>
 * </p>
 * <h3>Sample configuration node in snowowl_config.yml</h3>
 * <pre>
 * identity:
 *   providers:
 *     - ldap:
 *         uri: ldap://localhost:10389
 *         usePool: false
 *         baseDn: dc=snowowl,dc=b2international,dc=com
 *         rootDn: cn=admin,dc=snowowl,dc=b2international,dc=com
 *         rootDnPassword: adminpwd
 *         userIdProperty: uid
 * </pre>
 * 
 * @since 5.11
 * @see LdapIdentityProviderConfig
 * @see LdapIdentityProviderFactory
 */
final class LdapIdentityProvider implements IdentityProvider {

	static final String TYPE = "ldap";
	private static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String LDAP_CONNECTION_POOL = "com.sun.jndi.ldap.connect.pool";
	
	// Queries
	private static final String ALL_USER_QUERY = "(objectClass=inetOrgPerson)";
	private static final String ALL_ROLE = "(objectClass=role)";
	private static final String USER_FILTER = "(&(objectClass=inetOrgPerson)({uid}={userName}))";
	
	// Query variable substitution
	private static final String USER_NAME_PLACEHOLDER = "\\{userName\\}";
	private static final String UID_PLACEHOLDER = "\\{uid\\}";

	// Attributes
	private static final String ATTRIBUTE_DN = "dn";
	private static final String ATTR_CN = "cn";
	private static final String ATTR_PERMISSION_ID = "permissionId";
	private static final String ATTR_UNIQUE_MEMBER = "uniqueMember";
	
	private final LdapIdentityProviderConfig conf;
	
	public LdapIdentityProvider(LdapIdentityProviderConfig conf) {
		this.conf = conf;
	}
	
	@Override
	public String getInfo() {
		return String.format("%s@%s", TYPE, conf.getUri());
	}
	
	@Override
	public boolean auth(String username, String token) {
		InitialLdapContext systemContext = null;
		try {
			systemContext = createLdapContext();
			final String userDN = findUserDN(systemContext, username);
			return !Strings.isNullOrEmpty(userDN) && authenticateUser(userDN, token);
		} catch (final NamingException e) {
			throw new SnowowlRuntimeException("Cannot bind to LDAP server.", e);
		} finally {
			closeLdapContext(systemContext);
		}
	}
	
	private String findUserDN(final DirContext context, final String username) {
		Preconditions.checkNotNull(context, "Directory context is null.");
		Preconditions.checkNotNull(username, "Username is null.");

		final String userFilterWithUsername = USER_FILTER.replaceAll(UID_PLACEHOLDER, conf.getUserIdProperty()).replaceAll(USER_NAME_PLACEHOLDER, username);

		NamingEnumeration<SearchResult> searchResultEnumeration = null;

		try {
			searchResultEnumeration = context.search(conf.getBaseDn(), userFilterWithUsername, createSearchControls(1));
			final List<SearchResult> searchResults = ImmutableList.copyOf(Iterators.forEnumeration(searchResultEnumeration));

			if (searchResults.size() != 1) {
				return null;
			}

			final SearchResult searchResult = Iterables.getOnlyElement(searchResults);
			return searchResult.getNameInNamespace();
		} catch (final NamingException e) {
			return null;
		} finally {
			closeNamingEnumeration(searchResultEnumeration);
		}
	}
	
	private boolean authenticateUser(final String userDN, final String token) {
		InitialLdapContext userContext = null;
		try {
			userContext = createLdapContext(userDN, token);
			return true;
		} catch (final NamingException e) {
			return false;
		} finally {
			closeLdapContext(userContext);
		}
	}

	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int offset, int limit) {
		final ImmutableList.Builder<User> resultBuilder = ImmutableList.builder();

		final String baseDn = conf.getBaseDn();
		final String uidProp = conf.getUserIdProperty();
		
		InitialLdapContext context = null;
		NamingEnumeration<SearchResult> searchResultEnumeration = null;

		try {
			context = createLdapContext();
			Collection<LdapRole> ldapRoles = getAllLdapRoles(context, baseDn);
			
			
			searchResultEnumeration = context.search(baseDn, ALL_USER_QUERY, createSearchControls(ATTRIBUTE_DN, uidProp));
			for (final SearchResult searchResult : ImmutableList.copyOf(Iterators.forEnumeration(searchResultEnumeration))) {
				final Attributes attributes = searchResult.getAttributes();

				if (hasAttribute(attributes, uidProp)) {
					final String userName = (String) attributes.get(uidProp).get();
					final List<Role> userRoles = ldapRoles.stream()
							.filter(role -> role.getUniqueMembers().contains(searchResult.getNameInNamespace()))
							.map(role -> new Role(role.getName(), role.getPermissions()))
							.collect(Collectors.toList());
					
					resultBuilder.add(new User(userName, userRoles));
				}
			}

			final List<User> users = resultBuilder.build().stream()
					.sorted((u1, u2) -> u1.getUsername().compareTo(u2.getUsername()))
					.filter(user -> usernames.isEmpty() || usernames.contains(user.getUsername()))
					.skip(offset)
					.limit(limit)
					.collect(Collectors.toList());
			return Promise.immediate(new Users(users, offset, limit, users.size()));

		} catch (final NamingException e) {
			throw new SnowowlRuntimeException(e);
		} finally {
			closeNamingEnumeration(searchResultEnumeration);
			closeLdapContext(context);
		}
	}
	
	private Collection<LdapRole> getAllLdapRoles(InitialLdapContext context, String baseDn) throws NamingException {
		NamingEnumeration<SearchResult> enumeration = null;
		try {
			final ImmutableList.Builder<LdapRole> results = ImmutableList.builder();
			enumeration = context.search(baseDn, ALL_ROLE, createSearchControls(ATTR_CN, ATTR_PERMISSION_ID, ATTR_UNIQUE_MEMBER));
			
			NamingEnumeration<?> permissionEnumeration = null;
			NamingEnumeration<?> uniqueMemberEnumeration = null;
			
			for (final SearchResult searchResult : ImmutableList.copyOf(Iterators.forEnumeration(enumeration))) {
				final Attributes attributes = searchResult.getAttributes();
				
				final String name = (String) attributes.get(ATTR_CN).get();
				final ImmutableList.Builder<String> uniqueMembers = ImmutableList.builder();
				final ImmutableList.Builder<Permission> permissions = ImmutableList.builder();
				
				try {
					permissionEnumeration = attributes.get(ATTR_PERMISSION_ID).getAll();
					uniqueMemberEnumeration = attributes.get(ATTR_UNIQUE_MEMBER).getAll();
					
					// process permissions
					for (final Object permission : ImmutableList.copyOf(Iterators.forEnumeration(permissionEnumeration))) {
						permissions.add(new Permission((String) permission));
					}
					
					// process members
					for (final Object member : ImmutableList.copyOf(Iterators.forEnumeration(uniqueMemberEnumeration))) {
						uniqueMembers.add((String) member);
					}
					
				} finally {
					closeNamingEnumeration(permissionEnumeration);
					closeNamingEnumeration(uniqueMemberEnumeration);
				}
				
				results.add(new LdapRole(name, permissions.build(), uniqueMembers.build()));
			}
			return results.build();
		} finally {
			closeNamingEnumeration(enumeration);
		}
	}
	
	/**
	 * Sets up a {@link Hashtable}, needed for {@link InitialLdapContext} creation.
	 * 
	 * @param options the map of LDAP-specific configuration properties (may not be {@code null})
	 * @return a Hashtable of properties in a format required by InitialLdapContext's constructor
	 */
	private Hashtable<String, Object> createLdapEnvironment() {
		final Hashtable<String, Object> env = new Hashtable<String, Object>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);
		env.put(Context.PROVIDER_URL, conf.getUri()); 

		if (conf.getUri().startsWith("ldaps://")) {
			env.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		env.put(LDAP_CONNECTION_POOL, Boolean.toString(conf.isConnectionPoolEnabled()));
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, conf.getRootDn());
		env.put(Context.SECURITY_CREDENTIALS, conf.getRootDnPassword());

		return env;
	}

	private InitialLdapContext createLdapContext(final Hashtable<String, Object> environment) throws NamingException {
		return new InitialLdapContext(environment, null);
	}

	/**
	 * Creates and returns an {@link InitialLdapContext} instance with the default credentials.
	 * 
	 * @return the created LDAP context
	 * @throws NamingException 
	 */
	private InitialLdapContext createLdapContext() throws NamingException {
		return createLdapContext(createLdapEnvironment());
	}

	/**
	 * Creates and returns an {@link InitialLdapContext} instance with the specified user name and password.
	 * 
	 * @return the created LDAP context
	 * @throws NamingException 
	 */
	private InitialLdapContext createLdapContext(final String userName, final String password) throws NamingException {
		checkNotNull(userName, "User name may not be null.");
		checkNotNull(password, "Password may not be null.");

		final Hashtable<String, Object> ldapEnvironment = createLdapEnvironment();
		ldapEnvironment.put(Context.SECURITY_PRINCIPAL, userName);
		ldapEnvironment.put(Context.SECURITY_CREDENTIALS, password);

		// XXX Don't use the connection pool for authentication
		ldapEnvironment.put(LDAP_CONNECTION_POOL, "false");

		return createLdapContext(ldapEnvironment);
	}

	/**
	 * Checks whether the specified attribute set has an attribute with the given identifier and a single associated
	 * value.
	 * 
	 * @param attributes the attribute set to test (may not be {@code null})
	 * @param attributeId the attribute identifier to look for (may not be {@code null})
	 * @return {@code true} if the attribute set contains exactly one value for the attribute, {@code false} otherwise
	 * (the attribute is not present or holds multiple values)
	 */
	private static boolean hasAttribute(final Attributes attributes, final String attributeId) {
		return (null != attributes.get(attributeId)) && (attributes.get(attributeId).size() == 1);
	}

	/**
	 * Closes an {@link InitialLdapContext}, catching any exceptions that are thrown in the process and wrapping them in {@link SnowowlRuntimeException}.
	 * @param context the LDAP context to close (can be {@code null})
	 */
	private static void closeLdapContext(final InitialLdapContext context) {
		if (null != context) {
			try {
				context.close();
			} catch (final NamingException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
	}

	/**
	 * Closes a {@link NamingEnumeration}, catching any exceptions that are thrown in the process and wrapping them in {@link SnowowlRuntimeException}.
	 * 
	 * @param namingEnumeration the naming enumeration to close (can be {@code null})
	 */
	private static void closeNamingEnumeration(final NamingEnumeration<?> namingEnumeration) {

		if (null != namingEnumeration) {
			try {
				namingEnumeration.close();
			} catch (final NamingException e) {
				throw new SnowowlRuntimeException(e);
			}
		}
	}

	/**
	 * Creates a new {@link SearchControls} instance with the specified attributes to return.
	 * 
	 * @param returningAttributes the attributes to return with each search result
	 * @return the configured {@link SearchControls} instance
	 */
	private static SearchControls createSearchControls(final String... returningAttributes) {
		return createSearchControls(0, returningAttributes);
	}

	/**
	 * Creates a new {@link SearchControls} instance with the specified limit and attributes to return.
	 * 
	 * @param limit the maximum number of search results to return (0 for no limit)
	 * @param returningAttributes the attributes to return with each search result
	 * @return the configured {@link SearchControls} instance
	 */
	private static SearchControls createSearchControls(final int limit, final String... returningAttributes) {
		final SearchControls searchControls = new SearchControls();

		searchControls.setReturningAttributes(returningAttributes);
		searchControls.setDerefLinkFlag(true);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(limit);

		return searchControls;
	}
	
	private static class LdapRole {
		
		private final String name;
		private final List<Permission> permissions;
		private final List<String> uniqueMembers;
		
		public LdapRole(String name, List<Permission> permissions, List<String> uniqueMembers) {
			this.name = name;
			this.permissions = permissions;
			this.uniqueMembers = uniqueMembers;
		}
		
		public String getName() {
			return name;
		}
		
		public List<Permission> getPermissions() {
			return permissions;
		}
		
		public List<String> getUniqueMembers() {
			return uniqueMembers;
		}
		
	}

}
