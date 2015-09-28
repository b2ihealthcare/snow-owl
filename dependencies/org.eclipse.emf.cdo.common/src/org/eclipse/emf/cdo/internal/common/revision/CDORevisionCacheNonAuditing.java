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

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.CheckUtil;

/**
 * @author Eike Stepper
 */
public class CDORevisionCacheNonAuditing extends AbstractCDORevisionCache
{
  private Map<CDOID, Reference<InternalCDORevision>> revisions = new HashMap<CDOID, Reference<InternalCDORevision>>();

  public CDORevisionCacheNonAuditing()
  {
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new CDORevisionCacheNonAuditing();
  }

  public EClass getObjectType(CDOID id)
  {
    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          return revision.getEClass();
        }
      }

      return null;
    }
  }

  public InternalCDORevision getRevision(CDOID id, CDOBranchPoint branchPoint)
  {
    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && revision.isValid(branchPoint))
        {
          return revision;
        }
      }

      return null;
    }
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && revision.getVersion() == branchVersion.getVersion())
        {
          return revision;
        }
      }

      return null;
    }
  }

  public List<CDORevision> getCurrentRevisions()
  {
    List<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    synchronized (revisions)
    {
      for (Reference<InternalCDORevision> ref : revisions.values())
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && !revision.isHistorical())
        {
          currentRevisions.add(revision);
        }
      }
    }

    return currentRevisions;
  }

  public Map<CDOBranch, List<CDORevision>> getAllRevisions()
  {
    Map<CDOBranch, List<CDORevision>> result = new HashMap<CDOBranch, List<CDORevision>>();
    synchronized (revisions)
    {
      List<CDORevision> list = new ArrayList<CDORevision>();
      for (Reference<InternalCDORevision> ref : revisions.values())
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          list.add(revision);
        }
      }

      if (!list.isEmpty())
      {
        result.put(list.get(0).getBranch(), list);
      }
    }

    return result;
  }

  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    List<CDORevision> result = new ArrayList<CDORevision>();
    synchronized (revisions)
    {
      for (Reference<InternalCDORevision> ref : revisions.values())
      {
        InternalCDORevision revision = ref.get();
        if (revision != null && revision.isValid(branchPoint))
        {
          result.add(revision);
        }
      }
    }

    return result;
  }

  public void addRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");
    if (!revision.isHistorical())
    {
      synchronized (revisions)
      {
        revisions.put(revision.getID(), createReference(revision));
      }
    }
  }

  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    synchronized (revisions)
    {
      Reference<InternalCDORevision> ref = revisions.get(id);
      if (ref != null)
      {
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          if (revision.getVersion() == branchVersion.getVersion()) // No branch check needed in non-auditing
          {
            revisions.remove(id);
            return revision;
          }
        }
        else
        {
          revisions.remove(id);
        }
      }
    }

    return null;
  }

  public void clear()
  {
    synchronized (revisions)
    {
      revisions.clear();
    }
  }

  @Override
  public String toString()
  {
    synchronized (revisions)
    {
      return revisions.toString();
    }
  }
}
