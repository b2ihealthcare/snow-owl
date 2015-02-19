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
package org.eclipse.internal.net4j.buffer;

import org.eclipse.net4j.Net4jUtil;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.Factory;

/**
 * @author Eike Stepper
 */
public class BufferProviderFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.bufferProviders"; //$NON-NLS-1$

  public static final String TYPE = "default"; //$NON-NLS-1$

  public static final short BUFFER_CAPACITY = 4096;

  public BufferProviderFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public IBufferProvider create(String description)
  {
    return Net4jUtil.createBufferPool(BUFFER_CAPACITY);
  }

  public static IBufferProvider get(IManagedContainer container)
  {
    return (IBufferProvider)container.getElement(PRODUCT_GROUP, TYPE, null);
  }
}
