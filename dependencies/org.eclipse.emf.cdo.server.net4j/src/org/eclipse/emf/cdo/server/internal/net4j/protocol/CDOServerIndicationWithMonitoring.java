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

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataOutputImpl;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerIndicationWithMonitoring extends IndicationWithMonitoring
{
  private ExtendedDataInputStream indicationStream;

  private ExtendedDataOutputStream responseStream;

  protected CDOServerIndicationWithMonitoring(CDOServerProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  public CDOServerProtocol getProtocol()
  {
    return (CDOServerProtocol)super.getProtocol();
  }

  protected ExtendedDataInputStream getIndicationStream()
  {
    return indicationStream;
  }

  protected ExtendedDataOutputStream getResponseStream()
  {
    return responseStream;
  }

  protected InternalSession getSession()
  {
    return getProtocol().getSession();
  }

  protected InternalCDOPackageRegistry getPackageRegistry()
  {
    return getRepository().getPackageRegistry(false);
  }

  protected InternalRepository getRepository()
  {
    InternalRepository repository = getSession().getManager().getRepository();
    if (!LifecycleUtil.isActive(repository))
    {
      throw new IllegalStateException("CDORepositoryInfo has been deactivated"); //$NON-NLS-1$
    }

    return repository;
  }

  protected IStore getStore()
  {
    IStore store = getRepository().getStore();
    if (!LifecycleUtil.isActive(store))
    {
      throw new IllegalStateException("Store has been deactivated"); //$NON-NLS-1$
    }

    return store;
  }

  @Override
  protected final void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    indicationStream = in;

    try
    {
      indicating(new CDODataInputImpl(in)
      {
        @Override
        protected CDOPackageRegistry getPackageRegistry()
        {
          return CDOServerIndicationWithMonitoring.this.getPackageRegistry();
        }

        @Override
        protected StringIO getPackageURICompressor()
        {
          return getProtocol().getPackageURICompressor();
        }

        @Override
        protected CDOBranchManager getBranchManager()
        {
          return CDOServerIndicationWithMonitoring.this.getRepository().getBranchManager();
        }

        @Override
        protected CDOCommitInfoManager getCommitInfoManager()
        {
          return CDOServerIndicationWithMonitoring.this.getRepository().getCommitInfoManager();
        }

        @Override
        protected CDORevisionFactory getRevisionFactory()
        {
          return CDOServerIndicationWithMonitoring.this.getRepository().getRevisionManager().getFactory();
        }

        @Override
        protected CDOLobStore getLobStore()
        {
          return null; // Not used on server
        }

        @Override
        protected CDOListFactory getListFactory()
        {
          return CDOListImpl.FACTORY;
        }
      }, monitor);
    }
    catch (Exception ex)
    {
      indicatingFailed();
      throw ex;
    }
    catch (Error ex)
    {
      indicatingFailed();
      throw ex;
    }
  }

  @Override
  protected final void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    responseStream = out;
    responding(new CDODataOutputImpl(out)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return CDOServerIndicationWithMonitoring.this.getPackageRegistry();
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return CDOServerIndicationWithMonitoring.this.getSession();
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }
    }, monitor);
  }

  protected void indicatingFailed()
  {
  }

  protected abstract void indicating(CDODataInput in, OMMonitor monitor) throws Exception;

  protected abstract void responding(CDODataOutput out, OMMonitor monitor) throws Exception;
}
