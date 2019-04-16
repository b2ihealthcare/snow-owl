/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.internal.session;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.security.auth.login.LoginException;

import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.StringUtils;
import com.b2international.commons.encoding.RSAUtils;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.AlreadyLoggedInException;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.session.AccessToken;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Ordering;

/**
 *
 */
public class ApplicationSessionManager extends Notifier implements IApplicationSessionManager, InternalApplicationSessionManager {

	public final class LogoutListener extends LifecycleEventAdapter {

		@Override
		protected void onDeactivated(final ILifecycle lifecycle) {
			AUDIT_LOGGER.info("Received deactivation event: " + lifecycle);
			EventUtil.removeListener(lifecycle, this);

			if (!(lifecycle instanceof IChannelMultiplexer)) {
				return;
			}
			
			final RpcSession sessionToLogout = knownSessions.remove((IChannelMultiplexer) lifecycle);
			if (null == sessionToLogout) {
				AUDIT_LOGGER.info("No RPC session found to log out.");
				return;
			}

			sessionToLogout.put(KEY_IS_AUTHENTICATED, false);
			fireLogoutEvent(sessionToLogout);

			logUserLogout(sessionToLogout);
		}

		private void logUserLogout(final RpcSession sessionToLogout) {
			final String userId = getUserId(sessionToLogout);
			if (StringUtils.isEmpty(userId) || User.isSystem(userId)) {
				return;
			}
				
			logSessionClosed(sessionToLogout, userId);
				
			// Log as user access event (with "SNOWOWL_USER_ACCESS" marker)
			LogUtils.logUserAccess(AUDIT_LOGGER, userId, "Logged out.");
		}

		private void logSessionClosed(final RpcSession sessionToLogout, final String userId) {
			final Long sessionId = getSessionId(sessionToLogout);
			if (sessionId == null) {
				return;
			}
			
			// Log as a user event (with "SNOWOWL" marker)
			LogUtils.logUserEvent(AUDIT_LOGGER, userId, "Session closed: " + sessionId);
		}
	}

	/** Logger for the application log */
	private static final Logger LOG = LoggerFactory.getLogger("auth");
	/** Logger for the user audit event log */
	private static final Logger AUDIT_LOGGER = LoggerFactory.getLogger(ApplicationSessionManager.class);
	private static final int RANDOM_BYTES_LENGTH = 64;
	private static final AtomicLong ID_PROVIDER = new AtomicLong(1L);

	private final ConcurrentMap<IChannelMultiplexer, RpcSession> knownSessions = new MapMaker().makeMap();
	private final SecureRandom secureRandom = new SecureRandom();
	private final IdentityProvider identityProvider;
	private volatile boolean loginEnabled = true;

	public ApplicationSessionManager(final IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}
	
	@Override
	public AccessToken requestToken(final String userId, final PublicKey clientPublicKey) {

		final byte[] randomBytes = generateRandomBytes(RANDOM_BYTES_LENGTH);
		final KeyPair serverKeyPair = RSAUtils.generateRSAKeyPair();

		final RpcSession currentSession = RpcThreadLocal.getSession();
		currentSession.put(KEY_USER_ID, userId);
		currentSession.put(KEY_SESSION_ID, ID_PROVIDER.getAndIncrement());
		currentSession.put(KEY_USER_ROLES, Collections.singletonList(Role.UNSPECIFIED));
		currentSession.put(KEY_RANDOM_BYTES, randomBytes);
		currentSession.put(KEY_SERVER_PRIVATE_KEY, serverKeyPair.getPrivate());
		currentSession.put(KEY_IS_AUTHENTICATED, false);

		byte[] encryptedBytes;

		try {
			encryptedBytes = RSAUtils.rsaEncrypt(randomBytes, clientPublicKey);
		} catch (final Exception e) {
			throw new SecurityException();
		}

		final AccessToken result = new AccessToken(serverKeyPair.getPublic(), encryptedBytes);
		return result;
	}

	private byte[] generateRandomBytes(final int size) {

		final byte[] randomBytes = new byte[size];
		secureRandom.nextBytes(randomBytes);
		return randomBytes;
	}

	@Override
	public Map<Long,String> getConnectedSessionInfo() {

		final Map<Long, String> connectedSessionInfo = ImmutableList.copyOf(knownSessions.values())
			.stream()
			.filter(s -> {
				final String userId = getUserId(s);
				final Long sessionId = getSessionId(s);

				return !StringUtils.isEmpty(userId)
						&& !User.isSystem(userId)
						&& sessionId != null;
			})
			.sorted(Ordering.natural().onResultOf(this::getSessionId))
			.collect(Collectors.toMap(
					this::getSessionId, 
					this::getUserId, 
					(s1, s2) -> { throw new IllegalStateException("Session ID collision"); },
					LinkedHashMap::new));

		return connectedSessionInfo;
	}

	@Override
	public User loginWithResponse(final byte[] response) throws SecurityException {

		final RpcSession currentSession = RpcThreadLocal.getSession();
		final byte[] randomBytes = (byte[]) currentSession.get(KEY_RANDOM_BYTES);
		final PrivateKey serverPrivateKey = (PrivateKey) currentSession.get(KEY_SERVER_PRIVATE_KEY);
		final String userId = getUserId(currentSession);
		final Long sessionId = getSessionId(currentSession);

		if (null != getSession(userId)) {
			throw new AlreadyLoggedInException();
		}

		try {

			final byte[] decryptedResponse = RSAUtils.rsaDecrypt(response, serverPrivateKey);
			final byte[] responseRandomBytes = Arrays.copyOf(decryptedResponse, RANDOM_BYTES_LENGTH);

			if (!Arrays.equals(randomBytes, responseRandomBytes)) {
				throw new SecurityException("Failed to log in.");
			}

			final String password = new String(decryptedResponse, RANDOM_BYTES_LENGTH, decryptedResponse.length - RANDOM_BYTES_LENGTH, Charsets.UTF_8);
			authenticate(userId, password);
			
			final User user = identityProvider.searchUsers(ImmutableList.of(userId), 1)
					.getSync(1, TimeUnit.MINUTES)
					.first()
					.get();
			
			if (!loginEnabled && !user.isAdministrator()) {
				throw new SecurityException("Logging in for non-administrator users is temporarily disabled.");
			}

			currentSession.remove(KEY_RANDOM_BYTES);
			currentSession.remove(KEY_SERVER_PRIVATE_KEY);
			currentSession.put(KEY_USER_ROLES, user.getRoles());

			acceptSession(currentSession, currentSession.getProtocol().getChannel().getMultiplexer());

			LogUtils.logUserEvent(AUDIT_LOGGER, userId, "Session created: "+ sessionId);

			return user;
		} catch (final Exception e) {
			throw new SecurityException(e);
		}
	}

	@Override
	public void authenticate(final String username, final String password) throws LoginException {
		LogUtils.logUserAccess(LOG, username, "Authenticating: " + username);
		
		final boolean success = identityProvider.auth(username, password);
		if (!success) {
			throw new LoginException("Incorrect user name or password.");
		}
		
		LogUtils.logUserAccess(LOG, username, "Authentication succeeded");
	}

	@Override
	public void connectSystemUser() {

		final RpcSession currentSession = RpcThreadLocal.getSession();
		final IChannelMultiplexer currentMultiplexer = currentSession.getProtocol().getChannel().getMultiplexer();

		final IJVMAcceptor jvmAcceptor = JVMUtil.getAcceptor(IPluginContainer.INSTANCE, Net4jUtils.NET_4_J_CONNECTOR_NAME);
		final Set<IConnector> jvmAcceptedConnectors = ImmutableSet.copyOf(jvmAcceptor.getAcceptedConnectors());

		if (jvmAcceptedConnectors.size() > 1) {
			throw new IllegalStateException("Too many local JVM connections.");
		}

		if (!jvmAcceptedConnectors.contains(currentMultiplexer)) {
			throw new IllegalStateException("Current RPC session is not on the only local JVM connection.");
		}

		currentSession.put(KEY_USER_ROLES, User.SYSTEM.getRoles());
		currentSession.put(KEY_USER_ID, User.SYSTEM.getUsername());
		acceptSession(currentSession, currentMultiplexer);
	}

	private void acceptSession(final RpcSession currentSession, final IChannelMultiplexer multiplexer) {

		EventUtil.addUniqueListener(multiplexer, new LogoutListener());
		knownSessions.put(multiplexer, currentSession);
		currentSession.put(KEY_IS_AUTHENTICATED, true);

		fireLoginEvent(currentSession);
	}

	@Override
	public void disconnectSessions(final List<String> userIds, final Consumer<RpcSession> callback) {
		checkNotNull(userIds, "User ID list cannot be null.");
		checkState(!userIds.isEmpty(), "User ID list cannot be empty.");
		checkNotNull(callback, "Callback cannot be null.");
		
		final Predicate<String> userIdPredicate;
		
		if (userIds.size() == 1 && Iterables.getOnlyElement(userIds).equals("ALL")) {
			userIdPredicate = userId -> true;
		} else {
			final Set<String> userIdsAsSet = ImmutableSet.copyOf(userIds);
			userIdPredicate = userIdsAsSet::contains;
		}
		
		ImmutableList.copyOf(knownSessions.entrySet())
			.stream()
			.filter(entry -> {
					final String userId = getUserId(entry.getValue());
					return !StringUtils.isEmpty(userId) 
							&& !User.isSystem(userId)
							&& userIdPredicate.test(userId);
			})
			.forEachOrdered(entry -> {
				final Exception e = LifecycleUtil.deactivate(entry.getKey());
				if (e == null) {
					callback.accept(entry.getValue());
				}
			});
	}
	
	/**
	 * (non-API, for server-side use only)
	 *
	 * @param multiplexer
	 * @return
	 */
	public RpcSession getSession(final IChannelMultiplexer multiplexer) {
		return knownSessions.get(multiplexer);
	}

	/*returns with the RPC session which user ID key equals with the argument. may return with null if the session cannot be found.*/
	private RpcSession getSession(final String userId) {

		return ImmutableList.copyOf(knownSessions.values())
			.stream()
			.filter(s -> Objects.equals(getUserId(s), userId))
			.findFirst()
			.orElse(null);
	}

	private String getUserId(final RpcSession session) {
		return (String) session.get(KEY_USER_ID);
	}

	private Long getSessionId(final RpcSession session) {
		return (Long) session.get(KEY_SESSION_ID);
	}

	private void fireLoginEvent(final RpcSession session) {
		fireEvent(new LoginEvent(this, session));
	}

	private void fireLogoutEvent(final RpcSession session) {
		fireEvent(new LogoutEvent(this, session));
	}

	@Override
	public void enableLogins(final boolean loginEnabled) {
		this.loginEnabled = loginEnabled;
	}

	@Override
	public boolean isLoginEnabled() {
		return loginEnabled;
	}
}
