/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;

/**
 * @since 2.0
 * @author Eike Stepper
 */
public abstract class LongIDStoreAccessor extends StoreAccessor
{
  protected LongIDStoreAccessor(Store store, ISession session)
  {
    super(store, session);
  }

  protected LongIDStoreAccessor(Store store, ITransaction transaction)
  {
    super(store, transaction);
  }

  /**
   * @since 4.0
   */
  @Override
  public LongIDStore getStore()
  {
    return (LongIDStore)super.getStore();
  }

  @Override
  protected CDOID getNextCDOID(CDORevision revision)
  {
    return getStore().getNextCDOID(this, revision);
  }
}
