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
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * @author Simon McDuff
 */
public class CDOObjectMerger extends CDORevisionMerger
{
  public CDOObjectMerger()
  {
  }

  /**
   * @since 2.0
   */
  public synchronized void merge(InternalCDOObject object, CDORevisionDelta delta)
  {
    InternalCDORevision oldRevision = object.cdoRevision();
    InternalCDORevision revision = oldRevision.copy();
    object.cdoInternalSetRevision(revision);

    // NEW object should stay that state.
    if (object.cdoState() != CDOState.NEW)
    {
      object.cdoInternalSetState(CDOState.DIRTY);
    }

    merge(revision, delta);
    object.cdoInternalPostLoad();
  }
}
