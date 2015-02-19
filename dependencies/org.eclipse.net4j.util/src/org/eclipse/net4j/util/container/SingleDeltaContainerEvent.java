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

import org.eclipse.net4j.util.container.IContainerEventVisitor.Filtered;
import org.eclipse.net4j.util.event.Event;

import java.text.MessageFormat;

/**
 * A {@link IContainerEvent container event} with a single element {@link IContainerDelta delta}.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class SingleDeltaContainerEvent<E> extends Event implements IContainerEvent<E>
{
  private static final long serialVersionUID = 1L;

  private IContainerDelta<E>[] deltas;

  @SuppressWarnings("unchecked")
  public SingleDeltaContainerEvent(IContainer<E> container, E element, IContainerDelta.Kind kind)
  {
    super(container);
    deltas = new IContainerDelta[] { new ContainerDelta<E>(element, kind) };
  }

  /**
   * @since 3.0
   */
  @Override
  @SuppressWarnings("unchecked")
  public IContainer<E> getSource()
  {
    return (IContainer<E>)super.getSource();
  }

  public boolean isEmpty()
  {
    return false;
  }

  public IContainerDelta<E>[] getDeltas()
  {
    return deltas;
  }

  public IContainerDelta<E> getDelta() throws IllegalStateException
  {
    return deltas[0];
  }

  public E getDeltaElement() throws IllegalStateException
  {
    return deltas[0].getElement();
  }

  public IContainerDelta.Kind getDeltaKind() throws IllegalStateException
  {
    return deltas[0].getKind();
  }

  public void accept(IContainerEventVisitor<E> visitor)
  {
    E element = deltas[0].getElement();

    boolean filtered = true;
    if (visitor instanceof Filtered<?>)
    {
      filtered = ((Filtered<E>)visitor).filter(element);
    }

    if (filtered)
    {
      switch (deltas[0].getKind())
      {
      case ADDED:
        visitor.added(element);
        break;
      case REMOVED:
        visitor.removed(element);
        break;
      }
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("ContainerEvent[source={0}, {1}={2}]", getSource(), getDeltaElement(), getDeltaKind()); //$NON-NLS-1$
  }
}
