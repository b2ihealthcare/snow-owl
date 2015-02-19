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

import org.eclipse.net4j.internal.util.bundle.AbstractPlatform;
import org.eclipse.net4j.util.om.OMBundle;

/**
 * The default {@link OMLogger logger} implementation.
 *
 * @author Eike Stepper
 */
public class Logger implements OMLogger
{
  private OMBundle bundle;

  public Logger(OMBundle bundle)
  {
    this.bundle = bundle;
  }

  public OMBundle getBundle()
  {
    return bundle;
  }

  public void log(Level level, String msg, Throwable t)
  {
    ((AbstractPlatform)bundle.getPlatform()).log(this, level, msg, t);
  }

  public void error(String msg, Throwable t)
  {
    log(Level.ERROR, msg, t);
  }

  public void warn(String msg, Throwable t)
  {
    log(Level.WARN, msg, t);
  }

  public void info(String msg, Throwable t)
  {
    log(Level.INFO, msg, t);
  }

  public void debug(String msg, Throwable t)
  {
    log(Level.DEBUG, msg, t);
  }

  public void log(Level level, String msg)
  {
    log(level, msg, null);
  }

  public void error(String msg)
  {
    log(Level.ERROR, msg);
  }

  public void warn(String msg)
  {
    log(Level.WARN, msg);
  }

  public void info(String msg)
  {
    log(Level.INFO, msg);
  }

  public void debug(String msg)
  {
    log(Level.DEBUG, msg);
  }

  public void log(Level level, Throwable t)
  {
    String msg = t.getMessage();
    if (msg == null)
    {
      msg = t.getClass().getSimpleName();
    }

    log(level, msg, t);
  }

  public void error(Throwable t)
  {
    log(Level.ERROR, t);
  }

  public void warn(Throwable t)
  {
    log(Level.WARN, t);
  }

  public void info(Throwable t)
  {
    log(Level.INFO, t);
  }

  public void debug(Throwable t)
  {
    log(Level.DEBUG, t);
  }
}
