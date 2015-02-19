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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.UUID;

/**
 * A {@link OMTraceHandler trace handler} that sends {@link OMTraceHandlerEvent trace events} to a remote trace {@link RemoteTraceServer server}.
 *
 * @author Eike Stepper
 */
public class RemoteTraceHandler implements OMTraceHandler
{
  public static final String DEFAULT_HOST = "localhost"; //$NON-NLS-1$

  public static final int DEFAULT_PORT = RemoteTraceServer.DEFAULT_PORT;

  private static int uniqueCounter;

  private String agentID;

  private String host;

  private int port;

  private Socket socket;

  public RemoteTraceHandler() throws IOException
  {
    this(uniqueAgentID());
  }

  public RemoteTraceHandler(String agentID) throws IOException
  {
    this(agentID, DEFAULT_HOST);
  }

  public RemoteTraceHandler(String agentID, String host) throws IOException
  {
    this(agentID, host, DEFAULT_PORT);
  }

  public RemoteTraceHandler(String agentID, String host, int port) throws IOException
  {
    this.agentID = agentID;
    this.host = host;
    this.port = port;
    socket = connect();
  }

  public Exception close()
  {
    try
    {
      socket.close();
      return null;
    }
    catch (IOException ex)
    {
      OM.LOG.error(ex);
      return ex;
    }
  }

  public void traced(OMTraceHandlerEvent event)
  {
    try
    {
      OutputStream outputStream = socket.getOutputStream();
      DataOutputStream out = new DataOutputStream(outputStream);

      out.writeLong(event.getTimeStamp());
      writeUTF(out, agentID);
      writeUTF(out, event.getTracer().getBundle().getBundleID());
      writeUTF(out, event.getTracer().getFullName());
      writeUTF(out, event.getContext() == null ? "" : event.getContext().getName()); //$NON-NLS-1$
      writeUTF(out, event.getMessage());
      if (event.getThrowable() == null)
      {
        out.writeBoolean(false);
      }
      else
      {
        out.writeBoolean(true);
        String message = event.getThrowable().getMessage();
        writeUTF(out, message);

        StackTraceElement[] stackTrace = event.getThrowable().getStackTrace();
        int size = stackTrace == null ? 0 : stackTrace.length;
        out.writeInt(size);

        for (int i = 0; i < size; i++)
        {
          StackTraceElement element = stackTrace[i];
          writeUTF(out, element.getClassName());
          writeUTF(out, element.getMethodName());
          writeUTF(out, element.getFileName());
          out.writeInt(element.getLineNumber());
        }
      }

      out.flush();
    }
    catch (IOException ex)
    {
      IOUtil.print(ex);
    }
  }

  protected Socket connect() throws IOException
  {
    return new Socket(host, port);
  }

  protected void writeUTF(DataOutputStream out, String str) throws IOException
  {
    out.writeUTF(str == null ? "" : str); //$NON-NLS-1$
  }

  public static String uniqueAgentID()
  {
    try
    {
      InetAddress localMachine = InetAddress.getLocalHost();
      return localMachine.getHostName() + "#" + ++uniqueCounter; //$NON-NLS-1$
    }
    catch (Exception ex)
    {
      UUID uuid = UUID.randomUUID();
      return uuid.toString();
    }
  }
}
