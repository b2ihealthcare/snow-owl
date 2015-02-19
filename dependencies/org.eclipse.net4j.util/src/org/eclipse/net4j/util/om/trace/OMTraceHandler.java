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

/**
 * Handles {@link TraceHandlerEvent trace events}, for example appends them to a {@link PrintTraceHandler stream} sends them to a
 * {@link RemoteTraceHandler remote} trace handler.
 *
 * @author Eike Stepper
 * @see PrintTraceHandler#CONSOLE
 * @see RemoteTraceHandler
 */
public interface OMTraceHandler
{
  public void traced(OMTraceHandlerEvent event);
}
