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
package com.b2international.snowowl.authorization.server.providers.file;

import static com.google.common.collect.Sets.newHashSet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Collection;

import com.b2international.commons.encoding.Base64;
import com.b2international.snowowl.authorization.server.providers.AbstractAuthorizationStrategy;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class FileBasedAuthorizationStrategy extends AbstractAuthorizationStrategy {

	private static final String PERMISSIONS_FILE = "permissions";

	private static File getPermissionsFile() {
		return new File(SnowOwlApplication.INSTANCE.getEnviroment().getConfigDirectory(), PERMISSIONS_FILE);
	}

	private Supplier<Multimap<String,Role>> userRolesSupplier = Suppliers.memoize(new Supplier<Multimap<String,Role>>() {
		@Override
		public Multimap<String,Role> get() {
			return readRoles();
		}
	});

	private Multimap<String,Role> readRoles() {

		final File permissionsFile = getPermissionsFile();

		try (DataInputStream in = new DataInputStream(new FileInputStream(permissionsFile))) {

			if (in.available() <= 0) 
				return ImmutableMultimap.<String, Role>of();

			Multimap<String, Role> userRoles = HashMultimap.<String, Role>create();

			String userName = new String(Base64.decode(in.readUTF()));
			int numberOfRoles = in.readInt();
			for (int i = 0; i < numberOfRoles; i++) {

				String roleName = new String(Base64.decode(in.readUTF()));
				in.readUTF(); // consume the '=' 

				Collection<Permission> permissions = Sets.newHashSet();

				int numberOfPermissions = in.readInt();
				for (int j = 0; j < numberOfPermissions; j++) {
					String permissionId = new String(Base64.decode(in.readUTF()));
					permissions.add(new Permission(permissionId));
				}
				userRoles.put(userName, new Role(roleName, permissions));
			}
			return userRoles;
		} catch (Exception e) {
			return ImmutableMultimap.<String, Role>of();
		} 
	}

	@Override
	public Collection<Role> getRoles(String userId) {
		Multimap<String, Role> userRoles = userRolesSupplier.get();
		return newHashSet(userRoles.get(userId));
	}
}