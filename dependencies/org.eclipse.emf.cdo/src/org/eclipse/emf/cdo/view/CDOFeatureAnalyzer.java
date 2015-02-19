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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;

import org.eclipse.emf.internal.cdo.analyzer.NOOPFeatureAnalyzer;

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A call-back interface that is called by a {@link CDOView view} on each model read access.
 * 
 * @author Simon McDuff
 * @since 2.0
 * @apiviz.exclude
 */
public interface CDOFeatureAnalyzer
{
  public static final CDOFeatureAnalyzer NOOP = new NOOPFeatureAnalyzer();

  public void preTraverseFeature(CDOObject revision, EStructuralFeature feature, int index);

  public void postTraverseFeature(CDOObject revision, EStructuralFeature feature, int index, Object value);
}
