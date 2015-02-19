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
 * Provides a single static {@link #adapt(Object, Class) adapt()} method that conveniently and safely wraps the
 * Platform's adaptation framework.
 * 
 * @author Eike Stepper
 */
public final class AdapterUtil
{
  private AdapterUtil()
  {
  }

  public static <TYPE> TYPE adapt(Object object, Class<TYPE> type)
  {
    if (object == null)
    {
      return null;
    }

    Object adapter = null;
    if (type.isInstance(object))
    {
      adapter = object;
    }
    else
    {
      try
      {
        adapter = AdaptableHelper.adapt(object, type);
        if (adapter == null)
        {
          adapter = AdapterManagerHelper.adapt(object, type);
        }
      }
      catch (Throwable ignore)
      {
      }
    }

    @SuppressWarnings("unchecked")
    TYPE result = (TYPE)adapter;
    return result;
  }

  /**
   * Nested class to factor out dependencies on org.eclipse.core.runtime
   * 
   * @author Eike Stepper
   */
  private static final class AdaptableHelper
  {
    public static Object adapt(Object object, Class<?> type)
    {
      if (object instanceof org.eclipse.core.runtime.IAdaptable)
      {
        return ((org.eclipse.core.runtime.IAdaptable)object).getAdapter(type);
      }

      return null;
    }
  }

  /**
   * Nested class to factor out dependencies on org.eclipse.core.runtime
   * 
   * @author Eike Stepper
   */
  private static final class AdapterManagerHelper
  {
    private static org.eclipse.core.runtime.IAdapterManager adapterManager = org.eclipse.core.runtime.Platform
        .getAdapterManager();

    public static Object adapt(Object object, Class<?> type)
    {
      if (adapterManager != null)
      {
        return adapterManager.getAdapter(object, type);
      }

      return null;
    }
  }
}
