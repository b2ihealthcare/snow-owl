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
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.view.CDOViewSetImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

/**
 * @author Eike Stepper
 */
public final class SessionUtil
{
  private static final boolean ROOT_RESOURCE_EXCLUSION_CHECK = false;

  private SessionUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static InternalCDOViewSet prepareResourceSet(ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = null;
    synchronized (resourceSet)
    {
      if (ROOT_RESOURCE_EXCLUSION_CHECK)
      {
        addRootResourceExclusionCheckAdapter(resourceSet);
      }

      viewSet = (InternalCDOViewSet)CDOUtil.getViewSet(resourceSet);
      if (viewSet == null)
      {
        viewSet = new CDOViewSetImpl();
        resourceSet.eAdapters().add(viewSet);
      }
    }

    return viewSet;
  }

  private static void addRootResourceExclusionCheckAdapter(ResourceSet resourceSet)
  {
    class RootResourceExclusionCheckAdapter extends AdapterImpl
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        if (msg.getEventType() == Notification.ADD || msg.getEventType() == Notification.ADD_MANY)
        {
          Object newValue = msg.getNewValue();
          check(newValue);
        }
      }

      @Override
      public void setTarget(Notifier newTarget)
      {
        check(newTarget);
      }

      private void check(Object object)
      {
        if (object instanceof CDOResource && ((CDOResource)object).isRoot())
        {
          throw new AssertionError("Root resource in resource set not allowed");
        }
      }
    }

    resourceSet.eAdapters().add(new RootResourceExclusionCheckAdapter());
  }
}
