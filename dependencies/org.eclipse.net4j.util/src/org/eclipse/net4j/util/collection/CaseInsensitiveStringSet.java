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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 * @author Eike Stepper
 * @since 3.2
 */
public class CaseInsensitiveStringSet extends HashSet<String>
{
  private static final long serialVersionUID = 1L;

  public CaseInsensitiveStringSet()
  {
  }

  public CaseInsensitiveStringSet(Collection<? extends String> c)
  {
    super(c);
  }

  public CaseInsensitiveStringSet(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public CaseInsensitiveStringSet(int initialCapacity)
  {
    super(initialCapacity);
  }

  public boolean isLowerCase()
  {
    return true;
  }

  @Override
  public boolean contains(Object o)
  {
    return super.contains(convert(o));
  }

  @Override
  public boolean add(String e)
  {
    return super.add(convert(e));
  }

  @Override
  public boolean remove(Object o)
  {
    return super.remove(convert(o));
  }

  @Override
  public boolean removeAll(Collection<?> c)
  {
    return super.removeAll(convert(c));
  }

  @Override
  public boolean containsAll(Collection<?> c)
  {
    return super.containsAll(convert(c));
  }

  @Override
  public boolean addAll(Collection<? extends String> c)
  {
    boolean modified = false;
    Iterator<? extends String> e = c.iterator();
    while (e.hasNext())
    {
      if (add(convert(e.next())))
      {
        modified = true;
      }
    }

    return modified;
  }

  @Override
  public boolean retainAll(Collection<?> c)
  {
    return super.retainAll(convert(c));
  }

  protected String convert(Object o)
  {
    if (o instanceof String)
    {
      if (isLowerCase())
      {
        return ((String)o).toLowerCase();
      }

      return ((String)o).toUpperCase();
    }

    return null;
  }

  protected Collection<?> convert(Collection<?> c)
  {
    Collection<Object> list = new ArrayList<Object>();
    for (Iterator<?> it = c.iterator(); it.hasNext();)
    {
      Object o = it.next();
      list.add(convert(o));
    }

    return list;
  }
}
