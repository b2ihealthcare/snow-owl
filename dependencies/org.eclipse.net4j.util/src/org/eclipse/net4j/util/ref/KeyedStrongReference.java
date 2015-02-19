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

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class KeyedStrongReference<K, T> implements KeyedReference<K, T>
{
  private K key;

  private T ref;

  public KeyedStrongReference(K key, T ref)
  {
    this.key = key;
    this.ref = ref;
  }

  public ReferenceType getType()
  {
    return ReferenceType.STRONG;
  }

  public K getKey()
  {
    return key;
  }

  public T get()
  {
    return ref;
  }

  public void clear()
  {
    ref = null;
  }

  public boolean isEnqueued()
  {
    return false;
  }

  public boolean enqueue()
  {
    return false;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("KeyedStrongReference[{0} -> {1}]", key, ref); //$NON-NLS-1$
  }
}
