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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Collections;
import java.util.List;

/**
 * A strategy interface to customize the {@link CDORevision revision} prefetching behaviour of a {@link CDOView view}.
 * 
 * @see Options#setRevisionPrefetchingPolicy(CDORevisionPrefetchingPolicy)
 * @author Simon McDuff
 * @since 2.0
 */
public interface CDORevisionPrefetchingPolicy
{
  public static final CDORevisionPrefetchingPolicy NO_PREFETCHING = new CDORevisionPrefetchingPolicy()
  {
    public List<CDOID> loadAhead(CDORevisionManager revisionManager, CDOBranchPoint branchPoint, EObject targetObject,
        EStructuralFeature feature, CDOList list, int accessIndex, CDOID accessID)
    {
      return Collections.emptyList();
    }
  };

  /**
   * @param revisionManager
   *          Lookup availability of objects in the cache with
   *          {@link CDORevisionManager#containsRevision(CDOID, CDOBranchPoint)}.
   * @param targetObject
   *          Container of the list
   * @return Should return a list of id's to be fetch.
   * @since 3.0
   */
  public List<CDOID> loadAhead(CDORevisionManager revisionManager, CDOBranchPoint branchPoint, EObject targetObject,
      EStructuralFeature feature, CDOList list, int accessIndex, CDOID accessID);
}
