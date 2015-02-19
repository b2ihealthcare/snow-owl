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
package org.eclipse.net4j.util.om.trace;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.io.IOUtil;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.EventObject;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A server that {@link RemoteTraceHandler agents} can connect to and that passes the received {@link OMTraceHandlerEvent trace events}
 * to {@link #addListener(Listener) registered} {@link Listener listeners}.
 *
 * @author Eike Stepper
 */
public class RemoteTraceServer
{
  public static final String DEFAULT_ADDRESS = "0.0.0.0"; //$NON-NLS-1$

  public static final int DEFAULT_PORT = 2035;

  public static final int ANY_PORT = 0;

  private static long lastEventID;

  private int port;

  private String address;

  private ServerSocket serverSocket;

  private Queue<Listener> listeners = new ConcurrentLinkedQueue<Listener>();

  public RemoteTraceServer() throws IOException
  {
    this(DEFAULT_PORT);
  }

  public RemoteTraceServer(int port) throws IOException
  {
    this(port, DEFAULT_ADDRESS);
  }

  public RemoteTraceServer(int port, String address) throws IOException
  {
    this.port = port;
    this.address = address;
    serverSocket = bind();
    start();
  }

  /**
   * @since 3.0
   */
  public void start()
  {
    new Thread("RemoteTraceServer") //$NON-NLS-1$
    {
      @Override
      public void run()
      {
        handleConnections();
      }
    }.start();
  }

  public void addListener(Listener listener)
  {
    if (!listeners.contains(listener))
    {
      listeners.add(listener);
    }
  }

  public void removeListener(Listener listener)
  {
    listeners.remove(listener);
  }

  public Exception close()
  {
    try
    {
      serverSocket.close();
      return null;
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  protected ServerSocket bind() throws IOException
  {
    InetAddress addr = InetAddress.getByName(address);
    return new ServerSocket(port, 5, addr);
  }

  protected void handleConnections()
  {
    for (;;)
    {
      try
      {
        final Socket socket = serverSocket.accept();
        new Thread()
        {
          @Override
          public void run()
          {
            handleSession(socket);
          }
        }.start();
      }
      catch (IOException ex)
      {
        if (!serverSocket.isClosed())
        {
          IOUtil.print(ex);
        }

        break;
      }
    }
  }

  protected void handleSession(Socket socket)
  {
    try
    {
      InputStream inputStream = socket.getInputStream();
      DataInputStream in = new DataInputStream(inputStream);

      for (;;)
      {
        handleTrace(in);
      }
    }
    catch (IOException ex)
    {
      IOUtil.print(ex);
    }
  }

  protected void handleTrace(DataInputStream in) throws IOException
  {
    Event event = new Event(this);
    event.timeStamp = in.readLong();
    event.agentID = in.readUTF();
    event.bundleID = in.readUTF();
    event.tracerName = in.readUTF();
    event.context = in.readUTF();
    event.message = in.readUTF();
    if (in.readBoolean())
    {
      event.throwable = in.readUTF();
      int size = in.readInt();
      event.stackTrace = new StackTraceElement[size];
      for (int i = 0; i < size; i++)
      {
        String className = in.readUTF();
        String methodName = in.readUTF();
        String fileName = in.readUTF();
        int lineNumber = in.readInt();
        event.stackTrace[i] = new StackTraceElement(className, methodName, fileName, lineNumber);
      }
    }

    fireEvent(event);
  }

  protected void fireEvent(Event event)
  {
    for (Listener listener : listeners)
    {
      try
      {
        listener.notifyRemoteTrace(event);
      }
      catch (RuntimeException ex)
      {
        IOUtil.print(ex);
      }
    }
  }

  /**
   * A trace event being passed by a remote trace {@link RemoteTraceServer server} to
   * {@link RemoteTraceServer#addListener(Listener) registered} {@link Listener listeners}.
   *
   * @author Eike Stepper
   */
  public static class Event extends EventObject
  {
    private static final long serialVersionUID = 1L;

    private long id;

    long timeStamp;

    /**
     * @since 3.2
     */
    protected String agentID;

    /**
     * @since 3.2
     */
    protected String bundleID;

    /**
     * @since 3.2
     */
    protected String tracerName;

    /**
     * @since 3.2
     */
    protected String context;

    /**
     * @since 3.2
     */
    protected String message;

    /**
     * @since 3.2
     */
    protected String throwable;

    /**
     * @since 3.2
     */
    protected StackTraceElement[] stackTrace;

    /**
     * @since 3.2
     */
    protected Event(RemoteTraceServer server)
    {
      super(server);
      id = ++lastEventID;
    }

    public RemoteTraceServer getRemoteTraceServer()
    {
      return (RemoteTraceServer)source;
    }

    public long getID()
    {
      return id;
    }

    public long getTimeStamp()
    {
      return timeStamp;
    }

    public String getAgentID()
    {
      return agentID;
    }

    public String getBundleID()
    {
      return bundleID;
    }

    public String getContext()
    {
      return context;
    }

    public String getMessage()
    {
      return message;
    }

    public StackTraceElement[] getStackTrace()
    {
      return stackTrace;
    }

    public String getThrowable()
    {
      return throwable;
    }

    public String getTracerName()
    {
      return tracerName;
    }

    @Override
    public String toString()
    {
      StringBuilder builder = new StringBuilder();
      builder.append("TraceEvent[agentID="); //$NON-NLS-1$
      builder.append(agentID);

      builder.append(", bundleID="); //$NON-NLS-1$
      builder.append(bundleID);

      builder.append(", tracerName="); //$NON-NLS-1$
      builder.append(tracerName);

      builder.append(", context="); //$NON-NLS-1$
      builder.append(context);

      builder.append(", message="); //$NON-NLS-1$
      builder.append(message);

      builder.append(", throwable="); //$NON-NLS-1$
      builder.append(throwable);

      builder.append(", stackTrace="); //$NON-NLS-1$
      builder.append(stackTrace);

      builder.append("]"); //$NON-NLS-1$
      return builder.toString();
    }

    public String getText(int index)
    {
      switch (index)
      {
      case 0:
        return Long.toString(id);
      case 1:
        return new Date(timeStamp).toString();
      case 2:
        return agentID;
      case 3:
        return bundleID;
      case 4:
        return tracerName;
      case 5:
        return context;
      case 6:
        return message;
      case 7:
        return throwable;
      }

      throw new IllegalArgumentException("Invalid index: " + index); //$NON-NLS-1$
    }

    public boolean hasError()
    {
      return throwable != null && throwable.length() != 0 //
          || stackTrace != null && stackTrace.length != 0;
    }
  }

  /**
   * Listens to {@link Event trace events} being passed by a remote trace {@link RemoteTraceServer server}.
   *
   * @author Eike Stepper
   * @see RemoteTraceServer#addListener(Listener)
   * @see RemoteTraceServer#removeListener(Listener)
   * @see PrintListener
   */
  public interface Listener
  {
    public void notifyRemoteTrace(Event event);
  }

  /**
   * A {@link Listener listener} that appends {@link Event trace events} to a {@link #getStream() print stream}.
   *
   * @author Eike Stepper
   */
  public static class PrintListener implements Listener
  {
    public static final PrintListener CONSOLE = new PrintListener();

    private PrintStream stream;

    public PrintListener(PrintStream stream)
    {
      this.stream = stream;
    }

    protected PrintListener()
    {
      this(IOUtil.OUT());
    }

    /**
     * @since 3.2
     */
    public PrintStream getStream()
    {
      return stream;
    }

    public void notifyRemoteTrace(Event event)
    {
      stream.println("[TRACE] " + event.getAgentID()); //$NON-NLS-1$
      stream.println(event.getBundleID());
      stream.println(event.getTracerName());
      stream.println(event.getContext());
      stream.println(event.getMessage());

      String throwable = event.getThrowable();
      if (throwable != null && throwable.length() != 0)
      {
        stream.println(throwable);
      }

      StackTraceElement[] stackTrace = event.getStackTrace();
      if (stackTrace != null)
      {
        for (StackTraceElement element : stackTrace)
        {
          stream.print(element.getClassName());
          stream.print("." + element.getMethodName()); //$NON-NLS-1$
          stream.print("(" + element.getFileName()); //$NON-NLS-1$
          stream.print(":" + element.getLineNumber()); //$NON-NLS-1$
          stream.println(")"); //$NON-NLS-1$
        }
      }

      stream.println();
    }
  }
}
