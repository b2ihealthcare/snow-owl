/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Simon McDuff - bug 233273
 *    Eike Stepper - maintenance
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.server.mem;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchHandler;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLobHandler;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea.Handler;
import org.eclipse.emf.cdo.common.model.CDOModelConstants;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.IStoreAccessor.DurableLocking2;
import org.eclipse.emf.cdo.server.IStoreAccessor.QueryXRefsContext;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.StoreThreadLocal;
import org.eclipse.emf.cdo.server.mem.IMEMStore;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranchManager.BranchLoader;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetSegment;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.LongIDStore;
import org.eclipse.emf.cdo.spi.server.StoreAccessorPool;

import org.eclipse.net4j.util.HexUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Simon McDuff
 */
public class MEMStore extends LongIDStore implements IMEMStore, BranchLoader, DurableLocking2
{
  public static final String TYPE = "mem"; //$NON-NLS-1$

  private long creationTime;

  private Map<String, String> properties = new HashMap<String, String>();

  private Map<Integer, BranchInfo> branchInfos = new HashMap<Integer, BranchInfo>();

  private int lastBranchID;

  private int lastLocalBranchID;

  private Map<Object, List<InternalCDORevision>> revisions = new HashMap<Object, List<InternalCDORevision>>();

  private List<CommitInfo> commitInfos = new ArrayList<CommitInfo>();

  private Map<CDOID, EClass> objectTypes = new HashMap<CDOID, EClass>();

  private Map<String, LockArea> lockAreas = new HashMap<String, LockArea>();

  private Map<String, Object> lobs = new HashMap<String, Object>();

  private int listLimit;

  @ExcludeFromDump
  private transient EStructuralFeature resourceNameFeature;

  /**
   * @param listLimit
   *          See {@link #setListLimit(int)}.
   * @since 2.0
   */
  public MEMStore(int listLimit)
  {
    super(TYPE, set(ChangeFormat.REVISION, ChangeFormat.DELTA), set(RevisionTemporality.NONE,
        RevisionTemporality.AUDITING), set(RevisionParallelism.NONE, RevisionParallelism.BRANCHING));
    setRevisionTemporality(RevisionTemporality.AUDITING);
    setRevisionParallelism(RevisionParallelism.BRANCHING);
    this.listLimit = listLimit;
  }

  public MEMStore()
  {
    this(UNLIMITED);
  }

  @Override
  public CDOID createObjectID(String val)
  {
    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      byte[] decoded = CDOIDUtil.decodeUUID(val);
      return CDOIDUtil.createUUID(decoded);
    }

    return super.createObjectID(val);
  }

  @Override
  public boolean isLocal(CDOID id)
  {
    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      return false;
    }

    return super.isLocal(id);
  }

  @Override
  public void ensureLastObjectID(CDOID id)
  {
    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      return;
    }

    super.ensureLastObjectID(id);
  }

  public synchronized Map<String, String> getPersistentProperties(Set<String> names)
  {
    if (names == null || names.isEmpty())
    {
      return new HashMap<String, String>(properties);
    }

    Map<String, String> result = new HashMap<String, String>();
    for (String name : names)
    {
      String value = properties.get(name);
      if (value != null)
      {
        result.put(name, value);
      }
    }

    return result;
  }

  public synchronized void setPersistentProperties(Map<String, String> properties)
  {
    this.properties.putAll(properties);
  }

  public synchronized void removePersistentProperties(Set<String> names)
  {
    for (String name : names)
    {
      properties.remove(name);
    }
  }

  public synchronized Pair<Integer, Long> createBranch(int branchID, BranchInfo branchInfo)
  {
    if (branchID == NEW_BRANCH)
    {
      branchID = ++lastBranchID;
    }
    else if (branchID == NEW_LOCAL_BRANCH)
    {
      branchID = --lastLocalBranchID;
    }

    branchInfos.put(branchID, branchInfo);
    return new Pair<Integer, Long>(branchID, branchInfo.getBaseTimeStamp());
  }

  public synchronized BranchInfo loadBranch(int branchID)
  {
    return branchInfos.get(branchID);
  }

  public synchronized SubBranchInfo[] loadSubBranches(int branchID)
  {
    List<SubBranchInfo> result = new ArrayList<SubBranchInfo>();
    for (Entry<Integer, BranchInfo> entry : branchInfos.entrySet())
    {
      BranchInfo branchInfo = entry.getValue();
      if (branchInfo.getBaseBranchID() == branchID)
      {
        int id = entry.getKey();
        result.add(new SubBranchInfo(id, branchInfo.getName(), branchInfo.getBaseTimeStamp()));
      }
    }

    return result.toArray(new SubBranchInfo[result.size()]);
  }

  public synchronized int loadBranches(int startID, int endID, CDOBranchHandler handler)
  {
    int count = 0;
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    for (Entry<Integer, BranchInfo> entry : branchInfos.entrySet())
    {
      int id = entry.getKey();
      if (startID <= id && (id <= endID || endID == 0))
      {
        BranchInfo branchInfo = entry.getValue();
        InternalCDOBranch branch = branchManager.getBranch(id, branchInfo);
        handler.handleBranch(branch);
        ++count;
      }
    }

    return count;
  }

  public synchronized void loadCommitInfos(CDOBranch branch, long startTime, long endTime, CDOCommitInfoHandler handler)
  {
    InternalCDOCommitInfoManager manager = getRepository().getCommitInfoManager();
    for (int i = 0; i < commitInfos.size(); i++)
    {
      CommitInfo info = commitInfos.get(i);
      if (startTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() < startTime)
      {
        continue;
      }

      if (endTime != CDOBranchPoint.UNSPECIFIED_DATE && info.getTimeStamp() > endTime)
      {
        continue;
      }

      if (branch != null && !ObjectUtil.equals(info.getBranch(), branch))
      {
        continue;
      }

      info.handle(manager, handler);
    }
  }

  public synchronized Set<CDOID> readChangeSet(CDOChangeSetSegment[] segments)
  {
    Set<CDOID> ids = new HashSet<CDOID>();
    for (CDOChangeSetSegment segment : segments)
    {
      for (List<InternalCDORevision> list : revisions.values())
      {
        readChangeSet(segment, list, ids);
      }
    }

    return ids;
  }

  private void readChangeSet(CDOChangeSetSegment segment, List<InternalCDORevision> list, Set<CDOID> ids)
  {
    long startTime = segment.getTimeStamp();
    long endTime = segment.getEndTime();
    boolean listCheckDone = false;
    for (InternalCDORevision revision : list)
    {
      CDOID id = revision.getID();
      if (!listCheckDone)
      {
        if (ids.contains(id))
        {
          return;
        }

        if (!ObjectUtil.equals(revision.getBranch(), segment.getBranch()))
        {
          return;
        }

        listCheckDone = true;
      }

      if (CDOCommonUtil.isValidTimeStamp(revision.getTimeStamp(), startTime, endTime))
      {
        ids.add(id);
      }
    }
  }

  public synchronized void handleRevisions(EClass eClass, CDOBranch branch, long timeStamp, boolean exactTime,
      CDORevisionHandler handler)
  {
    for (List<InternalCDORevision> list : revisions.values())
    {
      for (InternalCDORevision revision : list)
      {
        if (!handleRevision(revision, eClass, branch, timeStamp, exactTime, handler))
        {
          return;
        }
      }
    }
  }

  private boolean handleRevision(InternalCDORevision revision, EClass eClass, CDOBranch branch, long timeStamp,
      boolean exactTime, CDORevisionHandler handler)
  {
    if (eClass != null && revision.getEClass() != eClass)
    {
      return true;
    }

    if (branch != null && !ObjectUtil.equals(revision.getBranch(), branch))
    {
      return true;
    }

    if (timeStamp != CDOBranchPoint.INVALID_DATE)
    {
      if (exactTime)
      {
        if (timeStamp != CDOBranchPoint.UNSPECIFIED_DATE && revision.getTimeStamp() != timeStamp)
        {
          return true;
        }
      }
      else
      {
        if (!revision.isValid(timeStamp))
        {
          return true;
        }
      }
    }

    return handler.handleRevision(revision);
  }

  /**
   * @since 2.0
   */
  public int getListLimit()
  {
    return listLimit;
  }

  /**
   * @since 2.0
   */
  public synchronized void setListLimit(int listLimit)
  {
    if (listLimit != UNLIMITED && this.listLimit != listLimit)
    {
      for (List<InternalCDORevision> list : revisions.values())
      {
        enforceListLimit(list);
      }
    }

    this.listLimit = listLimit;
  }

  /**
   * @since 2.0
   */
  public synchronized List<InternalCDORevision> getCurrentRevisions()
  {
    ArrayList<InternalCDORevision> simpleRevisions = new ArrayList<InternalCDORevision>();
    Iterator<List<InternalCDORevision>> itr = revisions.values().iterator();
    while (itr.hasNext())
    {
      List<InternalCDORevision> list = itr.next();
      InternalCDORevision revision = list.get(list.size() - 1);
      simpleRevisions.add(revision);
    }

    return simpleRevisions;
  }

  public synchronized InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    Object listKey = getListKey(id, branchVersion.getBranch());
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list == null)
    {
      return null;
    }

    return getRevisionByVersion(list, branchVersion.getVersion());
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    Object listKey = getListKey(id, branchPoint.getBranch());
    if (branchPoint.getTimeStamp() == CDORevision.UNSPECIFIED_DATE)
    {
      List<InternalCDORevision> list = revisions.get(listKey);
      if (list == null)
      {
        return null;
      }

      return list.get(list.size() - 1);
    }

    if (!getRepository().isSupportingAudits())
    {
      throw new UnsupportedOperationException("Auditing not supported");
    }

    List<InternalCDORevision> list = revisions.get(listKey);
    if (list == null)
    {
      return null;
    }

    return getRevision(list, branchPoint);
  }

  public synchronized void addRevision(InternalCDORevision revision, boolean raw)
  {
    Object listKey = getListKey(revision.getID(), revision.getBranch());
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list == null)
    {
      list = new ArrayList<InternalCDORevision>();
      revisions.put(listKey, list);
    }

    addRevision(list, revision, raw);

    if (raw)
    {
      ensureLastObjectID(revision.getID());
    }
  }

  public synchronized void addCommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID,
      String comment)
  {
    int index = commitInfos.size() - 1;
    while (index >= 0)
    {
      CommitInfo info = commitInfos.get(index);
      if (timeStamp > info.getTimeStamp())
      {
        break;
      }

      --index;
    }

    CommitInfo commitInfo = new CommitInfo(branch, timeStamp, previousTimeStamp, userID, comment);
    commitInfos.add(index + 1, commitInfo);
  }

  /**
   * @since 2.0
   */
  public synchronized boolean rollbackRevision(InternalCDORevision revision)
  {
    CDOID id = revision.getID();
    CDOBranch branch = revision.getBranch();
    int version = revision.getVersion();

    Object listKey = getListKey(id, branch);
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list == null)
    {
      return false;
    }

    for (Iterator<InternalCDORevision> it = list.iterator(); it.hasNext();)
    {
      InternalCDORevision rev = it.next();
      if (rev.getVersion() == version)
      {
        it.remove();
        return true;
      }
      else if (rev.getVersion() == version - 1)
      {
        rev.setRevised(CDORevision.UNSPECIFIED_DATE);
      }
    }

    return false;
  }

  /**
   * @since 3.0
   */
  public synchronized DetachedCDORevision detachObject(CDOID id, CDOBranch branch, long timeStamp)
  {
    Object listKey = getListKey(id, branch);
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list != null)
    {
      InternalCDORevision revision = getRevision(list, branch.getHead());
      if (revision != null)
      {
        revision.setRevised(timeStamp - 1);
      }
    }

    int version;
    if (list == null)
    {
      list = new ArrayList<InternalCDORevision>();
      revisions.put(listKey, list);
      version = CDOBranchVersion.FIRST_VERSION;
    }
    else
    {
      version = getHighestVersion(list) + 1;
    }

    EClass eClass = getObjectType(id);
    DetachedCDORevision detached = new DetachedCDORevision(eClass, id, branch, version, timeStamp);
    addRevision(list, detached, false);
    return detached;
  }

  /**
   * @since 2.0
   */
  public synchronized void queryResources(IStoreAccessor.QueryResourcesContext context)
  {
    CDOID folderID = context.getFolderID();
    String name = context.getName();
    boolean exactMatch = context.exactMatch();
    for (Entry<Object, List<InternalCDORevision>> entry : revisions.entrySet())
    {
      CDOBranch branch = getBranch(entry.getKey());
      if (!ObjectUtil.equals(branch, context.getBranch()))
      {
        continue;
      }

      List<InternalCDORevision> list = entry.getValue();
      if (list.isEmpty())
      {
        continue;
      }

      InternalCDORevision revision = list.get(0);
      if (revision instanceof SyntheticCDORevision)
      {
        continue;
      }

      if (!revision.isResourceNode())
      {
        continue;
      }

      revision = getRevision(list, context);
      if (revision == null || revision instanceof DetachedCDORevision)
      {
        continue;
      }

      CDOID revisionFolder = (CDOID)revision.data().getContainerID();
      if (!CDOIDUtil.equals(revisionFolder, folderID))
      {
        continue;
      }

      String revisionName = (String)revision.data().get(resourceNameFeature, 0);
      boolean useEquals = exactMatch || revisionName == null || name == null;
      boolean match = useEquals ? ObjectUtil.equals(revisionName, name) : revisionName.startsWith(name);

      if (match)
      {
        if (!context.addResource(revision.getID()))
        {
          // No more results allowed
          break;
        }
      }
    }
  }

  public synchronized void queryXRefs(QueryXRefsContext context)
  {
    Set<CDOID> targetIDs = context.getTargetObjects().keySet();
    Map<EClass, List<EReference>> sourceCandidates = context.getSourceCandidates();

    for (Entry<Object, List<InternalCDORevision>> entry : revisions.entrySet())
    {
      CDOBranch branch = getBranch(entry.getKey());
      if (!ObjectUtil.equals(branch, context.getBranch()))
      {
        continue;
      }

      List<InternalCDORevision> list = entry.getValue();
      if (list.isEmpty())
      {
        continue;
      }

      InternalCDORevision revision = getRevision(list, context);
      if (revision == null || revision instanceof SyntheticCDORevision)
      {
        continue;
      }

      EClass eClass = revision.getEClass();
      CDOID sourceID = revision.getID();

      List<EReference> eReferences = sourceCandidates.get(eClass);
      if (eReferences != null)
      {
        for (EReference eReference : eReferences)
        {
          Object value = revision.getValue(eReference);
          if (value != null)
          {
            if (eReference.isMany())
            {
              @SuppressWarnings("unchecked")
              List<CDOID> ids = (List<CDOID>)value;
              int index = 0;
              for (CDOID id : ids)
              {
                if (!queryXRefs(context, targetIDs, id, sourceID, eReference, index++))
                {
                  return;
                }
              }
            }
            else
            {
              CDOID id = (CDOID)value;
              if (!queryXRefs(context, targetIDs, id, sourceID, eReference, 0))
              {
                return;
              }
            }
          }
        }
      }
    }
  }

  private boolean queryXRefs(QueryXRefsContext context, Set<CDOID> targetIDs, CDOID targetID, CDOID sourceID,
      EReference sourceReference, int index)
  {
    for (CDOID id : targetIDs)
    {
      if (id.equals(targetID))
      {
        if (!context.addXRef(targetID, sourceID, sourceReference, index))
        {
          // No more results allowed
          return false;
        }
      }
    }

    return true;
  }

  public synchronized void rawExport(CDODataOutput out, int fromBranchID, int toBranchID, long fromCommitTime,
      long toCommitTime)
  {
    // TODO: implement MEMStore.rawExport(out, fromBranchID, toBranchID, fromCommitTime, toCommitTime)
    throw new UnsupportedOperationException();
  }

  public synchronized void rawImport(CDODataInput in, int fromBranchID, int toBranchID, long fromCommitTime,
      long toCommitTime, OMMonitor monitor)
  {
    // TODO: implement MEMStore.rawImport(in, fromBranchID, toBranchID, fromCommitTime, toCommitTime, monitor)
    throw new UnsupportedOperationException();
  }

  public synchronized void rawDelete(CDOID id, int version, CDOBranch branch)
  {
    Object listKey = getListKey(id, branch);
    List<InternalCDORevision> list = revisions.get(listKey);
    if (list != null)
    {
      for (Iterator<InternalCDORevision> it = list.iterator(); it.hasNext();)
      {
        InternalCDORevision rev = it.next();
        if (rev.getVersion() == version)
        {
          it.remove();
          break;
        }
      }
    }
  }

  public synchronized LockArea createLockArea(String userID, CDOBranchPoint branchPoint, boolean readOnly,
      Map<CDOID, LockGrade> locks)
  {
    return createLockArea(null, userID, branchPoint, readOnly, locks);
  }

  public synchronized LockArea createLockArea(String durableLockingID, String userID, CDOBranchPoint branchPoint,
      boolean readOnly, Map<CDOID, LockGrade> locks)
  {
    if (durableLockingID != null)
    {
      // If the caller is specifying the ID, make sure there is no area with this ID yet
      if (lockAreas.containsKey(durableLockingID))
      {
        throw new LockAreaAlreadyExistsException(durableLockingID);
      }
    }
    else
    {
      do
      {
        durableLockingID = CDOLockUtil.createDurableLockingID();
      } while (lockAreas.containsKey(durableLockingID));
    }

    LockArea area = CDOLockUtil.createLockArea(durableLockingID, userID, branchPoint, readOnly, locks);
    lockAreas.put(durableLockingID, area);
    return area;
  }

  public synchronized void updateLockArea(LockArea lockArea)
  {
    String durableLockingID = lockArea.getDurableLockingID();
    lockAreas.put(durableLockingID, lockArea);
  }

  public synchronized LockArea getLockArea(String durableLockingID) throws LockAreaNotFoundException
  {
    LockArea area = lockAreas.get(durableLockingID);
    if (area == null)
    {
      throw new LockAreaNotFoundException(durableLockingID);
    }

    return area;
  }

  public synchronized void getLockAreas(String userIDPrefix, Handler handler)
  {
    for (LockArea area : lockAreas.values())
    {
      String userID = area.getUserID();
      if (userID == null || userID.startsWith(userIDPrefix))
      {
        if (!handler.handleLockArea(area))
        {
          return;
        }
      }
    }
  }

  public synchronized void deleteLockArea(String durableLockingID)
  {
    lockAreas.remove(durableLockingID);
  }

  public synchronized void lock(String durableLockingID, LockType type, Collection<? extends Object> objectsToLock)
  {
    LockArea area = getLockArea(durableLockingID);
    Map<CDOID, LockGrade> locks = area.getLocks();

    InternalLockManager lockManager = getRepository().getLockingManager();
    for (Object objectToLock : objectsToLock)
    {
      CDOID id = lockManager.getLockKeyID(objectToLock);
      LockGrade grade = locks.get(id);
      if (grade != null)
      {
        grade = grade.getUpdated(type, true);
      }
      else
      {
        grade = LockGrade.get(type);
      }

      locks.put(id, grade);
    }
  }

  public synchronized void unlock(String durableLockingID, LockType type, Collection<? extends Object> objectsToUnlock)
  {
    LockArea area = getLockArea(durableLockingID);
    Map<CDOID, LockGrade> locks = area.getLocks();

    InternalLockManager lockManager = getRepository().getLockingManager();
    for (Object objectToUnlock : objectsToUnlock)
    {
      CDOID id = lockManager.getLockKeyID(objectToUnlock);
      LockGrade grade = locks.get(id);
      if (grade != null)
      {
        grade = grade.getUpdated(type, false);
        if (grade == LockGrade.NONE)
        {
          locks.remove(id);
        }
      }
    }
  }

  public synchronized void unlock(String durableLockingID)
  {
    LockArea area = getLockArea(durableLockingID);
    Map<CDOID, LockGrade> locks = area.getLocks();
    locks.clear();
  }

  public synchronized void queryLobs(List<byte[]> ids)
  {
    for (Iterator<byte[]> it = ids.iterator(); it.hasNext();)
    {
      byte[] id = it.next();
      String key = HexUtil.bytesToHex(id);
      if (!lobs.containsKey(key))
      {
        it.remove();
      }
    }
  }

  public void handleLobs(long fromTime, long toTime, CDOLobHandler handler) throws IOException
  {
    for (Entry<String, Object> entry : lobs.entrySet())
    {
      byte[] id = HexUtil.hexToBytes(entry.getKey());
      Object lob = entry.getValue();
      if (lob instanceof byte[])
      {
        byte[] blob = (byte[])lob;
        ByteArrayInputStream in = new ByteArrayInputStream(blob);
        OutputStream out = handler.handleBlob(id, blob.length);
        if (out != null)
        {
          try
          {
            IOUtil.copyBinary(in, out, blob.length);
          }
          finally
          {
            IOUtil.close(out);
          }
        }
      }
      else
      {
        char[] clob = (char[])lob;
        CharArrayReader in = new CharArrayReader(clob);
        Writer out = handler.handleClob(id, clob.length);
        if (out != null)
        {
          try
          {
            IOUtil.copyCharacter(in, out, clob.length);
          }
          finally
          {
            IOUtil.close(out);
          }
        }
      }
    }
  }

  public synchronized void loadLob(byte[] id, OutputStream out) throws IOException
  {
    String key = HexUtil.bytesToHex(id);
    Object lob = lobs.get(key);
    if (lob == null)
    {
      throw new IOException("Lob not found: " + key);
    }

    if (lob instanceof byte[])
    {
      byte[] blob = (byte[])lob;
      ByteArrayInputStream in = new ByteArrayInputStream(blob);
      IOUtil.copyBinary(in, out, blob.length);
    }
    else
    {
      char[] clob = (char[])lob;
      CharArrayReader in = new CharArrayReader(clob);
      IOUtil.copyCharacter(in, new OutputStreamWriter(out), clob.length);
    }
  }

  public synchronized void writeBlob(byte[] id, long size, InputStream inputStream) throws IOException
  {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    IOUtil.copyBinary(inputStream, out, size);
    lobs.put(HexUtil.bytesToHex(id), out.toByteArray());
  }

  public synchronized void writeClob(byte[] id, long size, Reader reader) throws IOException
  {
    CharArrayWriter out = new CharArrayWriter();
    IOUtil.copyCharacter(reader, out, size);
    lobs.put(HexUtil.bytesToHex(id), out.toCharArray());
  }

  @Override
  public MEMStoreAccessor createReader(ISession session)
  {
    return new MEMStoreAccessor(this, session);
  }

  /**
   * @since 2.0
   */
  @Override
  public MEMStoreAccessor createWriter(ITransaction transaction)
  {
    return new MEMStoreAccessor(this, transaction);
  }

  /**
   * @since 2.0
   */
  public long getCreationTime()
  {
    return creationTime;
  }

  public void setCreationTime(long creationTime)
  {
    this.creationTime = creationTime;
  }

  public boolean isFirstStart()
  {
    return true;
  }

  public synchronized Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
    InternalCDOBranchManager branchManager = getRepository().getBranchManager();
    result.put(branchManager.getMainBranch(), new ArrayList<CDORevision>());

    for (Integer branchID : branchInfos.keySet())
    {
      InternalCDOBranch branch = branchManager.getBranch(branchID);
      result.put(branch, new ArrayList<CDORevision>());
    }

    for (List<InternalCDORevision> list : revisions.values())
    {
      for (InternalCDORevision revision : list)
      {
        CDOBranch branch = revision.getBranch();
        List<CDORevision> resultList = result.get(branch);
        resultList.add(revision);
      }
    }

    return result;
  }

  public synchronized EClass getObjectType(CDOID id)
  {
    return objectTypes.get(id);
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    creationTime = getRepository().getTimeStamp();

    if (getRepository().getIDGenerationLocation() == IDGenerationLocation.CLIENT)
    {
      setObjectIDTypes(Collections.singleton(CDOID.ObjectType.UUID));
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    revisions.clear();
    branchInfos.clear();
    commitInfos.clear();
    objectTypes.clear();
    properties.clear();
    resourceNameFeature = null;
    lastBranchID = 0;
    lastLocalBranchID = 0;
    super.doDeactivate();
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // Pooling of store accessors not supported
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // Pooling of store accessors not supported
    return null;
  }

  private Object getListKey(CDOID id, CDOBranch branch)
  {
    if (getRevisionParallelism() == RevisionParallelism.NONE)
    {
      return id;
    }

    return new ListKey(id, branch);
  }

  private CDOBranch getBranch(Object key)
  {
    if (key instanceof ListKey)
    {
      return ((ListKey)key).getBranch();
    }

    return getRepository().getBranchManager().getMainBranch();
  }

  private int getHighestVersion(List<InternalCDORevision> list)
  {
    int version = CDOBranchVersion.UNSPECIFIED_VERSION;
    for (InternalCDORevision revision : list)
    {
      if (revision.getVersion() > version)
      {
        version = revision.getVersion();
      }
    }

    return version;
  }

  private InternalCDORevision getRevisionByVersion(List<InternalCDORevision> list, int version)
  {
    for (InternalCDORevision revision : list)
    {
      if (revision.getVersion() == version)
      {
        return revision;
      }
    }

    return null;
  }

  private InternalCDORevision getRevision(List<InternalCDORevision> list, CDOBranchPoint branchPoint)
  {
    long timeStamp = branchPoint.getTimeStamp();
    for (InternalCDORevision revision : list)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        if (!revision.isHistorical())
        {
          return revision;
        }
      }
      else
      {
        if (revision.isValid(timeStamp))
        {
          return revision;
        }
      }
    }

    return null;
  }

  private void addRevision(List<InternalCDORevision> list, InternalCDORevision revision, boolean raw)
  {
    boolean resource = !(revision instanceof SyntheticCDORevision) && revision.isResource();
    if (resource && resourceNameFeature == null)
    {
      resourceNameFeature = revision.getEClass().getEStructuralFeature(CDOModelConstants.RESOURCE_NODE_NAME_ATTRIBUTE);
    }

    if (!raw)
    {
      // Check version conflict
      int version = revision.getVersion();
      InternalCDORevision rev = getRevisionByVersion(list, version);
      if (rev != null)
      {
        rev = getRevisionByVersion(list, version);
        throw new IllegalStateException("Concurrent modification of " + rev.getEClass().getName() + "@" + rev.getID());
      }

      // Revise old revision
      int oldVersion = version - 1;
      if (oldVersion >= CDORevision.UNSPECIFIED_VERSION)
      {
        InternalCDORevision oldRevision = getRevisionByVersion(list, oldVersion);
        if (oldRevision != null)
        {
          if (getRepository().isSupportingAudits())
          {
            oldRevision.setRevised(revision.getTimeStamp() - 1);
          }
          else
          {
            list.remove(oldRevision);
          }
        }
      }

      // Check duplicate resource
      if (resource)
      {
        checkDuplicateResource(revision);
      }
    }

    // Adjust the list
    list.add(revision);
    if (listLimit != UNLIMITED)
    {
      enforceListLimit(list);
    }

    CDOID id = revision.getID();
    if (!objectTypes.containsKey(id))
    {
      objectTypes.put(id, revision.getEClass());
    }
  }

  private void checkDuplicateResource(InternalCDORevision revision)
  {
    CDOID revisionFolder = (CDOID)revision.data().getContainerID();
    String revisionName = (String)revision.data().get(resourceNameFeature, 0);

    IStoreAccessor accessor = StoreThreadLocal.getAccessor();

    CDOID resourceID = accessor.readResourceID(revisionFolder, revisionName, revision);
    if (!CDOIDUtil.isNull(resourceID))
    {
      throw new IllegalStateException("Duplicate resource: name=" + revisionName + ", folderID=" + revisionFolder); //$NON-NLS-1$ //$NON-NLS-2$
    }
  }

  private void enforceListLimit(List<InternalCDORevision> list)
  {
    while (list.size() > listLimit)
    {
      list.remove(0);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ListKey
  {
    private CDOID id;

    private CDOBranch branch;

    public ListKey(CDOID id, CDOBranch branch)
    {
      this.id = id;
      this.branch = branch;
    }

    public CDOID getID()
    {
      return id;
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    @Override
    public int hashCode()
    {
      return id.hashCode() ^ branch.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (obj instanceof ListKey)
      {
        ListKey that = (ListKey)obj;
        return ObjectUtil.equals(id, that.getID()) && ObjectUtil.equals(branch, that.getBranch());
      }

      return false;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}:{1}", id, branch.getID());
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class CommitInfo
  {
    private CDOBranch branch;

    private long timeStamp;

    private long previousTimeStamp;

    private String userID;

    private String comment;

    public CommitInfo(CDOBranch branch, long timeStamp, long previousTimeStamp, String userID, String comment)
    {
      this.branch = branch;
      this.timeStamp = timeStamp;
      this.previousTimeStamp = previousTimeStamp;
      this.userID = userID;
      this.comment = comment;
    }

    public CDOBranch getBranch()
    {
      return branch;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public void handle(InternalCDOCommitInfoManager manager, CDOCommitInfoHandler handler)
    {
      CDOCommitInfo commitInfo = manager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, null);
      handler.handleCommitInfo(commitInfo);
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CommitInfo[{0}, {1}, {2}, {3}, {4}]", branch, timeStamp, previousTimeStamp, userID,
          comment);
    }
  }
}
