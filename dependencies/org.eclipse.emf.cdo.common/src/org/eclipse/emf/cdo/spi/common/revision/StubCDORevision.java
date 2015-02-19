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

import java.io.IOException;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class StubCDORevision extends AbstractCDORevision
{
  public StubCDORevision(EClass eClass)
  {
    super(eClass);
  }

  public int compareTo(CDOBranchPoint o)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setID(CDOID id)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setVersion(int version)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setBranchPoint(CDOBranchPoint branchPoint)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setRevised(long revised)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setResourceID(CDOID resourceID)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setContainerID(Object containerID)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setContainingFeatureID(int containingFeatureID)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void add(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void clear(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object move(EStructuralFeature feature, int targetIndex, int sourceIndex)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object remove(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object set(EStructuralFeature feature, int index, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void unset(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object getValue(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object setValue(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void setList(EStructuralFeature feature, InternalCDOList list)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public CDOList getList(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public CDOList getList(EStructuralFeature feature, int size)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void read(CDODataInput in) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void write(CDODataOutput out, int referenceChunk) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  public void write(CDODataOutput out, int referenceChunk, CDOBranchPoint securityContext) throws IOException
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void convertEObjects(CDOIDProvider oidProvider)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public int getVersion()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public long getRevised()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public InternalCDORevisionDelta compare(CDORevision origin)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public void merge(CDORevisionDelta delta)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public InternalCDORevision copy()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public CDOID getID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public CDOBranch getBranch()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public long getTimeStamp()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public CDOID getResourceID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object getContainerID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public int getContainingFeatureID()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object get(EStructuralFeature feature, int index)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public int size(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public boolean isEmpty(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public boolean contains(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public int indexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public int lastIndexOf(EStructuralFeature feature, Object value)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public <T> T[] toArray(EStructuralFeature feature, T[] array)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public Object[] toArray(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  public int hashCode(EStructuralFeature feature)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.0
   */
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  public CDOPermission getPermission()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  public void setPermission(CDOPermission permission)
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.0
   */
  public void freeze()
  {
    throw new UnsupportedOperationException(getExceptionMessage());
  }

  /**
   * @since 4.1
   */
  public boolean isUnchunked()
  {
    return true;
  }

  /**
   * @since 4.1
   */
  public void setUnchunked()
  {
    // Do nothing
  }

  private String getExceptionMessage()
  {
    return "Unsupported operation in " + this;
  }
}
