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

import org.eclipse.net4j.util.container.IContainerDelta.Kind;
import org.eclipse.net4j.util.event.IEvent;

/**
 * An {@link IEvent event} fired from a {@link IContainer container} when its elements have changed.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.composedOf {@link IContainerDelta} - - deltas
 */
public interface IContainerEvent<E> extends IEvent
{
  /**
   * @since 3.0
   */
  public IContainer<E> getSource();

  public boolean isEmpty();

  public IContainerDelta<E>[] getDeltas();

  public IContainerDelta<E> getDelta() throws IllegalStateException;

  public E getDeltaElement() throws IllegalStateException;

  public Kind getDeltaKind() throws IllegalStateException;

  public void accept(IContainerEventVisitor<E> visitor);
}
