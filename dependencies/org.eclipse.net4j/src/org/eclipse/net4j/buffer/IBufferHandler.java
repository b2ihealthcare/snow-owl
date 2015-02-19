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
package org.eclipse.net4j.buffer;

/**
 * Provides clients with the ability to pass {@link IBuffer}s in for further buffer handling.
 * 
 * @author Eike Stepper
 */
public interface IBufferHandler
{
  /**
   * Handles an {@link IBuffer} and optionally releases it. The implementor of this method takes over the ownership of
   * the buffer. Care must be taken to properly {@link IBuffer#release() release} the buffer if the ownership is not
   * explicitely passed to some further party.
   * 
   * @param buffer
   *          The buffer to be handled and otionally released.
   */
  public void handleBuffer(IBuffer buffer);
}
