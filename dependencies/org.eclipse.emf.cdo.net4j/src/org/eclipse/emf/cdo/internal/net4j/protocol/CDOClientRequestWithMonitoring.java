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
import org.eclipse.emf.cdo.internal.common.revision.CDOListWithElementProxiesImpl;

import org.eclipse.net4j.signal.RequestWithMonitoring;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.io.StringIO;
import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public abstract class CDOClientRequestWithMonitoring<RESULT> extends RequestWithMonitoring<RESULT>
{
  private ExtendedDataOutputStream requestStream;

  private ExtendedDataInputStream confirmationStream;

  public CDOClientRequestWithMonitoring(CDOClientProtocol protocol, short signalID)
  {
    super(protocol, signalID);
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }

  protected ExtendedDataOutputStream getRequestStream()
  {
    return requestStream;
  }

  protected ExtendedDataInputStream getConfirmationStream()
  {
    return confirmationStream;
  }

  protected InternalCDOSession getSession()
  {
    return (InternalCDOSession)getProtocol().getSession();
  }

  protected CDOIDProvider getIDProvider()
  {
    return null;
  }

  @Override
  protected int getMonitorProgressSeconds()
  {
    org.eclipse.emf.cdo.net4j.CDONet4jSession session = (org.eclipse.emf.cdo.net4j.CDONet4jSession)getSession();
    return session.options().getProgressInterval();
  }

  @Override
  protected final void requesting(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    requestStream = out;
    requesting(new CDODataOutputImpl(out)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return CDOClientRequestWithMonitoring.this.getIDProvider();
      }

      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }
    }, monitor);
  }

  @Override
  protected final RESULT confirming(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    confirmationStream = in;
    return confirming(new CDODataInputImpl(in)
    {
      @Override
      protected StringIO getPackageURICompressor()
      {
        return getProtocol().getPackageURICompressor();
      }

      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return getSession().getPackageRegistry();
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return getSession().getBranchManager();
      }

      @Override
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return getSession().getCommitInfoManager();
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return getSession().getRevisionManager().getFactory();
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return getSession().getLobStore();
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return CDOListWithElementProxiesImpl.FACTORY;
      }
    }, monitor);
  }

  protected abstract void requesting(CDODataOutput out, OMMonitor monitor) throws IOException;

  protected abstract RESULT confirming(CDODataInput in, OMMonitor monitor) throws IOException;
}
