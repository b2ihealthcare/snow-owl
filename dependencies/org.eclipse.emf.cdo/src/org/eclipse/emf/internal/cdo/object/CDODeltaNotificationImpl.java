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
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDODeltaNotification;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDODeltaNotificationImpl extends ENotificationImpl implements CDODeltaNotification
{
  private CDORevisionDelta revisionDelta;

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, EStructuralFeature feature, Object oldValue,
      Object newValue)
  {
    super(getEObject(notifier), eventType, feature, oldValue, newValue);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
      Object newValue, int position)
  {
    super(getEObject(notifier), eventType, featureID, oldValue, newValue, position);
  }

  public CDODeltaNotificationImpl(InternalEObject notifier, int eventType, int featureID, Object oldValue,
      Object newValue)
  {
    super(getEObject(notifier), eventType, featureID, oldValue, newValue);
  }

  @Override
  public Object getNewValue()
  {
    Object object = super.getNewValue();
    return adapt(object);
  }

  @Override
  public Object getOldValue()
  {
    Object oldValue = super.getOldValue();
    if (oldValue == null && getEventType() == Notification.REMOVE_MANY)
    {
      Object feature = getFeature();
      if (feature instanceof EStructuralFeature)
      {
        EStructuralFeature structuralFeature = (EStructuralFeature)feature;
        if (structuralFeature.isMany())
        {
          return ECollections.emptyEList();
        }
      }
    }

    return adapt(oldValue);
  }

  public Object adapt(Object object)
  {
    if (object instanceof CDOID)
    {
      CDOID id = (CDOID)object;

      try
      {
        InternalCDOView view = getCDOObject().cdoView();
        object = view.getObject(id, true);
      }
      catch (ObjectNotFoundException ex)
      {
        object = null;
      }
    }

    if (object instanceof CDOObject)
    {
      object = CDOUtil.getEObject((EObject)object);
    }

    return object;
  }

  public boolean hasNext()
  {
    return next != null;
  }

  public CDORevisionDelta getRevisionDelta()
  {
    return revisionDelta;
  }

  public void setRevisionDelta(CDORevisionDelta revisionDelta)
  {
    this.revisionDelta = revisionDelta;
  }

  @Override
  public boolean merge(Notification notification)
  {
    // Do not merge at all. See bug 317144.
    return false;
  }

  private InternalCDOObject getCDOObject()
  {
    return (InternalCDOObject)CDOUtil.getCDOObject((EObject)getNotifier());
  }

  private static InternalEObject getEObject(InternalEObject notifier)
  {
    return (InternalEObject)CDOUtil.getEObject(notifier);
  }
}
