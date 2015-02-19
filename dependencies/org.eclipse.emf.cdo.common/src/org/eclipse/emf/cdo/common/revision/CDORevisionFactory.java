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
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.ecore.EClass;

/**
 * Creates {@link CDORevision revision} instances.
 * 
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDORevisionFactory
{
  /**
   * @since 3.0
   */
  public static final CDORevisionFactory DEFAULT = new CDORevisionFactory()
  {
    public CDORevision createRevision(EClass eClass)
    {
      return new org.eclipse.emf.cdo.internal.common.revision.CDORevisionImpl(eClass);
    }
  };

  /**
   * @since 3.0
   */
  public CDORevision createRevision(EClass eClass);
}
