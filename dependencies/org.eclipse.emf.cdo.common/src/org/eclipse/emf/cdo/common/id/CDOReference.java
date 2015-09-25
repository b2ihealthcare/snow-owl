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
package org.eclipse.emf.cdo.common.id;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * Represents a reference from one object to another object, possibly {@link CDOID} or CDOObject typed.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.has {@link org.eclipse.emf.ecore.EStructuralFeature}
 */
public interface CDOReference<OBJECT>
{
  public static final int NO_INDEX = Notification.NO_INDEX;

  public OBJECT getTargetObject();

  public OBJECT getSourceObject();

  /**
   * Returns the source {@link EReference} or the source {@link EAttribute} if the source feature is a
   * {@link FeatureMap}.
   */
  public EStructuralFeature getSourceFeature();

  public int getSourceIndex();
}
