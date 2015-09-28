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
import java.util.ArrayList;
import java.util.List;

/**
 * A default {@link IContainerEvent container event} implementation.
 * 
 * @author Eike Stepper
 * @noextend This class is not intended to be subclassed by clients.
 * @apiviz.exclude
 */
public class ContainerEvent<E> extends Event implements IContainerEvent<E>
{
  private static final long serialVersionUID = 1L;

  private List<IContainerDelta<E>> deltas;

  public ContainerEvent(IContainer<E> container)
  {
    super(container);
    deltas = new ArrayList<IContainerDelta<E>>();
  }

  public ContainerEvent(IContainer<E> container, List<IContainerDelta<E>> deltas)
  {
    super(container);
    this.deltas = deltas;
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
    return deltas.isEmpty();
  }

  @SuppressWarnings("unchecked")
  public IContainerDelta<E>[] getDeltas()
  {
    return deltas.toArray(new IContainerDelta[deltas.size()]);
  }

  public IContainerDelta<E> getDelta() throws IllegalStateException
  {
    if (deltas.size() != 1)
    {
      throw new IllegalStateException("deltas.size() != 1"); //$NON-NLS-1$
    }

    return deltas.get(0);
  }

  public E getDeltaElement() throws IllegalStateException
  {
    return getDelta().getElement();
  }

  public IContainerDelta.Kind getDeltaKind() throws IllegalStateException
  {
    return getDelta().getKind();
  }

  public void addDelta(E element, IContainerDelta.Kind kind)
  {
    addDelta(new ContainerDelta<E>(element, kind));
  }

  public void addDelta(IContainerDelta<E> delta)
  {
    deltas.add(delta);
  }

  public void accept(IContainerEventVisitor<E> visitor)
  {
    for (IContainerDelta<E> delta : deltas)
    {
      E element = delta.getElement();

      boolean filtered = true;
      if (visitor instanceof Filtered<?>)
      {
        filtered = ((Filtered<E>)visitor).filter(element);
      }

      if (filtered)
      {
        switch (delta.getKind())
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
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    for (IContainerDelta<E> delta : getDeltas())
    {
      builder.append(", "); //$NON-NLS-1$
      builder.append(delta.getKind());
      builder.append("="); //$NON-NLS-1$
      builder.append(delta.getElement());
    }

    return MessageFormat.format("ContainerEvent[source={0}{1}]", getSource(), builder.toString()); //$NON-NLS-1$
  }
}
