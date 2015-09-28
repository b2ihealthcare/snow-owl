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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;

/**
 * An unchecked exception that indicates the absence of a {@link IFactory factory} identified by
 * {@link IFactoryKey#getProductGroup() product group} and {@link IFactoryKey#getType() type}.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class FactoryNotFoundException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public FactoryNotFoundException()
  {
  }

  public FactoryNotFoundException(String message)
  {
    super(message);
  }

  public FactoryNotFoundException(Throwable cause)
  {
    super(cause);
  }

  public FactoryNotFoundException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
