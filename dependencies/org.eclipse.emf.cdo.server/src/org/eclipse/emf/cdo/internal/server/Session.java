/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 230832
 *    Simon McDuff - bug 233490
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.internal.common.commit.DelegatingCommitInfo;
import org.eclipse.emf.cdo.server.IPermissionManager;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.collection.IndexedList;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.text.MessageFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 */
public class Session extends Container<IView> implements InternalSession
{
  private InternalSessionManager manager;

  private ISessionProtocol protocol;

  private int sessionID;

  private String userID;

  private boolean passiveUpdateEnabled = true;

  private PassiveUpdateMode passiveUpdateMode = PassiveUpdateMode.INVALIDATIONS;

  private LockNotificationMode lockNotificationMode = LockNotificationMode.IF_REQUIRED_BY_VIEWS;

  private long lastUpdateTime;

  @ExcludeFromDump
  private Object lastUpdateTimeLock = new Object();

  private ConcurrentMap<Integer, InternalView> views = new ConcurrentHashMap<Integer, InternalView>();

  private AtomicInteger lastTempViewID = new AtomicInteger();

  @ExcludeFromDump
  private IListener protocolListener = new LifecycleEventAdapter()
  {
    @Override
    protected void onDeactivated(ILifecycle lifecycle)
    {
      deactivate();
    }
  };

  private boolean subscribed;

  /**
   * @since 2.0
   */
  public Session(InternalSessionManager manager, ISessionProtocol protocol, int sessionID, String userID)
  {
    this.manager = manager;
    this.protocol = protocol;
    this.sessionID = sessionID;
    this.userID = userID;

    EventUtil.addListener(protocol, protocolListener);
    activate();
  }

  /**
   * @since 2.0
   */
  public Options options()
  {
    return this;
  }

  /**
   * @since 2.0
   */
  public CDOCommonSession getContainer()
  {
    return this;
  }

  public InternalSessionManager getManager()
  {
    return manager;
  }

  public ISessionProtocol getProtocol()
  {
    return protocol;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  /**
   * @since 2.0
   */
  public String getUserID()
  {
    return userID;
  }

  /**
   * @since 2.0
   */
  public boolean isSubscribed()
  {
    return subscribed;
  }

  /**
   * @since 2.0
   */
  public void setSubscribed(boolean subscribed)
  {
    checkActive();
    if (this.subscribed != subscribed)
    {
      this.subscribed = subscribed;
      byte opcode = subscribed ? CDOProtocolConstants.REMOTE_SESSION_SUBSCRIBED
          : CDOProtocolConstants.REMOTE_SESSION_UNSUBSCRIBED;
      manager.sendRemoteSessionNotification(this, opcode);
    }
  }

  /**
   * @since 2.0
   */
  public boolean isPassiveUpdateEnabled()
  {
    return passiveUpdateEnabled;
  }

  /**
   * @since 2.0
   */
  public void setPassiveUpdateEnabled(boolean passiveUpdateEnabled)
  {
    checkActive();
    this.passiveUpdateEnabled = passiveUpdateEnabled;
  }

  public PassiveUpdateMode getPassiveUpdateMode()
  {
    return passiveUpdateMode;
  }

  public void setPassiveUpdateMode(PassiveUpdateMode passiveUpdateMode)
  {
    checkActive();
    checkArg(passiveUpdateMode, "passiveUpdateMode");
    this.passiveUpdateMode = passiveUpdateMode;
  }

  public LockNotificationMode getLockNotificationMode()
  {
    return lockNotificationMode;
  }

  public void setLockNotificationMode(LockNotificationMode lockNotificationMode)
  {
    checkActive();
    checkArg(lockNotificationMode, "lockNotificationMode");
    this.lockNotificationMode = lockNotificationMode;
  }

  public long getLastUpdateTime()
  {
    synchronized (lastUpdateTimeLock)
    {
      return lastUpdateTime;
    }
  }

  public InternalView[] getElements()
  {
    checkActive();
    return getViews();
  }

  @Override
  public boolean isEmpty()
  {
    checkActive();
    return views.isEmpty();
  }

  public InternalView[] getViews()
  {
    checkActive();
    return getViewsArray();
  }

  private InternalView[] getViewsArray()
  {
    return views.values().toArray(new InternalView[views.size()]);
  }

  public InternalView getView(int viewID)
  {
    checkActive();
    return views.get(viewID);
  }

  /**
   * @since 2.0
   */
  public InternalView openView(int viewID, CDOBranchPoint branchPoint)
  {
    checkActive();
    if (viewID == TEMP_VIEW_ID)
    {
      viewID = -lastTempViewID.incrementAndGet();
    }

    InternalView view = new View(this, viewID, branchPoint);
    view.activate();
    addView(view);
    return view;
  }

  /**
   * @since 2.0
   */
  public InternalTransaction openTransaction(int viewID, CDOBranchPoint branchPoint)
  {
    checkActive();
    if (viewID == TEMP_VIEW_ID)
    {
      viewID = -lastTempViewID.incrementAndGet();
    }

    InternalTransaction transaction = new Transaction(this, viewID, branchPoint);
    transaction.activate();
    addView(transaction);
    return transaction;
  }

  private void addView(InternalView view)
  {
    checkActive();
    int viewID = view.getViewID();
    views.put(viewID, view);
    fireElementAddedEvent(view);
  }

  /**
   * @since 2.0
   */
  public void viewClosed(InternalView view)
  {
    int viewID = view.getViewID();
    if (views.remove(viewID) == view)
    {
      view.doClose();
      fireElementRemovedEvent(view);
    }
  }

  /**
   * TODO I can't see how recursion is controlled/limited
   *
   * @since 2.0
   */
  public void collectContainedRevisions(InternalCDORevision revision, CDOBranchPoint branchPoint, int referenceChunk,
      Set<CDOID> revisions, List<CDORevision> additionalRevisions)
  {
    InternalCDORevisionManager revisionManager = getManager().getRepository().getRevisionManager();
    EClass eClass = revision.getEClass();
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(eClass);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      // TODO Clarify feature maps
      if (feature instanceof EReference && !feature.isMany() && ((EReference)feature).isContainment())
      {
        Object value = revision.getValue(feature);
        if (value instanceof CDOID)
        {
          CDOID id = (CDOID)value;
          if (!CDOIDUtil.isNull(id) && !revisions.contains(id))
          {
            InternalCDORevision containedRevision = revisionManager.getRevision(id, branchPoint, referenceChunk,
                CDORevision.DEPTH_NONE, true);
            revisions.add(id);
            additionalRevisions.add(containedRevision);

            // Recurse
            collectContainedRevisions(containedRevision, branchPoint, referenceChunk, revisions, additionalRevisions);
          }
        }
      }
    }
  }

  public CDOID provideCDOID(Object idObject)
  {
    return (CDOID)idObject;
  }

  public CDOPermission getPermission(CDORevision revision, CDOBranchPoint securityContext)
  {
    IPermissionManager permissionManager = manager.getPermissionManager();
    if (permissionManager != null)
    {
      return permissionManager.getPermission(revision, securityContext, userID);
    }

    return CDORevision.PERMISSION_PROVIDER.getPermission(revision, securityContext);
  }

  public void sendRepositoryTypeNotification(CDOCommonRepository.Type oldType, CDOCommonRepository.Type newType)
      throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRepositoryTypeNotification(oldType, newType);
    }
  }

  @Deprecated
  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState)
      throws Exception
  {
    sendRepositoryStateNotification(oldState, newState, null);
  }

  public void sendRepositoryStateNotification(CDOCommonRepository.State oldState, CDOCommonRepository.State newState,
      CDOID rootResourceID) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRepositoryStateNotification(oldState, newState, rootResourceID);
    }
  }

  public void sendBranchNotification(InternalCDOBranch branch) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendBranchNotification(branch);
    }
  }

  public void sendCommitNotification(final CDOCommitInfo commitInfo) throws Exception
  {
    if (protocol == null)
    {
      return;
    }

    if (!isPassiveUpdateEnabled())
    {
      return;
    }

    final InternalView[] views = getViews();
    protocol.sendCommitNotification(new DelegatingCommitInfo()
    {
      private final PassiveUpdateMode passiveUpdateMode = getPassiveUpdateMode();

      private final boolean additions = passiveUpdateMode == PassiveUpdateMode.ADDITIONS;

      private final boolean changes = passiveUpdateMode == PassiveUpdateMode.CHANGES;

      @Override
      protected CDOCommitInfo getDelegate()
      {
        return commitInfo;
      }

      @Override
      public List<CDOIDAndVersion> getNewObjects()
      {
        final List<CDOIDAndVersion> newObjects = super.getNewObjects();
        return new IndexedList<CDOIDAndVersion>()
        {
          @Override
          public CDOIDAndVersion get(int index)
          {
            // The following will always be a CDORevision!
            CDOIDAndVersion newObject = newObjects.get(index);
            if (additions)
            {
              // Return full revisions if not in INVALIDATION mode
              return newObject;
            }

            // Prevent sending whole revisions by copying the id and version
            return CDOIDUtil.createIDAndVersion(newObject);
          }

          @Override
          public int size()
          {
            return newObjects.size();
          }
        };
      }

      @Override
      public List<CDORevisionKey> getChangedObjects()
      {
        final List<CDORevisionKey> changedObjects = super.getChangedObjects();
        return new IndexedList<CDORevisionKey>()
        {
          @Override
          public CDORevisionKey get(int index)
          {
            // The following will always be a CDORevisionDelta!
            CDORevisionKey changedObject = changedObjects.get(index);
            if (changes || additions || hasSubscription(changedObject.getID(), views))
            {
              return changedObject;
            }

            // Prevent sending whole revisions by copying the id and version
            return CDORevisionUtil.copyRevisionKey(changedObject);
          }

          @Override
          public int size()
          {
            return changedObjects.size();
          }
        };
      }
    });

    synchronized (lastUpdateTimeLock)
    {
      lastUpdateTime = commitInfo.getTimeStamp();
    }
  }

  public void sendLockNotification(CDOLockChangeInfo lockChangeInfo) throws Exception
  {
    if (protocol != null)
    {
      if (options().getLockNotificationMode() == LockNotificationMode.ALWAYS)
      {
        protocol.sendLockNotification(lockChangeInfo);
        return;
      }

      if (options().getLockNotificationMode() == LockNotificationMode.IF_REQUIRED_BY_VIEWS)
      {
        // If this session has one (or more) views configured for this branch,
        // only then do we send the lockChangeInfo.
        for (InternalView view : getViews())
        {
          if (view.options().isLockNotificationEnabled())
          {
            CDOBranch affectedBranch = lockChangeInfo.getBranch();
            if (view.getBranch().equals(affectedBranch) || affectedBranch == null)
            {
              protocol.sendLockNotification(lockChangeInfo);
              break;
            }
          }
        }
      }
    }
  }

  private boolean hasSubscription(CDOID id, InternalView[] views)
  {
    for (InternalView view : views)
    {
      if (view.hasSubscription(id))
      {
        return true;
      }
    }

    return false;
  }

  public void sendRemoteSessionNotification(InternalSession sender, byte opcode) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRemoteSessionNotification(sender, opcode);
    }
  }

  public void sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message) throws Exception
  {
    if (protocol != null)
    {
      protocol.sendRemoteMessageNotification(sender, message);
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Session[{0}]", sessionID); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  /**
   * @since 2.0
   */
  public boolean isClosed()
  {
    return !isActive();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    EventUtil.removeListener(protocol, protocolListener);
    protocolListener = null;

    LifecycleUtil.deactivate(protocol, OMLogger.Level.DEBUG);
    protocol = null;

    for (IView view : getViewsArray())
    {
      view.close();
    }

    views = null;
    manager.sessionClosed(this);
    manager = null;
    super.doDeactivate();
  }
}
