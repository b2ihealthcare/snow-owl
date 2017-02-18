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
package com.b2international.snowowl.datastore.server.history;

import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.checkId;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.Pair;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IHistoryInfo;
import com.b2international.snowowl.core.api.IHistoryInfo.IVersion;
import com.b2international.snowowl.core.api.IHistoryInfoDetails;
import com.b2international.snowowl.core.api.SnowowlServiceException;
import com.b2international.snowowl.datastore.DatastoreQueries;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.cdo.ICDORepositoryManager;
import com.b2international.snowowl.datastore.history.DetachedHistoryInfo;
import com.b2international.snowowl.datastore.history.HistoryInfo;
import com.b2international.snowowl.datastore.history.HistoryInfoConfiguration;
import com.b2international.snowowl.datastore.history.HistoryInfoDetailsBuilder;
import com.b2international.snowowl.datastore.history.NullHistoryInfoConfiguration;
import com.b2international.snowowl.datastore.history.Version;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Service singleton which provides historical information for a component. 
 */
public enum HistoryInfoProvider {

	INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryInfoProvider.class);
	
	private static final long DEFAULT_TIMEOUT = TimeUnit.SECONDS.toMillis(15L);
	
	/**
	 * Computes history for the specified history configuration, within the time limits of {@link #DEFAULT_TIMEOUT}.
	 * 
	 * @param configuration the history configuration object (may not be {@code null})
	 * @return a collection of history information rows describing changes to the component itself, as well as any
	 * closely related components
	 * @throws SnowowlServiceException if history can not be retrieved for some reason (including integrity issues
	 * related to historical data)
	 */
	public Collection<IHistoryInfo> getHistoryInfo(final CDOView view, final HistoryInfoConfiguration configuration) throws SnowowlServiceException {
		return getHistoryInfo(view, configuration, DEFAULT_TIMEOUT);
	}
	
	/**
	 * Computes history for the specified history configuration and timeout.
	 * 
	 * @param configuration the history configuration object (may not be {@code null})
	 * @param timeout the maximum collection time which should be spent on computing history
	 * @return a collection of history information rows describing changes to the component itself, as well as any
	 * closely related components
	 * @throws SnowowlServiceException if history can not be retrieved for some reason (including integrity issues
	 * related to historical data)
	 */
	public Collection<IHistoryInfo> getHistoryInfo(final CDOView view, final HistoryInfoConfiguration configuration, final long timeout) throws SnowowlServiceException {
		checkNotNull(configuration, "History configuration object may not be null.");
		
		if (NullHistoryInfoConfiguration.isNullConfiguration(configuration)) {
			return ImmutableList.of();
		}
		
		try (final InternalHistoryInfoConfiguration internalConfiguration = new InternalHistoryInfoConfigurationImpl(configuration, getConnection(), view)) {
			return getHistoryInfo(internalConfiguration, timeout);
		} catch (final SQLException e) {
			final String msg = "Error while trying to get history for component: '" + configuration.getStorageKey() + "'.";
			LOGGER.error(msg, e);
			throw new SnowowlServiceException(msg, e);
		}
	}
	
	private List<IHistoryInfo> getHistoryInfo(final InternalHistoryInfoConfiguration configuration, final long timeout) throws SnowowlServiceException, SQLException {

		final long id = configuration.getStorageKey();
		
		if (!checkId(id)) {
			return ImmutableList.of();
		}
		
		final long startTime = System.currentTimeMillis();
		final CDOID cdoId = CDOIDUtil.createLong(id);

		try {
			
			final CDOView view = configuration.getView();
			final CDOBranch branch = view.getBranch();
			
			// If the object has been deleted return with DetachedHistoryInfo
			//XXX consider using com.b2international.snowowl.datastore.server.CDOServerUtils.getObjectRevisions(CDOBranchPoint, CDOID, int)
			if (view.getRevision(cdoId) == null) {
				return Collections.<IHistoryInfo>singletonList(new DetachedHistoryInfo(branch, cdoId));
			}
			
			final Stack<HistoryInfo> infos = new Stack<HistoryInfo>();
			final Map<Long, IVersion<CDOID>> query;
			
			// Try to find details for most recent commits first
			final HistoryInfoQueryExecutor executor = HistoryInfoQueryExecutorProvider.INSTANCE.getExecutor(configuration.getTerminologyComponentId());
			final Map<Long, IVersion<CDOID>> entries = executor.execute(configuration);

			query = ImmutableSortedMap.copyOf(entries, Ordering.natural().reverse());
			
			for (final Entry<Long, IVersion<CDOID>> entry : query.entrySet()) {
				final HistoryInfo historyInfo = createHistoryInfo(startTime, configuration, entry, timeout);
				
				// Ignore empty history infos, but add incomplete ones
				if (historyInfo.isIncomplete() || !historyInfo.getDetails().isEmpty()) {
					
					if (infos.empty()) {

						infos.push(historyInfo);
						
					} else {
						
						//try merge
						final HistoryInfo previousItem = infos.pop();
						
						final boolean success = previousItem.group(historyInfo);
						infos.push(previousItem);
						
						if (!success) {
							infos.push(historyInfo);
						}
						
					}
					
				}
			}
			
			int nextMinorVersion = 1;
			int nextMajorVersion = 1;
			
			final List<IHistoryInfo> $ = Lists.newArrayList();
			
			// Renumber remaining versions, process in chronological order
			while (!infos.isEmpty()) {
				
				final HistoryInfo info = infos.pop();
				final Version version = (Version) info.getVersion();
				
				if (version.representsMajorChange()) {
					version.setMajorVersion(nextMajorVersion);
					
					nextMajorVersion++;
					nextMinorVersion = 1;
					
				} else {
					
					// Use the previous major version when renumbering minor versions
					version.setMajorVersion(nextMajorVersion - 1);
					version.setMinorVersion(nextMinorVersion);

					nextMinorVersion++;
				}
				
				$.add(info);
			}
			
			return $;
			
		} catch (final OperationCanceledException e) {
			return emptyList();
		} finally {
			new NullProgressMonitor().done();
		}
	}

	private HistoryInfo createHistoryInfo(final long startTime, final InternalHistoryInfoConfiguration configuration, final Entry<Long, IVersion<CDOID>> entry, final long timeout) throws SQLException {
		final Long timeStamp = getPreviousAndCurrentTimestamps(entry.getValue().getAffectedObjectIds().values()).getB();
		final CDOBranch branch = configuration.getView().getBranch();
		final IVersion<CDOID> version = entry.getValue();
		final ICDOConnection connection = ApplicationContext.getInstance().getService(ICDOConnectionManager.class).get(configuration.getStorageKey());
		String author = null;
		String comments = null;
		boolean incomplete = false;
		final List<IHistoryInfoDetails> details = Lists.newArrayList();
		
		try (
			final PreparedStatement statement = prepareStatement(configuration, timeStamp);
			final ResultSet resultSet = statement.executeQuery();
		) {
			final boolean hasResult = resultSet.next();
			if (!hasResult) {
				LOGGER.warn("No commit info found with timestamp '" + timeStamp + "' in branch " + branch.getName());
				return null;
			}
			
			author = String.valueOf(resultSet.getObject(1));
			comments = String.valueOf(resultSet.getObject(2));
		} 
		
		if (System.currentTimeMillis() - startTime > timeout) {
			incomplete = true;
			return new HistoryInfo(timeStamp, version, author, comments, incomplete);
		}
		
		CDOView currentView = null;
		CDOView beforeView = null;
		
		try {
			
			final CDOCommitInfo commitInfo = HistoryInfoProvider.getChangesInBranch(configuration.getStorageKey(), branch, version.getAffectedObjectIds());
			
			if (commitInfo != null) {
				
				currentView = connection.createView(branch, commitInfo.getTimeStamp(), false);
				
				final CDOBranchPoint beforeBranchPoint = getAdjustedBranchPoint(branch, commitInfo.getPreviousTimeStamp());
				beforeView = connection.createView(beforeBranchPoint.getBranch(), beforeBranchPoint.getTimeStamp(), incomplete);
				
				final HistoryInfoDetailsBuilder builder = // 
						HistoryInfoDetailsBuilderProvider.INSTANCE.getBuilder(configuration.getTerminologyComponentId());
				details.addAll(builder.buildDetails(currentView, beforeView, commitInfo, configuration)); 
			}
			
		} finally {
			LifecycleUtil.deactivate(currentView);
			LifecycleUtil.deactivate(beforeView);
		}

		return new HistoryInfo(timeStamp, version, author, comments, incomplete, details);
	}

	private PreparedStatement prepareStatement(final InternalHistoryInfoConfiguration configuration, final Long timeStamp) throws SQLException {
		final PreparedStatement statement = configuration.getConnection().prepareStatement(DatastoreQueries.SQL_GET_COMMIT_INFO_DATA.getQuery());
		statement.setLong(1, timeStamp.longValue());
		return statement;
	}

	private static Pair<Long, Long> getPreviousAndCurrentTimestamps(final Collection<Long> longs) {
		final long[] timestamps = Longs.toArray(longs);
		final long timestamp = Longs.max(timestamps);
		final long previousTimestamp = Longs.min(timestamps) - 1L;
		return Pair.of(previousTimestamp, timestamp);
	}
	
	private static CDOCommitInfo getChangesInBranch(final long storageKey, final CDOBranch branch, final Map<CDOID, Long> map) {

		final Pair<Long, Long> previousAndCurrentTimestamps = getPreviousAndCurrentTimestamps(map.values());
		final long previousTimestamp = previousAndCurrentTimestamps.getA();
		final long timestamp = previousAndCurrentTimestamps.getB();
		
		final IRepository repository = getServiceForClass(ICDORepositoryManager.class).get(storageKey).getRepository();
		final CDORevisionManager revisionManager = repository.getRevisionManager();
    	
		final Map<CDOID, CDORevision> affectedRevisions = Maps.newHashMap();
		final Map<CDOID, CDOBranchPoint> idBranchPoint = newHashMap();
		
		for (final Entry<CDOID, Long> entry : map.entrySet()) {
			final long commitTime = entry.getValue();
			final CDOID cdoid = entry.getKey();
			final CDOBranchPoint branchPoint = getAdjustedBranchPoint(branch, commitTime);
			idBranchPoint.put(cdoid, branchPoint);
			affectedRevisions.put(cdoid, revisionManager.getRevision(cdoid, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true));
		}    			
		
		final List<CDOIDAndVersion> newObjects = Lists.newArrayList();
	    final List<CDORevisionKey> changedObjects = Lists.newArrayList();
	    final List<CDOIDAndVersion> detachedObjects = Lists.newArrayList();
	    
    	for (final Entry<CDOID, CDORevision> entry : affectedRevisions.entrySet()) {

    		if (entry.getValue() == null) {
    			final CDOID cdoId = entry.getKey();
    			final CDOBranchPoint branchPoint = idBranchPoint.get(cdoId);
    			final CDOBranchPoint previousBranchPoint = getAdjustedBranchPoint(branchPoint.getBranch(), branchPoint.getTimeStamp() - 1L);
				final CDORevision previousRevision = revisionManager.getRevision(cdoId, previousBranchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
				detachedObjects.add(previousRevision);
				continue;
    		}
    		
    		if (entry.getValue().getVersion() == CDORevision.FIRST_VERSION) {
    			
    			final CDORevision previousRevision = getPreviousRevision(revisionManager, entry.getValue());
    			
    			if (previousRevision != null) {
    				registerChange(entry.getValue(), previousRevision, changedObjects);
    			} else {
    				newObjects.add(entry.getValue());	
    			}
    			
    		} else {
    			
    			final CDORevision previousRevision = getPreviousVersion(revisionManager, entry.getValue());
    			if (null != previousRevision) { //guard against NPE when resurrecting deleted concept. FIXME
    				registerChange(entry.getValue(), previousRevision, changedObjects);
    			}
    		}
		}
    	
    	if (newObjects.isEmpty() && changedObjects.isEmpty() && detachedObjects.isEmpty()) {
    		return null;
    	}

    	final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).get(storageKey);
		final CDOCommitInfoManager commitInfoManager = connection.getSession().getCommitInfoManager();

		@SuppressWarnings("restriction")
		final
		CDOCommitInfo commitInfo = ((InternalCDOCommitInfoManager) commitInfoManager).createCommitInfo(
				branch, 
				timestamp, 
				previousTimestamp, 
				null, 
				null,
				new org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl(
						new ArrayList<CDOPackageUnit>(), 
						newObjects, 
						changedObjects, 
						detachedObjects));
		
		return commitInfo;
	}

	/*
	 * XXX: Returns a CDOBranchPoint with the actual branch the specified timestamp belongs to. This is required because
	 * certain parts of CDO truncate the untreated branch point to branch base points if the timestamp is before the
	 * branch start point.
	 */
	private static CDOBranchPoint getAdjustedBranchPoint(final CDOBranch branch, final long timeStamp) {
		CDOBranchPoint branchPoint = branch.getPoint(timeStamp);
		
		while (!branchPoint.getBranch().isMainBranch() && branchPoint.getTimeStamp() < branchPoint.getBranch().getBase().getTimeStamp()) {
			branchPoint = branchPoint.getBranch().getBase().getBranch().getPoint(branchPoint.getTimeStamp());
		}
		
		return branchPoint;
	}
	
	private Connection getConnection() {
		return ((IDBStoreAccessor) StoreThreadLocal.getAccessor()).getConnection();
	}
	
	private static CDORevision getPreviousVersion(final CDORevisionManager revisionManager, final CDORevision affectedRevision) {

		final CDOBranchVersion previousBranchVersion = affectedRevision.getBranch().getVersion(affectedRevision.getVersion() - 1);
		final CDORevision previousRevision = revisionManager.getRevisionByVersion(affectedRevision.getID(), previousBranchVersion, 
				CDORevision.UNCHUNKED, true);
		
		return previousRevision;
	}
	
	private static void registerChange(final CDORevision affectedRevision, final CDORevision previousRevision, final List<CDORevisionKey> changedObjects) {

		// Remove references to lists, as changes related to referred items will be handled individually
		final CDORevision trimmedAffectedRevision = removeManyValuedReferences(affectedRevision);
		final CDORevision trimmedPreviousRevision = removeManyValuedReferences(previousRevision);
		changedObjects.add(trimmedAffectedRevision.compare(trimmedPreviousRevision));
	}
	
	private static CDORevision getPreviousRevision(final CDORevisionManager revisionManager, final CDORevision source) {
    	
		CDOBranchPoint parentBranchPoint = source.getBranch().getBase();
		
		while (parentBranchPoint.getBranch() != null) {
			
			final CDORevision previousRevision = revisionManager.getRevision(source.getID(), parentBranchPoint, CDORevision.UNCHUNKED, 
					CDORevision.DEPTH_NONE, true);
			
			if (previousRevision != null) {
				return previousRevision;
			}
			
			parentBranchPoint = parentBranchPoint.getBranch().getBase();
		}

		return null;
    }
	
	private static CDORevision removeManyValuedReferences(final CDORevision source) {

		final CDORevision revisionCopy = source.copy();

		for (final EStructuralFeature ref : CDOModelUtil.getAllPersistentFeatures(revisionCopy.getEClass())) {
			if (ref.isMany()) {
				((InternalCDORevision) revisionCopy).setValue(ref, null);
			}
		}

		return revisionCopy;
	}
}
