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
package org.eclipse.net4j.protocol;

import org.eclipse.net4j.ILocationAware;
import org.eclipse.net4j.buffer.IBufferHandler;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.util.security.IUserAware;

import java.util.concurrent.ExecutorService;

/**
 * A {@link #getType() typed} {@link IBufferHandler buffer handler} for a {@link #getChannel() channel}.
 *
 * @author Eike Stepper
 */
public interface IProtocol<INFRA_STRUCTURE> extends IUserAware, ILocationAware, IBufferHandler
{
  public String getType();

  public IChannel getChannel();

  public void setChannel(IChannel channel);

  public INFRA_STRUCTURE getInfraStructure();

  public void setInfraStructure(INFRA_STRUCTURE infraStructure);

  public IBufferProvider getBufferProvider();

  public ExecutorService getExecutorService();

  public void setExecutorService(ExecutorService executorService);
}
