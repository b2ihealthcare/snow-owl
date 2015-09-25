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
package org.eclipse.net4j.util.cache;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.concurrent.Worker;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * @author Eike Stepper
 */
public abstract class Cache<E> extends Worker implements ICache
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, Cache.class);

  private ICacheMonitor cacheMonitor;

  private ICacheProbe cacheProbe;

  private ReferenceQueue<E> referenceQueue = new ReferenceQueue<E>();

  public Cache()
  {
  }

  public ICacheMonitor getCacheMonitor()
  {
    return cacheMonitor;
  }

  public void setCacheMonitor(ICacheMonitor cacheMonitor)
  {
    this.cacheMonitor = cacheMonitor;
  }

  protected ICacheProbe getCacheProbe()
  {
    return cacheProbe;
  }

  protected ReferenceQueue<E> getReferenceQueue()
  {
    return referenceQueue;
  }

  @Override
  protected void doBeforeActivate() throws Exception
  {
    super.doBeforeActivate();
    if (cacheMonitor == null)
    {
      throw new IllegalStateException("cacheMonitor == null"); //$NON-NLS-1$
    }
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    cacheProbe = cacheMonitor.registerCache(this);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    cacheMonitor.deregisterCache(this);
    cacheProbe = null;
    super.doDeactivate();
  }

  @Override
  protected void work(WorkContext context) throws Exception
  {
    Reference<? extends E> reference = referenceQueue.remove(200);
    if (reference != null)
    {
      unreachableElement(reference);
    }
  }

  protected void unreachableElement(Reference<? extends E> reference)
  {
    E element = reference.get();
    if (element != null)
    {
      unreachableElement(element);
    }
  }

  protected void unreachableElement(E element)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Unreachable: " + element); //$NON-NLS-1$
    }
  }
}
