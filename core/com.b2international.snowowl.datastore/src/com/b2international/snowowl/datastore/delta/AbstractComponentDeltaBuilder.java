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
package com.b2international.snowowl.datastore.delta;

import static com.b2international.commons.ChangeKind.ADDED;
import static com.b2international.commons.ChangeKind.DELETED;
import static com.b2international.commons.ChangeKind.UPDATED;
import static com.b2international.commons.collect.LongSets.newLongSet;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collection;
import java.util.Collections;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.collections.PrimitiveSets;
import com.b2international.collections.longs.LongSet;
import com.b2international.commons.ChangeKind;
import com.b2international.commons.StringUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.IComponentIconIdProvider;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.CodeSystemUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * Abstract base class for terminology specified component delta managers.
 * 
 * @see IComponentDeltaBuilder
 */
public abstract class AbstractComponentDeltaBuilder<C extends ComponentDelta> implements IComponentDeltaBuilder<C> {

	private static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
	}

	private static IComponentIconIdProvider<String> getIconProvider(final short terminologyComponentId) {
		return getTerminologyBroker().getComponentIconIdProvider(terminologyComponentId);
	}

	private static CoreTerminologyBroker getTerminologyBroker() {
		return CoreTerminologyBroker.getInstance();
	}

	/**
	 * Base view for the detached objects.
	 */
	private CDOView baseView;

	/**
	 * Current view for changed and new objects.
	 */
	private CDOView currentView;

	/**
	 * Change set data.
	 */
	private CDOChangeSetData changeSetData;

	/**
	 * The component deltas representing all changes. Keys are terminology specific unique IDs.
	 */
	private final Multimap<String, C> deltas = HashMultimap.create();

	/**
	 * Supplies the branch path extracted from the {@link #getCurrentView()}.
	 */
	private final Supplier<IBranchPath> branchPathSupplier = Suppliers.memoize(new Supplier<IBranchPath>() {
		@Override public IBranchPath get() {
			return BranchPathUtils.createPath(getCurrentView());
		}
	});

	@Override
	public Collection<C> processChanges(final CDOChangeSetData changeSetData, final CDOView baseView, final CDOView currentView) {
		checkNotNull(changeSetData, "Change set data argument cannot be null.");

		if (changeSetData.isEmpty()) {
			return Collections.emptySet();
		}

		// Save values for subclasses
		this.changeSetData = changeSetData;
		this.baseView = CDOUtils.check(baseView);
		this.currentView = CDOUtils.check(currentView);

		preProcess();

		for (final CDOIDAndVersion newObject : changeSetData.getNewObjects()) {
			if (getDeltaPredicate().apply(newObject, getCurrentView(), ADDED)) {
				processNew(newObject);
			}
		}

		for (final CDORevisionKey changedObject : changeSetData.getChangedObjects()) {
			if (getDeltaPredicate().apply(changedObject, getCurrentView(), UPDATED)) {
				processDirty(changedObject);
			}
		}

		for (final CDOIDAndVersion detachedObject : changeSetData.getDetachedObjects()) {
			if (getDeltaPredicate().apply(detachedObject, getBaseView(), DELETED)) {
				processDetached(detachedObject);
			}
		}

		postProcess();
		return deltas.values();
	}

	/**
	 * Returns with an iterator for all collected component deltas.
	 * <p>
	 * The returned iterator works on an immutable copy of the delta multimap values. 
	 */
	protected final Collection<C> getDeltas() {
		return ImmutableList.copyOf(deltas.values());
	}

	private void processNew(final CDOIDAndVersion idAndVersion) {
		processChange(idAndVersion, currentView, ChangeKind.ADDED);
	}

	private void processDirty(final CDORevisionKey revisionKey) {
		processChange(revisionKey, currentView, ChangeKind.UPDATED);
	}

	private void processDetached(final CDOIDAndVersion idAndVersion) {
		processChange(idAndVersion, baseView, ChangeKind.DELETED);
	}

	/**
	 * Processes the changed object given by the CDO ID.
	 * <p>
	 * The subject of the change should be available in the specified view.
	 */
	protected abstract void processChange(final CDOIDAndVersion idAndVersion, final CDOView view, final ChangeKind change);

	/**
	 * Returns with the predicate responsible for filtering out irrelevant components from the {@link #changeSetData change set}.
	 * By default this method returns with {@link IComponentDeltaPredicate#ACCEPT_ALL_PREDICATE the predicate} accepting everything.
	 * <p>
	 * Clients may override this method to define custom {@link IComponentDeltaPredicate predicate}.  
	 */
	protected IComponentDeltaPredicate getDeltaPredicate() {
		return IComponentDeltaPredicate.ACCEPT_ALL_PREDICATE;
	}

	/**
	 * Invoked before change set processing starts. Does nothing by default.
	 * <p>
	 * Clients may override this method to add custom behavior.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void preProcess() {
		return;
	}

	/**
	 * Invoked after change set processing is finished. Does nothing by default.
	 * <p>
	 * Clients may override this method to add custom behavior.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void postProcess() {
		return;
	}

	/**
	 * Returns a collection of registered component deltas (or an empty collection) for the specified component ID.
	 */
	protected final Collection<C> get(final String componentId) {
		return deltas.get(componentId);
	}

	protected final boolean contains(final C delta) {
		return deltas.containsEntry(delta.getId(), delta);
	}

	protected final boolean remove(final C delta) {
		return deltas.remove(delta.getId(), delta);
	}

	/**
	 * Registers the component delta to the underlying map.
	 * <p>
	 * Clients should consider the following:
	 * <ul>
	 * <li>
	 * If the underlying map does not contain the specified map, then, this method will return with the specified delta.
	 * </li>
	 * <li>
	 * If the underlying map already contains the delta, then the delta representing the
	 * more significant {@link ComponentDelta#getChange() change} will be stored and that will be the return value.
	 * </li>
	 * <li>
	 * If an already stored delta will be replaced with the specified one (since the priority is stronger) 
	 * all {@link ComponentDelta#getComponentChanges() related IDs} will be copied to.
	 * </li>
	 * </ul>
	 * </p>
	 * <p>
	 * The additional related CDO IDs (if any) will be added to the delta even if a higher priority exists.
	 * 
	 * @see ChangeKind
	 */
	protected final C put(final C delta, final long... relatedCdoIds) {
		final LongSet relatedCdoIdSet = PrimitiveSets.newLongOpenHashSet(relatedCdoIds);
		if (contains(delta)) {

			final Collection<C> storedDeltas = get(delta.getId());
			final C storedDelta = Iterables.find(storedDeltas, Predicates.equalTo(delta));

			if (storedDelta.getChange().compareTo(delta.getChange()) > 0) { // delta is more significant
				// Copy all related component CDO IDs from storedDelta
				delta.getRelatedCdoIds().addAll(storedDelta.getRelatedCdoIds());
				delta.getRelatedCdoIds().addAll(relatedCdoIdSet);

				// Remove previous element and add new one, as change kind not part of #hashCode we have to explicitly remove the old value
				deltas.remove(delta.getId(), storedDelta);
				deltas.put(delta.getId(), delta);
				return delta;

			} else { // storedDelta is more significant
				storedDelta.getRelatedCdoIds().addAll(relatedCdoIdSet);
				return storedDelta;
			}

		} else { 

			delta.getRelatedCdoIds().addAll(relatedCdoIdSet);
			deltas.put(delta.getId(), delta);
			return delta;
		}
	}

	/**
	 * Returns the code system OID associated with the application specific terminology component ID argument.
	 */
	protected final String getCodeSystemOID(final short terminologyComponentId) {
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return CoreTerminologyBroker.UNSPECIFIED;
		}

		final String terminologyComponentIdString = getTerminologyBroker().getTerminologyComponentId(terminologyComponentId);
		return getTerminologyBroker().getTerminologyOidByTerminologyComponentId(terminologyComponentIdString);
	}

	// XXX: The methods below behave differently in subtle ways

	/**
	 * Returns the component icon identifier for the given component. May return with an empty string if the component icon ID cannot be found.
	 * <p>
	 * This method is responsible to look up component icon ID based on the branch for the given view. If the branch does not exists for a particular 
	 * terminology, this method falls back to the ancestor branch of the given view.
	 */
	protected final String getComponentIconId(final CDOView view, final short terminologyComponentId, final String componentId) {
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return StringUtils.EMPTY_STRING;
		}

		final IComponentIconIdProvider<String> iconIdProvider = getIconProvider(terminologyComponentId);
		final IBranchPoint branchPoint = createBranchPoint(view, terminologyComponentId);
		return Strings.nullToEmpty(iconIdProvider.getIconId(branchPoint, componentId));
	}

	/**
	 * Returns with the component icon identifier for the given component. May return with an empty string if the component icon ID cannot be found.
	 * <p>
	 * This method is responsible to look up component icon ID based on the given branch path. If the branch does not exists for a particular 
	 * terminology, this method falls back to the MAIN branch path.
	 */
	protected final String getComponentIconId(final IBranchPath branchPath, final short terminologyComponentId, final String componentId) {
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return StringUtils.EMPTY_STRING;
		}

		final IComponentIconIdProvider<String> iconIdProvider = getIconProvider(terminologyComponentId);
		final IBranchPoint branchPoint = createBranchPoint(branchPath, terminologyComponentId);
		return Strings.nullToEmpty(iconIdProvider.getIconId(branchPoint, componentId));
	}

	private IBranchPoint createBranchPoint(final CDOView view, final short terminologyComponentId) {

		final String repositoryUuid = CodeSystemUtils.getRepositoryUuid(terminologyComponentId);
		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryUuid);
		final IBranchPath currentBranchPath = BranchPathUtils.createPath(view);
		final CDOBranch branch = connection.getBranch(currentBranchPath);

		// If the branch does not exist for the given component type, use the *ancestor branch* of the repository
		return null == branch ? BranchPointUtils.create(repositoryUuid, currentBranchPath.getParent()) : BranchPointUtils.create(view);
	}

	private IBranchPoint createBranchPoint(final IBranchPath branchPath, final short terminologyComponentId) {

		final String repositoryUuid = CodeSystemUtils.getRepositoryUuid(terminologyComponentId);
		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryUuid);
		final CDOBranch branch = connection.getBranch(branchPath);

		// If the branch does not exist for the given component type, use the *MAIN branch* of the repository
		return null == branch ? BranchPointUtils.create(repositoryUuid, createMainPath()) : BranchPointUtils.create(repositoryUuid, branchPath);
	}

	/**
	 * Returns the base view for detached objects.
	 */
	protected final CDOView getBaseView() {
		return baseView;
	}

	/**
	 * Returns the current view for all new and dirty objects. 
	 */
	protected final CDOView getCurrentView() {
		return currentView;
	}

	/**
	 * Returns the change set data.
	 */
	protected final CDOChangeSetData getChangeSetData() {
		return changeSetData;
	}

	/**
	 * Returns the branch path where current builder works on.
	 */
	protected final IBranchPath getBranchPath() {
		return branchPathSupplier.get();
	}
}
