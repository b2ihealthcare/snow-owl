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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;

import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ClientProtocolFactory;

/**
 * @author Eike Stepper
 */
public final class CDOClientProtocolFactory extends ClientProtocolFactory
{
  public static final String TYPE = CDOProtocolConstants.PROTOCOL_NAME;

  public CDOClientProtocolFactory()
  {
    super(TYPE);
  }

  public CDOClientProtocol create(String description)
  {
    return new CDOClientProtocol();
  }

  public static CDOClientProtocol get(IManagedContainer container, String description)
  {
    return (CDOClientProtocol)container.getElement(PRODUCT_GROUP, TYPE, description);
  }
}
