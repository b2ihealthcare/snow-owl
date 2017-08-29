/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 *    Victor Roldan Betancort - maintenance
 *    Gonzague Reydet - bug 298334
 *    Andre Dietisheim - bug 256649
 *    Caspar De Groot - bug 290032 (Sticky views)
 */
package org.eclipse.emf.internal.cdo.transaction;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo.Operation;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.EresourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceNodeImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.FailureCommitInfo;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.commit.CDORevisionAvailabilityInfo;
import org.eclipse.emf.cdo.spi.common.commit.InternalCDOCommitInfoManager;
import org.eclipse.emf.cdo.spi.common.id.AbstractCDOIDInteger;
import org.eclipse.emf.cdo.spi.common.lock.InternalCDOLockState;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOIDMapper;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver;
import org.eclipse.emf.cdo.transaction.CDOConflictResolver2;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler1;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOSavepoint;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionConflictEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionFinishedEvent;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler1;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler2;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler3;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandlerBase;
import org.eclipse.emf.cdo.transaction.CDOTransactionStartedEvent;
import org.eclipse.emf.cdo.transaction.CDOUserSavepoint;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.LegacyModeNotEnabledException;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.impl.EClassImpl.FeatureSubsetSupplier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.EContentsEList.FeatureIterator;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDONotificationBuilder;
import org.eclipse.emf.internal.cdo.object.CDOObjectMerger;
import org.eclipse.emf.internal.cdo.object.CDOObjectWrapper;
import org.eclipse.emf.internal.cdo.query.CDOQueryImpl;
import org.eclipse.emf.internal.cdo.util.CommitIntegrityCheck;
import org.eclipse.emf.internal.cdo.util.CompletePackageClosure;
import org.eclipse.emf.internal.cdo.util.IPackageClosure;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;
import org.eclipse.emf.internal.cdo.view.CDOViewImpl;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.CDOTransactionStrategy;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOSavepoint;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.ByteArrayWrapper;
import org.eclipse.net4j.util.collection.ConcurrentArray;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.options.OptionsEvent;
import org.eclipse.net4j.util.transaction.TransactionException;

/**
 * @author Eike Stepper
 */
public class CDOTransactionImpl extends CDOViewImpl implements InternalCDOTransaction
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_TRANSACTION, CDOTransactionImpl.class);

  private Object transactionHandlersLock = new Object();

  private ConcurrentArray<CDOTransactionHandler1> transactionHandlers1 = new ConcurrentArray<CDOTransactionHandler1>()
  {
    @Override
    protected CDOTransactionHandler1[] newArray(int length)
    {
      return new CDOTransactionHandler1[length];
    }
  };

  private ConcurrentArray<CDOTransactionHandler2> transactionHandlers2 = new ConcurrentArray<CDOTransactionHandler2>()
  {
    @Override
    protected CDOTransactionHandler2[] newArray(int length)
    {
      return new CDOTransactionHandler2[length];
    }
  };

  private InternalCDOSavepoint lastSavepoint = createSavepoint(null);

  private InternalCDOSavepoint firstSavepoint = lastSavepoint;

  private boolean dirty;

  private int conflict;

  private CDOTransactionStrategy transactionStrategy;

  private CDOIDGenerator idGenerator;

  private volatile long lastCommitTime = UNSPECIFIED_DATE;

  private String commitComment;

  // Bug 283985 (Re-attachment)
  private final ThreadLocal<Boolean> providingCDOID = new InheritableThreadLocal<Boolean>()
  {
    @Override
    protected Boolean initialValue()
    {
      return false;
    }
  };

  /**
   * An optional set to specify which objects in this TX are to be committed by {@link #commit()}
   */
  private Set<? extends EObject> committables;

  /**
   * A map to hold a clean (i.e. unmodified) revision for objects that have been modified or detached.
   */
  private Map<InternalCDOObject, InternalCDORevision> cleanRevisions = new ResolvingRevisionMap();

  public CDOTransactionImpl(CDOBranch branch)
  {
    super(branch, UNSPECIFIED_DATE);
  }

  public CDOTransactionImpl(String durableLockingID)
  {
    super(durableLockingID);
  }

  /**
   * @since 2.0
   */
  @Override
  public OptionsImpl options()
  {
    return (OptionsImpl)super.options();
  }

  /**
   * @since 2.0
   */
  @Override
  protected OptionsImpl createOptions()
  {
    return new OptionsImpl();
  }

  @Override
  public boolean isReadOnly()
  {
    return false;
  }

  @Override
  public synchronized boolean setBranchPoint(CDOBranchPoint branchPoint)
  {
    if (branchPoint.getTimeStamp() != UNSPECIFIED_DATE)
    {
      throw new IllegalArgumentException("Changing the target time is not supported by transactions");
    }

    if (isDirty() && !getBranch().equals(branchPoint.getBranch()))
    {
      throw new IllegalStateException("Changing the target branch is impossible while transaction is dirty");
    }

    return super.setBranchPoint(branchPoint);
  }

  public void addTransactionHandler(CDOTransactionHandlerBase handler)
  {
    synchronized (transactionHandlersLock)
    {
      if (handler instanceof CDOTransactionHandler1)
      {
        transactionHandlers1.add((CDOTransactionHandler1)handler);
      }

      if (handler instanceof CDOTransactionHandler2)
      {
        transactionHandlers2.add((CDOTransactionHandler2)handler);
      }
    }
  }

  public void removeTransactionHandler(CDOTransactionHandlerBase handler)
  {
    synchronized (transactionHandlersLock)
    {
      if (handler instanceof CDOTransactionHandler1)
      {
        transactionHandlers1.remove((CDOTransactionHandler1)handler);
      }

      if (handler instanceof CDOTransactionHandler2)
      {
        transactionHandlers2.remove((CDOTransactionHandler2)handler);
      }
    }
  }

  public CDOTransactionHandler[] getTransactionHandlers()
  {
    Set<CDOTransactionHandler> result = new HashSet<CDOTransactionHandler>();
    synchronized (transactionHandlersLock)
    {
      CDOTransactionHandler1[] handlers1 = transactionHandlers1.get();
      if (handlers1 != null)
      {
        for (CDOTransactionHandler1 handler : handlers1)
        {
          if (handler instanceof CDOTransactionHandler)
          {
            result.add((CDOTransactionHandler)handler);
          }
        }
      }

      CDOTransactionHandler2[] handlers2 = transactionHandlers2.get();
      if (handlers2 != null)
      {
        for (CDOTransactionHandler2 handler : handlers2)
        {
          if (handler instanceof CDOTransactionHandler)
          {
            result.add((CDOTransactionHandler)handler);
          }
        }
      }
    }

    return result.toArray(new CDOTransactionHandler[result.size()]);
  }

  public CDOTransactionHandler1[] getTransactionHandlers1()
  {
    synchronized (transactionHandlersLock)
    {
      return transactionHandlers1.get();
    }
  }

  public CDOTransactionHandler2[] getTransactionHandlers2()
  {
    synchronized (transactionHandlersLock)
    {
      return transactionHandlers2.get();
    }
  }

  @Override
  public synchronized boolean isDirty()
  {
    if (isClosed())
    {
      return false;
    }

    return dirty;
  }

  public void setDirty(boolean dirty)
  {
    this.dirty = dirty;
  }

  @Override
  public synchronized boolean hasConflict()
  {
    checkActive();
    return conflict != 0;
  }

  public void setConflict(InternalCDOObject object)
  {
    IEvent event = null;
    synchronized (this)
    {
      event = new ConflictEvent(object, conflict == 0);
      ++conflict;
    }

    fireEvent(event);
  }

  /**
   * @since 2.0
   */
  public synchronized Set<CDOObject> getConflicts()
  {
    Set<CDOObject> conflicts = new HashSet<CDOObject>();
    for (CDOObject object : getDirtyObjects().values())
    {
      if (object.cdoConflict())
      {
        conflicts.add(object);
      }
    }

    for (CDOObject object : getDetachedObjects().values())
    {
      if (object.cdoConflict())
      {
        conflicts.add(object);
      }
    }

    return conflicts;
  }

  public synchronized CDOChangeSetData getChangeSetData()
  {
    checkActive();
    return lastSavepoint.getAllChangeSetData();
  }

  public synchronized CDOChangeSetData merge(CDOBranchPoint source, CDOMerger merger)
  {
    return merge(source, null, merger);
  }

  public synchronized CDOChangeSetData merge(CDOBranchPoint source, CDOBranchPoint sourceBase, CDOMerger merger)
  {
    if (isDirty())
    {
      throw new IllegalStateException("Merging into dirty transactions not yet supported");
    }

    long now = getLastUpdateTime();
    CDOBranchPoint target = getBranch().getPoint(now);

    if (source.getTimeStamp() == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      source = source.getBranch().getPoint(now);
    }

    if (CDOBranchUtil.isContainedBy(source, target))
    {
      throw new IllegalArgumentException("Source is already contained in " + target);
    }

    if (sourceBase != null && !CDOBranchUtil.isContainedBy(sourceBase, source))
    {
      throw new IllegalArgumentException("Source base is not contained in " + source);
    }

    CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(target, source);

    InternalCDOSession session = getSession();
    CDORevisionAvailabilityInfo ancestorInfo = session.createRevisionAvailabilityInfo(ancestor);
    CDORevisionAvailabilityInfo targetInfo = session.createRevisionAvailabilityInfo(target);
    CDORevisionAvailabilityInfo sourceInfo = session.createRevisionAvailabilityInfo(source);
    CDORevisionAvailabilityInfo baseInfo = sourceBase != null ? session.createRevisionAvailabilityInfo(sourceBase)
        : null;

    CDOSessionProtocol sessionProtocol = session.getSessionProtocol();
    Set<CDOID> ids = sessionProtocol.loadMergeData(targetInfo, sourceInfo, ancestorInfo, baseInfo);

    session.cacheRevisions(targetInfo);
    session.cacheRevisions(sourceInfo);
    session.cacheRevisions(ancestorInfo);

    if (baseInfo != null)
    {
      session.cacheRevisions(baseInfo);
    }
    else
    {
      baseInfo = ancestorInfo;
    }

    CDOChangeSet targetChanges = createChangeSet(ids, ancestorInfo, targetInfo);
    CDOChangeSet sourceChanges = createChangeSet(ids, baseInfo, sourceInfo);

    CDOChangeSetData result = merger.merge(targetChanges, sourceChanges);
    if (result == null)
    {
      return null;
    }

    return applyChangeSet(result, baseInfo, targetInfo, source, false).getChangeSetData();
  }

  private CDOChangeSet createChangeSet(Set<CDOID> ids, CDORevisionAvailabilityInfo startInfo,
      CDORevisionAvailabilityInfo endInfo)
  {
    CDOChangeSetData data = CDORevisionUtil.createChangeSetData(ids, startInfo, endInfo);
    return CDORevisionUtil.createChangeSet(startInfo.getBranchPoint(), endInfo.getBranchPoint(), data);
  }

  /**
   * Intentionally copy pasted from {@link #applyChangeSetData(CDOChangeSetData)} to avoid bugs from production code.
   */
  public synchronized void revertChangeSetData(final CDOChangeSetData changeSetData)
  {

    if (isDirty())
    {
      throw new IllegalStateException("Applying change set into dirty transactions not yet supported");
    }

    // maybe we can also return if the CDOListFeatureDelta's list changes is empty
    // [CDORevisionDelta[Concept@OID135054:0v2 --> [CDOFeatureDelta[inboundRelationships, LIST, list=[]]]]]
    // ((CDOListFeatureDelta)((CDORevisionDelta)changeSetData.getChangedObjects().get(0)).getFeatureDeltas().get(0)).getListChanges().isEmpty()
    //
    // can happen if:
    // Start new relationship wizard, populate a relationship fully, press cancel. Editor becomes dirty,
    // DanglingReferenceException received on commit.
    // akitta: full wizard population is not required, setting the destination makes the editor and the underlying
    // transaction to dirty. probably the destination inbound relationship list is modified.
    if (changeSetData == null || changeSetData.isEmpty())
    {
      return;
    }

    // apply new objects
    final List<CDOIDAndVersion> reloadedNewRevisions = new ArrayList<CDOIDAndVersion>();
    final Map<CDOID, CDOID> idMappings = applyNewObjectsForRevert(changeSetData.getNewObjects(), reloadedNewRevisions);

    final CDOReferenceAdjuster idMapper = new CDOIDMapper(idMappings);

    long maxTempId = -1;

    for (final CDOIDAndVersion newIdAndVersion : reloadedNewRevisions)
    {
      // attempt to find the max CDO ID value to avoid temporary ID collision
      final CDOID tempCdoId = newIdAndVersion.getID();
      if (tempCdoId.isTemporary() && tempCdoId instanceof AbstractCDOIDInteger)
      {
        final AbstractCDOIDInteger cdoId = (AbstractCDOIDInteger)tempCdoId;
        if (cdoId.getIntValue() > maxTempId)
        {
          maxTempId = cdoId.getIntValue();
        }
      }
    }

    // fast wind the temporary ID generator to avoid collision
    for (int i = 0; i < maxTempId + 1; i++)
    {
      idGenerator.generateCDOID(null);
    }

    // apply changed objects
    Map<CDOID, CDOObject> dirtyObjects = lastSavepoint.getDirtyObjects();
    ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas();
    List<CDORevisionDelta> notificationDeltas = new ArrayList<CDORevisionDelta>();
    Map<InternalCDOObject, InternalCDORevision> oldObjectsMap = new HashMap<InternalCDOObject, InternalCDORevision>();

    for (CDORevisionKey key : changeSetData.getChangedObjects())
    {
      InternalCDORevisionDelta changeDelta = (InternalCDORevisionDelta)key;
      CDOID id = changeDelta.getID();
      InternalCDOObject object = getObjectIfExists(id);

      // can apply changes only if object exists in the view
      if (object != null)
      {
        // get the current revision in the branch
        InternalCDORevision currentRevision = object.cdoRevision();

        oldObjectsMap.put(object, currentRevision);

        // map the revision delta
        InternalCDORevisionDelta targetGoalDelta = (InternalCDORevisionDelta)changeDelta.copy();
        targetGoalDelta.setBranch(currentRevision.getBranch());
        targetGoalDelta.setVersion(currentRevision.getVersion());

        // create the goal revision and apply the changes
        InternalCDORevision goalRevision = currentRevision.copy();
        getSession().resolveAllElementProxies(goalRevision);
        goalRevision.setBranchPoint(this);
        goalRevision.setVersion(currentRevision.getBranch().equals(getBranch()) ? currentRevision.getVersion() : 1);
        goalRevision.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);

        for (CDOFeatureDelta featureDelta : targetGoalDelta.getFeatureDeltas())
        {
          // it is possible, that the indexes in the list feature deltas are incorrect, as the list of the revision has
          // been changed, handle this case manually
          if (featureDelta instanceof CDOListFeatureDelta)
          {
            applyListFeatureDelta((CDOListFeatureDelta)featureDelta, goalRevision);
          }
          else
          {
            featureDelta.apply(goalRevision);
          }
        }

        revisionDeltas.put(id, targetGoalDelta);
        notificationDeltas.add(targetGoalDelta);

        // set the revision as current revision of the object
        object.cdoInternalSetState(CDOState.DIRTY);
        object.cdoInternalSetRevision(goalRevision);

        dirtyObjects.put(id, object);
        dirty = true;

        object.cdoInternalPostLoad();
      }

    }

    // update the clean revisions from the oldRevision map since these objects have been loaded from a brand new
    // transaction
    cleanRevisions.putAll(oldObjectsMap);

    // apply detached objects
    Set<CDOObject> detachedObjects = applyDetachedObjects(changeSetData.getDetachedObjects(),
        new ArrayList<CDOIDAndVersion>());

    final Map<CDOID, InternalCDORevision> oldRevisions = new HashMap<CDOID, InternalCDORevision>();
    for (final Entry<InternalCDOObject, InternalCDORevision> entry : oldObjectsMap.entrySet())
    {
      oldRevisions.put(entry.getKey().cdoID(), entry.getValue());
    }

    if (!notificationDeltas.isEmpty() || !detachedObjects.isEmpty())
    {
      sendDeltaNotifications(notificationDeltas, detachedObjects, oldRevisions);
    }

    if (!idMappings.isEmpty())
    {

      for (final CDORevisionDelta dirtyObjectDelta : revisionDeltas.values())
      {
        ((InternalCDORevisionDelta)dirtyObjectDelta).adjustReferences(idMapper);
      }

      // adjust CDO IDs for new objects as well. e.g.: reference set member container
      for (final CDOIDAndVersion newObjectIdAndVersion : reloadedNewRevisions)
      {

        if (newObjectIdAndVersion instanceof InternalCDORevision)
        {

          applyIDMappings((InternalCDORevision)newObjectIdAndVersion, idMappings, idMapper);

        }

      }

    }

  }

  private void applyIDMappings(InternalCDORevision revision, Map<CDOID, CDOID> idMappings, CDOReferenceAdjuster idMapper)
  {
    if (revision != null)
    {
      CDOID newID = idMappings.get(revision.getID());
      if (newID != null)
      {
        revision.setID(newID);
      }

      revision.adjustReferences(idMapper);
    }
  }

  /**
   * Applies an arbitrary change set on the transaction.
   * 
   * @since 4.0.1
   */
  public synchronized void applyChangeSetData(CDOChangeSetData changeSetData)
  {

    if (isDirty())
    {
      throw new IllegalStateException("Applying change set into dirty transactions not yet supported");
    }

    // maybe we can also return if the CDOListFeatureDelta's list changes is empty
    // [CDORevisionDelta[Concept@OID135054:0v2 --> [CDOFeatureDelta[inboundRelationships, LIST, list=[]]]]]
    // ((CDOListFeatureDelta)((CDORevisionDelta)changeSetData.getChangedObjects().get(0)).getFeatureDeltas().get(0)).getListChanges().isEmpty()
    //
    // can happen if:
    // Start new relationship wizard, populate a relationship fully, press cancel. Editor becomes dirty,
    // DanglingReferenceException received on commit.
    // akitta: full wizard population is not required, setting the destination makes the editor and the underlying
    // transaction to dirty. probably the destination inbound relationship list is modified.
    if (changeSetData == null || changeSetData.isEmpty())
    {
      return;
    }

    // apply new objects
    final List<CDOIDAndVersion> reloadedNewRevisions = new ArrayList<CDOIDAndVersion>();
    applyNewObjects(changeSetData.getNewObjects(), reloadedNewRevisions);

    long maxTempId = -1;

    for (final CDOIDAndVersion newIdAndVersion : reloadedNewRevisions)
    {
      // attempt to find the max CDO ID value to avoid temporary ID collision
      final CDOID tempCdoId = newIdAndVersion.getID();
      if (tempCdoId.isTemporary() && tempCdoId instanceof AbstractCDOIDInteger)
      {
        final AbstractCDOIDInteger cdoId = (AbstractCDOIDInteger)tempCdoId;
        if (cdoId.getIntValue() > maxTempId)
        {
          maxTempId = cdoId.getIntValue();
        }
      }
    }

    // fast wind the temporary ID generator to avoid collision
    for (int i = 0; i < maxTempId + 1; i++)
    {
      idGenerator.generateCDOID(null);
    }

    // apply changed objects
    Map<CDOID, CDOObject> dirtyObjects = lastSavepoint.getDirtyObjects();
    ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas();
    List<CDORevisionDelta> notificationDeltas = new ArrayList<CDORevisionDelta>();
    Map<InternalCDOObject, InternalCDORevision> oldObjectsMap = new HashMap<InternalCDOObject, InternalCDORevision>();

    for (CDORevisionKey key : changeSetData.getChangedObjects())
    {
      InternalCDORevisionDelta changeDelta = (InternalCDORevisionDelta)key;
      CDOID id = changeDelta.getID();
      InternalCDOObject object = getObjectIfExists(id);

      // can apply changes only if object exists in the view
      if (object != null)
      {
        // get the current revision in the branch
        InternalCDORevision currentRevision = object.cdoRevision();

        oldObjectsMap.put(object, currentRevision);

        // map the revision delta
        InternalCDORevisionDelta targetGoalDelta = (InternalCDORevisionDelta)changeDelta.copy();
        targetGoalDelta.setBranch(currentRevision.getBranch());
        targetGoalDelta.setVersion(currentRevision.getVersion());

        // create the goal revision and apply the changes
        InternalCDORevision goalRevision = currentRevision.copy();
        getSession().resolveAllElementProxies(goalRevision);
        goalRevision.setBranchPoint(this);
        goalRevision.setVersion(currentRevision.getBranch().equals(getBranch()) ? currentRevision.getVersion() : 1);
        goalRevision.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);

        for (CDOFeatureDelta featureDelta : targetGoalDelta.getFeatureDeltas())
        {
          // it is possible, that the indexes in the list feature deltas are incorrect, as the list of the revision has
          // been changed, handle this case manually
          if (featureDelta instanceof CDOListFeatureDelta)
          {
            applyListFeatureDelta((CDOListFeatureDelta)featureDelta, goalRevision);
          }
          else
          {
            featureDelta.apply(goalRevision);
          }
        }

        revisionDeltas.put(id, targetGoalDelta);
        notificationDeltas.add(targetGoalDelta);

        // set the revision as current revision of the object
        object.cdoInternalSetState(CDOState.DIRTY);
        object.cdoInternalSetRevision(goalRevision);

        dirtyObjects.put(id, object);
        dirty = true;

        object.cdoInternalPostLoad();
      }

    }

    // update the clean revisions from the oldRevision map since these objects have been loaded from a brand new
    // transaction
    cleanRevisions.putAll(oldObjectsMap);

    // apply detached objects
    Set<CDOObject> detachedObjects = applyDetachedObjects(changeSetData.getDetachedObjects(),
        new ArrayList<CDOIDAndVersion>());

    final Map<CDOID, InternalCDORevision> oldRevisions = new HashMap<CDOID, InternalCDORevision>();
    for (final Entry<InternalCDOObject, InternalCDORevision> entry : oldObjectsMap.entrySet())
    {
      oldRevisions.put(entry.getKey().cdoID(), entry.getValue());
    }

    if (!notificationDeltas.isEmpty() || !detachedObjects.isEmpty())
    {
      sendDeltaNotifications(notificationDeltas, detachedObjects, oldRevisions);
    }

  }

  /**
   * Applies the specified <code>listFeatureDelta</code> on the revision, adjusting the indexes of the feature deltas if
   * they are out of the bounds of the revision's list (add operation), or pointing to an incorrect element in the list
   * (remove operation).
   * 
   * @since 4.0.1
   */
  private void applyListFeatureDelta(CDOListFeatureDelta listFeatureDelta, InternalCDORevision goalRevision)
  {
    Iterator<CDOFeatureDelta> featureIterator = listFeatureDelta.getListChanges().iterator();
    int originalRemoveIndex = -1;
    int adjustedRemoveIndex = -1;

    while (featureIterator.hasNext())
    {
      CDOFeatureDelta featureDelta = featureIterator.next();

      if (featureDelta instanceof CDOAddFeatureDelta)
      {
        CDOAddFeatureDelta addFeatureDelta = (CDOAddFeatureDelta)featureDelta;

        CDOList list = goalRevision.getList(addFeatureDelta.getFeature());

        if (addFeatureDelta.getValue() instanceof CDOObject)
        {
          // if the added value is transient, it is possible, that it points to another view
          ((InternalCDOObject)addFeatureDelta.getValue()).cdoInternalSetView(this);
        }

       	if (addFeatureDelta.getFeature().isUnique()) {
       		if (list.contains(addFeatureDelta.getValue())) {
       			featureIterator.remove();
       			continue;
       		}
       	}
       	
       	if (getObjectIfExists((CDOID) addFeatureDelta.getValue()) == null) {
       		featureIterator.remove();
       		continue;
       	}
       	
        // XXX (apeteri): Always add to the end of the list
        list.add(addFeatureDelta.getValue());
        ((CDOAddFeatureDeltaImpl)addFeatureDelta).setIndex(list.size() - 1);

        // Clear stored index values
        originalRemoveIndex = -1;
        adjustedRemoveIndex = -1;
      }
      else if (featureDelta instanceof CDORemoveFeatureDelta)
      {
        CDORemoveFeatureDelta removeFeatureDelta = (CDORemoveFeatureDelta)featureDelta;
        CDOList list = goalRevision.getList(removeFeatureDelta.getFeature());

        // Set index values
        originalRemoveIndex = removeFeatureDelta.getIndex();

        if (originalRemoveIndex >= list.size()
            || differentValue(removeFeatureDelta.getValue(), list, originalRemoveIndex))
        {
          adjustedRemoveIndex = list.indexOf(removeFeatureDelta.getValue());
          if (adjustedRemoveIndex > 0)
          {
            list.remove(adjustedRemoveIndex);
            ((CDORemoveFeatureDeltaImpl)removeFeatureDelta).setIndex(adjustedRemoveIndex);
          }
          else
          {
            featureIterator.remove();
          }
        }
        else
        {
          list.remove(originalRemoveIndex);
        }
      }
      else if (featureDelta instanceof CDOMoveFeatureDelta)
      {
        /*
         * XXX (apeteri): Special handling for move feature deltas related to unordered lists; any other attempt of
         * moving elements will result in moving a possibly unexpected element to a possibly incorrect destination.
         */
        CDOMoveFeatureDelta moveFeatureDelta = (CDOMoveFeatureDelta)featureDelta;
        CDOList list = goalRevision.getList(moveFeatureDelta.getFeature());

        // Moving from past the end of the list? Snap to last item
        if (moveFeatureDelta.getOldPosition() >= list.size())
        {
          ((CDOMoveFeatureDeltaImpl)moveFeatureDelta).setOldPosition(list.size() - 1);
        }

        // Moving to an index which has been removed? Use the adjusted index
        if (moveFeatureDelta.getNewPosition() == originalRemoveIndex && adjustedRemoveIndex != -1)
        {
          ((CDOMoveFeatureDeltaImpl)moveFeatureDelta).setNewPosition(adjustedRemoveIndex);
        }

        list.move(moveFeatureDelta.getNewPosition(), moveFeatureDelta.getOldPosition());

        // Clear stored index values
        originalRemoveIndex = -1;
        adjustedRemoveIndex = -1;
      }
      else if (featureDelta instanceof CDOListFeatureDelta)
      {
        applyListFeatureDelta((CDOListFeatureDelta)featureDelta, goalRevision);

        // Clear stored index values
        originalRemoveIndex = -1;
        adjustedRemoveIndex = -1;
      }
      else
      {
        featureDelta.apply(goalRevision);

        // Clear stored index values
        originalRemoveIndex = -1;
        adjustedRemoveIndex = -1;
      }
    }
    
    CDOList list = goalRevision.getList(listFeatureDelta.getFeature());
    
	Set<Object> visited = new HashSet<>();
	Iterator<Object> iterator = list.iterator();
	
	while (iterator.hasNext()) {
		
		Object listElement = iterator.next();
		
		if (!(listElement instanceof CDOID)) {
			throw new RuntimeException(String.format("Unknown type in list feature (%s) delta: %s", listFeatureDelta.getFeature().getName(),
					listElement.getClass().getSimpleName()));
		}
		
		if (listFeatureDelta.getFeature().isUnique()) {
			if (!visited.add(listElement)) {
				iterator.remove();
				continue;
			}
		}
		
		if (getObjectIfExists((CDOID) listElement) == null) {
			iterator.remove();
		}
		
	}
    
  }

  private boolean differentValue(Object value, CDOList list, int index)
  {
    return !value.equals(list.get(index));
  }

  @Deprecated
  public Pair<CDOChangeSetData, Pair<Map<CDOID, CDOID>, List<CDOID>>> applyChangeSetData(
      CDOChangeSetData changeSetData, CDORevisionProvider ancestorProvider, CDORevisionProvider targetProvider,
      CDOBranchPoint source)
  {
    throw new UnsupportedOperationException();
  }

  public synchronized ApplyChangeSetResult applyChangeSet(CDOChangeSetData changeSetData,
      CDORevisionProvider baseProvider, CDORevisionProvider targetProvider, CDOBranchPoint source,
      boolean keepVersions) throws ChangeSetOutdatedException
  {
    ApplyChangeSetResult result = new ApplyChangeSetResult();

    // Merges from local offline branches may require additional ID mappings: localID -> tempID
    if (source != null && source.getBranch().isLocal())
    {
      applyLocalIDMapping(changeSetData, result);
    }

    // New objects
    applyNewObjects(changeSetData.getNewObjects(), result.getChangeSetData().getNewObjects());

    // Detached objects
    Set<CDOObject> detachedSet = applyDetachedObjects(changeSetData.getDetachedObjects(), result.getChangeSetData()
        .getDetachedObjects());

    // Changed objects
    Map<CDOID, InternalCDORevision> oldRevisions = applyChangedObjects(changeSetData.getChangedObjects(),
        baseProvider, targetProvider, keepVersions, result.getChangeSetData().getChangedObjects());

    // Delta notifications
    Collection<CDORevisionDelta> notificationDeltas = lastSavepoint.getRevisionDeltas().values();
    if (!notificationDeltas.isEmpty() || !detachedSet.isEmpty())
    {
      sendDeltaNotifications(notificationDeltas, detachedSet, oldRevisions);
    }

    return result;
  }

  private void applyLocalIDMapping(CDOChangeSetData changeSetData, ApplyChangeSetResult result)
  {
    Map<CDOID, CDOID> idMappings = result.getIDMappings();

    // Collect needed ID mappings
    for (CDOIDAndVersion key : changeSetData.getNewObjects())
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      if (revision.getBranch().isLocal())
      {
        CDOID oldID = revision.getID();
        CDOID newID = createIDForNewObject(null);
        idMappings.put(oldID, newID);

        revision.setID(newID);
        revision.setVersion(0);
      }
    }

    if (!idMappings.isEmpty())
    {
      // Apply collected ID mappings
      CDOIDMapper idMapper = new CDOIDMapper(idMappings);
      idMapper.setAllowUnmappedTempIDs(true);

      for (CDOIDAndVersion key : changeSetData.getNewObjects())
      {
        InternalCDORevision revision = (InternalCDORevision)key;
        revision.adjustReferences(idMapper);
      }

      for (CDORevisionKey key : changeSetData.getChangedObjects())
      {
        InternalCDORevisionDelta revisionDelta = (InternalCDORevisionDelta)key;
        if (revisionDelta.adjustReferences(idMapper))
        {
          result.getAdjustedObjects().add(revisionDelta.getID());
        }
      }
    }
  }

  private Map<CDOID, CDOID> applyNewObjectsForRevert(List<CDOIDAndVersion> newObjects, List<CDOIDAndVersion> result)
  {
    final Map<CDOID, CDOID> idMap = new HashMap<CDOID, CDOID>(newObjects.size());

    for (CDOIDAndVersion key : newObjects)
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      CDOID id = revision.getID();
      if (getObjectIfExists(id) == null)
      {
        // XXX (apeteri): replace actual instances with temp CDOIDs
        revision.adjustReferences(CDOObjectToCDOIDAdjuster.INSTANCE);
        InternalCDOObject object = newInstance(revision.getEClass());
        object.cdoInternalSetView(this);

        final CDOID tempId = idGenerator.generateCDOID(object);
        object.cdoInternalSetID(tempId);
        final InternalCDORevision copyRevision = revision.copy();
        copyRevision.setID(tempId);
        copyRevision.setVersion(0);

        object.cdoInternalSetRevision(copyRevision);

        idMap.put(id, tempId);

        object.cdoInternalSetState(CDOState.NEW);
        object.cdoInternalPostLoad();

        registerObject(object);
        registerAttached(object, true);
        result.add(copyRevision);
        dirty = true;
      }
    }

    // for CDO ID adjusting on revisions and deltas.
    // basically if we replace a permanent CDO ID on a revision
    // and on an object we have to modify the IDs on revision deltas.
    // E.g.: we have to replace the CDO ID of a new concept in the CDO ADD revision delta
    return idMap;

  }

  private void applyNewObjects(List<CDOIDAndVersion> newObjects, List<CDOIDAndVersion> result)
  {
    for (CDOIDAndVersion key : newObjects)
    {
      InternalCDORevision revision = (InternalCDORevision)key;
      CDOID id = revision.getID();
      if (getObjectIfExists(id) == null)
      {
        // XXX (apeteri): replace actual instances with temp CDOIDs
        revision.adjustReferences(CDOObjectToCDOIDAdjuster.INSTANCE);
        InternalCDOObject object = newInstance(revision.getEClass());
        object.cdoInternalSetView(this);
        object.cdoInternalSetRevision(revision);
        object.cdoInternalSetID(id);
        object.cdoInternalSetState(CDOState.NEW);
        object.cdoInternalPostLoad();

        registerObject(object);
        registerAttached(object, true);
        result.add(revision);
        dirty = true;
      }
    }
  }

  private Set<CDOObject> applyDetachedObjects(List<CDOIDAndVersion> detachedObjects, List<CDOIDAndVersion> result)
  {
    Set<CDOObject> detachedSet = new HashSet<CDOObject>();
    for (CDOIDAndVersion key : detachedObjects)
    {
      CDOID id = key.getID();
      InternalCDOObject object = getObjectIfExists(id);
      if (object != null)
      {
        result.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
        CDOStateMachine.INSTANCE.detach(object);
        detachedSet.add(object);
        dirty = true;
      }
    }

    return detachedSet;
  }

  private Map<CDOID, InternalCDORevision> applyChangedObjects(List<CDORevisionKey> changedObjects,
      CDORevisionProvider baseProvider, CDORevisionProvider targetProvider, boolean keepVersions,
      List<CDORevisionKey> result) throws ChangeSetOutdatedException
  {
    Map<CDOID, InternalCDORevision> oldRevisions = new HashMap<CDOID, InternalCDORevision>();

    Map<CDOID, CDOObject> dirtyObjects = lastSavepoint.getDirtyObjects();
    ConcurrentMap<CDOID, CDORevisionDelta> revisionDeltas = lastSavepoint.getRevisionDeltas();

    for (CDORevisionKey key : changedObjects)
    {
      InternalCDORevisionDelta ancestorGoalDelta = (InternalCDORevisionDelta)key;
      ancestorGoalDelta.setTarget(null);
      CDOID id = ancestorGoalDelta.getID();
      InternalCDORevision baseRevision = (InternalCDORevision)baseProvider.getRevision(id);
      InternalCDOObject object = getObjectIfExists(id);
      
      if (object == null) 
      {
    	OM.LOG.warn(String.format("Skipping feature delta '%s' on possible deleted object '%s'", ancestorGoalDelta, CDOIDUtil.getLong(id)));
    	continue;
	  }
      
      boolean revisionChanged = false;

      InternalCDORevision targetRevision = object.cdoRevision();
      if (targetRevision == null)
      {
        targetRevision = (InternalCDORevision)targetProvider.getRevision(id);
        object.cdoInternalSetRevision(targetRevision);
        revisionChanged = true;
      }

      oldRevisions.put(id, targetRevision);

      // to avoid PartialCollectionLoadingNotSupportedException when comparing revisions
      resolveElementProxies(targetRevision);
      resolveElementProxies(baseRevision);

      InternalCDORevision goalRevision = baseRevision.copy();
      goalRevision.setBranchPoint(this);
      if (!keepVersions)
      {
        goalRevision.setVersion(targetRevision.getVersion());
        goalRevision.setVersion(targetRevision.getBranch().equals(baseRevision.getBranch()) ? targetRevision
            .getVersion() : CDORevision.FIRST_VERSION);
      }
      else
      {
        goalRevision.setBranchPoint(baseRevision.getBranch().getPoint(baseRevision.getTimeStamp()));
      }

      goalRevision.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);
      for (CDOFeatureDelta featureDelta : ancestorGoalDelta.getFeatureDeltas())
      {
        if (featureDelta instanceof CDOListFeatureDelta)
        {
          applyListFeatureDelta((CDOListFeatureDelta)featureDelta, goalRevision);
        }
        else
        {
          featureDelta.apply(goalRevision);
        }
      }

      InternalCDORevisionDelta targetGoalDelta = goalRevision.compare(targetRevision);
      targetGoalDelta.setTarget(null);

      if (!targetGoalDelta.isEmpty())
      {
        if (keepVersions && targetGoalDelta.getVersion() != baseRevision.getVersion())
        {
          throw new ChangeSetOutdatedException();
        }

        revisionDeltas.put(id, targetGoalDelta);
        result.add(targetGoalDelta);

        // handle reattached objects.
        if (lastSavepoint.getDetachedObjects().containsKey(id))
        {
          CDOStateMachine.INSTANCE.attach(object, this);
        }

        object.cdoInternalSetState(CDOState.DIRTY);
        object.cdoInternalSetRevision(goalRevision);
        revisionChanged = true;

        dirtyObjects.put(id, object);
        dirty = true;
      }

      if (revisionChanged)
      {
        object.cdoInternalPostLoad();
      }
    }

    return oldRevisions;
  }

  /* resolves the element proxies on all persisted many valued feature if required. */
  private void resolveElementProxies(final InternalCDORevision revision)
  {

    if (null == revision)
    {

      throw new NullPointerException("revision");

    }

    final CDOClassInfo classInfo = revision.getClassInfo();

    for (final EStructuralFeature feature : classInfo.getAllPersistentFeatures())
    {

      if (feature.isMany())
      {

        final CDOList list = revision.getList(feature);

        if (list instanceof CDOListWithElementProxiesImpl)
        {

          for (int i = 0; i < list.size(); i++)
          {

            final Object object = list.get(i, true);

            if (object instanceof CDOElementProxy)
            {

              getSession().resolveElementProxy(revision, feature, i, ((CDOElementProxy)object).getIndex());

            }

          }

        }

      }

    }

  }

  private InternalCDOObject getObjectIfExists(CDOID id)
  {
    try
    {
      return getObject(id);
    }
    catch (ObjectNotFoundException ex)
    {
      return null;
    }
  }

  /*
   * Synchronized through InvalidationRunnable.run()
   */
  @Override
  protected synchronized void handleConflicts(Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> conflicts,
      List<CDORevisionDelta> deltas)
  {
    CDOConflictResolver[] resolvers = options().getConflictResolvers();
    if (resolvers.length == 0)
    {
      return;
    }

    // Remember original state to be able to restore it after an exception
    List<CDOState> states = new ArrayList<CDOState>(conflicts.size());
    List<CDORevision> revisions = new ArrayList<CDORevision>(conflicts.size());
    for (CDOObject conflict : conflicts.keySet())
    {
      states.add(conflict.cdoState());
      revisions.add(conflict.cdoRevision());
    }

    int resolved = 0;

    try
    {
      Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> remaining = new HashMap<CDOObject, Pair<CDORevision, CDORevisionDelta>>(
          conflicts);
      for (CDOConflictResolver resolver : resolvers)
      {
        if (resolver instanceof CDOConflictResolver2)
        {
          ((CDOConflictResolver2)resolver).resolveConflicts(Collections.unmodifiableMap(remaining), deltas);
        }
        else
        {
          resolver.resolveConflicts(Collections.unmodifiableSet(remaining.keySet()));
        }

        for (Iterator<CDOObject> it = remaining.keySet().iterator(); it.hasNext();)
        {
          CDOObject object = it.next();
          if (!object.cdoConflict())
          {
            ++resolved;
            it.remove();
          }
        }
      }
    }
    catch (Exception ex)
    {
      // Restore original state
      Iterator<CDOState> state = states.iterator();
      Iterator<CDORevision> revision = revisions.iterator();
      for (CDOObject object : conflicts.keySet())
      {
        ((InternalCDOObject)object).cdoInternalSetState(state.next());
        ((InternalCDOObject)object).cdoInternalSetRevision(revision.next());
      }

      throw WrappedException.wrap(ex);
    }

    conflict -= resolved;
  }

  /**
   * @deprecated {@link #createIDForNewObject(EObject object)} is called since 4.1.
   */
  @Deprecated
  public synchronized CDOIDTemp getNextTemporaryID()
  {
    throw new UnsupportedOperationException();
  }

  public CDOID createIDForNewObject(EObject object)
  {
    return idGenerator.generateCDOID(object);
  }

  public synchronized CDOResourceFolder createResourceFolder(String path)
  {
    if (path.endsWith(CDOURIUtil.SEGMENT_SEPARATOR))
    {
      path = path.substring(0, path.length() - 1);
    }

    CDOResourceFolder folder = EresourceFactory.eINSTANCE.createCDOResourceFolder();
    int pos = path.lastIndexOf(CDOURIUtil.SEGMENT_SEPARATOR_CHAR);
    if (pos <= 0)
    {
      String name = path.substring(pos == 0 ? 1 : 0);
      folder.setName(name);

      getRootResource().getContents().add(folder);
    }
    else
    {
      String name = path.substring(pos + 1);
      folder.setName(name);

      path = path.substring(0, pos);
      CDOResourceNode parent = null;

      try
      {
        parent = getResourceNode(path);
      }
      catch (Exception ex)
      {
        parent = createResourceFolder(path);
      }

      if (parent instanceof CDOResourceFolder)
      {
        ((CDOResourceFolder)parent).getNodes().add(folder);
      }
      else
      {
        throw new CDOException("Parent is not a folder: " + parent);
      }
    }

    return folder;
  }

  public synchronized CDOResource createResource(String path)
  {
    checkActive();
    URI uri = CDOURIUtil.createResourceURI(this, path);
    return (CDOResource)getResourceSet().createResource(uri);
  }

  public synchronized CDOResource getOrCreateResource(String path)
  {
    checkActive();

    try
    {
      CDOID id = getResourceNodeID(path);
      if (!CDOIDUtil.isNull(id))
      {
        return (CDOResource)getObject(id);
      }
    }
    catch (Exception ignore)
    {
      // Just create the missing resource
    }

    return createResource(path);
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized void attachResource(CDOResourceImpl resource)
  {
    if (resource.isExisting())
    {
      super.attachResource(resource);
    }
    else
    {
      // ResourceSet.createResource(uri) was called!!
      attachNewResource(resource);
    }
  }

  private void attachNewResource(CDOResourceImpl resource)
  {
    URI uri = resource.getURI();
    List<String> names = CDOURIUtil.analyzePath(uri);
    String resourceName = names.isEmpty() ? null : names.remove(names.size() - 1);

    CDOResourceFolder folder = getOrCreateResourceFolder(names);
    attachNewResourceNode(folder, resourceName, resource);
  }

  public synchronized CDOResourceFolder getOrCreateResourceFolder(String path)
  {
    checkActive();

    try
    {
      CDOID id = getResourceNodeID(path);
      if (!CDOIDUtil.isNull(id))
      {
        return (CDOResourceFolder)getObject(id);
      }
    }
    catch (Exception ignore)
    {
      // Just create the missing folder
    }

    return createResourceFolder(path);
  }

  /**
   * @return never <code>null</code>;
   * @since 2.0
   */
  public synchronized CDOResourceFolder getOrCreateResourceFolder(List<String> names)
  {
    CDOResourceFolder folder = null;
    for (String name : names)
    {
      CDOResourceNode node;

      try
      {
        CDOID folderID = folder == null ? null : folder.cdoID();
        node = getResourceNode(folderID, name);
      }
      catch (CDOException ex)
      {
        node = EresourceFactory.eINSTANCE.createCDOResourceFolder();
        attachNewResourceNode(folder, name, node);
      }

      if (node instanceof CDOResourceFolder)
      {
        folder = (CDOResourceFolder)node;
      }
      else
      {
        throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.0"), node)); //$NON-NLS-1$
      }
    }

    return folder;
  }

  private void attachNewResourceNode(CDOResourceFolder folder, String name, CDOResourceNode newNode)
  {
    CDOResourceNodeImpl node = (CDOResourceNodeImpl)newNode;
    node.basicSetName(name, false);
    if (folder == null)
    {
      if (node.isRoot())
      {
        CDOStateMachine.INSTANCE.attach(node, this);
      }
      else
      {
        getRootResource().getContents().add(node);
      }
    }
    else
    {
      node.basicSetFolder(folder, false);
    }
  }

  /**
   * @since 2.0
   */
  public synchronized void detach(CDOResourceImpl cdoResource)
  {
    CDOStateMachine.INSTANCE.detach(cdoResource);
  }

  /**
   * @since 4.1
   */
  public InternalCDOSavepoint getFirstSavepoint()
  {
    return firstSavepoint;
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDOSavepoint getLastSavepoint()
  {
    checkActive();
    return lastSavepoint;
  }

  /**
   * @since 2.0
   */
  public synchronized CDOTransactionStrategy getTransactionStrategy()
  {
    if (transactionStrategy == null)
    {
      transactionStrategy = CDOTransactionStrategy.DEFAULT;
      transactionStrategy.setTarget(this);
    }

    return transactionStrategy;
  }

  /**
   * @since 2.0
   */
  public synchronized void setTransactionStrategy(CDOTransactionStrategy transactionStrategy)
  {
    if (this.transactionStrategy != null)
    {
      this.transactionStrategy.unsetTarget(this);
    }

    this.transactionStrategy = transactionStrategy;

    if (this.transactionStrategy != null)
    {
      this.transactionStrategy.setTarget(this);
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected synchronized CDOID getRootOrTopLevelResourceNodeID(String name)
  {
    if (dirty)
    {
      CDOResourceNode node = getRootResourceNode(name, getDirtyObjects().values());
      if (node != null)
      {
        return node.cdoID();
      }

      node = getRootResourceNode(name, getNewObjects().values());
      if (node != null)
      {
        return node.cdoID();
      }
    }

    CDOID id = super.getRootOrTopLevelResourceNodeID(name);
    if (getLastSavepoint().getAllDetachedObjects().containsKey(id) || getDirtyObjects().containsKey(id))
    {
      throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.1"), name)); //$NON-NLS-1$
    }

    return id;
  }

  private CDOResourceNode getRootResourceNode(String name, Collection<? extends CDOObject> objects)
  {
    for (CDOObject object : objects)
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        if (node.getFolder() == null && ObjectUtil.equals(name, node.getName()))
        {
          return node;
        }
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized InternalCDOObject getObject(CDOID id, boolean loadOnDemand)
  {
    checkActive();
    if (CDOIDUtil.isNull(id))
    {
      return null;
    }

    if (isObjectNew(id) && isObjectDetached(id))
    {
      throw new ObjectNotFoundException(id, this);
    }

    return super.getObject(id, loadOnDemand);
  }

  @Override
  public boolean isObjectNew(CDOID id)
  {
    return lastSavepoint.isNewObject(id);
  }

  private boolean isObjectDetached(CDOID id)
  {
    return lastSavepoint.getAllDetachedObjects().containsKey(id);
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDOCommitContext createCommitContext()
  {
    return new CDOCommitContextImpl(this);
  }

  /**
   * @since 2.0
   */
  public synchronized CDOCommitInfo commit(IProgressMonitor progressMonitor) throws CommitException
  {
    try
    {
      checkActive();
      if (hasConflict())
      {
        throw new CommitException(Messages.getString("CDOTransactionImpl.2")); //$NON-NLS-1$
      }

      if (progressMonitor == null)
      {
        progressMonitor = new NullProgressMonitor();
      }

      CDOTransactionStrategy transactionStrategy = getTransactionStrategy();
      CDOCommitInfo info = transactionStrategy.commit(this, progressMonitor);
      if (info != null)
      {
        lastCommitTime = info.getTimeStamp();
      }

      return info;
    }
    catch (CommitException ex)
    {
      throw ex;
    }
    catch (Throwable t)
    {
      throw new CommitException(t);
    }
  }

  public synchronized CDOCommitInfo commit() throws CommitException
  {
    return commit(null);
  }

  /**
   * @since 2.0
   */
  public synchronized void rollback()
  {
    checkActive();

    CDOTransactionStrategy strategy = getTransactionStrategy();
    strategy.rollback(this, firstSavepoint);

    cleanUp(null);
  }

  private void removeObject(CDOID id, final CDOObject object)
  {
    InternalCDOObject internal = (InternalCDOObject)object;
    internal.cdoInternalSetState(CDOState.TRANSIENT);
    removeObject(id);

    if (object instanceof CDOResource)
    {
      InternalCDOViewSet viewSet = getViewSet();
      viewSet.executeWithoutNotificationHandling(new Callable<Boolean>()
      {
        public Boolean call() throws Exception
        {
          EList<Resource> resources = getResourceSet().getResources();
          resources.remove(object);
          return true;
        }
      });
    }

    internal.cdoInternalSetID(null);
    internal.cdoInternalSetRevision(null);
    internal.cdoInternalSetView(null);
  }

  private Set<CDOID> rollbackCompletely(CDOUserSavepoint savepoint)
  {
    Set<CDOID> idsOfNewObjectsWithDeltas = new HashSet<CDOID>();

    // Start from the last savepoint and come back up to the active
    for (InternalCDOSavepoint itrSavepoint = lastSavepoint; itrSavepoint != null; itrSavepoint = itrSavepoint
        .getPreviousSavepoint())
    {
      Set<Object> toBeDetached = new HashSet<Object>();

      // Rollback new objects attached after the save point
      Map<CDOID, CDOObject> newObjectsMap = itrSavepoint.getNewObjects();
      for (CDOID id : newObjectsMap.keySet())
      {
        CDOObject object = newObjectsMap.get(id);
        toBeDetached.add(id);
        toBeDetached.add(object);
        toBeDetached.add(((InternalCDOObject)object).cdoInternalInstance());
        removeObject(id, object);
      }

      // Rollback new objects re-attached after the save point
      Map<CDOID, CDOObject> reattachedObjectsMap = itrSavepoint.getReattachedObjects();
      Set<CDOID> detachedIDs = itrSavepoint.getDetachedObjects().keySet();
      for (CDOObject reattachedObject : reattachedObjectsMap.values())
      {
        CDOID id = reattachedObject.cdoID();
        if (!detachedIDs.contains(id))
        {
          toBeDetached.add(id);
          toBeDetached.add(reattachedObject);
          toBeDetached.add(((InternalCDOObject)reattachedObject).cdoInternalInstance());
          removeObject(id, reattachedObject);
        }
      }

      for (Object idOrObject : toBeDetached)
      {
        if (idOrObject instanceof CDOObjectImpl)
        {
          CDOObjectImpl impl = (CDOObjectImpl)idOrObject;
          Internal directResource = impl.eDirectResource();
          EObject container = impl.eContainer();
          if (!toBeDetached.contains(directResource) && !toBeDetached.contains(container))
          {
            // Unset direct resource and/or eContainer
            impl.cdoInternalSetResource(null);
          }
        }
        else if (idOrObject instanceof CDOObjectWrapper)
        {
          CDOObjectWrapper wrapper = (CDOObjectWrapper)idOrObject;
          Internal directResource = wrapper.eDirectResource();
          EObject container = wrapper.eContainer();
          if (!toBeDetached.contains(directResource) && !toBeDetached.contains(container))
          {
            wrapper.setInstanceResource(null);
            wrapper.setInstanceContainer(null, 0);
          }
        }
      }

      Map<CDOID, CDORevisionDelta> revisionDeltas = itrSavepoint.getRevisionDeltas();
      if (!revisionDeltas.isEmpty())
      {
        for (CDORevisionDelta dirtyObject : revisionDeltas.values())
        {
          CDOID id = dirtyObject.getID();
          if (isObjectNew(id))
          {
            idsOfNewObjectsWithDeltas.add(id);
          }
        }
      }

      // Rollback all detached objects
      Map<CDOID, CDOObject> detachedObjectsMap = itrSavepoint.getDetachedObjects();
      if (!detachedObjectsMap.isEmpty())
      {
        for (Entry<CDOID, CDOObject> detachedObjectEntry : detachedObjectsMap.entrySet())
        {
          CDOID id = detachedObjectEntry.getKey();
          if (isObjectNew(id))
          {
            idsOfNewObjectsWithDeltas.add(id);
          }
          else
          {
            InternalCDOObject detachedObject = (InternalCDOObject)detachedObjectEntry.getValue();
            InternalCDORevision cleanRev = cleanRevisions.get(detachedObject);
            cleanObject(detachedObject, cleanRev);
          }
        }
      }

      for (Entry<CDOID, CDOObject> entryDirtyObject : itrSavepoint.getDirtyObjects().entrySet())
      {
        CDOID id = entryDirtyObject.getKey();
        if (!isObjectNew(id))
        {
          InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();

          // Bug 283985 (Re-attachment): Skip objects that were reattached, because
          // they were already reset to TRANSIENT earlier in this method
          if (!reattachedObjectsMap.values().contains(internalDirtyObject))
          {
            CDOStateMachine.INSTANCE.rollback(internalDirtyObject);
          }
        }
      }

      if (savepoint == itrSavepoint)
      {
        break;
      }
    }

    return idsOfNewObjectsWithDeltas;
  }

  private void loadSavepoint(CDOSavepoint savepoint, Set<CDOID> idsOfNewObjectWithDeltas)
  {
    Map<CDOID, CDOObject> dirtyObjects = getDirtyObjects();
    Map<CDOID, CDOObject> newObjMaps = getNewObjects();
    Map<CDOID, CDORevision> newBaseRevision = getBaseNewObjects();
    Map<CDOID, CDOObject> detachedObjects = getDetachedObjects();

    // Reload the objects (NEW) with their base.
    for (CDOID id : idsOfNewObjectWithDeltas)
    {
      if (detachedObjects.containsKey(id))
      {
        continue;
      }

      InternalCDOObject object = (InternalCDOObject)newObjMaps.get(id);
      CDORevision revision = newBaseRevision.get(id);
      if (revision != null)
      {
        object.cdoInternalSetRevision(revision.copy());
        object.cdoInternalSetView(this);
        object.cdoInternalSetID(revision.getID());
        object.cdoInternalSetState(CDOState.NEW);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
        if (super.getObject(object.cdoID(), false) == null)
        {
          registerObject(object);
        }
      }
    }

    // We need to register back new objects that are not removed anymore there.
    for (Entry<CDOID, CDOObject> entryNewObject : newObjMaps.entrySet())
    {
      InternalCDOObject object = (InternalCDOObject)entryNewObject.getValue();

      // Go back to the previous state
      cleanObject(object, object.cdoRevision());
      object.cdoInternalSetState(CDOState.NEW);
    }

    for (Entry<CDOID, CDOObject> entryDirtyObject : dirtyObjects.entrySet())
    {
      if (detachedObjects.containsKey(entryDirtyObject.getKey()))
      {
        continue;
      }

      // Rollback every persisted objects
      InternalCDOObject internalDirtyObject = (InternalCDOObject)entryDirtyObject.getValue();
      cleanObject(internalDirtyObject, getRevision(entryDirtyObject.getKey(), true));
    }

    CDOObjectMerger merger = new CDOObjectMerger();
    for (InternalCDOSavepoint itrSavepoint = firstSavepoint; itrSavepoint != savepoint; itrSavepoint = itrSavepoint
        .getNextSavepoint())
    {
      for (CDORevisionDelta delta : itrSavepoint.getRevisionDeltas().values())
      {
        CDOID id = delta.getID();
        boolean isNew = isObjectNew(id);
        if (isNew && !idsOfNewObjectWithDeltas.contains(id) || detachedObjects.containsKey(id))
        {
          continue;
        }

        Map<CDOID, CDOObject> map = isNew ? newObjMaps : dirtyObjects;
        InternalCDOObject object = (InternalCDOObject)map.get(id);

        // Change state of the objects
        merger.merge(object, delta);

        // Load the object from revision to EObject
        object.cdoInternalPostLoad();
      }
    }

    dirty = savepoint.wasDirty();
  }

  /**
   * @since 2.0
   */
  public synchronized void detachObject(InternalCDOObject object)
  {
    CDOTransactionHandler1[] handlers = getTransactionHandlers1();
    for (int i = 0; i < handlers.length; i++)
    {
      CDOTransactionHandler1 handler = handlers[i];
      handler.detachingObject(this, object);
    }

    // deregister object
    CDOID id = object.cdoID();
    if (object.cdoState() == CDOState.NEW)
    {
      Map<CDOID, CDOObject> map = getLastSavepoint().getNewObjects();

      // Determine if we added object
      if (map.containsKey(id))
      {
        map.remove(id);
      }
      else
      {
        getLastSavepoint().getDetachedObjects().put(id, object);
      }

      // deregister object
      deregisterObject(object);
    }
    else
    {
      getLastSavepoint().getDetachedObjects().put(id, object);

      if (!cleanRevisions.containsKey(object))
      {
        cleanRevisions.put(object, object.cdoRevision());
      }

      // Object may have been reattached previously, in which case it must
      // here be removed from the collection of reattached objects
      lastSavepoint.getReattachedObjects().remove(id);
    }

    if (!dirty)
    {
      dirty = true;
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new StartedEvent(), listeners);
      }
    }
  }

  /**
   * @since 2.0
   */
  public synchronized void handleRollback(InternalCDOSavepoint savepoint)
  {
    if (savepoint == null)
    {
      throw new IllegalArgumentException(Messages.getString("CDOTransactionImpl.3")); //$NON-NLS-1$
    }

    if (savepoint.getTransaction() != this)
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOTransactionImpl.4"), savepoint)); //$NON-NLS-1$
    }

    if (!savepoint.isValid())
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOTransactionImpl.6"), savepoint)); //$NON-NLS-1$
    }

    if (TRACER.isEnabled())
    {
      TRACER.trace("handleRollback()"); //$NON-NLS-1$
    }

    try
    {
      // Remember current revisions
      Map<CDOObject, CDORevision> oldRevisions = new HashMap<CDOObject, CDORevision>();
      for (CDOObject object : getDirtyObjects().values())
      {
        CDORevision oldRevision = object.cdoRevision();
        if (oldRevision != null)
        {
          oldRevisions.put(object, oldRevision);
        }
      }

      // Rollback objects
      Set<CDOID> idsOfNewObjectWithDeltas = rollbackCompletely(savepoint);

      lastSavepoint = savepoint;
      lastSavepoint.setNextSavepoint(null);
      lastSavepoint.clear();

      // Load from first savepoint up to current savepoint
      loadSavepoint(lastSavepoint, idsOfNewObjectWithDeltas);

      if (lastSavepoint == firstSavepoint && options().isAutoReleaseLocksEnabled())
      {
        CDORepositoryInfo repositoryInfo = getSession().getRepositoryInfo();
        if (isDurableView() && repositoryInfo.getState() == CDOCommonRepository.State.ONLINE
            || repositoryInfo.getType() == CDOCommonRepository.Type.MASTER)
        {
          // Unlock all objects
          unlockObjects(null, null);
        }
      }

      // Send notifications
      for (Entry<CDOObject, CDORevision> entry : oldRevisions.entrySet())
      {
        InternalCDOObject object = (InternalCDOObject)entry.getKey();
        if (FSMUtil.isTransient(object))
        {
          continue;
        }

        InternalCDORevision oldRevision = (InternalCDORevision)entry.getValue();
        InternalCDORevision newRevision = object.cdoRevision();
        if (newRevision == null)
        {
          newRevision = getRevision(oldRevision.getID(), true);
          object.cdoInternalSetRevision(newRevision);
          object.cdoInternalSetState(CDOState.CLEAN);
        }

        if (newRevision != null)
        {
          InternalCDORevisionDelta delta = newRevision.compare(oldRevision);
          if (!delta.isEmpty())
          {
            Set<CDOObject> detachedObjects = Collections.emptySet();

            CDONotificationBuilder builder = new CDONotificationBuilder(this);
            NotificationChain notification = builder.buildNotification(object, oldRevision, delta, detachedObjects);
            if (notification != null)
            {
              notification.dispatch();
            }
          }
        }
      }

      Map<CDOID, CDOID> idMappings = Collections.emptyMap();
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.ROLLED_BACK, idMappings), listeners);
      }

      CDOTransactionHandler2[] handlers = getTransactionHandlers2();
      for (int i = 0; i < handlers.length; i++)
      {
        CDOTransactionHandler2 handler = handlers[i];

        try
        {
          handler.rolledBackTransaction(this);
        }
        catch (RuntimeException ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
    catch (RuntimeException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      throw new TransactionException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDOSavepoint handleSetSavepoint()
  {
    addToBase(lastSavepoint.getNewObjects());
    lastSavepoint = createSavepoint(lastSavepoint);
    return lastSavepoint;
  }

  private CDOSavepointImpl createSavepoint(InternalCDOSavepoint lastSavepoint)
  {
    return new CDOSavepointImpl(this, lastSavepoint);
  }

  /**
   * @since 2.0
   */
  public synchronized InternalCDOSavepoint setSavepoint()
  {
    checkActive();
    return (InternalCDOSavepoint)getTransactionStrategy().setSavepoint(this);
  }

  private void addToBase(Map<CDOID, CDOObject> objects)
  {
    for (CDOObject object : objects.values())
    {
      // Load instance to revision
      ((InternalCDOObject)object).cdoInternalPreCommit();
      lastSavepoint.getBaseNewObjects().put(object.cdoID(), object.cdoRevision().copy());
    }
  }

  @Override
  protected String getClassName()
  {
    return "CDOTransaction"; //$NON-NLS-1$
  }

  public synchronized void registerAttached(InternalCDOObject object, boolean isNew)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering new object {0}", object); //$NON-NLS-1$
    }

    if (isNew)
    {
      registerNewPackage(object.eClass().getEPackage());
    }

    CDOTransactionHandler1[] handlers = getTransactionHandlers1();
    for (int i = 0; i < handlers.length; i++)
    {
      CDOTransactionHandler1 handler = handlers[i];
      handler.attachingObject(this, object);
    }

    if (isNew)
    {
      registerNew(lastSavepoint.getNewObjects(), object);
    }
  }

  private void registerNewPackage(EPackage ePackage)
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    if (!packageRegistry.containsKey(ePackage.getNsURI()))
    {
      packageRegistry.putEPackage(ePackage);
    }
  }

  /**
   * Receives notification for new and dirty objects
   */
  public synchronized void registerFeatureDelta(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    CDOID id = object.cdoID();
    boolean needToSaveFeatureDelta = true;

    if (object.cdoState() == CDOState.NEW)
    {
      // Register Delta for new objects only if objectA doesn't belong to
      // this savepoint
      if (getLastSavepoint().getPreviousSavepoint() == null || featureDelta == null)
      {
        needToSaveFeatureDelta = false;
      }
      else
      {
        Map<CDOID, CDOObject> map = getLastSavepoint().getNewObjects();
        needToSaveFeatureDelta = !map.containsKey(id);
      }
    }

    if (needToSaveFeatureDelta)
    {
      CDORevisionDelta revisionDelta = lastSavepoint.getRevisionDeltas().get(id);
      if (revisionDelta == null)
      {
        revisionDelta = CDORevisionUtil.createDelta(object.cdoRevision());
        lastSavepoint.getRevisionDeltas().put(id, revisionDelta);
      }

      ((InternalCDORevisionDelta)revisionDelta).addFeatureDelta(featureDelta);
    }

    CDOTransactionHandler1[] handlers = getTransactionHandlers1();
    for (int i = 0; i < handlers.length; i++)
    {
      CDOTransactionHandler1 handler = handlers[i];
      handler.modifyingObject(this, object, featureDelta);
    }
  }

  public synchronized void registerRevisionDelta(CDORevisionDelta revisionDelta)
  {
    lastSavepoint.getRevisionDeltas().putIfAbsent(revisionDelta.getID(), revisionDelta);
  }

  public synchronized void registerDirty(InternalCDOObject object, CDOFeatureDelta featureDelta)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Registering dirty object {0}", object); //$NON-NLS-1$
    }

    if (featureDelta != null)
    {
      registerFeatureDelta(object, featureDelta);
    }

    registerNew(lastSavepoint.getDirtyObjects(), object);
  }

  /**
   * TODO Simon: Should this method go to CDOSavePointImpl?
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  private void registerNew(Map map, InternalCDOObject object)
  {
    Object old = map.put(object.cdoID(), object);
    if (old != null)
    {
      throw new IllegalStateException(MessageFormat.format(Messages.getString("CDOTransactionImpl.10"), object)); //$NON-NLS-1$
    }

    if (!dirty)
    {
      dirty = true;
      IListener[] listeners = getListeners();
      if (listeners != null)
      {
        fireEvent(new StartedEvent(), listeners);
      }
    }
  }

  public synchronized List<CDOPackageUnit> analyzeNewPackages()
  {
    CDOPackageRegistry packageRegistry = getSession().getPackageRegistry();
    Set<EPackage> usedPackages = new HashSet<EPackage>();
    Set<EPackage> usedNewPackages = new HashSet<EPackage>();
    for (CDOObject object : getNewObjects().values())
    {
      EPackage ePackage = object.eClass().getEPackage();
      if (usedPackages.add(ePackage))
      {
        EPackage topLevelPackage = EMFUtil.getTopLevelPackage(ePackage);
        if (ePackage == topLevelPackage || usedPackages.add(topLevelPackage))
        {
          // if (!CDOModelUtil.isSystemPackage(topLevelPackage))
          {
            CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(topLevelPackage);
            if (packageUnit.getState() == CDOPackageUnit.State.NEW)
            {
              usedNewPackages.add(topLevelPackage);
            }
          }
        }
      }
    }

    if (usedNewPackages.size() > 0)
    {
      Set<CDOPackageUnit> result = new HashSet<CDOPackageUnit>();
      for (EPackage usedNewPackage : analyzeNewPackages(usedNewPackages, packageRegistry))
      {
        CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(usedNewPackage);
        result.add(packageUnit);
      }

      return new ArrayList<CDOPackageUnit>(result);
    }

    return Collections.emptyList();
  }

  private static List<EPackage> analyzeNewPackages(Collection<EPackage> usedTopLevelPackages,
      CDOPackageRegistry packageRegistry)
  {
    // Determine which of the corresdonding EPackages are new
    List<EPackage> newPackages = new ArrayList<EPackage>();

    IPackageClosure closure = new CompletePackageClosure();
    usedTopLevelPackages = closure.calculate(usedTopLevelPackages);

    for (EPackage usedPackage : usedTopLevelPackages)
    {
      // if (!CDOModelUtil.isSystemPackage(usedPackage))
      {
        CDOPackageUnit packageUnit = packageRegistry.getPackageUnit(usedPackage);
        if (packageUnit == null)
        {
          throw new CDOException(MessageFormat.format(Messages.getString("CDOTransactionImpl.11"), usedPackage)); //$NON-NLS-1$
        }

        if (packageUnit.getState() == CDOPackageUnit.State.NEW)
        {
          newPackages.add(usedPackage);
        }
      }
    }

    return newPackages;
  }

  private void cleanUp(CDOCommitContext commitContext)
  {
    if (commitContext == null || !commitContext.isPartialCommit())
    {
      lastSavepoint = firstSavepoint;
      firstSavepoint.clear();
      firstSavepoint.setNextSavepoint(null);

      cleanRevisions.clear();
      dirty = false;
      conflict = 0;
      idGenerator.reset();
    }
    else
    {
      collapseSavepoints(commitContext);

      for (CDOObject object : commitContext.getDetachedObjects().values())
      {
        cleanRevisions.remove(object);
      }

      for (CDOObject object : commitContext.getDirtyObjects().values())
      {
        cleanRevisions.remove(object);
      }
    }

    // Reset partial-commit filter
    committables = null;
  }

  private void collapseSavepoints(CDOCommitContext commitContext)
  {
    InternalCDOSavepoint newSavepoint = createSavepoint(null);
    copyUncommitted(lastSavepoint.getAllNewObjects(), commitContext.getNewObjects(), newSavepoint.getNewObjects());
    copyUncommitted(lastSavepoint.getAllDirtyObjects(), commitContext.getDirtyObjects(), newSavepoint.getDirtyObjects());
    copyUncommitted(lastSavepoint.getAllRevisionDeltas(), commitContext.getRevisionDeltas(),
        newSavepoint.getRevisionDeltas());
    copyUncommitted(lastSavepoint.getAllDetachedObjects(), commitContext.getDetachedObjects(),
        newSavepoint.getDetachedObjects());
    lastSavepoint = newSavepoint;
    firstSavepoint = lastSavepoint;
  }

  private <T> void copyUncommitted(Map<CDOID, T> oldSavepointMap, Map<CDOID, T> commitContextMap,
      Map<CDOID, T> newSavepointMap)
  {
    for (Entry<CDOID, T> entry : oldSavepointMap.entrySet())
    {
      if (!commitContextMap.containsKey(entry.getKey()))
      {
        newSavepointMap.put(entry.getKey(), entry.getValue());
      }
    }
  }

  public synchronized CDOSavepoint[] exportChanges(OutputStream stream) throws IOException
  {
    CDODataOutput out = new CDODataOutputImpl(new ExtendedDataOutputStream(stream))
    {
      @Override
      public CDOIDProvider getIDProvider()
      {
        return CDOTransactionImpl.this;
      }

      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }
    };

    List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
    int totalNewObjects = 0;

    InternalCDOSavepoint savepoint = firstSavepoint;
    while (savepoint != null)
    {
      Collection<CDOObject> newObjects = savepoint.getNewObjects().values();
      totalNewObjects += newObjects.size();

      savepoint = savepoint.getNextSavepoint();
    }

    out.writeInt(totalNewObjects);

    savepoint = firstSavepoint;
    while (savepoint != null)
    {
      Collection<CDOObject> newObjects = savepoint.getNewObjects().values();
      Collection<CDORevisionDelta> revisionDeltas = savepoint.getRevisionDeltas().values();
      if (newObjects.isEmpty() && revisionDeltas.isEmpty())
      {
        savepoint = savepoint.getNextSavepoint();
        continue;
      }

      savepoints.add(savepoint);
      out.writeBoolean(true);

      out.writeInt(newObjects.size());
      for (CDOObject newObject : newObjects)
      {
        out.writeCDORevision(newObject.cdoRevision(), CDORevision.UNCHUNKED);
      }

      out.writeInt(revisionDeltas.size());
      for (CDORevisionDelta revisionDelta : revisionDeltas)
      {
        out.writeCDORevisionDelta(revisionDelta);
      }

      savepoint = savepoint.getNextSavepoint();
    }

    out.writeBoolean(false);
    return savepoints.toArray(new CDOSavepoint[savepoints.size()]);
  }

  public CDOSavepoint[] importChanges(InputStream stream, boolean reconstructSavepoints, CDOObjectMerger objectMerger)
      throws IOException
  {
    List<CDOSavepoint> savepoints = new ArrayList<CDOSavepoint>();
    if (stream.available() > 0)
    {
      CDODataInput in = new CDODataInputImpl(new ExtendedDataInputStream(stream))
      {
        public CDOPackageRegistry getPackageRegistry()
        {
          return getSession().getPackageRegistry();
        }

        @Override
        protected CDOBranchManager getBranchManager()
        {
          return getSession().getBranchManager();
        }

        @Override
        protected CDOCommitInfoManager getCommitInfoManager()
        {
          return getSession().getCommitInfoManager();
        }

        @Override
        protected CDORevisionFactory getRevisionFactory()
        {
          return getSession().getRevisionManager().getFactory();
        }

        @Override
        protected CDOLobStore getLobStore()
        {
          return getSession().getLobStore();
        }

        @Override
        protected CDOListFactory getListFactory()
        {
          return CDOListWithElementProxiesImpl.FACTORY;
        }
      };

      // Increase the internal tempID counter to prevent ID collisions during mapping
      int totalNewObjects = in.readInt();
      for (int i = 0; i < totalNewObjects; i++)
      {
        createIDForNewObject(null);
      }

      Map<CDOID, CDOID> idMappings = new HashMap<CDOID, CDOID>();
      while (in.readBoolean())
      {
        if (reconstructSavepoints)
        {
          InternalCDOSavepoint savepoint = setSavepoint();
          savepoints.add(savepoint);
        }

        // Import revisions and deltas
        List<InternalCDORevision> revisions = new ArrayList<InternalCDORevision>();
        importNewRevisions(in, revisions, idMappings);
        List<InternalCDORevisionDelta> revisionDeltas = importRevisionDeltas(in);

        // Re-map temp IDs
        CDOIDMapper idMapper = new CDOIDMapper(idMappings);
        for (InternalCDORevision revision : revisions)
        {
          revision.adjustReferences(idMapper);
        }

        for (InternalCDORevisionDelta delta : revisionDeltas)
        {
          delta.adjustReferences(idMapper);
        }

        // Create new objects
        List<InternalCDOObject> newObjects = new ArrayList<InternalCDOObject>();
        for (InternalCDORevision revision : revisions)
        {
          InternalCDOObject object = newInstance(revision);
          registerObject(object);
          registerAttached(object, true);

          newObjects.add(object);
        }

        // Post-load new objects (important for legacy objects!)
        for (InternalCDOObject object : newObjects)
        {
          object.cdoInternalPostLoad();
        }

        // Apply deltas
        CDOObjectMerger merger = objectMerger;
        for (InternalCDORevisionDelta delta : revisionDeltas)
        {
          InternalCDOObject object = getObject(delta.getID());
          int oldVersion = object.cdoRevision().getVersion();

          merger.merge(object, delta);
          registerRevisionDelta(delta);
          registerDirty(object, null);

          if (delta.getVersion() < oldVersion)
          {
            setConflict(object);
          }
        }
      }
    }

    return savepoints.toArray(new CDOSavepoint[savepoints.size()]);
  }

  public synchronized CDOSavepoint[] importChanges(InputStream stream, boolean reconstructSavepoints)
      throws IOException
  {

    return importChanges(stream, reconstructSavepoints, new CDOObjectMerger());

  }

  private void importNewRevisions(CDODataInput in, List<InternalCDORevision> revisions, Map<CDOID, CDOID> idMappings)
      throws IOException
  {
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      InternalCDORevision revision = (InternalCDORevision)in.readCDORevision(false);

      CDOID oldID = revision.getID();
      if (oldID.isTemporary())
      {
        CDOID newID = createIDForNewObject(null);
        idMappings.put(oldID, newID);
        revision.setID(newID);
      }

      revisions.add(revision);
    }
  }

  private List<InternalCDORevisionDelta> importRevisionDeltas(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    List<InternalCDORevisionDelta> deltas = new ArrayList<InternalCDORevisionDelta>(size);
    for (int i = 0; i < size; i++)
    {
      InternalCDORevisionDelta delta = (InternalCDORevisionDelta)in.readCDORevisionDelta();
      deltas.add(delta);
    }

    return deltas;
  }

  private InternalCDOObject newInstance(InternalCDORevision revision)
  {
    InternalCDOObject object = newInstance(revision.getEClass());
    object.cdoInternalSetID(revision.getID());
    object.cdoInternalSetRevision(revision);
    object.cdoInternalSetState(CDOState.NEW);
    object.cdoInternalSetView(this);
    return object;
  }

  public synchronized Map<CDOID, CDOObject> getDirtyObjects()
  {
    checkActive();
    return lastSavepoint.getAllDirtyObjects();
  }

  public synchronized Map<CDOID, CDOObject> getNewObjects()
  {
    checkActive();
    return lastSavepoint.getAllNewObjects();
  }

  /**
   * @since 2.0
   */
  public synchronized Map<CDOID, CDORevision> getBaseNewObjects()
  {
    checkActive();
    return lastSavepoint.getAllBaseNewObjects();
  }

  public synchronized Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    checkActive();
    return lastSavepoint.getAllRevisionDeltas();
  }

  /**
   * @since 2.0
   */
  public synchronized Map<CDOID, CDOObject> getDetachedObjects()
  {
    checkActive();
    return lastSavepoint.getAllDetachedObjects();
  }

  @Override
  protected synchronized CDOID getXRefTargetID(CDOObject target)
  {
    CDORevisionKey key = cleanRevisions.get(target);
    if (key != null)
    {
      return key.getID();
    }

    return super.getXRefTargetID(target);
  }

  @Override
  protected synchronized CDOID getID(InternalCDOObject object, boolean onlyPersistedID)
  {
    CDOID id = super.getID(object, onlyPersistedID);

    // If super returned a good result, return immediately
    if (id != null)
    {
      return id;
    }

    // Don't perform the trickery that follows later in this method, if we are being called
    // indirectly through provideCDOID. This occurs when deltas or revisions are
    // being written out to a stream; in which case null must be returned (for transients) so that
    // the caller will detect a dangling reference
    if (providingCDOID.get())
    {
      return null;
    }

    // The super implementation will return null for a transient (unattached) object;
    // but in a tx, an transient object may previously have been attached. So we consult
    // the cleanRevisions if that's the case.
    CDORevisionKey revKey = cleanRevisions.get(object);
    if (revKey != null && getDetachedObjects().containsValue(object))
    {
      id = revKey.getID();
    }

    return id;
  }

  @Override
  public synchronized CDOID provideCDOID(Object idOrObject)
  {
    try
    {
      providingCDOID.set(true);
      return super.provideCDOID(idOrObject);
    }
    finally
    {
      providingCDOID.set(false);
    }
  }

  @Override
  public synchronized CDOQueryImpl createQuery(String language, String queryString, Object context)
  {
    return createQuery(language, queryString, context, false);
  }

  public synchronized CDOQueryImpl createQuery(String language, String queryString, boolean considerDirtyState)
  {
    return createQuery(language, queryString, null, considerDirtyState);
  }

  public synchronized CDOQueryImpl createQuery(String language, String queryString, Object context,
      boolean considerDirtyState)
  {
    CDOQueryImpl query = super.createQuery(language, queryString, context);
    if (considerDirtyState && isDirty())
    {
      query.setChangeSetData(getChangeSetData());
    }

    return query;
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();

    InternalCDOSession session = getSession();
    if (session.getRepositoryInfo().getIDGenerationLocation() == IDGenerationLocation.STORE)
    {
      idGenerator = new TempIDGenerator();
    }
    else
    {
      idGenerator = session.getIDGenerator();
      if (idGenerator == null)
      {
        idGenerator = CDOIDGenerator.UUID;
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected void doDeactivate() throws Exception
  {
    options().disposeConflictResolvers();
    lastSavepoint = null;
    firstSavepoint = null;
    transactionStrategy = null;
    idGenerator = null;
    super.doDeactivate();
  }

  /**
   * Bug 298561: This override removes references to remotely detached objects that are present in any DIRTY or NEW
   * objects.
   *
   * @since 3.0
   */
  /*
   * Synchronized through InvlidationRunner.run()
   */
  @Override
  protected Map<CDOObject, Pair<CDORevision, CDORevisionDelta>> invalidate(long lastUpdateTime,
      List<CDORevisionKey> allChangedObjects, List<CDOIDAndVersion> allDetachedObjects, List<CDORevisionDelta> deltas,
      Map<CDOObject, CDORevisionDelta> revisionDeltas, Set<CDOObject> detachedObjects)
  {
    if (!allDetachedObjects.isEmpty())
    {
      Set<CDOID> referencedOIDs = new HashSet<CDOID>();
      for (CDOIDAndVersion key : allDetachedObjects)
      {
        referencedOIDs.add(key.getID());
      }

      Collection<CDOObject> cachedDirtyObjects = getDirtyObjects().values();
      removeCrossReferences(cachedDirtyObjects, referencedOIDs);

      Collection<CDOObject> cachedNewObjects = getNewObjects().values();
      removeCrossReferences(cachedNewObjects, referencedOIDs);
    }

    // Bug 290032 - Sticky views
    InternalCDOSession session = getSession();
    if (session.isSticky())
    {
      session.clearCommittedSinceLastRefresh();
    }

    return super.invalidate(lastUpdateTime, allChangedObjects, allDetachedObjects, deltas, revisionDeltas,
        detachedObjects);
  }

  private void removeCrossReferences(Collection<CDOObject> referencers, Set<CDOID> referencedOIDs)
  {
    List<Pair<Setting, EObject>> objectsToBeRemoved = new LinkedList<Pair<Setting, EObject>>();
    for (CDOObject referencer : referencers)
    {
      FeatureIterator<EObject> it = getChangeableCrossReferences(referencer);
      while (it.hasNext())
      {
        EObject referencedObject = it.next();
        CDOID referencedOID = CDOUtil.getCDOObject(referencedObject).cdoID();

        if (referencedOIDs.contains(referencedOID))
        {
          EReference reference = (EReference)it.feature();

          // In the case of DIRTY, we must investigate further: Is the referencer dirty
          // because a reference to the referencedObject was added? Only in this case
          // should we remove it. If this is not the case (i.e. it is dirty in a different
          // way), we skip it. (If the reference is not persistent, then this exception
          // doesn't apply: it must be removed for sure.)
          if (referencer.cdoState() == CDOState.DIRTY && EMFUtil.isPersistent(reference))
          {
            InternalCDORevision cleanRevision = cleanRevisions.get(referencer);

            Object value = cleanRevision.get(reference, EStore.NO_INDEX);
            if (value instanceof CDOObject && value == referencedObject || //
                value instanceof CDOID && value.equals(referencedOID) || //
                value instanceof CDOList && ((CDOList)value).contains(referencedOID))
            {
              continue;
            }
          }

          Setting setting = ((InternalEObject)referencer).eSetting(reference);
          objectsToBeRemoved.add(new Pair<Setting, EObject>(setting, referencedObject));
        }
      }
    }

    for (Pair<Setting, EObject> pair : objectsToBeRemoved)
    {
      EcoreUtil.remove(pair.getElement1(), pair.getElement2());
    }
  }

  private FeatureIterator<EObject> getChangeableCrossReferences(EObject object)
  {
    FeatureSubsetSupplier features = (FeatureSubsetSupplier)object.eClass().getEAllStructuralFeatures();
    EStructuralFeature[] crossReferences = features.crossReferences();
    if (crossReferences != null)
    {
      List<EStructuralFeature> changeableReferences = new ArrayList<EStructuralFeature>();
      for (int i = 0; i < crossReferences.length; i++)
      {
        EStructuralFeature reference = crossReferences[i];

        // Filter out derived references
        if (reference.isDerived())
        {
          continue;
        }

        // Filter out unchangeable references
        if (!reference.isChangeable())
        {
          continue;
        }

        changeableReferences.add(reference);
      }

      if (!changeableReferences.isEmpty())
      {
        EStructuralFeature[] collectedStructuralFeatures = changeableReferences
            .toArray(new EStructuralFeature[changeableReferences.size()]);
        return (FeatureIterator<EObject>)new ECrossReferenceEListDerived(object, collectedStructuralFeatures)
            .iterator();
      }
    }

    return (FeatureIterator<EObject>)ECrossReferenceEList.<EObject> emptyContentsEList().iterator();
  }

  public synchronized long getLastCommitTime()
  {
    return lastCommitTime;
  }

  public synchronized String getCommitComment()
  {
    return commitComment;
  }

  public synchronized void setCommitComment(String comment)
  {
    commitComment = comment;
  }

  public synchronized void setCommittables(Set<? extends EObject> committables)
  {
    this.committables = committables;
  }

  public synchronized Set<? extends EObject> getCommittables()
  {
    return committables;
  }

  public synchronized Map<InternalCDOObject, InternalCDORevision> getCleanRevisions()
  {
    return cleanRevisions;
  }

  @Override
  protected InternalCDORevision getViewedRevision(InternalCDOObject object)
  {
    InternalCDORevision rev = super.getViewedRevision(object);

    // Bug 336590: If we have a clean revision for this object, return that instead
    if (rev != null)
    {
      InternalCDORevision cleanRev = cleanRevisions.get(object);
      if (cleanRev != null)
      {
        return cleanRev;
      }
    }

    return rev;
  }

  @Override
  protected InternalCDORevision getRevision(CDOObject object)
  {
    if (object.cdoState() == CDOState.TRANSIENT)
    {
      InternalCDORevision revision = cleanRevisions.get(object);
      if (revision == null)
      {
        throw new IllegalStateException("No revision for transient object " + object);
      }

      return revision;
    }

    return super.getRevision(object);
  }

  @Override
  protected InternalCDOLockState createUpdatedLockStateForNewObject(CDOObject object, LockType lockType, boolean on)
  {
    CheckUtil.checkState(FSMUtil.isNew(object), "Object is not in NEW state");
    CheckUtil.checkArg(lockType, "lockType");

    InternalCDOLockState lockState = (InternalCDOLockState)getLockState(object);
    if (lockState == null)
    {
      CheckUtil.checkArg(on == true, "on != true");
      Object lockTarget = getLockTarget(object);
      lockState = (InternalCDOLockState)CDOLockUtil.createLockState(lockTarget);
    }
    else
    {
      lockState = (InternalCDOLockState)CDOLockUtil.copyLockState(lockState);
    }

    CDOLockOwner lockOwner = CDOLockUtil.createLockOwner(this);

    if (on)
    {
      switch (lockType)
      {
      case READ:
        lockState.addReadLockOwner(lockOwner);
        break;
      case WRITE:
        lockState.setWriteLockOwner(lockOwner);
        break;
      case OPTION:
        lockState.setWriteOptionOwner(lockOwner);
        break;
      default:
        throw new IllegalArgumentException("Unknown lock type " + lockType);
      }
    }
    else
    {
      switch (lockType)
      {
      case READ:
        lockState.removeReadLockOwner(lockOwner);
        break;
      case WRITE:
        lockState.setWriteLockOwner(null);
        break;
      case OPTION:
        lockState.setWriteOptionOwner(null);
        break;
      default:
        throw new IllegalArgumentException("Unknown lock type " + lockType);
      }
    }

    return lockState;
  }

  @Override
  protected List<CDOLockState> createUnlockedLockStatesForAllNewObjects()
  {
    List<CDOLockState> locksOnNewObjects = new LinkedList<CDOLockState>();
    for (CDOObject object : getNewObjects().values())
    {
      Object lockTarget = getLockTarget(object);
      CDOLockState lockState = CDOLockUtil.createLockState(lockTarget);
      locksOnNewObjects.add(lockState);
    }

    return locksOnNewObjects;
  }

  private static Object getLockTarget(CDOObject object)
  {
    CDOView view = object.cdoView();
    if (view == null)
    {
      return null;
    }

    CDOID id = object.cdoID();
    boolean branching = view.getSession().getRepositoryInfo().isSupportingBranches();
    if (branching)
    {
      return CDOIDUtil.createIDAndBranch(id, view.getBranch());
    }

    return id;
  }

  /**
   * @author apeteri 
   */
  private enum CDOObjectToCDOIDAdjuster implements CDOReferenceAdjuster
  {
    INSTANCE;

    public Object adjustReference(Object id, EStructuralFeature feature, int index)
    {
      return id instanceof CDOObject ? ((CDOObject)id).cdoID() : id;
    }
  }

  private final class ResolvingRevisionMap extends HashMap<InternalCDOObject, InternalCDORevision>
  {
    private static final long serialVersionUID = 1L;

    public ResolvingRevisionMap()
    {
    }

    @Override
    public InternalCDORevision get(Object cdoObject)
    {
      InternalCDORevision revision = super.get(cdoObject);
      if (revision != null)
      {
        getSession().resolveAllElementProxies(revision);
      }

      return revision;
    }
  }

  /**
   * Generates {@link CDOIDTemp temporary} ID values.
   *
   * @author Eike Stepper
   */
  private static final class TempIDGenerator implements CDOIDGenerator
  {
    private AtomicInteger lastTemporaryID = new AtomicInteger();

    public TempIDGenerator()
    {
    }

    public CDOID generateCDOID(EObject object)
    {
      return CDOIDUtil.createTempObject(lastTemporaryID.incrementAndGet());
    }

    public void reset()
    {
      lastTemporaryID.set(0);
    }
  }

  /**
   * @author Simon McDuff
   */
  private final class CDOCommitContextImpl implements InternalCDOCommitContext
  {
    private InternalCDOTransaction transaction;

    /**
     * Tracks whether this commit is *actually* partial or not. (Having tx.committables != null does not in itself mean
     * that the commit will be partial, because the committables could cover all dirty/new/detached objects. But this
     * boolean gets set to reflect whether the commit will really commit less than all dirty/new/detached objects.)
     */
    private boolean isPartialCommit;

    private CDOCommitData commitData;

    private Collection<CDOLockState> locksOnNewObjects;

    private Map<CDOID, CDOObject> newObjects;

    private Map<CDOID, CDOObject> detachedObjects;

    private Map<CDOID, CDORevisionDelta> revisionDeltas;

    private Map<CDOID, CDOObject> dirtyObjects;

    private Map<ByteArrayWrapper, CDOLob<?>> lobs = new HashMap<ByteArrayWrapper, CDOLob<?>>();

    public CDOCommitContextImpl(InternalCDOTransaction transaction)
    {
      this.transaction = transaction;
      calculateCommitData();
    }

    private void calculateCommitData()
    {
      List<CDOPackageUnit> newPackageUnits = analyzeNewPackages();
      newObjects = filterCommittables(transaction.getNewObjects());
      List<CDOIDAndVersion> revisions = new ArrayList<CDOIDAndVersion>(newObjects.size());
      for (CDOObject newObject : newObjects.values())
      {
        revisions.add(newObject.cdoRevision());
      }

      revisionDeltas = filterCommittables(transaction.getRevisionDeltas());
      List<CDORevisionKey> deltas = new ArrayList<CDORevisionKey>(revisionDeltas.size());
      for (CDORevisionDelta delta : revisionDeltas.values())
      {
        deltas.add(delta);
      }

      detachedObjects = filterCommittables(transaction.getDetachedObjects());
      List<CDOIDAndVersion> detached = new ArrayList<CDOIDAndVersion>(detachedObjects.size());
      for (CDOID id : detachedObjects.keySet())
      {
        // Add "version-less" key.
        // CDOSessionImpl.reviseRevisions() will call reviseLatest() accordingly.
        detached.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
      }

      dirtyObjects = filterCommittables(transaction.getDirtyObjects());

      CDOLockState[] locksOnNewObjectsArray = getLockStates(newObjects.keySet(), false);
      locksOnNewObjects = Arrays.asList(locksOnNewObjectsArray);

      commitData = new CDOCommitDataImpl(newPackageUnits, revisions, deltas, detached);
    }

    private <T> Map<CDOID, T> filterCommittables(Map<CDOID, T> map)
    {
      if (committables == null)
      {
        // No partial commit filter -- nothing to do
        return map;
      }

      Map<CDOID, T> newMap = new HashMap<CDOID, T>();
      for (Entry<CDOID, T> entry : map.entrySet())
      {
        CDOID id = entry.getKey();
        CDOObject o = getObject(id);
        if (committables.contains(o))
        {
          newMap.put(id, entry.getValue());
        }
        else
        {
          isPartialCommit = true;
        }
      }

      return newMap;
    }

    public String getUserID()
    {
      return transaction.getSession().getUserID();
    }

    public int getViewID()
    {
      return transaction.getViewID();
    }

    public CDOBranch getBranch()
    {
      return transaction.getBranch();
    }

    public InternalCDOTransaction getTransaction()
    {
      return transaction;
    }

    public boolean isPartialCommit()
    {
      return isPartialCommit;
    }

    public boolean isAutoReleaseLocks()
    {
      return transaction.options().isAutoReleaseLocksEnabled();
    }

    public String getCommitComment()
    {
      return transaction.getCommitComment();
    }

    public CDOCommitData getCommitData()
    {
      return commitData;
    }

    public Map<CDOID, CDOObject> getDirtyObjects()
    {
      return dirtyObjects;
    }

    public Map<CDOID, CDOObject> getNewObjects()
    {
      return newObjects;
    }

    public List<CDOPackageUnit> getNewPackageUnits()
    {
      return commitData.getNewPackageUnits();
    }

    public Collection<CDOLockState> getLocksOnNewObjects()
    {
      return locksOnNewObjects;
    }

    public Map<CDOID, CDOObject> getDetachedObjects()
    {
      return detachedObjects;
    }

    public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
    {
      return revisionDeltas;
    }

    public Collection<CDOLob<?>> getLobs()
    {
      return lobs.values();
    }

    public void preCommit()
    {
      if (isDirty())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("commit()"); //$NON-NLS-1$
        }

        CDOTransactionHandler2[] handlers = getTransactionHandlers2();
        if (handlers.length != 0)
        {
          final boolean[] modifiedAgain = { false };
          CDOTransactionHandler1 modifiedAgainHandler = new CDODefaultTransactionHandler1()
          {
            @Override
            public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
            {
              modifiedAgain[0] = true;
            }
          };

          addTransactionHandler(modifiedAgainHandler);

          try
          {
            for (int i = 0; i < handlers.length; i++)
            {
              modifiedAgain[0] = false;
              CDOTransactionHandler2 handler = handlers[i];
              handler.committingTransaction(getTransaction(), this);
              if (modifiedAgain[0])
              {
                calculateCommitData();
              }
            }
          }
          finally
          {
            removeTransactionHandler(modifiedAgainHandler);
          }
        }

        try
        {
          // TODO (CD) It might be wise to always do the checks,
          // instead of only for partial commits
          if (isPartialCommit)
          {
            new CommitIntegrityCheck(this, CommitIntegrityCheck.Style.EXCEPTION_FAST).check();
          }

          preCommit(getNewObjects(), lobs);
          preCommit(getDirtyObjects(), lobs);

          if (!lobs.isEmpty())
          {
            CDOSessionProtocol sessionProtocol = getSession().getSessionProtocol();
            List<byte[]> alreadyKnown = sessionProtocol.queryLobs(ByteArrayWrapper.toByteArray(lobs.keySet()));

            for (byte[] id : alreadyKnown)
            {
              lobs.remove(new ByteArrayWrapper(id));
            }
          }
        }
        catch (RuntimeException ex)
        {
          throw ex;
        }
        catch (Exception ex)
        {
          throw new TransactionException(ex);
        }
      }
    }

    public void postCommit(CommitTransactionResult result)
    {
      try
      {
        InternalCDOSession session = getSession();
        long timeStamp = result.getTimeStamp();

        if (result.getRollbackMessage() != null)
        {
          CDOCommitInfo commitInfo = new FailureCommitInfo(timeStamp, result.getPreviousTimeStamp());
          session.invalidate(commitInfo, transaction);
          return;
        }

        CDOBranch branch = result.getBranch();
        boolean branchChanged = !ObjectUtil.equals(branch, getBranch());
        if (branchChanged)
        {
          basicSetBranchPoint(branch.getHead());
        }

        for (CDOPackageUnit newPackageUnit : getNewPackageUnits())
        {
          ((InternalCDOPackageUnit)newPackageUnit).setState(CDOPackageUnit.State.LOADED);
        }

        postCommit(getNewObjects(), result);
        postCommit(getDirtyObjects(), result);

        for (CDORevisionDelta delta : getRevisionDeltas().values())
        {
          ((InternalCDORevisionDelta)delta).adjustReferences(result.getReferenceAdjuster());
        }

        for (CDOID id : getDetachedObjects().keySet())
        {
          removeObject(id);
        }

        CDOCommitInfo commitInfo = makeCommitInfo(timeStamp, result.getPreviousTimeStamp());
        session.invalidate(commitInfo, transaction);

        // Bug 290032 - Sticky views
        if (session.isSticky())
        {
          CDOBranchPoint commitBranchPoint = CDOBranchUtil.copyBranchPoint(result);
          for (CDOObject object : getNewObjects().values()) // Note: keyset() does not work because ID mappings are
                                                            // not applied there!
          {
            session.setCommittedSinceLastRefresh(object.cdoID(), commitBranchPoint);
          }

          for (CDOID id : getDirtyObjects().keySet())
          {
            session.setCommittedSinceLastRefresh(id, commitBranchPoint);
          }

          for (CDOID id : getDetachedObjects().keySet())
          {
            session.setCommittedSinceLastRefresh(id, commitBranchPoint);
          }
        }

        CDOTransactionHandler2[] handlers = getTransactionHandlers2();
        for (int i = 0; i < handlers.length; i++)
        {
          CDOTransactionHandler2 handler = handlers[i];
          if (handler instanceof CDOTransactionHandler3)
          {
            CDOTransactionHandler3 handler3 = (CDOTransactionHandler3)handler;
            handler3.committedTransaction(transaction, this, commitInfo);
          }
          else
          {
            handler.committedTransaction(transaction, this);
          }
        }

        getChangeSubscriptionManager().committedTransaction(transaction, this);
        getAdapterManager().committedTransaction(transaction, this);

        cleanUp(this);
        Map<CDOID, CDOID> idMappings = result.getIDMappings();
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          if (branchChanged)
          {
            fireViewTargetChangedEvent(listeners);
          }

          fireEvent(new FinishedEvent(CDOTransactionFinishedEvent.Type.COMMITTED, idMappings), listeners);
        }

        CDOLockState[] newLockStates = result.getNewLockStates();
        if (newLockStates != null)
        {
          updateAndNotifyLockStates(Operation.UNLOCK, null, result.getTimeStamp(), newLockStates);
        }
      }
      catch (RuntimeException ex)
      {
        throw ex;
      }
      catch (Exception ex)
      {
        throw new TransactionException(ex);
      }
    }

    private CDOCommitInfo makeCommitInfo(long timeStamp, long previousTimeStamp)
    {
      InternalCDOSession session = getSession();
      CDOBranch branch = getBranch();
      String userID = session.getUserID();
      String comment = getCommitComment();

      InternalCDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
      return commitInfoManager.createCommitInfo(branch, timeStamp, previousTimeStamp, userID, comment, commitData);
    }

    private void preCommit(Map<CDOID, CDOObject> objects, Map<ByteArrayWrapper, CDOLob<?>> lobs)
    {
      if (!objects.isEmpty())
      {
        boolean noLegacy = !isLegacyModeEnabled();
        for (CDOObject object : objects.values())
        {
          if (noLegacy && object instanceof CDOObjectWrapper)
          {
            throw new LegacyModeNotEnabledException();
          }

          collectLobs((InternalCDORevision)object.cdoRevision(), lobs);
          ((InternalCDOObject)object).cdoInternalPreCommit();
        }
      }
    }

    private void collectLobs(InternalCDORevision revision, Map<ByteArrayWrapper, CDOLob<?>> lobs)
    {
      EStructuralFeature[] features = revision.getClassInfo().getAllPersistentFeatures();
      for (int i = 0; i < features.length; i++)
      {
        EStructuralFeature feature = features[i];
        if (CDOModelUtil.isLob(feature.getEType()))
        {
          CDOLob<?> lob = (CDOLob<?>)revision.getValue(feature);
          if (lob != null)
          {
            lobs.put(new ByteArrayWrapper(lob.getID()), lob);
          }
        }
      }
    }

    private void postCommit(Map<CDOID, CDOObject> objects, CommitTransactionResult result)
    {
      if (!objects.isEmpty())
      {
        for (CDOObject object : objects.values())
        {
          CDOStateMachine.INSTANCE.commit((InternalCDOObject)object, result);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class StartedEvent extends Event implements CDOTransactionStartedEvent
  {
    private static final long serialVersionUID = 1L;

    private StartedEvent()
    {
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionStartedEvent[source={0}]", getSource()); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FinishedEvent extends Event implements CDOTransactionFinishedEvent
  {
    private static final long serialVersionUID = 1L;

    private Type type;

    private Map<CDOID, CDOID> idMappings;

    private FinishedEvent(Type type, Map<CDOID, CDOID> idMappings)
    {
      this.type = type;
      this.idMappings = idMappings;
    }

    public Type getType()
    {
      return type;
    }

    public Map<CDOID, CDOID> getIDMappings()
    {
      return idMappings;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionFinishedEvent[source={0}, type={1}, idMappings={2}]", getSource(), //$NON-NLS-1$
          getType(), idMappings == null ? 0 : idMappings.size());
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ConflictEvent extends Event implements CDOTransactionConflictEvent
  {
    private static final long serialVersionUID = 1L;

    private InternalCDOObject conflictingObject;

    private boolean firstConflict;

    public ConflictEvent(InternalCDOObject conflictingObject, boolean firstConflict)
    {
      this.conflictingObject = conflictingObject;
      this.firstConflict = firstConflict;
    }

    public InternalCDOObject getConflictingObject()
    {
      return conflictingObject;
    }

    public boolean isFirstConflict()
    {
      return firstConflict;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("CDOTransactionConflictEvent[source={0}, conflictingObject={1}, firstConflict={2}]", //$NON-NLS-1$
          getSource(), getConflictingObject(), isFirstConflict());
    }
  }

  /**
   * @author Eike Stepper
   * @since 2.0
   */
  protected final class OptionsImpl extends CDOViewImpl.OptionsImpl implements CDOTransaction.Options
  {
    private List<CDOConflictResolver> conflictResolvers = new ArrayList<CDOConflictResolver>();

    private boolean autoReleaseLocksEnabled = true;

    public OptionsImpl()
    {
    }

    @Override
    public CDOTransactionImpl getContainer()
    {
      return (CDOTransactionImpl)super.getContainer();
    }

    public CDOConflictResolver[] getConflictResolvers()
    {
      synchronized (CDOTransactionImpl.this)
      {
        return conflictResolvers.toArray(new CDOConflictResolver[conflictResolvers.size()]);
      }
    }

    public void setConflictResolvers(CDOConflictResolver[] resolvers)
    {
      synchronized (CDOTransactionImpl.this)
      {
        for (CDOConflictResolver resolver : conflictResolvers)
        {
          resolver.setTransaction(null);
        }

        conflictResolvers.clear();

        for (CDOConflictResolver resolver : resolvers)
        {
          validateResolver(resolver);
          conflictResolvers.add(resolver);
        }
      }

      fireEvent(new ConflictResolversEventImpl());
    }

    public void addConflictResolver(CDOConflictResolver resolver)
    {
      IEvent event = null;
      synchronized (CDOTransactionImpl.this)
      {
        validateResolver(resolver);
        conflictResolvers.add(resolver);
        event = new ConflictResolversEventImpl();
      }

      fireEvent(event);
    }

    public void removeConflictResolver(CDOConflictResolver resolver)
    {
      IEvent event = null;
      synchronized (CDOTransactionImpl.this)
      {
        if (conflictResolvers.remove(resolver))
        {
          resolver.setTransaction(null);
          event = new ConflictResolversEventImpl();
        }
      }

      fireEvent(event);
    }

    public void disposeConflictResolvers()
    {
      try
      {
        // Do not call getConflictResolvers() because that method may block!
        CDOConflictResolver[] array = conflictResolvers.toArray(new CDOConflictResolver[conflictResolvers.size()]);
        for (CDOConflictResolver resolver : array)
        {
          try
          {
            resolver.setTransaction(null);
          }
          catch (Exception ignore)
          {
          }
        }
      }
      catch (Exception ignore)
      {
      }
    }

    private void validateResolver(CDOConflictResolver resolver)
    {
      if (resolver.getTransaction() != null)
      {
        throw new IllegalArgumentException(Messages.getString("CDOTransactionImpl.17")); //$NON-NLS-1$
      }

      resolver.setTransaction(CDOTransactionImpl.this);
    }

    public boolean isAutoReleaseLocksEnabled()
    {
      return autoReleaseLocksEnabled;
    }

    public void setAutoReleaseLocksEnabled(boolean on)
    {
      IEvent event = null;
      synchronized (CDOTransactionImpl.this)
      {
        if (autoReleaseLocksEnabled != on)
        {
          autoReleaseLocksEnabled = on;
          event = new AutoReleaseLocksEventImpl();
        }
      }

      fireEvent(event);
    }

    /**
     * @author Eike Stepper
     */
    private final class ConflictResolversEventImpl extends OptionsEvent implements ConflictResolversEvent
    {
      private static final long serialVersionUID = 1L;

      public ConflictResolversEventImpl()
      {
        super(OptionsImpl.this);
      }
    }

    /**
     * @author Eike Stepper
     */
    private final class AutoReleaseLocksEventImpl extends OptionsEvent implements AutoReleaseLocksEvent
    {
      private static final long serialVersionUID = 1L;

      public AutoReleaseLocksEventImpl()
      {
        super(OptionsImpl.this);
      }
    }
  }

  public static class ECrossReferenceEListDerived extends ECrossReferenceEList<EObject>
  {

    public ECrossReferenceEListDerived(EObject eObject)
    {
      super(eObject);
    }

    public ECrossReferenceEListDerived(EObject eObject, EStructuralFeature[] eStructuralFeatures)
    {
      super(eObject, eStructuralFeatures);
    }
  }

}
