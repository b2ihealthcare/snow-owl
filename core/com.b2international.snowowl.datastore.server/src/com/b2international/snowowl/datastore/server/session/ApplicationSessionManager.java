/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.session;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import javax.security.auth.login.LoginException;

import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.jvm.IJVMAcceptor;
import org.eclipse.net4j.jvm.JVMUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.Notifier;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.commons.StringUtils;
import com.b2international.commons.encoding.RSAUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.LogUtils;
import com.b2international.snowowl.core.api.AlreadyLoggedInException;
import com.b2international.snowowl.datastore.net4j.Net4jUtils;
import com.b2international.snowowl.datastore.server.InternalApplicationSessionManager;
import com.b2international.snowowl.datastore.session.AccessToken;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.identity.IdentityProvider;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.identity.domain.User;
import com.b2international.snowowl.identity.request.UserRequests;
import com.b2international.snowowl.rpc.RpcSession;
import com.b2international.snowowl.rpc.RpcThreadLocal;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Sets;

/**
 *
 */
public class ApplicationSessionManager extends Notifier implements IApplicationSessionManager, InternalApplicationSessionManager {

	private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger("auth");
	private static final org.slf4j.Logger AUDIT_LOGGER = org.slf4j.LoggerFactory.getLogger(ApplicationSessionManager.class);
	private static final AtomicLong ID_PROVIDER = new AtomicLong(1L);

	private volatile boolean loginEnabled = true;

	public final class LogoutListener extends LifecycleEventAdapter {

		/*
		 * (non-Javadoc)
		 * @see org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter#onDeactivated(org.eclipse.net4j.util.lifecycle.ILifecycle)
		 */
		@Override
		protected void onDeactivated(final ILifecycle lifecycle) {
			LOGGER.info("Received deactivation event: " + lifecycle);
			EventUtil.removeListener(lifecycle, this);

			final RpcSession sessionToLogout = knownSessions.remove(lifecycle);

			if (null == sessionToLogout) {
				LOGGER.info("No RPC session found to log out.");
				return;
			}

			if (sessionToLogout.containsKey(KEY_USER_ID) && sessionToLogout.containsKey(KEY_SESSION_ID)) {

				final String userId = String.valueOf(sessionToLogout.get(KEY_USER_ID));
				if (!User.isSystem(userId)) {

					final String sessionId = String.valueOf(sessionToLogout.get(KEY_SESSION_ID));
					//Log as a user activity
					LogUtils.logUserEvent(AUDIT_LOGGER, userId, "Session closed: " + sessionId);

					//Log is as user access event
					LogUtils.logUserAccess(AUDIT_LOGGER, userId, "Logged out.");

				}

			}

			sessionToLogout.put(KEY_IS_AUTHENTICATED, false);
			fireLogoutEvent(sessionToLogout);
		}
	}

	private static final int RANDOM_BYTES_LENGTH = 64;

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationSessionManager.class);

	private final ConcurrentMap<IChannelMultiplexer, RpcSession> knownSessions = new MapMaker().makeMap();

	private final SecureRandom secureRandom = new SecureRandom();
	private final IdentityProvider identityProvider;

	public ApplicationSessionManager(final IdentityProvider identityProvider) {
		this.identityProvider = identityProvider;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.session.ISessionManager#requestToken(java.lang.String, java.security.PublicKey)
	 */
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

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.session.IApplicationSessionManager#getConnectedSessionInfo()
	 */
	@Override
	public Iterable<Pair<String, String>> getConnectedSessionInfo() {

		final Collection<Pair<String, String>> $ = Sets.newHashSet();

		for (final Iterator<RpcSession> itr = Iterators.unmodifiableIterator(knownSessions.values().iterator()); itr.hasNext(); /**/) {

			final RpcSession session = itr.next();
			final String userId = String.valueOf(session.get(KEY_USER_ID));
			final String sessionId = String.valueOf(session.get(KEY_SESSION_ID));

			if (!StringUtils.isEmpty(sessionId) && !StringUtils.isEmpty(userId)) {

				if (!User.isSystem(userId)) {
					$.add(Pair.of(userId, sessionId));
				}

			}

		}

		return $;
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.session.ISessionManager#authenticate(byte[])
	 */
	@Override
	public void loginWithResponse(final byte[] response) throws SecurityException {


		final RpcSession currentSession = RpcThreadLocal.getSession();
		final byte[] randomBytes = (byte[]) currentSession.get(KEY_RANDOM_BYTES);
		final PrivateKey serverPrivateKey = (PrivateKey) currentSession.get(KEY_SERVER_PRIVATE_KEY);
		final String userId = (String) currentSession.get(KEY_USER_ID);
		final String sessionId = String.valueOf(currentSession.get(KEY_SESSION_ID));

		if (null != getByUserId(userId)) {
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
			
			User user = UserRequests.prepareGet(userId).buildAsync().execute(ApplicationContext.getServiceForClass(IEventBus.class)).getSync();
			
			if (!loginEnabled && !user.isAdministrator()) {
				throw new SecurityException("Logging in for non-administrator users is temporarily disabled.");
			}

			currentSession.remove(KEY_RANDOM_BYTES);
			currentSession.remove(KEY_SERVER_PRIVATE_KEY);
			currentSession.put(KEY_USER_ROLES, user.getRoles());

			acceptSession(currentSession, currentSession.getProtocol().getChannel().getMultiplexer());

			LogUtils.logUserEvent(AUDIT_LOGGER, userId, "Session created: "+ sessionId);

		} catch (final Exception e) {
			throw new SecurityException(e);
		}
	}

	@Override
	public void authenticate(final String username, final String password) throws LoginException {
		LogUtils.logUserAccess(LOG, username, "Authenticating: " + username);
		boolean success = this.identityProvider.auth(username, password);
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

	/**
	 * (non-API)
	 *
	 * @param session
	 * @return
	 */
	public List<Role> getRoles(final ISession session) {
		final RpcSession rpcSession = getSession(Preconditions.checkNotNull(session, "Server-side session argument cannot be null."));
		return getRoles(rpcSession);
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

	/**
	 * (non-API)
	 *
	 * Returns with the {@link RpcSession RPC session} for the given server-side view.
	 *
	 * @param view the server-side representation of a client-side's {@link CDOView CDO view}.
	 * @return the RPC session for the server-side view, or {@code null} if the RPC session cannot be resolved for the give view.
	 */
	public RpcSession getSession(final IView view) {
		return getSession(view.getSession());
	}

	/**
	 * (non-API)
	 *
	 * Returns with the {@link RpcSession RPC session} for the given server-side session.
	 *
	 * @param session the server-side representation of a client-side's {@link CDOSession CDO session}.
	 * @return the RPC session for the server-side session, or {@code null} if the RPC session cannot be resolved for the give session.
	 */
	@SuppressWarnings("restriction")
	public RpcSession getSession(final ISession session) {
		if (session != null) {
			final ISessionProtocol protocol = session.getProtocol();
			
			if (protocol instanceof org.eclipse.emf.cdo.server.internal.net4j.protocol.CDOServerProtocol) {
				
				final IChannel channel = ((org.eclipse.emf.cdo.server.internal.net4j.protocol.CDOServerProtocol) protocol).getChannel();
				final IChannelMultiplexer multiplexer = channel.getMultiplexer();
				return getSession(multiplexer);
				
			}
		}
		return null;
	}

	/*returns with the RPC session which user ID key equals with the argument. may return with null if the session cannot be found.*/
	private RpcSession getByUserId(final String userId) {
		Preconditions.checkNotNull(userId, "User ID argument cannot be null.");

		for (final Iterator<RpcSession> itr = Iterators.unmodifiableIterator(new CopyOnWriteArrayList<RpcSession>(knownSessions.values()).iterator()); itr.hasNext(); /* */) {

			final RpcSession rpcSession = itr.next();
			if (userId.equals(rpcSession.get(KEY_USER_ID))) {
				return rpcSession;
			}

		}

		return null;
	}

	/*returns with the roles associated with the RPC session argument.*/
	@SuppressWarnings("unchecked")
	private List<Role> getRoles(final RpcSession session) {
		return (List<Role>) Preconditions.checkNotNull(session, "RPC session argument cannot be null.").get(KEY_USER_ROLES);
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