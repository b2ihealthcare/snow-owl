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
package org.eclipse.emf.cdo.server;

/**
 * Creates CDO {@link IRepository repositories}.
 *
 * @author Eike Stepper
 * @apiviz.uses {@link IRepository} - - creates
 */
public interface IRepositoryFactory
{
  /**
   * @since 2.0
   */
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositories"; //$NON-NLS-1$

  public String getRepositoryType();

  public IRepository createRepository();
}
