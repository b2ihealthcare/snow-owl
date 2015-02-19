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

import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger.Level;

/**
 * Handles {@link OMLogFilter filtered} log events, for example appends them to a {@link PrintLogHandler stream} or
 * the Eclipse {@link EclipseLoggingBridge error log}.
 *
 * @author Eike Stepper
 * @see OMPlatform#addLogHandler(OMLogHandler)
 * @see OMPlatform#removeLogHandler(OMLogHandler)
 * @see EclipseLoggingBridge#INSTANCE
 * @see PrintLogHandler#CONSOLE
 * @see OSGiLoggingBridge#INSTANCE
 * @see FileLogHandler
 */
public interface OMLogHandler
{
  public void logged(OMLogger logger, Level level, String msg, Throwable t);
}
