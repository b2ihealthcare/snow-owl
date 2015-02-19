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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.view.CDOQuery;

/**
 * Represents the execution state of a {@link CDOQuery query} in the server towards a {@link IQueryHandler query
 * handler}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.exclude
 */
public interface IQueryContext extends CDOBranchPoint
{
  public IView getView();

  /**
   * @since 4.0
   */
  public int getResultCount();

  /**
   * Adds the given object to the results of the associated query.
   * 
   * @param object
   *          Support many primitives, CDOID and CDORevision. CDORevision are converted in CDOID and only CDOID are
   *          transfered to the client.
   * @return <code>true</code> to indicate that more results can be passed subsequently, <code>false</code> otherwise
   *         (i.e. maxResults has been reached or an asynchronous query has been canceled).
   */
  public boolean addResult(Object object);
}
