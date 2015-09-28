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
package org.eclipse.emf.cdo.common.util;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.cdo.common.branch.CDOBranchManager;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.lob.CDOLobStore;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOListFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataInputImpl;
import org.eclipse.emf.cdo.internal.common.protocol.CDODataOutputImpl;
import org.eclipse.net4j.util.io.ExtendedDataInputStream;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * Various static methods that may help with I/O and time stamps.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public final class CDOCommonUtil
{
  /**
   * @since 4.0
   */
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'.'SSS");

  private CDOCommonUtil()
  {
  }

  /**
   * @since 4.0
   */
  public static CDODataInput createCDODataInput(ExtendedDataInputStream inputStream,
      final CDOPackageRegistry packageRegistry, final CDOBranchManager branchManager,
      final CDOCommitInfoManager commitManager, final CDORevisionFactory revisionFactory,
      final CDOListFactory listFactory, final CDOLobStore lobStore) throws IOException
  {
    return new CDODataInputImpl(inputStream)
    {
      @Override
      protected CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
      protected CDOBranchManager getBranchManager()
      {
        return branchManager;
      }

      @Override
      protected CDOCommitInfoManager getCommitInfoManager()
      {
        return commitManager;
      }

      @Override
      protected CDORevisionFactory getRevisionFactory()
      {
        return revisionFactory;
      }

      @Override
      protected CDOListFactory getListFactory()
      {
        return listFactory;
      }

      @Override
      protected CDOLobStore getLobStore()
      {
        return lobStore;
      }
    };
  }

  /**
   * @since 4.0
   */
  public static CDODataOutput createCDODataOutput(ExtendedDataOutput extendedDataOutputStream,
      final CDOPackageRegistry packageRegistry, final CDOIDProvider idProvider)
  {
    return new CDODataOutputImpl(extendedDataOutputStream)
    {
      @Override
      public CDOPackageRegistry getPackageRegistry()
      {
        return packageRegistry;
      }

      @Override
      public CDOIDProvider getIDProvider()
      {
        return idProvider;
      }
    };
  }

  public static boolean isValidTimeStamp(long timeStamp, long startTime, long endTime)
  {
    if (timeStamp == CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return endTime == CDOBranchPoint.UNSPECIFIED_DATE;
    }

    return (endTime == CDOBranchPoint.UNSPECIFIED_DATE || endTime >= timeStamp) && timeStamp >= startTime;
  }

  public static int compareTimeStamps(long t1, long t2)
  {
    if (t1 == CDORevision.UNSPECIFIED_DATE)
    {
      t1 = Long.MAX_VALUE;
    }

    if (t2 == CDORevision.UNSPECIFIED_DATE)
    {
      t2 = Long.MAX_VALUE;
    }

    return t1 < t2 ? -1 : t1 == t2 ? 0 : 1;
  }

  public static String formatTimeStamp()
  {
    return formatTimeStamp(System.currentTimeMillis());
  }

  public static String formatTimeStamp(long timeStamp)
  {
    if (timeStamp == CDORevision.UNSPECIFIED_DATE)
    {
      return "*";
    }

    return DATE_FORMAT.format(new Date(timeStamp));
  }

  /**
   * @since 4.0
   */
  public static long parseTimeStamp(String timeStamp) throws ParseException
  {
    if ("*".equals(timeStamp))
    {
      return CDORevision.UNSPECIFIED_DATE;
    }

    return DATE_FORMAT.parse(timeStamp).getTime();
  }
}
