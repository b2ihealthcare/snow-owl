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
 *    Simon McDuff - bug 215688
 *    Simon McDuff - bug 213402
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectReference;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.internal.cdo.object.CDOObjectReferenceImpl;
import org.eclipse.emf.spi.cdo.CDOSessionProtocol.CommitTransactionResult;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;
import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CommitTransactionRequest extends CDOClientRequestWithMonitoring<CommitTransactionResult>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionRequest.class);

  private static long sleepMillisForTesting = 0L;

  private CDOIDProvider idProvider; // CDOTransaction

  private String comment;

  private boolean releaseLocks;

  private CDOCommitData commitData;

  private Collection<CDOLob<?>> lobs;

  private Collection<CDOLockState> locksOnNewObjects;

  private int viewID;

  private CDOTransaction transaction;

  public CommitTransactionRequest(CDOClientProtocol protocol, InternalCDOCommitContext context)
  {
    this(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION, context);
  }

  public CommitTransactionRequest(CDOClientProtocol protocol, short signalID, InternalCDOCommitContext context)
  {
    super(protocol, signalID);

    transaction = context.getTransaction();
    comment = context.getCommitComment();
    releaseLocks = context.isAutoReleaseLocks();
    idProvider = context.getTransaction();
    commitData = context.getCommitData();
    lobs = context.getLobs();
    locksOnNewObjects = context.getLocksOnNewObjects();
    viewID = context.getViewID();
  }

  @Override
  protected int getMonitorTimeoutSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDONet4jSession session = (org.eclipse.emf.cdo.net4j.CDONet4jSession)getSession();
    return session.options().getCommitTimeout();
  }

  @Override
  protected CDOIDProvider getIDProvider()
  {
    return idProvider;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    requestingTransactionInfo(out);
    requestingCommit(out);
  }

  protected void requestingTransactionInfo(CDODataOutput out) throws IOException
  {
    out.writeInt(viewID);
  }

  protected void requestingCommit(CDODataOutput out) throws IOException
  {
    List<CDOPackageUnit> newPackageUnits = commitData.getNewPackageUnits();
    List<CDOIDAndVersion> newObjects = commitData.getNewObjects();
    List<CDORevisionKey> changedObjects = commitData.getChangedObjects();
    List<CDOIDAndVersion> detachedObjects = commitData.getDetachedObjects();

    out.writeBoolean(releaseLocks);
    out.writeString(comment);
    out.writeInt(newPackageUnits.size());
    out.writeInt(locksOnNewObjects.size());
    out.writeInt(newObjects.size());
    out.writeInt(changedObjects.size());
    out.writeInt(detachedObjects.size());

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} new package units", newPackageUnits.size()); //$NON-NLS-1$
    }

    for (CDOPackageUnit newPackageUnit : newPackageUnits)
    {
      out.writeCDOPackageUnit(newPackageUnit, true);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} locks on new objects", locksOnNewObjects.size()); //$NON-NLS-1$
    }

    for (CDOLockState lockState : locksOnNewObjects)
    {
      out.writeCDOLockState(lockState);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} new objects", newObjects.size()); //$NON-NLS-1$
    }

    for (CDOIDAndVersion newObject : newObjects)
    {
      out.writeCDORevision((CDORevision)newObject, CDORevision.UNCHUNKED);

      if (sleepMillisForTesting != 0L)
      {
        ConcurrencyUtil.sleep(sleepMillisForTesting);
      }
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} dirty objects", changedObjects.size()); //$NON-NLS-1$
    }

    for (CDORevisionKey changedObject : changedObjects)
    {
      out.writeCDORevisionDelta((CDORevisionDelta)changedObject);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0} detached objects", detachedObjects.size()); //$NON-NLS-1$
    }

    for (CDOIDAndVersion detachedObject : detachedObjects)
    {
      CDOID id = detachedObject.getID();
      out.writeCDOID(id);
      EClass eClass = getObjectType(id);
      out.writeCDOClassifierRef(eClass);
    }

    requestingLobs();
  }

  protected void requestingLobs() throws IOException
  {
    ExtendedDataOutputStream out = getRequestStream();
    out.writeInt(lobs.size());
    for (CDOLob<?> lob : lobs)
    {
      out.writeByteArray(lob.getID());
      long size = lob.getSize();
      if (lob instanceof CDOBlob)
      {
        CDOBlob blob = (CDOBlob)lob;
        out.writeLong(size);
        IOUtil.copyBinary(blob.getContents(), out, size);
      }
      else
      {
        CDOClob clob = (CDOClob)lob;
        out.writeLong(-size);
        IOUtil.copyCharacter(clob.getContents(), new OutputStreamWriter(out), size);
      }
    }
  }

  protected EClass getObjectType(CDOID id)
  {
    CDOObject object = transaction.getObject(id);
    return object.eClass();
  }

  @Override
  protected CommitTransactionResult confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    CommitTransactionResult result = confirmingCheckError(in);
    if (result != null)
    {
      return result;
    }

    result = confirmingResult(in);
    confirmingMappingNewObjects(in, result);
    confirmingNewLockStates(in, result);
    return result;
  }

  protected CommitTransactionResult confirmingCheckError(CDODataInput in) throws IOException
  {
    boolean success = in.readBoolean();
    if (!success)
    {
      String rollbackMessage = in.readString();

      // Do not log the error automatically
      if (TRACER.isEnabled())
      {
        TRACER.trace(rollbackMessage);
        OM.LOG.error(rollbackMessage);
      }

      CDOBranchPoint branchPoint = in.readCDOBranchPoint();
      long previousTimeStamp = in.readLong();

      List<CDOObjectReference> xRefs = null;
      int size = in.readInt();
      if (size != 0)
      {
        xRefs = new ArrayList<CDOObjectReference>(size);
        for (int i = 0; i < size; i++)
        {
          CDOIDReference idReference = in.readCDOIDReference();
          xRefs.add(new CDOObjectReferenceImpl(transaction, idReference));
        }
      }

      return new CommitTransactionResult(idProvider, rollbackMessage, branchPoint, previousTimeStamp, xRefs);
    }

    return null;
  }

  protected CommitTransactionResult confirmingResult(CDODataInput in) throws IOException
  {
    CDOBranchPoint branchPoint = in.readCDOBranchPoint();
    long previousTimeStamp = in.readLong();
    return new CommitTransactionResult(idProvider, branchPoint, previousTimeStamp);
  }

  protected void confirmingMappingNewObjects(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    for (;;)
    {
      CDOID id = in.readCDOID();
      if (CDOIDUtil.isNull(id))
      {
        break;
      }

      if (id instanceof CDOIDTemp)
      {
        CDOIDTemp oldID = (CDOIDTemp)id;
        CDOID newID = in.readCDOID();
        result.addIDMapping(oldID, newID);
      }
      else
      {
        throw new ClassCastException("Not a temporary ID: " + id);
      }
    }
  }

  protected void confirmingNewLockStates(CDODataInput in, CommitTransactionResult result) throws IOException
  {
    int n = in.readInt();
    CDOLockState[] newLockStates = new CDOLockState[n];

    for (int i = 0; i < n; i++)
    {
      newLockStates[i] = in.readCDOLockState();
    }

    result.setNewLockStates(newLockStates);
  }
}
