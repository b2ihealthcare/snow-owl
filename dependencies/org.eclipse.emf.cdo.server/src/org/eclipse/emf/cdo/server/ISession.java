/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;

import org.eclipse.net4j.util.container.IContainer;

/**
 * The server-side representation of a client {@link CDOSession session}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link org.eclipse.emf.cdo.spi.server.ISessionProtocol}
 * @apiviz.composedOf {@link IView} - - views
 * @apiviz.composedOf {@link ITransaction} - - transactions
 */
public interface ISession extends CDOCommonSession, IContainer<IView>
{
  /**
   * @since 3.0
   */
  public ISessionManager getManager();

  /**
   * @since 3.0
   */
  public ISessionProtocol getProtocol();

  /**
   * @since 4.0
   */
  public long getLastUpdateTime();

  /**
   * @since 2.0
   */
  public boolean isSubscribed();

  /**
   * @since 3.0
   */
  public IView openView(int viewID, CDOBranchPoint branchPoint);

  /**
   * @since 3.0
   */
  public ITransaction openTransaction(int viewID, CDOBranchPoint branchPoint);
}
