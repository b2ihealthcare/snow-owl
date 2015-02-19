/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.event.INotifier;

import java.util.Collection;

/**
 * Contains a number of elements and notifies about element addition and removal.
 * <p>
 * A container can fire the following events:
 * <ul>
 * <li> {@link IContainerEvent} after the addition and/or removal of elements.
 * </ul>
 *
 * @author Eike Stepper
 * @apiviz.landmark
 * @apiviz.composedOf {@link java.lang.Object} - - elements
 * @apiviz.uses {@link IContainerEvent} - - fires
 */
public interface IContainer<E> extends INotifier
{
  public boolean isEmpty();

  public E[] getElements();

  /**
   * A {@link IContainer container} with additional methods to add or remove elements.
   *
   * @author Eike Stepper
   * @since 2.0
   */
  public interface Modifiable<E> extends IContainer<E>
  {
    public boolean addElement(E element);

    public boolean addAllElements(Collection<E> elements);

    public boolean removeElement(E element);

    public boolean removeAllElements(Collection<E> elements);
  }
}
