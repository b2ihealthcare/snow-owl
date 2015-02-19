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
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDOObject;

import java.util.Set;

/**
 * A strategy used to customize the default conflict resolution behaviour of {@link CDOTransaction transactions}.
 * 
 * @see CDOTransaction.Options#addConflictResolver(CDOConflictResolver)
 * @author Eike Stepper
 * @since 2.0
 */
public interface CDOConflictResolver
{
  /**
   * Returns the {@link CDOTransaction transaction} this conflict resolver is associated with.
   */
  public CDOTransaction getTransaction();

  /**
   * Sets the {@link CDOTransaction transaction} this conflict resolver is to be associated with.
   */
  public void setTransaction(CDOTransaction transaction);

  /**
   * Resolves conflicts after remote invalidations arrived for objects that are locally dirty or detached.
   */
  public void resolveConflicts(Set<CDOObject> conflicts);
}
