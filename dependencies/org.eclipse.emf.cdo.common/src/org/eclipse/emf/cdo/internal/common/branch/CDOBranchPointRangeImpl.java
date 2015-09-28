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
package org.eclipse.emf.cdo.internal.common.branch;

import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;

/**
 * @author Eike Stepper
 */
public class CDOBranchPointRangeImpl implements CDOBranchPointRange
{
  private CDOBranchPoint startPoint;

  private CDOBranchPoint endPoint;

  public CDOBranchPointRangeImpl(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    this.startPoint = startPoint;
    this.endPoint = endPoint;
  }

  public CDOBranchPoint getStartPoint()
  {
    return startPoint;
  }

  public CDOBranchPoint getEndPoint()
  {
    return endPoint;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Range[{0}, {1}]", startPoint, endPoint); //$NON-NLS-1$
  }
}
