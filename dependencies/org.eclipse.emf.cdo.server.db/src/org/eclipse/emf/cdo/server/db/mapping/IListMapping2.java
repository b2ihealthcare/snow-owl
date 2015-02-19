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
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * Extension interface to {@link IListMapping}.
 * 
 * @author Eike Stepper
 * @since 4.1
 */
public interface IListMapping2 extends IListMapping
{
  public void addSimpleChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int index);

  public void addRangedChunkWhere(IDBStoreAccessor accessor, CDOID cdoid, StringBuilder builder, int fromIndex,
      int toIndex);
}
