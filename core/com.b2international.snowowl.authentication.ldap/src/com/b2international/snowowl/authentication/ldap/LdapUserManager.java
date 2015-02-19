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

import static com.b2international.commons.ldap.LdapConstants.SNOW_OWL_BASE;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.ldap.LdapConstants;
import com.b2international.commons.ldap.LdapHelper;
import com.b2international.snowowl.core.users.IUserManager;
import com.b2international.snowowl.core.users.Role;
import com.b2international.snowowl.core.users.User;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.hash.Hashing;

/**
 * Utility class to handle user information stored in an LDAP directory.
 * 
 */
public class LdapUserManager implements IUserManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(LdapUserManager.class); 

	@Override
	public Set<User> getUsers() {

		final ImmutableSet.Builder<User> resultBuilder = ImmutableSet.builder();

		final Map<String, Object> options = LdapHelper.getLdapOptions();
		final String snowOwlBase = Strings.nullToEmpty((String) options.get(SNOW_OWL_BASE));
		final String allUsersFilter = (String) options.get(LdapConstants.ALL_USER_FILTER_KEY);

		InitialLdapContext context = null;
		NamingEnumeration<SearchResult> searchResultEnumeration = null;

		try {

			context = LdapHelper.createLdapContext();
			searchResultEnumeration = context.search(snowOwlBase, allUsersFilter, LdapHelper.createSearchControls(LdapConstants.ATTRIBUTE_UID, LdapConstants.ATTRIBUTE_PASSWORD));

			for (final SearchResult searchResult : ImmutableList.copyOf(Iterators.forEnumeration(searchResultEnumeration))) {
				final Attributes attributes = searchResult.getAttributes();

				if (LdapHelper.hasAttribute(attributes, LdapConstants.ATTRIBUTE_UID) && LdapHelper.hasAttribute(attributes, LdapConstants.ATTRIBUTE_PASSWORD)) {
					final String userName = (String) attributes.get(LdapConstants.ATTRIBUTE_UID).get();
					final byte[] password = (byte[]) attributes.get(LdapConstants.ATTRIBUTE_PASSWORD).get();
					
					// TODO: Remove password field from User; this value makes absolutely no sense, and can't be left empty either.
					resultBuilder.add(new User(userName, Hashing.sha512().hashBytes(password).toString()));
				}
			}

			return resultBuilder.build();

		} catch (final NamingException e) {
			LOGGER.error("Failed to list users from LDAP server, returning empty set.", e);
			return ImmutableSet.of();
		} finally {
			LdapHelper.closeNamingEnumeration(searchResultEnumeration);
			LdapHelper.closeLdapContext(context);
		}
	}

	@Override
	public void addUser(final User user, final Collection<Role> roles) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public boolean removeUser(final String username) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public User getUser(final String username) {
		for (final User user : getUsers()) {
			if (user.getUserName().equals(username)) {
				return user;
			}
		}
		return null;
	}
}