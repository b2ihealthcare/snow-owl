/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.internal.cdo.query;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.AbstractQueryIterator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * @author Simon McDuff
 */
public class CDOQueryResultIteratorImpl<T> extends AbstractQueryIterator<T>
{
  private Map<CDOID, CDOObject> detachedObjects;

  public CDOQueryResultIteratorImpl(CDOView view, CDOQueryInfo queryInfo)
  {
    super(view, queryInfo);
  }

  @Override
  public void close()
  {
    detachedObjects = null;
    super.close();
  }

  @Override
  public T next()
  {
    return adapt(super.next());
  }

  @SuppressWarnings("unchecked")
  protected T adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      CDOID id = (CDOID)object;
      if (id.isNull())
      {
        return null;
      }

      CDOView view = getView();

      try
      {
        CDOObject cdoObject = view.getObject(id, true);
        return (T)CDOUtil.getEObject(cdoObject);
      }
      catch (ObjectNotFoundException ex)
      {
        if (view instanceof CDOTransaction)
        {
          if (detachedObjects == null)
          {
            CDOTransaction transaction = (CDOTransaction)view;
            detachedObjects = transaction.getDetachedObjects();
          }

          CDOObject cdoObject = detachedObjects.get(id);
          return (T)CDOUtil.getEObject(cdoObject);
        }

        return null;
      }
    }

    // Support a query return value of Object[]
    if (object instanceof Object[])
    {
      Object[] objects = (Object[])object;
      Object[] resolvedObjects = new Object[objects.length];
      for (int i = 0; i < objects.length; i++)
      {
        if (objects[i] instanceof CDOID)
        {
          resolvedObjects[i] = adapt(objects[i]);
        }
        else
        {
          resolvedObjects[i] = objects[i];
        }
      }

      return (T)resolvedObjects;
    }

    return (T)object;
  }

  @Override
  public List<T> asList()
  {
    List<Object> result = new ArrayList<Object>();
    while (super.hasNext())
    {
      result.add(super.next());
    }

    return new QueryResultList(result);
  }

  /**
   * @author Simon McDuff
   */
  private class QueryResultList implements EList<T>
  {
    private List<Object> objects;

    public QueryResultList(List<Object> objects)
    {
      this.objects = objects;
    }

    public boolean add(T o)
    {
      throw new UnsupportedOperationException();
    }

    public void add(int index, T element)
    {
      throw new UnsupportedOperationException();
    }

    public T get(int index)
    {
      return adapt(objects.get(index));
    }

    public boolean isEmpty()
    {
      return objects.isEmpty();
    }

    public Iterator<T> iterator()
    {
      return new ECDOIDIterator(this.objects.iterator());
    }

    public void move(int newPosition, T object)
    {
      throw new UnsupportedOperationException();
    }

    public T move(int newPosition, int oldPosition)
    {
      throw new UnsupportedOperationException();
    }

    public boolean addAll(Collection<? extends T> arg0)
    {
      throw new UnsupportedOperationException();
    }

    public boolean addAll(int arg0, Collection<? extends T> arg1)
    {
      throw new UnsupportedOperationException();
    }

    public void clear()
    {
      throw new UnsupportedOperationException();
    }

    public boolean contains(Object arg0)
    {
      throw new UnsupportedOperationException();
    }

    public boolean containsAll(Collection<?> arg0)
    {
      throw new UnsupportedOperationException();
    }

    public int indexOf(Object arg0)
    {
      throw new UnsupportedOperationException();
    }

    public int lastIndexOf(Object arg0)
    {
      throw new UnsupportedOperationException();
    }

    public ListIterator<T> listIterator()
    {
      throw new UnsupportedOperationException();
    }

    public ListIterator<T> listIterator(int arg0)
    {
      throw new UnsupportedOperationException();
    }

    public boolean remove(Object arg0)
    {
      throw new UnsupportedOperationException();
    }

    public T remove(int arg0)
    {
      throw new UnsupportedOperationException();
    }

    public boolean removeAll(Collection<?> arg0)
    {
      throw new UnsupportedOperationException();
    }

    public boolean retainAll(Collection<?> arg0)
    {
      throw new UnsupportedOperationException();
    }

    public T set(int arg0, T arg1)
    {
      throw new UnsupportedOperationException();
    }

    public int size()
    {
      return objects.size();
    }

    public List<T> subList(int arg0, int arg1)
    {
      throw new UnsupportedOperationException();
    }

    public Object[] toArray()
    {
      Object array[] = new Object[size()];
      return toArray(array);
    }

    @SuppressWarnings("unchecked")
    public <E> E[] toArray(E[] input)
    {
      int size = size();
      if (input.length < size)
      {
        input = (E[])Array.newInstance(input.getClass(), size);
      }

      // TODO It will be more efficient to load all objects at once.
      for (int i = 0; i < size; i++)
      {
        input[i] = (E)get(i);
      }

      if (input.length > size)
      {
        input[size] = null;
      }

      return input;
    }

    @Override
    public String toString()
    {
      return objects.toString();
    }

    /**
     * @author Simon McDuff
     */
    private class ECDOIDIterator implements Iterator<T>
    {
      private Iterator<Object> iterator;

      public ECDOIDIterator(Iterator<Object> iterator)
      {
        this.iterator = iterator;
      }

      public boolean hasNext()
      {
        return iterator.hasNext();
      }

      public T next()
      {
        return adapt(iterator.next());
      }

      public void remove()
      {
        throw new UnsupportedOperationException();
      }
    }
  }
}
