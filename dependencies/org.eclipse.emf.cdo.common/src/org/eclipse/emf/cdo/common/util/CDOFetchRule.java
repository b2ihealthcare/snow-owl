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
package org.eclipse.emf.cdo.common.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents a subset of the {@link EStructuralFeature features} of a {@link EClass class}.
 * 
 * @author Simon McDuff
 * @since 3.0
 */
public final class CDOFetchRule
{
  private EClass eClass;

  private List<EStructuralFeature> features = new ArrayList<EStructuralFeature>(0);

  public CDOFetchRule(EClass eClass)
  {
    this.eClass = eClass;
  }

  public CDOFetchRule(CDODataInput in, CDOPackageRegistry packageManager) throws IOException
  {
    eClass = (EClass)in.readCDOClassifierRefAndResolve();
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      int featureID = in.readInt();
      EStructuralFeature feature = eClass.getEStructuralFeature(featureID);
      features.add(feature);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOClassifierRef(eClass);
    out.writeInt(features.size());
    for (EStructuralFeature feature : features)
    {
      out.writeInt(feature.getFeatureID());
    }
  }

  public EClass getEClass()
  {
    return eClass;
  }

  public List<EStructuralFeature> getFeatures()
  {
    return features;
  }

  public void addFeature(EStructuralFeature feature)
  {
    features.add(feature);
  }

  public void removeFeature(EStructuralFeature feature)
  {
    features.remove(feature);
  }

  public boolean isEmpty()
  {
    return features.isEmpty();
  }
}
