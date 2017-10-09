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
package com.b2international.snowowl.datastore.server.internal.branch;

import java.util.Collection;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import com.b2international.snowowl.core.merge.MergeConflict;
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
	protected final Object addedInSource(final CDORevision revision) {
		return delegate.addedInSource(revision, getTargetMap());
	}

	@Override
	protected final Object addedInTarget(final CDORevision revision) {
		throw new IllegalStateException("Dead code");
	}

	@Override
	protected final Object detachedInSource(CDOID id) {
		return delegate.detachedInSource(id);
	}
	
	@Override
	protected final Object detachedInTarget(CDOID id) {
		throw new IllegalStateException("Dead code");
	}
	
	@Override
	protected final Object changedInTargetAndDetachedInSource(final CDORevisionDelta targetDelta) {
		return delegate.changedInTargetAndDetachedInSource(targetDelta);
	}
	
	@Override
	protected final CDOFeatureDelta changedInSourceAndTargetSingleValued(final EStructuralFeature feature, 
			final CDOFeatureDelta targetFeatureDelta, 
			final CDOFeatureDelta sourceFeatureDelta) {

		return delegate.changedInSourceAndTargetSingleValued(targetFeatureDelta, sourceFeatureDelta);
	}
	
	@Override
	protected void preProcess() {
		delegate.preProcess(getSourceMap(), getTargetMap());
	}
	
	public void postProcess(final CDOTransaction transaction) {
		delegate.postProcess(transaction);
	}
	
	public Collection<MergeConflict> handleCDOConflicts(final CDOView sourceView, final CDOView targetView, boolean invertConflicts) {
		return delegate.handleCDOConflicts(sourceView, targetView, getConflicts(), invertConflicts);
	}
}
