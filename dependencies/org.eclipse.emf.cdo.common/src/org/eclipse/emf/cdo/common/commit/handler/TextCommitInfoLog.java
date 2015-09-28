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
package org.eclipse.emf.cdo.common.commit.handler;

import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.commit.CDOCommitInfoUtil;

/**
 * A {@link CDOCommitInfoHandler commit info handler} that synchronously writes {@link CDOCommitInfo commit infos} to a
 * text log.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class TextCommitInfoLog implements CDOCommitInfoHandler
{
  private PrintStream printStream;

  public TextCommitInfoLog(OutputStream stream)
  {
    printStream = stream instanceof PrintStream ? (PrintStream)stream : new PrintStream(stream);
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    try
    {
      if (printStream != null)
      {
        printStream.println(commitInfo);

        for (CDOPackageUnit packageUnit : commitInfo.getNewPackageUnits())
        {
          printStream.println("  P " + packageUnit.getID());
        }

        CDOCommitInfoUtil.dump(printStream, commitInfo);
        printStream.flush();
      }
    }
    catch (Exception ex)
    {
      handleException(ex);
    }
  }

  protected void handleException(Exception ex)
  {
    OM.LOG.error(ex);
  }
}
