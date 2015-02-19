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
import org.eclipse.net4j.util.io.ExtendedDataOutputStream;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Represents the sender side of a two-way {@link SignalActor signal}, i.e., one with a response.
 *
 * @author Eike Stepper
 */
public abstract class RequestWithConfirmation<RESULT> extends SignalActor
{
  private RESULT result;

  /**
   * @since 2.0
   */
  public RequestWithConfirmation(SignalProtocol<?> protocol, short id, String name)
  {
    super(protocol, id, name);
  }

  /**
   * @since 2.0
   */
  public RequestWithConfirmation(SignalProtocol<?> protocol, short signalID)
  {
    super(protocol, signalID);
  }

  /**
   * @since 2.0
   */
  public RequestWithConfirmation(SignalProtocol<?> protocol, Enum<?> literal)
  {
    super(protocol, literal);
  }

  /**
   * @since 2.0
   */
  public Future<RESULT> sendAsync()
  {
    ExecutorService executorService = getAsyncExecutorService();
    return executorService.submit(new Callable<RESULT>()
    {
      public RESULT call() throws Exception
      {
        return doSend(getProtocol().getTimeout());
      }
    });
  }

  /**
   * @since 2.0
   */
  public RESULT send() throws Exception, RemoteException
  {
    return doSend(getProtocol().getTimeout());
  }

  /**
   * @since 2.0
   */
  public RESULT send(long timeout) throws Exception, RemoteException
  {
    return doSend(timeout);
  }

  RESULT doSend(long timeout) throws Exception
  {
    result = null;
    getProtocol().startSignal(this, timeout);
    return result;
  }

  /**
   * @since 2.0
   */
  protected ExecutorService getAsyncExecutorService()
  {
    return getProtocol().getExecutorService();
  }

  @Override
  void doExecute(BufferInputStream in, BufferOutputStream out) throws Exception
  {
    doOutput(out);
    doInput(in);
  }

  protected abstract void requesting(ExtendedDataOutputStream out) throws Exception;

  /**
   * <b>Important Note:</b> The confirmation must not be empty, i.e. the stream must be used at least to read a
   * <code>boolean</code>. Otherwise synchronization problems will result!
   */
  protected abstract RESULT confirming(ExtendedDataInputStream in) throws Exception;

  @Override
  void doExtendedOutput(ExtendedDataOutputStream out) throws Exception
  {
    requesting(out);
  }

  @Override
  void doExtendedInput(ExtendedDataInputStream in) throws Exception
  {
    result = confirming(in);
  }

  void setRemoteException(Throwable t, boolean responding)
  {
    RemoteException remoteException = getRemoteException(t, responding);
    getBufferInputStream().setException(remoteException);
  }

  private RemoteException getRemoteException(Throwable t, boolean responding)
  {
    if (t instanceof RemoteException)
    {
      return (RemoteException)t;
    }

    return new RemoteException(t, this, responding);
  }
}
