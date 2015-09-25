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
package org.eclipse.net4j.util.registry;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class HashMapRegistry<K, V> extends Registry<K, V>
{
  private Map<K, V> map;

  public HashMapRegistry()
  {
    map = new HashMap<K, V>();
  }

  public HashMapRegistry(int initialCapacity)
  {
    map = new HashMap<K, V>(initialCapacity);
  }

  public HashMapRegistry(int initialCapacity, float loadFactor)
  {
    map = new HashMap<K, V>(initialCapacity, loadFactor);
  }

  public HashMapRegistry(Map<? extends K, ? extends V> m)
  {
    map = new HashMap<K, V>(m);
  }

  @Override
  protected Map<K, V> getMap()
  {
    return map;
  }
}
