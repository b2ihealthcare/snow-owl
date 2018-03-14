/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 201266
 *    Eike Stepper & Simon McDuff - bug 204890
 *    Simon McDuff - bug 246705
 *    Simon McDuff - bug 246622
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOAddFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOContainerFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOMoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOSetFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOUnsetFeatureDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOFeatureAnalyzer;
import org.eclipse.emf.cdo.view.CDORevisionPrefetchingPolicy;

import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.InternalEObject.EStore;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.text.MessageFormat;
import java.util.List;

/**
 * CDORevision needs to follow these rules:<br>
 * - Keep CDOID only when the object (!isNew && !isTransient) // Only when CDOID will not changed.<br>
 * - Keep EObject for external reference, new, transient and that until commit time.<br>
 * It is important since these objects could changed and we need to keep a reference to {@link EObject} until the end.
 * It is the reason why {@link CDOStoreImpl} always call {@link InternalCDOView#convertObjectToID(Object, boolean)} with
 * true.
 *
 * @author Eike Stepper
 */
public final class CDOStoreImpl implements CDOStore
{
  private final ContextTracer TRACER = new ContextTracer(OM.DEBUG_STORE, CDOStoreImpl.class);

  private InternalCDOView view;

  /**
   * @since 2.0
   */
  public CDOStoreImpl(InternalCDOView view)
  {
    this.view = view;
  }

  /**
   * @since 2.0
   */
  public InternalCDOView getView()
  {
    return view;
  }

  /**
   * @since 2.0
   */
  public void setContainer(InternalEObject eObject, CDOResource newResource, InternalEObject newEContainer,
      int newContainerFeatureID)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("setContainer({0}, {1}, {2}, {3})", cdoObject, newResource, newEContainer, newContainerFeatureID); //$NON-NLS-1$
      }

      Object newContainerID = newEContainer == null ? CDOID.NULL : cdoObject.cdoView().convertObjectToID(newEContainer,
          true);
      CDOID newResourceID = newResource == null ? CDOID.NULL : newResource.cdoID();

      CDOFeatureDelta delta = new CDOContainerFeatureDeltaImpl(newResourceID, newContainerID, newContainerFeatureID);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      revision.setResourceID(newResourceID);
      revision.setContainerID(newContainerID);
      revision.setContainingFeatureID(newContainerFeatureID);
    }
  }

  public InternalEObject getContainer(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getContainer({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return (InternalEObject)convertIDToObject(cdoObject.cdoView(), cdoObject,
          EcorePackage.eINSTANCE.eContainingFeature(), -1, revision.getContainerID());
    }
  }

  public int getContainingFeatureID(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getContainingFeatureID({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.getContainingFeatureID();
    }
  }

  /**
   * @since 2.0
   */
  public InternalEObject getResource(InternalEObject eObject)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("getResource({0})", cdoObject); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return (InternalEObject)convertIDToObject(cdoObject.cdoView(), cdoObject,
          EcorePackage.eINSTANCE.eContainingFeature(), -1, revision.getResourceID());
    }
  }

  public EStructuralFeature getContainingFeature(InternalEObject eObject)
  {
    throw new UnsupportedOperationException("Use getContainingFeatureID() instead"); //$NON-NLS-1$
  }

  public Object get(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("get({0}, {1}, {2})", cdoObject, feature, index); //$NON-NLS-1$
      }

      CDOFeatureAnalyzer featureAnalyzer = view.options().getFeatureAnalyzer();

      featureAnalyzer.preTraverseFeature(cdoObject, feature, index);
      InternalCDORevision revision = getRevisionForReading(cdoObject);

      Object value = revision.get(feature, index);
      value = convertToEMF(eObject, revision, feature, index, value);

      featureAnalyzer.postTraverseFeature(cdoObject, feature, index, value);
      return value;
    }
  }

  public boolean isSet(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("isSet({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      if (!feature.isUnsettable())
      {
        if (feature.isMany())
        {
          InternalCDORevision revision = getRevisionForReading(cdoObject);
          CDOList list = revision.getList(feature);
          return list != null && !list.isEmpty();
        }

        Object value = eObject.eGet(feature);
        Object defaultValue = feature.getDefaultValue();
        return !ObjectUtil.equals(value, defaultValue);
      }

      // TODO This get() may not work for lists, see above
      Object value = get(eObject, feature, NO_INDEX);
      return value != null;
    }
  }

  public int size(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("size({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.size(feature);
    }
  }

  public boolean isEmpty(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("isEmpty({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.isEmpty(feature);
    }
  }

  public boolean contains(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("contains({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      Object convertedValue = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      boolean result = revision.contains(feature, convertedValue);

      // Special handling of detached (TRANSIENT) objects, see bug 354395
      if (!result && value != convertedValue && value instanceof EObject)
      {
        result = revision.contains(feature, value);
      }

      return result;
    }
  }

  public int indexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("indexOf({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.indexOf(feature, value);
    }
  }

  public int lastIndexOf(InternalEObject eObject, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("lastIndexOf({0}, {1}, {2})", cdoObject, feature, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.lastIndexOf(feature, value);
    }
  }

  public int hashCode(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("hashCode({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      return revision.hashCode(feature);
    }
  }

  public Object[] toArray(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("toArray({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      InternalCDORevision revision = getRevisionForReading(cdoObject);
      Object[] result = revision.toArray(feature);
      for (int i = 0; i < result.length; i++)
      {
        result[i] = convertToEMF(eObject, revision, feature, i, result[i]);
      }

      // // TODO Clarify feature maps
      // if (feature instanceof EReference)
      // {
      // for (int i = 0; i < result.length; i++)
      // {
      // result[i] = resolveProxy(revision, feature, i, result[i]);
      // result[i] = convertIdToObject(cdoObject.cdoView(), eObject, feature, i, result[i]);
      // }
      // }

      return result;
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T[] toArray(InternalEObject eObject, EStructuralFeature feature, T[] a)
  {
    synchronized (view)
    {
      Object[] array = toArray(eObject, feature);
      int size = array.length;

      if (a.length < size)
      {
        a = (T[])java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size);
      }

      System.arraycopy(array, 0, a, 0, size);
      if (a.length > size)
      {
        a[size] = null;
      }

      return a;
    }
  }

  public Object set(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("set({0}, {1}, {2}, {3})", cdoObject, feature, index, value); //$NON-NLS-1$
      }

      value = convertToCDO(cdoObject, feature, value);

      InternalCDORevision oldRevision = getRevisionForReading(cdoObject);
      Object oldValue = oldRevision.get(feature, index);
      oldValue = convertToEMF(eObject, oldRevision, feature, index, oldValue);

      CDOFeatureDelta delta = new CDOSetFeatureDeltaImpl(feature, index, value, oldValue);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      revision.set(feature, index, value);

      return oldValue;
    }
  }

  public void unset(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("unset({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      CDOFeatureDelta delta = new CDOUnsetFeatureDeltaImpl(feature);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);

      if (feature.isUnsettable())
      {
        revision.unset(feature);
      }
      else
      {
        if (feature.isMany())
        {
          Object value = revision.getValue(feature);

          @SuppressWarnings("unchecked")
          List<Object> list = (List<Object>)value;
          list.clear();
        }
        else
        {
          Object defaultValue = convertToCDO(cdoObject, feature, feature.getDefaultValue());
          revision.set(feature, NO_INDEX, defaultValue);
        }
      }
    }
  }

  public void add(InternalEObject eObject, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("add({0}, {1}, {2}, {3})", cdoObject, feature, index, value); //$NON-NLS-1$
      }

      if (feature.isMany())
      {
        value = convertToCDO(cdoObject, feature, value);
      }
      else
      {
        throw new UnsupportedOperationException("ADD is not supported for single-valued features");
      }

      CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, value);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      revision.add(feature, index, value);
    }
  }

  public Object remove(InternalEObject eObject, EStructuralFeature feature, int index)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("remove({0}, {1}, {2})", cdoObject, feature, index); //$NON-NLS-1$
      }

      Object oldValue = null;

      // Bugzilla 293283 / 314387
      if (feature.isMany())
      {
        InternalCDORevision readLockedRevision = getRevisionForReading(cdoObject);
        CDOList list = readLockedRevision.getList(feature);
        int size = list.size();
        if (index < 0 || size <= index)
        {
          throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
      }
      else
      {
        throw new UnsupportedOperationException("REMOVE is not supported for single-valued features");
      }

      CDORemoveFeatureDeltaImpl delta = new CDORemoveFeatureDeltaImpl(feature, index);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);

      oldValue = revision.get(feature, index);
      delta.setValue(oldValue);

      try
      {
        oldValue = convertToEMF(eObject, revision, feature, index, oldValue);
      }
      finally
      {
        revision.remove(feature, index);

        /*
         * XXX: If the list is completely empty at this point, add a CDOClearFeatureDelta so that list mappings can
         * optimize for removal; makes writes faster when the list is slowly drained empty over multiple add/remove
         * operations.
         */
        if (revision.isEmpty(feature))
        {
          clear(eObject, feature);
        }
//        else
//        {
//          /*
//           * In unordered lists, move the last item only if the removed item was not the last item, or the item before
//           * the last item. Note that list.size() returns the size of the list after the remove at this point.
//           */
//          int lastIndexAfterRemove = revision.size(feature) - 1;
//          if (!feature.isOrdered() && index < lastIndexAfterRemove)
//          {
//            move(eObject, feature, index, lastIndexAfterRemove);
//          }
//        }
      }

      return oldValue;
    }
  }

  public void clear(InternalEObject eObject, EStructuralFeature feature)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("clear({0}, {1})", cdoObject, feature); //$NON-NLS-1$
      }

      CDOFeatureDelta delta = new CDOClearFeatureDeltaImpl(feature);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      // TODO Handle containment remove!!!
      revision.clear(feature);
    }
  }

  public Object move(InternalEObject eObject, EStructuralFeature feature, int target, int source)
  {
    synchronized (view)
    {
      InternalCDOObject cdoObject = getCDOObject(eObject);
      if (TRACER.isEnabled())
      {
        TRACER.format("move({0}, {1}, {2}, {3})", cdoObject, feature, target, source); //$NON-NLS-1$
      }

      CDOFeatureDelta delta = new CDOMoveFeatureDeltaImpl(feature, target, source);
      InternalCDORevision revision = getRevisionForWriting(cdoObject, delta);
      Object result = revision.move(feature, target, source);

      result = convertToEMF(eObject, revision, feature, EStore.NO_INDEX, result);
      return result;
    }
  }

  public EObject create(EClass eClass)
  {
    throw new UnsupportedOperationException("Use the generated factory to create objects"); //$NON-NLS-1$
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOStore[{0}]", view); //$NON-NLS-1$
  }

  /**
   * @since 2.0
   */
  public Object resolveProxy(InternalCDORevision revision, EStructuralFeature feature, int index, Object value)
  {
    synchronized (view)
    {
      if (value instanceof CDOElementProxy)
      {
        // Resolve proxy
        CDOElementProxy proxy = (CDOElementProxy)value;
        value = view.getSession().resolveElementProxy(revision, feature, index, proxy.getIndex());
      }

      return value;
    }
  }

  /**
   * @since 3.0
   */
  public Object convertToCDO(InternalCDOObject object, EStructuralFeature feature, Object value)
  {
    synchronized (view)
    {
      if (value != null)
      {
        if (feature instanceof EReference)
        {
          value = view.convertObjectToID(value, true);
        }
        else if (FeatureMapUtil.isFeatureMap(feature))
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)value;
          EStructuralFeature innerFeature = entry.getEStructuralFeature();
          Object innerValue = entry.getValue();
          Object convertedValue = view.convertObjectToID(innerValue);
          if (convertedValue != innerValue)
          {
            value = CDORevisionUtil.createFeatureMapEntry(innerFeature, convertedValue);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          if (type != null)
          {
            value = type.convertToCDO(feature.getEType(), value);
          }
        }
      }

      return value;
    }
  }

  /**
   * @since 2.0
   */
  public Object convertToEMF(EObject eObject, InternalCDORevision revision, EStructuralFeature feature, int index,
      Object value)
  {
    synchronized (view)
    {
      if (value != null)
      {
        if (feature.isMany())
        {
          if (index == EStore.NO_INDEX)
          {
            return value;
          }

          value = resolveProxy(revision, feature, index, value);
          if (value instanceof CDOID)
          {
            CDOID id = (CDOID)value;
            CDOList list = revision.getList(feature);
            CDORevisionPrefetchingPolicy policy = view.options().getRevisionPrefetchingPolicy();
            InternalCDORevisionManager revisionManager = view.getSession().getRevisionManager();
            List<CDOID> listOfIDs = policy.loadAhead(revisionManager, view, eObject, feature, list, index, id);
            if (!listOfIDs.isEmpty())
            {
              int initialChunkSize = view.getSession().options().getCollectionLoadingPolicy().getInitialChunkSize();
              revisionManager.getRevisions(listOfIDs, view, initialChunkSize, CDORevision.DEPTH_NONE, true);
            }
          }
        }

        if (feature instanceof EReference)
        {
          value = convertIDToObject(view, eObject, feature, index, value);
        }
        else if (FeatureMapUtil.isFeatureMap(feature))
        {
          FeatureMap.Entry entry = (FeatureMap.Entry)value;
          EStructuralFeature innerFeature = entry.getEStructuralFeature();
          Object innerValue = entry.getValue();
          Object convertedValue = convertIDToObject(view, eObject, feature, index, innerValue);
          if (convertedValue != innerValue)
          {
            value = FeatureMapUtil.createEntry(innerFeature, convertedValue);
          }
        }
        else
        {
          CDOType type = CDOModelUtil.getType(feature.getEType());
          if (type != null)
          {
            value = type.convertToEMF(feature.getEType(), value);
          }
        }
      }

      return value;
    }
  }

  private Object convertIDToObject(InternalCDOView view, EObject eObject, EStructuralFeature feature, int index,
      Object value)
  {
    try
    {
      value = view.convertIDToObject(value);
    }
    catch (ObjectNotFoundException ex)
    {
      if (value instanceof CDOID)
      {
        value = view.options().getStaleReferencePolicy().processStaleReference(eObject, feature, index, ex.getID());
      }
    }

    return value;
  }

  private InternalCDOObject getCDOObject(Object object)
  {
    return FSMUtil.adapt(object, view);
  }

  private static InternalCDORevision getRevisionForReading(InternalCDOObject cdoObject)
  {
    CDOStateMachine.INSTANCE.read(cdoObject);
    return getRevision(cdoObject);
  }

  private static InternalCDORevision getRevisionForWriting(InternalCDOObject cdoObject, CDOFeatureDelta delta)
  {
    CDOStateMachine.INSTANCE.write(cdoObject, delta);
    return getRevision(cdoObject);
  }

  private static InternalCDORevision getRevision(InternalCDOObject cdoObject)
  {
    InternalCDORevision revision = cdoObject.cdoRevision();
    if (revision == null)
    {
      throw new IllegalStateException("revision == null");
    }

    return revision;
  }
}
