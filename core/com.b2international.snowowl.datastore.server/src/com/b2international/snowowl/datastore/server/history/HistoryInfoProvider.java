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
import static com.b2international.snowowl.datastore.history.NullHistoryInfoConfiguration.isNullConfiguration;
import static com.b2international.snowowl.datastore.server.CDOServerUtils.getAccessor;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;
import static java.util.Collections.emptyList;
import static org.eclipse.emf.cdo.common.branch.CDOBranchPoint.UNSPECIFIED_DATE;
import static org.eclipse.emf.cdo.server.StoreThreadLocal.release;
import static org.eclipse.emf.cdo.server.StoreThreadLocal.setAccessor;
import static org.eclipse.net4j.util.lifecycle.LifecycleUtil.deactivate;

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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;
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

import com.b2international.commons.CancelableProgressMonitorWrapper;
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
import com.b2international.snowowl.datastore.history.Version;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Longs;

/**
 * Low-level, stateless service singleton for providing historical information for 
 * a terminology or content independent component. 
 * 
 *
 */
public enum HistoryInfoProvider {

	/**Shared service singleton.*/
	INSTANCE;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryInfoProvider.class);
	
	// Absolute time limit for determining history details in milliseconds
	private static final long DEFAULT_TIMEOUT = 15000;
	
	/**
	 * Returns with the historical information of a terminology independent component.
	 * @param configuration for running the query.
	 * @return a collection of historical information for a component.
	 * @throws SnowowlServiceException 
	 */
	public Collection<IHistoryInfo> getHistoryInfo(final HistoryInfoConfiguration configuration) throws SnowowlServiceException {
		
		if (isNullConfiguration(configuration)) {
			return emptyList();
		}
		
		checkNotNull(configuration, "configuration");
		
		CDOView view = null;
		
		try {
			
			setAccessor(getAccessor(configuration.getStorageKey()));
			view = openView(configuration);
			
			try (
					final InternalHistoryInfoConfiguration internalConfiguration = //
							new InternalHistoryInfoConfigurationImpl(configuration, getConnection(), view);
			) {

				return getHistoryInfo(internalConfiguration, DEFAULT_TIMEOUT);
				
			} catch (final SQLException e) {
				final String msg = "Error while trying to get history for component: '" + configuration.getStorageKey() + "'.";
				LOGGER.error(msg, e);
				throw new SnowowlServiceException(msg, e);
			}
			
		} finally {
			deactivate(view);
			release();
		}
		
	}
	
	public List<IHistoryInfo> getHistoryInfo(final InternalHistoryInfoConfiguration configuration, final long timeout) throws SnowowlServiceException, SQLException {

		final long id = configuration.getStorageKey();
		
		if (!checkId(id)) {
			return emptyList();
		}
		
		final long startTime = System.currentTimeMillis();
		final CDOID cdoId = CDOIDUtil.createLong(id);
		final NullProgressMonitor monitor = new NullProgressMonitor();
		final SubMonitor subMonitor = SubMonitor.convert(new CancelableProgressMonitorWrapper(monitor), 100);

		try {
			
			final CDOView view = configuration.getView();
			final CDOBranch branch = view.getBranch();
			
			// If the object has been deleted return with DetachedHistoryInfo
			//XXX consider using com.b2international.snowowl.datastore.server.CDOServerUtils.getObjectRevisions(CDOBranchPoint, CDOID, int)
			if (view.getRevision(cdoId) == null) {
				return Collections.<IHistoryInfo>singletonList(new DetachedHistoryInfo(branch, cdoId));
			}
			
			final HistoryInfoQueryExecutor executor = HistoryInfoQueryExecutorProvider.INSTANCE.getExecutor(configuration.getTerminologyComponentId());
	
			final Stack<HistoryInfo> infos = new Stack<HistoryInfo>();
			final Map<Long, IVersion<CDOID>> query;
			
			// Try to find details for most recent commits first
			final Map<Long, IVersion<CDOID>> entries = executor.execute(configuration);
			query = ImmutableSortedMap.copyOf(entries, Ordering.natural().reverse());
			
			int itemCount = 0;
			
			for (final IVersion<CDOID> version : query.values()) {
				itemCount += version.getAffectedObjectIds().size();
			}
			
			subMonitor.setWorkRemaining(itemCount);
			
			
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
				
				subMonitor.worked(entry.getValue().getAffectedObjectIds().size());
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
			monitor.done();
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
				beforeView = connection.createView(branch, commitInfo.getPreviousTimeStamp(), incomplete);
				
				final HistoryInfoDetailsBuilder builder = // 
						HistoryInfoDetailsBuilderProvider.INSTANCE.getBuilder(configuration.getTerminologyComponentId());
				details.addAll(builder.buildDetails(currentView, beforeView, commitInfo)); 
			}
			
		} finally {
			
			LifecycleUtil.deactivate(currentView);
			LifecycleUtil.deactivate(beforeView);
		}
		return new HistoryInfo(timeStamp, version, author, comments, incomplete, details);
	}

	private PreparedStatement prepareStatement(final InternalHistoryInfoConfiguration configuration, final Long timeStamp) throws SQLException {
		final PreparedStatement statement = configuration.getConnection().prepareStatement(DatastoreQueries.SQL_GET_COMMIT_INFO_DATA);
		statement.setLong(1, timeStamp.longValue());
		statement.setInt(2, configuration.getBranchId());
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
			CDOBranchPoint branchPoint = branch.getPoint(commitTime);
			
			while (commitTime < branchPoint.getBranch().getBase().getTimeStamp()) {
				branchPoint = branch.getBase().getBranch().getPoint(commitTime);
			}
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
				final CDORevision previousRevision = revisionManager.getRevision(cdoId, branchPoint.getBranch().getPoint(branchPoint.getTimeStamp() - 1), CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
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
	
	private CDOView openView(final HistoryInfoConfiguration configuration) {
		final long cdoId = configuration.getStorageKey();
		final ICDOConnection connection = getServiceForClass(ICDOConnectionManager.class).get(cdoId);
		final CDOBranch branch = connection.getBranch(configuration.getBranchPath());
		return connection.createView(branch, UNSPECIFIED_DATE, false);
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