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
import static com.b2international.commons.pcj.LongSets.newLongSet;
import static com.b2international.snowowl.datastore.BranchPathUtils.createMainPath;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.annotation.Nullable;
import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.view.CDOView;

import com.b2international.commons.ChangeKind;
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
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * Abstract base class for terminology specified component delta managers.
 * @see IComponentDeltaBuilder
 */
public abstract class AbstractComponentDeltaBuilder<C extends ComponentDelta> implements IComponentDeltaBuilder<C> {

	/**Base view for the detached objects.*/
	private CDOView baseView;
	/**Current view for changed and new objects.*/
	private CDOView currentView;
	/**Change set data.*/
	private CDOChangeSetData changeSetData;
	/**The component deltas representing all changes. Keys are terminology specific unique IDs.*/
	private Multimap<String, C> deltas = HashMultimap.create();
	/**Supplies the branch path extracted from the {@link #getCurrentView()}.*/
	private Supplier<IBranchPath> branchPathSupplier = Suppliers.memoize(new Supplier<IBranchPath>() {
			@Override public IBranchPath get() {
				return BranchPathUtils.createPath(getCurrentView());
		}
	});

	/* (non-Javadoc)
	 * @see com.b2international.snowowl.datastore.delta.IComponentDeltaBuilder#processChanges(org.eclipse.emf.cdo.common.commit.CDOChangeSetData, org.eclipse.emf.cdo.view.CDOView, org.eclipse.emf.cdo.view.CDOView)
	 */
	@Override
	public Collection<C> processChanges(final CDOChangeSetData changeSetData, final CDOView baseView, final CDOView currentView) {
		
		this.changeSetData = Preconditions.checkNotNull(changeSetData, "Change set data argument cannot be null.");
		
		if (this.changeSetData.isEmpty()) {
			
			return Collections.emptySet();
			
		}
		
		this.baseView = CDOUtils.check(baseView);
		this.currentView = CDOUtils.check(currentView);
		
		preProcess();
		
		for (final Iterator<CDOIDAndVersion> itr  = this.changeSetData.getNewObjects().iterator(); itr.hasNext(); /* */) {
			
			final CDOIDAndVersion idAndVersion = itr.next();

			//shortcut to ignore processing new objects
			if (!getDeltaPredicate().apply(idAndVersion, getCurrentView(), ADDED)) {
				continue;
			}
			
			processNew(idAndVersion);
			
		}
		
		for (final Iterator<CDORevisionKey> itr = this.changeSetData.getChangedObjects().iterator(); itr.hasNext(); /* */) {
			
			final CDORevisionKey revisionKey = itr.next();

			//shortcut to ignore processing dirty component
			if (!getDeltaPredicate().apply(revisionKey, getCurrentView(), UPDATED)) {
				continue;
			}
			
			processDirty(revisionKey);
			
		}

		for (final Iterator<CDOIDAndVersion> itr = this.changeSetData.getDetachedObjects().iterator(); itr.hasNext(); /* */) {
			
			final CDOIDAndVersion idAndVersion = itr.next();

			//shortcut to ignore detached component processing
			if (!getDeltaPredicate().apply(idAndVersion, getBaseView(), DELETED)) {
				continue;
			}
			
			processDetached(idAndVersion);
			
		}
		
		postProcess();
		
		return deltas.values();
	}

	/**Returns with an iterator for the deltas.*/
	protected Iterator<C> getDeltaIterator() {
		return ImmutableMultimap.<String, C>copyOf(deltas).values().iterator();
	}
	
	/**Processes a new component.*/
	protected abstract void processNew(final CDOIDAndVersion idAndVersion);

	/**Processes a dirty component.*/
	protected abstract void processDirty(final CDORevisionKey revisionKey);

	/**Processes a detached objects.*/
	protected abstract void processDetached(final CDOIDAndVersion idAndVersion);
	
	/**
	 * Returns with the predicate responsible for filtering out irrelevant components from the {@link #changeSetData change set}.
	 * By default this method returns with {@link IComponentDeltaPredicate#ACCEPT_ALL_PREDICATE the predicate} accepting everything.
	 * <br>Clients may override this method to define custom {@link IComponentDeltaPredicate predicate}.  
	 */
	protected IComponentDeltaPredicate getDeltaPredicate() {
		return IComponentDeltaPredicate.ACCEPT_ALL_PREDICATE;
	}
	
	/**
	 * Post processes the changed components. Does nothing by default.
	 * <br>Clients may override to add some custom behavior.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void postProcess() {
		//does nothing by default.
	}

	/**
	 * Post processes the changed components. Does nothing by default.
	 * <br>Clients may override to add some custom behavior.
	 */
	@OverridingMethodsMustInvokeSuper
	protected void preProcess() {
		//does nothing by default.
	}


	/**Returns with a collection of component (or an empty collection) looked up by its unique terminology specific component ID from the deltas.*/
	@Nullable protected Collection<C> get(final String componentId) {
		return deltas.get(componentId);
	}
	
	/**See {@link Multimap#containsEntry(Object, Object)}.*/
	protected boolean contains(final C delta) {
		return deltas.containsEntry(delta.getId(), delta);
	}
	
	/**See {@link Multimap#remove(Object, Object)}.*/
	protected boolean remove(final C delta) {
		return deltas.remove(delta.getId(), delta);
	}
	
	/**Registers the component delta to the underlying map.
	 * <p>Clients should consider followings:
	 * <ul>
	 * <li>If the underlying map does not contain the specified map, then, this method will return with the specified delta.</li>
	 * <li>If the underlying map already contains the delta, then the delta representing the
	 * more significant {@link ComponentDelta#getChange() change} will be stored and that will be the return value.</li>
	 * <li>If an already stored delta will be replaced with the specified one (since the priority is stronger) 
	 * all {@link ComponentDelta#getComponentChanges() related IDs} will be copied to.</li>
	 * </ul>
	 * </p>
	 * <p>The additional related CDO IDs (if any) will be added to the delta even if a higher priority exists.
	 * @see ChangeKind
	 */
	protected C put(final C delta, final long... relatedCdoIds) {
		
		//check if already a processed element
		if (contains(delta)) {
			
			final Collection<C> storedDeltas = get(delta.getId());
			
			C storedDelta = null;
			
			//required to store component deltas as a collection due to mapping type reference set members
			//where the source and map target terminology component ID could be the same
			//even for different reference set member instances
			for (final Iterator<C> itr = storedDeltas.iterator(); itr.hasNext(); /* */) {
				
				final C next = itr.next();
				
				if (delta.equals(next)) {
					
					storedDelta = next;
					break;
					
				}
				
			}
			
			if (storedDelta.getChange().compareTo(delta.getChange()) > 0) {

				//copy all related component CDO IDs
				delta.getComponentChanges().getIds().addAll(storedDelta.getComponentChanges().getIds());
				delta.getRelatedCdoIds().addAll(newLongSet(relatedCdoIds));
				//remove previous element, as change kind not part of #hashCode we have to explicitly remove the old value
				deltas.remove(delta.getId(), storedDelta);
				//and register the new one
				deltas.put(delta.getId(), delta);
				
				return delta;
				
			} else { //a more significant delta is stored nothing to do but set the related CDO IDs if any
				
				storedDelta.getRelatedCdoIds().addAll(newLongSet(relatedCdoIds));
				return storedDelta;
				
			}
			
		} else {
			
			//manually add additional related CDO IDs if any.
			delta.getRelatedCdoIds().addAll(newLongSet(relatedCdoIds));
			
			//nothing to do, just store the delta
			deltas.put(delta.getId(), delta);
			return delta;
			
		}
		
	}
	
	/**Returns with the code system OID associated with the application specific terminology component ID argument.*/
	protected String getCodeSystemOID(short terminologyComponentId) {
		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return CoreTerminologyBroker.UNSPECIFIED;
		}
		CoreTerminologyBroker terminologyBroker = CoreTerminologyBroker.getInstance();
		return terminologyBroker.getTerminologyOidByTerminologyComponentId(terminologyBroker.getTerminologyComponentId(terminologyComponentId));
	}
	
	/**Returns with the component icon identifier for the given component. May return with an empty string if the component icon ID cannot be found.
	 *<p>This method is responsible to look up component icon ID based on the branch for the given view. If the branch does not exists for a particular 
	 *terminology, this method falls back to the ancestor branch of the given view.*/
	protected String getComponentIconId(final CDOView view, final short terminologyComponentId, final String componentId) {

		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return Strings.nullToEmpty(null);
		}
		
		final IComponentIconIdProvider<String> iconIdProvider = getIconProvider(terminologyComponentId);
		return Strings.nullToEmpty(iconIdProvider.getIconId(createBranchPoint(view, terminologyComponentId), componentId));
		
	}
	
	/**Returns with the component icon identifier for the given component. May return with an empty string if the component icon ID cannot be found.
	 *<p>This method is responsible to look up component icon ID based on the given branch path. If the branch does not exists for a particular 
	 *terminology, this method falls back to the MAIN branch path.*/
	protected String getComponentIconId(final IBranchPath branchPath, final short terminologyComponentId, final String componentId) {

		if (CoreTerminologyBroker.UNSPECIFIED_NUMBER_SHORT == terminologyComponentId) {
			return Strings.nullToEmpty(null);
		}
		
		final IComponentIconIdProvider<String> iconIdProvider = getIconProvider(terminologyComponentId);
		return Strings.nullToEmpty(iconIdProvider.getIconId(createBranchPoint(branchPath, terminologyComponentId), componentId));
		
	}
	
	/**
	 * Returns with the base view for detached objects.
	 */
	protected CDOView getBaseView() {
		return baseView;
	}
	
	/**
	 * Returns with the current view for all new and dirty objects. 
	 */
	protected CDOView getCurrentView() {
		return currentView;
	}
	
	/**
	 * Returns with the change set data.
	 */
	protected CDOChangeSetData getChangeSetData() {
		return changeSetData;
	}

	/**Returns with the branch path where current builder works on.*/
	protected IBranchPath getBranchPath() {
		return branchPathSupplier.get();
	}
	
	/*creates a branch point for the given CDO view. if the branch does not exist for the given component type.
	 *if falls back to the ancestor branch of the given view.*/
	private IBranchPoint createBranchPoint(final CDOView view, final short terminologyComponentId) {
		
		final String repositoryUuid = getRepositoryUuid(terminologyComponentId);
		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryUuid);
		final IBranchPath currentBranchPath = BranchPathUtils.createPath(view);
		final CDOBranch branch = connection.getBranch(currentBranchPath);
		return null == branch ? BranchPointUtils.create(repositoryUuid, currentBranchPath.getParent()) : BranchPointUtils.create(view);
		
	}

	private IBranchPoint createBranchPoint(final IBranchPath branchPath, final short terminologyComponentId) {
		
		final String repositoryUuid = getRepositoryUuid(terminologyComponentId);
		final ICDOConnection connection = getConnectionManager().getByUuid(repositoryUuid);
		final CDOBranch branch = connection.getBranch(branchPath);
		return null == branch ? BranchPointUtils.create(repositoryUuid, createMainPath()) : BranchPointUtils.create(repositoryUuid, branchPath);
		
	}
	
	/*returns with the repository UUID for the given terminology component ID argument.*/
	private String getRepositoryUuid(final short terminologyComponentId) {
		return CodeSystemUtils.getRepositoryUuid(terminologyComponentId);
	}

	/*returns with the connection manager instance.*/
	private ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}

	/*returns with the icon provider service for the given component type*/
	private IComponentIconIdProvider<String> getIconProvider(final short terminologyComponentId) {
		return CoreTerminologyBroker.getInstance().getComponentIconIdProvider(terminologyComponentId);
	}
	
}