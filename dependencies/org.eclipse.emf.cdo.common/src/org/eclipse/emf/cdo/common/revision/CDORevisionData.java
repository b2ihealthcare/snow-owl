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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;

/**
 * Encapsulates the modeled information and the EMF system values of a {@link CDORevision revision}.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDORevisionData
{
  /**
   * The equivalent of <code>EStructuralFeatureImpl.NIL</code> (i.e. explicit <code>null</code>).
   * 
   * @since 3.0
   */
  public static final Object NIL = EStoreEObjectImpl.NIL;

  /**
   * @since 2.0
   */
  public CDORevision revision();

  public CDOID getResourceID();

  /**
   * @since 2.0
   */
  public Object getContainerID();

  /**
   * Provides the input to the calculation of the feature in the container revision that actually holds this revision.
   * <p>
   * <b>Usage Example:</b>
   * <p>
   * <code><pre>
   * CDORevision revision = ...;
   * CDORevision container = <i>Util.getRevision</i>(revision.data().getContainerID());
   * 
   * int containingFeatureID = revision.data().getContainingFeatureID();
   * 
   * EStructuralFeature feature = containingFeatureID <= InternalEObject.EOPPOSITE_FEATURE_BASE ?
   *     container.getEClass().getEStructuralFeature(InternalEObject.EOPPOSITE_FEATURE_BASE - containingFeatureID) :
   *     ((EReference)revision.getEClass().getEStructuralFeature(containingFeatureID)).getEOpposite();</pre></code>
   * 
   * @see BasicEObjectImpl#eContainingFeature()
   * @see #getContainerID()
   */
  public int getContainingFeatureID();

  /**
   * @since 2.0
   */
  public Object get(EStructuralFeature feature, int index);

  /**
   * @since 2.0
   */
  public int size(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public boolean isEmpty(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public boolean contains(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public int indexOf(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public int lastIndexOf(EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public <T> T[] toArray(EStructuralFeature feature, T[] array);

  /**
   * @since 2.0
   */
  public Object[] toArray(EStructuralFeature feature);

  /**
   * @since 2.0
   */
  public int hashCode(EStructuralFeature feature);

}
