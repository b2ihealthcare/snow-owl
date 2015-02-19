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

import org.eclipse.net4j.acceptor.IAcceptor;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

import org.eclipse.internal.net4j.buffer.BufferProviderFactory;

import org.eclipse.spi.net4j.InternalAcceptor;
import org.eclipse.spi.net4j.InternalConnector;

import java.util.concurrent.ExecutorService;

/**
 * An element post processor that injects a {@link ITransportConfig transport configuration} into the {@link IConnector
 * connectors} and {@link IAcceptor acceptors} of a {@link IManagedContainer managed container}.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public class TransportInjector implements IElementProcessor
{
  public TransportInjector()
  {
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof InternalAcceptor)
    {
      InternalAcceptor acceptor = (InternalAcceptor)element;
      processAcceptor(container, factoryType, description, acceptor);
    }
    else if (element instanceof InternalConnector)
    {
      InternalConnector connector = (InternalConnector)element;
      processConnector(container, factoryType, description, connector);
    }

    return element;
  }

  protected void processAcceptor(IManagedContainer container, String factoryType, String description,
      InternalAcceptor acceptor)
  {
    ITransportConfig config = acceptor.getConfig();
    if (config.getBufferProvider() == null)
    {
      config.setBufferProvider(getBufferProvider(container));
    }

    if (config.getReceiveExecutor() == null)
    {
      config.setReceiveExecutor(getExecutorService(container));
    }

    if (config.getProtocolProvider() == null)
    {
      config.setProtocolProvider(new ContainerProtocolProvider.Server(container));
    }
  }

  protected void processConnector(IManagedContainer container, String factoryType, String description,
      InternalConnector connector)
  {
    ITransportConfig config = connector.getConfig();
    if (config.getBufferProvider() == null)
    {
      config.setBufferProvider(getBufferProvider(container));
    }

    if (config.getReceiveExecutor() == null)
    {
      config.setReceiveExecutor(getExecutorService(container));
    }

    if (config.getProtocolProvider() == null)
    {
      config.setProtocolProvider(new ContainerProtocolProvider.Client(container));
    }
  }

  /**
   * @since 2.0
   */
  protected IBufferProvider getBufferProvider(IManagedContainer container)
  {
    return (IBufferProvider)container.getElement(BufferProviderFactory.PRODUCT_GROUP, BufferProviderFactory.TYPE, null);
  }

  protected ExecutorService getExecutorService(IManagedContainer container)
  {
    return (ExecutorService)container.getElement(ExecutorServiceFactory.PRODUCT_GROUP, ExecutorServiceFactory.TYPE,
        null);
  }
}
