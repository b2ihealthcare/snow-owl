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

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.CDOCommonSession.Options.PassiveUpdateMode;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

/**
 * A convenience adapter for common CDO {@link IEvent events}.
 * 
 * @author Eike Stepper
 * @since 3.0
 */
public class CDOCommonEventAdapter extends ContainerEventAdapter<Object>
{
  public CDOCommonEventAdapter()
  {
  }

  @Override
  protected void notifyOtherEvent(IEvent event)
  {
    if (event instanceof CDOCommonSession.Options.PassiveUpdateEvent)
    {
      CDOCommonSession.Options.PassiveUpdateEvent e = (CDOCommonSession.Options.PassiveUpdateEvent)event;
      boolean oldEnabled = e.getOldEnabled();
      boolean newEnabled = e.getNewEnabled();
      if (oldEnabled != newEnabled)
      {
        onPassiveUpdatesEnabled(oldEnabled, newEnabled);
      }
      else
      {
        onPassiveUpdatesMode(e.getOldMode(), e.getNewMode());
      }
    }
  }

  @Override
  protected void onAdded(IContainer<Object> container, Object element)
  {
    if (element instanceof CDOCommonView)
    {
      onViewOpened((CDOCommonView)element);
    }
  }

  @Override
  protected void onRemoved(IContainer<Object> container, Object element)
  {
    if (element instanceof CDOCommonView)
    {
      onViewClosed((CDOCommonView)element);
    }
  }

  protected void onViewOpened(CDOCommonView view)
  {
  }

  protected void onViewClosed(CDOCommonView element)
  {
  }

  protected void onPassiveUpdatesEnabled(boolean oldEnabled, boolean newEnabled)
  {
  }

  protected void onPassiveUpdatesMode(PassiveUpdateMode oldMode, PassiveUpdateMode newMode)
  {
  }
}
