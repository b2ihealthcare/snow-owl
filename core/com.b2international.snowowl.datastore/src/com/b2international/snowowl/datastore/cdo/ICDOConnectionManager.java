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
package com.b2international.snowowl.datastore.cdo;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.signal.ISignalProtocol;
import org.eclipse.net4j.util.event.IListener;

import com.b2international.snowowl.identity.domain.User;


/**
 * Interface for managing {@link ICDOConnection connections} and their lifecycles.
 */
public interface ICDOConnectionManager extends ICDOContainer<ICDOConnection> {

	/**
	 * Returns with the client user.
	 * @return the user.
	 */
	User getUser();
	
	/**
	 * Returns with the user ID of the user who opened the underlying session.
	 * @return the user ID.
	 */
	String getUserId();
	
	/**
	 * Returns {@code true} if the underlying connection for the session is using the JVM connector, hence the session is embedded, otherwise,
	 * it returns with {@code false}.
	 * @return {@code true} if wrapped session is embedded, otherwise, {@code false}.
	 */
	boolean isEmbedded();
	
	/**
	 * Configures and opens the given signal protocol for all containers associated with all managed connections.
	 * @param protocol the protocol to open.
	 */
	void openProtocol(final ISignalProtocol<?> protocol);
	
	/**
	 * Returns with the {@link ICDOConnection connection} associated with the branch.
	 * @param branch the CDO branch.
	 * @return the {@link ICDOConnection connection}.
	 */
	ICDOConnection get(final CDOBranch branch);
	
	ICDOConnection get(final CDORevision revision);
	
	ICDOConnection get(final CDOView view);

	IConnector getConnector();
	
	void subscribeForRemoteMessages(final IListener listener);
	
	void unsbscribeFromRemoteMessages(final IListener listener);

	/**
	 * @deprecated - to access the user provided password, TODO remove this method as soon as possible
	 */ 
	String getPassword();
	
}