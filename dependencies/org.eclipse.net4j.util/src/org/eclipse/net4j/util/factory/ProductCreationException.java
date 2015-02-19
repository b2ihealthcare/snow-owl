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
package org.eclipse.net4j.util.factory;

/**
 * An unchecked exception that may be thrown from {@link IFactory factories} to indicate the inability to create a
 * product.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class ProductCreationException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ProductCreationException()
  {
  }

  public ProductCreationException(String message)
  {
    super(message);
  }

  public ProductCreationException(Throwable cause)
  {
    super(cause);
  }

  public ProductCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
