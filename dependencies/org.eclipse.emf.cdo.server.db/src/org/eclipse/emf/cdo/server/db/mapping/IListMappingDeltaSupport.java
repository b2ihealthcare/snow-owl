/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - 271444: [DB] Multiple refactorings bug 271444
 *    Stefan Winkler - Bug 329025: [DB] Support branching for range-based mapping strategy
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;

/**
 * Interface to complement {@link IListMapping} in order to provide list delta processing support.
 * 
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IListMappingDeltaSupport
{
  /**
   * Process a set of CDOFeatureDeltas for a many-valued feature.
   * 
   * @param accessor
   *          the accessor to use
   * @param id
   *          the ID of the revision affected
   * @param oldVersion
   *          the original version of the revision
   * @param newVersion
   *          the new revision of the revision (after the change)
   * @param created
   *          the creation date for the new revision
   * @param delta
   *          the {@link CDOListFeatureDelta} which contains the list deltas.
   * @since 4.0
   */
  public void processDelta(IDBStoreAccessor accessor, CDOID id, int branchId, int oldVersion, int newVersion,
      long created, CDOListFeatureDelta delta);
}
