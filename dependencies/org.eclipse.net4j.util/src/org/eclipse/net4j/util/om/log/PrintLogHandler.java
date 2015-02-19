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
package org.eclipse.net4j.util.om.log;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

import java.io.PrintStream;

/**
 * A {@link OMLogHandler log handler} that appends log events to {@link #getErr() error} and {@link #getOut() output} streams.
 *
 * @author Eike Stepper
 */
public class PrintLogHandler extends AbstractLogHandler
{
  public static final PrintLogHandler CONSOLE = new PrintLogHandler();

  private PrintStream out;

  private PrintStream err;

  public PrintLogHandler(PrintStream out, PrintStream err)
  {
    this.out = out;
    this.err = err;
  }

  protected PrintLogHandler()
  {
    this(IOUtil.OUT(), IOUtil.ERR());
  }

  /**
   * @since 3.2
   */
  public PrintStream getOut()
  {
    return out;
  }

  /**
   * @since 3.2
   */
  public PrintStream getErr()
  {
    return err;
  }

  @Override
  protected void writeLog(OMLogger logger, Level level, String msg, Throwable t) throws Throwable
  {
    PrintStream stream = level == Level.ERROR ? err : out;
    stream.println(toString(level) + " " + msg); //$NON-NLS-1$
    if (t != null)
    {
      IOUtil.print(t, stream);
    }
  }
}
