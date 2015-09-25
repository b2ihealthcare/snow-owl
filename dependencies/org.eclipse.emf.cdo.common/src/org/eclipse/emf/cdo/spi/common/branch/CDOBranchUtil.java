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
package org.eclipse.emf.cdo.spi.common.branch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRange;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchManagerImpl;
import org.eclipse.emf.cdo.internal.common.branch.CDOBranchPointRangeImpl;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOBranchUtil
{
  private CDOBranchUtil()
  {
  }

  public static InternalCDOBranchManager createBranchManager()
  {
    return new CDOBranchManagerImpl();
  }

  public static CDOBranchPointRange createRange(CDOBranchPoint startPoint, CDOBranchPoint endPoint)
  {
    return new CDOBranchPointRangeImpl(startPoint, endPoint);
  }

  public static CDOBranchPoint copyBranchPoint(CDOBranchPoint source)
  {
    return source.getBranch().getPoint(source.getTimeStamp());
  }

  public static CDOBranchVersion copyBranchVersion(CDOBranchVersion source)
  {
    return source.getBranch().getVersion(source.getVersion());
  }

  public static boolean isContainedBy(CDOBranchPoint contained, CDOBranchPoint container)
  {
    CDOBranch containerBranch = container.getBranch();
    if (ObjectUtil.equals(containerBranch, contained.getBranch()))
    {
      return CDOCommonUtil.compareTimeStamps(contained.getTimeStamp(), container.getTimeStamp()) <= 0;
    }

    if (containerBranch == null)
    {
      return false;
    }

    return isContainedBy(contained, containerBranch.getBase());
  }

  public static CDOBranchPoint getAncestor(CDOBranchPoint point1, CDOBranchPoint point2)
  {
    if (point1.getBranch() == null)
    {
      // Must be the main branch base
      return point1;
    }

    if (point2.getBranch() == null)
    {
      // Must be the main branch base
      return point2;
    }

    CDOBranchPoint[] path1 = getPath(point1);
    CDOBranchPoint[] path2 = getPath(point2);
    for (CDOBranchPoint pathPoint1 : path1)
    {
      for (CDOBranchPoint pathPoint2 : path2)
      {
        if (ObjectUtil.equals(pathPoint1.getBranch(), pathPoint2.getBranch()))
        {
          if (CDOCommonUtil.compareTimeStamps(pathPoint1.getTimeStamp(), pathPoint2.getTimeStamp()) < 0)
          {
            return pathPoint1;
          }

          return pathPoint2;
        }
      }
    }

    // Can not happen because any two branches meet on the main branch
    return null;
  }

  public static CDOBranchPoint[] getPath(CDOBranchPoint point)
  {
    List<CDOBranchPoint> result = new ArrayList<CDOBranchPoint>();
    getPath(point, result);
    return result.toArray(new CDOBranchPoint[result.size()]);
  }

  private static void getPath(CDOBranchPoint point, List<CDOBranchPoint> result)
  {
    CDOBranch branch = point.getBranch();
    if (branch != null)
    {
      result.add(point);
      getPath(branch.getBase(), result);
    }
  }
}
