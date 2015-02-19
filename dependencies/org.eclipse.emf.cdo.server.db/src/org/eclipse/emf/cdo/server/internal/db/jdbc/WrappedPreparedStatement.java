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
package org.eclipse.emf.cdo.server.internal.db.jdbc;

import org.eclipse.emf.cdo.server.internal.db.bundle.OM;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import java.sql.PreparedStatement;
import java.text.MessageFormat;

/**
 * Wrapper for a prepared statement that is cleaned up when it is cached in a WeakReferenceCache and gc'd. Note that
 * this is just a wrapper with access to its wrapped object. There's no interface delegation, because the interface
 * delegation would also put the necessity to wrap resultSets and maybe even more, which seems to much overkill for a
 * simple internal implementation.
 * 
 * @author Stefan Winkler
 */
public class WrappedPreparedStatement
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, WrappedPreparedStatement.class);

  private PreparedStatement wrappedStatement;

  public WrappedPreparedStatement(PreparedStatement ps)
  {
    wrappedStatement = ps;
    if (TRACER.isEnabled())
    {
      TRACER.format("Wrapping Statement: {0}", wrappedStatement); //$NON-NLS-1$
    }
  }

  public PreparedStatement getWrappedStatement()
  {
    return wrappedStatement;
  }

  public PreparedStatement unwrapStatement()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("UnWrapping Statement: {0}", wrappedStatement); //$NON-NLS-1$
    }

    PreparedStatement result = wrappedStatement;
    wrappedStatement = null;
    return result;
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("Wrapped[{0}]", wrappedStatement); //$NON-NLS-1$
  }

  @Override
  protected void finalize() throws Throwable
  {
    if (wrappedStatement != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Closing statement: {0}", wrappedStatement); //$NON-NLS-1$
      }

      DBUtil.close(wrappedStatement);
      wrappedStatement = null;
    }
  }
}
