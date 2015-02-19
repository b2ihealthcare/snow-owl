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
package org.eclipse.net4j;

import org.eclipse.net4j.protocol.IProtocol;
import org.eclipse.net4j.protocol.IProtocolProvider;
import org.eclipse.net4j.util.concurrent.NonBlockingLongCounter;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.spi.net4j.ClientProtocolFactory;
import org.eclipse.spi.net4j.ServerProtocolFactory;

/**
 * Base class for container-based {@link IProtocolProvider protocol providers} like {@link Client} or {@link Server}.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ContainerProtocolProvider implements IProtocolProvider
{
  private static NonBlockingLongCounter counter = new NonBlockingLongCounter();

  private IManagedContainer container;

  private String productGroup;

  protected ContainerProtocolProvider(IManagedContainer container, String productGroup)
  {
    this.container = container;
    this.productGroup = productGroup;
  }

  public IManagedContainer getContainer()
  {
    return container;
  }

  public String getProductGroup()
  {
    return productGroup;
  }

  public IProtocol<?> getProtocol(String type)
  {
    return (IProtocol<?>)container.getElement(productGroup, type, "protocol-" + counter.increment(), false); //$NON-NLS-1$
  }

  /**
   * Container-based {@link IProtocolProvider protocol provider} for {@link ILocationAware.Location#CLIENT client}
   * protocols.
   * 
   * @author Eike Stepper
   */
  public static class Client extends ContainerProtocolProvider
  {
    public Client(IManagedContainer container)
    {
      super(container, ClientProtocolFactory.PRODUCT_GROUP);
    }
  }

  /**
   * Container-based {@link IProtocolProvider protocol provider} for {@link ILocationAware.Location#SERVER server}
   * protocols.
   * 
   * @author Eike Stepper
   */
  public static class Server extends ContainerProtocolProvider
  {
    public Server(IManagedContainer container)
    {
      super(container, ServerProtocolFactory.PRODUCT_GROUP);
    }
  }
}
