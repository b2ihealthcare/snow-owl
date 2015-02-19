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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * A {@link OMLogHandler log handler} that appends log events to a {@link #getLogFile() file}.
 * <p>
 * The file is opened before and closed after each log event.
 *
 * @author Eike Stepper
 * @since 2.0
 * @see PrintLogHandler
 */
public class FileLogHandler extends AbstractLogHandler
{
  private File logFile;

  public FileLogHandler(File logFile, Level logLevel)
  {
    super(logLevel);
    this.logFile = logFile;
  }

  public FileLogHandler(File logFile)
  {
    this.logFile = logFile;
  }

  public File getLogFile()
  {
    return logFile;
  }

  @Override
  protected void writeLog(OMLogger logger, Level level, String msg, Throwable t) throws Throwable
  {
    FileOutputStream fos = null;

    try
    {
      fos = new FileOutputStream(logFile, true);
      PrintStream stream = new PrintStream(fos);
      stream.println(toString(level) + " " + msg); //$NON-NLS-1$
      if (t != null)
      {
        IOUtil.print(t, stream);
      }
    }
    finally
    {
      IOUtil.close(fos);
    }
  }
}
