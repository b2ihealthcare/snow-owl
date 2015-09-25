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
package org.eclipse.net4j.util.io;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public abstract class CachedFileMap<K extends Comparable<K>, V> extends SortedFileMap<K, V>
{
  private Map<K, V> cache = new HashMap<K, V>();

  public CachedFileMap(File file, String mode)
  {
    super(file, mode);
  }

  @Override
  public V get(K key)
  {
    V value = cache.get(key);
    if (value == null)
    {
      value = super.get(key);
      cache.put(key, value);
    }

    return value;
  }

  @Override
  public V put(K key, V value)
  {
    cache.put(key, value);
    return super.put(key, value);
  }
}
