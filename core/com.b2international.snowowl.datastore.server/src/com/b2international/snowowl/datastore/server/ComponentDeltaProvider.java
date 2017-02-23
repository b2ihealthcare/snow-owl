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

import static org.slf4j.LoggerFactory.getLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;

import bak.pcj.LongCollection;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.CoreTerminologyBroker;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.core.api.IBranchPoint.NullBranchPoint;
import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.CDOIDUtils;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.delta.ComponentDelta;
import com.b2international.snowowl.datastore.delta.IBranchPointCalculationStrategy;
import com.b2international.snowowl.datastore.delta.IChangedComponentCDOIDs;
import com.b2international.snowowl.datastore.delta.IComponentDeltaBuilder;
import com.b2international.snowowl.datastore.delta.IComponentDeltaProvider;
import com.b2international.snowowl.datastore.history.HistoryInfoAdapter;
import com.b2international.snowowl.datastore.history.HistoryInfoDetailsBuilder;
import com.b2international.snowowl.datastore.history.NullHistoryInfoConfiguration;
import com.b2international.snowowl.datastore.server.history.HistoryInfoDetailsBuilderProvider;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Abstract base class for {@link ComponentDelta component delta} providers.
 * 
 */
public abstract class ComponentDeltaProvider<C extends ComponentDelta> implements IComponentDeltaProvider<C> {

	private static final Logger LOGGER = getLogger(ComponentDeltaProvider.class);
	
	@Override
	public Collection<C> getComponentDeltas(final IBranchPointCalculationStrategy strategy) {
		
		Preconditions.checkNotNull(strategy, "Strategy argument cannot be null.");
		
		final IBranchPoint sourceBranchPoint = Preconditions.checkNotNull(strategy.getSourceBranchPoint(), "Source branch point cannot be null.");
		final IBranchPoint targetBranchPoint = Preconditions.checkNotNull(strategy.getTargetBranchPoint(), "Target branch point cannot be null.");
		
		//source does not exist (yet)
		if (NullBranchPoint.INSTANCE.equals(sourceBranchPoint)) {
			
			return Collections.emptySet();
			
		}
		
		//target does not exist (yet)
		if (NullBranchPoint.INSTANCE.equals(targetBranchPoint)) {
			
			return Collections.emptySet();
			
		}
		
		final IBranchPath targetBranchPath = strategy.getTargetBranchPoint().getBranchPath();
		
		final ICDOConnection connection = getConnectionManager().get(strategy);
		final CDOBranch branch = connection.getBranch(targetBranchPath);
		if (null == branch) {
			
			return Collections.emptySet();
			
		}

		//no changes on branch nothing to calculate
		if (Long.MIN_VALUE == CDOServerUtils.getLastCommitTime(branch)) {
			
			return Collections.emptySet();
			
		}
		
		final CDOChangeSetData changeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);
		Preconditions.checkNotNull(changeSetData, "Change set data was null.");
		
		
		final CDOBranchPoint cdoSourceBranchPoint = BranchPointUtils.convert(sourceBranchPoint);
		final CDOBranchPoint cdoTargetBranchPoint = BranchPointUtils.convert(targetBranchPoint);

		if (!changeSetData.isEmpty()) {

			final CDOView currentView = connection.createView(cdoTargetBranchPoint.getBranch());
			final CDOView baseView = connection.createView(cdoSourceBranchPoint.getBranch(), sourceBranchPoint.getTimestamp());

			try {
				
				final IComponentDeltaBuilder<C> deltaBuilder = createComponentDeltaBuilder();
				final Collection<C> processChanges = deltaBuilder.processChanges(changeSetData, baseView, currentView);
				return HashMultiset.create(processChanges);
				
			} finally {
				
				LifecycleUtil.deactivate(baseView);
				LifecycleUtil.deactivate(currentView);
				
			}
		}
		
		return Collections.emptySet();
		
	}
	
	public Collection<IHistoryInfo> getChangesForComponent(final IBranchPointCalculationStrategy strategy, final IChangedComponentCDOIDs delta) {
		
		Preconditions.checkNotNull(strategy, "Strategy argument cannot be null.");
		Preconditions.checkNotNull(delta, "Changed component IDs argument cannot be null.");
		
		return collectChangesForComponent(strategy, delta);
	}
	
	/**
	 * Returns with a collection of {@link IHistoryInfo history info} representing all the changes made on a component
	 * given by its repository specific storage key (CDO ID).
	 * @param strategy strategy providing the two branch points where calculation has to be provided.
	 * @param delta representing the changed component.
	 * @return the a collection of history info.
	 */
	public static Collection<IHistoryInfo> collectChangesForComponent(final IBranchPointCalculationStrategy strategy, final IChangedComponentCDOIDs delta) {
		
		Preconditions.checkNotNull(strategy, "Strategy argument cannot be null.");
		
		final IBranchPoint sourceBranchPoint = Preconditions.checkNotNull(strategy.getSourceBranchPoint(), "Source branch point cannot be null.");
		final IBranchPoint targetBranchPoint = Preconditions.checkNotNull(strategy.getTargetBranchPoint(), "Target branch point cannot be null.");
		
		final ICDOConnection connection = getConnectionManager().get(strategy);
		
		final CDOChangeSetData changeSetData = CDOChangeSetDataProvider.INSTANCE.getChangeSetData(strategy);
		Preconditions.checkNotNull(changeSetData, "Change set data was null.");
	
		final CDOBranchPoint cdoSourceBranchPoint = BranchPointUtils.convert(sourceBranchPoint);
		final CDOBranchPoint cdoTargetBranchPoint = BranchPointUtils.convert(targetBranchPoint);
		
		if (!changeSetData.isEmpty()) {
	
			final CDOView currentView = connection.createView(cdoTargetBranchPoint.getBranch(), targetBranchPoint.getTimestamp());
			final CDOView baseView = connection.createView(cdoSourceBranchPoint.getBranch(), sourceBranchPoint.getTimestamp());
	
			try {
				
				final CDOObject object;
				
				//detached object
				if (CDOIDUtils.extractIds(changeSetData.getDetachedObjects()).contains(CDOIDUtil.createLong(delta.getCdoId()))) {
					
					object = CDOUtils.getObjectIfExists(baseView, delta.getCdoId());
					
				} else {
					
					object = CDOUtils.getObjectIfExists(currentView, delta.getCdoId());
					
				}
				
				if (object == null) {
					LOGGER.warn("Could not found CDO object with ID: {0} for not empty change set data between {1} and {2} branch points.", delta.getCdoId(), sourceBranchPoint, targetBranchPoint);
					return Collections.emptySet();
				}
				
				final CDOCommitInfo commitInfo = createCommitInfo(cdoTargetBranchPoint.getBranch(), cdoTargetBranchPoint.getTimeStamp(), cdoSourceBranchPoint.getTimeStamp(), changeSetData);
				
				final CDOCommitInfo filteredCommitInfo = new FilteredCommitInfo(commitInfo, delta);
				final CoreTerminologyBroker terminologyBroker = CoreTerminologyBroker.getInstance();
				final String terminologyComponentId = terminologyBroker.getTerminologyComponentId(object);
				final HistoryInfoDetailsBuilder detailsBuilder = HistoryInfoDetailsBuilderProvider.INSTANCE.getBuilder(terminologyComponentId);
				final Collection<IHistoryInfoDetails> details = detailsBuilder.buildDetails(currentView, baseView, filteredCommitInfo, NullHistoryInfoConfiguration.INSTANCE);
				
				return Collections.singleton(createHistoryInfo(details));
				
			} finally {
				
				LifecycleUtil.deactivate(baseView);
				LifecycleUtil.deactivate(currentView);
				
			}
		}
		
		return Collections.emptySet();
		
	}

	/**
	 * Creates and returns a {@link ComponentDeltaBuilder} object.
	 * 
	 * @return a new component delta builder object
	 */
	protected abstract IComponentDeltaBuilder<C> createComponentDeltaBuilder();
	
	/**Detaches the specified objects via the CDO state machine.*/
	@SuppressWarnings("restriction")
	protected void detachObject(final CDOObject cdoObject) {
		org.eclipse.emf.internal.cdo.view.CDOStateMachine.INSTANCE.detach((InternalCDOObject) cdoObject);
	}

	/**Returns with the revision manager associated with the {@link EPackage package} argument.*/
	protected InternalCDORevisionManager getRevisionManager(final EPackage ePackage) {
		return getSession(ePackage).getRevisionManager();
	}

	/*creates a commit info based on the specified configurations*/
	private static CDOCommitInfo createCommitInfo(final CDOBranch branch, final long timeStamp, final long previousTimeStamp, final CDOChangeSetData changeSetData) {
		
		final ICDOConnection connection = getConnectionManager().get(branch);
		final CDONet4jSession session = connection.getSession();
		final InternalCDOCommitInfoManager commitInfoManager = (InternalCDOCommitInfoManager) session.getCommitInfoManager();

		return commitInfoManager.createCommitInfo(
				branch, 
				timeStamp, 
				previousTimeStamp, 
				null, //user ID (intentionally ignored )
				null, //commit comment (intentionally ignored)
				createCommitData(changeSetData));
	}
	
	/*transforms the change set data into commit data*/
	@SuppressWarnings("restriction")
	private static CDOCommitData createCommitData(final CDOChangeSetData changeSetData) {
		
		return new org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl(
				Collections.<CDOPackageUnit>emptyList(), 
				changeSetData.getNewObjects(), 
				changeSetData.getChangedObjects(), 
				changeSetData.getDetachedObjects());
		
	}

	/*creates a history info instance with the given details.*/
	private static IHistoryInfo createHistoryInfo(final Collection<? extends IHistoryInfoDetails> details) {
		return new HistoryInfoAdapter(details);
	}
	
	
	/*returns with the CDO session*/
	private InternalCDOSession getSession(final EPackage ePackage) {
		return (InternalCDOSession) getConnectionManager().get(ePackage).getSession();
	}

	/*returns with the connection manager*/
	private static ICDOConnectionManager getConnectionManager() {
		return ApplicationContext.getInstance().getService(ICDOConnectionManager.class);
	}


	@SuppressWarnings("restriction")
	private static final class FilteredCommitInfo extends org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo {
		
		private final CDOCommitInfo delegate;
		private final Predicate<CDOIDAndVersion> predicate;

		private FilteredCommitInfo(final CDOCommitInfo delegate, final IChangedComponentCDOIDs delta) {
			this.delegate = delegate;
			predicate = new CDOIDFilterPredicate(delta.getRelatedCdoIds());
		}

		/* (non-Javadoc)
		 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getDelegate()
		 */
		@Override
		protected CDOCommitInfo getDelegate() {
			return delegate;
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getChangedObjects()
		 */
		@Override
		public List<CDORevisionKey> getChangedObjects() {
			return Lists.newArrayList(Iterables.filter(delegate.getChangedObjects(), predicate));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getNewObjects()
		 */
		@Override
		public List<CDOIDAndVersion> getNewObjects() {
			return Lists.newArrayList(Iterables.filter(delegate.getNewObjects(), predicate));
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo#getDetachedObjects()
		 */
		@Override
		public List<CDOIDAndVersion> getDetachedObjects() {
			return Lists.newArrayList(Iterables.filter(delegate.getDetachedObjects(), predicate));
		}
		
		/**Predicate for filtering {@link CDOIDAndVersion} based on containment for a given long set.*/
		private static final class CDOIDFilterPredicate implements Predicate<CDOIDAndVersion> {
			
			private final LongCollection ids;

			private CDOIDFilterPredicate(final LongCollection ids) {
				this.ids = ids;
			}
			
			/* (non-Javadoc)
			 * @see com.google.common.base.Predicate#apply(java.lang.Object)
			 */
			@Override
			public boolean apply(final CDOIDAndVersion input) {
				return ids.contains(CDOIDUtils.asLong(input.getID()));
			}
			
		}
		
		
	}

	
}