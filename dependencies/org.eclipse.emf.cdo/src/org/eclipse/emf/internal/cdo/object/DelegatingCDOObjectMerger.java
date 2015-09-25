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
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDORevisionMerger;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * Delegating CDO object merger to visit and merge {@link CDOFeatureDelta feature deltas} based
 * on the wrapped {@link CDORevisionMerger merger}.
 * @author akitta
 */
public class DelegatingCDOObjectMerger extends CDOObjectMerger
{

  private final CDORevisionMerger merger;

  public DelegatingCDOObjectMerger(final CDORevisionMerger merger)
  {
    this.merger = merger;
  }

  @Override
  public synchronized void merge(final InternalCDOObject object, final CDORevisionDelta delta)
  {
    final InternalCDORevision oldRevision = object.cdoRevision();
    final InternalCDORevision revision = oldRevision.copy();
    object.cdoInternalSetRevision(revision);

    // NEW object should stay that state.
    if (object.cdoState() != CDOState.NEW)
    {
      object.cdoInternalSetState(CDOState.DIRTY);
    }

    merger.merge(revision, delta);
    object.cdoInternalPostLoad();
  }

}
