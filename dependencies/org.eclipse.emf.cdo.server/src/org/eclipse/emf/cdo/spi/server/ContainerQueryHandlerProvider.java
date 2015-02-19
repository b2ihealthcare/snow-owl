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

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IQueryHandlerProvider;

import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class ContainerQueryHandlerProvider implements IQueryHandlerProvider
{
  private IManagedContainer container;

  public ContainerQueryHandlerProvider(IManagedContainer container)
  {
    this.container = container;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  /**
   * @since 3.0
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info)
  {
    return (IQueryHandler)container.getElement(QueryHandlerFactory.PRODUCT_GROUP, info.getQueryLanguage(), null);
  }
}
