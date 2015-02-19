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

import java.lang.ref.Reference;

/**
 * @see Reference
 * @author Eike Stepper
 */
public interface KeyedReference<K, T>
{
  public ReferenceType getType();

  public K getKey();

  /**
   * @see Reference#get()
   */
  public T get();

  /**
   * @see Reference#clear()
   */
  public void clear();

  /**
   * @see Reference#isEnqueued()
   */
  public boolean isEnqueued();

  /**
   * @see Reference#enqueue()
   */
  public boolean enqueue();
}
