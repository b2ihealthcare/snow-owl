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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.security.CDOPermission;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOObjectImpl;
import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.view.CDOStateMachine;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @author Martin Fluegge
 * @since 2.0
 */
public abstract class CDOLegacyWrapper extends CDOObjectWrapper
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_OBJECT, CDOLegacyWrapper.class);

  /**
   * This ThreadLocal map stores all pre-registered objects. This avoids a never-ending loop when setting the container
   * of an object.
   */
  private static ThreadLocal<Map<CDOID, CDOLegacyWrapper>> wrapperRegistry = new InheritableThreadLocal<Map<CDOID, CDOLegacyWrapper>>()
  {
    @Override
    protected Map<CDOID, CDOLegacyWrapper> initialValue()
    {
      return new HashMap<CDOID, CDOLegacyWrapper>();
    }
  };

  private static ThreadLocal<Counter> recursionCounter = new InheritableThreadLocal<Counter>()
  {
    @Override
    protected Counter initialValue()
    {
      return new Counter();
    }
  };

  protected CDOState state;

  protected InternalCDORevision revision;

  /**
   * It could happen that while <i>revisionToInstance()</i> is executed externally the <i>internalPostLoad()</i> method
   * will be called. This happens for example if <i>internalPostInvalidate()</i> is called. The leads to another
   * <i>revisionToInstance()</i> call while the first call has not finished. This is certainly not so cool. That's why
   * <b>underConstruction</b> will flag that <i>revisionToInstance()</i> is still running and avoid the second call.
   *
   * @since 3.0
   */
  private boolean underConstruction;

  public CDOLegacyWrapper(InternalEObject instance)
  {
    this.instance = instance;
    state = CDOState.TRANSIENT;
  }

  public CDOState cdoState()
  {
    return state;
  }

  public InternalCDORevision cdoRevision()
  {
    return revision;
  }

  @Override
  public CDOResourceImpl cdoResource()
  {
    revisionToInstanceResource();
    return super.cdoResource();
  }

  public void cdoReload()
  {
    CDOStateMachine.INSTANCE.reload(this);
  }

  public CDOState cdoInternalSetState(CDOState state)
  {
    if (this.state != state)
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("Setting state {0} for {1}", state, this); //$NON-NLS-1$
      }

      CDOState oldState = this.state;
      this.state = state;
      adjustEProxy();

      if (view != null)
      {
        view.handleObjectStateChanged(this, oldState, state);
      }

      return oldState;
    }

    return null;
  }

  public void cdoInternalSetRevision(CDORevision revision)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Setting revision: " + revision); //$NON-NLS-1$
    }

    this.revision = (InternalCDORevision)revision;
  }

  public void cdoInternalPostAttach()
  {
    instanceToRevision();

    for (Adapter adapter : eAdapters())
    {
      if (!(adapter instanceof CDOObjectWrapper))
      {
        view.handleAddAdapter(this, adapter);
        view.subscribe(this, adapter);
      }
    }
  }

  public void cdoInternalPostDetach(boolean remote)
  {
    if (remote)
    {
      // Do nothing??
      return;
    }

    EClass eClass = revision.getEClass();

    // This loop adjusts the opposite wrapper objects to support dangling references. See Bugzilla_251263_Test
    for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      EReference oppositeReference = ((EStructuralFeature.Internal)feature).getEOpposite();
      if (oppositeReference != null && !oppositeReference.isContainment() && EMFUtil.isPersistent(oppositeReference))
      {
        if (feature.isMany())
        {
          int size = revision.size(feature);
          for (int i = 0; i < size; i++)
          {
            EObject object = (EObject)getValueFromRevision(feature, i);
            adjustPersistentOppositeReference(this, object, oppositeReference);
          }
        }
        else
        {
          EObject oppositeObject = (EObject)instance.eGet(feature);
          if (oppositeObject != null)
          {
            adjustPersistentOppositeReference(this, oppositeObject, oppositeReference);
          }
        }
      }
    }
  }

  /**
   * @since 3.0
   */
  public void cdoInternalPostRollback()
  {
    CDOStateMachine.INSTANCE.read(this);
  }

  /**
   * CDO persists the isUnset state of an eObject in the database. The indicator for this is that the feature is null in
   * the revision (see CDOStore.isSet()). When committing a legacy object all values in the instance for native
   * attributes are set with the java default values. So, these values will be stored in the revision and CDO cannot
   * distinguish whether the feature is set or not. This method must ensure that the value will be set to null if the
   * feature is not set.
   */
  public void cdoInternalPreCommit()
  {
    // We have to set this here because the CDOLegacyAdapter will not be notified when the instance is the target of a
    // single-directional containment reference.
    // If the container is not an legacy Object the system will get no information
    instanceToRevisionContainment();

    EClass eClass = revision.getEClass();
    for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      if (feature.isUnsettable())
      {
        if (!instance.eIsSet(feature))
        {
          if (feature.isMany())
          {
            @SuppressWarnings("unchecked")
            InternalEList<Object> list = (InternalEList<Object>)instance.eGet(feature);
            clearEList(list);
          }
          else
          {
            revision.set(feature, EStore.NO_INDEX, null);
          }
        }
        else if (instance.eGet(feature) == null)
        {
          // Must be single-valued!
          revision.set(feature, EStore.NO_INDEX, CDORevisionData.NIL);
        }
      }
    }
  }

  public void cdoInternalPreLoad()
  {
    // Do nothing
  }

  public void cdoInternalPostLoad()
  {
    // TODO Consider not remembering the revision after copying it to the instance (spare 1/2 of the space)
    revisionToInstance();
  }

  public void cdoInternalPostInvalidate()
  {
    if (cdoState() != CDOState.CLEAN)
    {
      InternalCDORevision revision = cdoView().getRevision(cdoID(), true);
      cdoInternalSetRevision(revision);
    }

    revisionToInstance();
    cdoInternalSetState(CDOState.CLEAN);
  }

  @Override
  public boolean equals(Object obj)
  {
    return obj == this || obj == instance;
  }

  @Override
  public int hashCode()
  {
    if (instance != null)
    {
      return instance.hashCode();
    }

    return super.hashCode();
  }

  @Override
  public String toString()
  {
    return "CDOLegacyWrapper[" + instance.getClass().getSimpleName() + "@" + id + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
  }

  protected void instanceToRevision()
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering instance to revision: {0} --> {1}", instance, revision); //$NON-NLS-1$
    }

    // Handle containment
    instanceToRevisionContainment();

    // Handle values
    CDOPackageRegistry packageRegistry = cdoView().getSession().getPackageRegistry();
    EClass eClass = revision.getEClass();
    for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      instanceToRevisionFeature(feature, packageRegistry);
    }

    revision.setUnchunked();
  }

  protected void instanceToRevisionContainment()
  {
    CDOResource resource = (CDOResource)getInstanceResource(instance);
    revision.setResourceID(resource == null ? CDOID.NULL : resource.cdoID());

    InternalEObject eContainer = getInstanceContainer(instance);
    if (eContainer == null)
    {
      revision.setContainerID(CDOID.NULL);
      revision.setContainingFeatureID(0);
    }
    else
    {
      CDOObject cdoContainer = FSMUtil.adapt(eContainer, view);
      revision.setContainerID(cdoContainer);
      revision.setContainingFeatureID(getInstanceContainerFeatureID(instance));
    }
  }

  protected void instanceToRevisionFeature(EStructuralFeature feature, CDOPackageRegistry packageRegistry)
  {
    Object instanceValue = getInstanceValue(instance, feature, packageRegistry);
    CDOObjectImpl.instanceToRevisionFeature(view, this, feature, instanceValue);
  }

  protected void revisionToInstance()
  {

    if (underConstruction)
    {
      // Return if revisionToInstance was called before to avoid doubled calls
      return;
    }

    underConstruction = true;

    if (TRACER.isEnabled())
    {
      TRACER.format("Transfering revision to instance: {0} --> {1}", revision, instance); //$NON-NLS-1$
    }

    boolean deliver = instance.eDeliver();
    if (deliver)
    {
      instance.eSetDeliver(false);
    }

    Counter counter = recursionCounter.get();

    try
    {
      registerWrapper(this);
      counter.increment();
      view.registerObject(this);

      revisionToInstanceContainer();

      for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(revision.getEClass()))
      {
        revisionToInstanceFeature(feature);
      }

      revisionToInstanceResource();
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
      throw new CDOException(ex);
    }
    finally
    {
      if (deliver)
      {
        instance.eSetDeliver(true);
      }

      counter.decrement();
      unregisterWrapper(this);
      underConstruction = false;
    }
  }

  /**
   * @since 4.0
   */
  protected void revisionToInstanceContainer()
  {
    CDOPermission permission = revision.getPermission();
    if (permission != CDOPermission.WRITE)
    {
      revision.setPermission(CDOPermission.WRITE);
    }

    try
    {
      Object containerID = revision.getContainerID();
      InternalEObject container = getEObjectFromPotentialID(view, null, containerID);
      EObject oldContainer = instance.eContainer();
      if (oldContainer != container)
      {
        setInstanceContainer(container, revision.getContainingFeatureID());
      }
    }
    finally
    {
      if (permission != CDOPermission.WRITE)
      {
        revision.setPermission(permission);
      }
    }
  }

  /**
   * @since 4.0
   */
  protected void revisionToInstanceResource()
  {
    if (revision != null)
    {
      CDOID resourceID = revision.getResourceID();
      InternalEObject resource = getEObjectFromPotentialID(view, null, resourceID);
      setInstanceResource((Resource.Internal)resource);
      if (resource != null)
      {
        view.registerObject((InternalCDOObject)resource);
      }
    }
  }

  /**
   * @since 3.0
   */
  protected void revisionToInstanceFeature(EStructuralFeature feature)
  {
    if (feature.isUnsettable() && !view.getStore().isSet(this, feature))
    {
      // Clarify if this is sufficient for bidirectional references
      instance.eUnset(feature);
      return;
    }

    if (feature.isMany())
    {
      if (TRACER.isEnabled())
      {
        TRACER.format("State of Object (" + this + "/" + instance + ") is : " + state); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }

      if (state == CDOState.CLEAN || state == CDOState.PROXY || state == CDOState.NEW || state == CDOState.DIRTY)
      {
        int size = revision.size(feature);

        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>)instance.eGet(feature);

        clearEList(list);
        for (int i = 0; i < size; i++)
        {
          Object object = getValueFromRevision(feature, i);

          if (TRACER.isEnabled())
          {
            TRACER.format("Adding " + object + " to feature " + feature + "in instance " + instance); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          }

          list.basicAdd(object, null);
        }
      }
    }
    else
    {
      // !feature.isMany()
      Object object = getValueFromRevision(feature, 0);
      if (feature instanceof EAttribute)
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Setting attribute value " + object + " to feature " + feature + " in instance " + instance); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        // Just fake it for the store :(
        if (feature.isUnsettable() && object.equals(CDORevisionData.NIL))
        {
          eSet(feature, null);
        }
        else
        {
          eSet(feature, object);
        }
      }
      else
      {
        // EReferences
        if (TRACER.isEnabled())
        {
          TRACER.format("Adding object " + object + " to feature " + feature + " in instance " + instance); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        int featureID = instance.eClass().getFeatureID(feature);
        Class<? extends Object> baseClass = object == null ? null : object.getClass();
        EStructuralFeature.Internal internalFeature = (EStructuralFeature.Internal)feature;
        EReference oppositeReference = internalFeature.getEOpposite();

        if (oppositeReference != null)
        {
          if (object != null)
          {
            // If you have a containment reference but the container is not set, but the object is attached to a
            // resource
            // do not set the feature to null. Otherwise the object will be removed from the container which is the
            // resource instead of the original container. As a result the object will be detached. See
            // MapTest.testEObjectToEObjectValueContainedMap for more information
            if (object != instance.eContainer())
            {
              instance.eInverseAdd((InternalEObject)object, featureID, baseClass, null);
            }

            if (!EMFUtil.isPersistent(oppositeReference))
            {
              adjustTransientOppositeReference(instance, (InternalEObject)object, oppositeReference);
            }
          }
        }
        else
        {
          if (object != CDORevisionData.NIL)
          {
            EReference reference = (EReference)feature;
            if (reference.isContainment())
            {
              if (object != null)
              {
                // Calling eSet it not the optimal approach, but currently there is no other way to set the value here.
                // To avoid attaching already processed (clean) objects a check was introduced to
                // CDOResourceImpl.attached(EObject).
                // If we find a way to avoid the call of eSet and if we are able to only set the feature value directly
                // this check can be removed from CDOResourceImpl. See also Bug 352204.
                instance.eSet(feature, object);
              }
              else
              {
                instance.eSet(feature, null);
              }
            }
            else
            {
              instance.eSet(feature, object);
            }
          }
          else
          {
            instance.eSet(feature, null);
          }
        }

        if (TRACER.isEnabled())
        {
          TRACER.format("Added object " + object + " to feature " + feature + " in instance " + instance); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
      }
    }
  }

  /**
   * This method retrieves the value from the feature at the given index. It retrieves the value either from the views's
   * store or the internal pre-registration Map.
   *
   * @param feature
   *          the feature to retrieve the value from
   * @param index
   *          the given index of the object in the feature
   * @return the value from the feature at the given index
   */
  private Object getValueFromRevision(EStructuralFeature feature, int index)
  {
    Object object = revision.get(feature, index);
    if (object == null)
    {
      return null;
    }

    if (object instanceof CDOElementProxy)
    {
      // Resolve proxy
      CDOElementProxy proxy = (CDOElementProxy)object;
      object = view.getSession().resolveElementProxy(revision, feature, index, proxy.getIndex());
    }

    if (object instanceof CDOLegacyWrapper)
    {
      return ((CDOLegacyWrapper)object).cdoInternalInstance();
    }

    CDOType type = CDOModelUtil.getType(feature.getEType());
    object = view.getStore().convertToEMF(instance, revision, feature, index, object);

    if (type == CDOType.OBJECT)
    {
      if (object instanceof CDOID)
      {
        CDOID id = (CDOID)object;
        if (id.isNull())
        {
          return null;
        }

        object = getRegisteredWrapper(id);
        if (object != null)
        {
          return ((CDOLegacyWrapper)object).cdoInternalInstance();
        }

        if (id.isExternal())
        {
          object = view.getResourceSet().getEObject(URI.createURI(id.toURIFragment()), true);
        }
        else
        {
          object = view.getObject(id);
        }

        if (object instanceof CDOObjectWrapper)
        {
          return ((CDOObjectWrapper)object).cdoInternalInstance();
        }
      }
    }

    return object;
  }

  /**
   * @param feature
   *          in case that a proxy has to be created the feature that will determine the interface type of the proxy and
   *          that will be used later to resolve the proxy. <code>null</code> indicates that proxy creation will be
   *          avoided!
   */
  protected InternalEObject getEObjectFromPotentialID(InternalCDOView view, EStructuralFeature feature,
      Object potentialID)
  {
    CDOLegacyWrapper wrapper;
    if (potentialID instanceof CDOID && (wrapper = getRegisteredWrapper((CDOID)potentialID)) != null)
    {
      potentialID = wrapper.instance;

      if (TRACER.isEnabled())
      {
        TRACER.format("Getting Object (" + potentialID + ") from localThread instead of the view"); //$NON-NLS-1$ //$NON-NLS-2$
      }
    }
    else
    {
      if (potentialID instanceof CDOID)
      {
        CDOID id = (CDOID)potentialID;
        if (id.isNull())
        {
          return null;
        }

        if (id.isExternal())
        {
          URI uri = URI.createURI(id.toURIFragment());
          InternalEObject eObject = (InternalEObject)view.getResourceSet().getEObject(uri, true);
          return eObject;
        }

        boolean loadOnDemand = feature == null;
        potentialID = view.getObject(id, loadOnDemand);
        if (potentialID == null && !loadOnDemand)
        {
          return createProxy(view, feature, id);
        }
      }

      if (potentialID instanceof InternalCDOObject)
      {
        return ((InternalCDOObject)potentialID).cdoInternalInstance();
      }
    }

    return (InternalEObject)potentialID;
  }

  /**
   * Creates and returns a <em>proxy</em> object. The usage of a proxy object is strongly limited. The only guarantee
   * that can be made is that the following methods are callable and will behave in the expected way:
   * <ul>
   * <li>{@link CDOObject#cdoID()} will return the {@link CDOID} of the target object
   * <li>{@link CDOObject#cdoState()} will return {@link CDOState#PROXY PROXY}
   * <li>{@link InternalEObject#eIsProxy()} will return <code>true</code>
   * <li>{@link InternalEObject#eProxyURI()} will return the EMF proxy URI of the target object
   * </ul>
   * Calling any other method on the proxy object will result in an {@link UnsupportedOperationException} being thrown
   * at runtime. Note also that the proxy object might even not be cast to the concrete type of the target object. The
   * proxy can only guaranteed to be of <em>any</em> concrete subtype of the declared type of the given feature.
   * <p>
   * TODO {@link InternalEObject#eResolveProxy(InternalEObject)}
   */
  protected InternalEObject createProxy(InternalCDOView view, EStructuralFeature feature, CDOID id)
  {
    EClassifier eType = feature.getEType();
    Class<?> instanceClass = eType.getInstanceClass();

    Class<?>[] interfaces = { instanceClass, InternalEObject.class, LegacyProxy.class };
    ClassLoader classLoader = CDOLegacyWrapper.class.getClassLoader();
    LegacyProxyInvocationHandler handler = new LegacyProxyInvocationHandler(this, id);
    return (InternalEObject)Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

  protected void clearEList(InternalEList<?> list)
  {
    for (int i = list.size() - 1; i >= 0; --i)
    {
      Object obj = list.get(i);
      list.basicRemove(obj, null);
    }
  }

  /**
   * TODO Consider using only EMF concepts for resolving proxies!
   */
  protected void resolveAllProxies()
  {
    CDOPackageRegistry packageRegistry = cdoView().getSession().getPackageRegistry();
    EClass eClass = revision.getEClass();
    for (EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      if (feature instanceof EReference)
      {
        resolveProxies(feature, packageRegistry);
      }
    }
  }

  /**
   * IMPORTANT: Compile errors in this method might indicate an old version of EMF. Legacy support is only enabled for
   * EMF with fixed bug #247130. These compile errors do not affect native models!
   */
  protected void resolveProxies(EStructuralFeature feature, CDOPackageRegistry packageRegistry)
  {
    Object value = getInstanceValue(instance, feature, packageRegistry);
    if (value != null)
    {
      if (feature.isMany())
      {
        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>)value;
        int size = list.size();

        boolean deliver = instance.eDeliver();
        if (deliver)
        {
          instance.eSetDeliver(false);
        }

        for (int i = 0; i < size; i++)
        {
          Object element = list.get(i);
          if (element instanceof LegacyProxy)
          {
            CDOID id = ((LegacyProxy)element).getID();
            InternalCDOObject resolved = (InternalCDOObject)view.getObject(id);
            InternalEObject instance = resolved.cdoInternalInstance();

            // TODO LEGACY
            // // TODO Is InternalEList.basicSet() needed???
            // if (list instanceof
            // org.eclipse.emf.ecore.util.DelegatingInternalEList)
            // {
            // list =
            // ((org.eclipse.emf.ecore.util.DelegatingInternalEList)list).getDelegateInternalEList();
            // }

            // if (list instanceof NotifyingListImpl<?>)
            // {
            // ((NotifyingListImpl<Object>)list).basicSet(i, instance, null);
            // }
            // else
            // {
            list.set(i, instance);
            // }
          }
        }

        if (deliver)
        {
          instance.eSetDeliver(true);
        }
      }
      else
      {
        if (value instanceof LegacyProxy)
        {
          CDOID id = ((LegacyProxy)value).getID();
          InternalCDOObject resolved = (InternalCDOObject)view.getObject(id);
          InternalEObject instance = resolved.cdoInternalInstance();
          setInstanceValue(instance, feature, instance);
        }
      }
    }
  }

  protected void adjustEProxy()
  {
    // Setting eProxyURI is necessary to prevent content adapters from
    // loading the whole content tree.
    // TODO Does not have the desired effect ;-( see CDOEditor.createModel()
    if (state == CDOState.PROXY)
    {
      if (!instance.eIsProxy())
      {
        URI uri = URI.createURI(CDOProtocolConstants.PROTOCOL_NAME + ":proxy#" + id); //$NON-NLS-1$
        if (TRACER.isEnabled())
        {
          TRACER.format("Setting proxyURI {0} for {1}", uri, instance); //$NON-NLS-1$
        }

        instance.eSetProxyURI(uri);
      }
    }
    else
    {
      if (instance.eIsProxy())
      {
        if (TRACER.isEnabled())
        {
          TRACER.format("Unsetting proxyURI for {0}", instance); //$NON-NLS-1$
        }

        instance.eSetProxyURI(null);
      }
    }
  }

  @Override
  public synchronized EList<Adapter> eAdapters()
  {
    EList<Adapter> adapters = instance.eAdapters();
    for (Adapter adapter : adapters)
    {
      if (!FSMUtil.isTransient(this) && !(adapter instanceof CDOLegacyWrapper))
      {
        cdoView().handleAddAdapter(this, adapter);
      }
    }

    return adapters;
  }

  public static boolean isLegacyProxy(Object object)
  {
    return object instanceof LegacyProxy;
  }

  protected static int getEFlagMask(Class<?> instanceClass, String flagName)
  {
    Field field = ReflectUtil.getField(instanceClass, flagName);
    if (!field.isAccessible())
    {
      field.setAccessible(true);
    }

    try
    {
      return (Integer)field.get(null);
    }
    catch (IllegalAccessException ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @since 3.0
   */
  protected static CDOLegacyWrapper getRegisteredWrapper(CDOID id)
  {
    return wrapperRegistry.get().get(id);
  }

  /**
   * Adds an object to the pre-registered objects list which hold all created objects even if they are not registered in
   * the view
   *
   * @since 3.0
   */
  protected static void registerWrapper(CDOLegacyWrapper wrapper)
  {
    wrapperRegistry.get().put(wrapper.cdoID(), wrapper);
  }

  /**
   * @since 3.0
   */
  protected static void unregisterWrapper(CDOLegacyWrapper wrapper)
  {
    wrapperRegistry.get().remove(wrapper.cdoID());
  }

  /**
   * @since 3.0
   */
  protected static boolean isRegisteredWrapper(CDOLegacyWrapper wrapper)
  {
    return wrapperRegistry.get().containsKey(wrapper.cdoID());
  }

  // TODO: Remove this method if it is ensured that ist is not needed anymore
  // private void adjustOppositeReference(InternalCDOObject cdoObject, EObject oppositeObject, EReference
  // oppositeReference)
  // {
  // if (oppositeObject != null)
  // {
  // InternalCDOObject oppositeCDOObject = (InternalCDOObject)CDOUtil.getCDOObject(oppositeObject);
  //
  // if (!FSMUtil.isTransient(oppositeCDOObject) && !EMFUtil.isPersistent(oppositeReference))
  // {
  // adjustPersistentOppositeReference(cdoObject, oppositeObject, oppositeReference);
  // }
  // else
  // {
  // if (oppositeReference.isResolveProxies())
  // {
  // adjustTransientOppositeReference(instance, (InternalEObject)oppositeObject, oppositeReference);
  // }
  // }
  // }
  // }

  private void adjustPersistentOppositeReference(InternalCDOObject cdoObject, EObject oppositeObject,
      EReference oppositeReference)
  {
    InternalCDOObject oppositeCDOObject = (InternalCDOObject)CDOUtil.getCDOObject(oppositeObject);
    if (oppositeCDOObject != null)
    {
      InternalCDOView view = oppositeCDOObject.cdoView();
      if (view != null)
      {
        CDOStore store = view.getStore();
        if (store != null)
        {
          if (oppositeReference.isMany())
          {
            EObject eObject = oppositeCDOObject.cdoInternalInstance();

            @SuppressWarnings("unchecked")
            EList<Object> list = (EList<Object>)eObject.eGet(oppositeReference);
            int index = list.indexOf(instance);

            if (!store.isEmpty(oppositeCDOObject, oppositeReference) && index != EStore.NO_INDEX)
            {
              store.set(oppositeCDOObject, oppositeReference, index, cdoObject);
            }
          }
          else
          {
            store.set(oppositeCDOObject, oppositeReference, 0, cdoObject);
          }
        }
      }
    }
  }

  private void adjustTransientOppositeReference(InternalEObject instance, InternalEObject object,
      EReference oppositeReference)
  {
    boolean wasDeliver = object.eDeliver(); // Disable notifications
    if (wasDeliver)
    {
      object.eSetDeliver(false);
    }

    try
    {
      if (oppositeReference.isMany())
      {
        @SuppressWarnings("unchecked")
        InternalEList<Object> list = (InternalEList<Object>)object.eGet(oppositeReference);
        list.basicAdd(instance, null);
      }
      else
      {
        if (object.eGet(oppositeReference) != instance)
        {
          object.eInverseAdd(instance, oppositeReference.getFeatureID(), ((EObject)instance).getClass(), null);
        }
      }
    }
    finally
    {
      if (wasDeliver)
      {
        object.eSetDeliver(true);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static interface LegacyProxy
  {
    public CDOID getID();
  }

  /**
   * @author Eike Stepper
   */
  private static final class LegacyProxyInvocationHandler implements InvocationHandler, LegacyProxy
  {
    private static final Method getIDMethod = ReflectUtil.getMethod(LegacyProxy.class, "getID"); //$NON-NLS-1$

    private static final Method eIsProxyMethod = ReflectUtil.getMethod(EObject.class, "eIsProxy"); //$NON-NLS-1$

    private static final Method eProxyURIMethod = ReflectUtil.getMethod(InternalEObject.class, "eProxyURI"); //$NON-NLS-1$

    private CDOLegacyWrapper wrapper;

    private CDOID id;

    public LegacyProxyInvocationHandler(CDOLegacyWrapper wrapper, CDOID id)
    {
      this.wrapper = wrapper;
      this.id = id;
    }

    public CDOID getID()
    {
      return id;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      if (method.equals(getIDMethod))
      {
        return id;
      }

      if (method.equals(eIsProxyMethod))
      {
        return true;
      }

      if (method.equals(eProxyURIMethod))
      {
        // Use container's resource because it's guaranteed to be in the same CDOView as the resource of the target!
        Resource resource = wrapper.eResource();

        // TODO Consider using a "fake" Resource implementation. See Resource.getEObject(...)
        return resource.getURI().appendFragment(id.toURIFragment());
      }

      // A client must have invoked the proxy while being told not to do so!
      throw new UnsupportedOperationException(method.getName());
    }
  }

  /**
   * @author Martin Fluegge
   */
  private static final class Counter
  {
    private int value;

    public Counter()
    {
    }

    public void increment()
    {
      ++value;
    }

    public int decrement()
    {
      return --value;
    }
  }
}
