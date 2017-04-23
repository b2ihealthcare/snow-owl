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
package org.eclipse.net4j.signal.wrapping;

import org.eclipse.net4j.signal.SignalProtocol;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.io.IStreamWrapper;

/**
 * An {@link IElementProcessor element post processor} that injects a {@link #getStreamWrapper() stream wrapper}
 * into {@link SignalProtocol signal protocol} instances.
 *
 * @author Eike Stepper
 */
public class StreamWrapperInjector implements IElementProcessor
{
  private final String protocolID;

  private final IStreamWrapper streamWrapper;

  public StreamWrapperInjector(String protocolID, IStreamWrapper streamWrapper)
  {
    this.protocolID = protocolID;
    this.streamWrapper = streamWrapper;
  }

  public String getProtocolID()
  {
    return protocolID;
  }

  public IStreamWrapper getStreamWrapper()
  {
    return streamWrapper;
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof SignalProtocol<?>)
    {
      SignalProtocol<?> signalProtocol = (SignalProtocol<?>)element;
      if (shouldInject(container, productGroup, factoryType, description, signalProtocol))
      {
        element = inject(container, productGroup, factoryType, description, signalProtocol);
      }
    }

    return element;
  }

  protected boolean shouldInject(IManagedContainer container, String productGroup, String factoryType,
      String description, SignalProtocol<?> signalProtocol)
  {
    if (signalProtocol.getStreamWrapper() == streamWrapper)
    {
      return false;
    }

    return StringUtil.isEmpty(protocolID) || ObjectUtil.equals(signalProtocol.getType(), protocolID);
  }

  protected Object inject(IManagedContainer container, String productGroup, String factoryType, String description,
      SignalProtocol<?> signalProtocol)
  {
    signalProtocol.addStreamWrapper(streamWrapper);
    return signalProtocol;
  }
}
