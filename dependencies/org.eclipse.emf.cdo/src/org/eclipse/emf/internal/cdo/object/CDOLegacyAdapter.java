/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Fluegge - bug 247226: Transparently support legacy models
 */
package org.eclipse.emf.internal.cdo.object;

import org.eclipse.emf.cdo.CDONotification;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOLegacyAdapter extends CDOLegacyWrapper implements Adapter.Internal
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyAdapter.class);

  /**
   * @since 3.0
   */
  public CDOLegacyAdapter(InternalEObject object)
  {
    super(object);

    instance.eAdapters().add(this);
    ((org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList)instance.eAdapters())
        .addListener(new AdapterListListener());
  }

  public void setTarget(Notifier newTarget)
  {
    instance = (InternalEObject)newTarget;
  }

  public void unsetTarget(Notifier oldTarget)
  {
    if (instance == oldTarget)
    {
      instance = null;
    }
  }

  public Notifier getTarget()
  {
    return instance;
  }

  public boolean isAdapterForType(Object type)
  {
    return type == CDOLegacyAdapter.class;
  }

  public void notifyChanged(Notification msg)
  {
    if (msg.isTouch() || msg instanceof CDONotification)
    {
      return;
    }

    EStructuralFeature feature = (EStructuralFeature)msg.getFeature();
    if (view == null || feature == null || !(view instanceof CDOTransaction))
    {
      return;
    }

    if (EMFUtil.isPersistent(feature))
    {
      int eventType = msg.getEventType();
      int position = msg.getPosition();
      Object oldValue = msg.getOldValue();
      Object newValue = msg.getNewValue();

      switch (eventType)
      {
      case Notification.SET:
        notifySet(feature, position, oldValue, newValue);
        break;

      case Notification.UNSET:
        notifyUnset(feature, oldValue);
        break;

      case Notification.MOVE:
        notifyMove(feature, position, oldValue);
        break;

      case Notification.ADD:
        notifyAdd(feature, position, newValue);
        break;

      case Notification.ADD_MANY:
        notifyAddMany(feature, position, newValue);
        break;

      case Notification.REMOVE:
        notifyRemove(feature, position);
        break;

      case Notification.REMOVE_MANY:
        notifyRemoveMany(feature, oldValue);
        break;
      }

      // Align Container for bidirectional references because this is not set in the store. See Bugzilla_246622_Test
      instanceToRevisionContainment();
    }
  }

  protected void notifySet(EStructuralFeature feature, int position, Object oldValue, Object newValue)
  {
    CDOStore store = view.getStore();
    store.set(instance, feature, position, newValue);
    if (feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      if (reference.isContainment())
      {
        if (oldValue != null)
        {
          InternalEObject oldChild = (InternalEObject)oldValue;
          setContainer(store, oldChild, null, 0);
        }

        if (newValue != null)
        {
          InternalEObject newChild = (InternalEObject)newValue;
          setContainer(store, newChild, this, reference.getFeatureID());
        }
      }
    }
  }

  protected void notifyUnset(EStructuralFeature feature, Object oldValue)
  {
    CDOStore store = view.getStore();
    if (feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      if (reference.isContainment())
      {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>)oldValue;
        for (Object child : list)
        {
          if (child != null)
          {
            setContainer(store, (InternalEObject)child, null, 0);
          }
        }
      }
    }

    store.unset(instance, feature);
  }

  protected void notifyMove(EStructuralFeature feature, int position, Object oldValue)
  {
    CDOStore store = view.getStore();
    store.move(instance, feature, position, (Integer)oldValue);
  }

  protected void notifyAdd(EStructuralFeature feature, int position, Object newValue)
  {
    CDOStore store = view.getStore();
    store.add(instance, feature, position, newValue);
    if (newValue != null && feature instanceof EReference)
    {
      EReference reference = (EReference)feature;
      if (reference.isContainment())
      {
        InternalEObject newChild = (InternalEObject)newValue;
        setContainer(store, newChild, this, reference.getFeatureID());
      }
    }
  }

  protected void notifyAddMany(EStructuralFeature feature, int position, Object newValue)
  {
    CDOStore store = view.getStore();
    int pos = position;
    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>)newValue;
    for (Object object : list)
    {
      store.add(instance, feature, pos++, object);
      if (object != null && feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          InternalEObject newChild = (InternalEObject)object;
          setContainer(store, newChild, this, reference.getFeatureID());
        }
      }
    }
  }

  protected void notifyRemove(EStructuralFeature feature, int position)
  {
    CDOStore store = view.getStore();

    Object oldChild = store.remove(instance, feature, position);
    if (oldChild instanceof InternalEObject)
    {
      if (feature instanceof EReference)
      {
        EReference reference = (EReference)feature;
        if (reference.isContainment())
        {
          InternalEObject oldChildEObject = (InternalEObject)oldChild;
          setContainer(store, oldChildEObject, null, 0);
        }
      }
    }
  }

  protected void notifyRemoveMany(EStructuralFeature feature, Object oldValue)
  {
    CDOStore store = view.getStore();

    @SuppressWarnings("unchecked")
    List<Object> list = (List<Object>)oldValue;
    for (int i = list.size() - 1; i >= 0; --i)
    {
      Object oldChild = store.remove(instance, feature, i);
      if (oldChild instanceof InternalEObject)
      {
        if (feature instanceof EReference)
        {
          EReference reference = (EReference)feature;
          if (reference.isContainment())
          {
            InternalEObject oldChildEObject = (InternalEObject)oldChild;
            setContainer(store, oldChildEObject, null, 0);
          }
        }
      }
    }
  }

  private void setContainer(CDOStore store, InternalEObject object, InternalEObject container, int containingFeatureID)
  {
    if (object instanceof CDOObjectImpl)
    {
      // Don't touch native objects
      return;
    }

    if (FSMUtil.isTransient(CDOUtil.getCDOObject(object)))
    {
      // Don't touch transient objects
      return;
    }

    store.setContainer(object, null, container, InternalEObject.EOPPOSITE_FEATURE_BASE - containingFeatureID);
  }

  /**
   * @author Martin Flügge
   * @since 3.0
   */
  protected class AdapterListListener implements
      org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EObservableAdapterList.Listener
  {
    /**
     * @since 4.0
     */
    public AdapterListListener()
    {
    }

    public void added(Notifier notifier, Adapter adapter)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Added : {0} to {1} ", adapter, CDOLegacyAdapter.this); //$NON-NLS-1$
      }

      if (!FSMUtil.isTransient(CDOLegacyAdapter.this))
      {
        cdoView().handleAddAdapter(CDOLegacyAdapter.this, adapter);
      }
    }

    public void removed(Notifier notifier, Adapter adapter)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Removed : {0} from {1} ", adapter, CDOLegacyAdapter.this); //$NON-NLS-1$
      }

      if (!FSMUtil.isTransient(CDOLegacyAdapter.this))
      {
        cdoView().handleRemoveAdapter(CDOLegacyAdapter.this, adapter);
      }
    }
  }
}
