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
 * An unchecked exception for general CDO purposes.
 * 
 * @author Eike Stepper
 */
public class CDOException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public CDOException()
  {
  }

  public CDOException(String message)
  {
    super(message);
  }

  public CDOException(Throwable cause)
  {
    super(cause);
  }

  public CDOException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
