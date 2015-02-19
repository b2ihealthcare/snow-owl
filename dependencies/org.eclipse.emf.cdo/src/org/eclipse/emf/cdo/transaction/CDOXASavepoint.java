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
package org.eclipse.emf.cdo.transaction;

import java.util.List;

/**
 * Creates a save point in a {@link CDOXATransaction} that can be used to roll back a part of the transaction.
 * <p>
 * <b>Note:</b> Save points do not flush to disk. Everything is done in memory on the client side.
 * 
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOXASavepoint extends CDOUserSavepoint
{
  public CDOXATransaction getTransaction();

  public CDOXASavepoint getNextSavepoint();

  public CDOXASavepoint getPreviousSavepoint();

  public List<CDOSavepoint> getSavepoints();
}
