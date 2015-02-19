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
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.db.mapping.ITypeMapping.Descriptor;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * Abstract implementation for {@link ITypeMapping.Factory}. Implementors should implement their custom
 * {@link #create(String)} method and construct the factory using their custom descriptor. Subclasses must have a
 * default constructor!
 * 
 * @author Stefan Winkler
 * @since 4.0
 */
public abstract class AbstractTypeMappingFactory extends Factory implements
    org.eclipse.emf.cdo.server.db.mapping.ITypeMapping.Factory
{
  private ITypeMapping.Descriptor descriptor;

  public AbstractTypeMappingFactory(Descriptor descriptor)
  {
    super(PRODUCT_GROUP, descriptor.getFactoryType());
    this.descriptor = descriptor;
  }

  public abstract ITypeMapping create(String description) throws ProductCreationException;

  public final Descriptor getDescriptor()
  {
    return descriptor;
  }
}
