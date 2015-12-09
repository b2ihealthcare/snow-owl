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
package com.b2international.snowowl.datastore.server.internal.branch;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndDetachedInTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.AddedInSourceAndTargetConflict;
import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;

/**
 * An extension of CDO's {@link ManyValued many-valued} merger implementation, that delegates to a terminology-specific
 * {@link ICDOConflictProcessor} when considering certain types of changes.
 */
public class CDOBranchMerger extends DefaultCDOMerger.PerFeature.ManyValued {

	private final ICDOConflictProcessor delegate;

	/**
	 * Creates a new instance using the specified repository identifier.
	 * 
	 * @param delegate the CDO conflict processor handling terminology-specific merge decisions 
	 */
	public CDOBranchMerger(final ICDOConflictProcessor delegate) {
		this.delegate = delegate;
	}

	@Override
	protected Object addedInSource(final CDORevision revision) {
		return delegate.addedInSource(revision, getTargetMap());
	}

	@Override
	protected Object addedInTarget(final CDORevision revision) {
		return delegate.addedInTarget(revision, getSourceMap());
	}

	@Override
	protected Object changedInTargetAndDetachedInSource(final CDORevisionDelta targetDelta) {
		return delegate.changedInTargetAndDetachedInSource(targetDelta);
	}
	
	@Override
	protected CDOFeatureDelta changedInSourceAndTargetSingleValued(EStructuralFeature feature, 
			CDOFeatureDelta targetFeatureDelta, 
			CDOFeatureDelta sourceFeatureDelta) {

		return delegate.changedInSourceAndTargetSingleValued(targetFeatureDelta, sourceFeatureDelta);
	}

	public void postProcess(final CDOTransaction transaction) {
		Conflict conflict = delegate.postProcess(transaction);
		if (conflict != null) {
			String conflictDetails = getConflictDetails(conflict);
			throw new com.b2international.snowowl.core.exceptions.ConflictException("Conflicts detected while post-processing transaction on branch %s: %s", transaction.getBranch().getPathName(), conflictDetails);
		}
	}

	private String getConflictDetails(Conflict conflict) {
		String details = conflict.getClass().getSimpleName() + " - ";
		
		if (conflict instanceof AddedInSourceAndDetachedInTargetConflict) {
			AddedInSourceAndDetachedInTargetConflict detachedConflict = (AddedInSourceAndDetachedInTargetConflict) conflict;
			details += " source: " + detachedConflict.getSourceId();
			details += " target: " + detachedConflict.getTargetId();
		} else if (conflict instanceof AddedInSourceAndTargetConflict) {
			AddedInSourceAndTargetConflict addedConflict = (AddedInSourceAndTargetConflict)conflict;
			details += " source: " + addedConflict.getSourceId();
			details += " target: " + addedConflict.getTargetId();
		} else {
			details += " id: " + conflict.getID().toString();
		}
		return details;
	}
}
