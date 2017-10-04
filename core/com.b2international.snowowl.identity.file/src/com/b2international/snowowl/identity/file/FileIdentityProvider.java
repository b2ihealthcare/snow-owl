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
package com.b2international.snowowl.identity.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;

import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.exceptions.AlreadyExistsException;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.domain.Users;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.11
 */
final class FileIdentityProvider implements IdentityProvider {

	private static final String COLON = ":";
	private static final Splitter COLON_SPLITTER = Splitter.on(COLON);
	private static final Joiner COLON_JOINER = Joiner.on(COLON);
	
	private final Map<String, FileUser> users;
	private final Path usersFile;
	
	public FileIdentityProvider(Path usersFile) throws IOException {
		final File file = usersFile.toFile();
		if (!file.exists()) {
			file.createNewFile();
		}
		this.usersFile = usersFile;
		this.users = readUsers(this.usersFile);
	}

	@Override
	public boolean auth(String username, String token) {
		final FileUser user = getFileUser(username);
		return user != null && BCrypt.checkpw(token, user.getHashedPassword());
	}

	@Override
	public void addUser(String username, String password) {
		if (users.containsKey(username)) {
			throw new AlreadyExistsException(User.class.getSimpleName(), username);
		}
		final FileUser newUser = new FileUser(username, BCrypt.hashpw(password, BCrypt.gensalt()));
		users.put(username, newUser);
		// persist map to file
		writeUsers(usersFile, users);
	}
	
	@Override
	public Promise<Users> searchUsers(Collection<String> usernames, int offset, int limit) {
		final List<User> matches = users.values().stream()
			.filter(user -> usernames.isEmpty() || usernames.contains(user.getUsername())) // match users by user name
			.sorted()
			.skip(offset)
			.limit(limit)
			.map(user -> new User(user.getUsername(), Collections.emptyList()))
			.collect(Collectors.toList());
		return Promise.immediate(new Users(matches, offset, limit, users.size()));
	}
	
	private FileUser getFileUser(String username) {
		return users.get(username);
	}
	
	private static void writeUsers(Path file, Map<String, FileUser> users) {
		try {
			Files.write(file, toLines(users));
		} catch (IOException e) {
			throw new SnowowlRuntimeException("Couldn't sync users file " + file, e);
		}
	}
	
	private static List<String> toLines(Map<String, FileUser> users) {
		return users.values().stream()
				.map(user -> ImmutableList.of(user.getUsername(), user.getHashedPassword()))
				.map(COLON_JOINER::join)
				.collect(Collectors.toList());
	}

	private static Map<String, FileUser> readUsers(Path file) throws IOException {
		return Files.lines(file, Charsets.UTF_8)
				.filter(FileIdentityProvider::checkLine)
				.map(COLON_SPLITTER::splitToList)
				.map(values -> new FileUser(values.get(0), values.get(1)))
				.collect(Collectors.toConcurrentMap(FileUser::getUsername, Function.identity()));
	}
	
	private static boolean checkLine(String line) {
		final boolean valid = line.contains(COLON);
		if (!valid) {
			IdentityProvider.LOG.warn("Skipping line '{}' due to invalid format", line);
		}
		return valid;
	}
	
	private static class FileUser implements Comparable<FileUser> {

		private final String username;
		private final String hashedPassword;

		public FileUser(String username, String hashedPassword) {
			this.username = username;
			this.hashedPassword = hashedPassword;
		}
		
		public String getUsername() {
			return username;
		}
		
		public String getHashedPassword() {
			return hashedPassword;
		}
		
		@Override
		public int compareTo(FileUser o) {
			return username.compareTo(o.username);
		}
		
	}

}
