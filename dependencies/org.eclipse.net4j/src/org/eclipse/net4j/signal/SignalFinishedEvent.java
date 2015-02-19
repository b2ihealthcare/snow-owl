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
package org.eclipse.net4j.signal;

import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;

/**
 * An {@link IEvent event} fired from a {@link ISignalProtocol signal protocol} when
 * the local execution of a scheduled {@link #getSignal() signal} has finished.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 */
public class SignalFinishedEvent<INFRA_STRUCTURE> extends Event
{
  private static final long serialVersionUID = 1L;

  private Signal signal;

  private Exception exception;

  SignalFinishedEvent(ISignalProtocol<INFRA_STRUCTURE> source, Signal signal, Exception exception)
  {
    super(source);
    this.signal = signal;
    this.exception = exception;
  }

  @Override
  public ISignalProtocol<INFRA_STRUCTURE> getSource()
  {
    @SuppressWarnings("unchecked")
    ISignalProtocol<INFRA_STRUCTURE> source = (ISignalProtocol<INFRA_STRUCTURE>)super.getSource();
    return source;
  }

  public Signal getSignal()
  {
    return signal;
  }

  public Exception getException()
  {
    return exception;
  }

  @Override
  protected String formatAdditionalParameters()
  {
    String result = "signal=" + signal.getClass().getSimpleName();
    if (exception != null)
    {
      result += ", exception=" + exception.getClass().getSimpleName();
    }

    return result;
  }
}
