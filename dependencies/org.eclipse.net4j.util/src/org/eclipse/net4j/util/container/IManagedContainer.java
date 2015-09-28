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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.registry.IRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * A {@link IContainer container} that populates itself by means of element {@link #getFactoryRegistry() factories} and
 * {@link #getPostProcessors() post processors} .
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.composedOf {@link org.eclipse.net4j.util.factory.IFactory} - - factoryRegistry
 * @apiviz.composedOf {@link org.eclipse.net4j.util.container.IElementProcessor} - - postProcessors
 */
public interface IManagedContainer extends IContainer<Object>, ILifecycle
{
  public IRegistry<IFactoryKey, IFactory> getFactoryRegistry();

  public IManagedContainer registerFactory(IFactory factory);

  public List<IElementProcessor> getPostProcessors();

  public void addPostProcessor(IElementProcessor postProcessor, boolean processExistingElements);

  public void addPostProcessor(IElementProcessor postProcessor);

  public void removePostProcessor(IElementProcessor postProcessor);

  public Set<String> getProductGroups();

  public Set<String> getFactoryTypes(String productGroup);

  public IFactory getFactory(String productGroup, String factoryType) throws FactoryNotFoundException;

  public Object putElement(String productGroup, String factoryType, String description, Object element);

  public String[] getElementKey(Object element);

  public Object[] getElements(String productGroup);

  public Object[] getElements(String productGroup, String factoryType);

  public Object getElement(String productGroup, String factoryType, String description)
      throws FactoryNotFoundException, ProductCreationException;

  /**
   * @since 2.0
   */
  public Object getElement(String productGroup, String factoryType, String description, boolean activate)
      throws FactoryNotFoundException, ProductCreationException;

  public Object removeElement(String productGroup, String factoryType, String description);

  public void clearElements();

  public void loadElements(InputStream stream) throws IOException, FactoryNotFoundException, ProductCreationException;

  public void saveElements(OutputStream stream) throws IOException;
}
