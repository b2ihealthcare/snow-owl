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

import org.eclipse.emf.cdo.common.model.CDOPackageUnit.State;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Descriptor;

/**
 * Describes a single {@link EPackage package } instance of the nested package tree strucure represented by the
 * containing {@link CDOPackageUnit package unit}.
 * <p>
 * While the containing package unit is in the {@link CDOPackageUnit.State#PROXY PROXY} state this package info acts as
 * a {@link Descriptor package descriptor} in the associated {@link CDOPackageRegistry package registry}. When that
 * package unit is loaded all contained package infos/descriptors are resolved and replaced by their actual packages. At
 * the same time the describing package info objects are attached as adapters to the resolved packages. This way the
 * descriptive information is available before and after loading the packages.
 * 
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 * @apiviz.landmark
 * @apiviz.has {@link org.eclipse.emf.ecore.EPackage}
 * @apiviz.composedOf {@link CDOClassInfo}
 */
public interface CDOPackageInfo extends Adapter, EPackage.Descriptor, Comparable<CDOPackageInfo>
{
  /**
   * Returns the package unit containing this package info.
   */
  public CDOPackageUnit getPackageUnit();

  /**
   * Returns the namespace URI of the package described by this package info.
   */
  public String getPackageURI();

  /**
   * Returns the namespace URI of the {@link EPackage#getESuperPackage() super package} of the package described by this
   * package info.
   */
  public String getParentURI();

  /**
   * Returns the {@link EPackage package} described by this package info.
   * 
   * @param loadOnDemand
   *          If <code>true</code> and the containing {@link CDOPackageUnit package unit} is not {@link State#LOADED
   *          LOADED} the package unit is implicitely loaded. If <code>false</code> and this package unit is not
   *          {@link State#LOADED LOADED} <code>null</code> is returned.
   */
  public EPackage getEPackage(boolean loadOnDemand);

  /**
   * Returnes <code>true</code> if the package described by this package info is the <i>Ecore</i> model,
   * <code>false</code> oterwise.
   */
  public boolean isCorePackage();

  /**
   * Returnes <code>true</code> if the package described by this package info is the <i>Eresource</i> model,
   * <code>false</code> oterwise.
   */
  public boolean isResourcePackage();

  /**
   * Returnes <code>true</code> if the package described by this package info is the <i>Etypes</i> model,
   * <code>false</code> oterwise.
   * 
   * @since 4.0
   */
  public boolean isTypePackage();

  /**
   * Returns <code>true</code> is this package info describes one of the models <i>Ecore</i>, <i>Eresource</i> or
   * <i>Etypes</i>, <code>false</code> otherwise.
   * <p>
   * Note that the models <i>Ecore</i>, <i>Eresource</i> and <i>Etypes</i> are expected to present as generated
   * {@link CDOPackageUnit.Type#NATIVE NATIVE} models in all deployments.
   */
  public boolean isSystemPackage();
}
