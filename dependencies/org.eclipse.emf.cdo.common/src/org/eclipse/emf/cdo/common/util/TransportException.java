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
 * An unchecked exception that indicates transport-level problems.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 */
public class TransportException extends CDOException
{
  private static final long serialVersionUID = 1L;

  public TransportException()
  {
  }

  public TransportException(String message)
  {
    super(message);
  }

  public TransportException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public TransportException(Throwable cause)
  {
    super(cause);
  }
}
