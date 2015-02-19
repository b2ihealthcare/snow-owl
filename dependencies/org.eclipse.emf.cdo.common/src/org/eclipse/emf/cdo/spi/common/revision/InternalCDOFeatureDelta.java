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
package org.eclipse.emf.cdo.spi.common.revision;

import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * @author Simon McDuff
 * @since 3.0
 */
public interface InternalCDOFeatureDelta extends CDOFeatureDelta
{
  /**
   * @author Eike Stepper
   */
  public interface WithIndex
  {
    public void adjustAfterAddition(int index);

    public void adjustAfterRemoval(int index);
  }

  /**
   * @author Eike Stepper
   */
  public interface ListIndexAffecting
  {
    /**
     * Expects the number of indices in the first element of the indices array.
     */
    public void affectIndices(ListTargetAdding source[], int[] indices);
  }

  /**
   * @author Eike Stepper
   */
  public interface ListTargetAdding
  {
    /**
     * @since 4.0
     */
    public Object getValue();

    public int getIndex();

    public void clear();
  }
}
