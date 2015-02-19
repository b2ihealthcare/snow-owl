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

import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * A {@link CDOFeatureDelta feature delta} that represents a move of one element of a many-valued
 * {@link EStructuralFeature feature} to a different list position.
 * 
 * @author Simon McDuff
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOMoveFeatureDelta extends CDOFeatureDelta
{
  public int getOldPosition();

  public int getNewPosition();

  /**
   * @since 4.0
   */
  public Object getValue();
}
