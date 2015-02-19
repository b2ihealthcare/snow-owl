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
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.view.CDOQuery;

/**
 * A query language handler that is capable of executing a {@link CDOQuery query}.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public interface IQueryHandler
{
  /**
   * Executes the {@link CDOQuery query} represented by the specified {@link CDOQueryInfo query info} by
   * {@link IQueryContext#addResult(Object) passing} the query results to the query execution engine represented by the
   * specified {@link IQueryContext execution context}.
   * 
   * @since 3.0
   */
  public void executeQuery(CDOQueryInfo info, IQueryContext context);
}
