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
package org.eclipse.net4j.util.security;

import org.eclipse.net4j.util.container.IElementProcessor;
import org.eclipse.net4j.util.container.IManagedContainer;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class ResponseNegotiatorInjector implements IElementProcessor
{
  private INegotiator negotiator;

  public ResponseNegotiatorInjector(INegotiator negotiator)
  {
    this.negotiator = negotiator;
  }

  public INegotiator getNegotiator()
  {
    return negotiator;
  }

  public Object process(IManagedContainer container, String productGroup, String factoryType, String description,
      Object element)
  {
    if (element instanceof INegotiatorAware)
    {
      INegotiatorAware negotiatorAware = (INegotiatorAware)element;
      if (negotiatorAware.getNegotiator() == null)
      {
        if (filterElement(productGroup, factoryType, description, negotiatorAware))
        {
          if (negotiator != null)
          {
            negotiatorAware.setNegotiator(negotiator);
          }
        }
      }
    }

    return element;
  }

  protected abstract boolean filterElement(String productGroup, String factoryType, String description,
      INegotiatorAware negotiatorAware);
}
