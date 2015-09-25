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
package org.eclipse.net4j.util.event;

/**
 * An {@link IEvent} fired from {@link ValueNotifier value notifiers} after value changes.
 * 
 * @author Eike Stepper
 * @since 3.1
 * @noextend This class is not intended to be subclassed by clients.
 */
public class ValueEvent<VALUE> extends Event
{
  private static final long serialVersionUID = 1L;

  private VALUE oldValue;

  private VALUE newValue;

  ValueEvent(ValueNotifier<VALUE> notifier, VALUE oldValue, VALUE newValue)
  {
    super(notifier);
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  @SuppressWarnings("unchecked")
  public ValueNotifier<VALUE> getSource()
  {
    return (ValueNotifier<VALUE>)super.getSource();
  }

  public VALUE getOldValue()
  {
    return oldValue;
  }

  public VALUE getNewValue()
  {
    return newValue;
  }
}
