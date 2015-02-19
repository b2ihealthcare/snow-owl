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
package org.eclipse.emf.internal.cdo.analyzer;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class NOOPFeatureAnalyzer implements CDOFeatureAnalyzer
{
  public NOOPFeatureAnalyzer()
  {
  }

  public void preTraverseFeature(CDOObject eClass, EStructuralFeature feature, int index)
  {
  }

  public void postTraverseFeature(CDOObject eClass, EStructuralFeature feature, int index, Object value)
  {
  }
}
