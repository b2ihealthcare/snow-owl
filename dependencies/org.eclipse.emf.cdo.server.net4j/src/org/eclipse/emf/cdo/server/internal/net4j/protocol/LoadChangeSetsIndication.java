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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadChangeSetsIndication extends CDOServerReadIndication
{
  private CDOBranchPointRange[] ranges;

  public LoadChangeSetsIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_CHANGE_SETS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    int size = in.readInt();
    ranges = new CDOBranchPointRange[size];
    for (int i = 0; i < ranges.length; i++)
    {
      CDOBranchPoint startPoint = in.readCDOBranchPoint();
      CDOBranchPoint endPoint = in.readCDOBranchPoint();
      ranges[i] = CDOBranchUtil.createRange(startPoint, endPoint);
    }
  }

  @Override
  protected void responding(final CDODataOutput out) throws IOException
  {
    InternalRepository repository = getRepository();
    for (CDOBranchPointRange range : ranges)
    {
      CDOBranchPoint startPoint = range.getStartPoint();
      CDOBranchPoint endPoint = range.getEndPoint();
      CDOChangeSetData data = repository.getChangeSet(startPoint, endPoint);
      out.writeCDOChangeSetData(data); // Exposes revision to client side
    }
  }
}
