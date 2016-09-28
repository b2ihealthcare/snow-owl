/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 * 		Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.common.protocol;

import java.io.IOException;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.commit.CDOCommitData;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDReference;
import org.eclipse.emf.cdo.common.lock.CDOLockChangeInfo;
import org.eclipse.emf.cdo.common.lock.CDOLockOwner;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.lock.IDurableLockingManager.LockArea;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.io.ExtendedDataInput;

/**
 * Provides I/O methods for reading various CDO data types and concepts from streams.
 *
 * @author Eike Stepper
 * @since 3.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDODataInput extends ExtendedDataInput
{
  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDOPackageUnit readCDOPackageUnit(ResourceSet resourceSet) throws IOException;

  public CDOPackageUnit[] readCDOPackageUnits(ResourceSet resourceSet) throws IOException;

  public CDOPackageUnit.Type readCDOPackageUnitType() throws IOException;

  public CDOPackageInfo readCDOPackageInfo() throws IOException;

  public CDOClassifierRef readCDOClassifierRef() throws IOException;

  public EClassifier readCDOClassifierRefAndResolve() throws IOException;

  public String readCDOPackageURI() throws IOException;

  public CDOType readCDOType() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDOBranch readCDOBranch() throws IOException;

  public CDOBranchPoint readCDOBranchPoint() throws IOException;

  public CDOBranchVersion readCDOBranchVersion() throws IOException;

  public CDOChangeSetData readCDOChangeSetData() throws IOException;

  public CDOCommitData readCDOCommitData() throws IOException;

  public CDOCommitInfo readCDOCommitInfo() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDOID readCDOID() throws IOException;

  /**
   * @since 4.0
   */
  public CDOIDReference readCDOIDReference() throws IOException;

  /**
   * @since 4.0
   */
  public CDOIDAndVersion readCDOIDAndVersion() throws IOException;

  /**
   * @since 4.0
   */
  public CDOIDAndBranch readCDOIDAndBranch() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public CDORevisionKey readCDORevisionKey() throws IOException;

  public CDORevision readCDORevision() throws IOException;

  /**
   * @since 4.1
   */
  public CDORevision readCDORevision(boolean freeze) throws IOException;

  /**
   * @since 4.0
   */
  public CDORevisable readCDORevisable() throws IOException;

  public CDOList readCDOList(EClass owner, EStructuralFeature feature) throws IOException;

  public Object readCDOFeatureValue(EStructuralFeature feature) throws IOException;

  public CDORevisionDelta readCDORevisionDelta() throws IOException;

  public CDOFeatureDelta readCDOFeatureDelta(EClass owner) throws IOException;

  /**
   * Read either a CDORevision or a primitive value.
   */
  public Object readCDORevisionOrPrimitive() throws IOException;

  /**
   * Read either a CDORevision, a primitive value or a EClass.
   */
  public Object readCDORevisionOrPrimitiveOrClassifier() throws IOException;

  // /////////////////////////////////////////////////////////////////////////////////////////////////

  public LockType readCDOLockType() throws IOException;

  /**
   * @since 4.1
   */
  public CDOLockChangeInfo readCDOLockChangeInfo() throws IOException;

  /**
   * @since 4.1
   */
  public CDOLockOwner readCDOLockOwner() throws IOException;

  /**
   * @since 4.1
   */
  public CDOLockState readCDOLockState() throws IOException;

  /**
   * @since 4.1
   */
  public LockArea readCDOLockArea() throws IOException;
  
  /**
   * @since 4.2
   */
  public CDOPackageRegistry getPackageRegistry();
  
}
