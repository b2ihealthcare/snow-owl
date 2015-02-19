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

/**
 * Symbolic model constants commonly used in CDO.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOModelConstants
{
  public static final String RESOURCE_NODE_NAME_ATTRIBUTE = "name"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String CORE_PACKAGE_URI = "http://www.eclipse.org/emf/2002/Ecore"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String ROOT_CLASS_NAME = "EObject"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_PACKAGE_URI = "http://www.eclipse.org/emf/CDO/Eresource/4.0.0"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_NODE_CLASS_NAME = "CDOResourceNode"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_FOLDER_CLASS_NAME = "CDOResourceFolder"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String RESOURCE_CLASS_NAME = "CDOResource"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String TYPES_PACKAGE_URI = "http://www.eclipse.org/emf/CDO/Etypes/4.0.0"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String BLOB_CLASS_NAME = "Blob"; //$NON-NLS-1$

  /**
   * @since 4.0
   */
  public static final String CLOB_CLASS_NAME = "Clob"; //$NON-NLS-1$
}
