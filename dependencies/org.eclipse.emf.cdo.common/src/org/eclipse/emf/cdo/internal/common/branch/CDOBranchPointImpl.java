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

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 */
public class CDOBranchPointImpl implements CDOBranchPoint, Comparable<CDOBranchPoint>
{
  private CDOBranch branch;

  private long timeStamp;

  public CDOBranchPointImpl(CDOBranch branch, long timeStamp)
  {
    this.branch = branch;
    this.timeStamp = timeStamp;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public int compareTo(CDOBranchPoint o)
  {
    int result = branch.compareTo(o.getBranch());
    if (result == 0)
    {
      result = CDOCommonUtil.compareTimeStamps(timeStamp, o.getTimeStamp());
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    return branch.hashCode() ^ new Long(timeStamp).hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOBranchPoint)
    {
      CDOBranchPoint that = (CDOBranchPoint)obj;
      return ObjectUtil.equals(branch, that.getBranch()) && timeStamp == that.getTimeStamp();
    }

    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("BranchPoint[{0}, {1}]", branch, CDOCommonUtil.formatTimeStamp(timeStamp)); //$NON-NLS-1$
  }
}
