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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDORevisionCacheAuditing extends AbstractCDORevisionCache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_REVISION, CDORevisionCacheAuditing.class);

  protected Map<Object, RevisionList> revisionLists = new HashMap<Object, RevisionList>();

  public CDORevisionCacheAuditing()
  {
  }

  public InternalCDORevisionCache instantiate(CDORevision revision)
  {
    return new CDORevisionCacheAuditing();
  }

  public EClass getObjectType(CDOID id)
  {
    synchronized (revisionLists)
    {
      RevisionList revisionList = revisionLists.get(id);
      if (revisionList != null && !revisionList.isEmpty())
      {
        Reference<InternalCDORevision> ref = revisionList.getFirst();
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
    RevisionList revisionList = getRevisionList(id, branchPoint.getBranch());
    if (revisionList != null)
    {
      return revisionList.getRevision(branchPoint.getTimeStamp());
    }

    return null;
  }

  public InternalCDORevision getRevisionByVersion(CDOID id, CDOBranchVersion branchVersion)
  {
    RevisionList revisionList = getRevisionList(id, branchVersion.getBranch());
    if (revisionList != null)
    {
      return revisionList.getRevisionByVersion(branchVersion.getVersion());
    }

    return null;
  }

  public List<CDORevision> getCurrentRevisions()
  {
    List<CDORevision> currentRevisions = new ArrayList<CDORevision>();
    synchronized (revisionLists)
    {
      for (RevisionList revisionList : revisionLists.values())
      {
        InternalCDORevision revision = revisionList.getRevision(CDORevision.UNSPECIFIED_DATE);
        if (revision != null)
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
    synchronized (revisionLists)
    {
      for (RevisionList list : revisionLists.values())
      {
        list.getAllRevisions(result);
      }
    }

    return result;
  }

  public List<CDORevision> getRevisions(CDOBranchPoint branchPoint)
  {
    List<CDORevision> result = new ArrayList<CDORevision>();
    CDOBranch branch = branchPoint.getBranch();
    synchronized (revisionLists)
    {
      for (Map.Entry<Object, RevisionList> entry : revisionLists.entrySet())
      {
        if (isKeyInBranch(entry.getKey(), branch))
        // if (ObjectUtil.equals(entry.getKey().getBranch(), branch))
        {
          RevisionList list = entry.getValue();
          InternalCDORevision revision = list.getRevision(branchPoint.getTimeStamp());
          if (revision != null)
          {
            result.add(revision);
          }
        }
      }
    }

    return result;
  }

  public void addRevision(CDORevision revision)
  {
    CheckUtil.checkArg(revision, "revision");

    CDOID id = revision.getID();
    Object key = createKey(id, revision.getBranch());

    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(key);
      if (list == null)
      {
        list = new RevisionList();
        revisionLists.put(key, list);
      }

      list.addRevision((InternalCDORevision)revision, createReference(revision));
      typeRefIncrease(id, revision.getEClass());
    }
  }

  public InternalCDORevision removeRevision(CDOID id, CDOBranchVersion branchVersion)
  {
    Object key = createKey(id, branchVersion.getBranch());
    synchronized (revisionLists)
    {
      RevisionList list = revisionLists.get(key);
      if (list != null)
      {
        list.removeRevision(branchVersion.getVersion());
        if (list.isEmpty())
        {
          revisionLists.remove(key);
          typeRefDecrease(id);

          if (TRACER.isEnabled())
          {
            TRACER.format("Removed cache list of {0}", key); //$NON-NLS-1$
          }
        }
      }
    }

    return null;
  }

  public void clear()
  {
    synchronized (revisionLists)
    {
      revisionLists.clear();
      typeRefDispose();
    }
  }

  @Override
  public String toString()
  {
    synchronized (revisionLists)
    {
      return revisionLists.toString();
    }
  }

  protected void typeRefIncrease(CDOID id, EClass type)
  {
    // Do nothing
  }

  protected void typeRefDecrease(CDOID id)
  {
    // Do nothing
  }

  protected void typeRefDispose()
  {
    // Do nothing
  }

  protected Object createKey(CDOID id, CDOBranch branch)
  {
    return id;
  }

  protected boolean isKeyInBranch(Object key, CDOBranch branch)
  {
    return true;
  }

  protected RevisionList getRevisionList(CDOID id, CDOBranch branch)
  {
    Object key = createKey(id, branch);
    synchronized (revisionLists)
    {
      return revisionLists.get(key);
    }
  }

  /**
   * @author Eike Stepper
   */
  protected static final class RevisionList extends LinkedList<Reference<InternalCDORevision>>
  {
    private static final long serialVersionUID = 1L;

    public RevisionList()
    {
    }

    public synchronized InternalCDORevision getRevision(long timeStamp)
    {
      if (timeStamp == CDORevision.UNSPECIFIED_DATE)
      {
        Reference<InternalCDORevision> ref = isEmpty() ? null : getFirst();
        if (ref != null)
        {
          InternalCDORevision revision = ref.get();
          if (revision != null)
          {
            if (!revision.isHistorical())
            {
              return revision;
            }
          }
          else
          {
            removeFirst();
          }
        }

        return null;
      }

      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          long created = revision.getTimeStamp();
          if (created <= timeStamp)
          {
            long revised = revision.getRevised();
            if (timeStamp <= revised || revised == CDORevision.UNSPECIFIED_DATE)
            {
              return revision;
            }

            break;
          }
        }
        else
        {
          it.remove();
        }
      }

      return null;
    }

    public synchronized InternalCDORevision getRevisionByVersion(int version)
    {
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          int v = revision.getVersion();
          if (v == version)
          {
            return revision;
          }
          else if (v < version)
          {
            break;
          }
        }
        else
        {
          it.remove();
        }
      }

      return null;
    }

    public synchronized boolean addRevision(InternalCDORevision revision, Reference<InternalCDORevision> reference)
    {
      int version = revision.getVersion();
      for (ListIterator<Reference<InternalCDORevision>> it = listIterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision foundRevision = ref.get();
        if (foundRevision != null)
        {
          CDORevisionKey key = (CDORevisionKey)ref;
          int v = key.getVersion();
          if (v == version)
          {
            return false;
          }

          if (v < version)
          {
            it.previous();
            it.add(reference);
            return true;
          }
        }
        else
        {
          it.remove();
        }
      }

      addLast(reference);
      return true;
    }

    public synchronized void removeRevision(int version)
    {
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        CDORevisionKey key = (CDORevisionKey)ref;
        int v = key.getVersion();
        if (v == version)
        {
          it.remove();
          if (TRACER.isEnabled())
          {
            TRACER.format("Removed version {0} from cache list of {1}", version, key.getID()); //$NON-NLS-1$
          }

          break;
        }
        else if (v < version)
        {
          break;
        }
      }
    }

    @Override
    public String toString()
    {
      StringBuffer buffer = new StringBuffer();
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (buffer.length() == 0)
        {
          buffer.append("{");
        }
        else
        {
          buffer.append(", ");
        }

        buffer.append(revision);
      }

      buffer.append("}");
      return buffer.toString();
    }

    public void getAllRevisions(Map<CDOBranch, List<CDORevision>> result)
    {
      for (Iterator<Reference<InternalCDORevision>> it = iterator(); it.hasNext();)
      {
        Reference<InternalCDORevision> ref = it.next();
        InternalCDORevision revision = ref.get();
        if (revision != null)
        {
          CDOBranch branch = revision.getBranch();
          List<CDORevision> resultList = result.get(branch);
          if (resultList == null)
          {
            resultList = new ArrayList<CDORevision>(1);
            result.put(branch, resultList);
          }

          resultList.add(revision);
        }
      }
    }
  }
}
