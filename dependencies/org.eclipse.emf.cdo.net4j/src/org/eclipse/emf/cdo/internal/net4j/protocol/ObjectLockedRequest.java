/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.io.IOException;

/**
 * @author Simon McDuff
 */
public class ObjectLockedRequest extends CDOClientRequest<Boolean>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, ObjectLockedRequest.class);

  private CDOView view;

  private CDOObject object;

  private LockType lockType;

  private boolean byOthers;

  public ObjectLockedRequest(CDOClientProtocol protocol, CDOView view, CDOObject object, LockType lockType,
      boolean byOthers)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_OBJECT_LOCKED);
    this.view = view;
    this.object = object;
    this.lockType = lockType;
    this.byOthers = byOthers;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Requesting if object {0} has  of lock for object {1}", object.cdoID(), //$NON-NLS-1$
          lockType == LockType.READ ? "read" : "write"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    out.writeInt(view.getViewID());
    out.writeCDOLockType(lockType);
    out.writeCDOID(object.cdoID());
    out.writeBoolean(byOthers);
  }

  @Override
  protected Boolean confirming(CDODataInput in) throws IOException
  {
    return in.readBoolean();
  }
}
