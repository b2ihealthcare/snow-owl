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
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitDataImpl;
import org.eclipse.emf.cdo.internal.server.bundle.OM;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;

import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public abstract class StoreAccessorBase extends Lifecycle implements IStoreAccessor
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, StoreAccessorBase.class);

  private Store store;

  private Object context;

  private boolean reader;

  private List<CommitContext> commitContexts = new ArrayList<CommitContext>();

  private StoreAccessorBase(Store store, Object context, boolean reader)
  {
    this.store = store;
    this.context = context;
    this.reader = reader;
  }

  protected StoreAccessorBase(Store store, ISession session)
  {
    this(store, session, true);
  }

  protected StoreAccessorBase(Store store, ITransaction transaction)
  {
    this(store, transaction, false);
  }

  void setContext(Object context)
  {
    this.context = context;
  }

  public Store getStore()
  {
    return store;
  }

  public boolean isReader()
  {
    return reader;
  }

  /**
   * @since 3.0
   */
  public InternalSession getSession()
  {
    if (context instanceof ITransaction)
    {
      return (InternalSession)((ITransaction)context).getSession();
    }

    return (InternalSession)context;
  }

  public ITransaction getTransaction()
  {
    if (context instanceof ITransaction)
    {
      return (ITransaction)context;
    }

    return null;
  }

  public void release()
  {
    store.releaseAccessor(this);
    commitContexts.clear();
  }

  /**
   * @since 3.0
   */
  public final void write(InternalCommitContext context, OMMonitor monitor)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing transaction: {0}", getTransaction()); //$NON-NLS-1$
    }

    commitContexts.add(context);
    doWrite(context, monitor);
  }

  protected abstract void doWrite(InternalCommitContext context, OMMonitor monitor);

  /**
   * @since 3.0
   */
  public final void commit(OMMonitor monitor)
  {
    doCommit(monitor);

    long latest = CDORevision.UNSPECIFIED_DATE;
    long latestNonLocal = CDORevision.UNSPECIFIED_DATE;
    for (CommitContext commitContext : commitContexts)
    {
      CDOBranchPoint branchPoint = commitContext.getBranchPoint();
      long timeStamp = branchPoint.getTimeStamp();
      if (timeStamp > latest)
      {
        latest = timeStamp;
      }

      CDOBranch branch = branchPoint.getBranch();
      if (!branch.isLocal())
      {
        if (timeStamp > latestNonLocal)
        {
          latestNonLocal = timeStamp;
        }
      }
    }

    getStore().setLastCommitTime(latest);
    getStore().setLastNonLocalCommitTime(latestNonLocal);
  }

  /**
   * @since 3.0
   */
  protected abstract void doCommit(OMMonitor monitor);

  public final void rollback()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Rolling back transaction: {0}", getTransaction()); //$NON-NLS-1$
    }

    for (CommitContext commitContext : commitContexts)
    {
      doRollback(commitContext);
    }
  }

  protected abstract void doRollback(CommitContext commitContext);

  /**
   * @since 3.0
   */
  public CDOID readResourceID(CDOID folderID, String name, CDOBranchPoint branchPoint)
  {
    QueryResourcesContext.ExactMatch context = Store.createExactMatchContext(folderID, name, branchPoint);
    queryResources(context);
    return context.getResourceID();
  }

  /**
   * @since 3.0
   */
  public CDOCommitData loadCommitData(long timeStamp)
  {
    CommitDataRevisionHandler handler = new CommitDataRevisionHandler(this, timeStamp);
    return handler.getCommitData();
  }

  /**
   * Add ID mappings for all new objects of a transaction to the commit context. The implementor must, for each new
   * object of the commit context, determine a permanent CDOID and make it known to the context by calling
   * {@link InternalCommitContext#addIDMapping(CDOID, CDOID)}.
   * 
   * @since 3.0
   */
  public void addIDMappings(InternalCommitContext commitContext, OMMonitor monitor)
  {
    try
    {
      CDORevision[] newObjects = commitContext.getNewObjects();
      monitor.begin(newObjects.length);
      for (CDORevision revision : newObjects)
      {
        CDOID id = revision.getID();
        if (id instanceof CDOIDTemp)
        {
          CDOIDTemp oldID = (CDOIDTemp)id;
          CDOID newID = getNextCDOID(revision);
          if (CDOIDUtil.isNull(newID) || newID.isTemporary())
          {
            throw new IllegalStateException("newID=" + newID); //$NON-NLS-1$
          }

          commitContext.addIDMapping(oldID, newID);
        }

        monitor.worked();
      }
    }
    finally
    {
      monitor.done();
    }
  }

  protected abstract CDOID getNextCDOID(CDORevision revision);

  protected void doPassivate() throws Exception
  {
  }

  protected void doUnpassivate() throws Exception
  {
  }

  /**
   * @author Eike Stepper
   * @since 3.0
   */
  public static class CommitDataRevisionHandler implements CDORevisionHandler
  {
    private IStoreAccessor storeAccessor;

    private long timeStamp;

    private InternalCDORevisionManager revisionManager;

    private List<CDOPackageUnit> newPackageUnits = new ArrayList<CDOPackageUnit>();

    private List<CDOIDAndVersion> newObjects = new ArrayList<CDOIDAndVersion>();

    private List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();

    private DetachCounter detachCounter = new DetachCounter();

    public CommitDataRevisionHandler(IStoreAccessor storeAccessor, long timeStamp)
    {
      this.storeAccessor = storeAccessor;
      this.timeStamp = timeStamp;

      InternalStore store = (InternalStore)storeAccessor.getStore();
      InternalRepository repository = store.getRepository();
      revisionManager = repository.getRevisionManager();

      InternalCDOPackageRegistry packageRegistry = repository.getPackageRegistry(false);
      InternalCDOPackageUnit[] packageUnits = packageRegistry.getPackageUnits(timeStamp, timeStamp);
      for (InternalCDOPackageUnit packageUnit : packageUnits)
      {
        if (!packageUnit.isSystem())
        {
          newPackageUnits.add(packageUnit);
        }
      }
    }

    public CDOCommitData getCommitData()
    {
      storeAccessor.handleRevisions(null, null, timeStamp, true, new CDORevisionHandler.Filtered.Undetached(this));

      List<CDOIDAndVersion> detachedObjects = detachCounter.getDetachedObjects();
      return new CDOCommitDataImpl(newPackageUnits, newObjects, changedObjects, detachedObjects);
    }

    /**
     * @since 4.0
     */
    public boolean handleRevision(CDORevision rev)
    {
      if (rev.getTimeStamp() != timeStamp)
      {
        throw new IllegalArgumentException("Invalid revision time stamp: "
            + CDOCommonUtil.formatTimeStamp(rev.getTimeStamp()));
      }

      if (rev instanceof DetachedCDORevision)
      {
        // Do nothing. Detached objects are handled by detachCounter.
      }
      else
      {
        InternalCDORevision revision = (InternalCDORevision)rev;
        CDOID id = revision.getID();
        CDOBranch branch = revision.getBranch();
        int version = revision.getVersion();
        if (version > CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion oldVersion = branch.getVersion(version - 1);
          InternalCDORevision oldRevision = revisionManager.getRevisionByVersion(id, oldVersion, CDORevision.UNCHUNKED,
              true);
          InternalCDORevisionDelta delta = revision.compare(oldRevision);
          changedObjects.add(delta);

          detachCounter.update(oldRevision, delta);
        }
        else
        {
          InternalCDORevision oldRevision = getRevisionFromBase(id, branch);
          if (oldRevision != null)
          {
            InternalCDORevisionDelta delta = revision.compare(oldRevision);
            changedObjects.add(delta);
          }
          else
          {
            InternalCDORevision newRevision = revision.copy();
            newRevision.setRevised(CDOBranchPoint.UNSPECIFIED_DATE);
            newObjects.add(newRevision);
          }
        }
      }

      return true;
    }

    private InternalCDORevision getRevisionFromBase(CDOID id, CDOBranch branch)
    {
      if (branch.isMainBranch())
      {
        return null;
      }

      CDOBranchPoint base = branch.getBase();
      InternalCDORevision revision = revisionManager.getRevision(id, base, CDORevision.UNCHUNKED,
          CDORevision.DEPTH_NONE, true);
      if (revision == null)
      {
        revision = getRevisionFromBase(id, base.getBranch());
      }

      return revision;
    }

    /**
     * @author Eike Stepper
     */
    private static final class DetachCounter extends CDOFeatureDeltaVisitorImpl
    {
      private Map<CDOID, AtomicInteger> counters = new HashMap<CDOID, AtomicInteger>();

      private InternalCDORevision oldRevision;

      public DetachCounter()
      {
      }

      public void update(InternalCDORevision oldRevision, InternalCDORevisionDelta delta)
      {
        try
        {
          this.oldRevision = oldRevision;
          delta.accept(this);
        }
        finally
        {
          this.oldRevision = null;
        }
      }

      public List<CDOIDAndVersion> getDetachedObjects()
      {
        List<CDOIDAndVersion> result = new ArrayList<CDOIDAndVersion>();
        for (Entry<CDOID, AtomicInteger> entry : counters.entrySet())
        {
          int value = entry.getValue().get();
          if (value == -1)
          {
            CDOID id = entry.getKey();
            result.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
          }
        }

        return result;
      }

      @Override
      public void visit(CDOAddFeatureDelta delta)
      {
        if (isContainment(delta.getFeature()))
        {
          handleContainment(delta.getValue(), 1);
        }
      }

      @Override
      public void visit(CDORemoveFeatureDelta delta)
      {
        if (isContainment(delta.getFeature()))
        {
          handleContainment(delta.getValue(), -1);
        }
      }

      @Override
      public void visit(CDOSetFeatureDelta delta)
      {
        if (isContainment(delta.getFeature()))
        {
          handleContainment(delta.getValue(), 1);
        }
      }

      @Override
      public void visit(CDOUnsetFeatureDelta delta)
      {
        EStructuralFeature feature = delta.getFeature();
        if (isContainment(feature))
        {
          Object value = oldRevision.getValue(feature);
          handleContainment(value, -1);
        }
      }

      @Override
      public void visit(CDOClearFeatureDelta delta)
      {
        EStructuralFeature feature = delta.getFeature();
        if (isContainment(feature))
        {
          CDOList list = oldRevision.getList(feature);
          for (Object value : list)
          {
            handleContainment(value, -1);
          }
        }
      }

      private void handleContainment(Object value, int delta)
      {
        CDOID id = (CDOID)value;
        AtomicInteger counter = counters.get(id);
        if (counter == null)
        {
          counter = new AtomicInteger();
          counters.put(id, counter);
        }

        counter.addAndGet(delta);
      }

      private static boolean isContainment(EStructuralFeature feature)
      {
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          return reference.isContainment();
        }

        return false;
      }
    }
  }
}
