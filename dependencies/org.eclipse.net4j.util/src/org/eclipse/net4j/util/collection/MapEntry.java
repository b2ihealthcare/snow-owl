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
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.ObjectUtil;

import java.text.MessageFormat;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class MapEntry<K, V> implements Map.Entry<K, V>
{
  private K key;

  private V value;

  public MapEntry(K key, V value)
  {
    this.key = key;
    this.value = value;
  }

  public MapEntry(Map.Entry<K, V> entry)
  {
    key = entry.getKey();
    value = entry.getValue();
  }

  public K getKey()
  {
    return key;
  }

  public V getValue()
  {
    return value;
  }

  public V setValue(V value)
  {
    V oldValue = this.value;
    this.value = value;
    return oldValue;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof Map.Entry<?, ?>)
    {
      Map.Entry<?, ?> entry = (Map.Entry<?, ?>)obj;
      return ObjectUtil.equals(key, entry.getKey()) && ObjectUtil.equals(value, entry.getValue());
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(key) ^ ObjectUtil.hashCode(value);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0}={1}", key, value); //$NON-NLS-1$
  }
}
