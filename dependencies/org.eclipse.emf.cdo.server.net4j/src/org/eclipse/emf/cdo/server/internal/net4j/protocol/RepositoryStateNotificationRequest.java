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
 *    Simon McDuff - bug 233490
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class RepositoryStateNotificationRequest extends CDOServerRequest
{
  private final CDOCommonRepository.State oldState;

  private final CDOCommonRepository.State newState;

  private final CDOID rootResourceID;

  public RepositoryStateNotificationRequest(CDOServerProtocol serverProtocol, CDOCommonRepository.State oldState,
      CDOCommonRepository.State newState, CDOID rootResourceID)
  {
    super(serverProtocol, CDOProtocolConstants.SIGNAL_REPOSITORY_STATE_NOTIFICATION);
    this.oldState = oldState;
    this.newState = newState;
    this.rootResourceID = rootResourceID;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    out.writeEnum(oldState);
    out.writeEnum(newState);
    out.writeCDOID(rootResourceID);
  }
}
