/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.util;

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;
import org.eclipse.emf.cdo.transaction.CDOCommitContext;
import org.eclipse.emf.cdo.transaction.CDODefaultTransactionHandler;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.transaction.CDOTransactionHandler;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * Maintains the {@link Resource#isModified() modified state} of a CDO {@link CDOResource resource} with the help of a
 * {@link CDOTransactionHandler transaction handler}.
 * 
 * @author Victor Roldan Betancort
 * @since 4.0
 */
public class CDOModificationTrackingAdapter extends CDOLazyContentAdapter
{
  private CDOResource container;

  public CDOModificationTrackingAdapter(CDOResource resource)
  {
    container = resource;
    CDOTransaction transaction = ((InternalCDOView)resource.cdoView()).toTransaction();
    transaction.addTransactionHandler(new CDODefaultTransactionHandler()
    {
      @Override
      public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
      {
        container.setModified(false);
      }

      @Override
      public void rolledBackTransaction(CDOTransaction transaction)
      {
        if (!transaction.getLastSavepoint().wasDirty())
        {
          container.setModified(false);
        }
      }
    });
  }

  @Override
  public void notifyChanged(Notification notification)
  {
    if (notification.isTouch())
    {
      return;
    }

    if (notification instanceof CDONotification)
    {
      return;
    }

    // Listen to changes on Resources, only if its the Resource when this adapter is installed on
    Object notifier = notification.getNotifier();
    if (notifier == container)
    {
      // The only attribute that triggers modified = true is "contents". The rest are ignored.
      if (notification.getFeature() == EresourcePackage.Literals.CDO_RESOURCE__CONTENTS)
      {
        container.setModified(true);
      }
    }
    else if (!(notifier instanceof Resource))
    {
      container.setModified(true);
    }
  }
}
