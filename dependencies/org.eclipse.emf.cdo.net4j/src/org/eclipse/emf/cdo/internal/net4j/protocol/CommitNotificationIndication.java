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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class CommitNotificationIndication extends CDOClientIndication
{
  public CommitNotificationIndication(CDOClientProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_COMMIT_NOTIFICATION);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    InternalCDOSession session = getSession();
    CDOCommitInfo commitInfo = in.readCDOCommitInfo();
    session.handleCommitNotification(commitInfo);
  }
}
