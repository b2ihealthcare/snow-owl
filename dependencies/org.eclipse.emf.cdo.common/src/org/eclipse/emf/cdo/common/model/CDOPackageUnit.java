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
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;

/**
 * Represents a tree structure of nested {@link EPackage packages} that are registered with a {@link CDOPackageRegistry
 * package registry} and that can only be serialized as a whole.
 * <p>
 * A package unit is the granule of committing or lazy loading packages. It contains some overall information like
 * {@link Type type}, {@link State state}, {@link #getTimeStamp() commit time} and nested {@link CDOPackageInfo package
 * info} objects that describe all the nested packages.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link CDOPackageUnit.Type}
 * @apiviz.has {@link CDOPackageUnit.State}
 * @apiviz.composedOf {@link CDOPackageInfo}
 */
public interface CDOPackageUnit extends Comparable<CDOPackageUnit>
{
  /**
   * Returns the package registry this package unit is managed by.
   */
  public CDOPackageRegistry getPackageRegistry();

  /**
   * Returns the ID of this package unit.
   * <p>
   * Same as {@link #getTopLevelPackageInfo()}. {@link CDOPackageInfo#getPackageURI()}.
   */
  public String getID();

  /**
   * Returns the current state of this package unit.
   */
  public State getState();

  /**
   * Returns the current type of this package unit.
   */
  public Type getType();

  /**
   * Returns the type of this package unit as it was at the time it was originally committed by a client.
   */
  public Type getOriginalType();

  /**
   * Returns the time this package unit was originally committed.
   */
  public long getTimeStamp();

  /**
   * Returns the {@link CDOPackageInfo package info} object that describes the top level {@link EPackage package} of the
   * nested package tree structure described by this package unit.
   */
  public CDOPackageInfo getTopLevelPackageInfo();

  /**
   * Returns the {@link CDOPackageInfo package info} object that describes the {@link EPackage package} with the given
   * namespace URI, or <code>null</code> if this package unit does not contain a package with this URI.
   */
  public CDOPackageInfo getPackageInfo(String packageURI);

  /**
   * Returns all {@link CDOPackageInfo package info} objects of the nested package tree structure described by this
   * package unit in depth-first traversal order.
   */
  public CDOPackageInfo[] getPackageInfos();

  /**
   * Returns all {@link EPackage packages} of the nested package tree structure described by this package unit in
   * depth-first traversal order.
   * 
   * @param loadOnDemand
   *          If <code>true</code> and this package unit is not {@link State#LOADED LOADED} the package unit is
   *          implicitely loaded. If <code>false</code> and this package unit is not {@link State#LOADED LOADED} an
   *          empty array is returned.
   */
  public EPackage[] getEPackages(boolean loadOnDemand);

  /**
   * Returns <code>true</code> is this package unit describes one of the models <i>Ecore</i>, <i>Eresource</i> or
   * <i>Etypes</i>, <code>false</code> otherwise.
   * <p>
   * Note that the models <i>Ecore</i>, <i>Eresource</i> and <i>Etypes</i> are expected to be present as generated
   * {@link Type#NATIVE NATIVE} models in all deployments.
   */
  public boolean isSystem();

  /**
   * Returns <code>true</code> is this package unit describes the model <i>Eresource</i> , <code>false</code> otherwise.
   * <p>
   * Note that the model <i>Eresource</i> is expected to bepresent as generated {@link Type#NATIVE NATIVE} models in all
   * deployments.
   * 
   * @since 4.0
   */
  public boolean isResource();

  /**
   * Describes the possible states a {@link CDOPackageUnit package unit} may be in during its lifecycle.
   * 
   * @author Eike Stepper
   */
  public enum State
  {
    /**
     * The state of a {@link CDOPackageUnit package unit} after one of its described {@link EPackage packages} is newly
     * attached to a transactional {@link CDOPackageRegistry package registry}, but before the associated transaction is
     * committed. A {@link #NEW} package unit can only transition to {@link #LOADED} or {@link #DISPOSED}.
     */
    NEW,

    /**
     * The state of a {@link CDOPackageUnit package unit} after the described {@link EPackage packages} are loaded or
     * wired from the {@link Registry#INSTANCE global package registry}. A {@link #LOADED} package unit can only
     * transition to {@link #DISPOSED}.
     */
    LOADED,

    /**
     * The state of a {@link CDOPackageUnit package unit} after the context of the associated {@link CDOPackageRegistry
     * package registry} has been initialized, that is the repository been started or the session been opened. A
     * {@link #PROXY} package unit can only transition to {@link #LOADED} or {@link #DISPOSED}.
     */
    PROXY,

    /**
     * The state of a {@link CDOPackageUnit package unit} after the associated {@link CDOPackageRegistry package
     * registry} has been deactivated, that is the repository been stopped or the session been closed. A
     * {@link #DISPOSED} package unit can not transition to any other state.
     */
    DISPOSED
  }

  /**
   * Describes the instances of {@link EClass classes} of a {@link CDOPackageUnit package unit}.
   * 
   * @author Eike Stepper
   */
  public enum Type
  {
    /**
     * The type of models that are generated specifically for the usage with CDO. Instances of {@link EClass classes} of
     * these models can be directly cast to InternalCDOObject.
     */
    NATIVE,

    /**
     * The type of models that are <b>not</b> generated specifically for the usage with CDO. Instances of {@link EClass
     * classes} of these models can <b>not</b> be directly cast to InternalCDOObject.
     */
    LEGACY,

    /**
     * The type of models that are not generated <b>at all</b> but rather dynamically contructed at runtime. Instances
     * of {@link EClass classes} of these models <b>can</b> be directly cast to InternalCDOObject, i.e. they're
     * implicitely <i>native</i>.
     */
    DYNAMIC,

    /**
     * Used to indicate that the type of a model could not be determined. Refer to the
     * {@link CDOPackageTypeRegistry#INSTANCE package type registry} on how to deal with this scenario.
     */
    UNKNOWN;

    /**
     * Returns <code>true</code> if this type is either {@link #NATIVE} or {@link #LEGACY}, <code>false</code>
     * otherwise.
     */
    public boolean isGenerated()
    {
      checkNotUnknown();
      return this == NATIVE || this == LEGACY;
    }

    /**
     * @throws IllegalStateException
     *           if this type is {@link #UNKNOWN}.
     */
    public void checkNotUnknown() throws IllegalStateException
    {
      if (this == UNKNOWN)
      {
        throw new IllegalStateException(Messages.getString("CDOPackageUnit.0")); //$NON-NLS-1$
      }
    }
  }
}
