/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager.CommitInfoLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a <i>connection</i> to a physical data storage back-end.
 *
 * @author Eike Stepper
 * @apiviz.uses {@link IStoreChunkReader} - - creates
 */
public interface IStoreAccessor extends IQueryHandlerProvider, BranchLoader, CommitInfoLoader
{
  /**
   * Returns the store this accessor is associated with.
   */
  public IStore getStore();

  /**
   * Returns the session this accessor is associated with.
   *
   * @since 3.0
   */
  public InternalSession getSession();

  /**
   * Returns the transaction this accessor is associated with if {@link #isReader()} returns <code>false</code>,
   * <code>null</code> otherwise.
   *
   * @since 2.0
   */
  public ITransaction getTransaction();

  /**
   * Returns <code>true</code> if this accessor has been configured for read-only access to the back-end,
   * <code>false</code> otherwise.
   *
   * @since 2.0
   */
  public boolean isReader();

  /**
   * @since 2.0
   */
  public IStoreChunkReader createChunkReader(InternalCDORevision revision, EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public Collection<InternalCDOPackageUnit> readPackageUnits();

  /**
   * Demand loads a given package proxy that has been created on startup of the repository.
   * <p>
   * This method must only load the given package, <b>not</b> possible contained packages.
   *
   * @since 2.0
   */
  public EPackage[] loadPackageUnit(InternalCDOPackageUnit packageUnit);

  /**
   * Reads a revision from the back-end that was valid at the given timeStamp in the given branch.
   *
   * @since 4.0
   */
  public InternalCDORevision readRevision(CDOID id, CDOBranchPoint branchPoint, int listChunk,
      CDORevisionCacheAdder cache);

  /**
   * Reads a revision with the given version in the given branch from the back-end.
   *
   * @since 4.0
   */
  public InternalCDORevision readRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int listChunk,
      CDORevisionCacheAdder cache);

  /**
   * Passes all revisions of the store to the {@link CDORevisionHandler handler} if <b>all</b> of the following
   * conditions are met:
   * <ul>
   * <li>The <code>eClass</code> parameter is <code>null</code> or equal to <code>revision.getEClass()</code>.
   * <li>The <code>branch</code> parameter is <code>null</code> or equal to <code>revision.getBranch()</code>.
   * <li><b>One</b> of the following conditions is met:
   * <ul>
   * <li>The <code>timeStamp</code> parameter is {@link CDOBranchPoint#INVALID_DATE INVALID}.
   * <li>The <code>exactTime</code> parameter is <code>true</code> and the <code>timeStamp</code> parameter is
   * {@link CDOBranchPoint#UNSPECIFIED_DATE UNSPECIFIED} or equal to <code>revision.getTimeStamp()</code>.
   * <li>The <code>exactTime</code> parameter is <code>false</code> and the <code>timeStamp</code> parameter is between
   * <code>revision.getTimeStamp()</code> and <code>revision.getRevised()</code>.
   * </ul>
   * </ul>
   *
   * @since 4.0
   */
  public void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler);

  /**
   * Returns a set of CDOIDs that have at least one revision in any of the passed branches and time ranges.
   * DetachedCDORevisions must also be considered!
   * @param nsURIs namespace URI restriction
   *
   * @since 4.0
   * @since Snow Owl 2.6
   */
  public Set<CDOID> readChangeSet(OMMonitor monitor, String[] nsURIs, CDOChangeSetSegment... segments);

  /**
   * Returns the <code>CDOID</code> of the resource node with the given folderID and name if a resource with this
   * folderID and name exists in the store, <code>null</code> otherwise.
   *
   * @since 3.0
   */
  public CDOID readResourceID(CDOID folderID, String name, CDOBranchPoint branchPoint);

  /**
   * @since 2.0
   */
  public void queryResources(QueryResourcesContext context);

  /**
   * @since 3.0
   */
  public void queryXRefs(QueryXRefsContext context);

  /**
   * Determines which of the large objects identified by the given {@link CDOLob#getID() IDs} are known in the backend
   * represented by this {@link IStoreAccessor} by removing the unknown IDs from the passed collection.
   * <p>
   * The identifier of a {@link CDOLob large object} is the SHA-1 digest of the content of this large object.
   * <p>
   * <b>Usage context:</b> This method is only called in the context of a commit operation of a client transaction if
   * that transaction contains additions of or changes to large objects.
   *
   * @param ids
   *          the collection of large object IDs that the unknown IDs are supposed to be removed from.
   * @since 4.0
   */
  public void queryLobs(List<byte[]> ids);

  /**
   * Serializes the content of the large object identified by the given {@link CDOLob#getID() ID} to the given
   * <i>stream</i>.
   * <p>
   * The identifier of a {@link CDOLob large object} is the SHA-1 digest of the content of this large object.
   *
   * @param id
   *          the ID of the large object whose content is to be written to the <i>stream</i>.
   * @throws IOException
   *           if the <i>stream</i> could not be written to.
   * @since 4.0
   */
  public void loadLob(byte[] id, OutputStream out) throws IOException;

  /**
   * @since 4.0
   */
  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException;

  /**
   * @since 2.0
   */
  public void writePackageUnits(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor);

  /**
   * Called before committing. An instance of this accessor represents an instance of a back-end transaction. Could be
   * called multiple times before commit it called. {@link IStoreAccessor#commit(OMMonitor)} or
   * {@link IStoreAccessor#rollback()} will be called after any numbers of
   * {@link IStoreAccessor#write(InternalCommitContext, OMMonitor)}.
   * <p>
   * <b>Note</b>: {@link IStoreAccessor#write(InternalCommitContext, OMMonitor)} and
   * {@link IStoreAccessor#commit(OMMonitor)} could be called from different threads.
   *
   * @since 3.0
   */
  public void write(InternalCommitContext context, OMMonitor monitor);

  /**
   * Flushes to the back-end and makes available the data for others.
   * <p>
   * <b>Note</b>: {@link IStoreAccessor#write(InternalCommitContext, OMMonitor)} and
   * {@link IStoreAccessor#commit(OMMonitor)} could be called from different threads.
   * <p>
   * <b>Note</b>: Implementors should detect if dirty write occurred. In this case it should throw an exception.
   *
   * <pre>
   * if (revision.getVersion() != revisionDelta.getOriginVersion())
   * {
   *   throw new ConcurrentModificationException(&quot;Trying to update object &quot; + revisionDelta.getID()
   *       + &quot; that was already modified&quot;);
   * }
   * </pre>
   *
   * @since 2.0
   */
  public void commit(OMMonitor monitor);

  /**
   * <b>Note</b>: {@link IStoreAccessor#write(InternalCommitContext, OMMonitor)} and {@link IStoreAccessor#rollback()}
   * could be called from different threads.
   *
   * @since 2.0
   */
  public void rollback();

  public void release();

  /**
   * Represents the state of a single, logical commit operation which is driven through multiple calls to several
   * methods on the {@link IStoreAccessor} API. All these method calls get the same <code>CommitContext</code> instance
   * passed so that the implementor of the {@link IStoreAccessor} can track the state and progress of the commit
   * operation.
   *
   * @author Eike Stepper
   * @since 2.0
   * @noimplement This interface is not intended to be implemented by clients.
   * @noextend This interface is not intended to be extended by clients.
   * @apiviz.exclude
   */
  public interface CommitContext extends CDORevisionProvider
  {
    /**
     * Returns the transactional view (<code>ITransaction</code>) which is the scope of the commit operation represented
     * by this <code>CommitContext</code>.
     *
     * @since 4.0
     */
    public ITransaction getTransaction();

    /**
     * Returns the branch ID and timestamp of this commit operation.
     *
     * @since 3.0
     */
    public CDOBranchPoint getBranchPoint();

    /**
     * @since 4.0
     */
    public long getPreviousTimeStamp();

    /**
     * @since 3.0
     */
    public String getUserID();

    /**
     * @since 3.0
     */
    public String getCommitComment();

    /**
     * @since 3.0
     */
    public boolean isAutoReleaseLocksEnabled();

    /**
     * Returns the temporary, transactional package manager associated with the commit operation represented by this
     * <code>CommitContext</code>. In addition to the packages registered with the session this package manager also
     * contains the new packages that are part of this commit operation.
     */
    public InternalCDOPackageRegistry getPackageRegistry();

    /**
     * Returns an array of the new package units that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public InternalCDOPackageUnit[] getNewPackageUnits();

    /**
     * Returns an array of the locks on the new objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     *
     * @since 4.1
     */
    public CDOLockState[] getLocksOnNewObjects();

    /**
     * Returns an array of the new objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public InternalCDORevision[] getNewObjects();

    /**
     * Returns an array of the dirty objects that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public InternalCDORevision[] getDirtyObjects();

    /**
     * Returns an array of the dirty object deltas that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     */
    public InternalCDORevisionDelta[] getDirtyObjectDeltas();

    /**
     * Returns an array of the removed object that are part of the commit operation represented by this
     * <code>CommitContext</code>.
     *
     * @since 2.0
     */
    public CDOID[] getDetachedObjects();

    /**
     * Returns a map with an {@link EClass} value per {@link CDOID} type.
     *
     * @since 4.0
     */
    public Map<CDOID, EClass> getDetachedObjectTypes();

    /**
     * Returns a stream that all {@link CDOLob lobs} can be read from. The format of the data delivered through the
     * stream is:
     * <p>
     * <ol>
     * <li> {@link ExtendedDataInputStream#readInt() int}: the number of lobs to be read from the stream.
     * <li>The following data can be read from the stream in a loop with one iteration per lob in the stream:
     * <ol>
     * <li> {@link ExtendedDataInputStream#readByteArray() int + byte[]}: the id of the lob (prepended by the size of the
     * id).
     * <li> {@link ExtendedDataInputStream#readLong() long}: the size of the lob. The foollowing interpretation applies:
     * <ul>
     * <li>A positive size indicates a {@link CDOBlob blob} and means the number of bytes that can be
     * {@link IOUtil#copyBinary(java.io.InputStream, java.io.OutputStream) read}.
     * <li>A negative size indicates a {@link CDOClob clob} and means the number of characters that can be
     * {@link IOUtil#copyCharacter(java.io.Reader, java.io.Writer) read}.
     * </ul>
     * </ol>
     * </ol>
     *
     * @since 4.0
     */
    public ExtendedDataInputStream getLobs();

    /**
     * Returns an unmodifiable map from all temporary IDs to their persistent counter parts.
     */
    public Map<CDOID, CDOID> getIDMappings();

    /**
     * @since 4.0
     */
    public CDOCommitInfo createCommitInfo();

    /**
     * @since 3.0
     */
    public String getRollbackMessage();

    /**
     * @since 4.0
     */
    public List<CDOIDReference> getXRefs();

    /**
     * @since 4.1
     */
    public List<LockState<Object, IView>> getPostCommmitLockStates();
  }

  /**
   * Represents the query execution state of a {@link IStoreAccessor#queryResources(QueryResourcesContext) resources
   * query}.
   *
   * @author Eike Stepper
   * @since 2.0
   * @noimplement This interface is not intended to be implemented by clients.
   * @apiviz.exclude
   */
  public interface QueryResourcesContext extends CDOBranchPoint
  {
    public CDOID getFolderID();

    public String getName();

    public boolean exactMatch();

    /**
     * Returns the maximum number of results expected by the client or {@link CDOQueryInfo#UNLIMITED_RESULTS} for no
     * limitation.
     */
    public int getMaxResults();

    /**
     * Adds the CDOID of one resource to the results of the underlying query.
     *
     * @return <code>true</code> to indicate that more results can be passed subsequently, <code>false</code> otherwise
     *         (i.e. maxResults has been reached or an asynchronous query has been canceled).
     */
    public boolean addResource(CDOID resourceID);

    /**
     * Represents the query execution state of a {@link IStoreAccessor#queryResources(QueryResourcesContext) resources
     * query} that is supposed to deliver one exact resource, or <code>null</code>.
     *
     * @author Eike Stepper
     * @since 2.0
     * @apiviz.exclude
     */
    public interface ExactMatch extends QueryResourcesContext
    {
      public CDOID getResourceID();
    }
  }

  /**
   * Represents the query execution state of a {@link IStoreAccessor#queryXRefs(QueryXRefsContext) XRefs query}.
   *
   * @author Eike Stepper
   * @since 3.0
   * @noimplement This interface is not intended to be implemented by clients.
   * @apiviz.exclude
   */
  public interface QueryXRefsContext extends CDOBranchPoint
  {
    /**
     * @since 4.0
     */
    public Map<CDOID, EClass> getTargetObjects();

    public EReference[] getSourceReferences();

    /**
     * @since 4.0
     */
    public Map<EClass, List<EReference>> getSourceCandidates();

    /**
     * Returns the maximum number of results expected by the client or {@link CDOQueryInfo#UNLIMITED_RESULTS} for no
     * limitation.
     */
    public int getMaxResults();

    /**
     * Adds the data of one cross reference to the results of the underlying query.
     *
     * @return <code>true</code> to indicate that more results can be passed subsequently, <code>false</code> otherwise
     *         (i.e. maxResults has been reached or an asynchronous query has been canceled).
     */
    public boolean addXRef(CDOID targetID, CDOID sourceID, EReference sourceReference, int sourceIndex);
  }

  /**
   * An extension interface for {@link IStoreAccessor store accessors} that support <i>raw data access</i> as needed by
   * {@link IRepositorySynchronizer repository synchronizers} or {@link CDOServerImporter server importers}.
   *
   * @author Eike Stepper
   * @since 4.0
   * @apiviz.exclude
   */
  public interface Raw extends IStoreAccessor
  {
    /**
     * Serializes all backend data within the given ranges such that it can be deserialized by the
     * {@link #rawImport(CDODataInput, int, int, long, long, OMMonitor) rawImport()} method of a different instance of
     * the same implementation of {@link IStoreAccessor.Raw raw store accessor}.
     * <p>
     * <b>Implementation note:</b> The implementor of this method is free to choose a serialization format as it only
     * needs to be understood by different instances of the same implementation of {@link IStoreAccessor.Raw raw store
     * accessor}.
     * <p>
     * <b>Usage context:</b> This method is only called in the context of a
     * {@link CDOProtocolConstants#SIGNAL_REPLICATE_REPOSITORY_RAW REPLICATE_REPOSITORY_RAW} signal that is triggered
     * from {@link IRepositorySynchronizer}.
     *
     * @param out
     *          the <i>stream</i> to serialize the data to.
     * @param fromBranchID
     *          the {@link CDOBranch#getID() ID} of the first branch to be exported.
     * @param toBranchID
     *          the {@link CDOBranch#getID() ID} of the last branch to be exported.
     * @param fromCommitTime
     *          the first {@link CDOBranchPoint#getTimeStamp() time stamp} of all non-branch data (e.g.
     *          {@link CDORevision revisions}, {@link CDOCommitInfo commit infos}, {@link CDOPackageUnit package units},
     *          etc...) to be exported.
     * @param toCommitTime
     *          the last {@link CDOBranchPoint#getTimeStamp() time stamp} of all non-branch data (e.g.
     *          {@link CDORevision revisions}, {@link CDOCommitInfo commit infos}, {@link CDOPackageUnit package units},
     *          etc...) to be exported.
     * @throws IOException
     *           if the <i>stream</i> could not be written to.
     * @throws UnsupportedOperationException
     *           if this {@link IStoreAccessor.Raw raw store accessor} does not support branching.
     */
    public void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime)
        throws IOException;

    /**
     * Deserializes backend data that has been serialized by the {@link #rawExport(CDODataOutput, int, int, long, long)
     * rawExport()} method of a different instance of the same implementation of {@link IStoreAccessor.Raw raw store
     * accessor}.
     * <p>
     * <b>Implementation note:</b> The implementor of this method is free to choose a serialization format as it only
     * needs to be understood by different instances of the same implementation of {@link IStoreAccessor.Raw raw store
     * accessor}.
     * <p>
     * <b>Usage context:</b> This method is only called in the context of a
     * {@link CDOProtocolConstants#SIGNAL_REPLICATE_REPOSITORY_RAW REPLICATE_REPOSITORY_RAW} signal that is triggered
     * from {@link IRepositorySynchronizer}.
     *
     * @param in
     *          the <i>stream</i> to deserialize the data from.
     * @param fromBranchID
     *          the {@link CDOBranch#getID() ID} of the first branch to be imported.
     * @param toBranchID
     *          the {@link CDOBranch#getID() ID} of the last branch to be imported.
     * @param fromCommitTime
     *          the first {@link CDOBranchPoint#getTimeStamp() time stamp} of all non-branch data (e.g.
     *          {@link CDORevision revisions}, {@link CDOCommitInfo commit infos}, {@link CDOPackageUnit package units},
     *          etc...) to be imported.
     * @param toCommitTime
     *          the last {@link CDOBranchPoint#getTimeStamp() time stamp} of all non-branch data (e.g.
     *          {@link CDORevision revisions}, {@link CDOCommitInfo commit infos}, {@link CDOPackageUnit package units},
     *          etc...) to be imported.
     * @throws IOException
     *           if the <i>stream</i> could not be read from.
     * @throws UnsupportedOperationException
     *           if this {@link IStoreAccessor.Raw raw store accessor} does not support branching.
     */
    public void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime, long toCommitTime,
        OMMonitor monitor) throws IOException;

    /**
     * Stores the given {@link CDOPackageUnit package units} in the backend represented by this
     * {@link IStoreAccessor.Raw raw store accessor} without going through a regular
     * {@link IStoreAccessor #commit(OMMonitor) commit}. A regular commit operation would assign new
     * {@link CDOPackageUnit#getTimeStamp() time stamps}, which is not desired in the context of a replication
     * operation.
     * <p>
     * <b>Implementation note:</b> The implementor of this method may rely on the fact that multiple subsequent calls to
     * this method are followed by a single final call to the {@link #rawCommit(double, OMMonitor) rawCommit()} method
     * where the accumulated backend changes can be committed atomically.
     *
     * @param packageUnits
     *          the package units to be stored in the backend represented by this {@link IStoreAccessor.Raw raw store
     *          accessor}.
     * @param monitor
     *          a progress monitor that <b>may be</b> used to report proper progress of this operation to the caller and
     *          <b>may be</b> used to react to cancelation requests of the caller and <b>must be</b> touched regularly
     *          to prevent timeouts from expiring in the caller.
     * @see #rawCommit(double, OMMonitor)
     */
    public void rawStore(InternalCDOPackageUnit[] packageUnits, OMMonitor monitor);

    /**
     * Stores the given {@link CDORevision revision} in the backend represented by this {@link IStoreAccessor.Raw raw
     * store accessor} without going through a regular {@link IStoreAccessor#commit(OMMonitor) commit}. A regular commit
     * operation would assign new {@link CDORevisionKey#getID() IDs} and {@link CDOBranchPoint#getTimeStamp() time
     * stamps}, which is not desired in the context of a replication operation.
     * <p>
     * <b>Implementation note:</b> The implementor of this method may rely on the fact that multiple subsequent calls to
     * this method are followed by a single final call to the {@link #rawCommit(double, OMMonitor) rawCommit()} method
     * where the accumulated backend changes can be committed atomically.
     *
     * @param revision
     *          the revision to be stored in the backend represented by this {@link IStoreAccessor.Raw raw store
     *          accessor}.
     * @param monitor
     *          a progress monitor that <b>may be</b> used to report proper progress of this operation to the caller and
     *          <b>may be</b> used to react to cancelation requests of the caller and <b>must be</b> touched regularly
     *          to prevent timeouts from expiring in the caller.
     * @see #rawCommit(double, OMMonitor)
     */
    public void rawStore(InternalCDORevision revision, OMMonitor monitor);

    /**
     * Stores the given {@link CDOBlob blob} in the backend represented by this {@link IStoreAccessor.Raw raw store
     * accessor} without going through a regular {@link IStoreAccessor#commit(OMMonitor) commit}.
     * <p>
     * <b>Implementation note:</b> The implementor of this method may rely on the fact that multiple subsequent calls to
     * this method are followed by a single final call to the {@link #rawCommit(double, OMMonitor) rawCommit()} method
     * where the accumulated backend changes can be committed atomically.
     *
     * @param id
     *          the {@link CDOBlob#getID() ID} of the blob to be stored in the backend represented by this
     *          {@link IStoreAccessor.Raw raw store accessor}.
     * @param size
     *          the {@link CDOBlob#getSize() size} of the blob to be stored in the backend represented by this
     *          {@link IStoreAccessor.Raw raw store accessor}.
     * @param inputStream
     *          the {@link CDOBlob#getContents() contents} of the blob to be stored in the backend represented by this
     *          {@link IStoreAccessor.Raw raw store accessor}.
     * @see #rawCommit(double, OMMonitor)
     */
    public void rawStore(byte[] id, long size, InputStream inputStream) throws IOException;

    /**
     * Stores the given {@link CDOClob clob} in the backend represented by this {@link IStoreAccessor.Raw raw store
     * accessor} without going through a regular {@link IStoreAccessor#commit(OMMonitor) commit}.
     * <p>
     * <b>Implementation note:</b> The implementor of this method may rely on the fact that multiple subsequent calls to
     * this method are followed by a single final call to the {@link #rawCommit(double, OMMonitor) rawCommit()} method
     * where the accumulated backend changes can be committed atomically.
     *
     * @param id
     *          the {@link CDOClob#getID() ID} of the clob to be stored in the backend represented by this
     *          {@link IStoreAccessor.Raw raw store accessor}.
     * @param size
     *          the {@link CDOClob#getSize() size} of the clob to be stored in the backend represented by this
     *          {@link IStoreAccessor.Raw raw store accessor}.
     * @param reader
     *          the {@link CDOClob#getContents() contents} of the clob to be stored in the backend represented by this
     *          {@link IStoreAccessor.Raw raw store accessor}.
     * @see #rawCommit(double, OMMonitor)
     */
    public void rawStore(byte[] id, long size, Reader reader) throws IOException;

    /**
     * Stores the given {@link CDOCommitInfo commit} in the backend represented by this {@link IStoreAccessor.Raw raw
     * store accessor} without going through a regular {@link IStoreAccessor#commit(OMMonitor) commit}.
     * <p>
     * <b>Implementation note:</b> The implementor of this method may rely on the fact that multiple subsequent calls to
     * this method are followed by a single final call to the {@link #rawCommit(double, OMMonitor) rawCommit()} method
     * where the accumulated backend changes can be committed atomically.
     *
     * @param branch
     *          the {@link CDOCommitInfo#getBranch() branch} of the commit info to be stored in the backend represented
     *          by this {@link IStoreAccessor.Raw raw store accessor}.
     * @param timeStamp
     *          the {@link CDOCommitInfo#getTimeStamp() time stamp} of the commit info to be stored in the backend
     *          represented by this {@link IStoreAccessor.Raw raw store accessor}.
     * @param previousTimeStamp
     *          the {@link CDOCommitInfo#getPreviousTimeStamp() previous time stamp} of the commit info to be stored in
     *          the backend represented by this {@link IStoreAccessor.Raw raw store accessor}.
     * @param userID
     *          the {@link CDOCommitInfo#getUserID() user ID} of the commit info to be stored in the backend represented
     *          by this {@link IStoreAccessor.Raw raw store accessor}.
     * @param comment
     *          the {@link CDOCommitInfo#getComment() comment} of the commit info to be stored in the backend
     *          represented by this {@link IStoreAccessor.Raw raw store accessor}.
     * @see #rawCommit(double, OMMonitor)
     */
    public void rawStore(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment,
        OMMonitor monitor);

    /**
     * Deletes the revision identified by the given {@link CDORevisionKey key} from the backend represented by this
     * {@link IStoreAccessor.Raw raw store accessor} without going through a regular
     * {@link IStoreAccessor#commit(OMMonitor) commit}.
     * <p>
     * <b>Implementation note:</b> The implementor of this method may rely on the fact that multiple subsequent calls to
     * this method are followed by a single final call to the {@link #rawCommit(double, OMMonitor) rawCommit()} method
     * where the accumulated backend changes can be committed atomically.
     *
     * @see #rawCommit(double, OMMonitor)
     */
    public void rawDelete(CDOID id, int version, CDOBranch branch, EClass eClass, OMMonitor monitor);

    /**
     * Atomically commits the accumulated backend changes resulting from previous calls to the rawStore() methods.
     *
     * @param commitWork
     *          the amount of work to use up from the monitor while executing the commit.
     * @param monitor
     *          a progress monitor that <b>may be</b> used to report proper progress of this operation to the caller and
     *          <b>may be</b> used to react to cancelation requests of the caller and <b>must be</b> touched regularly
     *          to prevent timeouts from expiring in the caller.
     * @see #rawStore(InternalCDOPackageUnit[], OMMonitor)
     * @see #rawStore(InternalCDORevision, OMMonitor)
     * @see #rawStore(byte[], long, InputStream)
     * @see #rawStore(byte[], long, Reader)
     * @see #rawStore(CDOBranch, long, long, String, String, OMMonitor)
     */
    public void rawCommit(double commitWork, OMMonitor monitor);
  }

  /**
   * An extension interface for {@link IStoreAccessor store accessors} that support <i>durable locking</i>.
   *
   * @see DurableLocking2
   * @author Eike Stepper
   * @since 4.0
   * @apiviz.exclude
   */
  public interface DurableLocking extends IDurableLockingManager
  {
    public void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock);

    public void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock);

    public void unlock(String durableLockingID);
  }

  /**
   * An extension interface for {@link IStoreAccessor store accessors} that support <i>durable locking</i>.
   *
   * @author Caspar De Groot
   * @since 4.1
   * @apiviz.exclude
   */
  public interface DurableLocking2 extends DurableLocking
  {
    LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint, boolean readOnly,
        Map<CDOID, LockGrade> locks);

    public void updateLockArea(LockArea lockArea);
  }
}
