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
 */
package org.eclipse.emf.cdo.internal.common.revision;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCache;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.common.revision.PointerCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.ReflectUtil.ExcludeFromDump;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends Lifecycle implements InternalCDORevisionManager
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionManagerImpl.class);

  private boolean supportingAudits;

  private boolean supportingBranches;

  private RevisionLoader revisionLoader;

  private RevisionLocker revisionLocker;

  private CDORevisionFactory factory;

  private InternalCDORevisionCache cache;

  @ExcludeFromDump
  private transient Object loadAndAddLock = new Object()
  {
    @Override
    public String toString()
    {
      return "LoadAndAddLock"; //$NON-NLS-1$
    }
  };

  @ExcludeFromDump
  private transient Object reviseLock = new Object()
  {
    @Override
    public String toString()
    {
      return "ReviseLock"; //$NON-NLS-1$
    }
  };

  public CDORevisionManagerImpl()
  {
  }

  public boolean isSupportingAudits()
  {
    return supportingAudits;
  }

  public void setSupportingAudits(boolean on)
  {
    checkInactive();
    supportingAudits = on;
  }

  public boolean isSupportingBranches()
  {
    return supportingBranches;
  }

  public void setSupportingBranches(boolean on)
  {
    checkInactive();
    supportingBranches = on;
  }

  public RevisionLoader getRevisionLoader()
  {
    return revisionLoader;
  }

  public void setRevisionLoader(RevisionLoader revisionLoader)
  {
    checkInactive();
    this.revisionLoader = revisionLoader;
  }

  public RevisionLocker getRevisionLocker()
  {
    return revisionLocker;
  }

  public void setRevisionLocker(RevisionLocker revisionLocker)
  {
    checkInactive();
    this.revisionLocker = revisionLocker;
  }

  public CDORevisionFactory getFactory()
  {
    return factory;
  }

  public void setFactory(CDORevisionFactory factory)
  {
    checkInactive();
    this.factory = factory;
  }

  public InternalCDORevisionCache getCache()
  {
    return cache;
  }

  public void setCache(CDORevisionCache cache)
  {
    checkInactive();
    this.cache = (InternalCDORevisionCache)cache;
  }

  public EClass getObjectType(CDOID id, CDOBranchManager branchManagerForLoadOnDemand)
  {
    EClass type = cache.getObjectType(id);
    if (type == null && branchManagerForLoadOnDemand != null)
    {
      CDOBranch mainBranch = branchManagerForLoadOnDemand.getMainBranch();
      CDORevision revision = getRevisionByVersion(id, mainBranch.getVersion(CDOBranchVersion.FIRST_VERSION), 0, true);
      if (revision != null)
      {
        type = revision.getEClass();
      }
    }

    return type;
  }

  public EClass getObjectType(CDOID id)
  {
    return getObjectType(id, null);
  }

  public boolean containsRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    if (supportingBranches)
    {
      return getRevision(id, branchPoint, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, false, null) != null;
    }

    return getCachedRevision(id, branchPoint) != null;
  }

  public boolean containsRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return cache.getRevisionByVersion(id, branchVersion) != null;
  }

  public void reviseLatest(CDOID id, CDOBranch branch)
  {
    acquireAtomicRequestLock(reviseLock);

    try
    {
      InternalCDORevision revision = (InternalCDORevision)cache.getRevision(id, branch.getHead());
      if (revision != null)
      {
        cache.removeRevision(id, branch.getVersion(revision.getVersion()));
      }
    }
    finally
    {
      releaseAtomicRequestLock(reviseLock);
    }
  }

  public void reviseVersion(CDOID id, CDOBranchVersion branchVersion, long timeStamp)
  {
    acquireAtomicRequestLock(reviseLock);

    try
    {
      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision != null)
      {
        if (timeStamp == CDORevision.UNSPECIFIED_DATE)
        {
          cache.removeRevision(id, branchVersion);
        }
        else
        {
          revision.setRevised(timeStamp - 1);
        }
      }
      CDOBranch branch = branchVersion.getBranch();
      List<CDORevision> revisions = getCache().getAllRevisions().get(branch);
      if (revisions != null)
      {
        for (CDORevision cdoRevision : revisions)
        {
          if (cdoRevision.getID().equals(id) && cdoRevision.getVersion() < branchVersion.getVersion()
              && !cdoRevision.isHistorical())
          {
            cache.removeRevision(cdoRevision.getID(), cdoRevision);
          }
        }
      }
    }
    finally
    {
      releaseAtomicRequestLock(reviseLock);
    }
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion, int referenceChunk,
      boolean loadOnDemand)
  {
    checkArg(branchVersion.getVersion() >= CDOBranchVersion.FIRST_VERSION,
        "Invalid version: " + branchVersion.getVersion());
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      InternalCDORevision revision = getCachedRevisionByVersion(id, branchVersion);
      if (revision == null)
      {
        if (loadOnDemand)
        {
          if (TRACER.isEnabled())
          {
            TRACER.format("Loading revision {0} from {1}", id, branchVersion); //$NON-NLS-1$
          }

          revision = revisionLoader.loadRevisionByVersion(id, branchVersion, referenceChunk);
          addRevision(revision);
        }
      }

      return revision;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand)
  {
    return getRevision(id, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, null);
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint, int referenceChunk, int prefetchDepth,
      boolean loadOnDemand, SyntheticCDORevision[] synthetics)
  {
    List<CDOID> ids = Collections.singletonList(id);
    CDORevision result = getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, synthetics).get(0);
    return (InternalCDORevision)result;
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth, boolean loadOnDemand)
  {
    return getRevisions(ids, branchPoint, referenceChunk, prefetchDepth, loadOnDemand, null);
  }

  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, int referenceChunk,
      int prefetchDepth, boolean loadOnDemand, SyntheticCDORevision[] synthetics)
  {
    RevisionInfo[] infos = new RevisionInfo[ids.size()];
    List<RevisionInfo> infosToLoad = createRevisionInfos(ids, branchPoint, prefetchDepth, loadOnDemand, infos);
    if (infosToLoad != null)
    {
      loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth);
    }

    return getResultsAndSynthetics(infos, synthetics);
  }

  private List<RevisionInfo> createRevisionInfos(List<CDOID> ids, CDOBranchPoint branchPoint, int prefetchDepth,
      boolean loadOnDemand, RevisionInfo[] infos)
  {
    List<RevisionInfo> infosToLoad = null;
    Iterator<CDOID> idIterator = ids.iterator();
    for (int i = 0; i < infos.length; i++)
    {
      CDOID id = idIterator.next();
      RevisionInfo info = createRevisionInfo(id, branchPoint);
      infos[i] = info;

      if (loadOnDemand && (prefetchDepth != CDORevision.DEPTH_NONE || info.isLoadNeeded()))
      {
        if (infosToLoad == null)
        {
          infosToLoad = new ArrayList<RevisionInfo>(1);
        }

        infosToLoad.add(info);
      }
    }

    return infosToLoad;
  }

  private RevisionInfo createRevisionInfo(CDOID id, CDOBranchPoint branchPoint)
  {
    InternalCDORevision revision = getCachedRevision(id, branchPoint);
    if (revision != null)
    {
      return createRevisionInfoAvailable(revision, branchPoint);
    }

    if (supportingBranches)
    {
      revision = getCachedRevisionRecursively(id, branchPoint);
      if (revision != null)
      {
        return createRevisionInfoAvailable(revision, branchPoint);
      }
    }

    return createRevisionInfoMissing(id, branchPoint);
  }

  private RevisionInfo.Available createRevisionInfoAvailable(InternalCDORevision revision,
      CDOBranchPoint requestedBranchPoint)
  {
    if (revision instanceof PointerCDORevision)
    {
      PointerCDORevision pointer = (PointerCDORevision)revision;
      CDOBranchVersion target = pointer.getTarget();
      InternalCDORevision targetRevision = target == null ? null : getCachedRevisionByVersion(pointer.getID(), target);
      if (targetRevision != null)
      {
        target = targetRevision;
      }

      return new RevisionInfo.Available.Pointer(pointer.getID(), requestedBranchPoint, pointer, target);
    }

    if (revision instanceof DetachedCDORevision)
    {
      DetachedCDORevision detached = (DetachedCDORevision)revision;
      return new RevisionInfo.Available.Detached(detached.getID(), requestedBranchPoint, detached);
    }

    return new RevisionInfo.Available.Normal(revision.getID(), requestedBranchPoint, revision);
  }

  private RevisionInfo.Missing createRevisionInfoMissing(CDOID id, CDOBranchPoint requestedBranchPoint)
  {
    return new RevisionInfo.Missing(id, requestedBranchPoint);
  }

  protected List<InternalCDORevision> loadRevisions(List<RevisionInfo> infosToLoad, CDOBranchPoint branchPoint,
      int referenceChunk, int prefetchDepth)
  {
    acquireAtomicRequestLock(loadAndAddLock);

    try
    {
      List<InternalCDORevision> additionalRevisions = //
      revisionLoader.loadRevisions(infosToLoad, branchPoint, referenceChunk, prefetchDepth);

      if (additionalRevisions != null)
      {
        for (InternalCDORevision revision : additionalRevisions)
        {
          addRevision(revision);
        }
      }

      return additionalRevisions;
    }
    finally
    {
      releaseAtomicRequestLock(loadAndAddLock);
    }
  }

  private List<CDORevision> getResultsAndSynthetics(RevisionInfo[] infos, SyntheticCDORevision[] synthetics)
  {
    List<CDORevision> results = new ArrayList<CDORevision>(infos.length);
    for (int i = 0; i < infos.length; i++)
    {
      RevisionInfo info = infos[i];
      info.processResult(this, results, synthetics, i);
    }

    return results;
  }

  public void addRevision(CDORevision revision)
  {
    if (revision != null)
    {
      acquireAtomicRequestLock(loadAndAddLock);

      try
      {
        if (revision instanceof PointerCDORevision)
        {
          PointerCDORevision pointer = (PointerCDORevision)revision;
          CDOBranchVersion target = pointer.getTarget();
          if (target instanceof InternalCDORevision)
          {
            revision = new PointerCDORevision(pointer.getEClass(), pointer.getID(), pointer.getBranch(),
                pointer.getRevised(), CDOBranchUtil.copyBranchVersion(target));
          }
        }

        int oldVersion = revision.getVersion() - 1;
        if (oldVersion >= CDORevision.UNSPECIFIED_VERSION)
        {
          CDOBranchVersion old = revision.getBranch().getVersion(oldVersion);
          InternalCDORevision oldRevision = getCachedRevisionByVersion(revision.getID(), old);
          if (!revision.isHistorical())
          {
            if (oldRevision != null)
            {
              oldRevision.setRevised(revision.getTimeStamp() - 1);
            }
            else
            {
              // Remove last revision from cache, which is not revised
              InternalCDORevision cachedLatestRevision = getCachedRevision(revision.getID(), revision);
              if (cachedLatestRevision != null && !cachedLatestRevision.isHistorical())
              {
                // Found revision is stale.
                // We cannot revise it now because of lack information, thus remove it from the cache
                cache.removeRevision(cachedLatestRevision.getID(), cachedLatestRevision);
              }
            }
          }
        }

        cache.addRevision(revision);
      }
      finally
      {
        releaseAtomicRequestLock(loadAndAddLock);
      }
    }
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (factory == null)
    {
      factory = CDORevisionFactory.DEFAULT;
    }

    if (cache == null)
    {
      cache = (InternalCDORevisionCache)CDORevisionUtil.createRevisionCache(supportingAudits, supportingBranches);
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    LifecycleUtil.activate(cache);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    LifecycleUtil.deactivate(cache);
    super.doDeactivate();
  }

  private void acquireAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.acquireAtomicRequestLock(key);
    }
  }

  private void releaseAtomicRequestLock(Object key)
  {
    if (revisionLocker != null)
    {
      revisionLocker.releaseAtomicRequestLock(key);
    }
  }

  private InternalCDORevision getCachedRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    return (InternalCDORevision)cache.getRevisionByVersion(id, branchVersion);
  }

  private InternalCDORevision getCachedRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    return (InternalCDORevision)cache.getRevision(id, branchPoint);
  }

  private InternalCDORevision getCachedRevisionRecursively(CDOID id, CDOBranchPoint branchPoint)
  {
    CDOBranch branch = branchPoint.getBranch();
    if (!branch.isMainBranch())
    {
      CDOBranchPoint base = branch.getBase();
      InternalCDORevision revision = getCachedRevision(id, base);
      if (revision != null)
      {
        return revision;
      }

      // Recurse
      return getCachedRevisionRecursively(id, base);
    }

    // Reached main branch
    return null;
  }
}
