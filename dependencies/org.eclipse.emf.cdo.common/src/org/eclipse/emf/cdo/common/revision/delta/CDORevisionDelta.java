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
 */
package org.eclipse.emf.cdo.common.revision.delta;

import java.util.List;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDODetachedRevisionDeltaImpl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents the change delta between two {@link CDORevision revisions} of a CDO object. The detailed
 * {@link CDOFeatureDelta feature deltas} are returned by the {@link #getFeatureDeltas()} method.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link org.eclipse.emf.ecore.EClass}
 * @apiviz.has {@link org.eclipse.emf.cdo.common.revision.CDORevisable} oneway - - target
 * @apiviz.composedOf {@link CDOFeatureDelta}
 */
public interface CDORevisionDelta extends CDORevisionKey
{
  /**
   * This constant is only passed into conflict resolvers to indicate that a conflict was caused by remote detachment of
   * an object. Calling any method on this marker instance will result in an {@link UnsupportedOperationException} being
   * thrown.
   * 
   * @since 4.0
   */
  public static final CDORevisionDelta DETACHED = new CDODetachedRevisionDeltaImpl();

  /**
   * @since 3.0
   */
  public EClass getEClass();

  /**
   * @since 4.0
   */
  public CDORevisable getTarget();

  /**
   * @since 3.0
   */
  public boolean isEmpty();

  /**
   * @since 4.0
   */
  public CDORevisionDelta copy();

  /**
   * @since 4.0
   */
  public CDOFeatureDelta getFeatureDelta(EStructuralFeature feature);

  public List<CDOFeatureDelta> getFeatureDeltas();

  /**
   * Applies the {@link #getFeatureDeltas() feature deltas} in this revision delta to the {@link CDORevisionData data}
   * of the given revision.
   * <p>
   * The system data of the given revision, e.g. {@link CDOBranchPoint branch point} or {@link CDOBranchVersion branch
   * version} of the given revision are <b>not</b> modified.
   */
  public void apply(CDORevision revision);

  public void accept(CDOFeatureDeltaVisitor visitor);
}
