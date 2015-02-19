/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDOLock;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;
import org.eclipse.emf.internal.cdo.object.CDOLockImpl;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.concurrent.IRWLockManager.LockType;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.BasicEObjectImpl;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EStoreEObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Internal;
import org.eclipse.emf.ecore.util.DelegatingFeatureMap;
import org.eclipse.emf.ecore.util.EcoreEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOLoadable;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * The base class of all <em>native</em> {@link CDOObject objects}.
 *
 * @author Eike Stepper
 */
public class CDOObjectImpl extends EStoreEObjectImpl implements InternalCDOObject
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOObjectImpl.class);

  private CDOID id;

  private CDOState state;

  private InternalCDOView view;

  private InternalCDORevision revision;

  /**
   * CDO uses this list instead of eSettings for transient objects. EMF uses eSettings as cache. CDO deactivates the
   * cache but EMF still used eSettings to store list wrappers. CDO needs another place to store the real list with the
   * actual data (transient mode) and accessible through EStore. This allows CDO to always use the same instance of the
   * list wrapper.
   */
  private transient Object[] cdoSettings;

  public CDOObjectImpl()
  {
    state = CDOState.TRANSIENT;
    eContainer = null;
    cdoSettings = null;
  }

  public CDOID cdoID()
  {
    return id;
  }

  public CDOState cdoState()
  {
    return state;
  }

  /**
   * @since 2.0
   */
  public InternalCDORevision cdoRevision()
  {
    return revision;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView cdoView()
  {
    return view;
  }

  public CDOResourceImpl cdoResource()
  {
    Resource resource = eResource();
    if (resource instanceof CDOResourceImpl)
    {
      return (CDOResourceImpl)resource;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public CDOResourceImpl cdoDirectResource()
  {
    Resource.Internal resource = eDirectResource();
    if (resource instanceof CDOResourceImpl)
    {
      return (CDOResourceImpl)resource;
    }

    return null;
  }

  /**
   * @since 3.0
   */
  public void cdoPrefetch(int depth)
  {
    view.prefetchRevisions(id, depth);
  }

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  /**
   * @since 2.0
   */
  public boolean cdoConflict()
  {
    return FSMUtil.isConflict(this);
  }

  /**
   * @since 2.0
   */
  public boolean cdoInvalid()
  {
    return FSMUtil.isInvalid(this);
  }

  /**
   * @since 2.0
   */
  public CDOLock cdoReadLock()
  {
    return createLock(this, LockType.READ);
  }

  /**
   * @since 2.0
   */
  public CDOLock cdoWriteLock()
  {
    return createLock(this, LockType.WRITE);
  }

  /**
   * @since 4.1
   */
  public CDOLock cdoWriteOption()
  {
    return createLock(this, LockType.OPTION);
  }

  /**
   * @since 4.1
   */
  public CDOLockState cdoLockState()
  {
    return getLockState(this);
  }

  public void cdoInternalSetID(CDOID id)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting ID: {0}", id); //$NON-NLS-1$
    }

    this.id = id;
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    CDOState oldState = this.state;
    if (oldState != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this); //$NON-NLS-1$
      }

      this.state = state;
      if (view != null)
      {
        view.handleObjectStateChanged(this, oldState, state);
      }

      return oldState;
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting revision: {0}", revision); //$NON-NLS-1$
    }

    this.revision = (InternalCDORevision)revision;
  }

  /**
   * @since 2.0
   */
  public void cdoInternalSetView(CDOView view)
  {
    this.view = (InternalCDOView)view;
    if (this.view != null)
    {
      eSetStore(this.view.getStore());
    }
    else
    {
      eSetStore(null);
    }
  }

  public void cdoInternalSetResource(CDOResource resource)
  {
    // Unsets direct resource and/or eContainer.
    // Only intended to be called by CDOTransactionImpl.removeObject(CDOID, CDOObject).
    // See bug 383370.

    // TODO Rename this method to cdoInternalDetach()

    if (resource != null)
    {
      throw new IllegalArgumentException(
          "Only intended to be called by CDOTransactionImpl.removeObject(CDOID, CDOObject");
    }

    super.eSetDirectResource(null);
    eContainer = null;
    eContainerFeatureID = 0;
  }

  /**
   * @since 2.0
   */
  public void cdoInternalPreLoad()
  {
    // Do nothing
  }

  public void cdoInternalPostLoad()
  {
    // Reset EMAP objects
    if (eSettings != null)
    {
      // Make sure transient features are kept but persisted values are not cached.
      EClass eClass = eClass();
      for (int i = 0; i < eClass.getFeatureCount(); i++)
      {
        EStructuralFeature eFeature = cdoInternalDynamicFeature(i);

        // We need to keep the existing list if possible.
        if (EMFUtil.isPersistent(eFeature) && eSettings[i] instanceof InternalCDOLoadable)
        {
          ((InternalCDOLoadable)eSettings[i]).cdoInternalPostLoad();
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public void cdoInternalPostInvalidate()
  {
    // Do nothing
  }

  public void cdoInternalPostAttach()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating revision for {0}", this); //$NON-NLS-1$
    }

    revision.setContainerID(eContainer == null ? CDOID.NULL : view.convertObjectToID(eContainer, true));
    revision.setContainingFeatureID(eContainerFeatureID);

    Resource directResource = eDirectResource();
    if (directResource instanceof CDOResource)
    {
      CDOResource cdoResource = (CDOResource)directResource;
      revision.setResourceID(cdoResource.cdoID());
    }

    if (cdoSettings != null)
    {
      EClass eClass = eClass();
      for (int i = 0; i < eClass.getFeatureCount(); i++)
      {
        EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
        if (EMFUtil.isPersistent(eFeature))
        {
          instanceToRevisionFeature(view, this, eFeature, cdoSettings[i]);
        }
      }

      cdoRevision().setUnchunked();
      cdoSettings = null;
    }
  }

  /**
   * It is really important for accessing the data to go through {@link #cdoStore()}. {@link #eStore()} will redirect
   * you to the transient data.
   *
   * @since 2.0
   */
  public void cdoInternalPostDetach(boolean remote)
  {
    if (remote)
    {
      // Do nothing
      return;
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating revision for {0}", this); //$NON-NLS-1$
    }

    CDOStore store = cdoStore();
    super.eSetDirectResource((Resource.Internal)store.getResource(this));
    eContainer = store.getContainer(this);
    eContainerFeatureID = store.getContainingFeatureID(this);

    // Ensure that the internal eSettings array is initialized;
    resetSettings();

    EClass eClass = eClass();
    for (int i = 0; i < eClass.getFeatureCount(); i++)
    {
      EStructuralFeature eFeature = cdoInternalDynamicFeature(i);
      if (EMFUtil.isPersistent(eFeature))
      {
        revisionToInstanceFeature(this, revision, eFeature);
      }
    }
  }

  /**
   * @since 3.0
   */
  public void cdoInternalPostRollback()
  {
    // Do nothing
  }

  public void cdoInternalPreCommit()
  {
    // Do nothing
  }

  public InternalEObject cdoInternalInstance()
  {
    return this;
  }

  public EStructuralFeature cdoInternalDynamicFeature(int dynamicFeatureID)
  {
    return eDynamicFeature(dynamicFeatureID);
  }

  /**
   * @since 2.0
   */
  @Override
  public synchronized EList<Adapter> eAdapters()
  {
    if (eAdapters == null)
    {
      // TODO Adjust for EObjectEAdapterList (see bug #247130)
      eAdapters = new EAdapterList<Adapter>(this)
      {
        private static final long serialVersionUID = 1L;

        @Override
        protected void didAdd(int index, Adapter newObject)
        {
          if (view == null || view.isActive())
          {
            super.didAdd(index, newObject);
            if (!FSMUtil.isTransient(CDOObjectImpl.this))
            {
              view.handleAddAdapter(CDOObjectImpl.this, newObject);
            }
          }
        }

        @Override
        protected void didRemove(int index, Adapter oldObject)
        {
          if (view == null || view.isActive())
          {
            super.didRemove(index, oldObject);
            if (!FSMUtil.isTransient(CDOObjectImpl.this))
            {
              view.handleRemoveAdapter(CDOObjectImpl.this, oldObject);
            }
          }
        }
      };
    }

    return eAdapters;
  }

  /**
   * @since 2.0
   */
  @Override
  public Resource.Internal eDirectResource()
  {
    if (FSMUtil.isTransient(this))
    {
      return super.eDirectResource();
    }

    return (Resource.Internal)cdoStore().getResource(this);
  }

  @Override
  public Resource.Internal eInternalResource()
  {
    if (FSMUtil.isInvalid(this))
    {
      return null;
    }

    return super.eInternalResource();
  }

  @Override
  public Object dynamicGet(int dynamicFeatureID)
  {
    Object result = eSettings[dynamicFeatureID];
    if (result == null)
    {
      EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
      if (EMFUtil.isPersistent(eStructuralFeature))
      {
        if (FeatureMapUtil.isFeatureMap(eStructuralFeature))
        {
          eSettings[dynamicFeatureID] = result = createFeatureMap(eStructuralFeature);
        }
        else if (eStructuralFeature.isMany())
        {
          eSettings[dynamicFeatureID] = result = createList(eStructuralFeature);
        }
        else
        {
          result = eStore().get(this, eStructuralFeature, EStore.NO_INDEX);
          if (eIsCaching())
          {
            eSettings[dynamicFeatureID] = result;
          }
        }
      }
    }

    return result;
  }

  @Override
  public void dynamicSet(int dynamicFeatureID, Object value)
  {
    EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
    if (!EMFUtil.isPersistent(eStructuralFeature))
    {
      eSettings[dynamicFeatureID] = value;
    }
    else
    {
      eStore().set(this, eStructuralFeature, EStore.NO_INDEX, value);
      if (eIsCaching())
      {
        eSettings[dynamicFeatureID] = value;
      }
    }
  }

  @Override
  public void dynamicUnset(int dynamicFeatureID)
  {
    EStructuralFeature eStructuralFeature = eDynamicFeature(dynamicFeatureID);
    if (!EMFUtil.isPersistent(eStructuralFeature))
    {
      eSettings[dynamicFeatureID] = null;
    }
    else
    {
      eStore().unset(this, eDynamicFeature(dynamicFeatureID));
      if (eIsCaching())
      {
        eSettings[dynamicFeatureID] = null;
      }
    }
  }

  /**
   * @since 2.0
   */
  @Override
  protected boolean eDynamicIsSet(int dynamicFeatureID, EStructuralFeature eFeature)
  {
    if (dynamicFeatureID < 0)
    {
      return eOpenIsSet(eFeature);
    }

    if (EMFUtil.isPersistent(eFeature))
    {
      return eStore().isSet(this, eFeature);
    }

    return eSettingDelegate(eFeature).dynamicIsSet(this, eSettings(), dynamicFeatureID);
  }

  /**
   * @since 2.0
   */
  @Override
  public EStore eStore()
  {
    if (FSMUtil.isTransient(this))
    {
      return CDOStoreSettingsImpl.INSTANCE;
    }

    return cdoStore();
  }

  @Override
  public InternalEObject eInternalContainer()
  {
    InternalEObject container;
    if (FSMUtil.isTransient(this))
    {
      container = eContainer;
    }
    else
    {
      // Delegate to CDOStore
      container = cdoStore().getContainer(this);
    }

    return container;
  }

  @Override
  public int eContainerFeatureID()
  {
    if (FSMUtil.isTransient(this))
    {
      return eContainerFeatureID;
    }

    // Delegate to CDOStore
    return cdoStore().getContainingFeatureID(this);
  }

  /**
   * Code took from {@link BasicEObjectImpl#eBasicSetContainer} and modify it to detect when object are moved in the
   * same context. (E.g.: An object is moved from resA to resB. resA and resB belongs to the same CDORepositoryInfo.
   * Without this special handling, a detach and newObject will be generated for the object moved)
   *
   * @since 2.0
   */
  @Override
  public NotificationChain eBasicSetContainer(InternalEObject newContainer, int newContainerFeatureID,
      NotificationChain msgs)
  {
    boolean isResourceRoot = this instanceof CDOResource && ((CDOResource)this).isRoot();

    InternalEObject oldContainer = eInternalContainer();
    Resource.Internal oldResource = eDirectResource();
    Resource.Internal newResource = null;
    if (oldResource != null)
    {
      if (newContainer != null && !eContainmentFeature(this, newContainer, newContainerFeatureID).isResolveProxies())
      {
        msgs = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, msgs);
        eSetDirectResource(null);
        newResource = newContainer.eInternalResource();
      }
      else
      {
        oldResource = null;
      }
    }
    else
    {
      if (oldContainer != null)
      {
        oldResource = oldContainer.eInternalResource();
      }

      if (newContainer != null)
      {
        newResource = newContainer.eInternalResource();
      }
    }

    CDOView oldView = view;
    CDOView newView = newResource != null && newResource instanceof CDOResource ? ((CDOResource)newResource).cdoView()
        : null;

    boolean moved = oldView != null && oldView == newView;
    if (!moved && oldResource != null && !isResourceRoot)
    {
      oldResource.detached(this);
    }

    int oldContainerFeatureID = eContainerFeatureID();
    eBasicSetContainer(newContainer, newContainerFeatureID);

    if (!moved && oldResource != newResource && newResource != null)
    {
      newResource.attached(this);
    }

    if (eNotificationRequired())
    {
      if (oldContainer != null && oldContainerFeatureID >= 0 && oldContainerFeatureID != newContainerFeatureID)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, oldContainerFeatureID,
            oldContainer, null);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }

      if (newContainerFeatureID >= 0)
      {
        ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, newContainerFeatureID,
            oldContainerFeatureID == newContainerFeatureID ? oldContainer : null, newContainer);
        if (msgs == null)
        {
          msgs = notification;
        }
        else
        {
          msgs.add(notification);
        }
      }
    }

    return msgs;
  }

  /**
   * Code took from {@link BasicEObjectImpl#eSetResource} and modify it to detect when object are moved in the same
   * context.
   *
   * @since 2.0
   */
  @Override
  public NotificationChain eSetResource(Resource.Internal resource, NotificationChain notifications)
  {
    Resource.Internal oldResource = eDirectResource();

    CDOView oldView = view;
    CDOView newView = resource != null && resource instanceof CDOResource ? ((CDOResource)resource).cdoView() : null;

    boolean isSameView;
    if (state == CDOState.NEW)
    {
      isSameView = false;
    }
    else
    {
      isSameView = oldView != null && oldView == newView;
    }

    if (oldResource != null && resource != null)
    {
      notifications = ((InternalEList<?>)oldResource.getContents()).basicRemove(this, notifications);

      // When setting the resource to null we assume that detach has already been called in the resource
      // implementation
      if (!isSameView)
      {
        oldResource.detached(this);
      }
    }

    InternalEObject oldContainer = eInternalContainer();
    if (oldContainer != null && !isSameView)
    {
      if (eContainmentFeature().isResolveProxies())
      {
        Resource.Internal oldContainerResource = oldContainer.eInternalResource();
        if (oldContainerResource != null)
        {
          // If we're not setting a new resource, attach it to the old container's resource.
          if (resource == null)
          {
            oldContainerResource.attached(this);
          }

          // If we didn't detach it from an old resource already, detach it from the old container's resource.
          //
          else if (oldResource == null)
          {
            oldContainerResource.detached(this);
          }
        }
      }
      else
      {
        notifications = eBasicRemoveFromContainer(notifications);
        notifications = eBasicSetContainer(null, -1, notifications);
      }
    }

    eSetDirectResource(resource);

    return notifications;
  }

  /**
   * Specializing the behaviour of {@link #hashCode()} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final int hashCode()
  {
    return super.hashCode();
  }

  /**
   * Specializing the behaviour of {@link #equals(Object)} is not permitted as per {@link EObject} specification.
   */
  @Override
  public final boolean equals(Object obj)
  {
    return super.equals(obj);
  }

  @Override
  public String toString()
  {
    if (id == null)
    {
      return eClass().getName() + "?"; //$NON-NLS-1$
    }

    return eClass().getName() + "@" + id; //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  protected Object[] cdoSettings()
  {
    if (cdoSettings == null)
    {
      int size = eClass().getFeatureCount() - eStaticFeatureCount();
      if (size == 0)
      {
        cdoSettings = ENO_SETTINGS;
      }
      else
      {
        cdoSettings = new Object[size];
      }
    }

    return cdoSettings;
  }

  /**
   * @since 2.0
   */
  protected Object[] cdoBasicSettings()
  {
    return cdoSettings;
  }

  @Override
  protected FeatureMap createFeatureMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreFeatureMap(eStructuralFeature);
  }

  /**
   * @since 4.1
   */
  protected CDOStoreEcoreEMap createMap(EStructuralFeature eStructuralFeature)
  {
    return new CDOStoreEcoreEMap(eStructuralFeature);
  }

  @Override
  protected EList<?> createList(EStructuralFeature eStructuralFeature)
  {
    if (isMap(eStructuralFeature))
    {
      return createMap(eStructuralFeature);
    }

    return super.createList(eStructuralFeature);
  }

  private boolean isMap(EStructuralFeature eStructuralFeature)
  {
    // Answer from Christian Damus:
    // Java ensures that string constants are interned, so this is actually
    // more efficient than .equals() and it's correct
    return eStructuralFeature.getEType().getInstanceClassName() == "java.util.Map$Entry"; //$NON-NLS-1$
  }

  @Override
  protected void eInitializeContainer()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void eSetDirectResource(Internal resource)
  {
    if (FSMUtil.isTransient(this))
    {
      super.eSetDirectResource(resource);
    }
    else if (resource instanceof CDOResourceImpl || resource == null)
    {
      cdoStore().setContainer(this, (CDOResourceImpl)resource, eInternalContainer(), eContainerFeatureID());
    }
    else
    {
      throw new IllegalArgumentException(Messages.getString("CDOObjectImpl.8")); //$NON-NLS-1$
    }
  }

  /**
   * Don't cache non-transient features in this CDOObject's {@link #eSettings()}.
   */
  @Override
  protected boolean eIsCaching()
  {
    return false;
  }

  @Override
  protected void eBasicSetContainer(InternalEObject newEContainer, int newContainerFeatureID)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Setting container: {0}, featureID={1}", newEContainer, newContainerFeatureID); //$NON-NLS-1$
    }

    if (FSMUtil.isTransient(this))
    {
      super.eBasicSetContainer(newEContainer, newContainerFeatureID);
    }
    else
    {
      cdoStore().setContainer(this, cdoDirectResource(), newEContainer, newContainerFeatureID);
    }
  }

  private CDOStore cdoStore()
  {
    return view.getStore();
  }

  private void resetSettings()
  {
    cdoSettings = null;
    cdoSettings();
  }

  /**
   * Adjust the reference ONLY if the opposite reference used CDOID. This is true ONLY if the state of <cdo>this</code>
   * was not {@link CDOState#NEW}.
   */
  private static void adjustOppositeReference(InternalCDOObject instance, InternalEObject object, EReference feature)
  {
    if (object != null)
    {
      InternalCDOObject cdoObject = (InternalCDOObject)CDOUtil.getCDOObject(object);
      if (cdoObject != null && !FSMUtil.isTransient(cdoObject))
      {
        if (feature.isMany())
        {
          int index = cdoObject.eStore().indexOf(cdoObject, feature, instance.cdoID());

          // TODO Simon Log an error in the new view.getErrors() in the case we are not able to find the object.
          // Cannot throw an exception, the detach process is too far.
          if (index != -1)
          {
            cdoObject.eStore().set(cdoObject, feature, index, instance);
          }
        }
        else
        {
          cdoObject.eStore().set(cdoObject, feature, 0, instance);
        }
      }
      else
      {
        if (feature.isResolveProxies())
        {
          // We should not trigger events. But we have no choice :-(.
          if (feature.isMany())
          {
            @SuppressWarnings("unchecked")
            InternalEList<Object> list = (InternalEList<Object>)object.eGet(feature);
            int index = list.indexOf(instance);
            if (index != -1)
            {
              list.set(index, instance);
            }
          }
          else
          {
            object.eSet(feature, instance);
          }
        }
      }
    }
  }

  /**
   * @since 2.0
   */
  public static void revisionToInstanceFeature(InternalCDOObject instance, InternalCDORevision revision,
      EStructuralFeature eFeature)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Depopulating feature {0}", eFeature); //$NON-NLS-1$
    }

    EStructuralFeature.Internal internalFeature = (EStructuralFeature.Internal)eFeature;
    InternalCDOView view = instance.cdoView();
    EReference oppositeReference = view.isObjectNew(instance.cdoID()) ? null : internalFeature.getEOpposite();

    CDOStore cdoStore = view.getStore();
    EStore eStore = instance.eStore();

    if (eFeature.isMany())
    {
      int size = cdoStore.size(instance, eFeature);
      for (int index = 0; index < size; index++)
      {
        // Do not trigger events
        // Do not trigger inverse updates
        Object object = cdoStore.get(instance, eFeature, index);
        eStore.add(instance, eFeature, index, object);
        if (oppositeReference != null)
        {
          adjustOppositeReference(instance, (InternalEObject)object, oppositeReference);
        }
      }
    }
    else
    {
      Object object = cdoStore.get(instance, eFeature, EStore.NO_INDEX);
      eStore.set(instance, eFeature, EStore.NO_INDEX, object);
      if (oppositeReference != null)
      {
        adjustOppositeReference(instance, (InternalEObject)object, oppositeReference);
      }
    }
  }

  /**
   * @since 3.0
   */
  public static void instanceToRevisionFeature(InternalCDOView view, InternalCDOObject object,
      EStructuralFeature feature, Object setting)
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Populating feature {0}", feature); //$NON-NLS-1$
    }

    CDOStore cdoStore = view.getStore();
    InternalCDORevision revision = object.cdoRevision();

    if (feature.isMany())
    {
      if (setting != null)
      {
        int index = 0;
        @SuppressWarnings("unchecked")
        EList<Object> list = (EList<Object>)setting;
        for (Object value : list)
        {
          value = cdoStore.convertToCDO(object, feature, value);
          revision.add(feature, index++, value);
        }
      }
    }
    else
    {
      setting = cdoStore.convertToCDO(object, feature, setting);
      revision.set(feature, 0, setting);
    }
  }

  /**
   * @since 4.1
   */
  public static CDOLock createLock(InternalCDOObject object, LockType type)
  {
    if (FSMUtil.isTransient(object))
    {
      throw new IllegalStateException("Call CDOView.lockObjects() for transient object " + object);
    }

    return new CDOLockImpl(object, type);
  }

  /**
   * @since 4.1
   */
  public static CDOLockState getLockState(InternalCDOObject object)
  {
    if (!FSMUtil.isTransient(object))
    {
      InternalCDOView view = object.cdoView();
      CDOID id = object.cdoID();

      return view.getLockStates(Collections.singletonList(id))[0];
    }

    return null;
  }

  /**
   * For internal use only.
   *
   * @author Simon McDuff
   * @since 2.0
   */
  public static class CDOStoreSettingsImpl implements InternalEObject.EStore
  {
    public static CDOStoreSettingsImpl INSTANCE = new CDOStoreSettingsImpl();

    private CDOStoreSettingsImpl()
    {
    }

    protected Object getValue(InternalEObject eObject, int dynamicFeatureID)
    {
      Object value = ((CDOObjectImpl)eObject).cdoSettings()[dynamicFeatureID];
      return value;
    }

    protected EList<Object> getValueAsList(InternalEObject eObject, int dynamicFeatureID)
    {
      @SuppressWarnings("unchecked")
      EList<Object> result = (EList<Object>)getValue(eObject, dynamicFeatureID);
      if (result == null)
      {
        result = new BasicEList<Object>();
        ((CDOObjectImpl)eObject).cdoSettings()[dynamicFeatureID] = result;
      }

      return result;
    }

    protected Object setValue(InternalEObject eObject, int dynamicFeatureID, Object newValue)
    {
      Object settings[] = ((CDOObjectImpl)eObject).cdoSettings();
      Object oldSetting = settings[dynamicFeatureID];
      settings[dynamicFeatureID] = newValue;
      return oldSetting;
    }

    protected int eDynamicFeatureID(InternalEObject eObject, EStructuralFeature feature)
    {
      return ((CDOObjectImpl)eObject).eDynamicFeatureID(feature);
    }

    public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (index != NO_INDEX)
      {
        return getValueAsList(eObject, dynamicFeatureID).get(index);
      }

      return getValue(eObject, dynamicFeatureID);
    }

    public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (index != NO_INDEX)
      {
        return getValueAsList(eObject, dynamicFeatureID).set(index, value);
      }

      return setValue(eObject, dynamicFeatureID, value);
    }

    public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      getValueAsList(eObject, dynamicFeatureID).add(index, value);
    }

    public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).remove(index);
    }

    public Object move(InternalEObject eObject, EStructuralFeature feature, int targetIndex, int sourceIndex)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).move(targetIndex, sourceIndex);
    }

    public void clear(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (feature.isMany())
      {
        getValueAsList(eObject, dynamicFeatureID).clear();
      }

      setValue(eObject, dynamicFeatureID, null);
    }

    public int size(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).size();
    }

    public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).indexOf(value);
    }

    public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).lastIndexOf(value);
    }

    public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).toArray();
    }

    public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] array)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).toArray(array);
    }

    public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).isEmpty();
    }

    public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).contains(value);
    }

    public int hashCode(InternalEObject eObject, EStructuralFeature feature)
    {
      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return getValueAsList(eObject, dynamicFeatureID).hashCode();
    }

    public InternalEObject getContainer(InternalEObject eObject)
    {
      return null;
    }

    public EStructuralFeature getContainingFeature(InternalEObject eObject)
    {
      throw new UnsupportedOperationException("Should never be called");
    }

    public EObject create(EClass eClass)
    {
      return new EStoreEObjectImpl(eClass, this);
    }

    public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
    {
      if (!feature.isUnsettable())
      {
        if (feature.isMany())
        {
          @SuppressWarnings("unchecked")
          InternalEList<Object> list = (InternalEList<Object>)eObject.eGet(feature);
          return list != null && !list.isEmpty();
        }

        return !ObjectUtil.equals(eObject.eGet(feature), feature.getDefaultValue());
      }

      Object[] settings = ((CDOObjectImpl)eObject).cdoBasicSettings();
      if (settings == null)
      {
        return false;
      }

      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      return settings[dynamicFeatureID] != null;
    }

    public void unset(InternalEObject eObject, EStructuralFeature feature)
    {
      Object[] settings = ((CDOObjectImpl)eObject).cdoBasicSettings();
      if (settings == null)
      {
        // Is already unset
        return;
      }

      int dynamicFeatureID = eDynamicFeatureID(eObject, feature);
      if (feature.isUnsettable())
      {
        settings[dynamicFeatureID] = null;
      }
      else
      {
        settings[dynamicFeatureID] = feature.getDefaultValue();
      }
    }
  }

  /**
   * @author Eike Stepper
   * @since 4.1
   */
  public class CDOStoreEcoreEMap extends EcoreEMap<Object, Object> implements InternalCDOLoadable
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreEcoreEMap(EStructuralFeature eStructuralFeature)
    {
      super((EClass)eStructuralFeature.getEType(), BasicEMap.Entry.class, null);
      delegateEList = new BasicEStoreEList<BasicEMap.Entry<Object, Object>>(CDOObjectImpl.this, eStructuralFeature)
      {
        private static final long serialVersionUID = 1L;

        @Override
        public void unset()
        {
          super.unset();
          doClear();
        }

        @Override
        protected void didAdd(int index, BasicEMap.Entry<Object, Object> newObject)
        {
          CDOStoreEcoreEMap.this.doPut(newObject);
        }

        @Override
        protected void didSet(int index, BasicEMap.Entry<Object, Object> newObject,
            BasicEMap.Entry<Object, Object> oldObject)
        {
          didRemove(index, oldObject);
          didAdd(index, newObject);
        }

        @Override
        protected void didRemove(int index, BasicEMap.Entry<Object, Object> oldObject)
        {
          CDOStoreEcoreEMap.this.doRemove(oldObject);
        }

        @Override
        protected void didClear(int size, Object[] oldObjects)
        {
          CDOStoreEcoreEMap.this.doClear();
        }

        @Override
        protected void didMove(int index, BasicEMap.Entry<Object, Object> movedObject, int oldIndex)
        {
          CDOStoreEcoreEMap.this.doMove(movedObject);
        }
      };

      size = delegateEList.size();
    }

    private void checkListForReading()
    {
      if (!FSMUtil.isTransient(CDOObjectImpl.this))
      {
        CDOStateMachine.INSTANCE.read(CDOObjectImpl.this);
      }
    }

    /**
     * Ensures that the entry data is created and is populated with contents of the delegate list.
     */
    @Override
    protected synchronized void ensureEntryDataExists()
    {
      checkListForReading();
      super.ensureEntryDataExists();
    }

    @Override
    public int size()
    {
      checkListForReading();
      return size;
    }

    @Override
    public boolean isEmpty()
    {
      checkListForReading();
      return size == 0;
    }

    @Override
    public boolean contains(Object object)
    {
      checkListForReading();
      return super.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection)
    {
      checkListForReading();
      return super.containsAll(collection);
    }

    @Override
    public boolean containsKey(Object key)
    {
      checkListForReading();
      return super.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value)
    {
      checkListForReading();
      return super.containsValue(value);
    }

    public void cdoInternalPostLoad()
    {
      entryData = null;
      size = delegateEList.size();
    }

    public void cdoInternalPreLoad()
    {
    }
  }

  /**
   * TODO Remove this when EMF has fixed bug 197487
   *
   * @author Eike Stepper
   */
  public class CDOStoreFeatureMap extends DelegatingFeatureMap
  {
    private static final long serialVersionUID = 1L;

    public CDOStoreFeatureMap(EStructuralFeature eStructuralFeature)
    {
      super(CDOObjectImpl.this, eStructuralFeature);
    }

    @Override
    protected List<FeatureMap.Entry> delegateList()
    {
      throw new UnsupportedOperationException();
    }

    @Override
    public EStructuralFeature getEStructuralFeature()
    {
      return eStructuralFeature;
    }

    @Override
    protected void delegateAdd(int index, Entry object)
    {
      eStore().add(owner, eStructuralFeature, index, object);
    }

    @Override
    protected void delegateAdd(Entry object)
    {
      delegateAdd(delegateSize(), object);
    }

    @Override
    protected List<FeatureMap.Entry> delegateBasicList()
    {
      int size = delegateSize();
      if (size == 0)
      {
        return ECollections.emptyEList();
      }

      Object[] data = cdoStore().toArray(owner, eStructuralFeature);
      return new EcoreEList.UnmodifiableEList<FeatureMap.Entry>(owner, eStructuralFeature, data.length, data);
    }

    @Override
    protected void delegateClear()
    {
      eStore().clear(owner, eStructuralFeature);
    }

    @Override
    protected boolean delegateContains(Object object)
    {
      return eStore().contains(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateContainsAll(Collection<?> collection)
    {
      for (Object o : collection)
      {
        if (!delegateContains(o))
        {
          return false;
        }
      }

      return true;
    }

    @Override
    protected Entry delegateGet(int index)
    {
      return (Entry)eStore().get(owner, eStructuralFeature, index);
    }

    @Override
    protected int delegateHashCode()
    {
      return eStore().hashCode(owner, eStructuralFeature);
    }

    @Override
    protected int delegateIndexOf(Object object)
    {
      return eStore().indexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected boolean delegateIsEmpty()
    {
      return eStore().isEmpty(owner, eStructuralFeature);
    }

    @Override
    protected Iterator<FeatureMap.Entry> delegateIterator()
    {
      return iterator();
    }

    @Override
    protected int delegateLastIndexOf(Object object)
    {
      return eStore().lastIndexOf(owner, eStructuralFeature, object);
    }

    @Override
    protected ListIterator<FeatureMap.Entry> delegateListIterator()
    {
      return listIterator();
    }

    @Override
    protected Entry delegateRemove(int index)
    {
      return (Entry)eStore().remove(owner, eStructuralFeature, index);
    }

    @Override
    protected Entry delegateSet(int index, Entry object)
    {
      return (Entry)eStore().set(owner, eStructuralFeature, index, object);
    }

    @Override
    protected int delegateSize()
    {
      return eStore().size(owner, eStructuralFeature);
    }

    @Override
    protected Object[] delegateToArray()
    {
      return eStore().toArray(owner, eStructuralFeature);
    }

    @Override
    protected <T> T[] delegateToArray(T[] array)
    {
      return eStore().toArray(owner, eStructuralFeature, array);
    }

    @Override
    protected Entry delegateMove(int targetIndex, int sourceIndex)
    {
      return (Entry)eStore().move(owner, eStructuralFeature, targetIndex, sourceIndex);
    }

    @Override
    protected String delegateToString()
    {
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("["); //$NON-NLS-1$
      for (int i = 0, size = size(); i < size;)
      {
        Object value = delegateGet(i);
        stringBuffer.append(String.valueOf(value));
        if (++i < size)
        {
          stringBuffer.append(", "); //$NON-NLS-1$
        }
      }

      stringBuffer.append("]"); //$NON-NLS-1$
      return stringBuffer.toString();
    }
  }
}
