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
 * Provides the consumer with {@link IQueryHandler query handlers} that are capable of executing {@link CDOQuery
 * queries} represented by specific {@link CDOQueryInfo query infos}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @apiviz.uses {@link IQueryHandler} - - provides
 */
public interface IQueryHandlerProvider
{
  /**
   * @since 3.0
   */
  public IQueryHandler getQueryHandler(CDOQueryInfo info);
}
