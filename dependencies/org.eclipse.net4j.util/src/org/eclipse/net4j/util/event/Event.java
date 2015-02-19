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

import java.text.MessageFormat;
import java.util.EventObject;

/**
 * A default implementation of an {@link IEvent event}.
 * 
 * @author Eike Stepper
 */
public class Event extends EventObject implements IEvent
{
  private static final long serialVersionUID = 1L;

  public Event(INotifier notifier)
  {
    super(notifier);
  }

  @Override
  public INotifier getSource()
  {
    return (INotifier)source;
  }

  @Override
  public String toString()
  {
    String params = formatAdditionalParameters();
    params = params == null ? "" : ", " + params;
    return MessageFormat.format("{0}[source={1}{2}]", getClass().getSimpleName(), getSource(), params);
  }

  /**
   * @since 3.0
   */
  protected String formatAdditionalParameters()
  {
    return null;
  }
}
