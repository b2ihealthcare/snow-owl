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
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IRepositoryProvider;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * @author Eike Stepper
 */
public class CDOServerProtocolFactory extends ServerProtocolFactory
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  private IRepositoryProvider repositoryProvider;

  public CDOServerProtocolFactory(IRepositoryProvider repositoryProvider)
  {
    super(TYPE);
    this.repositoryProvider = repositoryProvider;
  }

  public IRepositoryProvider getRepositoryProvider()
  {
    return repositoryProvider;
  }

  public CDOServerProtocol create(String description)
  {
    return new CDOServerProtocol(repositoryProvider);
  }

  public static CDOServerProtocol get(IManagedContainer container, String description)
  {
    return (CDOServerProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
