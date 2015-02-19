/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 *    Victor Roldan Betancort - bug 338921
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDOViewSetImpl extends NotifierImpl implements InternalCDOViewSet
{
  private Set<InternalCDOView> views = new HashSet<InternalCDOView>();

  private Map<String, InternalCDOView> mapOfViews = new HashMap<String, InternalCDOView>();

  private CDOResourceFactory resourceFactory = CDOResourceFactory.INSTANCE;

  private CDOViewSetPackageRegistryImpl packageRegistry;

  private ResourceSet resourceSet;

  private ThreadLocal<Boolean> ignoreNotifications = new InheritableThreadLocal<Boolean>();

  public CDOViewSetImpl()
  {
  }

  public ResourceSet getResourceSet()
  {
    return resourceSet;
  }

  public EPackage.Registry getPackageRegistry()
  {
    return packageRegistry;
  }

  public CDOResourceFactory getResourceFactory()
  {
    return resourceFactory;
  }

  public CDOView[] getViews()
  {
    synchronized (views)
    {
      return views.toArray(new CDOView[views.size()]);
    }
  }

  /**
   * @throws IllegalArgumentException
   *           if repositoryUUID doesn't match any CDOView.
   */
  public InternalCDOView resolveView(String repositoryUUID)
  {
    InternalCDOView view = null;
    synchronized (views)
    {
      view = mapOfViews.get(repositoryUUID);
      if (view == null)
      {
        if (repositoryUUID != null)
        {
          throw new IllegalArgumentException(MessageFormat.format(
              Messages.getString("CDOViewSetImpl.0"), repositoryUUID)); //$NON-NLS-1$
        }

        if (mapOfViews.size() == 1)
        {
          return views.iterator().next();
        }

        if (mapOfViews.size() == 0)
        {
          return null;
        }

        throw new IllegalStateException(Messages.getString("CDOViewSetImpl.1")); //$NON-NLS-1$
      }
    }

    return view;
  }

  public InternalCDOView getView(String repositoryUUID)
  {
    synchronized (views)
    {
      return mapOfViews.get(repositoryUUID);
    }
  }

  public void add(InternalCDOView view)
  {
    String repositoryUUID = view.getSession().getRepositoryInfo().getUUID();
    synchronized (views)
    {
      CDOView lookupView = mapOfViews.get(repositoryUUID);
      if (lookupView != null)
      {
        throw new RuntimeException(Messages.getString("CDOViewSetImpl.2")); //$NON-NLS-1$
      }

      views.add(view);
      mapOfViews.put(repositoryUUID, view);
    }

    if (eNotificationRequired())
    {
      NotificationImpl notification = new NotificationImpl(NotificationImpl.ADD, null, view);
      eNotify(notification);
    }
  }

  public void remove(InternalCDOView view)
  {
    String repositoryUUID = view.getSession().getRepositoryInfo().getUUID();
    List<Resource> resToRemove = new ArrayList<Resource>();
    synchronized (views)
    {
      // It is important to remove view from the list first. It is the way we can differentiate close and detach.
      views.remove(view);
      mapOfViews.remove(repositoryUUID);

      for (Resource resource : getResourceSet().getResources())
      {
        if (resource instanceof CDOResource)
        {
          CDOResource cdoRes = (CDOResource)resource;
          if (cdoRes.cdoView() == view)
          {
            resToRemove.add(resource);
          }
        }
      }
    }

    getResourceSet().getResources().removeAll(resToRemove);
    if (eNotificationRequired())
    {
      NotificationImpl notification = new NotificationImpl(NotificationImpl.REMOVE, view, null);
      eNotify(notification);
    }
  }

  public Notifier getTarget()
  {
    return resourceSet;
  }

  public void setTarget(Notifier newTarget)
  {
    if (!isAdapterForType(newTarget))
    {
      throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOViewSetImpl.3"), newTarget)); //$NON-NLS-1$
    }

    if (resourceSet != null)
    {
      throw new IllegalStateException(Messages.getString("CDOViewSetImpl.4")); //$NON-NLS-1$
    }

    resourceSet = (ResourceSet)newTarget;
    EPackage.Registry oldPackageRegistry = resourceSet.getPackageRegistry();
    packageRegistry = new CDOViewSetPackageRegistryImpl(this, oldPackageRegistry);
    resourceSet.setPackageRegistry(packageRegistry);

    Registry registry = resourceSet.getResourceFactoryRegistry();
    Map<String, Object> map = registry.getProtocolToFactoryMap();
    map.put(CDOProtocolConstants.PROTOCOL_NAME, getResourceFactory());
  }

  public boolean isAdapterForType(Object type)
  {
    return type instanceof ResourceSet;
  }

  public synchronized <V> V executeWithoutNotificationHandling(Callable<V> callable)
  {
    Boolean wasIgnore = ignoreNotifications.get();

    try
    {
      ignoreNotifications.set(true);
      return callable.call();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      if (wasIgnore == null)
      {
        ignoreNotifications.remove();
      }
    }
  }

  public void notifyChanged(Notification notification)
  {
    // The resource <-> view association is done in CDOResourceImpl.basicSetResourceSet()

    if (ignoreNotifications.get() == null)
    {
      // We need to deregister CDOResources from CDOView if removed from the ResourceSet, see bug 338921
      switch (notification.getEventType())
      {
      case Notification.REMOVE_MANY:
        deregisterResources((List<?>)notification.getOldValue());
        break;

      case Notification.REMOVE:
        deregisterResources(Collections.singleton(notification.getOldValue()));
        break;
      }
    }
  }

  private void deregisterResources(Collection<?> potentialResources)
  {
    List<CDOResource> allDirtyResources = new ArrayList<CDOResource>();

    try
    {
      Map<CDOView, List<CDOResource>> resourcesPerView = getResourcesPerView(potentialResources);

      for (Entry<CDOView, List<CDOResource>> entry : resourcesPerView.entrySet())
      {
        InternalCDOView view = (InternalCDOView)entry.getKey();
        List<CDOResource> resources = entry.getValue();

        if (view.isDirty())
        {
          List<CDOResource> dirtyResources = getDirtyResources(resources);
          if (!dirtyResources.isEmpty())
          {
            allDirtyResources.addAll(dirtyResources);
            resourceSet.getResources().addAll(resources);
            continue;
          }
        }

        for (CDOResource resource : resources)
        {
          InternalCDOObject internalResource = (InternalCDOObject)resource;
          view.deregisterObject(internalResource);
          internalResource.cdoInternalSetState(CDOState.INVALID);
        }
      }
    }
    finally
    {
      int size = allDirtyResources.size();
      if (size == 1)
      {
        throw new CDOException("Attempt to remove a dirty resource from a resource set: " + allDirtyResources.get(0));
      }
      else if (size > 1)
      {
        throw new CDOException("Attempt to remove dirty resources from a resource set: " + allDirtyResources);
      }
    }
  }

  private List<CDOResource> getDirtyResources(List<CDOResource> resources)
  {
    List<CDOResource> dirtyResources = new ArrayList<CDOResource>();
    for (CDOResource resource : resources)
    {
      switch (resource.cdoState())
      {
      case NEW:
      case DIRTY:
      case CONFLICT:
      case INVALID_CONFLICT:
        dirtyResources.addAll(resources);
      }
    }

    return dirtyResources;
  }

  private Map<CDOView, List<CDOResource>> getResourcesPerView(Collection<?> potentialResources)
  {
    Map<CDOView, List<CDOResource>> resourcesPerView = new HashMap<CDOView, List<CDOResource>>();

    for (Object potentialResource : potentialResources)
    {
      if (potentialResource instanceof CDOResource)
      {
        CDOResource resource = (CDOResource)potentialResource;
        CDOView view = resource.cdoView();

        if (views.contains(view))
        {
          List<CDOResource> resources = resourcesPerView.get(view);
          if (resources == null)
          {
            resources = new ArrayList<CDOResource>();
            resourcesPerView.put(view, resources);
          }

          resources.add(resource);
        }
      }
    }

    return resourcesPerView;
  }
}
