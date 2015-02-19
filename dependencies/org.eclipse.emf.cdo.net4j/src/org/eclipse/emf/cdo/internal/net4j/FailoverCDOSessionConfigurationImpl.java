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
package org.eclipse.emf.cdo.internal.net4j;

import org.eclipse.emf.cdo.net4j.FailoverCDOSessionConfiguration;
import org.eclipse.emf.cdo.session.CDOSession.ExceptionHandler;

import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class FailoverCDOSessionConfigurationImpl extends RecoveringCDOSessionConfigurationImpl implements
    FailoverCDOSessionConfiguration
{
  private String monitorConnectorDescription;

  private String repositoryGroup;

  public FailoverCDOSessionConfigurationImpl(String monitorConnectorDescription, String repositoryGroup,
      IManagedContainer container)
  {
    super(container);

    this.monitorConnectorDescription = monitorConnectorDescription;
    this.repositoryGroup = repositoryGroup;
  }

  public String getMonitorConnectorDescription()
  {
    return monitorConnectorDescription;
  }

  public String getRepositoryGroup()
  {
    return repositoryGroup;
  }

  @Override
  public void setRepositoryName(String repositoryName)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setConnector(IConnector connector)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setExceptionHandler(ExceptionHandler exceptionHandler)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public InternalCDOSession createSession()
  {
    return new FailoverCDOSessionImpl();
  }

  @Override
  protected void configureSession(InternalCDOSession session)
  {
    super.configureSession(session);

    FailoverCDOSessionImpl sessionImpl = (FailoverCDOSessionImpl)session;
    sessionImpl.setMonitorConnectionDescription(monitorConnectorDescription);
    sessionImpl.setRepositoryGroup(repositoryGroup);
  }
}
