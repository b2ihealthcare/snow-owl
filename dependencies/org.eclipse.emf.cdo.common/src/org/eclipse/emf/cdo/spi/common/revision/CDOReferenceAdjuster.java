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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDTemp;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 * @since 4.0
 */
public interface CDOReferenceAdjuster
{
  /**
   * Adjusts the internal structure of an object (e.g: {@link CDORevision}). This is mainly used after committing a
   * transaction. {@link CDORevision} must replace {@link CDOIDTemp} for non-temporary {@link CDOID} with a mapped ID.
   * Only the internal structure knows how to do these modifications. This is important to consider using different
   * implementation of {@link CDOList}.
   */
  public Object adjustReference(Object id, EStructuralFeature feature, int index);
}
