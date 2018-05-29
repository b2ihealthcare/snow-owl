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
 *    Andre Dietisheim - bug 256649
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.spi.server.InternalView;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.concurrent.RWOLockManager.LockState;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.ProgressDistributor;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CommitTransactionIndication extends CDOServerIndicationWithMonitoring
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, CommitTransactionIndication.class);

  protected InternalCommitContext commitContext;

  public CommitTransactionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_TRANSACTION);
  }

  protected CommitTransactionIndication(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  protected InternalCDOPackageRegistry getPackageRegistry()
  {
    return commitContext.getPackageRegistry();
  }

  @Override
  protected void indicatingFailed()
  {
    if (commitContext != null)
    {
      commitContext.postCommit(false);
      commitContext = null;
    }
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    try
    {
      monitor.begin(OMMonitor.TEN);
      indicatingCommit(in, monitor.fork(OMMonitor.ONE));
      indicatingCommit(monitor.fork(OMMonitor.TEN - OMMonitor.ONE));
    }
    catch (IOException ex)
    {
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      throw WrappedException.wrap(ex);
    }
    finally
    {
      monitor.done();
    }
  }

  protected void indicatingCommit(CDODataInput in, OMMonitor monitor) throws Exception
  {
    // Create commit context
    initializeCommitContext(in);
    commitContext.preWrite();

    boolean autoReleaseLocksEnabled = in.readBoolean();
    commitContext.setAutoReleaseLocksEnabled(autoReleaseLocksEnabled);
    String commitComment = in.readString();

    InternalCDOPackageUnit[] newPackageUnits = new InternalCDOPackageUnit[in.readInt()];
    CDOLockState[] locksOnNewObjects = new CDOLockState[in.readInt()];
    InternalCDORevision[] newObjects = new InternalCDORevision[in.readInt()];
    InternalCDORevisionDelta[] dirtyObjectDeltas = new InternalCDORevisionDelta[in.readInt()];
    CDOID[] detachedObjects = new CDOID[in.readInt()];
    monitor.begin(newPackageUnits.length + newObjects.length + dirtyObjectDeltas.length + detachedObjects.length);

    try
    {
      // New package units
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} new package units", newPackageUnits.length); //$NON-NLS-1$
      }

      InternalCDOPackageRegistry packageRegistry = commitContext.getPackageRegistry();
      ResourceSet resourceSet = createResourceSet(packageRegistry);
      for (int i = 0; i < newPackageUnits.length; i++)
      {
        newPackageUnits[i] = (InternalCDOPackageUnit)in.readCDOPackageUnit(resourceSet);
        packageRegistry.putPackageUnit(newPackageUnits[i]); // Must happen before readCDORevision!!!
        monitor.worked();
      }

      // When all packages are deserialized and registered, resolve them
      // Note: EcoreUtil.resolveAll(resourceSet) does *not* do the trick
      EMFUtil.safeResolveAll(resourceSet);

      // Locks on new objects
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} locks on new objects", locksOnNewObjects.length); //$NON-NLS-1$
      }

      for (int i = 0; i < locksOnNewObjects.length; i++)
      {
        locksOnNewObjects[i] = in.readCDOLockState();
        monitor.worked();
      }

      // New objects
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} new objects", newObjects.length); //$NON-NLS-1$
      }

      for (int i = 0; i < newObjects.length; i++)
      {
        newObjects[i] = (InternalCDORevision)in.readCDORevision();
        monitor.worked();
      }

      // Make the assignment of permanent IDs predictable
      Arrays.sort(newObjects, new Comparator<InternalCDORevision>()
      {
        public int compare(InternalCDORevision r1, InternalCDORevision r2)
        {
          return r1.getID().compareTo(r2.getID());
        }
      });

      // Dirty objects
      if (TRACER.isEnabled())
      {
        TRACER.format("Reading {0} dirty object deltas", dirtyObjectDeltas.length); //$NON-NLS-1$
      }

      for (int i = 0; i < dirtyObjectDeltas.length; i++)
      {
        dirtyObjectDeltas[i] = (InternalCDORevisionDelta)in.readCDORevisionDelta();
        monitor.worked();
      }

      Map<CDOID, EClass> detachedObjectTypes = new HashMap<CDOID, EClass>();

      for (int i = 0; i < detachedObjects.length; i++)
      {
        CDOID id = in.readCDOID();
        detachedObjects[i] = id;

        EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
        detachedObjectTypes.put(id, eClass);

        monitor.worked();
      }

      if (detachedObjectTypes.isEmpty())
      {
        detachedObjectTypes = null;
      }

      commitContext.setNewPackageUnits(newPackageUnits);
      commitContext.setLocksOnNewObjects(locksOnNewObjects);
      commitContext.setNewObjects(newObjects);
      commitContext.setDirtyObjectDeltas(dirtyObjectDeltas);
      commitContext.setDetachedObjects(detachedObjects);
      commitContext.setDetachedObjectTypes(detachedObjectTypes);
      commitContext.setCommitComment(commitComment);
      commitContext.setLobs(getIndicationStream());
    }
    finally
    {
      monitor.done();
    }
  }

  private ResourceSet createResourceSet(InternalCDOPackageRegistry packageRegistry)
  {
    ResourceSet resourceSet = new ResourceSetImpl()
    {
      @Override
      protected void demandLoad(Resource resource) throws IOException
      {
        // Do nothing: we don't want this ResourceSet to attempt demandloads.
      }
    };

    Resource.Factory resourceFactory = new EcoreResourceFactoryImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }

  protected void initializeCommitContext(CDODataInput in) throws Exception
  {
    int viewID = in.readInt();
    commitContext = getTransaction(viewID).createCommitContext();
  }

  protected void indicatingCommit(OMMonitor monitor)
  {
    ProgressDistributor distributor = getStore().getIndicatingCommitDistributor();
    distributor.run(InternalCommitContext.OPS, commitContext, monitor);
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    boolean success = false;

    try
    {
      success = respondingException(out, commitContext.getRollbackMessage(), commitContext.getXRefs());
      if (success)
      {
        respondingResult(out);
        respondingMappingNewObjects(out);
        respondingNewLockStates(out);
      }
    }
    finally
    {
      commitContext.postCommit(success);
    }
  }

  protected boolean respondingException(CDODataOutput out, String rollbackMessage, List<CDOIDReference> xRefs)
      throws Exception
  {
    boolean success = rollbackMessage == null;
    out.writeBoolean(success);
    if (!success)
    {
      out.writeString(rollbackMessage);
      out.writeCDOBranchPoint(commitContext.getBranchPoint());
      out.writeLong(commitContext.getPreviousTimeStamp());

      if (xRefs != null)
      {
        out.writeInt(xRefs.size());
        for (CDOIDReference xRef : xRefs)
        {
          out.writeCDOIDReference(xRef);
        }
      }
      else
      {
        out.writeInt(0);
      }
    }

    return success;
  }

  protected void respondingResult(CDODataOutput out) throws Exception
  {
    out.writeCDOBranchPoint(commitContext.getBranchPoint());
    out.writeLong(commitContext.getPreviousTimeStamp());
  }

  protected void respondingMappingNewObjects(CDODataOutput out) throws Exception
  {
    Map<CDOID, CDOID> idMappings = commitContext.getIDMappings();
    for (Entry<CDOID, CDOID> entry : idMappings.entrySet())
    {
      CDOID oldID = entry.getKey();
      CDOID newID = entry.getValue();
      out.writeCDOID(oldID);
      out.writeCDOID(newID);
    }

    out.writeCDOID(CDOID.NULL);
  }

  protected void respondingNewLockStates(CDODataOutput out) throws Exception
  {
    List<LockState<Object, IView>> newLockStates = commitContext.getPostCommmitLockStates();
    if (newLockStates != null)
    {
      out.writeInt(newLockStates.size());
      for (LockState<Object, IView> lockState : newLockStates)
      {
        CDOLockState cdoLockState = CDOLockUtil.createLockState(lockState);
        out.writeCDOLockState(cdoLockState);
      }
    }
    else
    {
      out.writeInt(0);
    }
  }

  protected InternalTransaction getTransaction(int viewID)
  {
    InternalView view = getSession().getView(viewID);
    if (view instanceof InternalTransaction)
    {
      return (InternalTransaction)view;
    }

    throw new IllegalStateException("Illegal transaction: " + view); //$NON-NLS-1$
  }
}
