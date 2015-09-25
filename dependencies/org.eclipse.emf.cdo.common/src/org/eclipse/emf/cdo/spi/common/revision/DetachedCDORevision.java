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
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EClass;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class DetachedCDORevision extends SyntheticCDORevision
{
  private int version;

  private long timeStamp;

  private long revised;

  public DetachedCDORevision(EClass eClass, CDOID id, CDOBranch branch, int version, long timeStamp)
  {
    this(eClass, id, branch, version, timeStamp, UNSPECIFIED_DATE);
  }

  /**
   * @since 4.0
   */
  public DetachedCDORevision(EClass eClass, CDOID id, CDOBranch branch, int version, long timeStamp, long revised)
  {
    super(eClass, id, branch);
    this.version = version;
    this.timeStamp = timeStamp;
    this.revised = revised;
  }

  @Override
  public final int getVersion()
  {
    return version;
  }

  @Override
  public long getTimeStamp()
  {
    return timeStamp;
  }

  @Override
  public long getRevised()
  {
    return revised;
  }

  @Override
  public void setRevised(long revised)
  {
    this.revised = revised;
  }
}
