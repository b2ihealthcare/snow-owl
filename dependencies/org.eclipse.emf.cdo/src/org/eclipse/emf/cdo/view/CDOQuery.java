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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;

import org.eclipse.net4j.util.collection.CloseableIterator;

import java.util.List;

/**
 * Provides access to the information that specifies a query from a {@link CDOCommonView view} to a
 * {@link CDOCommonRepository repository} and to the results of the remote query execution;
 * 
 * @author Simon McDuff
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link java.lang.Object} oneway - - context
 * @apiviz.composedOf {@link java.util.Map.Entry} - - parameters
 * @apiviz.uses {@link java.util.List} - - result
 * @apiviz.uses {@link org.eclipse.net4j.util.collection.CloseableIterator} - - resultAsync
 */
public interface CDOQuery extends CDOQueryInfo
{
  /**
   * Returns the {@link CDOView view} this query was created by and is associated with.
   * 
   * @return Never <code>null</code>.
   */
  public CDOView getView();

  /**
   * Sends this query to the server and returns a typed {@link CloseableIterator iterator} over the query result.
   * <p>
   * As opposed to the {@link #getResult(Class)} method, this method <b>asynchronously</b> communicates with the server.
   * In other words, the returned iterator can be used immediately, even if the server is still about to send pending
   * result elements.
   */
  public <T> CloseableIterator<T> getResultAsync(Class<T> classObject);

  /**
   * Same as {@link #getResultAsync(Class)} but tries to infer the return type from the static context.
   * 
   * @since 4.0
   */
  public <T> CloseableIterator<T> getResultAsync();

  /**
   * Sends this query to the server and returns a typed {@link List list} containing the query result.
   * <p>
   * As opposed to the {@link #getResultAsync(Class)} method, this method <b>synchronously</b> communicates with the
   * server. In other words, the result list is only returned after all result elements have been received by the
   * client.
   */
  public <T> List<T> getResult(Class<T> classObject);

  /**
   * Same as {@link #getResult(Class)} but tries to infer the return type from the static context.
   * 
   * @since 4.0
   */
  public <T> List<T> getResult();

  /**
   * Sets the maximum number of results to retrieve from the server.
   * 
   * @param maxResults
   *          the maximum number of results to retrieve or {@link #UNLIMITED_RESULTS} for no limitation.
   * @return the same query instance.
   */
  public CDOQuery setMaxResults(int maxResults);

  /**
   * Binds an argument value to a named parameter.
   * 
   * @param name
   *          the parameter name
   * @param value
   *          the value to bind
   * @return the same query instance
   * @throws IllegalArgumentException
   *           if the parameter name does not correspond to a parameter in the query string or if the argument value is
   *           of incorrect type
   */
  public CDOQuery setParameter(String name, Object value);

  /**
   * Binds an object as teh context for this query.
   * 
   * @since 4.0
   */
  public CDOQuery setContext(Object object);
}
