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

import org.eclipse.net4j.util.ObjectUtil;

/**
 * A {@link INotifier notifier} with an {@link #getID() ID} and a single {@link #getValue() value}.
 * <p>
 * A value notifier can fire the following events:
 * <ul>
 * <li> {@link ValueEvent} after value changes.
 * </ul>
 * 
 * @author Eike Stepper
 * @since 3.1
 * @apiviz.has {@link java.lang.Object} oneway - - value
 * @apiviz.uses {@link ValueEvent} - - fires
 */
public class ValueNotifier<VALUE> extends Notifier
{
  private String id;

  private VALUE value;

  public ValueNotifier()
  {
    this(null, null);
  }

  public ValueNotifier(VALUE value)
  {
    this(null, value);
  }

  public ValueNotifier(String id)
  {
    this(id, null);
  }

  public ValueNotifier(String id, VALUE value)
  {
    this.id = id;
    this.value = value;
  }

  public String getID()
  {
    return id;
  }

  public VALUE getValue()
  {
    return value;
  }

  public synchronized void setValue(VALUE value)
  {
    VALUE oldValue = this.value;
    if (!ObjectUtil.equals(value, oldValue))
    {
      this.value = value;
      fireEvent(new ValueEvent<VALUE>(this, oldValue, value));
    }
  }
}
