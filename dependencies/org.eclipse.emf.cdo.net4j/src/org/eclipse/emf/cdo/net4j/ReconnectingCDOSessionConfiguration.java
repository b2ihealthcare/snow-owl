/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.net4j;

/**
 * A {@link RecoveringCDOSessionConfiguration session configuration} that recovers from network problems by attempting
 * to reconnect to the same repository in specific intervals.
 * 
 * @author Caspar De Groot
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ReconnectingCDOSessionConfiguration extends RecoveringCDOSessionConfiguration
{
  public long getReconnectInterval();

  public void setReconnectInterval(long interval);

  public int getMaxReconnectAttempts();

  public void setMaxReconnectAttempts(int attempts);
}
