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
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.util.InvalidObjectException;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.emf.spi.cdo.InternalCDOObject;

/**
 * A strategy interface to customize the invalidation behaviour of a {@link CDOView view}.
 * 
 * @see Options#setInvalidationPolicy(CDOInvalidationPolicy)
 * @author Eike Stepper
 * @since 3.0
 */
public interface CDOInvalidationPolicy
{
  public static final CDOInvalidationPolicy DEFAULT = new CDOInvalidationPolicy()
  {
    public void handleInvalidation(CDOObject object, CDORevisionKey key)
    {
      ((InternalCDOObject)object).cdoInternalSetRevision(null);
    }

    public void handleInvalidObject(CDOObject object)
    {
      throw new InvalidObjectException(object.cdoID(), object.cdoView());
    }
  };

  public static final CDOInvalidationPolicy RELAXED = new CDOInvalidationPolicy()
  {
    public void handleInvalidation(CDOObject object, CDORevisionKey key)
    {
      // Do nothing
    }

    public void handleInvalidObject(CDOObject object)
    {
      // Do nothing
    }
  };

  public void handleInvalidation(CDOObject object, CDORevisionKey key);

  public void handleInvalidObject(CDOObject object);
}
