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
package org.eclipse.net4j.channel;

/**
 * Thrown by an {@link IChannel} to indicate channel management problems.
 * 
 * @see IChannelMultiplexer
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 */
public class ChannelException extends RuntimeException
{
  private static final long serialVersionUID = 1L;

  public ChannelException()
  {
  }

  public ChannelException(String message)
  {
    super(message);
  }

  public ChannelException(Throwable cause)
  {
    super(cause);
  }

  public ChannelException(String message, Throwable cause)
  {
    super(message, cause);
  }
}
