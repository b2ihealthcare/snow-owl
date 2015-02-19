/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager;

/**
 * Manages all persistent aspects of durable CDO views and provides for vetoable
 * {@link #addDurableViewHandler(ILockingManager.DurableViewHandler) interception} of the durable view resumption
 * process.
 * 
 * @author Caspar De Groot
 * @since 4.1
 */
public interface ILockingManager extends IDurableLockingManager
{
  public void addDurableViewHandler(DurableViewHandler handler);

  public void removeDurableViewHandler(DurableViewHandler handler);

  public DurableViewHandler[] getDurableViewHandlers();

  /**
   * A call-back interface primarily intended to allow implementers to prevent the view from being opened by throwing an
   * exception. See {@link ILockingManager#addDurableViewHandler(DurableViewHandler)}.
   * 
   * @author Caspar De Groot
   * @since 4.1
   */
  public interface DurableViewHandler
  {
    /**
     * A call-back method primarily intended to allow implementers to prevent the view from being opened by throwing an
     * exception. See {@link ILockingManager#addDurableViewHandler(DurableViewHandler)}.
     */
    public void openingView(CDOCommonSession session, int viewID, boolean readOnly, LockArea area) throws Exception;
  }
}
