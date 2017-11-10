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
package com.b2international.snowowl.datastore.internal.branch;

import java.util.Collection;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.DefaultCDOMerger;

import com.b2international.snowowl.core.merge.MergeConflict;
import com.b2international.snowowl.datastore.cdo.ConflictMapper;
import com.b2international.snowowl.datastore.cdo.ICDOConflictProcessor;
import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * An extension of CDO's {@link ManyValued many-valued} merger implementation, that delegates to a terminology-specific
 * {@link ICDOConflictProcessor} when considering certain types of changes.
 */
public class CDOBranchMerger extends DefaultCDOMerger.PerFeature.ManyValued {

	private final ICDOConflictProcessor delegate;
	private final boolean isRebase;
	private CDOBranch sourceBranch;
	private CDOBranch targetBranch;

	/**
	 * Creates a new instance using the specified repository identifier.
	 * 
	 * @param delegate the CDO conflict processor handling terminology-specific merge decisions 
	 * @param targetBranch 
	 * @param sourceBranch 
	 */
	public CDOBranchMerger(final ICDOConflictProcessor delegate, CDOBranch sourceBranch, CDOBranch targetBranch, final boolean isRebase) {
		this.delegate = delegate;
		this.sourceBranch = sourceBranch;
		this.targetBranch = targetBranch;
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
	protected Object detachedInSource(CDOID id) {
		return delegate.detachedInSource(id);
	}
	
	@Override
	protected Object detachedInTarget(CDOID id) {
		return delegate.detachedInTarget(id);
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
		delegate.preProcess(getSourceMap(), getTargetMap(), sourceBranch, targetBranch, isRebase);
	}

	@Override
	public Map<CDOID, Conflict> getConflicts() {
		// Due to the nature of rebase we need to transform certain conflicts to reflect the source and target branches properly
		if (isRebase) {
			return Maps.transformValues(super.getConflicts(), new Function<Conflict, Conflict>() {
				@Override public Conflict apply(Conflict input) {
					return ConflictMapper.invert(input);
				}
			});
		}
		return super.getConflicts();
	}
	
	public void postProcess(final CDOTransaction transaction) {
		delegate.postProcess(transaction);
	}
	
	public Collection<MergeConflict> handleCDOConflicts(final CDOView sourceView, final CDOView targetView) {
		if (isRebase) {
			return delegate.handleCDOConflicts(targetView, sourceView, getConflicts());
		}
		return delegate.handleCDOConflicts(sourceView, targetView, getConflicts());
	}
}
