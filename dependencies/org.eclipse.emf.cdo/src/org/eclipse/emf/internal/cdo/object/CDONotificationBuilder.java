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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOContainerFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOFeatureDeltaVisitorImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.NotificationChainImpl;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 * @author Simon McDuff
 * @since 2.0
 */
public class CDONotificationBuilder extends CDOFeatureDeltaVisitorImpl
{
  private CDOView view;

  private InternalEObject object;

  private CDORevisionDelta revisionDelta;

  private NotificationChainImpl notification;

  private Set<CDOObject> detachedObjects;

  private InternalCDORevision oldRevision;

  /**
   * @since 3.0
   */
  public CDONotificationBuilder(CDOView view)
  {
    this.view = view;
  }

  /**
   * @since 3.0
   */
  public CDOView getView()
  {
    return view;
  }

  /**
   * @since 3.0
   */
  public synchronized NotificationChain buildNotification(InternalEObject object, InternalCDORevision oldRevision,
      CDORevisionDelta revisionDelta, Set<CDOObject> detachedObjects)
  {
    notification = new NotificationChainImpl();

    this.object = object;
    this.revisionDelta = revisionDelta;
    this.detachedObjects = detachedObjects;
    this.oldRevision = oldRevision;
    revisionDelta.accept(this);
    return notification;
  }

  public synchronized NotificationChain buildNotification(InternalCDOObject object, InternalCDORevision newRevision)
  {
    InternalCDORevision oldRevision = (InternalCDORevision)CDORevisionFactory.DEFAULT.createRevision(object.eClass());
    CDORevisionDelta revisionDelta = newRevision.compare(oldRevision);
    return buildNotification(object, oldRevision, revisionDelta, null);
  }

  @Override
  public void visit(CDOMoveFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    int oldPosition = delta.getOldPosition();
    int newPosition = delta.getNewPosition();
    Object oldValue = delta.getValue();
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.MOVE, getEFeatureID(feature), Integer.valueOf(oldPosition),
        oldValue, newPosition));
  }

  @Override
  public void visit(CDOAddFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    add(new CDODeltaNotificationImpl(object, Notification.ADD, getEFeatureID(feature), getOldValue(feature),
        delta.getValue(), delta.getIndex()));
  }

  @Override
  public void visit(CDORemoveFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    int index = delta.getIndex();

    Object oldValue = delta.getValue();
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.REMOVE, getEFeatureID(feature), oldValue, null, index));
  }

  @Override
  public void visit(CDOSetFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    Object oldValue = getOldValue(feature);
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.SET, getEFeatureID(feature), oldValue, delta.getValue(),
        delta.getIndex()));
  }

  @Override
  public void visit(CDOUnsetFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    Object oldValue = getOldValue(feature);
    if (oldValue instanceof CDOID)
    {
      CDOID oldID = (CDOID)oldValue;
      CDOObject object = findObjectByID(oldID);
      if (object != null)
      {
        oldValue = object;
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.UNSET, getEFeatureID(feature), oldValue, null));
  }

  @Override
  public void visit(CDOClearFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    Object oldValue = getOldValue(feature);
    if (oldValue instanceof List<?>)
    {
      @SuppressWarnings("unchecked")
      List<Object> list = (List<Object>)oldValue;
      if (!list.isEmpty())
      {
        list = new ArrayList<Object>(list); // Copy the list so that it.set() does not change the frozen oldRevision
        boolean changed = false;

        for (ListIterator<Object> it = list.listIterator(); it.hasNext();)
        {
          Object element = it.next();
          if (element instanceof CDOID)
          {
            CDOID id = (CDOID)element;
            CDOObject oldObject = findObjectByID(id);
            if (oldObject != null)
            {
              it.set(oldObject);
              changed = true;
            }
          }
        }

        if (changed)
        {
          oldValue = list;
        }
      }
    }

    add(new CDODeltaNotificationImpl(object, Notification.REMOVE_MANY, getEFeatureID(feature), oldValue, null));
  }

  private CDOObject findObjectByID(CDOID id)
  {
    CDOObject object = view.getObject(id, false);
    if (object == null)
    {
      object = findDetachedObjectByID(id);
    }

    return object;
  }

  private CDOObject findDetachedObjectByID(CDOID id)
  {
    if (detachedObjects != null)
    {
      for (CDOObject object : detachedObjects)
      {
        if (id.equals(object.cdoID()))
        {
          return object;
        }
      }
    }

    return null;
  }

  @Override
  public void visit(CDOContainerFeatureDelta delta)
  {
    Object oldValue = null;
    if (oldRevision != null)
    {
      oldValue = oldRevision.getContainerID();

      if (oldValue instanceof CDOID)
      {
        CDOID oldID = (CDOID)oldValue;
        CDOObject object = findObjectByID(oldID);
        if (object != null)
        {
          oldValue = object;
        }
      }

    }

    add(new CDODeltaNotificationImpl(object, Notification.SET, EcorePackage.eINSTANCE.eContainmentFeature(), oldValue,
        delta.getContainerID()));
  }

  protected void add(CDODeltaNotificationImpl newNotificaton)
  {
    newNotificaton.setRevisionDelta(revisionDelta);
    if (notification.add(newNotificaton))
    {
      int size = notification.size();
      if (size > 1)
      {
        CDODeltaNotificationImpl previousNotification = (CDODeltaNotificationImpl)notification.get(size - 2);

        // Ensure that previousNotification.next is set
        previousNotification.add(newNotificaton);
      }
    }
  }

  private int getEFeatureID(EStructuralFeature eFeature)
  {
    return object.eClass().getFeatureID(eFeature);
  }

  private Object getOldValue(EStructuralFeature feature)
  {
    if (oldRevision == null)
    {
      return null;
    }

    return oldRevision.getValue(feature);
  }
}
