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
package com.b2international.snowowl.datastore;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * An implementation of the {@link ICDOCommitChangeSet} interface.
 */
public final class CDOCommitChangeSet implements ICDOCommitChangeSet {

	private final CDOView view;
	private final String userId;
	private final String commitComment;
	private final Collection<CDOObject> newComponents;
	private final Collection<CDOObject> dirtyComponents;
	private final Map<CDOID, EClass> detachedComponents;
	private final Map<CDOID, CDORevisionDelta> revisionDeltas;
	private final long timestamp;
	
	public CDOCommitChangeSet(final CDOView view, 
			final String userId, 
			final String commitComment,
			final Collection<CDOObject> newComponents, 
			final Collection<CDOObject> dirtyComponents, 
			final Map<CDOID, EClass> detachedComponents, 
			final Map<CDOID, CDORevisionDelta> revisionDeltas, 
			final long timestamp) {
		
		checkNotNull(view, "CDO view argument cannot be null.");
		checkNotNull(userId, "User ID argument cannot be null.");
		checkNotNull(newComponents, "New components argument cannot be null.");
		checkNotNull(dirtyComponents, "Dirty components argument cannot be null.");
		checkNotNull(detachedComponents, "Detached components argument cannot be null.");
		checkNotNull(revisionDeltas, "Revision deltas map cannot be null.");

		this.view = view;
		this.userId = userId;
		this.commitComment = commitComment;
		this.newComponents = ImmutableList.copyOf(newComponents);
		this.dirtyComponents = ImmutableList.copyOf(dirtyComponents);
		this.detachedComponents = ImmutableMap.copyOf(detachedComponents);
		this.revisionDeltas = ImmutableMap.copyOf(revisionDeltas);
		this.timestamp = timestamp;
	}

	@Override
	public boolean isEmpty() {
		return dirtyComponents.isEmpty() && newComponents.isEmpty() && detachedComponents.isEmpty() && revisionDeltas.isEmpty();
	}
	
	@Override
	public Collection<CDOObject> getNewComponents() {
		return newComponents;
	}
	
	@Override
	public Collection<CDOObject> getDirtyComponents() {
		return dirtyComponents;
	}
	
	@Override
	public Map<CDOID, EClass> getDetachedComponents() {
		return detachedComponents;
	}
	
	@Override
	public String getUserId() {
		return userId;
	}
	
	@Override
	public String getCommitComment() {
		return commitComment;
	}
	
	@Override
	public CDOView getView() {
		return view;
	}
	
	@Override
	public Map<CDOID, CDORevisionDelta> getRevisionDeltas() {
		return revisionDeltas;
	}
	
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public <T extends CDOObject> Iterable<T> getNewComponents(final Class<T> type) {
		return FluentIterable.from(getNewComponents()).filter(type).toSet();
	}
	
	@Override
	public <T extends CDOObject> Iterable<T> getDirtyComponents(final Class<T> type) {
		return FluentIterable.from(getDirtyComponents()).filter(type).toSet();
	}
	
	@Override
	public Collection<CDOID> getDetachedComponents(final EClass eClass) {
		return FluentIterable.from(getDetachedComponents().entrySet())
				.filter(input -> eClass.isSuperTypeOf(input.getValue()))
				.transform(input -> input.getKey())
				.toSet();
	}
	
	@Override
	public <T extends CDOObject> Iterable<T> getDirtyComponents(Class<T> type, Set<EStructuralFeature> allowedFeatures) {
		return FluentIterable.from(getDirtyComponents(type))
			.filter(input -> {
				final DirtyFeatureDeltaVisitor visitor = new DirtyFeatureDeltaVisitor(allowedFeatures);
				final CDORevisionDelta revisionDelta = getRevisionDeltas().get(input.cdoID());
				if (revisionDelta != null) {
					revisionDelta.accept(visitor);
					return visitor.hasAllowedChanges();
				} else {
					return false;
				}
			});
	}
	
	/**
	 * @since 6.3
	 */
	private final static class DirtyFeatureDeltaVisitor implements CDOFeatureDeltaVisitor {
		
		private final Set<EStructuralFeature> allowedFeatures;
		
		private boolean hasAllowedChanges;
		
		public DirtyFeatureDeltaVisitor(Set<EStructuralFeature> allowedFeatures) {
			this.allowedFeatures = allowedFeatures;
		}

		@Override
		public void visit(CDOSetFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOListFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOAddFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOClearFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOMoveFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDORemoveFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOUnsetFeatureDelta delta) {
			visitDelta(delta);
		}
		
		@Override
		public void visit(CDOContainerFeatureDelta delta) {
			visitDelta(delta);
		}
		
		private void visitDelta(CDOFeatureDelta delta) {
			hasAllowedChanges |= allowedFeatures.contains(delta.getFeature());
		}

		public boolean hasAllowedChanges() {
			return hasAllowedChanges;
		}
		
	}
	
}
