/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.common.lock;

import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Caspar De Groot
 */
public class CDOLockOwnerImpl implements CDOLockOwner
{
  private final int sessionID;

  private final int viewID;

  private final String durableLockingID;

  private final boolean isDurableView;

  public CDOLockOwnerImpl(int sessionID, int viewID, String durableLockingID, boolean isDurableView)
  {
    this.sessionID = sessionID;
    this.viewID = viewID;
    this.durableLockingID = durableLockingID;
    this.isDurableView = isDurableView;
  }

  public int getSessionID()
  {
    return sessionID;
  }

  public int getViewID()
  {
    return viewID;
  }

  public String getDurableLockingID()
  {
    return durableLockingID;
  }

  public boolean isDurableView()
  {
    return isDurableView;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(sessionID, viewID);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOLockOwner)
    {
      CDOLockOwner that = (CDOLockOwner)obj;
      return sessionID == that.getSessionID() && viewID == that.getViewID();
    }

    return false;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder("CDOLockOwner[");
    builder.append(sessionID);
    builder.append(':');
    builder.append(viewID);
    builder.append(']');
    return builder.toString();
  }
}
