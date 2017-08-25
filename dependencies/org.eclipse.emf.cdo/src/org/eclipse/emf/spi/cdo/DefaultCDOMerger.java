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
package org.eclipse.emf.spi.cdo;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;
import org.eclipse.emf.cdo.transaction.CDOMerger;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.net4j.util.collection.Pair;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public class DefaultCDOMerger implements CDOMerger
{
  private CDOChangeSetData result;

  private Map<CDOID, Conflict> conflicts;

  private Map<CDOID, Object> targetMap;

  private Map<CDOID, Object> sourceMap;

  public DefaultCDOMerger()
  {
  }

  public CDOChangeSetData getResult()
  {
    return result;
  }

  public Map<CDOID, Conflict> getConflicts()
  {
    return conflicts;
  }

  public synchronized CDOChangeSetData merge(CDOChangeSet target, CDOChangeSet source) throws ConflictException
  {
    result = new CDOChangeSetDataImpl();
    conflicts = new HashMap<CDOID, Conflict>();

    targetMap = Collections.unmodifiableMap(createMap(target));
    sourceMap = Collections.unmodifiableMap(createMap(source));

    preProcess();
    
    for (Entry<CDOID, Object> entry : sourceMap.entrySet())
    {
      CDOID id = entry.getKey();
      Object sourceData = entry.getValue();
      Object targetData = targetMap.get(id);
      merge(targetData, sourceData);
    }

    // XXX: We only consider conflicts from the source changes' point of view
    
    if (!conflicts.isEmpty())
    {
      throw new ConflictException("Merger could not resolve all conflicts: " + conflicts, this, result);
    }

    return result;
  }

  protected void preProcess() {
  }

  protected boolean merge(Object targetData, Object sourceData)
  {
    Object data = null;
    if (sourceData == null)
    {
      if (targetData instanceof CDORevision)
      {
        data = addedInTarget((CDORevision)targetData);
      }
      else if (targetData instanceof CDORevisionDelta)
      {
        data = changedInTarget((CDORevisionDelta)targetData);
      }
      else if (targetData instanceof CDOID)
      {
        data = detachedInTarget((CDOID)targetData);
      }
    }
    else if (targetData == null)
    {
      if (sourceData instanceof CDORevision)
      {
        data = addedInSource((CDORevision)sourceData);
      }
      else if (sourceData instanceof CDORevisionDelta)
      {
        data = changedInSource((CDORevisionDelta)sourceData);
      }
      else if (sourceData instanceof CDOID)
      {
        data = detachedInSource((CDOID)sourceData);
      }
    }
    else if (sourceData instanceof CDOID && targetData instanceof CDOID)
    {
      data = detachedInSourceAndTarget((CDOID)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDORevisionDelta)
    {
      data = changedInSourceAndTarget((CDORevisionDelta)targetData, (CDORevisionDelta)sourceData);
    }
    else if (sourceData instanceof CDORevision && targetData instanceof CDORevision)
    {
      data = addedInSourceAndTarget((CDORevision)targetData, (CDORevision)sourceData);
    }
    else if (sourceData instanceof CDORevisionDelta && targetData instanceof CDOID)
    {
      data = changedInSourceAndDetachedInTarget((CDORevisionDelta)sourceData);
    }
    else if (targetData instanceof CDORevisionDelta && sourceData instanceof CDOID)
    {
      data = changedInTargetAndDetachedInSource((CDORevisionDelta)targetData);
    } 
    else if (targetData instanceof CDORevision && sourceData instanceof CDORevisionDelta) {
      data = changedInSourceAndNewInTarget((CDORevision) targetData, (CDORevisionDelta) sourceData);
    } else if (targetData instanceof CDORevision && sourceData instanceof CDOID) {
      data = detachedInSourceAndAddedInTarget((CDORevision) targetData, (CDOID) sourceData);
    }

    return take(data);
  }

  protected Object detachedInSourceAndAddedInTarget(CDORevision targetData, CDOID sourceData) {
	return sourceData;
  }

// select the feature delta if something is new on target and changed on source by default
  protected Object changedInSourceAndNewInTarget(CDORevision targetData, CDORevisionDelta sourceData) {
	return sourceData;
  }

  protected Object addedInTarget(CDORevision revision)
  {
    return revision;
  }

  protected Object addedInSource(CDORevision revision)
  {
    return revision;
  }

  protected Object addedInSourceAndTarget(CDORevision targetRevision, CDORevision sourceRevision)
  {
    return targetRevision;
  }

  protected Object changedInTarget(CDORevisionDelta delta)
  {
    return delta;
  }

  protected Object detachedInTarget(CDOID id)
  {
    return id;
  }

  protected Object changedInSource(CDORevisionDelta delta)
  {
    return delta;
  }

  protected Object detachedInSource(CDOID id)
  {
    return id;
  }

  protected Object detachedInSourceAndTarget(CDOID id)
  {
    return id;
  }

  protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
  {
    return new ChangedInSourceAndTargetConflict(targetDelta, sourceDelta);
  }

  protected Object changedInSourceAndDetachedInTarget(CDORevisionDelta sourceDelta)
  {
    return new ChangedInSourceAndDetachedInTargetConflict(sourceDelta);
  }

  protected Object changedInTargetAndDetachedInSource(CDORevisionDelta targetDelta)
  {
    return new ChangedInTargetAndDetachedInSourceConflict(targetDelta);
  }

  protected Map<CDOID, Object> getTargetMap()
  {
    return targetMap;
  }

  protected Map<CDOID, Object> getSourceMap()
  {
    return sourceMap;
  }

  private Map<CDOID, Object> createMap(CDOChangeSetData changeSetData)
  {
    Map<CDOID, Object> map = new HashMap<CDOID, Object>();
    for (CDOIDAndVersion data : changeSetData.getNewObjects())
    {
      map.put(data.getID(), data);
    }

    for (CDORevisionKey data : changeSetData.getChangedObjects())
    {
      map.put(data.getID(), data);
    }

    for (CDOIDAndVersion data : changeSetData.getDetachedObjects())
    {
      map.put(data.getID(), data.getID());
    }

    return map;
  }

  private boolean take(Object data)
  {
    if (data instanceof Pair<?, ?>)
    {
      Pair<?, ?> pair = (Pair<?, ?>)data;
      boolean taken = takeNoPair(pair.getElement1());
      taken |= takeNoPair(pair.getElement2());
      return taken;
    }

    return takeNoPair(data);
  }

  private boolean takeNoPair(Object data)
  {
    if (data instanceof CDORevision)
    {
      result.getNewObjects().add((CDORevision)data);
    }
    else if (data instanceof CDORevisionDelta)
    {
      result.getChangedObjects().add((CDORevisionDelta)data);
    }
    else if (data instanceof CDOID)
    {
      result.getDetachedObjects().add(CDOIDUtil.createIDAndVersion((CDOID)data, CDOBranchVersion.UNSPECIFIED_VERSION));
    }
    else if (data instanceof Conflict)
    {
      Conflict conflict = (Conflict)data;
      conflicts.put(conflict.getID(), conflict);
    }
    else if (data != null)
    {
      throw new IllegalArgumentException("Must be a CDORevision, a CDORevisionDelta, a CDOID, a Conflict or null: "
          + data);
    }
    else
    {
      return false;
    }

    return true;
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class Conflict
  {
    public abstract CDOID getID();
  }

  /**
   * @author Eike Stepper
   */
  public static class ChangedInSourceAndTargetConflict extends Conflict
  {
    private CDORevisionDelta targetDelta;

    private CDORevisionDelta sourceDelta;

    public ChangedInSourceAndTargetConflict(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
    {
      this.targetDelta = targetDelta;
      this.sourceDelta = sourceDelta;
    }

    @Override
    public CDOID getID()
    {
      return targetDelta.getID();
    }

    public CDORevisionDelta getTargetDelta()
    {
      return targetDelta;
    }

    public CDORevisionDelta getSourceDelta()
    {
      return sourceDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInSourceAndTarget[target={0}, source={1}]", targetDelta, sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ChangedInSourceAndDetachedInTargetConflict extends Conflict
  {
    private CDORevisionDelta sourceDelta;

    public ChangedInSourceAndDetachedInTargetConflict(CDORevisionDelta sourceDelta)
    {
      this.sourceDelta = sourceDelta;
    }

    @Override
    public CDOID getID()
    {
      return sourceDelta.getID();
    }

    public CDORevisionDelta getSourceDelta()
    {
      return sourceDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInSourceAndDetachedInTarget[source={0}]", sourceDelta); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class ChangedInTargetAndDetachedInSourceConflict extends Conflict
  {
    private CDORevisionDelta targetDelta;

    public ChangedInTargetAndDetachedInSourceConflict(CDORevisionDelta targetDelta)
    {
      this.targetDelta = targetDelta;
    }

    @Override
    public CDOID getID()
    {
      return targetDelta.getID();
    }

    public CDORevisionDelta getTargetDelta()
    {
      return targetDelta;
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("ChangedInTargetAndDetachedInSource[target={0}]", targetDelta); //$NON-NLS-1$
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class PerFeature extends DefaultCDOMerger
  {
    public PerFeature()
    {
    }

    @Override
    protected Object changedInSourceAndTarget(CDORevisionDelta targetDelta, CDORevisionDelta sourceDelta)
    {
      InternalCDORevisionDelta result = new CDORevisionDeltaImpl(targetDelta, false);
      ChangedInSourceAndTargetConflict conflict = null;

      Map<EStructuralFeature, CDOFeatureDelta> targetMap = ((InternalCDORevisionDelta)targetDelta).getFeatureDeltaMap();
      Map<EStructuralFeature, CDOFeatureDelta> sourceMap = ((InternalCDORevisionDelta)sourceDelta).getFeatureDeltaMap();

      for (CDOFeatureDelta targetFeatureDelta : targetMap.values())
      {
        EStructuralFeature feature = targetFeatureDelta.getFeature();
        CDOFeatureDelta sourceFeatureDelta = sourceMap.get(feature);

        if (sourceFeatureDelta == null)
        {
          CDOFeatureDelta featureDelta = changedInTarget(targetFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta);
          }
        }
        else
        {
          CDOFeatureDelta featureDelta = changedInSourceAndTarget(targetFeatureDelta, sourceFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta);
          }
          else
          {
            if (conflict == null)
            {
              conflict = new ChangedInSourceAndTargetConflict(new CDORevisionDeltaImpl(targetDelta, false),
                  new CDORevisionDeltaImpl(sourceDelta, false));
            }

            ((InternalCDORevisionDelta)conflict.getTargetDelta()).addFeatureDelta(targetFeatureDelta);
            ((InternalCDORevisionDelta)conflict.getSourceDelta()).addFeatureDelta(sourceFeatureDelta);
          }
        }
      }

      for (CDOFeatureDelta sourceFeatureDelta : sourceMap.values())
      {
        EStructuralFeature feature = sourceFeatureDelta.getFeature();
        CDOFeatureDelta targetFeatureDelta = targetMap.get(feature);

        if (targetFeatureDelta == null)
        {
          CDOFeatureDelta featureDelta = changedInSource(sourceFeatureDelta);
          if (featureDelta != null)
          {
            result.addFeatureDelta(featureDelta);
          }
        }
      }

      if (result.isEmpty())
      {
        return conflict;
      }

      if (conflict != null)
      {
        return new Pair<InternalCDORevisionDelta, ChangedInSourceAndTargetConflict>(result, conflict);
      }

      return result;
    }

    /**
     * @return the result feature delta, or <code>null</code> to ignore the change.
     */
    protected CDOFeatureDelta changedInTarget(CDOFeatureDelta featureDelta)
    {
      return featureDelta;
    }

    /**
     * @return the result feature delta, or <code>null</code> to ignore the change.
     */
    protected CDOFeatureDelta changedInSource(CDOFeatureDelta featureDelta)
    {
      return featureDelta;
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTarget(CDOFeatureDelta targetFeatureDelta,
        CDOFeatureDelta sourceFeatureDelta)
    {
      EStructuralFeature feature = targetFeatureDelta.getFeature();
      if (feature.isMany())
      {
        return changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      return changedInSourceAndTargetSingleValued(feature, targetFeatureDelta, sourceFeatureDelta);
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature,
        CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta)
    {
      return null;
    }

    /**
     * @return the result feature delta, or <code>null</code> to indicate an unresolved conflict.
     */
    protected CDOFeatureDelta changedInSourceAndTargetSingleValued(EStructuralFeature feature,
        CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta)
    {
      if (targetFeatureDelta.isStructurallyEqual(sourceFeatureDelta))
      {
        return targetFeatureDelta;
      }

      return null;
    }

    /**
     * @author Eike Stepper
     */
    public static class ManyValued extends PerFeature
    {
      public ManyValued()
      {
      }

      @Override
      protected CDOFeatureDelta changedInSourceAndTargetManyValued(EStructuralFeature feature,
          CDOFeatureDelta targetFeatureDelta, CDOFeatureDelta sourceFeatureDelta)
      {
        if (targetFeatureDelta instanceof CDOListFeatureDelta && sourceFeatureDelta instanceof CDOListFeatureDelta)
        {
          CDOListFeatureDelta targetListDelta = (CDOListFeatureDelta)targetFeatureDelta.copy();
          CDOListFeatureDelta sourceListDelta = (CDOListFeatureDelta)sourceFeatureDelta.copy();

          CDOListFeatureDelta result = createResult(feature);
          List<CDOFeatureDelta> resultChanges = result.getListChanges();

          List<CDOFeatureDelta> targetChanges = targetListDelta.getListChanges();
          List<CDOFeatureDelta> sourceChanges = sourceListDelta.getListChanges();

          handleListDelta(resultChanges, targetChanges, sourceChanges);
          handleListDelta(resultChanges, sourceChanges, null);
          return result;
        }

        return super.changedInSourceAndTargetManyValued(feature, targetFeatureDelta, sourceFeatureDelta);
      }

      protected CDOListFeatureDelta createResult(EStructuralFeature feature)
      {
        return new CDOListFeatureDeltaImpl(feature);
      }

      protected void handleListDelta(List<CDOFeatureDelta> resultList, List<CDOFeatureDelta> listToHandle,
          List<CDOFeatureDelta> listToAdjust)
      {
        for (CDOFeatureDelta deltaToHandle : listToHandle)
        {
          if (deltaToHandle instanceof CDOAddFeatureDelta)
          {
            if (!handleListDeltaAdd(resultList, (CDOAddFeatureDelta)deltaToHandle, listToAdjust))
            {
              if (listToAdjust == null && !isEqualDeltaPresent(resultList, deltaToHandle))
              {
                // If the ADD delta was not taken into the result the remaining deltas must be adjusted
                adjustAfterRemoval(listToHandle, 0);
              }
            }
          }
          else if (deltaToHandle instanceof CDORemoveFeatureDelta)
          {
            if (!handleListDeltaRemove(resultList, (CDORemoveFeatureDelta)deltaToHandle, listToAdjust))
            {
              if (listToAdjust == null && !isEqualDeltaPresent(resultList, deltaToHandle))
              {
            	// If the REMOVE delta was not taken into the result the remaining deltas must be adjusted
                adjustAfterAddition(listToHandle, 0);
              }
            }
          }
          else if (deltaToHandle instanceof CDOMoveFeatureDelta)
          {
            handleListDeltaMove(resultList, (CDOMoveFeatureDelta)deltaToHandle, listToAdjust);
          }
          else if (deltaToHandle instanceof CDOClearFeatureDelta)
          {
            handleListDeltaClear(resultList, (CDOClearFeatureDelta)deltaToHandle);
          }
          else
          {
            throw new UnsupportedOperationException("Unable to handle list feature conflict: " + deltaToHandle);
          }
        }
      }

      private boolean isEqualDeltaPresent(List<CDOFeatureDelta> resultList, CDOFeatureDelta deltaToHandle) 
      {
	    for (CDOFeatureDelta resultDelta : resultList) {
			if (resultDelta.isStructurallyEqual(deltaToHandle)) {
				return true;
			}
		}
	    
	    return false;
	  }

	  /**
       * Decides whether an ADD delta is to be taken (added to the result list) and returns <code>true</code> if it was
       * taken, <code>false</code> otherwise. Note that the passed ADD delta has to be copied prior to adding it to the
       * result list!
       */
      protected boolean handleListDeltaAdd(List<CDOFeatureDelta> resultList, CDOAddFeatureDelta addDelta,
          List<CDOFeatureDelta> listToAdjust)
      {
        int index = addDelta.getIndex();
        if (listToAdjust == null)
        {
          // listToAdjust is only null for the sourceFeatureDeltas.
          // In this case ignore a potential duplicate ADD delta.
          Object value = addDelta.getValue();
          if (getTargetMap().get(value) instanceof CDORevision && getSourceMap().get(value) instanceof CDORevision)
          {
            // Remove ADD deltas for objects that have been added to source and target.
            // This can for example happen if a source is re-merged to target.
            return false;
          }
        }

        resultList.add(addDelta.copy());
        if (listToAdjust != null)
        {
          adjustAfterAddition(listToAdjust, index);
        }

        return true;
      }

      /**
       * Decides whether a REMOVE delta is to be taken (added to the result list) and returns <code>true</code> if it
       * was taken, <code>false</code> otherwise. Note that the passed REMOVE delta has to be copied prior to adding it
       * to the result list!
       */
      protected boolean handleListDeltaRemove(List<CDOFeatureDelta> resultList, CDORemoveFeatureDelta removeDelta,
          List<CDOFeatureDelta> listToAdjust)
      {
        int index = removeDelta.getIndex();
        if (listToAdjust == null)
        {
          // listToAdjust is only null for the sourceFeatureDeltas.
          // In this case ignore a potential duplicate REMOVE delta.
          Object value = removeDelta.getValue();
          
          // Remove REMOVE deltas for objects that have been removed from source and target.
          // This can for example happen if a source is re-merged to target.
          if (!getTargetMap().containsKey(value) && !getSourceMap().containsKey(value))
          {
            return false;
          }
          
          if (getTargetMap().get(value) instanceof CDOID && getSourceMap().get(value) instanceof CDOID)
          {
        	return false;
          }
        }

        resultList.add(removeDelta.copy());
        if (listToAdjust != null)
        {
          adjustAfterRemoval(listToAdjust, index);
        }

        return true;
      }

      protected boolean handleListDeltaClear(List<CDOFeatureDelta> resultList, CDOClearFeatureDelta clearDelta)
      {
        resultList.add(clearDelta.copy());

        return true;
      }

      /**
       * Decides whether a MOVE delta is to be taken (added to the result list) and returns <code>true</code> if it was
       * taken, <code>false</code> otherwise. Note that the passed MOVE delta has to be copied prior to adding it to the
       * result list!
       */
      protected boolean handleListDeltaMove(List<CDOFeatureDelta> resultList, CDOMoveFeatureDelta moveDelta,
          List<CDOFeatureDelta> listToAdjust)
      {
        resultList.add(moveDelta.copy());
        if (listToAdjust != null)
        {
          int oldPosition = moveDelta.getOldPosition();
          int newPosition = moveDelta.getNewPosition();
          adjustAfterMove(listToAdjust, oldPosition, newPosition);
        }

        return true;
      }

      public static void adjustAfterAddition(List<CDOFeatureDelta> list, int index)
      {
        for (CDOFeatureDelta delta : list)
        {
          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterAddition(index);
          }
        }
      }

      public static void adjustAfterRemoval(List<CDOFeatureDelta> list, int index)
      {
        for (CDOFeatureDelta delta : list)
        {
          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterRemoval(index);
          }
        }
      }

      public static void adjustAfterMove(List<CDOFeatureDelta> list, int oldPosition, int newPosition)
      {
        for (CDOFeatureDelta delta : list)
        {
          if (delta instanceof InternalCDOFeatureDelta.WithIndex)
          {
            InternalCDOFeatureDelta.WithIndex withIndex = (InternalCDOFeatureDelta.WithIndex)delta;
            withIndex.adjustAfterRemoval(oldPosition);
            withIndex.adjustAfterAddition(newPosition);
          }
        }
      }
    }
  }
}
