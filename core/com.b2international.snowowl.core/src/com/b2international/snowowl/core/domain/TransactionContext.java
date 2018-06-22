/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.ecore.EObject;

import com.b2international.index.Doc;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.domain.DelegatingContext.Builder;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * Represents an ongoing transaction to the underlying repository. The transaction can commit all aggregated changes up to a given point using the {@link #commit() commit method}. 
 * The changes can be new, changed and deleted objects. 
 * An object is basically a POJO with a {@link Doc} annotation, so the underlying repository will recognize and treat them properly during {@link #commit()}. 
 * If the given Object is an instance of {@link Revision} then it will be treated as a {@link Revision} will be persisted on the branch available via {@link #branch()}.
 * 
 * @since 4.5
 */
public interface TransactionContext extends BranchContext, AutoCloseable {

	/**
	 * The author of the changes. 
	 * @return
	 */
	String userId();
	
	/**
	 * Adds the given {@link Object} to this transaction context as a completely new object. 
	 * 
	 * @param obj - the object to persist and add to the repository
	 */
	void add(Object obj);
	
	/**
	 * @param oldVersion
	 * @param newVersion
	 */
	void update(Revision oldVersion, Revision newVersion);

	/**
	 * Removes the given Object from this TransactionContext and from the underlying repository on {@link #commit() commit}. If the deletion of the
	 * object is being prevented by a domain specific rule usually in the form of an {@link Exception}, then clients should be able to forcefully
	 * delete the object from the underlying repository via the {@link #delete(Object, boolean) force delete method}.
	 * 
	 * @param obj
	 *            - the object to delete from the context and from the repository
	 */
	void delete(Object obj);

	/**
	 * Forcefully removes the given Object from the transaction context and from underlying repository on {@link #commit() commit} even if domain specific rules would otherwise prevent the deletion from happening.
	 * 
	 * @param obj - the object to forcefully delete from the context and from the repository
	 */
	void delete(Object obj, boolean force);

	/**
	 * Commits all changes made to {@link EObject}s into the store.
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
	<T> T lookup(String componentId, Class<T> type) throws ComponentNotFoundException;

	/**
	 * Returns a persisted component from the store with the given component id and
	 * type or <code>null</code> if does not exist.
	 * 
	 * @param componentId
	 * @param type
	 * @return
	 */
	<T> T lookupIfExists(String componentId, Class<T> type);
	
	/**
	 * Lookup all components of the given type and ID set. The returned {@link Map} will contain all resolved objects, but won't contain any value for missing components.
	 * 
	 * @param componentIds
	 * @param type
	 * @return
	 */
	<T> Map<String, T> lookup(Collection<String> componentIds, Class<T> type);

	/**
	 * Clears the entire content of the repository this context belongs to.
	 */
	void clearContents();

	@Override
	default Builder<? extends TransactionContext> inject() {
		return new DelegatingContext.Builder<TransactionContext>(this, TransactionContext.class);
	}
	
}
