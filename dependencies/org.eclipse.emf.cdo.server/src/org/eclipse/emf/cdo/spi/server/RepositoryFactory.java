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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryFactory;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class RepositoryFactory implements IRepositoryFactory
{
  public static final String TYPE = "default"; //$NON-NLS-1$

  public RepositoryFactory()
  {
  }

  public String getRepositoryType()
  {
    return TYPE;
  }

  public IRepository createRepository()
  {
    return new Repository.Default();
  }

  public static IRepository get(IManagedContainer container, String name)
  {
    return (IRepository)container.getElement(PRODUCT_GROUP, TYPE, name);
  }
}
