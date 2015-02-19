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

import org.eclipse.net4j.util.om.OMBundle;

/**
 * The {@link OMBundle#logger() logger} of a {@link OMBundle bundle}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMLogger
{
  public OMBundle getBundle();

  public void log(Level level, String msg, Throwable t);

  public void error(String msg, Throwable t);

  public void warn(String msg, Throwable t);

  public void info(String msg, Throwable t);

  public void debug(String msg, Throwable t);

  public void log(Level level, String msg);

  public void error(String msg);

  public void warn(String msg);

  public void info(String msg);

  public void debug(String msg);

  public void log(Level level, Throwable t);

  public void error(Throwable t);

  public void warn(Throwable t);

  public void info(Throwable t);

  public void debug(Throwable t);

  /**
   * Enumerates the possible severity levels of log events.
   *
   * @author Eike Stepper
   */
  public enum Level
  {
    ERROR, WARN, INFO, DEBUG
  }
}
