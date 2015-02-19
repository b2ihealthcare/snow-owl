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
import org.eclipse.net4j.util.io.ExtendedDataInputStream;

/**
 * Represents the receiver side of a one-way {@link Signal signal}, i.e., one with no response.
 *
 * @author Eike Stepper
 */
public abstract class Indication extends SignalReactor
{
  /**
   * @since 2.0
   */
  public Indication(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public Indication(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public Indication(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  @Override
  protected void execute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    doInput(in);
  }

  @Override
  void doExtendedInput(ExtendedDataInputStream in) throws Exception
  {
    indicating(in);
  }

  protected abstract void indicating(ExtendedDataInputStream in) throws Exception;
}
