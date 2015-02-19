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

import org.eclipse.net4j.util.om.log.OMLogger.Level;

/**
 * An abstract base implementation of a {@link OMLogHandler log handler} that filters log events with a {@link Level log level}
 * greater than a configurable {@link #setLogLevel(Level) maximum level}.
 *
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractLogHandler implements OMLogHandler
{
  private Level logLevel;

  public AbstractLogHandler(Level logLevel)
  {
    this.logLevel = logLevel;
  }

  public AbstractLogHandler()
  {
    this(Level.DEBUG);
  }

  public Level getLogLevel()
  {
    return logLevel;
  }

  public void setLogLevel(Level logLevel)
  {
    this.logLevel = logLevel;
  }

  public void logged(OMLogger logger, Level level, String msg, Throwable t)
  {
    try
    {
      if (level.ordinal() <= logLevel.ordinal())
      {
        writeLog(logger, level, msg, t);
      }
    }
    catch (Throwable ignore)
    {
      // Ignore
    }
  }

  protected abstract void writeLog(OMLogger logger, Level level, String msg, Throwable t) throws Throwable;

  public static String toString(Level level)
  {
    switch (level)
    {
    case ERROR:
      return "[ERROR]"; //$NON-NLS-1$
    case WARN:
      return "[WARN]"; //$NON-NLS-1$
    case INFO:
      return "[INFO]"; //$NON-NLS-1$
    case DEBUG:
      return "[DEBUG]"; //$NON-NLS-1$
    default:
      throw new IllegalArgumentException("Illegal log level: " + level); //$NON-NLS-1$
    }
  }
}
