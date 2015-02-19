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
package org.eclipse.emf.cdo.common.util;

/**
 * An unchecked {@link SecurityException security exception} indicating that a user has canceled an attempt to authenticate himself.
 *
 * @author Eike Stepper
 * @since 4.1
 */
public class NotAuthenticatedException extends SecurityException
{
  private static final long serialVersionUID = 1L;

  public NotAuthenticatedException()
  {
  }

  public NotAuthenticatedException(String s)
  {
    super(s);
  }

  public NotAuthenticatedException(Throwable cause)
  {
    super(cause);
  }

  public NotAuthenticatedException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
