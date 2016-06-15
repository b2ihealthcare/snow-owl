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

import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import com.b2international.snowowl.datastore.server.cdo.ICDOConflictProcessor;

/**
 * An extension of CDO's {@link ManyValued many-valued} merger implementation, that delegates to a terminology-specific
 * {@link ICDOConflictProcessor} when considering certain types of changes.
 */
public class CDOBranchMerger extends DefaultCDOMerger.PerFeature.ManyValued {

	private final ICDOConflictProcessor delegate;
	private final boolean isRebase;

	/**
	 * Creates a new instance using the specified repository identifier.
	 * 
	 * @param delegate the CDO conflict processor handling terminology-specific merge decisions 
	 */
	public CDOBranchMerger(final ICDOConflictProcessor delegate, final boolean isRebase) {
		this.delegate = delegate;
		this.isRebase = isRebase;
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
	protected CDOFeatureDelta changedInSourceAndTargetSingleValued(final EStructuralFeature feature, 
			final CDOFeatureDelta targetFeatureDelta, 
			final CDOFeatureDelta sourceFeatureDelta) {

		return delegate.changedInSourceAndTargetSingleValued(targetFeatureDelta, sourceFeatureDelta);
	}
	
	@Override
	protected void preProcess() {
		delegate.preProcess(getSourceMap(), getTargetMap());
	}

	@Override
	public Map<CDOID, Conflict> getConflicts() {
		// Due to the nature of rebase we need to transform certain conflicts to reflect the source and target branches properly
		if (isRebase) {
			final Map<CDOID, Conflict> transformedConflicts = newHashMap();
			for (final Entry<CDOID, Conflict> entry : super.getConflicts().entrySet()) {
				final CDOID id = entry.getKey();
				final Conflict conflict = entry.getValue();
				if (conflict instanceof ChangedInSourceAndDetachedInTargetConflict) {
					final ChangedInSourceAndDetachedInTargetConflict inSourceAndDetachedInTargetConflict = (ChangedInSourceAndDetachedInTargetConflict) conflict;
					transformedConflicts.put(id, new ChangedInTargetAndDetachedInSourceConflict(inSourceAndDetachedInTargetConflict.getSourceDelta()));
				} else if (conflict instanceof ChangedInTargetAndDetachedInSourceConflict) {
					final ChangedInTargetAndDetachedInSourceConflict targetAndDetachedInSourceConflict = (ChangedInTargetAndDetachedInSourceConflict) conflict;
					transformedConflicts.put(id, new ChangedInSourceAndDetachedInTargetConflict(targetAndDetachedInSourceConflict.getTargetDelta()));
				} else if (conflict instanceof ChangedInSourceAndTargetConflict) {
					final ChangedInSourceAndTargetConflict sourceAndTargetConflict = (ChangedInSourceAndTargetConflict) conflict;
					transformedConflicts.put(id, new ChangedInSourceAndTargetConflict(sourceAndTargetConflict.getTargetDelta(), sourceAndTargetConflict.getSourceDelta()));
				} else {
					transformedConflicts.put(id, conflict);
				}
			}
			return transformedConflicts;
		}
		return super.getConflicts();
	}
	
	public void postProcess(final CDOTransaction transaction) {
		delegate.postProcess(transaction);
	}
	
	public Map<String, Object> handleCDOConflicts(final CDOTransaction sourceTransaction, final CDOTransaction targetTransaction) {
		return delegate.handleCDOConflicts(sourceTransaction, targetTransaction, getConflicts());
	}
}
