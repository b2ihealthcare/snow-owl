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
package org.eclipse.emf.cdo.internal.common.commit;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.common.commit.CDOChangeKind;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeKindCache;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

/**
 * @author Eike Stepper
 */
public class CDOChangeSetDataImpl implements CDOChangeSetData
{
  private List<CDOIDAndVersion> newObjects;

  private List<CDORevisionKey> changedObjects;

  private List<CDOIDAndVersion> detachedObjects;

  private CDOChangeKindCache changeKindCache;

  public CDOChangeSetDataImpl(List<CDOIDAndVersion> newObjects, List<CDORevisionKey> changedObjects,
      List<CDOIDAndVersion> detachedObjects)
  {
    this.newObjects = newObjects;
    this.changedObjects = changedObjects;
    this.detachedObjects = detachedObjects;
  }

  public CDOChangeSetDataImpl()
  {
    this(new ArrayList<CDOIDAndVersion>(), new ArrayList<CDORevisionKey>(), new ArrayList<CDOIDAndVersion>());
  }

  public boolean isEmpty()
  {
    if (newObjects != null && !newObjects.isEmpty())
    {
      return false;
    }

    if (changedObjects != null && !changedObjects.isEmpty())
    {
      return false;
    }

    if (detachedObjects != null && !detachedObjects.isEmpty())
    {
      return false;
    }

    return true;
  }

  public CDOChangeSetData copy()
  {
    List<CDOIDAndVersion> newObjectsCopy = new ArrayList<CDOIDAndVersion>(newObjects.size());
    for (CDOIDAndVersion key : newObjects)
    {
      if (key instanceof CDORevision)
      {
        CDORevision revision = (CDORevision)key;
        newObjectsCopy.add(revision.copy());
      }
      else
      {
        newObjectsCopy.add(key);
      }
    }

    List<CDORevisionKey> changedObjectsCopy = new ArrayList<CDORevisionKey>(changedObjects.size());
    for (CDORevisionKey key : changedObjects)
    {
      if (key instanceof CDORevisionDelta)
      {
        CDORevisionDelta delta = (CDORevisionDelta)key;
        changedObjectsCopy.add(delta.copy());
      }
      else
      {
        changedObjectsCopy.add(key);
      }
    }

    List<CDOIDAndVersion> detachedObjectsCopy = new ArrayList<CDOIDAndVersion>(detachedObjects.size());
    for (CDOIDAndVersion key : detachedObjects)
    {
      detachedObjectsCopy.add(key);
    }

    return new CDOChangeSetDataImpl(newObjectsCopy, changedObjectsCopy, detachedObjectsCopy);
  }

  public void merge(CDOChangeSetData changeSetData)
  {
    Map<CDOID, CDOIDAndVersion> newMap = new HashMap<CDOID, CDOIDAndVersion>();
    fillMap(newMap, newObjects);
    fillMap(newMap, changeSetData.getNewObjects());

    Map<CDOID, CDORevisionKey> changedMap = new HashMap<CDOID, CDORevisionKey>();
    fillMap(changedMap, changedObjects);
    for (CDORevisionKey key : changeSetData.getChangedObjects())
    {
      mergeChangedObject(key, newMap, changedMap);
    }

    Map<CDOID, CDOIDAndVersion> detachedMap = new HashMap<CDOID, CDOIDAndVersion>();
    fillMap(detachedMap, detachedObjects);
    for (CDOIDAndVersion key : changeSetData.getDetachedObjects())
    {
      CDOID id = key.getID();
      if (newMap.remove(id) == null)
      {
        detachedMap.put(id, key);
      }
    }

    newObjects = new ArrayList<CDOIDAndVersion>(newMap.values());
    changedObjects = new ArrayList<CDORevisionKey>(changedMap.values());
    detachedObjects = new ArrayList<CDOIDAndVersion>(detachedMap.values());
  }

  private void mergeChangedObject(CDORevisionKey key, Map<CDOID, CDOIDAndVersion> newMap,
      Map<CDOID, CDORevisionKey> changedMap)
  {
    CDOID id = key.getID();
    if (key instanceof CDORevisionDelta)
    {
      CDORevisionDelta delta = (CDORevisionDelta)key;

      // Try to add the delta to existing new revision
      CDOIDAndVersion oldRevision = newMap.get(id);
      if (oldRevision instanceof CDORevision)
      {
        CDORevision newRevision = (CDORevision)oldRevision;
        delta.apply(newRevision);
        return;
      }

      // Try to add the delta to existing delta
      CDORevisionKey oldDelta = changedMap.get(id);
      if (oldDelta instanceof CDORevisionDelta)
      {
        InternalCDORevisionDelta newDelta = (InternalCDORevisionDelta)oldDelta;
        for (CDOFeatureDelta featureDelta : delta.getFeatureDeltas())
        {
          newDelta.addFeatureDelta(featureDelta);
        }

        return;
      }
    }

    // Fall back
    changedMap.put(id, key);
  }

  public List<CDOIDAndVersion> getNewObjects()
  {
    return newObjects;
  }

  public List<CDORevisionKey> getChangedObjects()
  {
    return changedObjects;
  }

  public List<CDOIDAndVersion> getDetachedObjects()
  {
    return detachedObjects;
  }

  public synchronized Map<CDOID, CDOChangeKind> getChangeKinds()
  {
    if (changeKindCache == null)
    {
      changeKindCache = new CDOChangeKindCache(this);
    }

    return changeKindCache;
  }

  public CDOChangeKind getChangeKind(CDOID id)
  {
    return getChangeKinds().get(id);
  }

  @Override
  public String toString()
  {
    return MessageFormat
        .format(
            "ChangeSetData[newObjects={0}, changedObjects={1}, detachedObjects={2}]", newObjects.size(), changedObjects.size(), detachedObjects.size()); //$NON-NLS-1$
  }

  private static <T extends CDOIDAndVersion> void fillMap(Map<CDOID, T> map, Collection<T> c)
  {
    for (T key : c)
    {
      map.put(key.getID(), key);
    }
  }
}
