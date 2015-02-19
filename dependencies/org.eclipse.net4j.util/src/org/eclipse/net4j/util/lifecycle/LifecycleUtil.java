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
package org.eclipse.net4j.util.lifecycle;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.om.log.OMLogger.Level;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Various static helper methods for dealing with {@link ILifecycle lifecycles}.
 *
 * @author Eike Stepper
 */
public final class LifecycleUtil
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_LIFECYCLE, LifecycleUtil.class);

  private LifecycleUtil()
  {
  }

  /**
   * @since 3.2
   */
  public static boolean isDeferredActivation(Object object)
  {
    if (object instanceof ILifecycle.DeferrableActivation)
    {
      return ((ILifecycle.DeferrableActivation)object).isDeferredActivation();
    }

    return false;
  }

  /**
   * @since 3.0
   */
  public static LifecycleState getLifecycleState(Object object)
  {
    if (object instanceof ILifecycle)
    {
      return ((ILifecycle)object).getLifecycleState();
    }

    return LifecycleState.ACTIVE;
  }

  public static boolean isActive(Object object)
  {
    if (object instanceof ILifecycle)
    {
      return ((ILifecycle)object).isActive();
    }

    return object != null;
  }

  /**
   * @since 2.0
   */
  public static void checkActive(Object object) throws IllegalStateException
  {
    if (!isActive(object))
    {
      throw new IllegalStateException("Not active: " + object); //$NON-NLS-1$
    }
  }

  /**
   * @since 2.0
   */
  public static void checkInactive(Object object) throws IllegalStateException
  {
    if (isActive(object))
    {
      throw new IllegalStateException("Not inactive: " + object); //$NON-NLS-1$
    }
  }

  public static void activate(Object object) throws LifecycleException
  {
    activate(object, false);
  }

  /**
   * @see Activator
   */
  public static void activate(Object object, boolean useAnnotation) throws LifecycleException
  {
    if (object instanceof ILifecycle)
    {
      ((ILifecycle)object).activate();
    }
    else if (object != null && useAnnotation)
    {
      invokeAnnotation(object, Activator.class);
    }
  }

  public static Exception activateSilent(Object object)
  {
    return activateSilent(object, false);
  }

  /**
   * @see Activator
   */
  public static Exception activateSilent(Object object, boolean useAnnotation)
  {
    try
    {
      activate(object, useAnnotation);
      return null;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public static boolean waitForActive(Object object, long millis)
  {
    return waitFor(object, millis, LifecycleState.ACTIVE);
  }

  /**
   * @since 3.1
   */
  public static boolean waitForInactive(Object object, long millis)
  {
    return waitFor(object, millis, LifecycleState.INACTIVE);
  }

  /**
   * @since 3.1
   */
  public static boolean waitFor(Object object, long millis, final LifecycleState state)
  {
    try
    {
      if (object instanceof ILifecycle)
      {
        ILifecycle lifecycle = (ILifecycle)object;
        if (lifecycle.getLifecycleState() == state)
        {
          return true;
        }

        final CountDownLatch latch = new CountDownLatch(1);
        IListener adapter = new IListener()
        {
          public void notifyEvent(IEvent event)
          {
            if (event instanceof ILifecycleEvent)
            {
              ILifecycleEvent e = (ILifecycleEvent)event;
              if (e.getSource().getLifecycleState() == state)
              {
                latch.countDown();
              }
            }
          }
        };

        try
        {
          lifecycle.addListener(adapter);
          latch.await(millis, TimeUnit.MILLISECONDS);
        }
        finally
        {
          lifecycle.removeListener(adapter);
        }

        return lifecycle.getLifecycleState() == state;
      }

      return true;
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  public static Exception deactivate(Object object)
  {
    return deactivate(object, false);
  }

  /**
   * @see Deactivator
   */
  public static Exception deactivate(Object object, boolean useAnnotation)
  {
    if (object instanceof ILifecycle)
    {
      return ((ILifecycle)object).deactivate();
    }
    else if (object != null && useAnnotation)
    {
      // TODO Handle evtl. return value (exception)
      invokeAnnotation(object, Deactivator.class);
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static void deactivate(Object object, Level logLevel)
  {
    Exception exception = deactivate(object);
    if (exception != null)
    {
      OM.LOG.log(logLevel, "Problem while deactivating " + object, exception); //$NON-NLS-1$
    }
  }

  public static void deactivateNoisy(Object object) throws LifecycleException
  {
    deactivateNoisy(object, false);
  }

  public static void deactivateNoisy(Object object, boolean useAnnotation) throws LifecycleException
  {
    Exception ex = deactivate(object, useAnnotation);
    if (ex instanceof RuntimeException)
    {
      throw (RuntimeException)ex;
    }
    else if (ex != null)
    {
      throw new LifecycleException(ex);
    }
  }

  /**
   * @since 2.0
   */
  public static <T> T delegateLifecycle(ClassLoader loader, T pojo, Class<?> pojoInterface, ILifecycle delegate)
  {
    return Delegator.newProxy(loader, pojo, pojoInterface, delegate);
  }

  /**
   * @since 2.0
   */
  public static <T> T delegateLifecycle(ClassLoader loader, T pojo, ILifecycle delegate)
  {
    return Delegator.newProxy(loader, pojo, pojo.getClass(), delegate);
  }

  private static <T extends Annotation> void invokeAnnotation(Object object, Class<T> annotationClass)
  {
    Class<?> c = object.getClass();
    while (c != Object.class)
    {
      final Method[] methods = c.getDeclaredMethods();
      for (Method method : methods)
      {
        if (method.getParameterTypes().length == 0)
        {
          Annotation annotation = method.getAnnotation(annotationClass);
          if (annotation != null)
          {
            invokeMethod(object, method);
            boolean propagate = annotationClass == Activator.class ? ((Activator)annotation).propagate()
                : ((Deactivator)annotation).propagate();
            if (!propagate)
            {
              break;
            }
          }
        }
      }

      c = c.getSuperclass();
    }
  }

  private static Object invokeMethod(Object object, Method method)
  {
    try
    {
      return method.invoke(object, (Object[])null);
    }
    catch (IllegalAccessException iae)
    {
      try
      {
        method.setAccessible(true);
        return method.invoke(object, (Object[])null);
      }
      catch (Exception ex)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace(ex);
        }
      }
    }
    catch (Exception ex)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace(ex);
      }
    }

    return null;
  }

  /**
   * Annotates a method of a POJO class that's supposed to be called to <em>activate</em> a POJO object during
   * {@link LifecycleUtil#activate(Object)}.
   *
   * @author Eike Stepper
   * @apiviz.exclude
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Activator
  {
    boolean propagate() default true;
  }

  /**
   * Annotates a method of a POJO class that's supposed to be called to <em>deactivate</em> a POJO object during
   * {@link LifecycleUtil#deactivate(Object)}.
   *
   * @author Eike Stepper
   * @apiviz.exclude
   */
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Deactivator
  {
    boolean propagate() default true;
  }

  /**
   * The {@link InvocationHandler invocation handler} of the {@link Proxy dynamic proxy} created in
   * {@link LifecycleUtil#delegateLifecycle(ClassLoader, Object, ILifecycle) LifecycleUtil.delegateLifecycle()}.
   *
   * @author Eike Stepper
   * @since 2.0
   * @apiviz.exclude
   */
  public static final class Delegator<T> implements InvocationHandler
  {
    private static final Class<ILifecycle> INTERFACE = ILifecycle.class;

    private T pojo;

    private ILifecycle delegate;

    public Delegator(T pojo, ILifecycle delegate)
    {
      this.pojo = pojo;
      this.delegate = delegate;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      String name = method.getName();
      if (name.equals("activate")) //$NON-NLS-1$
      {
        delegate.activate();
        return null;
      }

      if (name.equals("deactivate")) //$NON-NLS-1$
      {
        return delegate.deactivate();
      }

      if (name.equals("isActive")) //$NON-NLS-1$
      {
        return delegate.isActive();
      }

      if (name.equals("getLifecycleState")) //$NON-NLS-1$
      {
        return delegate.getLifecycleState();
      }

      if (name.equals("getListeners")) //$NON-NLS-1$
      {
        return delegate.getListeners();
      }

      if (name.equals("hasListeners")) //$NON-NLS-1$
      {
        return delegate.hasListeners();
      }

      if (name.equals("addListener")) //$NON-NLS-1$
      {
        delegate.addListener((IListener)args[0]);
        return null;
      }

      if (name.equals("removeListener")) //$NON-NLS-1$
      {
        delegate.removeListener((IListener)args[0]);
        return null;
      }

      try
      {
        return method.invoke(pojo, args);
      }
      catch (Exception ex)
      {
        throw ex;
      }
    }

    public static <T> T newProxy(ClassLoader loader, T pojo, Class<?> pojoInterface, ILifecycle delegate)
    {
      if (pojo == null)
      {
        return pojo;
      }

      Delegator<T> h = new Delegator<T>(pojo, delegate);
      final Class<?>[] interfaces = { pojoInterface, INTERFACE };

      @SuppressWarnings("unchecked")
      T proxy = (T)Proxy.newProxyInstance(loader, interfaces, h);
      return proxy;
    }
  }
}
