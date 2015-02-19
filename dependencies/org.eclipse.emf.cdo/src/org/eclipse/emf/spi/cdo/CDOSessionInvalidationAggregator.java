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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.session.CDOSessionInvalidationEvent;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;

/**
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOSessionInvalidationAggregator
{
  private CDOSession session;

  private IListener sessionListener = new IListener()
  {
    public void notifyEvent(IEvent event)
    {
      try
      {
        if (event instanceof CDOSessionInvalidationEvent)
        {
          CDOSessionInvalidationEvent e = (CDOSessionInvalidationEvent)event;
          handleEvent(e);
        }
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  };

  private CDOChangeSetData changeSetData;

  public CDOSessionInvalidationAggregator(CDOSession session)
  {
    this.session = session;
    session.addListener(sessionListener);
  }

  public void dispose()
  {
    reset();
    session.removeListener(sessionListener);
    session = null;
  }

  public CDOSession getSession()
  {
    return session;
  }

  public CDOChangeSetData getChangeSetData()
  {
    return changeSetData;
  }

  public void reset()
  {
    changeSetData = null;
  }

  protected void handleEvent(CDOSessionInvalidationEvent event) throws Exception
  {
    CDOChangeSetData copy = event.copy();
    if (changeSetData == null)
    {
      changeSetData = copy;
    }
    else
    {
      changeSetData.merge(copy);
    }
  }
}
