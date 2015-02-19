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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.io.IOException;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class SwitchTargetRequest extends CDOClientRequestWithMonitoring<Object>
{
  private int viewID;

  private CDOBranchPoint branchPoint;

  private List<InternalCDOObject> invalidObjects;

  private List<CDORevisionKey> allChangedObjects;

  private List<CDOIDAndVersion> allDetachedObjects;

  public SwitchTargetRequest(CDOClientProtocol protocol, int viewID, CDOBranchPoint branchPoint,
      List<InternalCDOObject> invalidObjects, List<CDORevisionKey> allChangedObjects,
      List<CDOIDAndVersion> allDetachedObjects)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_SWITCH_TARGET);
    this.viewID = viewID;
    this.branchPoint = branchPoint;
    this.invalidObjects = invalidObjects;
    this.allChangedObjects = allChangedObjects;
    this.allDetachedObjects = allDetachedObjects;
  }

  @Override
  protected void requesting(CDODataOutput out, OMMonitor monitor) throws IOException
  {
    out.writeInt(viewID);
    out.writeCDOBranchPoint(branchPoint);

    out.writeInt(invalidObjects.size());
    for (InternalCDOObject object : invalidObjects)
    {
      out.writeCDOID(object.cdoID());
    }
  }

  @Override
  protected boolean[] confirming(CDODataInput in, OMMonitor monitor) throws IOException
  {
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      allChangedObjects.add(in.readCDORevisionDelta());
    }

    size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOID id = in.readCDOID();
      allDetachedObjects.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
    }

    return null;
  }
}
