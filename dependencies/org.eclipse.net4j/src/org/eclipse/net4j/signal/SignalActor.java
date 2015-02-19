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

import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.BufferOutputStream;

/**
 * Represents the sender side of a {@link Signal signal}.
 *
 * @author Eike Stepper
 */
public abstract class SignalActor extends Signal
{
  /**
   * @since 2.0
   */
  public SignalActor(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  /**
   * @since 2.0
   */
  public SignalActor(SignalProtocol<?> protocol, short id)
  {
    super(protocol, id);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  /**
   * @since 2.0
   */
  public SignalActor(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
    setCorrelationID(protocol.getNextCorrelationID());
  }

  @Override
  protected final void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    doExecute(in, out);
  }

  abstract void doExecute(BufferInputStream in, BufferOutputStream out) throws Exception;

  @Override
  String getInputMeaning()
  {
    return "Confirming"; //$NON-NLS-1$
  }

  @Override
  String getOutputMeaning()
  {
    return "Requesting"; //$NON-NLS-1$
  }
}
