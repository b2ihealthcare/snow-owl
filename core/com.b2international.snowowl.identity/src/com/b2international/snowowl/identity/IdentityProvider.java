/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.identity;

import java.util.Base64;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.b2international.commons.exceptions.UnauthorizedException;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.SnowowlRuntimeException;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.setup.Environment;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.domain.Users;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;

/**
 * @since 5.11
 */
public interface IdentityProvider {

	/**
	 * Global logger that should be used for identity provider related logging. 
	 */
	Logger LOG = LoggerFactory.getLogger("identity");
	
	/**
	 * @since 7.0 
	 */
	IdentityProvider NOOP = new IdentityProvider() {
		
		@Override
		public User auth(String authorizationToken) {
			// allow all tokens in unprotected mode
			return User.SYSTEM;
		}
		
		@Override
		public User auth(String username, String password) {
			return new User(username, ImmutableList.of(Role.ADMINISTRATOR));
		}
		
		@Override
		public Promise<Users> searchUsers(Collection<String> usernames, int limit) {
			// generate fake Users for given usernames with admin permission
			final List<User> users = usernames.stream()
					.limit(limit)
					.map(username -> new User(username, ImmutableList.of(Role.ADMINISTRATOR)))
					.collect(Collectors.toList());
			return Promise.immediate(new Users(users, limit, usernames.size()));
		}
		
		@Override
		public String getInfo() {
			return "unprotected";
		}
		
	};
	
	/**
	 * @since 5.11
	 */
	final class Factory {
		
		private static ServiceLoader<IdentityProviderFactory> FACTORIES;
		
		static {
			FACTORIES = ServiceLoader.load(IdentityProviderFactory.class, IdentityProviderFactory.class.getClassLoader());
		}
		
		private Factory() {}
		
		/**
		 * Creates a new {@link IdentityProvider} instance based on the currently available {@link IdentityProviderFactory} instances provided by the fragments of this bundle.
		 * @return
		 */
		public static List<IdentityProvider> createProviders(Environment env, Collection<IdentityProviderConfig> providerConfigurations) {
			final ImmutableList.Builder<IdentityProvider> providers = ImmutableList.builder();
			Iterator<IdentityProviderFactory> it = FACTORIES.iterator();
			while (it.hasNext()) {
				IdentityProviderFactory<IdentityProviderConfig> factory = it.next();
				Optional<IdentityProviderConfig> providerConfig = providerConfigurations.stream().filter(conf -> conf.getClass() == factory.getConfigType()).findFirst();
				if (providerConfig.isPresent()) {
					try {
						providers.add(factory.create(env, providerConfig.get()));
					} catch (Exception e) {
						throw new SnowowlRuntimeException(String.format("Couldn't initialize '%s' identity provider", factory), e);
					}
				}
			}
			return providers.build();
		}

		public static Collection<Class<? extends IdentityProviderConfig>> getAvailableConfigClasses() {
			final ImmutableList.Builder<Class<? extends IdentityProviderConfig>> configs = ImmutableList.builder();
			final Iterator<IdentityProviderFactory> it = FACTORIES.iterator();
			while (it.hasNext()) {
				configs.add(it.next().getConfigType());
			}
			return configs.build();
		}
		
	}
	
	/**
	 * Authenticates an authorization token.
	 * Supported formats are:
	 * - Basic: Base64 encoded username:password
	 * - Bearer: JWT token issued by Snow Owl
	 * 
	 * @param authorizationToken - a supported security token
	 * @return the {@link User} if the security token is valid otherwise return <code>null</code>
	 * @throws UnauthorizedException
	 */
	default User auth(String authorizationToken) {
		final String[] parts = authorizationToken.trim().split(" ");
		if (parts.length == 2) {
			switch (parts[0].toLowerCase()) {
			case "basic":
				return authBase64(parts[1]);
			case "bearer": 
				return authJWT(parts[1]);
			// treat any other authorization token as bearer token and verify as JWT
			default: 
				throw new UnauthorizedException("Incorrect authorization token");
			}
		} else if (parts.length == 1) {
			return authJWT(parts[0]);
		} else {
			throw new UnauthorizedException("Incorrect authorization token");
		}
	}

	/**
	 * Authenticates a token as JWT and returns the authenticated {@link User} object or throws an {@link UnauthorizedException}.
	 * @param token
	 * @return
	 * @throws UnauthorizedException
	 */
	default User authJWT(final String token) {
		try {
			final DecodedJWT jwt = ApplicationContext.getServiceForClass(JWTVerifier.class).verify(token);
			return JWTGenerator.toUser(jwt);
		} catch (JWTVerificationException e) {
			throw new UnauthorizedException("Incorrect authorization token"); 
		}
	}

	/**
	 * Authenticates a token as Base64 encoded user:pass String (HTTP Basic) and returns a {@link User} object or throws an {@link UnauthorizedException}.
	 * @param token
	 * @return
	 * @throws UnauthorizedException
	 */
	default User authBase64(final String token) {
		final String decoded = new String(Base64.getDecoder().decode(token), Charsets.UTF_8);
		final String[] base64Parts = decoded.split(":");
		if (base64Parts.length != 2) {
			throw new UnauthorizedException("Incorrect username or password");
		}
		return auth(base64Parts[0], base64Parts[1]);
	}
	
	/**
	 * Authenticates a username and password.
	 * @param username - a username to use for authentication
	 * @param password - the user's password to use for authentication
	 * @return an authenticated {@link User} and its {@link Role}s or <code>null</code> if the username or password is incorrect.
	 */
	User auth(String username, String password);
	
	/**
	 * Filters and return users based on the given filters. In case of no filters returns all users (paged response). 
	 * @param usernames - filter by user name
	 * @param limit - paging limit to specify how many users should we read from the users collection
	 * @return
	 */
	Promise<Users> searchUsers(Collection<String> usernames, int limit);
	
	/**
	 * Returns a summary like information about this {@link IdentityProvider}, usually it contains the type and some non-sensitive configuration values
	 * @return 
	 */
	String getInfo();
	
}
