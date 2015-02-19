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
package org.eclipse.emf.cdo.server;

/**
 * An unchecked exception that is thrown from concurrent commit operations if cycles in the tree containmnent structure
 * would result.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class ContainmentCycleDetectedException extends IllegalStateException
{
  private static final long serialVersionUID = 1L;

  public ContainmentCycleDetectedException()
  {
  }

  public ContainmentCycleDetectedException(String message, Throwable cause)
  {
    super(message, cause);
  }

  public ContainmentCycleDetectedException(String s)
  {
    super(s);
  }

  public ContainmentCycleDetectedException(Throwable cause)
  {
    super(cause);
  }
}
