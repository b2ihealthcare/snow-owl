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
package org.eclipse.emf.cdo.internal.common.revision.delta;

import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class CDODetachedRevisionDeltaImpl implements CDORevisionDelta
{
  public CDODetachedRevisionDeltaImpl()
  {
  }

  public CDOID getID()
  {
    throw new UnsupportedOperationException();
  }

  public CDOBranch getBranch()
  {
    throw new UnsupportedOperationException();
  }

  public int getVersion()
  {
    throw new UnsupportedOperationException();
  }

  public EClass getEClass()
  {
    throw new UnsupportedOperationException();
  }

  public CDORevisable getTarget()
  {
    throw new UnsupportedOperationException();
  }

  public boolean isEmpty()
  {
    throw new UnsupportedOperationException();
  }

  public CDORevisionDelta copy()
  {
    return this;
  }

  public CDOFeatureDelta getFeatureDelta(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException();
  }

  public List<CDOFeatureDelta> getFeatureDeltas()
  {
    throw new UnsupportedOperationException();
  }

  public void apply(CDORevision revision)
  {
    throw new UnsupportedOperationException();
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    throw new UnsupportedOperationException();
  }
}
