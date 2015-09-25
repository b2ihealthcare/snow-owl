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
package org.eclipse.net4j.util;

/**
 * Should not be used anymore in favour of {@link AssertionError}. Likely to be deprecated soon.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class ImplementationError extends Error
{
  private static final long serialVersionUID = 1L;

  public ImplementationError()
  {
  }

  public ImplementationError(String message)
  {
    super(message);
  }

  public ImplementationError(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ImplementationError(Throwable cause)
  {
    super(cause);
  }
}
