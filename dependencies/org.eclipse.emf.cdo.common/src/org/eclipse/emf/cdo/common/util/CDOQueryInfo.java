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
package org.eclipse.emf.cdo.common.util;

import java.util.Map;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.CDOCommonView;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;

/**
 * Encapsulates all the transferrable information that fully specifies a query from a {@link CDOCommonView view} to a
 * {@link CDOCommonRepository repository}.
 * 
 * @author Simon McDuff
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOQueryInfo
{
  public static final int UNLIMITED_RESULTS = -1;

  /**
   * Returns the language identifier of this query, never <code>null</code>.
   */
  public String getQueryLanguage();

  /**
   * Returns the query string of this query or <code>null</code> if no query string has been set.
   */
  public String getQueryString();

  /**
   * Returns the parameters of this query as a map.
   */
  public Map<String, Object> getParameters();

  /**
   * Returns the context object, or <code>null</code> if no context is bound.
   * 
   * @since 4.0
   */
  public Object getContext();

  /**
   * Returns the maximum number of results to retrieve or {@link #UNLIMITED_RESULTS} for no limitation.
   */
  public int getMaxResults();

  /**
   * Returns <code>true</code> if the view of this query had legacy mode enabled at the time this query was created,
   * <code>false</code> otherwise.
   * 
   * @since 4.0
   */
  public boolean isLegacyModeEnabled();

  /**
   * Returns the {@link CDOChangeSetData change set} to be considered if this query has been created by a dirty
   * transaction, <code>null</code> otherwise.
   * 
   * @since 4.0
   */
  public CDOChangeSetData getChangeSetData();
}
