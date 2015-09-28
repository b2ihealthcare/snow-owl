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
package org.eclipse.net4j.util.container;

/**
 * A default {@link IContainerDelta container delta} implementation.
 * 
 * @author Eike Stepper
 * @apiviz.exclude
 */
public class ContainerDelta<E> implements IContainerDelta<E>
{
  private E element;

  private Kind kind;

  public ContainerDelta(E element, Kind kind)
  {
    this.element = element;
    this.kind = kind;
  }

  public E getElement()
  {
    return element;
  }

  public E setValue(E value)
  {
    throw new UnsupportedOperationException();
  }

  public Kind getKind()
  {
    return kind;
  }
}
