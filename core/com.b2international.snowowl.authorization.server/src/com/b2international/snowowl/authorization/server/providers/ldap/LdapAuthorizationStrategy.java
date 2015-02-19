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
package com.b2international.snowowl.authorization.server.providers.ldap;

import static com.b2international.commons.ldap.LdapConstants.ATTRIBUTE_COMMON_NAME;
import static com.b2international.commons.ldap.LdapConstants.ATTRIBUTE_PERMISSION_ID;
import static com.b2international.commons.ldap.LdapConstants.PERMISSIONS_BASE;
import static com.b2international.commons.ldap.LdapConstants.PERMISSIONS_FOR_ROLE_QUERY;
import static com.b2international.commons.ldap.LdapConstants.PERMISSION_CLASS_QUERY;
import static com.b2international.commons.ldap.LdapConstants.ROLE_FILTER_KEY;
import static com.b2international.commons.ldap.LdapConstants.ROLE_NAME_PLACEHOLDER;
import static com.b2international.commons.ldap.LdapConstants.SNOW_OWL_BASE;
import static com.b2international.commons.ldap.LdapConstants.USER_DN_PLACEHOLDER;
import static com.b2international.commons.ldap.LdapConstants.USER_FILTER_KEY;
import static com.b2international.commons.ldap.LdapConstants.USER_NAME_PLACEHOLDER;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import com.b2international.commons.ldap.LdapConstants;
import com.b2international.commons.ldap.LdapHelper;
import com.b2international.snowowl.authorization.server.providers.AbstractAuthorizationStrategy;
import com.b2international.snowowl.authorization.server.providers.IAuthorizationStrategy;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

/**
 * Java implementation of the authorization related services declared in {@link IAuthorizationStrategy}.
 *  
 */
public class LdapAuthorizationStrategy extends AbstractAuthorizationStrategy {

	@Override
	public Collection<Role> getRoles(final String userId) {

		final Map<String, Object> conf = LdapHelper.getLdapOptions();
		final String snowOwlBase = Strings.nullToEmpty((String) conf.get(SNOW_OWL_BASE));

		InitialLdapContext ldapContext = null;
		NamingEnumeration<SearchResult> userResultset = null;
		NamingEnumeration<SearchResult> rolesForUserResultset = null;

		try {

			ldapContext = LdapHelper.createLdapContext();

			final String userQueryTemplate = (String) conf.get(USER_FILTER_KEY);
			final String userQuery = userQueryTemplate.replaceAll(USER_NAME_PLACEHOLDER, userId);

			userResultset = ldapContext.search(snowOwlBase, userQuery, LdapHelper.createSearchControls(1));
			final List<SearchResult> userSearchResults = ImmutableList.copyOf(Iterators.forEnumeration(userResultset)); 

			if (userSearchResults.size() != 1) {
				return Collections.emptySet();
			}

			final SearchResult userResult = Iterables.getOnlyElement(userSearchResults);
			final String userDn = (String) userResult.getNameInNamespace();

			final String rolesForUserQueryTemplate = (String) conf.get(ROLE_FILTER_KEY);
			final String rolesForUserQuery = rolesForUserQueryTemplate.replaceAll(USER_DN_PLACEHOLDER, userDn);
			rolesForUserResultset = ldapContext.search(snowOwlBase, rolesForUserQuery, LdapHelper.createSearchControls(ATTRIBUTE_COMMON_NAME));

			final Set<Role> roles = Sets.newHashSet();
			for (final SearchResult userRoleResult : ImmutableList.copyOf(Iterators.forEnumeration(rolesForUserResultset))) {
				final String roleName = (String) userRoleResult.getAttributes().get(ATTRIBUTE_COMMON_NAME).get();
				final Role role = new Role(roleName, getPermissions(ldapContext, roleName));
				roles.add(role);
			}

			return roles;

		} catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			LdapHelper.closeNamingEnumeration(rolesForUserResultset);
			LdapHelper.closeNamingEnumeration(userResultset);
			LdapHelper.closeLdapContext(ldapContext);
		}
	}

	private Collection<Permission> getPermissions(final InitialLdapContext context, final String roleName) {

		final Map<String, Object> conf = LdapHelper.getLdapOptions();

		final String snowOwlBase = Strings.nullToEmpty((String) conf.get(SNOW_OWL_BASE));
		final String permissionBase = Strings.nullToEmpty((String) conf.get(PERMISSIONS_BASE));
		final String permissionClassQueryTemplate = (String) conf.get(PERMISSION_CLASS_QUERY);
		final String uniqueRoleQueryTemplate = (String) conf.get(PERMISSIONS_FOR_ROLE_QUERY);

		NamingEnumeration<SearchResult> uniqueRoleResults = null;

		try {

			final String uniqueRoleQuery = uniqueRoleQueryTemplate.replaceAll(ROLE_NAME_PLACEHOLDER, roleName);
			uniqueRoleResults = context.search(snowOwlBase, uniqueRoleQuery, LdapHelper.createSearchControls(ATTRIBUTE_PERMISSION_ID));

			final Set<Permission> results = Sets.newHashSet();

			for (final SearchResult uniqueRoleResult : ImmutableList.copyOf(Iterators.forEnumeration(uniqueRoleResults))) {
				registerPermissionsForRole(context, permissionBase, permissionClassQueryTemplate, uniqueRoleResult, results);
			}

			return results;

		} catch (final Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		} finally {
			LdapHelper.closeNamingEnumeration(uniqueRoleResults);
		}
	}

	private void registerPermissionsForRole(final InitialLdapContext context, 
			final String permissionBase,
			final String permissionClassQueryTemplate, 
			final SearchResult uniqueRoleResult,
			final Set<Permission> results) throws NamingException {

		NamingEnumeration<String> permissionIdsForRole = null;

		try {

			permissionIdsForRole = (NamingEnumeration<String>) uniqueRoleResult.getAttributes().get(ATTRIBUTE_PERMISSION_ID).getAll();
			for (final String permissionId : ImmutableList.copyOf(Iterators.forEnumeration(permissionIdsForRole))) {
				registerPermission(context, permissionBase, permissionClassQueryTemplate, permissionId, results);
			}

		} finally {
			LdapHelper.closeNamingEnumeration(permissionIdsForRole);
		}
	}

	private void registerPermission(
			final InitialLdapContext context, 
			final String permissionBase,
			final String permissionClassQueryTemplate, 
			final String permissionId, 
			final Set<Permission> results) throws NamingException {

		final String permissionClassQuery = permissionClassQueryTemplate.replaceAll(LdapConstants.PERMISSION_ID_PLACEHOLDER, permissionId);
		NamingEnumeration<SearchResult> permissionResult = null;

		try {

			permissionResult = context.search(permissionBase, permissionClassQuery, LdapHelper.createSearchControls(1, ATTRIBUTE_PERMISSION_ID, ATTRIBUTE_COMMON_NAME));

			for (final SearchResult permission : ImmutableList.copyOf(Iterators.forEnumeration(permissionResult))) {
				final String id = (String) permission.getAttributes().get(ATTRIBUTE_PERMISSION_ID).get();
				final String permissionName = (String) permission.getAttributes().get(ATTRIBUTE_COMMON_NAME).get();
				results.add(new Permission(id, permissionName));
			}

		} finally {
			LdapHelper.closeNamingEnumeration(permissionResult);
		}
	}
}