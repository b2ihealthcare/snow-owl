/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.index.Doc;
import com.b2international.index.revision.Revision;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.index.revision.StagingArea;
import com.b2international.snowowl.core.codesystem.CodeSystemEntry;
import com.b2international.snowowl.core.codesystem.CodeSystemVersionEntry;
import com.b2international.snowowl.core.domain.DelegatingContext.Builder;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;

/**
 * Represents an ongoing transaction to the underlying repository. The transaction can commit all aggregated changes up to a given point using the {@link #commit() commit method}. 
 * The changes can be new, changed and deleted objects. 
 * An object is basically a POJO with a {@link Doc} annotation, so the underlying repository will recognize and treat them properly during {@link #commit()}. 
 * If the given Object is an instance of {@link Revision} then it will be treated as a {@link Revision} and it will be persisted on the branch available via {@link #branch()}.
 * 
 * @since 4.5
 */
public interface TransactionContext extends BranchContext, AutoCloseable {

	/**
	 * The author of the changes. 
	 * @return
	 */
	String author();
	
	/**
	 * Adds the given {@link Object} to this transaction context as a completely new object. 
	 * 
	 * @param obj - the object to persist and add to the repository
	 * @return the identifier of the object if it is an instanceof of any of the following classes: {@link Revision} / {@link CodeSystemEntry} / {@link CodeSystemVersionEntry} or <code>null</code> in any other cases.
	 */
	String add(Object obj);
	
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
	 * Commits all changes made so far using the current userId, default commit comment and no lock context.
	 * 
	 * @return - the timestamp of the successful commit
	 */
	Long commit();
	
	/**
	 * Commits all changes made so far using the current userId, the given commit comment and no lock context.
	 * 
	 * @param commitComment - the commit comment to use for the commit
	 * @return - the timestamp of the successful commit
	 */
	Long commit(String commitComment);
	
	/**
	 * Commits all changes made so far using the current userId and the given commitComment and lock context.
	 * 
	 * @param commitComment - the commit comment to use for the commit
	 * @param parentContextDescription - the parent lock context to use for the commit
	 * @return - the timestamp of the successful commit
	 */
	Long commit(String commitComment, String parentContextDescription);
	
	/**
	 * Commits all changes made so far.
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
	Long commit(String userId, String commitComment, String parentContextDescription);
	
	/**
	 * @return whether the commit will notify interested services, notification services about this transaction's commit or not. It's enabled by default.
	 */
	boolean isNotificationEnabled();
	
	/**
	 * Enable or disable notification of other services about this commit.
	 * 
	 * @param notificationEnabled
	 */
	void setNotificationEnabled(boolean notificationEnabled);
	
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
	 * Stages all indexed instances of {@link Revision} and subclasses for deletion
	 * that are currently returned by a "match all" query using
	 * {@link RevisionIndex}.
	 * <p>
	 * Documents not under revision control should be removed separately, along with
	 * any code system versions and corresponding version branches, if a complete
	 * clear operation is needed.
	 */
	void clearContents();
	
	/**
	 * @return <code>true</code> if the underlying {@link StagingArea} is dirty
	 * @see StagingArea#isDirty()
	 */
	boolean isDirty();

	/**
	 * @return the parent lock context for any commits that made outside of the current context 
	 */
	String parentLock();
	
	@Override
	default Builder<? extends TransactionContext> inject() {
		return new DelegatingContext.Builder<TransactionContext>(TransactionContext.class, this);
	}

}
