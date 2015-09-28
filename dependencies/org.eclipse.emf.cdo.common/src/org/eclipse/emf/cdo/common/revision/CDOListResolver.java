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

/**
 * A strategy that specifies which list elememts must be present (loaded) in a {@link CDOID} list of a
 * {@link CDORevision revision} when a certain list index is accessed. Implementations of this interface can control the
 * exact characteristics of a certain <em>partial collection loading</em> strategy.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @apiviz.uses {@link CDOList} - - resolves
 */
public interface CDOListResolver
{
  /**
   * Defines a strategy to be used when the collection needs to resolve one element.
   */
  public Object resolveProxy(CDORevision revision, EStructuralFeature feature, int accessIndex, int serverIndex);

  /**
   * Defines a strategy to be used when the collection needs to resolve all elements.
   */
  public void resolveAllProxies(CDORevision revision, EStructuralFeature feature);
}
