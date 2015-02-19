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
 * Thrown from {@link FactoryDescriptor#createFactory()} if a {@link IFactory factory} could not be created.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class FactoryCreationException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public FactoryCreationException()
  {
  }

  public FactoryCreationException(String message)
  {
    super(message);
  }

  public FactoryCreationException(Throwable cause)
  {
    super(cause);
  }

  public FactoryCreationException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
