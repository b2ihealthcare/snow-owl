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
package com.b2international.snowowl.datastore.server;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.api.index.IIndexUpdater;
import com.b2international.snowowl.datastore.ICDOChangeProcessor;
import com.b2international.snowowl.datastore.ICDOCommitChangeSet;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * An abstract {@link ICDOChangeProcessor} implementation which tracks changes to a pre-defined set of EClasses.
 */
public abstract class AbstractCDOChangeProcessor<E extends IIndexEntry, T extends EObject> implements ICDOChangeProcessor {

	protected final IIndexUpdater<E> indexService;
	protected final IBranchPath branchPath;
	protected final Collection<EClass> trackedEClasses;

	protected final Set<T> newComponents = Sets.newHashSet();
	protected final Set<T> dirtyComponents = Sets.newHashSet();
	protected final Set<Entry<CDOID, EClass>> detachedComponents = Sets.newHashSet();

	protected ICDOCommitChangeSet commitChangeSet;

	protected AbstractCDOChangeProcessor(final IIndexUpdater<E> indexService, final IBranchPath branchPath, final Collection<EClass> trackedEClasses) {
		this.indexService = checkNotNull(indexService, "Index service argument cannot be null.");
		this.branchPath = checkNotNull(branchPath, "Branch path argument cannot be null.");
		this.trackedEClasses = ImmutableSet.copyOf(checkNotNull(trackedEClasses, "trackedEClasses argument cannot be null"));
	}

	protected Collection<EClass> getTrackedEClasses() {
		return trackedEClasses;
	}

	/**
	 * Returns with the {@link ICDOCommitChangeSet commit change set} for the change processing.
	 * <p>Could be {@code null} if called before invoking {@link #process(ICDOCommitChangeSet)}.
	 * @return the commit change set for the change processing.
	 */
	@Nullable
	protected ICDOCommitChangeSet getCommitChangeSet() {
		return commitChangeSet;
	}

	@Override
	public void process(final ICDOCommitChangeSet commitChangeSet) throws SnowowlServiceException {
		this.commitChangeSet = checkNotNull(commitChangeSet, "CDO commit change set argument cannot be null.");

		populateChangedComponents();

		// always process deletions first, some changes will come in as delete + add
		for (final Entry<CDOID, EClass> detachedComponent : detachedComponents) {
			processDeletion(detachedComponent);
		}

		for (final T newComponent : newComponents) {
			processAddition(newComponent);
		}

		for (final T dirtyComponent : dirtyComponents) {
			processUpdate(dirtyComponent);
		}
	}

	@Override
	public void afterCommit() {
		reset();
	}

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

	@Override
	public boolean hadChangesToProcess() {
		return !detachedComponents.isEmpty() || !dirtyComponents.isEmpty() || !newComponents.isEmpty();
	}

	@Override
	public IBranchPath getBranchPath() {
		return branchPath;
	}

	@Override
	public void prepareCommit() throws SnowowlServiceException {
		throw new UnsupportedOperationException("Not implemented yet.");
	}

	@Override
	public void commit() throws SnowowlServiceException {
		// No-op, subclasses should override
	}
	
	@Override
	public void rollback() throws SnowowlServiceException {
		reset();
	}

	@Override
	public String getUserId() {
		return commitChangeSet.getUserId();
	}

	@SuppressWarnings("unchecked")
	protected void populateChangedComponents() {
		dirtyComponents.addAll((Set<T>) Sets.newHashSet(Iterables.filter(commitChangeSet.getDirtyComponents(), new AnyInstanceOfPredicate(trackedEClasses))));
		newComponents.addAll((Set<T>) Sets.newHashSet(Iterables.filter(commitChangeSet.getNewComponents(), new AnyInstanceOfPredicate(trackedEClasses))));

		for (final Entry<CDOID, EClass> detachedEntry : commitChangeSet.getDetachedComponents().entrySet()) {
			if (trackedEClasses.contains(detachedEntry.getValue())) {
				detachedComponents.add(detachedEntry);
			}
		}
	}

	/**
	 * Clears out the tracking {@link Map}s.
	 */
	protected void reset() {
		detachedComponents.clear();
		dirtyComponents.clear();
		newComponents.clear();
	}

	protected abstract void processUpdate(T dirtyComponent);

	protected abstract void processAddition(T newComponent);

	protected abstract void processDeletion(Entry<CDOID, EClass> detachedComponent);

	private static final class AnyInstanceOfPredicate implements Predicate<Object> {

		private final Collection<EClass> classes;

		AnyInstanceOfPredicate(final Collection<EClass> classes) {
			this.classes = Preconditions.checkNotNull(classes, "Classes argument cannot be null.");
		}

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
