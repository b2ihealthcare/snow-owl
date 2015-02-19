/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Andre Dietisheim - maintenance
 */
package org.eclipse.net4j.signal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.internal.net4j.bundle.OM;
import org.eclipse.net4j.buffer.BufferInputStream;
import org.eclipse.net4j.buffer.IBuffer;
import org.eclipse.net4j.buffer.IBufferProvider;
import org.eclipse.net4j.channel.ChannelOutputStream;
import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.connector.IConnector;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.event.Event;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.io.IStreamWrapper;
import org.eclipse.net4j.util.io.StreamWrapperChain;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.trace.ContextTracer;
import org.eclipse.spi.net4j.Protocol;

/**
 * The default implementation of a {@link ISignalProtocol signal protocol}.
 * <p>
 * On the {@link org.eclipse.net4j.ILocationAware.Location#SERVER receiver side(s)} of protocol the
 * {@link #createSignalReactor(short) createSignalReactor()} method has to be overridden to
 * create appropriate peer instances for incoming {@link Signal signals}.
 *
 * @author Eike Stepper
 */
public class SignalProtocol<INFRA_STRUCTURE> extends Protocol<INFRA_STRUCTURE> implements
    ISignalProtocol<INFRA_STRUCTURE>
{
  /**
   * @since 2.0
   */
  public static final short SIGNAL_REMOTE_EXCEPTION = -1;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_MONITOR_CANCELED = -2;

  /**
   * @since 2.0
   */
  public static final short SIGNAL_MONITOR_PROGRESS = -3;

  /**
   * @since 4.1
   */
  public static final short SIGNAL_SET_TIMEOUT = -4;

  private static final int MIN_CORRELATION_ID = 1;

  private static final int MAX_CORRELATION_ID = Integer.MAX_VALUE;

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_SIGNAL, SignalProtocol.class);

  private static final ContextTracer STREAM_TRACER = new ContextTracer(OM.DEBUG_BUFFER_STREAM, SignalProtocol.class);

  private long timeout = DEFAULT_TIMEOUT;

  private IStreamWrapper streamWrapper;

  private Map<Integer, Signal> signals = new HashMap<Integer, Signal>();

  private int nextCorrelationID = MIN_CORRELATION_ID;

  private boolean failingOver;

  /**
   * @since 2.0
   */
  public SignalProtocol(String type)
  {
    super(type);
  }

  /**
   * @since 2.0
   */
  public long getTimeout()
  {
    return timeout;
  }

  /**
   * @since 2.0
   */
  public void setTimeout(long timeout)
  {
    long oldTimeout = this.timeout;
    handleSetTimeOut(timeout);

    if (oldTimeout != this.timeout && isActive())
    {
      sendSetTimeout();
    }
  }

  public IStreamWrapper getStreamWrapper()
  {
    return streamWrapper;
  }

  public void setStreamWrapper(IStreamWrapper streamWrapper)
  {
    this.streamWrapper = streamWrapper;
  }

  public void addStreamWrapper(IStreamWrapper streamWrapper)
  {
    if (this.streamWrapper == null)
    {
      this.streamWrapper = streamWrapper;
    }
    else
    {
      this.streamWrapper = new StreamWrapperChain(streamWrapper, this.streamWrapper);
    }
  }

  /**
   * @since 2.0
   */
  public IChannel open(IConnector connector)
  {
    return connector.openChannel(this);
  }

  /**
   * @since 2.0
   */
  public void close()
  {
    LifecycleUtil.deactivate(this, OMLogger.Level.DEBUG);
  }

  public boolean waitForSignals(long timeout)
  {
    synchronized (signals)
    {
      while (!signals.isEmpty())
      {
        try
        {
          signals.wait(timeout);
        }
        catch (InterruptedException ex)
        {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Handles a given (incoming) buffer. Creates a signal to act upon the given buffer or uses a previously created
   * signal.
   */
  public void handleBuffer(IBuffer buffer)
  {
    ByteBuffer byteBuffer = buffer.getByteBuffer();
    int correlationID = byteBuffer.getInt();
    if (TRACER.isEnabled())
    {
      TRACER.trace("Received buffer for correlation " + correlationID); //$NON-NLS-1$
    }

    Signal signal;
    boolean newSignalScheduled = false;

    synchronized (signals)
    {
      if (correlationID > 0)
      {
        // Incoming indication
        signal = signals.get(-correlationID);
        if (signal == null)
        {
          short signalID = byteBuffer.getShort();
          if (TRACER.isEnabled())
          {
            TRACER.trace("Got signalID: " + signalID); //$NON-NLS-1$
          }

          signal = provideSignalReactor(signalID);
          signal.setCorrelationID(-correlationID);
          signal.setBufferInputStream(new SignalInputStream(getTimeout()));
          if (signal instanceof IndicationWithResponse)
          {
            signal.setBufferOutputStream(new SignalOutputStream(-correlationID, signalID, false));
          }

          signals.put(-correlationID, signal);
          getExecutorService().execute(signal);
          newSignalScheduled = true;
        }
      }
      else
      {
        // Incoming confirmation
        signal = signals.get(-correlationID);
      }
    }

    if (signal != null) // Can be null after timeout
    {
      if (newSignalScheduled)
      {
        IListener[] listeners = getListeners();
        if (listeners != null)
        {
          fireEvent(new SignalScheduledEvent<INFRA_STRUCTURE>(this, signal), listeners);
        }
      }

      BufferInputStream inputStream = signal.getBufferInputStream();
      inputStream.handleBuffer(buffer);
    }
    else
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Discarding buffer"); //$NON-NLS-1$
      }

      buffer.release();
    }
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("SignalProtocol[{0}]", getType()); //$NON-NLS-1$
  }

  @Override
  protected void doAfterActivate() throws Exception
  {
    super.doAfterActivate();

    if (timeout != DEFAULT_TIMEOUT)
    {
      sendSetTimeout();
    }
  }

  @Override
  protected void doBeforeDeactivate() throws Exception
  {
    synchronized (signals)
    {
      // Wait at most 10 seconds for running signals to finish
      int waitMillis = 10 * 1000;
      long stop = System.currentTimeMillis() + waitMillis;
      while (!signals.isEmpty() && System.currentTimeMillis() < stop)
      {
        signals.wait(1000L);
      }
    }
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    synchronized (signals)
    {
      // Forcefully remove signals waiting on input
      for (final Integer correlationId : signals.keySet()) {
    	Signal signalToClose = signals.remove(correlationId);
		
    	BufferInputStream signalInputStream = signalToClose.getBufferInputStream();
    	if (signalInputStream != null) {
    	  signalInputStream.setException(new RemoteException("Stopping signal because of deactivation: " + this, false));
    	}
      }

      signals.notifyAll();
    }

    IChannel channel = getChannel();
    if (channel != null)
    {
      channel.close();
      setChannel(null);
    }

    super.doDeactivate();
  }

  @Override
  protected void handleChannelDeactivation()
  {
    if (!failingOver)
    {
      super.handleChannelDeactivation();
    }
  }

  protected final SignalReactor provideSignalReactor(short signalID)
  {
    checkActive();
    switch (signalID)
    {
    case SIGNAL_REMOTE_EXCEPTION:
      return new RemoteExceptionIndication(this);

    case SIGNAL_MONITOR_CANCELED:
      return new MonitorCanceledIndication(this);

    case SIGNAL_MONITOR_PROGRESS:
      return new MonitorProgressIndication(this);

    case SIGNAL_SET_TIMEOUT:
      return new SetTimeoutIndication(this);

    default:
      SignalReactor signal = createSignalReactor(signalID);
      if (signal == null)
      {
        throw new IllegalArgumentException("Invalid signalID " + signalID); //$NON-NLS-1$
      }

      return signal;
    }
  }

  /**
   * Returns a new signal instance to serve the given signal ID or <code>null</code> if the signal ID is invalid/unknown
   * for this protocol.
   */
  protected SignalReactor createSignalReactor(short signalID)
  {
    return null;
  }

  /**
   * Returns <code>true</code> by default, override to change this behaviour.
   *
   * @since 4.1
   */
  protected boolean isSendingTimeoutChanges()
  {
    return true;
  }

  synchronized int getNextCorrelationID()
  {
    int correlationID = nextCorrelationID;
    if (nextCorrelationID == MAX_CORRELATION_ID)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Correlation ID wrap-around"); //$NON-NLS-1$
      }

      nextCorrelationID = MIN_CORRELATION_ID;
    }
    else
    {
      ++nextCorrelationID;
    }

    return correlationID;
  }

  InputStream wrapInputStream(InputStream in) throws IOException
  {
    if (streamWrapper != null)
    {
      in = streamWrapper.wrapInputStream(in);
    }

    return in;
  }

  OutputStream wrapOutputStream(OutputStream out) throws IOException
  {
    if (streamWrapper != null)
    {
      out = streamWrapper.wrapOutputStream(out);
    }

    return out;
  }

  void finishInputStream(InputStream in) throws IOException
  {
    if (streamWrapper != null)
    {
      streamWrapper.finishInputStream(in);
    }
  }

  void finishOutputStream(OutputStream out) throws IOException
  {
    if (streamWrapper != null)
    {
      streamWrapper.finishOutputStream(out);
    }
  }

  void startSignal(SignalActor signalActor, long timeout) throws Exception
  {
    checkArg(signalActor.getProtocol() == this, "Wrong protocol"); //$NON-NLS-1$
    short signalID = signalActor.getID();
    int correlationID = signalActor.getCorrelationID();
    signalActor.setBufferOutputStream(new SignalOutputStream(correlationID, signalID, true));
    if (signalActor instanceof RequestWithConfirmation<?>)
    {
      signalActor.setBufferInputStream(new SignalInputStream(timeout));
    }

    synchronized (signals)
    {
      signals.put(correlationID, signalActor);
    }

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new SignalScheduledEvent<INFRA_STRUCTURE>(this, signalActor), listeners);
    }

    signalActor.runSync();
  }

  void stopSignal(Signal signal, Exception exception)
  {
    int correlationID = signal.getCorrelationID();
    synchronized (signals)
    {
      signals.remove(correlationID);
      signals.notifyAll();
    }

    IListener[] listeners = getListeners();
    if (listeners != null)
    {
      fireEvent(new SignalFinishedEvent<INFRA_STRUCTURE>(this, signal, exception), listeners);
    }
  }

  void handleRemoteException(int correlationID, Throwable t, boolean responding)
  {
    synchronized (signals)
    {
      Signal signal = signals.remove(correlationID);
      if (signal instanceof RequestWithConfirmation<?>)
      {
        RequestWithConfirmation<?> request = (RequestWithConfirmation<?>)signal;
        request.setRemoteException(t, responding);
      }

      signals.notifyAll();
    }
  }

  void handleMonitorProgress(int correlationID, double totalWork, double work)
  {
    synchronized (signals)
    {
      Signal signal = signals.get(correlationID);
      if (signal instanceof RequestWithMonitoring<?>)
      {
        RequestWithMonitoring<?> request = (RequestWithMonitoring<?>)signal;
        request.setMonitorProgress(totalWork, work);
      }
    }
  }

  void handleMonitorCanceled(int correlationID)
  {
    synchronized (signals)
    {
      Signal signal = signals.get(correlationID);
      if (signal instanceof IndicationWithMonitoring)
      {
        IndicationWithMonitoring indication = (IndicationWithMonitoring)signal;
        indication.setMonitorCanceled();
      }
    }
  }

  void handleSetTimeOut(long timeout)
  {
    long oldTimeout = this.timeout;
    if (oldTimeout != timeout)
    {
      this.timeout = timeout;
      fireEvent(new TimeoutChangedEvent(this, oldTimeout, timeout));
    }
  }

  void sendSetTimeout()
  {
    if (isSendingTimeoutChanges())
    {
      try
      {
        new SetTimeoutRequest(this, this.timeout).send();
      }
      catch (Exception ex)
      {
        throw WrappedException.wrap(ex);
      }
    }
  }

  /**
   * An {@link IEvent event} fired from a {@link ISignalProtocol signal protocol} when the protocol {@link ISignalProtocol#setTimeout(long) timeout}
   * has been changed.
   *
   * @author Eike Stepper
   * @since 4.1
   */
  public static final class TimeoutChangedEvent extends Event
  {
    private static final long serialVersionUID = 1L;

    private long oldTimeout;

    private long newTimeout;

    private TimeoutChangedEvent(ISignalProtocol<?> source, long oldTimeout, long newTimeout)
    {
      super(source);
      this.oldTimeout = oldTimeout;
      this.newTimeout = newTimeout;
    }

    @Override
    public SignalProtocol<?> getSource()
    {
      return (SignalProtocol<?>)super.getSource();
    }

    public long getOldTimeout()
    {
      return oldTimeout;
    }

    public long getNewTimeout()
    {
      return newTimeout;
    }

    @Override
    public String toString()
    {
      return "TimeoutChangedEvent [oldTimeout=" + oldTimeout + ", newTimeout=" + newTimeout + ", source=" + source
          + "]";
    }

  }

  /**
   * @author Eike Stepper
   */
  class SignalInputStream extends BufferInputStream
  {
    private long timeout;

    public SignalInputStream(long timeout)
    {
      this.timeout = timeout;
    }

    @Override
    public long getMillisBeforeTimeout()
    {
      return timeout;
    }
  }

  /**
   * @author Eike Stepper
   */
  class SignalOutputStream extends ChannelOutputStream
  {
    public SignalOutputStream(final int correlationID, final short signalID, final boolean addSignalID)
    {
      super(getChannel(), new IBufferProvider()
      {
        private IBufferProvider delegate = getBufferProvider();

        private boolean firstBuffer = addSignalID;

        public short getBufferCapacity()
        {
          return delegate.getBufferCapacity();
        }

        public IBuffer provideBuffer()
        {
          IChannel channel = getChannel();
          if (channel == null)
          {
            throw new IORuntimeException("No channel for protocol " + SignalProtocol.this); //$NON-NLS-1$
          }

          IBuffer buffer = delegate.provideBuffer();
          ByteBuffer byteBuffer = buffer.startPutting(channel.getID());
          if (STREAM_TRACER.isEnabled())
          {
            STREAM_TRACER.trace("Providing buffer for correlation " + correlationID); //$NON-NLS-1$
          }

          byteBuffer.putInt(correlationID);
          if (firstBuffer)
          {
            if (SignalProtocol.TRACER.isEnabled())
            {
              STREAM_TRACER.trace("Put signal id " + signalID); //$NON-NLS-1$
            }

            byteBuffer.putShort(signalID);
          }

          firstBuffer = false;
          return buffer;
        }

        public void retainBuffer(IBuffer buffer)
        {
          delegate.retainBuffer(buffer);
        }
      });
    }
  }
}
