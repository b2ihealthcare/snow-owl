/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.identity.IdentityProvider;
import com.b2international.snowowl.core.identity.Permission;
import com.b2international.snowowl.core.identity.Role;
import com.b2international.snowowl.core.identity.User;
import com.b2international.snowowl.core.identity.Users;
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
 * </pre>
 * 
 * @since 5.11
 * @see LdapIdentityProviderConfig
 */
final class LdapIdentityProvider implements IdentityProvider {

	private enum EmptyNamingEnumeration implements NamingEnumeration<Object> {
		INSTANCE;

		@Override
		public boolean hasMore() throws NamingException {
			return hasMoreElements();
		}

		@Override
		public Object next() throws NamingException {
			return nextElement();
		}
		
		@Override
		public void close() throws NamingException {
			// Nothing to do on close
		}
		
		@Override
		public boolean hasMoreElements() {
			return false;
		}
		
		@Override
		public Object nextElement() {
			throw new NoSuchElementException();
		}
	}

	static final String TYPE = "ldap";
	private static final String LDAP_CTX_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String LDAP_CONNECTION_POOL = "com.sun.jndi.ldap.connect.pool";
	private static final Logger LOG = LoggerFactory.getLogger("ldap");
	
	// Attributes
	private static final String ATTRIBUTE_DN = "dn";
	private static final String ATTR_CN = "cn";
	
	private final LdapIdentityProviderConfig conf;
	
	public LdapIdentityProvider(LdapIdentityProviderConfig conf) {
		this.conf = conf;
		final Map<String, String> options = new TreeMap<>();
		options.put("bindDn", conf.getBindDn());
		options.put("baseDn", conf.getBaseDn());
		options.put("roleBaseDn", conf.getRoleBaseDn());
		options.put("userFilter", conf.getUserFilter());
		options.put("roleFilter", conf.getRoleFilter());
		options.put("userIdProperty", conf.getUserIdProperty());
		options.put("memberProperty", conf.getMemberProperty());
		options.put("permissionProperty", conf.getPermissionProperty());
		LOG.info("Configured LDAP identity provider with the following options: {}", options);
	}
	
	@Override
	public void validateSettings() throws Exception {
		InitialLdapContext systemContext = null;
		try {
			systemContext = createLdapContext();
			getAllLdapRoles(systemContext);
		} catch (final NamingException e) {
			throw new SnowowlRuntimeException("Check LDAP identity provider settings, one or more parameters are invalid.", e);
		} finally {
			closeLdapContext(systemContext);
		}
	}
	
	@Override
	public String getInfo() {
		return String.join("@", TYPE, conf.getUri());
	}
	
	@Override
	public User auth(String username, String token) {
		InitialLdapContext systemContext = null;
		try {
			systemContext = createLdapContext();
			final String userDN = findUserDN(systemContext, username);
			if (!Strings.isNullOrEmpty(userDN) && authenticateUser(userDN, token)) {
				return searchUsers(Collections.singleton(username), 1).getSync(1, TimeUnit.MINUTES).first().get();
			} else {
				return null;
			}
		} catch (final NamingException e) {
			throw new SnowowlRuntimeException("Cannot bind to LDAP server.", e);
		} finally {
			closeLdapContext(systemContext);
		}
	}
	
	protected String findUserDN(final DirContext context, final String username) {
		Preconditions.checkNotNull(context, "Directory context is null.");
		Preconditions.checkNotNull(username, "Username is null.");

		final String userFilterWithUsername = String.format("(&%s(%s=%s))", conf.getUserFilter(), conf.getUserIdProperty(), username);

		NamingEnumeration<SearchResult> searchResultEnumeration = null;

		try {
			searchResultEnumeration = context.search(conf.getBaseDn(), userFilterWithUsername, createSearchControls(1));
			final List<SearchResult> searchResults = ImmutableList.copyOf(Iterators.forEnumeration(searchResultEnumeration));

			if (searchResults.size() != 1) {
				return null;
			}

			return Iterables.getOnlyElement(searchResults).getNameInNamespace();
		} catch (final NamingException e) {
			LOG.error("Couldn't find user due to LDAP communication error: {}", e.getMessage(), e);
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
	public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
		final ImmutableList.Builder<User> resultBuilder = ImmutableList.builder();
		final String uidProp = conf.getUserIdProperty();
		
		InitialLdapContext context = null;
		NamingEnumeration<SearchResult> searchResultEnumeration = null;

		try {
			context = createLdapContext();
			Collection<LdapRole> ldapRoles = getAllLdapRoles(context);
			
			searchResultEnumeration = context.search(conf.getBaseDn(), conf.getUserFilter(), createSearchControls(ATTRIBUTE_DN, uidProp));
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
					.limit(limit)
					.collect(Collectors.toList());
			return Promise.immediate(new Users(users, limit, users.size()));

		} catch (final NamingException e) {
			LOG.error("Couldn't search users/roles due to LDAP communication error: {}", e.getMessage(), e);
			throw new SnowowlRuntimeException(e);
		} finally {
			closeNamingEnumeration(searchResultEnumeration);
			closeLdapContext(context);
		}
	}
	
	protected Collection<LdapRole> getAllLdapRoles(InitialLdapContext context) throws NamingException {
		NamingEnumeration<SearchResult> enumeration = null;
		try {
			final ImmutableList.Builder<LdapRole> results = ImmutableList.builder();
			final String permissionProperty = conf.getPermissionProperty();
			final String memberProperty = conf.getMemberProperty();
			
			enumeration = context.search(conf.getRoleBaseDn(), conf.getRoleFilter(), createSearchControls(ATTR_CN, permissionProperty, memberProperty));
			
			NamingEnumeration<?> permissionEnumeration = null;
			NamingEnumeration<?> uniqueMemberEnumeration = null;
			
			for (final SearchResult searchResult : ImmutableList.copyOf(Iterators.forEnumeration(enumeration))) {
				final Attributes attributes = searchResult.getAttributes();
				
				final String name = (String) attributes.get(ATTR_CN).get();
				final ImmutableList.Builder<String> uniqueMembers = ImmutableList.builder();
				final ImmutableList.Builder<Permission> permissions = ImmutableList.builder();
				
				try {
					permissionEnumeration = getNamingEnumeration(attributes, permissionProperty);
					uniqueMemberEnumeration = getNamingEnumeration(attributes, memberProperty);
					
					// process permissions
					for (final Object permission : ImmutableList.copyOf(Iterators.forEnumeration(permissionEnumeration))) {
						if ("unused".equals(permission)) {
							continue;
						}
						permissions.add(Permission.valueOf(((String) permission).trim()));
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
		env.put(Context.SECURITY_PRINCIPAL, conf.getBindDn());
		env.put(Context.SECURITY_CREDENTIALS, conf.getBindDnPassword());

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
	protected InitialLdapContext createLdapContext() throws NamingException {
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
	 * Extracts attribute values from a collection; returns an empty {@link NamingEnumeration} if the attribute is not present
	 * or has no values set (it is not possible to distinguish the two cases here unfortunately). 
	 * 
	 * @param attributes the attribute collection
	 * @param attributeId the attribute identifier
	 * @return an enumeration holding attribute values
	 * @throws NamingException - if attribute value retrieval from the LDAP server fails for some reason
	 */
	private static NamingEnumeration<?> getNamingEnumeration(
			final Attributes attributes, 
			final String attributeId) throws NamingException {
		
		final Attribute attribute = attributes.get(attributeId);
		if (attribute == null) {
			return EmptyNamingEnumeration.INSTANCE;
		} else {		
			return attribute.getAll();
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
	
	protected static class LdapRole {
		
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
