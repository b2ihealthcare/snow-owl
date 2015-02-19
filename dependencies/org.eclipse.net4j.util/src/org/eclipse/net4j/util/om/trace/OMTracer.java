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

import org.eclipse.net4j.util.om.OMBundle;

/**
 * A {@link #getName() named} {@link OMBundle#tracer(String) tracer} within the {@link #getParent() hierarchy} of tracers of a {@link OMBundle bundle}.
 *
 * @author Eike Stepper
 * @see OMBundle#tracer(String)
 * @see OMTracer#tracer(String)
 * @see ContextTracer
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface OMTracer
{
  public OMBundle getBundle();

  public OMTracer getParent();

  public String getName();

  public String getFullName();

  public boolean isEnabled();

  public void setEnabled(boolean enabled);

  public void trace(OMTraceHandlerEvent traceRecord);

  public OMTraceHandlerEvent format(Class<?> context, String pattern, Object... args);

  public OMTraceHandlerEvent format(Class<?> context, String pattern, Throwable t, Object... args);

  public OMTraceHandlerEvent trace(Class<?> context, String msg, Throwable t);

  public OMTraceHandlerEvent trace(Class<?> context, String msg);

  public OMTraceHandlerEvent trace(Class<?> context, Throwable t);

  public OMTracer tracer(String name);
}
