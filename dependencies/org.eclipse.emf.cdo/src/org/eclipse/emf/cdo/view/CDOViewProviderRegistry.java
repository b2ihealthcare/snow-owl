/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.net4j.util.container.IContainer;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

/**
 * A global registry of {@link CDOViewProvider view provider} implementations.
 * 
 * @author Victor Roldan Betancort
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.composedOf {@link org.eclipse.emf.cdo.view.CDOViewProvider}
 */
public interface CDOViewProviderRegistry extends IContainer<CDOViewProvider>
{
  public static final CDOViewProviderRegistry INSTANCE = org.eclipse.emf.internal.cdo.view.CDOViewProviderRegistryImpl.INSTANCE;

  /**
   * Returns a {@link CDOView view} that serves the given URI in the given {@link CDOViewSet view set}, or
   * <code>null</code> if no {@link CDOViewProvider view provider} in this registry can provide such a view.
   */
  public CDOView provideView(URI uri, ResourceSet viewSet);

  /**
   * Returns a {@link CDOView view} that serves the given URI in the given {@link CDOViewSet view set}, or
   * <code>null</code> if no {@link CDOViewProvider view provider} in this registry can provide such a view. The
   * returned {@link Pair pair} contains the provider that provided the view or null if the view was already present in
   * the {@link CDOViewSet view set} of the resource set.
   * 
   * @since 4.0
   */
  public Pair<CDOView, CDOViewProvider> provideViewWithInfo(URI uri, ResourceSet resourceSet);

  /**
   * Returns an array of <code>CDOViewProvider</code> instances, determined and ordered by certain criteria based on the
   * argument URI.
   */
  public CDOViewProvider[] getViewProviders(URI uri);

  /**
   * Returns <code>true</code> if the given view provider instance is registered with this registry, <code>false</code>
   * otherwise.
   * 
   * @since 4.0
   */
  public boolean hasViewProvider(CDOViewProvider viewProvider);

  /**
   * Registers the given view provider instance with this registry.
   */
  public void addViewProvider(CDOViewProvider viewProvider);

  /**
   * Removes the given view provider instance from this registry.
   */
  public void removeViewProvider(CDOViewProvider viewProvider);
}
