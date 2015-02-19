/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Victor Roldan Betancort - initial API and implementation
 *   Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.AbstractCDOViewProvider;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;
import org.eclipse.emf.cdo.view.CDOViewSet;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.Container;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * When instanced in Eclipse, it's populated with contributions from the viewProvider Extension Point. A default
 * CDOViewProvider implementation is registered, regardless of the execution environment.
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 * @see CDOViewProvider
 */
public class CDOViewProviderRegistryImpl extends Container<CDOViewProvider> implements CDOViewProviderRegistry
{
  public static final CDOViewProviderRegistryImpl INSTANCE = new CDOViewProviderRegistryImpl();

  private static final String EXT_POINT = "viewProviders"; //$NON-NLS-1$

  private List<CDOViewProvider> viewProviders = new ArrayList<CDOViewProvider>();

  public CDOViewProviderRegistryImpl()
  {
    addViewProvider(PluginContainerViewProvider.INSTANCE);
  }

  public CDOView provideView(URI uri, ResourceSet resourceSet)
  {
    Pair<CDOView, CDOViewProvider> pair = provideViewWithInfo(uri, resourceSet);
    if (pair == null)
    {
      return null;
    }

    return pair.getElement1();
  }

  public Pair<CDOView, CDOViewProvider> provideViewWithInfo(URI uri, ResourceSet resourceSet)
  {
    if (uri == null)
    {
      return null;
    }

    CDOViewSet viewSet = CDOUtil.getViewSet(resourceSet);
    if (viewSet != null)
    {
      try
      {
        String uuid = CDOURIUtil.extractRepositoryUUID(uri);
        CDOView view = viewSet.resolveView(uuid);
        if (view != null)
        {
          return new Pair<CDOView, CDOViewProvider>(view, null);
        }
      }
      catch (Exception ignore)
      {
        // Do nothing
      }
    }

    for (CDOViewProvider viewProvider : getViewProviders(uri))
    {
      CDOView view = viewProvider.getView(uri, resourceSet);
      if (view != null)
      {
        return new Pair<CDOView, CDOViewProvider>(view, viewProvider);
      }
    }

    return null;
  }

  public CDOViewProvider[] getViewProviders(URI uri)
  {
    List<CDOViewProvider> result = new ArrayList<CDOViewProvider>();
    for (CDOViewProvider viewProvider : viewProviders)
    {
      if (viewProvider.matchesRegex(uri))
      {
        result.add(viewProvider);
      }
    }

    // Sort highest priority first
    Collections.sort(result, new Comparator<CDOViewProvider>()
    {
      public int compare(CDOViewProvider o1, CDOViewProvider o2)
      {
        return -Integer.valueOf(o1.getPriority()).compareTo(o2.getPriority());
      }
    });

    return result.toArray(new CDOViewProvider[result.size()]);
  }

  public boolean hasViewProvider(CDOViewProvider viewProvider)
  {
    synchronized (viewProviders)
    {
      return viewProviders.contains(viewProvider);
    }
  }

  public void addViewProvider(CDOViewProvider viewProvider)
  {
    boolean added;
    synchronized (viewProviders)
    {
      added = !viewProviders.contains(viewProvider);
      if (added)
      {
        viewProviders.add(viewProvider);
      }
    }

    if (added)
    {
      fireElementAddedEvent(viewProvider);
    }
  }

  public void removeViewProvider(CDOViewProvider viewProvider)
  {
    boolean removed;
    synchronized (viewProviders)
    {
      removed = viewProviders.remove(viewProvider);
    }

    if (removed)
    {
      fireElementRemovedEvent(viewProvider);
    }
  }

  public CDOViewProvider[] getElements()
  {
    synchronized (viewProviders)
    {
      return viewProviders.toArray(new CDOViewProvider[viewProviders.size()]);
    }
  }

  @Override
  public boolean isEmpty()
  {
    synchronized (viewProviders)
    {
      return viewProviders.isEmpty();
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    if (OMPlatform.INSTANCE.isOSGiRunning())
    {
      try
      {
        readExtensions();
      }
      catch (Throwable t)
      {
        OM.LOG.error(t);
      }
    }
  }

  public void readExtensions()
  {
    IExtensionRegistry registry = Platform.getExtensionRegistry();
    IConfigurationElement[] configurationElements = registry.getConfigurationElementsFor(OM.BUNDLE_ID, EXT_POINT);
    for (IConfigurationElement element : configurationElements)
    {
      try
      {
        CDOViewProviderDescriptor descriptor = new CDOViewProviderDescriptor(element);
        addViewProvider(descriptor);
      }
      catch (Exception ex)
      {
        OM.LOG.error(ex);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class CDOViewProviderDescriptor extends AbstractCDOViewProvider
  {
    private IConfigurationElement element;

    public CDOViewProviderDescriptor(IConfigurationElement element)
    {
      super(element.getAttribute("regex"), Integer.parseInt(element.getAttribute("priority"))); //$NON-NLS-1$ //$NON-NLS-2$
      this.element = element;

      if (StringUtil.isEmpty(element.getAttribute("class"))) //$NON-NLS-1$
      {
        throw new IllegalArgumentException(MessageFormat.format(
            Messages.getString("CDOViewProviderRegistryImpl.4"), element)); //$NON-NLS-1$
      }

      if (StringUtil.isEmpty(element.getAttribute("regex"))) //$NON-NLS-1$
      {
        throw new IllegalArgumentException(MessageFormat.format(
            Messages.getString("CDOViewProviderRegistryImpl.6"), element)); //$NON-NLS-1$
      }
    }

    public CDOView getView(URI uri, ResourceSet resourceSet)
    {
      return getViewProvider().getView(uri, resourceSet);
    }

    private CDOViewProvider getViewProvider()
    {
      try
      {
        return (CDOViewProvider)element.createExecutableExtension("class"); //$NON-NLS-1$
      }
      catch (CoreException ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }
}
