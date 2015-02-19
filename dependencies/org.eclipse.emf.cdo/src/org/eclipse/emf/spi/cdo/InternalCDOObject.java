/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fl�gge - enhancements
 */
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry.CDOObjectMarker;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

/**
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOObject extends CDOObject, InternalEObject, InternalCDOLoadable, CDOObjectMarker
{
  public InternalCDOView cdoView();

  public InternalCDORevision cdoRevision();

  public void cdoInternalPostAttach();

  public void cdoInternalPostDetach(boolean remote);

  public void cdoInternalPostInvalidate();

  /**
   * @since 3.0
   */
  public void cdoInternalPostRollback();

  public void cdoInternalPreCommit();

  public void cdoInternalSetID(CDOID id);

  public void cdoInternalSetView(CDOView view);

  public void cdoInternalSetRevision(CDORevision revision);

  public CDOState cdoInternalSetState(CDOState state);

  public InternalEObject cdoInternalInstance();

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID);
}
