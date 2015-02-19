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
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotifierImpl;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents a change delta between two values of a single {@link EStructuralFeature feature}.
 *
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link org.eclipse.emf.ecore.EStructuralFeature}
 * @apiviz.has {@link CDOFeatureDelta.Type}
 */
public interface CDOFeatureDelta
{
  /**
   * @since 4.0
   */
  public static final int NO_INDEX = Notification.NO_INDEX;

  /**
   * @since 3.0
   */
  public static final Object UNKNOWN_VALUE = new NotifierImpl()
  {
    @Override
    public String toString()
    {
      return "UNKNOWN"; //$NON-NLS-1$
    }
  };

  public Type getType();

  /**
   * @since 2.0
   */
  public EStructuralFeature getFeature();

  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);

  /**
   * @since 3.0
   */
  public CDOFeatureDelta copy();

  /**
   * @since 4.0
   */
  public boolean isStructurallyEqual(Object obj);

  /**
   * Enumerates the possible types of {@link CDOFeatureDelta feature deltas}.
   *
   * @author Simon McDuff
   */
  public enum Type
  {
    ADD, REMOVE, CLEAR, MOVE, SET, UNSET, LIST, CONTAINER
  }
}
