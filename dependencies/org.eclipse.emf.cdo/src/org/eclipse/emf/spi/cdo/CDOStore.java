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
 *    Eike Stepper & Simon McDuff - bug 204890
 *    Simon McDuff - bug 246705
 *    Simon McDuff - bug 246622
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;

/**
 * A CDO specific version of an {@link EStore}.
 * <p>
 * CDORevisions need to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew && !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStore} always call {@link InternalCDOView#convertObjectToID(Object, boolean)} with
 * true.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOStore extends EStore
{
  /**
   * @since 2.0
   */
  public InternalCDOView getView();

  /**
   * @since 2.0
   */
  public void setContainer(InternalEObject eObject, CDOResource newResource, InternalEObject newEContainer,
      int newContainerFeatureID);

  public int getContainingFeatureID(InternalEObject eObject);

  /**
   * @since 2.0
   */
  public InternalEObject getResource(InternalEObject eObject);

  /**
   * @since 2.0
   */
  public Object resolveProxy(InternalCDORevision revision, EStructuralFeature feature, int index, Object value);

  /**
   * @since 3.0
   */
  public Object convertToCDO(InternalCDOObject object, EStructuralFeature feature, Object value);

  /**
   * @since 2.0
   */
  public Object convertToEMF(EObject eObject, InternalCDORevision revision, EStructuralFeature feature, int index,
      Object value);
}
