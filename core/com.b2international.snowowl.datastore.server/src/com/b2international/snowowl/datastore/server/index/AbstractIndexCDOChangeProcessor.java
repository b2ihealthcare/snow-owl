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
package com.b2international.snowowl.datastore.server.index;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexMappingStrategy;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologymetadata.CodeSystemVersion;
import com.b2international.snowowl.terminologymetadata.TerminologymetadataPackage;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemIndexMappingStrategy;
import com.b2international.snowowl.terminologyregistry.core.index.CodeSystemVersionIndexMappingStrategy;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Abstract {@link ICDOChangeProcessor} implementation.
 * 
 */
public abstract class AbstractIndexCDOChangeProcessor<E extends IIndexEntry, T extends EObject> implements ICDOChangeProcessor {
	
	protected final Set<T> newComponents = Sets.newHashSet();
	protected final Set<T> dirtyComponents = Sets.newHashSet();
	protected final Set<Entry<CDOID, EClass>> detachedComponents = Sets.newHashSet();
	protected final IBranchPath branchPath;
	
	private final Collection<EClass> trackedEClasses;
	private final IIndexUpdater<E> indexService;
	
	private ICDOCommitChangeSet commitChangeSet;
	/**
	 * Constructor
	 * @param indexService
	 * @param branchPath
	 */
	public  AbstractIndexCDOChangeProcessor(final IIndexUpdater<E> indexService, final IBranchPath branchPath, final Collection<EClass> trackedEClasses) {
		this.indexService = checkNotNull(indexService, "Index service argument cannot be null.");
		this.branchPath = checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.trackedEClasses = ImmutableSet.copyOf(checkNotNull(trackedEClasses, "trackedEClasses argument cannot be null"));
	}

	/**Returns with a collection of tracked {@link EClass EClasses} the current change processor is reposnsible for. */
	public Collection<EClass> getTrackedEClasses() {
		return trackedEClasses;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#process(com.b2international.snowowl.datastore.ICDOCommitChangeSet)
	 */
	@Override
	public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {

		this.commitChangeSet = checkNotNull(commitChangeSet, "CDO commit change set argument cannot be null.");
		
		for (final CodeSystemVersion dirtyCodeSystemVersion : filter(dirtyComponents, CodeSystemVersion.class)) {
			checkAndSetCodeSystemLastUpdateTime(dirtyCodeSystemVersion);
		}
		
		populateChangedComponents(commitChangeSet);
		
		// always process deletions first, some changes will come in as delete + add
		for (final Entry<CDOID, EClass> detachedComponent : detachedComponents) {
			indexDeletion(detachedComponent);
		}
		
		for (final T newComponent : newComponents) {
			indexAddition(newComponent);
		}
		
		for (final T dirtyComponent : dirtyComponents) {
			indexUpdate(dirtyComponent);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.CDOChangeProcessor#commit()
	 */
	@Override
	public void commit() throws SnowowlServiceException {
		indexService.commit(branchPath);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#afterCommit()
	 */
	@Override
	public void afterCommit() {
		reset();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getChangeDescription()
	 */
	@Override
	public String getChangeDescription() {

		final StringBuffer sb = new StringBuffer(getName());
		sb.append(": ");

		if (!newComponents.isEmpty()) {
			sb.append(" new components added: ");
			sb.append(newComponents.size());
		}

		if (!dirtyComponents.isEmpty()) {
			sb.append(" changed components: ");
			sb.append(dirtyComponents.size());
		}

		if (!detachedComponents.isEmpty()) {
			sb.append(" deleted components: ");
			sb.append(detachedComponents.size());
		}

		return sb.toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#hadChangesToProcess()
	 */
	@Override
	public boolean hadChangesToProcess() {
		return (!detachedComponents.isEmpty() || !dirtyComponents.isEmpty() || !newComponents.isEmpty());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getBranchPath()
	 */
	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}
	
	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#prepareCommit()
	 */
	@Override
	public void prepareCommit() throws SnowowlServiceException {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.CDOChangeProcessor#rollback()
	 */
	@Override
	public void rollback() throws SnowowlServiceException {
		indexService.rollback(branchPath);
		reset();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.ICDOChangeProcessor#getUserId()
	 */
	@Override
	public String getUserId() {
		return commitChangeSet.getUserId();
	}

	/**
	 * Returns with the {@link ICDOCommitChangeSet commit change set} for the change processing.
	 * <p>Could be {@code null} if called before invoking {@link #process(ICDOCommitChangeSet)}.
	 * @return the commit change set for the change processing.
	 */
	@Nullable protected ICDOCommitChangeSet getCommitChangeSet() {
		return commitChangeSet;
	}
	
	/**
	 * Create a {@link IIndexMappingStrategy} for the specified object.
	 * @param object
	 * @param <C> - type of the terminology independent component
	 * @return
	 */
	protected abstract <C extends IComponent<?>> IIndexMappingStrategy createMappingStrategy(T object);

	/**Populates the new, dirty and detached components based on the given commit change set data argument.*/
	@SuppressWarnings("unchecked")
	@OverridingMethodsMustInvokeSuper
	protected void populateChangedComponents(final ICDOCommitChangeSet commitChangeSet) {
		
		Preconditions.checkNotNull(commitChangeSet, "CDO commit change set argument cannot be null.");
	
		dirtyComponents.addAll((Set<T>) Sets.newHashSet(Iterables.filter(commitChangeSet.getDirtyComponents(), new AnyInstanceOfPredicate(trackedEClasses))));
		newComponents.addAll((Set<T>) Sets.newHashSet(Iterables.filter(commitChangeSet.getNewComponents(), new AnyInstanceOfPredicate(trackedEClasses))));
		
		for (final Entry<CDOID, EClass> detachedEntry : commitChangeSet.getDetachedComponents().entrySet()) {
			if (trackedEClasses.contains(detachedEntry.getValue())) {
				detachedComponents.add(detachedEntry);
			}
		}
	}

	/**
	 * Indexes the dirty component argument.
	 * @param dirtyComponent the changed component.
	 */
	protected void indexUpdate(final T dirtyComponent) {
		indexService.index(branchPath, createMappingStrategyForDirtyComponent(dirtyComponent));
	}

	/**
	 * Indexes the new component argument.
	 * @param newComponent the new component to index.
	 */
	protected void indexAddition(final T newComponent) {
		indexService.index(branchPath, createMappingStrategyForNewComponent(newComponent));
	}

	/**
	 * Indexes a deletion defined with the {@link CDOID} and {@link EClass} entry argument.
	 * @param detachedComponent the entry representing a deleted component to index.
	 */
	protected void indexDeletion(final Entry<CDOID, EClass> detachedComponent) {
		indexService.delete(branchPath, CDOIDUtil.getLong(detachedComponent.getKey()));
	}

	/**
	 * Creates the index mapping strategy for the new component.
	 * @param newComponent the new component to index.
	 * @return the {@link IIndexMappingStrategy index mapping strategy} for the new component.
	 */
	protected IIndexMappingStrategy createMappingStrategyForNewComponent(final T newComponent) {
		return createMappingStrategy(newComponent);
	}

	/**
	 * Creates the index mapping strategy for the dirty component.
	 * @param dirtyComponent the dirty component to index.
	 * @return the {@link IIndexMappingStrategy index mapping strategy} for the dirty component.
	 */
	protected IIndexMappingStrategy createMappingStrategyForDirtyComponent(final T dirtyComponent) {
		return createMappingStrategy(dirtyComponent);
	}
	
	/**Returns with {@code true} if the object argument is a {@link CodeSystem} instance. Otherwise {@code false}.*/
	protected boolean isCodeSystem(final EObject object) {
		return TerminologymetadataPackage.eINSTANCE.getCodeSystem().isSuperTypeOf(object.eClass());
	}
	
	/**Returns with {@code true} if the object argument is a {@link CodeSystemVersion} instance. Otherwise {@code false}.*/
	protected boolean isCodeSystemVersion(final EObject object) {
		return TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion().isSuperTypeOf(object.eClass());
	}
	
	/**Returns with the code system index mapping strategy.*/
	protected IIndexMappingStrategy createMappingStrategy(final CodeSystem codeSystem) {
		return new CodeSystemIndexMappingStrategy(codeSystem);
	}
	
	/**Returns with the index mapping strategy for the {@link CodeSystemVersion} argument.*/
	protected IIndexMappingStrategy createMappingStrategy(final CodeSystemVersion version) {
		return new CodeSystemVersionIndexMappingStrategy(version);
	}
	
	/**
	 * Clears out the tracking {@link Map}s.
	 */
	private void reset() {
		detachedComponents.clear();
		dirtyComponents.clear();
		newComponents.clear();
	}
	
	@SuppressWarnings({ "restriction", "unchecked" })
	private void checkAndSetCodeSystemLastUpdateTime(final CDOObject component) {
		final CodeSystemVersion codeSystemVersion = (CodeSystemVersion) component;
		final CDOFeatureDelta lastUpdateFeatureDelta = commitChangeSet.getRevisionDeltas().get(component.cdoID()).getFeatureDelta(TerminologymetadataPackage.eINSTANCE.getCodeSystemVersion_LastUpdateDate());
		if (lastUpdateFeatureDelta instanceof org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) {
			((org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl) lastUpdateFeatureDelta).setValue(new Date(commitChangeSet.getTimestamp()));
			dirtyComponents.add((T) codeSystemVersion);
		}		
	}
	
	private static final class AnyInstanceOfPredicate implements Predicate<Object> {

		private final Collection<EClass> classes;

		AnyInstanceOfPredicate(final Collection<EClass> classes) {
			this.classes = Preconditions.checkNotNull(classes, "Classes argument cannot be null.");
		}
		
		/* (non-Javadoc)
		 * @see com.google.common.base.Predicate#apply(java.lang.Object)
		 */
		@Override
		public boolean apply(final Object input) {
			Preconditions.checkNotNull(input, "Input argument cannot be null.");
			if (input instanceof EClass) {
				return classes.contains(input);
			} else if (input instanceof EObject) {
				return classes.contains(((EObject) input).eClass());
			} else if (input instanceof Entry<?, ?>) {
				if (((Entry<?, ?>) input).getValue() instanceof EClass) {
					return classes.contains(((Entry<?, ?>) input).getValue());
				} else {
					throw new IllegalArgumentException("Unsupported input entry value type: " + ((Entry<?, ?>) input).getValue().getClass());
				}
			} else {
				throw new IllegalArgumentException("Unsupported input type: " + input.getClass());
			}
		}
		
	}
}