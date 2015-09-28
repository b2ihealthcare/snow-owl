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

import org.eclipse.net4j.util.event.INotifier;

/**
 * @author Eike Stepper
 */
public interface IHistory<T> extends INotifier, Iterable<IHistoryElement<T>>
{
  public boolean isEmpty();

  public int size();

  public boolean clear();

  public int indexOf(T data);

  public boolean add(T data);

  public IHistoryElement<T> remove(int index);

  public IHistoryElement<T> get(int index);

  public T getMostRecent();

  public <D> D[] getData(D[] a);

  public IHistoryElement<T>[] toArray();
}
