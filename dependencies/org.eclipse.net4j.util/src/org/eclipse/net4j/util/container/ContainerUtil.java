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
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;
import org.eclipse.net4j.util.concurrent.TimerLifecycle;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.security.FileUserManagerFactory;
import org.eclipse.net4j.util.security.RandomizerFactory;

/**
 * Various static helper methods for dealing with {@link IContainer containers}.
 * 
 * @author Eike Stepper
 */
public final class ContainerUtil
{
  private static final Object[] NO_ELEMENTS = {};

  private static final IContainer<Object> EMPTY = new IContainer<Object>()
  {
    public Object[] getElements()
    {
      return NO_ELEMENTS;
    }

    public boolean isEmpty()
    {
      return true;
    }

    public void addListener(IListener listener)
    {
    }

    public void removeListener(IListener listener)
    {
    }

    public IListener[] getListeners()
    {
      return EventUtil.NO_LISTENERS;
    }

    public boolean hasListeners()
    {
      return false;
    }

    @Override
    public String toString()
    {
      return "EMPTY_CONTAINER"; //$NON-NLS-1$
    }
  };

  private ContainerUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new TimerLifecycle.DaemonFactory());
    container.registerFactory(new ExecutorServiceFactory());
    container.registerFactory(new RandomizerFactory());
    container.registerFactory(new FileUserManagerFactory());
  }

  public static IContainer<Object> emptyContainer()
  {
    return EMPTY;
  }

  public static IManagedContainer createContainer()
  {
    return new ManagedContainer();
  }

  public static boolean isEmpty(Object container)
  {
    if (container instanceof IContainer<?>)
    {
      return ((IContainer<?>)container).isEmpty();
    }

    return true;
  }

  public static Object[] getElements(Object container)
  {
    if (container instanceof IContainer<?>)
    {
      return ((IContainer<?>)container).getElements();
    }

    return NO_ELEMENTS;
  }
}
