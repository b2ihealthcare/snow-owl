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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.CDOCommonRepository.State;
import org.eclipse.emf.cdo.common.CDOCommonRepository.Type;
import org.eclipse.emf.cdo.common.branch.CDOBranchChangedEvent.ChangeKind;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;

/**
 * An {@link ISessionProtocol} implementation for impersonating users on the server.  
 *
 */
public final class ImpersonatingSessionProtocol implements ISessionProtocol {
	
	private static final byte[] EMPTY_TOKEN = new byte[0];
	
	private final String userID;

	public ImpersonatingSessionProtocol(final String userID) {
		this.userID = userID;
	}

	@Override
	public CDOCommonSession getSession() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendRepositoryTypeNotification(final Type oldType, final Type newType) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendRepositoryStateNotification(final State oldState, final State newState, final CDOID rootResourceID) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	@Deprecated
	public void sendRepositoryStateNotification(final State oldState, final State newState) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendRemoteSessionNotification(final InternalSession sender, final byte opcode) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendRemoteMessageNotification(final InternalSession sender, final CDORemoteSessionMessage message) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendLockNotification(final CDOLockChangeInfo lockChangeInfo) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendCommitNotification(final CDOCommitInfo commitInfo) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void sendBranchNotification(final InternalCDOBranch branch) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	@Override
	public void sendBranchNotification(InternalCDOBranch branch, ChangeKind changeKind) throws Exception {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public CDOAuthenticationResult sendAuthenticationChallenge(final byte[] randomToken) throws Exception {
		return new CDOAuthenticationResult(userID, EMPTY_TOKEN);
	}
	
	/**
	 * Returns with the impersonating user's unique ID 
	 * @return the user ID.
	 */
	public String getUserID() {
		return userID;
	}
}