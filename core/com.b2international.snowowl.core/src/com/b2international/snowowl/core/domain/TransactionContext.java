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
package com.b2international.snowowl.core.domain;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * @since 4.5
 */
public interface TransactionContext extends BranchContext, AutoCloseable {

	/**
	 * The author of the changes. 
	 * @return
	 */
	String userId();
	
	/**
	 * Adds the given {@link EObject} to this transaction context.
	 * 
	 * @param o
	 */
	void add(EObject o);

	/**
	 * Removes the given EObject from the transaction context and from the store as
	 * well.
	 * 
	 * @param o
	 */
	void delete(EObject o);

	/**
	 * Forcefully removes the given EObject from the transaction context and from
	 * the store as well.
	 * 
	 * @param o
	 */
	void delete(EObject o, boolean force);

	/**
	 * Prepares the commit.
	 */
	void preCommit();

	/**
	 * Commits any changes made to {@link EObject}s into the store.
	 * 
	 * @return
	 */
	long commit();
	
	/**
	 * Commits any changes made to {@link EObject}s into the store.
	 * 
	 * @param userId
	 *            - the owner of the commit
	 * @param commitComment
	 *            - a message for the commit
	 * @param parentContextDescription
	 *            - the description of the lock context already held, for nested
	 *            locking
	 * 
	 * @return - the timestamp of the successful commit
	 */
	long commit(String userId, String commitComment, String parentContextDescription);
	
	/**
	 * Returns whether the commit will notify interested services, notification services about this transaction's commit or not. It's enabled by default.
	 * @return
	 */
	boolean isNotificationEnabled();
	
	/**
	 * Enable or disable notification of other services about this commit.
	 * @param notificationEnabled
	 */
	void setNotificationEnabled(boolean notificationEnabled);

	/**
	 * Rolls back any changes the underlying transaction has since its creation.
	 */
	void rollback();

	/**
	 * Returns a persisted component from the store with the given component id and
	 * type.
	 * 
	 * @param componentId
	 * @param type
	 * @return
	 * @throws ComponentNotFoundException
	 *             - if the component cannot be found
	 */
	<T extends EObject> T lookup(String componentId, Class<T> type) throws ComponentNotFoundException;

	/**
	 * Returns a persisted component from the store with the given component id and
	 * type or <code>null</code> if does not exist.
	 * 
	 * @param componentId
	 * @param type
	 * @return
	 */
	<T extends EObject> T lookupIfExists(String componentId, Class<T> type);
	
	/**
	 * Lookup all components of the given type and ID set. The returned {@link Map} will contain all resolved objects, but won't contain any value for missing components.
	 * 
	 * @param componentIds
	 * @param type
	 * @return
	 */
	<T extends CDOObject> Map<String, T> lookup(Collection<String> componentIds, Class<T> type);

	/**
	 * Clears the entire content of the repository this context belongs to.
	 */
	void clearContents();

}
