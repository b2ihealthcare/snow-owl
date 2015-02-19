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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * An EMF {@link Adapter adapter} that encapsulates CDO specific information about an {@link EClass}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link org.eclipse.emf.ecore.EClass}
 */
public interface CDOClassInfo extends Adapter
{
  public EClass getEClass();

  public boolean isResourceNode();

  public boolean isResourceFolder();

  public boolean isResource();

  public EStructuralFeature[] getAllPersistentFeatures();

  public int getFeatureIndex(EStructuralFeature feature);

  public int getFeatureIndex(int featureID);
}
