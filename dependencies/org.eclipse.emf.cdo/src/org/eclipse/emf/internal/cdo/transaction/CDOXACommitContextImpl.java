/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.id.CDOIDTempObjectExternalImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.ImplementationError;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction;
import org.eclipse.emf.spi.cdo.InternalCDOXATransaction.InternalCDOXACommitContext;

import org.eclipse.core.runtime.IProgressMonitor;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOXACommitContextImpl implements InternalCDOXACommitContext
{
  private InternalCDOXATransaction transactionManager;

  private IProgressMonitor progressMonitor;

  private CDOXAState state;

  private CommitTransactionResult result;

  private InternalCDOCommitContext delegateCommitContext;

  private Map<CDOIDTempObjectExternalImpl, InternalCDOTransaction> requestedIDs = new HashMap<CDOIDTempObjectExternalImpl, InternalCDOTransaction>();

  private Map<InternalCDOObject, CDOIDTempObjectExternalImpl> objectToID = new HashMap<InternalCDOObject, CDOIDTempObjectExternalImpl>();

  public CDOXACommitContextImpl(InternalCDOXATransaction manager, InternalCDOCommitContext commitContext)
  {
    transactionManager = manager;
    delegateCommitContext = commitContext;
  }

  public InternalCDOXATransaction getTransactionManager()
  {
    return transactionManager;
  }

  public void setProgressMonitor(IProgressMonitor progressMonitor)
  {
    this.progressMonitor = progressMonitor;
  }

  public CDOXAState getState()
  {
    return state;
  }

  public void setState(CDOXAState state)
  {
    this.state = state;
  }

  public CommitTransactionResult getResult()
  {
    return result;
  }

  public void setResult(CommitTransactionResult result)
  {
    this.result = result;
  }

  public int getViewID()
  {
    return delegateCommitContext.getViewID();
  }

  public String getUserID()
  {
    return delegateCommitContext.getUserID();
  }

  public CDOBranch getBranch()
  {
    return delegateCommitContext.getBranch();
  }

  public InternalCDOTransaction getTransaction()
  {
    return delegateCommitContext.getTransaction();
  }

  public boolean isAutoReleaseLocks()
  {
    return delegateCommitContext.isAutoReleaseLocks();
  }

  public boolean isPartialCommit()
  {
    return delegateCommitContext.isPartialCommit();
  }

  public CDOCommitData getCommitData()
  {
    return delegateCommitContext.getCommitData();
  }

  public String getCommitComment()
  {
    return delegateCommitContext.getCommitComment();
  }

  public Map<CDOIDTempObjectExternalImpl, InternalCDOTransaction> getRequestedIDs()
  {
    return requestedIDs;
  }

  public Map<CDOID, CDOObject> getDirtyObjects()
  {
    return delegateCommitContext.getDirtyObjects();
  }

  public Map<CDOID, CDOObject> getNewObjects()
  {
    return delegateCommitContext.getNewObjects();
  }

  public List<CDOPackageUnit> getNewPackageUnits()
  {
    return delegateCommitContext.getNewPackageUnits();
  }

  public Collection<CDOLockState> getLocksOnNewObjects()
  {
    return delegateCommitContext.getLocksOnNewObjects();
  }

  public Map<CDOID, CDOObject> getDetachedObjects()
  {
    return delegateCommitContext.getDetachedObjects();
  }

  public Map<CDOID, CDORevisionDelta> getRevisionDeltas()
  {
    return delegateCommitContext.getRevisionDeltas();
  }

  public Collection<CDOLob<?>> getLobs()
  {
    return delegateCommitContext.getLobs();
  }

  public Object call() throws Exception
  {
    state.handle(this, progressMonitor);
    return true;
  }

  public CDOID provideCDOID(Object idOrObject)
  {
    CDOID id = getTransaction().provideCDOID(idOrObject);
    if (id instanceof CDOIDTempObjectExternalImpl)
    {
      if (idOrObject instanceof InternalEObject)
      {
        CDOIDTempObjectExternalImpl proxyTemp = (CDOIDTempObjectExternalImpl)id;
        if (!requestedIDs.containsKey(proxyTemp))
        {
          InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject((InternalEObject)idOrObject);
          InternalCDOTransaction cdoTransaction = (InternalCDOTransaction)cdoObject.cdoView();
          getTransactionManager().add(cdoTransaction, proxyTemp);
          requestedIDs.put(proxyTemp, cdoTransaction);
          objectToID.put(cdoObject, proxyTemp);
        }
      }
      else
      {
        throw new ImplementationError(MessageFormat.format(Messages.getString("CDOXACommitContextImpl.0"), idOrObject)); //$NON-NLS-1$
      }
    }

    return id;
  }

  public void preCommit()
  {
    delegateCommitContext.preCommit();
  }

  public void postCommit(CommitTransactionResult result)
  {
    if (result != null)
    {
      if (result.getRollbackMessage() != null)
      {
        final CDOReferenceAdjuster defaultReferenceAdjuster = result.getReferenceAdjuster();
        result.setReferenceAdjuster(new CDOReferenceAdjuster()
        {
          public Object adjustReference(Object id, EStructuralFeature feature, int index)
          {
            CDOIDTempObjectExternalImpl externalID = objectToID.get(id);
            if (externalID != null)
            {
              id = externalID;
            }

            return defaultReferenceAdjuster.adjustReference(id, feature, index);
          }
        });
      }

      delegateCommitContext.postCommit(result);
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOXACommitContext[{0}, {1}]", transactionManager, state);
  }
}
