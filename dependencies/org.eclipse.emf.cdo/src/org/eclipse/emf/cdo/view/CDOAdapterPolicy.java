/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.CDOAdapter;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.ecore.EObject;

/**
 * A policy that allows to specify valid {@link Adapter} / {@link EObject} combinations.
 * 
 * @author Simon McDuff
 * @see CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
 * @see CDOView.Options#setStrongReferencePolicy(CDOAdapterPolicy)
 * @since 2.0
 */
public interface CDOAdapterPolicy
{
  /**
   * A default adapter policy that never triggers any special behaviour.
   */
  public static final CDOAdapterPolicy NONE = new CDOAdapterPolicy()
  {
    /**
     * Always returns <code>false</code>.
     */
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return false;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.1"); //$NON-NLS-1$
    }
  };

  /**
   * A default adapter policy that only triggers special behaviour if the adapter under test implements
   * {@link CDOAdapter}.
   */
  public static final CDOAdapterPolicy CDO = new CDOAdapterPolicy()
  {
    /**
     * Returns <code>true</code> if the given adapter implements {@link CDOAdapter}.
     */
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return adapter instanceof CDOAdapter;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.0"); //$NON-NLS-1$
    }
  };

  /**
   * A default adapter policy that always triggers special behaviour.
   */
  public static final CDOAdapterPolicy ALL = new CDOAdapterPolicy()
  {
    /**
     * Always returns <code>true</code>.
     */
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return true;
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOAdapterPolicy.2"); //$NON-NLS-1$
    }
  };

  /**
   * Returns <code>true</code> if the given adapter on the given object should trigger a certain operation or behaviour,
   * <code>false</code> otherwise.
   * 
   * @see CDOView.Options#addChangeSubscriptionPolicy(CDOAdapterPolicy)
   * @see CDOView.Options#setStrongReferencePolicy(CDOAdapterPolicy)
   */
  public boolean isValid(EObject eObject, Adapter adapter);
}
