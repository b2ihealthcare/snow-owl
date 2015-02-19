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
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDORevisionMerger extends CDOFeatureDeltaVisitorImpl
{
  private InternalCDORevision revision;

  public CDORevisionMerger()
  {
  }

  public synchronized void merge(InternalCDORevision revision, CDORevisionDelta delta)
  {
    this.revision = revision;
    delta.accept(this);
    revision = null;
  }

  @Override
  public void visit(CDOMoveFeatureDelta delta)
  {
    revision.move(delta.getFeature(), delta.getNewPosition(), delta.getOldPosition());
  }

  @Override
  public void visit(CDOAddFeatureDelta delta)
  {
    revision.add(delta.getFeature(), delta.getIndex(), delta.getValue());
  }

  @Override
  public void visit(CDORemoveFeatureDelta delta)
  {
    revision.remove(delta.getFeature(), delta.getIndex());
  }

  @Override
  public void visit(CDOSetFeatureDelta delta)
  {
    revision.set(delta.getFeature(), delta.getIndex(), delta.getValue());
  }

  @Override
  public void visit(CDOUnsetFeatureDelta delta)
  {
    revision.unset(delta.getFeature());
  }

  @Override
  public void visit(CDOClearFeatureDelta delta)
  {
    revision.clear(delta.getFeature());
  }

  @Override
  public void visit(CDOContainerFeatureDelta delta)
  {
    revision.setResourceID(delta.getResourceID());
    revision.setContainerID(delta.getContainerID());
    revision.setContainingFeatureID(delta.getContainerFeatureID());
  }
}
