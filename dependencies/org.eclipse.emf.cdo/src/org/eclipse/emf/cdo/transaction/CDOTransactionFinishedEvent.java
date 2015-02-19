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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.view.CDOViewEvent;

import java.util.Map;

/**
 * A {@link CDOViewEvent view event} fired from a {@link CDOTransaction transaction} when it becomes
 * {@link CDOTransaction#isDirty() clean} after a sucessful {@link CDOTransaction#commit() commit} or
 * {@link CDOTransaction#rollback() rollback}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOTransactionFinishedEvent extends CDOViewEvent
{
  public Type getType();

  public Map<CDOID, CDOID> getIDMappings();

  /**
   * Enumerates the possible {@link CDOTransactionFinishedEvent#getType() causes} for a {@link CDOTransaction
   * transaction} to become finished.
   * 
   * @author Eike Stepper
   */
  public enum Type
  {
    COMMITTED, ROLLED_BACK
  }
}
