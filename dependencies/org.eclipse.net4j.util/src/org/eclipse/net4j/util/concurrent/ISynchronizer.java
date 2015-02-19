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
package org.eclipse.net4j.util.concurrent;

/**
 * Synchronizes a producer and a consumer thread letting the producer pass a value to the consumer. Both producer and
 * consumer must have access to this {@link ISynchronizer} and there must only ever exist one consumer for it. Once the
 * result value is consumed by the consumer this {@link ISynchronizer} must not be reused.
 * <p>
 * 
 * @author Eike Stepper
 */
public interface ISynchronizer<RESULT>
{
  public RESULT get(long timeout);

  public void put(RESULT result);

  public boolean put(RESULT result, long timeout);
}
