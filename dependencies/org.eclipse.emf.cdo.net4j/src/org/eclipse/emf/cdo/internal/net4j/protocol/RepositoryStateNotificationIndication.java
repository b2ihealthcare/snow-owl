/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionConfigurationImpl.RepositoryInfo;
import org.eclipse.emf.cdo.internal.net4j.CDONet4jSessionImpl;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryStateNotificationIndication extends CDOClientIndication
{
  public RepositoryStateNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_REPOSITORY_STATE_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    CDOCommonRepository.State oldState = in.readEnum(CDOCommonRepository.State.class);
    CDOCommonRepository.State newState = in.readEnum(CDOCommonRepository.State.class);
    CDOID rootResourceID = in.readCDOID();

    CDONet4jSessionImpl session = (CDONet4jSessionImpl)getSession();
    RepositoryInfo repositoryInfo = (RepositoryInfo)session.getRepositoryInfo();
    repositoryInfo.setState(newState);
    repositoryInfo.setRootResourceID(rootResourceID);

    session.handleRepositoryStateChanged(oldState, newState);
  }
}
