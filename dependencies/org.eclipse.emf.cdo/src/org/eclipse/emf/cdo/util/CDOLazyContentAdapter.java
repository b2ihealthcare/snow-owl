/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

/**
 * A scalable {@link EContentAdapter content adapter} that uses CDO mechansims to attach itself to {@link CDOObject
 * objects} when they are lazily loaded.
 * 
 * @author Victor Roldan Betancort
 * @since 4.0
 */
public class CDOLazyContentAdapter extends EContentAdapter
{
  private CDOObjectHandler handler = new CleanObjectHandler();

  private Set<WeakReference<CDOObject>> adaptedObjects = new HashSet<WeakReference<CDOObject>>();

  /**
   * The root object to be adapted.
   */
  private WeakReference<CDOObject> adaptedRoot;

  @Override
  protected void setTarget(EObject target)
  {
    if (isConnectedObject(target))
    {
      if (adaptedRoot == null)
      {
        adaptedRoot = new WeakReference<CDOObject>(CDOUtil.getCDOObject(target));
      }

      basicSetTarget(target);
      if (target instanceof Resource)
      {
        addCleanObjectHandler(target);
      }
    }
    else
    {
      super.setTarget(target);
    }
  }

  /**
   * EContentAdapter removes adapter from all contained EObjects. In this case, we remove this adapter from all lazily
   * loaded objects
   */
  @Override
  protected void unsetTarget(EObject target)
  {
    if (isConnectedObject(target))
    {
      basicUnsetTarget(target);
      if (target instanceof Resource)
      {
        InternalCDOView view = getCDOView(target);
        if (view != null)
        {
          // Remove adapter from all adapted objects
          for (WeakReference<CDOObject> weakReference : adaptedObjects)
          {
            CDOObject object = weakReference.get();
            if (object != null)
            {
              removeAdapter(object);
            }
          }
        }

        target.eAdapters().remove(this);
        removeCleanObjectHandler(target);
      }
    }
    else
    {
      super.unsetTarget(target);
    }
  }

  private void addCleanObjectHandler(EObject target)
  {
    InternalCDOView view = getCDOView(target);
    if (view != null)
    {
      CDOObjectHandler[] handlers = view.getObjectHandlers();
      for (CDOObjectHandler handler : handlers)
      {
        if (handler.equals(this.handler))
        {
          return;
        }
      }

      view.addObjectHandler(handler);

      // Adapt already loaded objects
      for (CDOObject cdoObject : view.getObjects().values())
      {
        if (isContained(cdoObject))
        {
          addAdapter(cdoObject);
        }
      }
    }
  }

  private void removeCleanObjectHandler(EObject target)
  {
    InternalCDOView view = getCDOView(target);
    if (view != null)
    {
      CDOObjectHandler[] handlers = view.getObjectHandlers();
      for (CDOObjectHandler handler : handlers)
      {
        if (handler.equals(this.handler))
        {
          view.removeObjectHandler(handler);
          break;
        }
      }
    }
  }

  @Override
  protected void addAdapter(Notifier notifier)
  {
    if (isConnectedObject(notifier) && !isAlreadyAdapted(notifier))
    {
      adaptedObjects.add(new WeakReference<CDOObject>(CDOUtil.getCDOObject((EObject)notifier)));
    }

    super.addAdapter(notifier);
  }

  private boolean isAlreadyAdapted(Notifier notifier)
  {
    return notifier.eAdapters().contains(this);
  }

  private static InternalCDOView getCDOView(EObject target)
  {
    CDOObject object = CDOUtil.getCDOObject(target);
    if (object != null)
    {
      return (InternalCDOView)object.cdoView();
    }

    return null;
  }

  private static boolean isConnectedObject(Notifier target)
  {
    if (target instanceof EObject)
    {
      CDOObject object = CDOUtil.getCDOObject((EObject)target);
      if (object != null)
      {
        return !FSMUtil.isTransient(object);
      }
    }

    return false;
  }

  /**
   * Checks if the argument is contained in the object graph of the root element
   */
  private boolean isContained(CDOObject object)
  {
    if (adaptedRoot == null)
    {
      return false;
    }

    CDOObject root = adaptedRoot.get();
    if (object == null)
    {
      return false;
    }

    if (root instanceof Resource)
    {
      return root == (object instanceof Resource ? object : object.cdoResource());
    }

    return EcoreUtil.isAncestor(root, object);
  }

  /**
   * @author Victor Roldan Betancort
   */
  private final class CleanObjectHandler implements CDOObjectHandler
  {
    public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
    {
      if (newState == CDOState.CLEAN || newState == CDOState.NEW)
      {
        if (isConnectedObject(object) && !isAlreadyAdapted(object) && isContained(object))
        {
          addAdapter(object);
        }
      }
    }
  }
}
