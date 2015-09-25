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
package org.eclipse.emf.cdo.internal.common.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 */
public class CDOClassInfoImpl extends AdapterImpl implements CDOClassInfo
{
  private static final int NOT_MAPPED = -1;

  private EStructuralFeature[] allPersistentFeatures;

  private int[] featureIDMappings;

  public CDOClassInfoImpl()
  {
  }

  @Override
  public boolean isAdapterForType(Object type)
  {
    return type == CDOClassInfo.class;
  }

  @Override
  public void setTarget(Notifier newTarget)
  {
    init((EClass)newTarget);
    super.setTarget(newTarget);
  }

  public EClass getEClass()
  {
    return (EClass)getTarget();
  }

  public boolean isResource()
  {
    return CDOModelUtil.isResource(getEClass());
  }

  public boolean isResourceFolder()
  {
    return CDOModelUtil.isResourceFolder(getEClass());
  }

  public boolean isResourceNode()
  {
    return CDOModelUtil.isResourceNode(getEClass());
  }

  public EStructuralFeature[] getAllPersistentFeatures()
  {
    return allPersistentFeatures;
  }

  public int getFeatureIndex(EStructuralFeature feature)
  {
    int featureID = getEClass().getFeatureID(feature);
    return getFeatureIndex(featureID);
  }

  public int getFeatureIndex(int featureID)
  {
    int index = featureIDMappings[featureID];
    if (index == NOT_MAPPED)
    {
      throw new IllegalArgumentException("Feature not mapped: " + getEClass().getEStructuralFeature(featureID)); //$NON-NLS-1$
    }

    return index;
  }

  private void init(EClass eClass)
  {
    List<EStructuralFeature> persistentFeatures = new ArrayList<EStructuralFeature>();
    EList<EStructuralFeature> allFeatures = eClass.getEAllStructuralFeatures();
    for (EStructuralFeature feature : allFeatures)
    {
      if (EMFUtil.isPersistent(feature))
      {
        persistentFeatures.add(feature);
      }
    }

    allPersistentFeatures = persistentFeatures.toArray(new EStructuralFeature[persistentFeatures.size()]);
    featureIDMappings = new int[allFeatures.size()];
    Arrays.fill(featureIDMappings, NOT_MAPPED);

    for (int i = 0; i < allPersistentFeatures.length; i++)
    {
      EStructuralFeature feature = allPersistentFeatures[i];
      int featureID = eClass.getFeatureID(feature);
      featureIDMappings[featureID] = i;
    }
  }

  @Override
  public String toString()
  {
    return getEClass().toString();
  }
}
