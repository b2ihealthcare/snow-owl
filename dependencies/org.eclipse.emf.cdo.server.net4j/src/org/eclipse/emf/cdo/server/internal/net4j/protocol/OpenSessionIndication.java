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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonSession.Options.LockNotificationMode;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.NotAuthenticatedException;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.RepositoryNotFoundException;
import org.eclipse.emf.cdo.server.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalSessionManager;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OpenSessionIndication extends CDOServerIndicationWithMonitoring
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, OpenSessionIndication.class);

  private String repositoryName;

  private boolean passiveUpdateEnabled;

  private PassiveUpdateMode passiveUpdateMode;

  private LockNotificationMode lockNotificationMode;

  private InternalRepository repository;

  private InternalSession session;

  public OpenSessionIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OPEN_SESSION);
  }

  @Override
  protected InternalRepository getRepository()
  {
    return repository;
  }

  @Override
  protected InternalSession getSession()
  {
    return session;
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    repositoryName = in.readString();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read repositoryName: {0}", repositoryName); //$NON-NLS-1$
    }

    passiveUpdateEnabled = in.readBoolean();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read passiveUpdateEnabled: {0}", passiveUpdateEnabled); //$NON-NLS-1$
    }

    passiveUpdateMode = in.readEnum(PassiveUpdateMode.class);
    if (TRACER.isEnabled())
    {
      TRACER.format("Read passiveUpdateMode: {0}", passiveUpdateMode); //$NON-NLS-1$
    }

    lockNotificationMode = in.readEnum(LockNotificationMode.class);
    if (TRACER.isEnabled())
    {
      TRACER.format("Read lockNotificationMode: {0}", lockNotificationMode); //$NON-NLS-1$
    }
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      CDOServerProtocol protocol = getProtocol();
      IRepositoryProvider repositoryProvider = protocol.getRepositoryProvider();
      repository = (InternalRepository)repositoryProvider.getRepository(repositoryName);
      if (repository == null)
      {
        throw new RepositoryNotFoundException(repositoryName);
      }

      try
      {
        InternalSessionManager sessionManager = repository.getSessionManager();
        session = sessionManager.openSession(protocol);
      }
      catch (NotAuthenticatedException ex)
      {
        // Skip response because the user has canceled the authentication
        out.writeInt(0);
        return;
      }

      session.setPassiveUpdateEnabled(passiveUpdateEnabled);
      session.setPassiveUpdateMode(passiveUpdateMode);
      session.setLockNotificationMode(lockNotificationMode);

      protocol.setInfraStructure(session);
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing sessionID: {0}", session.getSessionID()); //$NON-NLS-1$
      }

      out.writeInt(session.getSessionID());
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing userID: {0}", session.getUserID()); //$NON-NLS-1$
      }

      out.writeString(session.getUserID());
      if (TRACER.isEnabled())
      {
        TRACER.format("Writing repositoryUUID: {0}", repository.getUUID()); //$NON-NLS-1$
      }

      out.writeString(repository.getUUID());
      out.writeEnum(repository.getType());
      out.writeEnum(repository.getState());
      out.writeString(repository.getStoreType());

      Set<CDOID.ObjectType> objectIDTypes = repository.getObjectIDTypes();
      int types = objectIDTypes.size();
      out.writeInt(types);
      for (CDOID.ObjectType objectIDType : objectIDTypes)
      {
        out.writeEnum(objectIDType);
      }

      out.writeLong(repository.getCreationTime());
      out.writeLong(repository.getLastCommitTimeStamp());
      out.writeCDOID(repository.getRootResourceID());
      out.writeBoolean(repository.isSupportingAudits());
      out.writeBoolean(repository.isSupportingBranches());
      out.writeBoolean(repository.isSupportingEcore());
      out.writeBoolean(repository.isEnsuringReferentialIntegrity());
      out.writeEnum(repository.getIDGenerationLocation());

      CDOPackageUnit[] packageUnits = repository.getPackageRegistry().getPackageUnits();
      out.writeCDOPackageUnits(packageUnits);
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }
}
