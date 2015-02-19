/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.transaction;

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * An abstract call-back class that is called by a {@link CDOTransaction transcation} after {@link CDOObject objects} have been
 * attached, modified or detached.
 *
 * @see CDOTransactionHandler1
 * @author Martin Fluegge
 * @since 4.1
 */
public abstract class CDOPostEventTransactionHandler implements CDOTransactionHandler
{
  private final Adapter ATTACHED_ADAPTER = new PostEventAdapter()
  {
    @Override
    protected void postEvent(CDOTransaction transaction, CDOObject object, Notification msg)
    {
      attachedObject(transaction, object, msg);
    }
  };

  private final Adapter MODIFIED_ADAPTER = new PostEventAdapter()
  {
    @Override
    protected void postEvent(CDOTransaction transaction, CDOObject object, Notification msg)
    {
      modifiedObject(transaction, object, msg);
    }
  };

  private final Adapter DEATTACHED_ADAPTER = new PostEventAdapter()
  {
    @Override
    protected void postEvent(CDOTransaction transaction, CDOObject object, Notification msg)
    {
      detachedObject(transaction, object, msg);
    }
  };

  public CDOPostEventTransactionHandler()
  {
  }

  public void attachingObject(CDOTransaction transaction, CDOObject object)
  {
    object.eAdapters().add(ATTACHED_ADAPTER);
  }

  public void modifyingObject(CDOTransaction transaction, CDOObject object, CDOFeatureDelta featureDelta)
  {
    object.eAdapters().add(MODIFIED_ADAPTER);
  }

  public void detachingObject(CDOTransaction transaction, CDOObject object)
  {
    object.eAdapters().add(DEATTACHED_ADAPTER);
  }

  public void committingTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
  }

  public void committedTransaction(CDOTransaction transaction, CDOCommitContext commitContext)
  {
  }

  public void rolledBackTransaction(CDOTransaction transaction)
  {
  }

  protected abstract void attachedObject(CDOTransaction transaction, CDOObject object, Notification msg);

  protected abstract void modifiedObject(CDOTransaction transaction, CDOObject object, Notification msg);

  protected abstract void detachedObject(CDOTransaction transaction, CDOObject object, Notification msg);

  /**
   * An empty default implementation of {@link CDOPostEventTransactionHandler}.
   *
   * @author Eike Stepper
   */
  public static class Default extends CDOPostEventTransactionHandler
  {
    @Override
    protected void attachedObject(CDOTransaction transaction, CDOObject object, Notification msg)
    {
    }

    @Override
    protected void modifiedObject(CDOTransaction transaction, CDOObject object, Notification msg)
    {
    }

    @Override
    protected void detachedObject(CDOTransaction transaction, CDOObject object, Notification msg)
    {
    }
  }

  /**
   * @author Martin Fluegge
   */
  private static abstract class PostEventAdapter extends AdapterImpl
  {
    @Override
    public void notifyChanged(Notification msg)
    {
      if (msg instanceof CDONotification)
      {
        return;
      }

      if (isModifyingEvent(msg.getEventType()))
      {
        Object notifier = msg.getNotifier();
        if (notifier instanceof EObject)
        {
          CDOObject object = CDOUtil.getCDOObject((EObject)notifier);

          // Avoid duplicate notifications
          EList<Adapter> adapters = object.eAdapters();
          if (adapters.contains(this))
          {
            postEvent((CDOTransaction)object.cdoView(), object, msg);

            boolean eDeliver = object.eDeliver();

            try
            {
              object.eSetDeliver(false);
              adapters.remove(this);
            }
            finally
            {
              object.eSetDeliver(eDeliver);
            }
          }
        }
      }
    }

    private boolean isModifyingEvent(int eventType)
    {
      switch (eventType)
      {
      case Notification.ADD:
      case Notification.ADD_MANY:
      case Notification.MOVE:
      case Notification.REMOVE:
      case Notification.REMOVE_MANY:
      case Notification.SET:
      case Notification.UNSET:
        return true;

      default:
        return false;
      }
    }

    protected abstract void postEvent(CDOTransaction transaction, CDOObject object, Notification msg);
  }
}
