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
package com.b2international.snowowl.datastore;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;

/**
 * Processes new, detached and changed {@link CDOObject CDOObjects} to update a lightweight secondary store.
 *
 *
 */
public interface ICDOChangeProcessor {

	/**
	 * Processes the changes.
	 * <p>
	 * When entering this method, the currently executing thread must have a valid {@link IStoreAccessor store 
	 * accessor} or {@link InternalSession session} set in {@link StoreThreadLocal}.
	 * 
	 * @param commitChangeSet the set of changes
	 * @throws SnowowlServiceException
	 */
	void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException;
	
	/**
	 * Persists the processed changes.
	 * @throws SnowowlServiceException
	 */
	void commit() throws SnowowlServiceException;
	
	/**
	 * Prepares the changes to be persisted.
	 * @throws SnowowlServiceException
	 */
	void prepareCommit() throws SnowowlServiceException;
	
	/**
	 * Rolls back.
	 * @throws SnowowlServiceException
	 */
	void rollback() throws SnowowlServiceException;

	/**
	 * Returns with the humane readable name of the change processor.
	 * @return the name of the processor.
	 */
	String getName();

	/**
	 * Returns the user id of the user responsible for the changes
	 * processed by this change processor.
	 * @return the user id as a string
	 */
	String getUserId();
	
	/**
	 * Returns the branch path this change processor is operating on.
	 * @return the branch path of the change processor
	 */
	IBranchPath getBranchPath();
	
	/**
	 * Returns the human-readable description of the change performed.
	 * @return
	 */
	String getChangeDescription();
	
	/**
	 * Returns true if this processor had any changes to process.
	 * @return
	 */
	boolean hadChangesToProcess();

	/**
	 * After commit operation to do any cleanup necessary.
	 */
	void afterCommit();
	
	public static final ICDOChangeProcessor NULL_IMPL = new ICDOChangeProcessor() {
		
		@Override public void rollback() throws SnowowlServiceException {
		}
		
		@Override public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		}
		
		@Override public void prepareCommit() throws SnowowlServiceException {
		}
		
		@Override public boolean hadChangesToProcess() {
			return false;
		}
		
		@Override public String getUserId() {
			return null;
		}
		
		@Override public String getName() {
			return null;
		}
		
		@Override public String getChangeDescription() {
			return null;
		}
		
		@Override public IBranchPath getBranchPath() {
			return null;
		}
		
		@Override
		public void commit() throws SnowowlServiceException {
		}
		
		@Override
		public void afterCommit() {
		}
		
	};
	
}