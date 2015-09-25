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
 * A callback interface for visiting {@link IContainerDelta container deltas}.
 * 
 * @see IContainerEvent#accept(IContainerEventVisitor)
 * @author Eike Stepper
 * @apiviz.exclude
 */
public interface IContainerEventVisitor<E>
{
  public void added(E element);

  public void removed(E element);

  /**
   * An extension interface for {@link IContainerEventVisitor container event visitors} that can {@link #filter(Object)
   * filter} deltas from being visited.
   * 
   * @see IContainerEvent#accept(IContainerEventVisitor)
   * @author Eike Stepper
   * @apiviz.exclude
   */
  public interface Filtered<E> extends IContainerEventVisitor<E>
  {
    public boolean filter(E element);
  }
}
