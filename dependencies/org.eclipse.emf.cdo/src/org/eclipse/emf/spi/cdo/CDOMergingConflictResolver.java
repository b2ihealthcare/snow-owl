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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.cdo.transaction.CDOMerger.ConflictException;

import org.eclipse.net4j.util.CheckUtil;

import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.0
 * @deprecated This conflict resolver is still under development. It's not safe to use it.
 */
@Deprecated
public class CDOMergingConflictResolver extends AbstractChangeSetsConflictResolver
{
  private CDOMerger merger;

  public CDOMergingConflictResolver()
  {
    this(new DefaultCDOMerger.PerFeature.ManyValued());
  }

  public CDOMergingConflictResolver(CDOMerger merger)
  {
    this.merger = merger;
  }

  public CDOMerger getMerger()
  {
    return merger;
  }

  public void resolveConflicts(Set<CDOObject> conflicts)
  {
    CDOChangeSetData result;

    try
    {
      CDOChangeSet target = getLocalChangeSet();
      CDOChangeSet source = getRemoteChangeSet();

      result = merger.merge(target, source);
    }
    catch (ConflictException ex)
    {
      result = ex.getResult();
    }

    InternalCDOTransaction transaction = (InternalCDOTransaction)getTransaction();
    InternalCDORevisionManager revisionManager = transaction.getSession().getRevisionManager();
    Map<CDOID, CDORevisionDelta> localDeltas = transaction.getLastSavepoint().getRevisionDeltas();

    for (CDORevisionKey key : result.getChangedObjects())
    {
      InternalCDORevisionDelta delta = (InternalCDORevisionDelta)key;
      CDOID id = delta.getID();
      InternalCDOObject object = (InternalCDOObject)transaction.getObject(id, false);
      if (object != null)
      {
        CDOState state = object.cdoState();
        if (state == CDOState.CLEAN || state == CDOState.PROXY)
        {
          InternalCDORevision revision = revisionManager.getRevision(id, transaction, CDORevision.UNCHUNKED,
              CDORevision.DEPTH_NONE, false);
          CheckUtil.checkState(revision, "revision");

          object.cdoInternalSetRevision(revision);
          object.cdoInternalSetState(CDOState.CLEAN);
        }
        else if (state == CDOState.CONFLICT)
        {
          int newVersion = delta.getVersion() + 1;

          InternalCDORevision revision = transaction.getCleanRevisions().get(object).copy();
          revision.setVersion(newVersion);
          delta.apply(revision);

          object.cdoInternalSetRevision(revision);
          object.cdoInternalSetState(CDOState.DIRTY);

          InternalCDORevisionDelta localDelta = (InternalCDORevisionDelta)localDeltas.get(id);
          localDelta.setVersion(newVersion);
        }
        else
        {
          throw new IllegalStateException("Unexpected objects state: " + state);
        }
      }
    }
  }
}
