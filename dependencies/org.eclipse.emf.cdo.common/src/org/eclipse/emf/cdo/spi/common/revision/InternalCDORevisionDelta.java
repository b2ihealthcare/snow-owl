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
package org.eclipse.emf.cdo.spi.common.revision;

import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @since 2.0
 */
public interface InternalCDORevisionDelta extends CDORevisionDelta, CDOReferenceAdjustable
{
  /**
   * @since 3.0
   */
  public Map<EStructuralFeature, CDOFeatureDelta> getFeatureDeltaMap();

  public void addFeatureDelta(CDOFeatureDelta delta);

  /**
   * @since 3.0
   */
  public void setBranch(CDOBranch branch);

  /**
   * @since 3.0
   */
  public void setVersion(int version);

  /**
   * @since 4.0
   */
  public void setTarget(CDORevisable target);
}
