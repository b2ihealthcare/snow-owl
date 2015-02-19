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
package com.b2international.snowowl.datastore.server.cdo;

import java.util.Map;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.snowowl.datastore.cdo.ConflictWrapper;

/**
 * Conflict processor representation for CDO.
 * @see NullCDOConflictProcessor
 */
public interface ICDOConflictProcessor {

	/**Unique ID of the CDO conflict processor extension point.*/
	String CONFLICT_PROCESSOR_EXTENSION_ID = "com.b2international.snowowl.datastore.server.conflictProcessor";
	
	/**
	 * Checks if a new CDO object on the synchronization source branch conflicts with any changes that happened on the
	 * target branch (according to the given change set).
	 * <p>
	 * <b>Note:</b> the "synchronization source" role can be filled by either the task parent and the task branch, it is not always the task branch.
	 * 
	 * @param targetChangeSet the change set of the target branch.
	 * @param newInSource the CDO ID and version of an object which is new on the source branch. Could be full {@link InternalCDORevision revision}.
	 * @param sourceView a view opened on the source branch for resolving any CDO IDs. The caller is responsible for disposing the view.
	 * @return the {@link ConflictWrapper} describing the conflict, or {@code null} if this new object is not part of a conflicting change.
	 */
	@Nullable ConflictWrapper checkConflictForNewObjects(final CDOChangeSetData targetChangeSet, final CDOIDAndVersion newInSource, final CDOView sourceView);
	
	/**
	 * Checks for application specific conflicts for objects which has been deleted on the source branch. 
	 * @param changedComponentsMapping a mapping of changed components from the target. Keys are CDO IDs and values are {@link CDOIDAndVersion}. Could be full revisions.
	 * @param detachedOnSource the CDO ID and version representing a deleted objects. (Never a full CDO revision)
	 * @param sourceView the view for the source.
	 * @param targetView view associated with the target branch.
	 * @return a wrapper representing an application specific conflict, or {@code null} if no conflicts were found for the deleted component. 
	 */
	@Nullable ConflictWrapper checkConflictForDetachedObjects(final Map<CDOID, CDORevisionKey> changedComponentsMapping, final CDOIDAndVersion detachedOnSource, final CDOView sourceView, final CDOView targetView);
	
	/**
	 * Detaches the specified object.
	 * @param objectToRemove the object to remove.
	 */
	void detachConflictingObject(final CDOObject objectToRemove);
	
	
	/***
	 * Returns with the repository UUID where the current processor works on.
	 * @return the repository UUID.
	 */
	String getRepositoryUuid();
}