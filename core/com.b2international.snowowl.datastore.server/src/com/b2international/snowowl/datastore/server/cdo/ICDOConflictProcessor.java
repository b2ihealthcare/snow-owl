/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger.Conflict;

import com.b2international.snowowl.core.exceptions.ConflictException;
import com.b2international.snowowl.core.merge.MergeConflict;

/**
 * Handles conflicting changes when synchronizing branches.
 */
public interface ICDOConflictProcessor {

	/**
	 * The extension identifier of CDO conflict processors.
	 */
	String EXTENSION_ID = "com.b2international.snowowl.datastore.server.conflictProcessor";

	/***
	 * Returns the conflict processor's associated repository identifier.
	 * 
	 * @return the identifier of the repository this conflict processor operates on
	 */
	String getRepositoryUuid();

	/**
	 * Checks if the specified {@link CDORevision} from the source change set conflicts with any changes on the target.
	 * 
	 * @param sourceRevision the new {@link CDORevision} from the source change set
	 * @param targetMap the computed change set for the target branch, indexed by {@link CDOID}
	 * @return <ul>
	 * <li>{@code null} if the add should be ignored;
	 * <li>a {@link CDORevision} if an addition should take place;
	 * <li>a {@link CDOID} if an object should be removed;
	 * <li>a {@link Conflict} if a merge conflict should be reported.
	 * </ul>
	 */
	Object addedInSource(CDORevision sourceRevision, Map<CDOID, Object> targetMap);

	/**
	 * Checks if the specified {@link CDOFeatureDelta} from the target change set conflicts with the corresponding 
	 * {@code CDOFeatureDelta} on the source.
	 * 
	 * @param targetFeatureDelta the single-value change on the target
	 * @param sourceFeatureDelta the single-value change on the source
	 * @return <ul>
	 * <li>{@code null} if a conflict should be reported;
	 * <li>a {@link CDOFeatureDelta} containing the "winning" change otherwise.
	 * </ul>
	 */
	CDOFeatureDelta changedInSourceAndTargetSingleValued(CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta);

	/**
	 * Checks if the object with the removed {@link CDOID} from the target change set conflicts with an item in the
	 * source change set.
	 * 
	 * @param targetDelta the computed change on the target branch for the object removed on the source branch
	 * @return <ul>
	 * <li>{@code null} if the add should be ignored;
	 * <li>a {@link CDORevision} if an addition should take place;
	 * <li>a {@link CDOID} if an object should be removed;
	 * <li>a {@link Conflict} if a merge conflict should be reported.
	 * </ul>
	 */
	Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta);
	
	Object detachedInSource(CDOID id);
	
	void preProcess(Map<CDOID, Object> sourceMap, Map<CDOID, Object> targetMap);

	/**
	 * Post-processes the resulting change set. This usually removes cross-references from objects queued for removal
	 * before detaching them from the persistent object graph, using the specified transaction.
	 * 
	 * @param transaction the CDO transaction after the change set has been applied (may not be {@code null})
	 * @throws ConflictException if conflicts are detected while post-processing takes place (can be specific to a
	 * terminology, not necessarily tied to a CDO revision or delta)
	 */
	void postProcess(CDOTransaction transaction) throws ConflictException;
	
	/**
	 * Handle conflicts detected by CDO during branch merges. Can be used for converting CDO conflicts to {@link MergeConflict}s.
	 * 
	 * @param sourceView
	 * @param targetView
	 * @param conflicts
	 * @param invertConflicts 
	 * @return
	 */
	Collection<MergeConflict> handleCDOConflicts(final CDOView sourceView, final CDOView targetView, final Map<CDOID, Conflict> conflicts, boolean invertConflicts);
}
