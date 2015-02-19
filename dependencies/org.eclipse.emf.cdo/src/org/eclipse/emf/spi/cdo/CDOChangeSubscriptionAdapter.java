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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.view.CDOAdapterPolicy;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EObject;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public class CDOChangeSubscriptionAdapter extends AdapterImpl
{
  private CDOView view;

  private CDOAdapterPolicy policy = new CDOAdapterPolicy()
  {
    public boolean isValid(EObject eObject, Adapter adapter)
    {
      return adapter == CDOChangeSubscriptionAdapter.this;
    }
  };

  private Set<CDOObject> notifiers = new HashSet<CDOObject>();

  public CDOChangeSubscriptionAdapter(CDOView view)
  {
    this.view = view;
    view.options().addChangeSubscriptionPolicy(policy);
  }

  public void dispose()
  {
    reset();
    view.options().removeChangeSubscriptionPolicy(policy);
    view = null;
  }

  public CDOView getView()
  {
    return view;
  }

  public Set<CDOObject> getNotifiers()
  {
    return notifiers;
  }

  public void attach(CDOObject notifier)
  {
    if (notifiers.add(notifier))
    {
      notifier.eAdapters().add(this);
    }
  }

  public void reset()
  {
    for (CDOObject notifier : notifiers)
    {
      notifier.eAdapters().remove(this);
    }

    notifiers.clear();
  }
}
