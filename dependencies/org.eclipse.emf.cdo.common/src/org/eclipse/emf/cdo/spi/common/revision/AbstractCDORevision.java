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
 *    Simon McDuff - bug 212958
 *    Simon McDuff - bug 213402
 */
package org.eclipse.emf.cdo.spi.common.revision;

import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.model.CDOClassInfo;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class AbstractCDORevision implements InternalCDORevision
{
  private CDOClassInfo classInfo;

  /**
   * @since 3.0
   */
  protected AbstractCDORevision(EClass eClass)
  {
    if (eClass != null)
    {
      if (eClass.isAbstract())
      {
        throw new IllegalArgumentException(MessageFormat.format(Messages.getString("AbstractCDORevision.0"), eClass)); //$NON-NLS-1$
      }

      classInfo = CDOModelUtil.getClassInfo(eClass);
    }
  }

  /**
   * @since 3.0
   */
  public CDOClassInfo getClassInfo()
  {
    return classInfo;
  }

  public EClass getEClass()
  {
    CDOClassInfo classInfo = getClassInfo();
    if (classInfo != null)
    {
      return classInfo.getEClass();
    }

    return null;
  }

  public boolean isResourceNode()
  {
    return getClassInfo().isResourceNode();
  }

  public boolean isResourceFolder()
  {
    return getClassInfo().isResourceFolder();
  }

  public boolean isResource()
  {
    return getClassInfo().isResource();
  }

  public CDORevisionData data()
  {
    return this;
  }

  public CDORevision revision()
  {
    return this;
  }

  /**
   * @since 3.0
   */
  public boolean isHistorical()
  {
    return getRevised() != UNSPECIFIED_DATE;
  }

  public boolean isValid(long timeStamp)
  {
    long startTime = getTimeStamp();
    long endTime = getRevised();
    return CDOCommonUtil.isValidTimeStamp(timeStamp, startTime, endTime);
  }

  /**
   * @since 4.0
   */
  public boolean isValid(CDOBranchPoint branchPoint)
  {
    return getBranch() == branchPoint.getBranch() && isValid(branchPoint.getTimeStamp());
  }

  /**
   * @since 4.1
   */
  public boolean isReadable()
  {
    return getPermission().isReadable();
  }

  /**
   * @since 4.1
   */
  public boolean isWritable()
  {
    return getPermission().isWritable();
  }

  /**
   * @since 3.0
   */
  public void adjustForCommit(CDOBranch branch, long timeStamp)
  {
    if (ObjectUtil.equals(branch, getBranch()))
    {
      // Same branch, increase version
      setVersion(getVersion() + 1);
    }
    else
    {
      // Different branch, start with v1
      setVersion(FIRST_VERSION);
    }

    setBranchPoint(branch.getPoint(timeStamp));
    setRevised(UNSPECIFIED_DATE);
  }

  @Override
  public int hashCode()
  {
    return getID().hashCode() ^ getBranch().hashCode() ^ getVersion();
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDORevision)
    {
      CDORevision that = (CDORevision)obj;
      return getID().equals(that.getID()) && getBranch().equals(that.getBranch()) && getVersion() == that.getVersion();
    }

    return false;
  }

  @Override
  public String toString()
  {
    EClass eClass = getEClass();
    String name = eClass == null ? "Revision" : eClass.getName();

    CDOBranch branch = getBranch();
    if (branch == null)
    {
      return name + "@" + getID() + "v" + getVersion();
    }

    return name + "@" + getID() + ":" + branch.getID() + "v" + getVersion();
  }

  /**
   * @since 3.0
   */
  protected void setClassInfo(CDOClassInfo classInfo)
  {
    this.classInfo = classInfo;
  }

  /**
   * @since 3.0
   */
  protected EStructuralFeature[] getAllPersistentFeatures()
  {
    return classInfo.getAllPersistentFeatures();
  }

  /**
   * @since 3.0
   */
  protected int getFeatureIndex(EStructuralFeature feature)
  {
    return classInfo.getFeatureIndex(feature);
  }
}
