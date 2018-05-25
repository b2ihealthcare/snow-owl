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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
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

import com.b2international.commons.collections.Collections3;
import com.b2international.index.query.Expression;
import com.b2international.index.query.Expressions;
import com.b2international.index.query.Query;
import com.b2international.index.revision.RevisionIndex;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.google.common.collect.FluentIterable;
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
	private final Map<CDOID, EClass> detachedObjects;
	private final Map<CDOID, CDORevisionDelta> revisionDeltas;
	private final long timestamp;
	private final RevisionIndex index;
	private final String branchPath;
	
	public CDOCommitChangeSet(
			final RevisionIndex index,
			final String branchPath,
			final CDOView view, 
			final String userId, 
			final String commitComment,
			final Collection<CDOObject> newComponents, 
			final Collection<CDOObject> dirtyComponents, 
			final Map<CDOID, EClass> detachedObjects, 
			final Map<CDOID, CDORevisionDelta> revisionDeltas, 
			final long timestamp) {
		this.branchPath = branchPath;
		this.index = checkNotNull(index, "Index argument cannot be null");
		this.view = checkNotNull(view, "CDO view argument cannot be null.");
		this.userId = checkNotNull(userId, "User ID argument cannot be null.");
		this.commitComment = commitComment;
		this.newComponents = Collections3.toImmutableList(newComponents);
		this.dirtyComponents = Collections3.toImmutableList(dirtyComponents);
		this.detachedObjects = detachedObjects == null ? Collections.emptyMap() : detachedObjects;
		this.revisionDeltas = ImmutableMap.copyOf(revisionDeltas);
		this.timestamp = timestamp;
	}

	@Override
	public boolean isEmpty() {
		return dirtyComponents.isEmpty() && newComponents.isEmpty() && detachedObjects.isEmpty() && revisionDeltas.isEmpty();
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
	public <T extends CDOObject> Set<T> getNewComponents(final Class<T> type) {
		return FluentIterable.from(getNewComponents()).filter(type).toSet();
	}
	
	@Override
	public <T extends CDOObject> Set<T> getDirtyComponents(final Class<T> type) {
		return FluentIterable.from(getDirtyComponents()).filter(type).toSet();
	}

	@Override
	public <T extends RevisionDocument> List<T> getDetachedComponents(final EClass eClass, final Class<T> type) {
		return getDetachedComponents(eClass, type, storageKeys -> Expressions.matchAnyLong(RevisionDocument.Fields.STORAGE_KEY, storageKeys));
	}
	
	@Override
	public <T> List<T> getDetachedComponents(final EClass eClass, final Class<T> type, final Function<Iterable<Long>, Expression> matchStorageKeyExpression) {
		final Set<Long> detachedComponentStorageKeys = getDetachedComponentStorageKeys(eClass);
		if (detachedComponentStorageKeys.isEmpty()) return Collections.emptyList();
		return index.read(branchPath, searcher -> {
			return searcher.search(Query.select(type)
					.from(type)
					.where(matchStorageKeyExpression.apply(detachedComponentStorageKeys))
					.limit(Integer.MAX_VALUE)
					.build())
					.getHits();
		});
	}
	
	@Override
	public Set<Long> getDetachedComponentStorageKeys(EClass eClass) {
		return detachedObjects.entrySet()
				.stream()
				.filter(entry -> eClass.isSuperTypeOf(entry.getValue()))
				.map(entry -> entry.getKey())
				.map(CDOIDUtil::getLong)
				.collect(Collectors.toSet());
	}

	@Override
	public <T extends CDOObject> Set<T> getDirtyComponents(Class<T> type, Set<EStructuralFeature> allowedFeatures) {
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
			})
			.toSet();
	}
	
	@Override
	public <T extends RevisionDocument> Set<String> getDetachedComponentIds(EClass eClass, Class<T> type) {
		return getDetachedComponents(eClass, type).stream().map(RevisionDocument::getId).collect(Collectors.toSet());
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
