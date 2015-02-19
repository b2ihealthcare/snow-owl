/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.spi.common.AbstractQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalQueryResult;
import org.eclipse.emf.cdo.spi.server.InternalView;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class QueryResult extends AbstractQueryResult<Object> implements InternalQueryResult
{
  public QueryResult(InternalView view, CDOQueryInfo queryInfo, int queryID)
  {
    super(view, queryInfo, queryID);
  }

  @Override
  public InternalView getView()
  {
    return (InternalView)super.getView();
  }
}
