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
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 *    Caspar De Groot - bug 341081
 */
package org.eclipse.emf.cdo.internal.common.revision;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.spi.common.revision.BaseCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class CDORevisionImpl extends BaseCDORevision
{
  private Object[] values;

  public CDORevisionImpl(EClass eClass)
  {
    super(eClass);
  }

  protected CDORevisionImpl(CDORevisionImpl source)
  {
    super(source);
    EStructuralFeature[] features = CDOModelUtil.getAllPersistentFeatures(getEClass());
    initValues(features);
    for (int i = 0; i < features.length; i++)
    {
      EStructuralFeature feature = features[i];
      EClassifier classifier = feature.getEType();
      if (feature.isMany())
      {
        InternalCDOList sourceList = (InternalCDOList)source.values[i];
        if (sourceList != null)
        {
          setValue(i, sourceList.clone(classifier));
        }
      }
      else
      {
        CDOType type = CDOModelUtil.getType(feature);
        setValue(i, type.copyValue(source.values[i]));
      }
    }
  }

  public InternalCDORevision copy()
  {
    return new CDORevisionImpl(this);
  }

  @Override
  protected void initValues(EStructuralFeature[] allPersistentFeatures)
  {
    values = new Object[allPersistentFeatures.length];
  }

  @Override
  protected Object doGetValue(int featureIndex)
  {
    return values[featureIndex];
  }

  @Override
  protected void doSetValue(int featureIndex, Object value)
  {
    values[featureIndex] = value;
  }
}
