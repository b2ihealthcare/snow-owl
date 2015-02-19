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
package com.b2international.snowowl.authentication.file;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.encoding.Base64;
import com.b2international.snowowl.core.users.IUserManager;
import com.b2international.snowowl.core.users.Permission;
import com.b2international.snowowl.core.users.Role;
import com.b2international.snowowl.core.users.User;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

/**
 * Utility class to handle user information stored in a file in the filesystem.
 * 
 */
public class FileUserManager implements IUserManager {

	private static final Logger LOG = LoggerFactory.getLogger(FileUserManager.class);
	private static final String USERS_FILE = "users";
	private static final String PERMISSIONS_FILE = "permissions";

	private Collection<User> readUsers() throws IOException {
		final File usersFile = getUsersFile();
		final DataInputStream in = new DataInputStream(new FileInputStream(usersFile));
		try {
			if (in.available() > 0) {
				final String username = new String(Base64.decode(in.readUTF()));
				in.readUTF(); // consuming the '=' char.
				final String pw = new String(Base64.decode(in.readUTF()));
				return Collections.singleton(new User(username, pw));
			}
		} finally {
			in.close();
		}
		return Collections.emptySet();
	}

	private void storeUser(User user, Collection<Role> roles) throws IOException {
		final File usersFile = getUsersFile();
		clearFile(usersFile);
		
		final File permissionsFile = getPermissionsFile();
		clearFile(permissionsFile);

		try (
			final DataOutputStream usersOut = new DataOutputStream(new FileOutputStream(usersFile));
			final DataOutputStream permissionsOut = new DataOutputStream(new FileOutputStream(permissionsFile));
			) {

				final String username = user.getUserName();
				final String pw = user.getPassword();

				usersOut.writeUTF(Base64.encodeBytes(username.getBytes()));
				usersOut.writeUTF("=");
				usersOut.writeUTF(Base64.encodeBytes(pw.getBytes()));

				final int roleCount = roles.size();

				usersOut.writeInt(roleCount);

				//append role to the end of users file
				for (final Role role : roles) {
					usersOut.writeUTF(Base64.encodeBytes(role.getName().getBytes()));
				}
				
				permissionsOut.writeUTF(Base64.encodeBytes(user.getUserName().getBytes()));
				permissionsOut.writeInt(roleCount);
				for (final Role role : roles) {
					permissionsOut.writeUTF(Base64.encodeBytes(role.getName().getBytes()));
					permissionsOut.writeUTF("=");
					Collection<Permission> permissions = role.getPermissions();
					permissionsOut.writeInt(permissions.size());
					for (Permission permission : permissions) {
						permissionsOut.writeUTF(Base64.encodeBytes(permission.getId().getBytes()));
					}
				}
				
				
		} 
	}

	private static void clearFile(final File file) throws FileNotFoundException, IOException {
		final FileOutputStream fos = new FileOutputStream(file);
		fos.write(new String().getBytes());
		fos.close();
	}

	private File usersFile;
	private File permissionsFile;
	private Collection<User> knownUsers;

	public FileUserManager(File configDirectory, FileAuthConfig config) throws Exception {
		checkNotNull(configDirectory, "configDirectory");
		this.usersFile = new File(configDirectory, USERS_FILE);
		if (this.usersFile.createNewFile()) {
			LOG.info("Created new users file at " + usersFile);
		}
		this.permissionsFile = new File(configDirectory, PERMISSIONS_FILE);
		if (this.permissionsFile.createNewFile()) {
			LOG.info("Created new permissions file at " + permissionsFile);
		}
		this.knownUsers = newHashSet(readUsers());
		// add all users from the custom configuration
		final Collection<User> customUsers = Collections2.transform(config.getUsers(), new Function<UserConfig, User>() {
			@Override
			public User apply(UserConfig input) {
				return input.toUser();
			}
		});
		for (User user : customUsers) {
			LOG.info("Adding custom user {}", user);
			this.knownUsers.add(user);
		}
	}
	
	private File getUsersFile() {
		return usersFile;
	}
	
	private File getPermissionsFile() {
		return permissionsFile;
	}

	@Override
	public void addUser(User user, Collection<Role> roles) {
		knownUsers.add(user);
		try {
			storeUser(user, roles);
		} catch (final IOException e) {
			throw new RuntimeException("Error while adding new user: " + user.getUserName(), e);
		}
	}

	@Override
	public Set<User> getUsers() {
		return ImmutableSet.copyOf(knownUsers);
	}

	@Override
	public boolean removeUser(final String username) {
		throw new UnsupportedOperationException("File based user removal is not supported");
	}

	@Override
	public User getUser(final String username) {
		for (User user : getUsers()) {
			if (user.getUserName().equals(username)) {
				return user;
			}
		}
		return null;
	}
}