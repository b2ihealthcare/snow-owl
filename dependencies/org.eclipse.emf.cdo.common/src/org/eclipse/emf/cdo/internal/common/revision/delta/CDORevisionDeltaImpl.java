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
 *    Simon McDuff - bug 204890
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOWithID;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOElementProxy;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevisable;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionData;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOUnsetFeatureDelta;
import org.eclipse.emf.cdo.common.util.PartialCollectionLoadingNotSupportedException;
import org.eclipse.emf.cdo.common.util.UnorderedListDifferenceAnalyzer;
import org.eclipse.emf.cdo.internal.common.revision.CDOListImpl;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.change.ListChange;
import org.eclipse.emf.ecore.change.util.ListDifferenceAnalyzer;

/**
 * @author Eike Stepper
 */
public class CDORevisionDeltaImpl implements InternalCDORevisionDelta
{
  private EClass eClass;

  private CDOID id;

  private CDOBranch branch;

  private int version;

  private CDORevisable target;

  private Map<EStructuralFeature, CDOFeatureDelta> featureDeltas = new HashMap<EStructuralFeature, CDOFeatureDelta>();

  public CDORevisionDeltaImpl(CDORevision revision)
  {
    eClass = revision.getEClass();
    id = revision.getID();
    branch = revision.getBranch();
    version = revision.getVersion();
  }

  public CDORevisionDeltaImpl(CDORevisionDelta revisionDelta, boolean copyFeatureDeltas)
  {
    eClass = revisionDelta.getEClass();
    id = revisionDelta.getID();
    branch = revisionDelta.getBranch();
    version = revisionDelta.getVersion();

    if (copyFeatureDeltas)
    {
      for (CDOFeatureDelta delta : revisionDelta.getFeatureDeltas())
      {
        addFeatureDelta(((InternalCDOFeatureDelta)delta).copy());
      }
    }
  }

  public CDORevisionDeltaImpl(CDORevision sourceRevision, CDORevision targetRevision)
  {
    if (sourceRevision.getEClass() != targetRevision.getEClass())
    {
      throw new IllegalArgumentException();
    }

    eClass = sourceRevision.getEClass();
    id = sourceRevision.getID();
    branch = sourceRevision.getBranch();
    version = sourceRevision.getVersion();
    target = CDORevisionUtil.copyRevisable(targetRevision);

    compare(sourceRevision, targetRevision);

    CDORevisionData originData = sourceRevision.data();
    CDORevisionData dirtyData = targetRevision.data();

    Object dirtyContainerID = dirtyData.getContainerID();
    if (dirtyContainerID instanceof CDOWithID)
    {
      dirtyContainerID = ((CDOWithID)dirtyContainerID).cdoID();
    }

    if (!compare(originData.getContainerID(), dirtyContainerID)
        || !compare(originData.getContainingFeatureID(), dirtyData.getContainingFeatureID())
        || !compare(originData.getResourceID(), dirtyData.getResourceID()))
    {
      addFeatureDelta(new CDOContainerFeatureDeltaImpl(dirtyData.getResourceID(), dirtyContainerID,
          dirtyData.getContainingFeatureID()));
    }
  }

  public CDORevisionDeltaImpl(CDODataInput in) throws IOException
  {
    eClass = (EClass)in.readCDOClassifierRefAndResolve();
    id = in.readCDOID();
    branch = in.readCDOBranch();
    version = in.readInt();
    if (version < 0)
    {
      version = -version;
      target = in.readCDORevisable();
    }

    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      CDOFeatureDelta featureDelta = in.readCDOFeatureDelta(eClass);
      featureDeltas.put(featureDelta.getFeature(), featureDelta);
    }
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOClassifierRef(eClass);
    out.writeCDOID(id);
    out.writeCDOBranch(branch);
    if (target == null)
    {
      out.writeInt(version);
    }
    else
    {
      out.writeInt(-version);
      out.writeCDORevisable(target);
    }

    out.writeInt(featureDeltas.size());
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      out.writeCDOFeatureDelta(eClass, featureDelta);
    }
  }

  public EClass getEClass()
  {
    return eClass;
  }

  public CDOID getID()
  {
    return id;
  }

  public CDOBranch getBranch()
  {
    return branch;
  }

  public void setBranch(CDOBranch branch)
  {
    this.branch = branch;
  }

  public int getVersion()
  {
    return version;
  }

  public void setVersion(int version)
  {
    this.version = version;
  }

  public CDORevisable getTarget()
  {
    return target;
  }

  public void setTarget(CDORevisable target)
  {
    this.target = target;
  }

  public boolean isEmpty()
  {
    return featureDeltas.isEmpty();
  }

  public CDORevisionDelta copy()
  {
    return new CDORevisionDeltaImpl(this, true);
  }

  public Map<EStructuralFeature, CDOFeatureDelta> getFeatureDeltaMap()
  {
    return featureDeltas;
  }

  public CDOFeatureDelta getFeatureDelta(EStructuralFeature feature)
  {
    return featureDeltas.get(feature);
  }

  public List<CDOFeatureDelta> getFeatureDeltas()
  {
    return new ArrayList<CDOFeatureDelta>(featureDeltas.values());
  }

  public void apply(CDORevision revision)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).apply(revision);
    }
  }

  public void addFeatureDelta(CDOFeatureDelta delta)
  {
    if (delta instanceof CDOListFeatureDelta)
    {
      CDOListFeatureDelta deltas = (CDOListFeatureDelta)delta;
      for (CDOFeatureDelta childDelta : deltas.getListChanges())
      {
        addFeatureDelta(childDelta);
      }
    }
    else
    {
      addSingleFeatureDelta(delta);
    }
  }

  private void addSingleFeatureDelta(CDOFeatureDelta delta)
  {
    EStructuralFeature feature = delta.getFeature();
    if (feature.isMany())
    {
      CDOListFeatureDeltaImpl listDelta = (CDOListFeatureDeltaImpl)featureDeltas.get(feature);
      if (listDelta == null)
      {
        listDelta = new CDOListFeatureDeltaImpl(feature);
        featureDeltas.put(listDelta.getFeature(), listDelta);
      }

      // Remove all previous changes
      if (delta instanceof CDOClearFeatureDelta || delta instanceof CDOUnsetFeatureDelta)
      {
        listDelta.getListChanges().clear();
      }

      listDelta.add(delta);
    }
    else
    {
      featureDeltas.put(feature, delta);
    }
  }

  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    boolean changed = false;
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      changed |= ((CDOFeatureDeltaImpl)featureDelta).adjustReferences(referenceAdjuster);
    }

    return changed;
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas.values())
    {
      ((CDOFeatureDeltaImpl)featureDelta).accept(visitor);
    }
  }

  private void compare(CDORevision originRevision, CDORevision dirtyRevision)
  {
    CDORevisionData originData = originRevision.data();
    CDORevisionData dirtyData = dirtyRevision.data();

    for (final EStructuralFeature feature : CDOModelUtil.getAllPersistentFeatures(eClass))
    {
      if (feature.isMany())
      {
        if (originData.size(feature) > 0 && dirtyData.size(feature) == 0)
        {
          addFeatureDelta(new CDOClearFeatureDeltaImpl(feature));
        }
        else
        {
          CDOListFeatureDelta listFeatureDelta = new CDOListFeatureDeltaImpl(feature);
          List<CDOFeatureDelta> changes = listFeatureDelta.getListChanges();
          CDOList originList = ((InternalCDORevision)originRevision).getList(feature);
          CDOList dirtyList = ((InternalCDORevision)dirtyRevision).getList(feature);
          checkNoProxies(originList);
          checkNoProxies(dirtyList);

          if (!feature.isOrdered())
          {
            compareUnorderedList(feature, originList, dirtyList, changes);
          }
          else
          {
            compareOrderedList(feature, originList, dirtyList, changes);
          }

          if (!changes.isEmpty())
          {
            featureDeltas.put(feature, listFeatureDelta);
          }
        }
      }
      else
      {
        Object originValue = originData.get(feature, 0);
        Object dirtyValue = dirtyData.get(feature, 0);
        if (!compare(originValue, dirtyValue))
        {
          if (dirtyValue == null)
          {
            addFeatureDelta(new CDOUnsetFeatureDeltaImpl(feature));
          }
          else
          {
            addFeatureDelta(new CDOSetFeatureDeltaImpl(feature, 0, dirtyValue, originValue));
          }
        }
      }
    }
  }
  
  private void checkNoProxies(EList<?> list)
  {
    for (Object element : list)
    {
      if (element instanceof CDOElementProxy || element == CDOListImpl.UNINITIALIZED)
      {
        throw new PartialCollectionLoadingNotSupportedException("List contains proxy elements");
      }
    }
  }

  private void compareUnorderedList(final EStructuralFeature feature, final CDOList originList, final CDOList dirtyList, final List<CDOFeatureDelta> changes) 
  {
    final CDOListImpl oldListClone = new CDOListImpl(originList.size(), 0, false);
    oldListClone.addAll(originList);
      
    final UnorderedListDifferenceAnalyzer analyzer = new UnorderedListDifferenceAnalyzer()
    {
      @Override
      protected void createAddListChange(CDOList oldList, Object newObject, int index) 
      {
        CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, newObject);
        changes.add(delta);
        super.createAddListChange(oldList, newObject, index);
      }

      @Override
      protected void createRemoveListChange(CDOList oldList, Object oldObject, int index) 
      {
        CDORemoveFeatureDeltaImpl delta = new CDORemoveFeatureDeltaImpl(feature, index);
        delta.setValue(oldObject);
        changes.add(delta);
        super.createRemoveListChange(oldList, oldObject, index);
      }
    };
      
    analyzer.createListChanges(oldListClone, dirtyList);
  }

  private void compareOrderedList(final EStructuralFeature feature, final CDOList originList, final CDOList dirtyList, final List<CDOFeatureDelta> changes) 
  {
    ListDifferenceAnalyzer analyzer = new ListDifferenceAnalyzer()
    {
      @Override
      protected void createAddListChange(EList<Object> oldList, EList<ListChange> listChanges, Object value,
          int index)
      {
        CDOFeatureDelta delta = new CDOAddFeatureDeltaImpl(feature, index, value);
        changes.add(delta);
        oldList.add(index, value);
      }

      @Override
      protected void createRemoveListChange(EList<?> oldList, EList<ListChange> listChanges, Object value,
          int index)
      {
        CDORemoveFeatureDeltaImpl delta = new CDORemoveFeatureDeltaImpl(feature, index);
        // fix until ListDifferenceAnalyzer delivers the correct value (bug #308618).
        delta.setValue(oldList.get(index));
        changes.add(delta);
        oldList.remove(index);
      }

      @Override
      protected void createMoveListChange(EList<?> oldList, EList<ListChange> listChanges, Object value,
          int index, int toIndex)
      {
        CDOMoveFeatureDeltaImpl delta = new CDOMoveFeatureDeltaImpl(feature, toIndex, index);
        // fix until ListDifferenceAnalyzer delivers the correct value (same problem as bug #308618).
        delta.setValue(oldList.get(index));
        changes.add(delta);
        oldList.move(toIndex, index);
      }
    };

    analyzer.analyzeLists(originList, dirtyList, new NOOPList());
  }

  private boolean compare(Object originValue, Object dirtyValue)
  {
    return originValue == dirtyValue || originValue != null && dirtyValue != null && originValue.equals(dirtyValue);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDORevisionDelta[{0}@{1}:{2}v{3} --> {4}]", eClass.getName(), id, branch.getID(),
        version, featureDeltas.values());
  }

  /**
   * @author Eike Stepper
   */
  public static class NOOPList implements EList<ListChange>
  {
    private static final EList<ListChange> LIST = ECollections.emptyEList();

    public NOOPList()
    {
    }

    public int size()
    {
      return 0;
    }

    public boolean isEmpty()
    {
      return true;
    }

    public boolean contains(Object o)
    {
      return false;
    }

    public Iterator<ListChange> iterator()
    {
      return LIST.iterator();
    }

    public Object[] toArray()
    {
      return LIST.toArray();
    }

    public <T> T[] toArray(T[] a)
    {
      return LIST.toArray(a);
    }

    public boolean add(ListChange o)
    {
      return false;
    }

    public boolean remove(Object o)
    {
      return false;
    }

    public boolean containsAll(Collection<?> c)
    {
      return false;
    }

    public boolean addAll(Collection<? extends ListChange> c)
    {
      return false;
    }

    public boolean addAll(int index, Collection<? extends ListChange> c)
    {
      return false;
    }

    public boolean removeAll(Collection<?> c)
    {
      return false;
    }

    public boolean retainAll(Collection<?> c)
    {
      return false;
    }

    public void clear()
    {
    }

    public ListChange get(int index)
    {
      return LIST.get(index);
    }

    public ListChange set(int index, ListChange element)
    {
      return null;
    }

    public void add(int index, ListChange element)
    {
    }

    public ListChange remove(int index)
    {
      return null;
    }

    public int indexOf(Object o)
    {
      return LIST.indexOf(o);
    }

    public int lastIndexOf(Object o)
    {
      return LIST.lastIndexOf(o);
    }

    public ListIterator<ListChange> listIterator()
    {
      return LIST.listIterator();
    }

    public ListIterator<ListChange> listIterator(int index)
    {
      return LIST.listIterator(index);
    }

    public List<ListChange> subList(int fromIndex, int toIndex)
    {
      return LIST.subList(fromIndex, toIndex);
    }

    public void move(int newPosition, ListChange object)
    {
    }

    public ListChange move(int newPosition, int oldPosition)
    {
      return null;
    }
  }
}
