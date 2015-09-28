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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoHandler;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.net4j.util.concurrent.QueueWorker;

/**
 * A {@link CDOCommitInfoHandler commit info handler} that asynchronously delegates {@link CDOCommitInfo commit infos}
 * to another handler.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class AsyncCommitInfoHandler extends QueueWorker<CDOCommitInfo> implements CDOCommitInfoHandler
{
  private CDOCommitInfoHandler delegate;

  public AsyncCommitInfoHandler(CDOCommitInfoHandler delegate)
  {
    this.delegate = delegate;
  }

  public void handleCommitInfo(CDOCommitInfo commitInfo)
  {
    addWork(commitInfo);
  }

  @Override
  protected void work(WorkContext context, CDOCommitInfo commitInfo)
  {
    try
    {
      delegate.handleCommitInfo(commitInfo);
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  @Override
  protected boolean doRemainingWorkBeforeDeactivate()
  {
    return true;
  }
}
