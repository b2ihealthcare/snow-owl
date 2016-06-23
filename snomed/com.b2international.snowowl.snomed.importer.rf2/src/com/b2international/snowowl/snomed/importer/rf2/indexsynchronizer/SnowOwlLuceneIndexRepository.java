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
package com.b2international.snowowl.snomed.importer.rf2.indexsynchronizer;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitInfoImpl;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.CommitContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.CDOReplicationContext;
import org.eclipse.emf.cdo.spi.common.CDOReplicationInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalCommitManager;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalQueryManager;
import org.eclipse.emf.cdo.spi.server.InternalRepositorySynchronizer;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalStore;
import org.eclipse.emf.cdo.spi.server.InternalSynchronizableRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.LockObjectsResult;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.UnlockObjectsResult;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.core.api.IBranchPoint;
import com.b2international.snowowl.datastore.BranchPathUtils;
import com.b2international.snowowl.datastore.BranchPointUtils;
import com.b2international.snowowl.datastore.cdo.ICDOConnection;
import com.b2international.snowowl.datastore.cdo.ICDOConnectionManager;
import com.b2international.snowowl.datastore.server.CDOChangeProcessorFactoryManager;

/**
 * Snow Owl's Lucene index-based repository responsible for recreating the index
 * based on the based on the CDO store.
 */
public class SnowOwlLuceneIndexRepository implements InternalSynchronizableRepository {

	private State state = State.OFFLINE;
	int counter = 0;

	@Override
	public void handleCommitInfo(CDOCommitInfo commitInfo) {

		String repositoryName = "snomedStore";
		ICDOConnectionManager connectionManager = ApplicationContext.getServiceForClass(ICDOConnectionManager.class);
		ICDOConnection connection = connectionManager.getByUuid(repositoryName);

		List<CDOIDAndVersion> newObjects = commitInfo.getNewObjects();
		System.out.println("New objects size: " + newObjects.size());
		
		for (CDOIDAndVersion cdoidAndVersion : newObjects) {
			InternalCDORevision cdoRevision = (InternalCDORevision) cdoidAndVersion;
			IBranchPath branchPath = BranchPathUtils.createPath(cdoRevision.getBranch());
			IBranchPoint branchPoint = BranchPointUtils.create(connection, branchPath, cdoRevision.getTimeStamp());
			
			CDOView cdoView = connection.createView(branchPoint);
			CDOObject object = cdoView.getObject(cdoRevision.getID());
			System.out.println("CDO Object: " + object);
			
		}
		
;/*
		SnomedOntology snomedOntology = new SnomedOntology(commitInfo);
		snomedOntology.preProcessCommitInfo();
		snomedOntology.processRevisions();
		snomedOntology.createDocuments();
		*/
		
		
		
	}
	
	@Override
	public String[] getLockAreaIDs() {
		return new String[] {};
	}

	@Override
	public int getLastReplicatedBranchID() {
		// no branch has been synched
		return -1;
	}

	@Override
	public long getLastReplicatedCommitTime() {
		return 0;
	}

	@Override
	public void goOnline() {
	}

	@Override
	public void goOffline() {
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}

	@Override
	public IQueryHandlerProvider getQueryHandlerProvider() {
		return null;
	}

	@Override
	public long getLastCommitTimeStamp() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.server.IRepository#waitForCommit(long)
	 */
	@Override
	public long waitForCommit(long timeout) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.server.IRepository#validateTimeStamp(long)
	 */
	@Override
	public void validateTimeStamp(long timeStamp) throws IllegalArgumentException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.server.IRepository#getCommitInfoHandlers()
	 */
	@Override
	public CDOCommitInfoHandler[] getCommitInfoHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addCommitInfoHandler(CDOCommitInfoHandler handler) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.server.IRepository#removeCommitInfoHandler(org.
	 * eclipse.emf.cdo.common.commit.CDOCommitInfoHandler)
	 */
	@Override
	public void removeCommitInfoHandler(CDOCommitInfoHandler handler) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.server.IRepository#getHandlers()
	 */
	@Override
	public Set<Handler> getHandlers() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.server.IRepository#addHandler(org.eclipse.emf.cdo.
	 * server.IRepository.Handler)
	 */
	@Override
	public void addHandler(Handler handler) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.server.IRepository#removeHandler(org.eclipse.emf.cdo.
	 * server.IRepository.Handler)
	 */
	@Override
	public void removeHandler(Handler handler) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.server.IRepository#setInitialPackages(org.eclipse.emf
	 * .ecore.EPackage[])
	 */
	@Override
	public void setInitialPackages(EPackage... initialPackages) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getName()
	 */
	@Override
	public String getName() {
		return "Dummy Index store";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getUUID()
	 */
	@Override
	public String getUUID() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getType()
	 */
	@Override
	public Type getType() {
		// TODO Auto-generated method stub
		return Type.BACKUP;
	}

	@Override
	public State getState() {
		return state;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getCreationTime()
	 */
	@Override
	public long getCreationTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getStoreType()
	 */
	@Override
	public String getStoreType() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getObjectIDTypes()
	 */
	@Override
	public Set<ObjectType> getObjectIDTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.common.CDOCommonRepository#getIDGenerationLocation()
	 */
	@Override
	public IDGenerationLocation getIDGenerationLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#getRootResourceID()
	 */
	@Override
	public CDOID getRootResourceID() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#isSupportingAudits()
	 */
	@Override
	public boolean isSupportingAudits() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.common.CDOCommonRepository#isSupportingBranches()
	 */
	@Override
	public boolean isSupportingBranches() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#isSupportingEcore()
	 */
	@Override
	public boolean isSupportingEcore() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.CDOCommonRepository#
	 * isEnsuringReferentialIntegrity()
	 */
	@Override
	public boolean isEnsuringReferentialIntegrity() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.common.util.CDOTimeProvider#getTimeStamp()
	 */
	@Override
	public long getTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.server.IQueryHandlerProvider#getQueryHandler(org.
	 * eclipse.emf.cdo.common.util.CDOQueryInfo)
	 */
	@Override
	public IQueryHandler getQueryHandler(CDOQueryInfo info) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.net4j.util.container.IContainer#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.net4j.util.container.IContainer#getElements()
	 */
	@Override
	public Object[] getElements() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.net4j.util.event.INotifier#addListener(org.eclipse.net4j.util
	 * .event.IListener)
	 */
	@Override
	public void addListener(IListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.net4j.util.event.INotifier#removeListener(org.eclipse.net4j.
	 * util.event.IListener)
	 */
	@Override
	public void removeListener(IListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.net4j.util.event.INotifier#hasListeners()
	 */
	@Override
	public boolean hasListeners() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.net4j.util.event.INotifier#getListeners()
	 */
	@Override
	public IListener[] getListeners() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.spi.server.InternalRepository#setName(java.lang.
	 * String)
	 */
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.spi.server.InternalRepository#setType(org.eclipse.emf
	 * .cdo.common.CDOCommonRepository.Type)
	 */
	@Override
	public void setType(Type type) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.emf.cdo.spi.server.InternalRepository#setState(org.eclipse.
	 * emf.cdo.common.CDOCommonRepository.State)
	 */
	@Override
	public void setState(State state) {
		this.state = state;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.cdo.spi.server.InternalRepository#getStore()
	 */
	@Override
	public InternalStore getStore() {
		return null;
	}

	@Override
	public void setStore(InternalStore store) {
	}

	@Override
	public void setProperties(Map<String, String> properties) {
	}

	@Override
	public InternalCDOBranchManager getBranchManager() {
		return null;
	}

	@Override
	public void setBranchManager(InternalCDOBranchManager branchManager) {
	}

	@Override
	public Semaphore getPackageRegistryCommitLock() {
		return null;
	}

	@Override
	public InternalCDOPackageRegistry getPackageRegistry() {
		return null;
	}

	@Override
	public InternalCDOPackageRegistry getPackageRegistry(boolean considerCommitContext) {
		return null;
	}

	@Override
	public InternalCDORevisionManager getRevisionManager() {
		return null;
	}

	@Override
	public void setRevisionManager(InternalCDORevisionManager revisionManager) {
	}

	@Override
	public InternalCDOCommitInfoManager getCommitInfoManager() {
		return null;
	}

	@Override
	public InternalSessionManager getSessionManager() {
		return null;
	}

	@Override
	public void setSessionManager(InternalSessionManager sessionManager) {
	}

	@Override
	public InternalLockManager getLockManager() {
		return null;
	}

	@Override
	public InternalLockManager getLockingManager() {
		return null;
	}

	@Override
	public InternalQueryManager getQueryManager() {
		return null;
	}

	@Override
	public void setQueryHandlerProvider(IQueryHandlerProvider queryHandlerProvider) {
	}

	@Override
	public InternalCommitManager getCommitManager() {
		return null;
	}

	@Override
	public InternalCommitContext createCommitContext(InternalTransaction transaction) {
		return null;
	}

	@Override
	public long[] createCommitTimeStamp(OMMonitor monitor) {
		return null;
	}

	@Override
	public long[] forceCommitTimeStamp(long timestamp, OMMonitor monitor) {
		return null;
	}

	@Override
	public void endCommit(long timeStamp) {
	}

	@Override
	public void failCommit(long timeStamp) {
	}

	@Override
	public void sendCommitNotification(InternalSession sender, CDOCommitInfo commitInfo) {
	}

	@Override
	public void setRootResourceID(CDOID rootResourceID) {
	}

	@Override
	public void setLastCommitTimeStamp(long commitTimeStamp) {
	}

	@Override
	public void ensureChunks(InternalCDORevision revision) {
	}

	@Override
	public IStoreAccessor ensureChunk(InternalCDORevision revision, EStructuralFeature feature, int chunkStart, int chunkEnd) {
		return null;
	}

	@Override
	public void notifyReadAccessHandlers(InternalSession session, CDORevision[] revisions, List<CDORevision> additionalRevisions) {
	}

	@Override
	public void notifyWriteAccessHandlers(ITransaction transaction, CommitContext commitContext, boolean beforeCommit, OMMonitor monitor) {
	}

	@Override
	public void rollbackWriteAccessHandlers(ITransaction transaction, CommitContext commitContext) {
	}

	@Override
	public void replicate(CDOReplicationContext context) {
		System.out.println("Context: " + context.getLastReplicatedCommitTime());
	}

	@Override
	public CDOReplicationInfo replicateRaw(CDODataOutput out, int lastReplicatedBranchID, long lastReplicatedCommitTime) throws IOException {
		return null;
	}

	@Override
	public CDOChangeSetData getChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint) {
		return null;
	}

	@Override
	public Set<CDOID> getMergeData(CDORevisionAvailabilityInfo targetInfo, CDORevisionAvailabilityInfo sourceInfo, CDORevisionAvailabilityInfo targetBaseInfo,
			CDORevisionAvailabilityInfo sourceBaseInfo, String[] nsURIs, OMMonitor monitor) {
		return null;
	}

	@Override
	public void queryLobs(List<byte[]> ids) {

	}

	@Override
	public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException {

	}

	@Override
	public void loadLob(byte[] id, OutputStream out) throws IOException {
	}

	@Override
	public void handleRevisions(EClass eClass, CDOBranch branch, boolean exactBranch, long timeStamp, boolean exactTime, CDORevisionHandler handler) {
	}

	@Override
	public boolean isSkipInitialization() {
		return false;
	}

	@Override
	public void setSkipInitialization(boolean skipInitialization) {
	}

	@Override
	public void initSystemPackages() {
	}

	@Override
	public void initMainBranch(InternalCDOBranchManager branchManager, long timeStamp) {
	}

	@Override
	public LockObjectsResult lock(InternalView view, LockType type, List<CDORevisionKey> keys, boolean recursive, long timeout) {
		return null;
	}

	@Override
	public UnlockObjectsResult unlock(InternalView view, LockType type, List<CDOID> ids, boolean recursive) {
		return null;
	}

	@Override
	public Object processPackage(Object value) {
		return null;
	}

	@Override
	public EPackage[] loadPackages(CDOPackageUnit packageUnit) {
		return null;
	}

	@Override
	public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo) {
		return null;
	}

	@Override
	public BranchInfo loadBranch(int branchID) {
		return null;
	}

	@Override
	public SubBranchInfo[] loadSubBranches(int branchID) {
		return null;
	}

	@Override
	public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler) {
		return 0;
	}

	@Override
	public List<InternalCDORevision> loadRevisions(List<RevisionInfo> infos, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth) {
		return null;
	}

	@Override
	public InternalCDORevision loadRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk) {
		return null;
	}

	@Override
	public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler) {
	}

	@Override
	public CDOCommitData loadCommitData(long timeStamp) {
		return null;
	}

	@Override
	public void handleBranch(CDOBranch branch) {
		// branches are already created during start
	}

	@Override
	public boolean handleLockArea(LockArea area) {
		return false;
	}

	@Override
	public void replicateRaw(CDODataInput in, OMMonitor monitor) throws IOException {
	}

	@Override
	public void handleLockChangeInfo(CDOLockChangeInfo lockChangeInfo) {
	}

	@Override
	public InternalRepositorySynchronizer getSynchronizer() {
		return null;
	}

	@Override
	public void setSynchronizer(InternalRepositorySynchronizer synchronizer) {
	}

	@Override
	public InternalSession getReplicatorSession() {
		return null;
	}

	@Override
	public void setLastReplicatedBranchID(int lastReplicatedBranchID) {
	}

	@Override
	public void setLastReplicatedCommitTime(long lastReplicatedCommitTime) {
	}

}
