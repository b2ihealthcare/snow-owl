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
package org.eclipse.net4j.util.ref;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class KeyedWeakReference<K, T> extends WeakReference<T> implements KeyedReference<K, T>
{
  private K key;

  public KeyedWeakReference(K key, T ref)
  {
    super(ref);
    this.key = key;
  }

  public KeyedWeakReference(K key, T ref, ReferenceQueue<T> queue)
  {
    super(ref, queue);
    this.key = key;
  }

  public ReferenceType getType()
  {
    return ReferenceType.WEAK;
  }

  public K getKey()
  {
    return key;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("KeyedWeakReference[{0} -> {1}]", key, isEnqueued() ? "ENQUEUED" : get()); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
