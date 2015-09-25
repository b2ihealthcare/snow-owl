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
package org.eclipse.emf.cdo.spi.common.commit;

import java.text.MessageFormat;
import java.util.LinkedList;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointImpl;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 */
public class CDOChangeSetSegment implements CDOBranchPoint
{
  private CDOBranchPoint branchPoint;

  private long endTime;

  public CDOChangeSetSegment(CDOBranch branch, long timeStamp, long endTime)
  {
    branchPoint = new CDOBranchPointImpl(branch, timeStamp);
    this.endTime = endTime;
  }

  public CDOBranch getBranch()
  {
    return branchPoint.getBranch();
  }

  public long getTimeStamp()
  {
    return branchPoint.getTimeStamp();
  }

  public long getEndTime()
  {
    return endTime;
  }

  public CDOBranchPoint getEndPoint()
  {
    return getBranch().getPoint(endTime);
  }

  public boolean isOpenEnded()
  {
    return endTime == CDOBranchPoint.UNSPECIFIED_DATE;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Segment[{0}, {1}, {2}]", getBranch(), getTimeStamp(), endTime); //$NON-NLS-1$
  }

  public static CDOChangeSetSegment[] createFrom(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    LinkedList<CDOChangeSetSegment> result = new LinkedList<CDOChangeSetSegment>();
    CDOBranch startBranch = startPoint.getBranch();
    CDOBranch endBranch = endPoint.getBranch();

    while (!ObjectUtil.equals(startBranch, endBranch))
    {
      CDOBranchPoint base = endBranch.getBase();
      result.addFirst(new CDOChangeSetSegment(endBranch, base.getTimeStamp(), endPoint.getTimeStamp()));
      endPoint = base;
      endBranch = base.getBranch();
    }

    result.addFirst(new CDOChangeSetSegment(startBranch, startPoint.getTimeStamp(), endPoint.getTimeStamp()));
    return result.toArray(new CDOChangeSetSegment[result.size()]);
  }
}
