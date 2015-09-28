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
package org.eclipse.net4j.connector;

/**
 * Thrown by an {@link IConnector} to indicate connection problems.
 * 
 * @author Eike Stepper
 */
public class ConnectorException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ConnectorException()
  {
  }

  public ConnectorException(String message)
  {
    super(message);
  }

  public ConnectorException(Throwable cause)
  {
    super(cause);
  }

  public ConnectorException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
