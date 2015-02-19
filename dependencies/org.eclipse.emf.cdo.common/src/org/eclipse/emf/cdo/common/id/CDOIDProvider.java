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
package org.eclipse.emf.cdo.common.id;

/**
 * Provides the {@link CDOID IDs} of passed objects.
 * 
 * @author Eike Stepper
 * @apiviz.uses {@link CDOID} - - provides
 */
public interface CDOIDProvider
{
  /**
   * @since 3.0
   */
  public static final CDOIDProvider NOOP = new CDOIDProvider()
  {
    public CDOID provideCDOID(Object id)
    {
      return (CDOID)id;
    }
  };

  public CDOID provideCDOID(Object idOrObject);
}
