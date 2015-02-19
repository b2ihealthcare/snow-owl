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
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;

/**
 * An empty default implementation of {@link CDOTransactionHandler1}.
 * 
 * @author Eike Stepper
 * @since 4.0
 */
public class CDODefaultTransactionHandler1 implements CDOTransactionHandler1
{
  protected CDODefaultTransactionHandler1()
  {
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    // Do nothing
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    // Do nothing
  }

  /**
   * This implementation does nothing. Clients may override to provide specialized behaviour.
   */
  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureChange)
  {
    // Do nothing
  }
}
