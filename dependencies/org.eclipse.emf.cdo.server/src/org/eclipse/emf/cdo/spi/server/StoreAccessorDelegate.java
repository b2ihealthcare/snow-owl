/*******************************************************************************
 * Copyright (c) 2016 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package org.eclipse.emf.cdo.spi.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreChunkReader;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 *
 */
public class StoreAccessorDelegate implements IStoreAccessor {

	private IStoreAccessor delegate;

	/**
	 * @param delegate
	 */
	public StoreAccessorDelegate(IStoreAccessor delegate) {
		super();
		this.delegate = delegate;
	}

	/**
	 * @param info
	 * @return
	 * @see org.eclipse.emf.cdo.server.IQueryHandlerProvider#getQueryHandler(org.eclipse.emf.cdo.common.util.CDOQueryInfo)
	 */
	public IQueryHandler getQueryHandler(CDOQueryInfo info) {
		return delegate.getQueryHandler(info);
	}

	/**
	 * @param branch
	 * @param startTime
	 * @param endTime
	 * @param handler
	 * @see org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader#loadCommitInfos(org.eclipse.emf.cdo.common.branch.CDOBranch, long, long, org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler)
	 */
	public void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler) {
		delegate.loadCommitInfos(branch, startTime, endTime, handler);
	}

	/**
	 * @param timeStamp
	 * @return
	 * @see org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader#loadCommitData(long)
	 */
	public CDOCommitData loadCommitData(long timeStamp) {
		return delegate.loadCommitData(timeStamp);
	}

	/**
	 * @param branchID
	 * @param branchInfo
	 * @return
	 * @see org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader#createBranch(int, org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader.BranchInfo)
	 */
	public Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo) {
		return delegate.createBranch(branchID, branchInfo);
	}

	/**
	 * @param branchID
	 * @return
	 * @see org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader#loadBranch(int)
	 */
	public BranchInfo loadBranch(int branchID) {
		return delegate.loadBranch(branchID);
	}

	/**
	 * @param branchID
	 * @return
	 * @see org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader#loadSubBranches(int)
	 */
	public SubBranchInfo[] loadSubBranches(int branchID) {
		return delegate.loadSubBranches(branchID);
	}

	/**
	 * @param startID
	 * @param endID
	 * @param branchHandler
	 * @return
	 * @see org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader#loadBranches(int, int, org.eclipse.emf.cdo.common.branch.CDOBranchHandler)
	 */
	public int loadBranches(int startID, int endID, CDOBranchHandler branchHandler) {
		return delegate.loadBranches(startID, endID, branchHandler);
	}

	/**
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#getStore()
	 */
	public IStore getStore() {
		return delegate.getStore();
	}

	/**
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#getSession()
	 */
	public InternalSession getSession() {
		return delegate.getSession();
	}

	/**
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#getTransaction()
	 */
	public ITransaction getTransaction() {
		return delegate.getTransaction();
	}

	/**
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#isReader()
	 */
	public boolean isReader() {
		return delegate.isReader();
	}

	/**
	 * @param revision
	 * @param feature
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#createChunkReader(org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision, org.eclipse.emf.ecore.EStructuralFeature)
	 */
	public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature) {
		return delegate.createChunkReader(revision, feature);
	}

	/**
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#readPackageUnits()
	 */
	public Collection<InternalCDOPackageUnit> readPackageUnits() {
		return delegate.readPackageUnits();
	}

	/**
	 * @param packageUnit
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#loadPackageUnit(org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit)
	 */
	public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit) {
		return delegate.loadPackageUnit(packageUnit);
	}

	/**
	 * @param id
	 * @param branchPoint
	 * @param listChunk
	 * @param cache
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#readRevision(org.eclipse.emf.cdo.common.id.CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchPoint, int, org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder)
	 */
	public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk, CDORevisionCacheAdder cache) {
		return delegate.readRevision(id, branchPoint, listChunk, cache);
	}

	/**
	 * @param id
	 * @param branchVersion
	 * @param listChunk
	 * @param cache
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#readRevisionByVersion(org.eclipse.emf.cdo.common.id.CDOID, org.eclipse.emf.cdo.common.branch.CDOBranchVersion, int, org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder)
	 */
	public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk, CDORevisionCacheAdder cache) {
		return delegate.readRevisionByVersion(id, branchVersion, listChunk, cache);
	}

	/**
	 * @param eClass
	 * @param branch
	 * @param timeStamp
	 * @param exactTime
	 * @param handler
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#handleRevisions(org.eclipse.emf.ecore.EClass, org.eclipse.emf.cdo.common.branch.CDOBranch, long, boolean, org.eclipse.emf.cdo.common.revision.CDORevisionHandler)
	 */
	public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime, CDORevisionHandler handler) {
		delegate.handleRevisions(eClass, branch, timeStamp, exactTime, handler);
	}

	/**
	 * @param monitor
	 * @param nsURIs
	 * @param segments
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#readChangeSet(org.eclipse.net4j.util.om.monitor.OMMonitor, java.lang.String[], org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment[])
	 */
	public Set<CDOID> readChangeSet(OMMonitor monitor, String[] nsURIs, CDOChangeSetSegment... segments) {
		return delegate.readChangeSet(monitor, nsURIs, segments);
	}

	/**
	 * @param folderID
	 * @param name
	 * @param branchPoint
	 * @return
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#readResourceID(org.eclipse.emf.cdo.common.id.CDOID, java.lang.String, org.eclipse.emf.cdo.common.branch.CDOBranchPoint)
	 */
	public CDOID readResourceID(CDOID folderID, String name, CDOBranchPoint branchPoint) {
		return delegate.readResourceID(folderID, name, branchPoint);
	}

	/**
	 * @param context
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#queryResources(org.eclipse.emf.cdo.server.IStoreAccessor.QueryResourcesContext)
	 */
	public void queryResources(QueryResourcesContext context) {
		delegate.queryResources(context);
	}

	/**
	 * @param context
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#queryXRefs(org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext)
	 */
	public void queryXRefs(QueryXRefsContext context) {
		delegate.queryXRefs(context);
	}

	/**
	 * @param ids
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#queryLobs(java.util.List)
	 */
	public void queryLobs(List<byte[]> ids) {
		delegate.queryLobs(ids);
	}

	/**
	 * @param id
	 * @param out
	 * @throws IOException
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#loadLob(byte[], java.io.OutputStream)
	 */
	public void loadLob(byte[] id, OutputStream out) throws IOException {
		delegate.loadLob(id, out);
	}

	/**
	 * @param fromTime
	 * @param toTime
	 * @param handler
	 * @throws IOException
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#handleLobs(long, long, org.eclipse.emf.cdo.common.lob.CDOLobHandler)
	 */
	public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException {
		delegate.handleLobs(fromTime, toTime, handler);
	}

	/**
	 * @param packageUnits
	 * @param monitor
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#writePackageUnits(org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit[], org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor) {
		delegate.writePackageUnits(packageUnits, monitor);
	}

	/**
	 * @param context
	 * @param monitor
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#write(org.eclipse.emf.cdo.spi.server.InternalCommitContext, org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	public void write(InternalCommitContext context, OMMonitor monitor) {
		delegate.write(context, monitor);
	}

	/**
	 * @param monitor
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#commit(org.eclipse.net4j.util.om.monitor.OMMonitor)
	 */
	public void commit(OMMonitor monitor) {
		delegate.commit(monitor);
	}

	/**
	 * 
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#rollback()
	 */
	public void rollback() {
		delegate.rollback();
	}

	/**
	 * 
	 * @see org.eclipse.emf.cdo.server.IStoreAccessor#release()
	 */
	public void release() {
		delegate.release();
	}
	
	
}