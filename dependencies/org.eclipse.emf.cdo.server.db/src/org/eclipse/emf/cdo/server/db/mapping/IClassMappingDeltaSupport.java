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
 */
package org.eclipse.emf.cdo.server.db.mapping;

import org.eclipse.emf.cdo.server.db.IDBStoreAccessor;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

/**
 * Interface which complements {@link IClassMapping} with methods to facilitate revision delta support.
 * 
 * @see IMappingStrategy#hasDeltaSupport()
 * @author Eike Stepper
 * @author Stefan Winkler
 * @since 2.0
 */
public interface IClassMappingDeltaSupport
{
  /**
   * Write a revision delta.
   * 
   * @param accessor
   *          the accessor to use.
   * @param delta
   *          the delta to write.
   * @param created
   *          the creation timestamp of the new version
   * @param monitor
   *          the monitor to report progress.
   */
  public void writeRevisionDelta(IDBStoreAccessor accessor, InternalCDORevisionDelta delta, long created,
      OMMonitor monitor);
}
