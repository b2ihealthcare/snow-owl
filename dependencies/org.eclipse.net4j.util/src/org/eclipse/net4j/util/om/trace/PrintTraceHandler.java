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

import org.eclipse.net4j.util.io.IOUtil;

import java.io.PrintStream;
import java.text.MessageFormat;

/**
 * A {@link OMTraceHandler trace handler} that appends {@link OMTraceHandlerEvent trace events}
 * to a {@link #getStream() print stream}.
 *
 * @author Eike Stepper
 */
public class PrintTraceHandler implements OMTraceHandler
{
  public static final PrintTraceHandler CONSOLE = new PrintTraceHandler();

  private PrintStream stream;

  private String pattern;

  private boolean shortContext;

  public PrintTraceHandler(PrintStream stream)
  {
    this.stream = stream;
  }

  protected PrintTraceHandler()
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

  public String getPattern()
  {
    return pattern;
  }

  /**
   * Pattern arguments:
   * <p>
   * <ul>
   * <li>0} --> String <b>tracerName</b><br>
   * <li>1} --> String <b>tracerShort</b><br>
   * <li>2} --> String <b>contextName</b><br>
   * <li>3} --> String <b>contextShort</b><br>
   * <li>4} --> long <b>timeStamp</b><br>
   * <li>5} --> String <b>message</b><br>
   * <li>6} --> String <b>threadName</b><br>
   * <li>7} --> long <b>threadID</b><br>
   * <li>8} --> int <b>threadPriority</b><br>
   * <li>9} --> Thread.State <b>threadState</b><br>
   * </ul>
   */
  public void setPattern(String pattern)
  {
    this.pattern = pattern;
  }

  public boolean isShortContext()
  {
    return shortContext;
  }

  public void setShortContext(boolean shortContext)
  {
    this.shortContext = shortContext;
  }

  public void traced(OMTraceHandlerEvent event)
  {
    String line = pattern == null ? format(shortContext, event) : format(pattern, event);
    stream.println(line);
    if (event.getThrowable() != null)
    {
      IOUtil.print(event.getThrowable(), stream);
    }
  }

  public static String format(boolean shortContext, OMTraceHandlerEvent event)
  {
    Class<?> context = event.getContext();
    String contextName = shortContext ? context.getSimpleName() : context.getName();
    return Thread.currentThread().getName() + " [" + contextName + "] " + event.getMessage(); //$NON-NLS-1$ //$NON-NLS-2$
  }

  /**
   * Pattern arguments:
   * <p>
   * <ul>
   * <li>0} --> String <b>tracerName</b><br>
   * <li>1} --> String <b>tracerShort</b><br>
   * <li>2} --> String <b>contextName</b><br>
   * <li>3} --> String <b>contextShort</b><br>
   * <li>4} --> long <b>timeStamp</b><br>
   * <li>5} --> String <b>message</b><br>
   * <li>6} --> String <b>threadName</b><br>
   * <li>7} --> long <b>threadID</b><br>
   * <li>8} --> int <b>threadPriority</b><br>
   * <li>9} --> Thread.State <b>threadState</b><br>
   * </ul>
   */
  public static String format(String pattern, OMTraceHandlerEvent event)
  {
    final OMTracer tracer = event.getTracer();
    final String tracerName = tracer.getFullName();
    final String tracerShort = tracer.getName();

    final Class<?> context = event.getContext();
    final String contextName = context.getName();
    final String contextShort = context.getName();

    final long timeStamp = event.getTimeStamp();
    final String message = event.getMessage();

    final Thread thread = Thread.currentThread();
    final String threadName = thread.getName();
    final long threadID = thread.getId();
    final int threadPriority = thread.getPriority();
    final Thread.State threadState = thread.getState();

    return MessageFormat.format(pattern, tracerName, tracerShort, contextName, contextShort, timeStamp, message,
        threadName, threadID, threadPriority, threadState);
  }
}
