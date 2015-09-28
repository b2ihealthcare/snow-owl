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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

/**
 * A {@link CDOCommitInfoHandler commit info handler} that synchronously writes {@link CDOCommitInfo commit infos} to a
 * binary log.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class BinaryCommitInfoLog implements CDOCommitInfoHandler
{
  private CDODataOutput out;

  public BinaryCommitInfoLog(OutputStream stream, CDOPackageRegistry packageRegistry)
  {
    ExtendedDataOutput eod = ExtendedDataOutputStream.wrap(stream);
    out = CDOCommonUtil.createCDODataOutput(eod, packageRegistry, CDOIDProvider.NOOP);
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    try
    {
      out.writeCDOCommitInfo(commitInfo);
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
