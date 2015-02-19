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

import static com.b2international.commons.ldap.LdapConstants.ALL_USER_FILTER_KEY;
import static com.b2international.commons.ldap.LdapConstants.BIND_PASS;
import static com.b2international.commons.ldap.LdapConstants.BIND_USER;
import static com.b2international.commons.ldap.LdapConstants.JAAS_CONFIG_NAME_FOR_LDAP;
import static com.b2international.commons.ldap.LdapConstants.LDAP_CTX_FACTORY;
import static com.b2international.commons.ldap.LdapConstants.ROLE_FILTER_KEY;
import static com.b2international.commons.ldap.LdapConstants.USER_FILTER_KEY;
import static com.b2international.commons.ldap.LdapConstants.USER_PROVIDER_KEY;
import static com.b2international.commons.ldap.LdapConstants.USE_POOL;
import static com.b2international.commons.ldap.LdapConstants.USE_SSL_KEY;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.InitialLdapContext;
import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * Collection of static helper methods, helping to set up LDAP operations.
 *  
 */
public abstract class LdapHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapHelper.class);

	/**
	 * Returns JAAS configuration options set in {@code snowowl_jaas_configuration.conf} for LDAP.
	 * 
	 * @return a map of LDAP-specific configuration properties  
	 */
	public static Map<String, Object> getLdapOptions() {

		final Configuration configuration = Configuration.getConfiguration();
		final AppConfigurationEntry[] configurationEntries = configuration.getAppConfigurationEntry(JAAS_CONFIG_NAME_FOR_LDAP);
		final Map<String, Object> result = Maps.newHashMap();

		if (null != configurationEntries && configurationEntries.length > 0) {
			result.putAll(configurationEntries[0].getOptions());
		}

		return result;
	}

	/**
	 * Sets up a {@link Hashtable}, needed for {@link InitialLdapContext} creation.
	 * 
	 * @param options the map of LDAP-specific configuration properties (may not be {@code null})
	 * @return a Hashtable of properties in a format required by InitialLdapContext's constructor
	 */
	private static Hashtable<String, Object> createLdapEnvironment(final Map<String, Object> options) {

		checkNotNull(options, "LDAP option map may not be null.");

		checkNotNull(options.get(USER_FILTER_KEY), "User filter expression may not be null.");
		checkNotNull(options.get(ROLE_FILTER_KEY), "Role filter expression may not be null.");
		checkNotNull(options.get(ALL_USER_FILTER_KEY), "All user filter expression is null.");

		final Hashtable<String, Object> ldapEnvironment = new Hashtable<String, Object>();

		ldapEnvironment.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CTX_FACTORY);

		final String userProvider = (String) options.get(USER_PROVIDER_KEY);
		if (userProvider != null) { 
			ldapEnvironment.put(Context.PROVIDER_URL, userProvider); 
		}

		if (Boolean.parseBoolean((String) options.get(USE_SSL_KEY))) {
			ldapEnvironment.put(Context.SECURITY_PROTOCOL, "ssl");
		}

		// Sanitize boolean value
		final boolean usePool = Boolean.parseBoolean((String) options.get(USE_POOL));
		ldapEnvironment.put("com.sun.jndi.ldap.connect.pool", Boolean.toString(usePool));

		ldapEnvironment.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnvironment.put(Context.SECURITY_PRINCIPAL, options.get(BIND_USER));
		ldapEnvironment.put(Context.SECURITY_CREDENTIALS, options.get(BIND_PASS));

		return ldapEnvironment;
	}

	private static InitialLdapContext createLdapContext(final Hashtable<String, Object> environment) throws NamingException {
		return new InitialLdapContext(environment, null);
	}

	/**
	 * Creates and returns an {@link InitialLdapContext} instance with the default credentials.
	 * 
	 * @return the created LDAP context
	 * @throws NamingException 
	 */
	public static InitialLdapContext createLdapContext() throws NamingException {
		return createLdapContext(createLdapEnvironment(getLdapOptions()));
	}

	/**
	 * Creates and returns an {@link InitialLdapContext} instance with the specified user name and password.
	 * 
	 * @return the created LDAP context
	 * @throws NamingException 
	 */
	public static InitialLdapContext createLdapContext(final String userName, final String password) throws NamingException {
		checkNotNull(userName, "User name may not be null.");
		checkNotNull(password, "Password may not be null.");

		final Hashtable<String, Object> ldapEnvironment = createLdapEnvironment(getLdapOptions());
		ldapEnvironment.put(Context.SECURITY_PRINCIPAL, userName);
		ldapEnvironment.put(Context.SECURITY_CREDENTIALS, password);

		// Don't use the connection pool for authentication
		ldapEnvironment.put("com.sun.jndi.ldap.connect.pool", "false");

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
	public static boolean hasAttribute(final Attributes attributes, final String attributeId) {
		return (null != attributes.get(attributeId)) && (attributes.get(attributeId).size() == 1);
	}

	/**
	 * Closes an {@link InitialLdapContext}, catching any exceptions that are thrown in the process.
	 * 
	 * @param context the LDAP context to close (can be {@code null})
	 */
	public static void closeLdapContext(final InitialLdapContext context) {

		if (null != context) {
			try {
				context.close();
			} catch (final NamingException e) {
				LOGGER.warn("Caught exception while closing LDAP context.", e);
			}
		}
	}

	/**
	 * Closes a {@link NamingEnumeration}, catching any exceptions that are thrown in the process.
	 * 
	 * @param namingEnumeration the naming enumeration to close (can be {@code null})
	 */
	public static void closeNamingEnumeration(final NamingEnumeration<?> namingEnumeration) {

		if (null != namingEnumeration) {
			try {
				namingEnumeration.close();
			} catch (final NamingException e) {
				LOGGER.warn("Caught exception while closing naming enumeration.", e);
			}
		}
	}

	/**
	 * Creates a new {@link SearchControls} instance with the specified attributes to return.
	 * 
	 * @param returningAttributes the attributes to return with each search result
	 * @return the configured {@link SearchControls} instance
	 */
	public static SearchControls createSearchControls(final String... returningAttributes) {
		return createSearchControls(0, returningAttributes);
	}

	/**
	 * Creates a new {@link SearchControls} instance with the specified limit and attributes to return.
	 * 
	 * @param limit the maximum number of search results to return (0 for no limit)
	 * @param returningAttributes the attributes to return with each search result
	 * @return the configured {@link SearchControls} instance
	 */
	public static SearchControls createSearchControls(final int limit, final String... returningAttributes) {
		final SearchControls searchControls = new SearchControls();

		searchControls.setReturningAttributes(returningAttributes);
		searchControls.setDerefLinkFlag(true);
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setCountLimit(limit);

		return searchControls;
	}

	private LdapHelper() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}