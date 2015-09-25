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
package org.eclipse.emf.cdo.internal.common.revision.delta;

import java.io.IOException;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class CDOClearFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOClearFeatureDelta
{
  public CDOClearFeatureDeltaImpl(EStructuralFeature feature)
  {
    super(feature);
  }

  public CDOClearFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
  }

  public Type getType()
  {
    return Type.CLEAR;
  }

  public CDOFeatureDelta copy()
  {
    return new CDOClearFeatureDeltaImpl(getFeature());
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).clear(getFeature());
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    return false;
  }

  @Override
  protected String toStringAdditional()
  {
    return null;
  }
}
