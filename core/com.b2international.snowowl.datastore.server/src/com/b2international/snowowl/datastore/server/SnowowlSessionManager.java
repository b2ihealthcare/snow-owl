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
package com.b2international.snowowl.datastore.server;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.internal.server.SessionManager;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.net4j.channel.IChannelMultiplexer;
import org.eclipse.spi.net4j.Protocol;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.datastore.cdo.ImpersonatingSessionProtocol;
import com.b2international.snowowl.datastore.server.session.ApplicationSessionManager;
import com.b2international.snowowl.datastore.session.IApplicationSessionManager;
import com.b2international.snowowl.identity.domain.Role;
import com.b2international.snowowl.rpc.RpcSession;
import com.google.common.collect.Iterables;

@SuppressWarnings("restriction")
public class SnowowlSessionManager extends SessionManager {

	private final ApplicationSessionManager applicationSessionManager;

	public SnowowlSessionManager() {
		this(ApplicationContext.getInstance().getService(IApplicationSessionManager.class));
	}

	public SnowowlSessionManager(final IApplicationSessionManager applicationSessionManager) {
		this.applicationSessionManager = (ApplicationSessionManager) applicationSessionManager;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.server.SessionManager#createSession(int, java.lang.String, org.eclipse.emf.cdo.spi.server.ISessionProtocol)
	 */
	@Override
	protected InternalSession createSession(final int id, final String userID, final ISessionProtocol protocol) {

		/*
		 * Filter out our customized ISessionProtocol implementation.
		 */
		if (protocol instanceof ImpersonatingSessionProtocol) {
			//shortcut to avoid session creation logging when impersonating someone on the server
			return new ImpersonatingCDOSession(this, null, id, userID);
		}

		return super.createSession(id, userID, protocol);
	}

	/**
	 * Custom implementation to authenticate any user automatically.
	 * At this point the user must be already authenticated, so there is no need for a second CDO authentication.
	 *
	 */
	@Override
	protected String authenticateUser(final ISessionProtocol sessionProtocol) throws SecurityException {

		/*
		 * Required by Repository.initRootResource() which opens a session
		 * with a null session protocol (see also the authenticateUser method in this
		 * class' superclass)
		 */
		if (sessionProtocol == null) {
			return null;
		}

		//required for impersonated session authentication
		//especially when creating commit info
		if (sessionProtocol instanceof ImpersonatingSessionProtocol) {
			return ((ImpersonatingSessionProtocol) sessionProtocol).getUserID();
		}

		if (sessionProtocol instanceof Protocol) {

			final Protocol<?> protocol = (Protocol<?>) sessionProtocol;
			final IChannelMultiplexer multiplexer = protocol.getChannel().getMultiplexer();
			final RpcSession multiplexerSession = applicationSessionManager.getSession(multiplexer);

			if (null == multiplexerSession) {
				throw new SecurityException(MessageFormat.format("Unknown client ''{0}''.", multiplexer));
			}

			final boolean isAuthenticated = (Boolean) multiplexerSession.get(IApplicationSessionManager.KEY_IS_AUTHENTICATED);

			if (!isAuthenticated) {
				throw new NotAuthenticatedException(MessageFormat.format("Client ''{0}'' is not authenticated.", multiplexer));
			}

			final String sessionUserId = (String) multiplexerSession.get(IApplicationSessionManager.KEY_USER_ID);

			@SuppressWarnings("unchecked")
			final List<Role> userRoles = (List<Role>) multiplexerSession.get(IApplicationSessionManager.KEY_USER_ROLES);

		    if (CompareUtils.isEmpty(userRoles)) {
		    	throw new SecurityException(MessageFormat.format("No roles are associated with user for ID ''{0}''. Please contact the administrator.", sessionUserId));
		    }

			if (1 == userRoles.size() && Role.UNSPECIFIED.equals(Iterables.getOnlyElement(userRoles))) {
				throw new SecurityException(MessageFormat.format("No roles are associated with user for ID ''{0}''. Please contact the administrator.", sessionUserId));
			}

			
			// The next few steps are similar to what the regular SessionManager does
			final byte[] randomToken = createRandomToken();
			final CDOAuthenticationResult result;

			try {
				result = sessionProtocol.sendAuthenticationChallenge(randomToken);
			} catch (final Exception e) {

				final Throwable cause = e.getCause();

				if (cause instanceof SecurityException) {
					throw (SecurityException) cause;
				}

				throw new SecurityException(e);
			}

			if (null == result) {
				throw new NotAuthenticatedException("No authentication result has been received.");
			}

		    final String receivedUserId = result.getUserID();

			if (!canImpersonate(sessionUserId, receivedUserId)) {
				throw new SecurityException(MessageFormat.format("User {0} is not allowed to open a CDO session as user {1}.", sessionUserId, receivedUserId));
			}

		    return receivedUserId;
		}

		throw new SecurityException("Failed to determine user ID for session protocol.");
	}

	/**
	 * Overridden to not display the stack trace if a notification problem occurs.
	 */
	@Override
	protected void handleNotificationProblem(final InternalSession session, final Throwable t) {
		OM.LOG.warn("Could not notify session " + session + "; it is probably closed.");
	}

	private boolean canImpersonate(final String sessionUserId, final String receivedUserId) {
		// TODO: more advanced access control for user impersonation (ex.: when importing)
		return sessionUserId.equals(receivedUserId);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.cdo.internal.server.SessionManager#sendCommitNotification(org.eclipse.emf.cdo.spi.server.InternalSession, org.eclipse.emf.cdo.common.commit.CDOCommitInfo)
	 */
	@Override
	public void sendCommitNotification(final InternalSession sender, final CDOCommitInfo commitInfo) {
		for (final InternalSession session : getSessions()) {

			if (session instanceof ImpersonatingCDOSession) {
				continue;
			}

			if (session != sender) {
				try {
					session.sendCommitNotification(commitInfo);
				} catch (final Exception ex) {
					handleNotificationProblem(session, ex);
				}
			}

		}
	}

}