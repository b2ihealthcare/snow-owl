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

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class DynamicArray<E>
{
  private Object[] elements = {};

  public DynamicArray()
  {
  }

  public int add(E element)
  {
    int length = elements.length;
    for (int i = 0; i < length; i++)
    {
      if (elements[i] == null)
      {
        elements[i] = element;
        return i;
      }
    }

    grow(length);
    elements[length] = element;
    return length;
  }

  @SuppressWarnings("unchecked")
  public E add(int index, E element)
  {
    grow(index);
    Object old = elements[index];
    elements[index] = element;
    return (E)old;
  }

  @SuppressWarnings("unchecked")
  public E remove(int index)
  {
    Object old = elements[index];
    if (old != null)
    {
      elements[index] = null;
      shrink();
    }

    return (E)old;
  }

  @SuppressWarnings("unchecked")
  public E get(int index)
  {
    return (E)elements[index];
  }

  private void grow(int index)
  {
    if (index >= elements.length)
    {
      Object[] newChannels = new Object[index + 1];
      System.arraycopy(elements, 0, newChannels, 0, elements.length);
      elements = newChannels;
    }
  }

  private void shrink()
  {
    boolean shrink = false;
    int lastIndex = elements.length - 1;
    while (lastIndex > 0 && (shrink = elements[lastIndex] == null))
    {
      --lastIndex;
    }

    if (shrink)
    {
      int newLength = lastIndex + 1;
      Object[] newChannels = new Object[newLength];
      System.arraycopy(elements, 0, newChannels, 0, newLength);
      elements = newChannels;
    }
  }
}
