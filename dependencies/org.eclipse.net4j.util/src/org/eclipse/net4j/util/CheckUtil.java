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
 * Provides static methods that check object states and invocation arguments.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public final class CheckUtil
{
  private CheckUtil()
  {
  }

  public static void checkNull(Object handle, String msg) throws NullPointerException
  {
    if (handle == null)
    {
      throw new NullPointerException(msg);
    }
  }

  public static void checkArg(boolean expr, String msg) throws IllegalArgumentException
  {
    if (!expr)
    {
      throw new IllegalArgumentException(msg);
    }
  }

  public static void checkArg(Object handle, String handleName) throws IllegalArgumentException
  {
    if (handle == null)
    {
      throw new IllegalArgumentException(handleName + " is null"); //$NON-NLS-1$
    }
  }

  public static void checkState(boolean expr, String msg) throws IllegalStateException
  {
    if (!expr)
    {
      throw new IllegalStateException(msg);
    }
  }

  public static void checkState(Object handle, String handleName) throws IllegalStateException
  {
    if (handle == null)
    {
      throw new IllegalStateException(handleName + " is null"); //$NON-NLS-1$
    }
  }
}
