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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOAuthenticator;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.CDOAuthenticationResult;

import org.eclipse.net4j.signal.IndicationWithMonitoring;
import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class AuthenticationIndication extends IndicationWithMonitoring
{
  private byte[] randomToken;

  public AuthenticationIndication(SignalProtocol<?> protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_AUTHENTICATION);
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }

  protected InternalCDOSession getSession()
  {
    return (InternalCDOSession)getProtocol().getSession();
  }

  @Override
  protected void indicating(ExtendedDataInputStream in, OMMonitor monitor) throws Exception
  {
    randomToken = in.readByteArray();
  }

  @Override
  protected void responding(ExtendedDataOutputStream out, OMMonitor monitor) throws Exception
  {
    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      CDOAuthenticator authenticator = getSession().getAuthenticator();
      if (authenticator == null)
      {
        throw new IllegalStateException("No authenticator configured"); //$NON-NLS-1$
      }

      CDOAuthenticationResult result = authenticator.authenticate(randomToken);
      if (result == null)
      {
        out.writeBoolean(false);
        return;
      }

      String userID = result.getUserID();
      if (userID == null)
      {
        throw new SecurityException("No user ID"); //$NON-NLS-1$
      }

      byte[] cryptedToken = result.getCryptedToken();
      if (cryptedToken == null)
      {
        throw new SecurityException("No crypted token"); //$NON-NLS-1$
      }

      out.writeBoolean(true);
      result.write(out);
    }
    catch (Exception ex)
    {
      out.writeBoolean(false);
      throw ex;
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }
}
