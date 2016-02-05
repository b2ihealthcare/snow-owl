/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff  - initial API and implementation
 *    Eike Stepper  - maintenance
 *    Cyril Jaquier - Bug 310574 (with the help of Pascal Lehmann)
 */
package org.eclipse.emf.cdo.internal.common.revision.delta;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORemoveFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.collection.Pair;

/**
 * @author Simon McDuff
 */
public class CDOListFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOListFeatureDelta
{
  private List<CDOFeatureDelta> featureDeltas = new ArrayList<CDOFeatureDelta>();

  private transient int[] cachedIndices;

  private transient ListTargetAdding[] cachedSources;

  private transient List<CDOFeatureDelta> unprocessedFeatureDeltas;

  public CDOListFeatureDeltaImpl(EStructuralFeature feature)
  {
    super(feature);
  }

  public CDOListFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    int size = in.readInt();
    for (int i = 0; i < size; i++)
    {
      featureDeltas.add(in.readCDOFeatureDelta(eClass));
    }
  }

  public CDOListFeatureDelta copy()
  {
    CDOListFeatureDeltaImpl result = new CDOListFeatureDeltaImpl(getFeature());

    Map<CDOFeatureDelta, CDOFeatureDelta> map = null;
    if (cachedSources != null || unprocessedFeatureDeltas != null)
    {
      map = new HashMap<CDOFeatureDelta, CDOFeatureDelta>();
    }

    for (CDOFeatureDelta delta : featureDeltas)
    {
      CDOFeatureDelta newDelta = delta.copy();
      result.featureDeltas.add(newDelta);
      if (map != null)
      {
        map.put(delta, newDelta);
      }
    }

    if (cachedIndices != null)
    {
      result.cachedIndices = copyOf(cachedIndices, cachedIndices.length);
    }

    if (cachedSources != null)
    {
      int length = cachedSources.length;
      result.cachedSources = new ListTargetAdding[length];
      for (int i = 0; i < length; i++)
      {
        ListTargetAdding oldElement = cachedSources[i];
        CDOFeatureDelta newElement = map.get(oldElement);
        if (newElement instanceof ListTargetAdding)
        {
          result.cachedSources[i] = (ListTargetAdding)newElement;
        }
      }
    }

    if (unprocessedFeatureDeltas != null)
    {
      int size = unprocessedFeatureDeltas.size();
      result.unprocessedFeatureDeltas = new ArrayList<CDOFeatureDelta>(size);
      for (CDOFeatureDelta oldDelta : unprocessedFeatureDeltas)
      {
        CDOFeatureDelta newDelta = map.get(oldDelta);
        if (newDelta != null)
        {
          result.unprocessedFeatureDeltas.add(newDelta);
        }
      }
    }

    return result;
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(featureDeltas.size());
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      out.writeCDOFeatureDelta(eClass, featureDelta);
    }
  }

  public Type getType()
  {
    return Type.LIST;
  }

  public List<CDOFeatureDelta> getListChanges()
  {
    return featureDeltas;
  }

  /**
   * Returns the number of indices as the first element of the array.
   * 
   * @return never <code>null</code>.
   */
  public Pair<ListTargetAdding[], int[]> reconstructAddedIndices()
  {
    reconstructAddedIndicesWithNoCopy();
    return new Pair<ListTargetAdding[], int[]>(copyOf(cachedSources, cachedSources.length, cachedSources.getClass()),
        copyOf(cachedIndices, cachedIndices.length));
  }

  private void reconstructAddedIndicesWithNoCopy()
  {
    // Note that cachedIndices and cachedSources are always either both null or
    // both non-null, and in the latter case, are always of the same length.
    // Furthermore, there can only be unprocessedFeatureDeltas if cachesIndices
    // and cachedSources are non-null.

    if (cachedIndices == null || unprocessedFeatureDeltas != null)
    {
      if (cachedIndices == null)
      {
        int initialCapacity = featureDeltas.size() + 1;
        cachedIndices = new int[initialCapacity];
        cachedSources = new ListTargetAdding[initialCapacity];
      }
      else
      // i.e. unprocessedFeatureDeltas != null
      {
        int requiredCapacity = 1 + cachedIndices[0] + unprocessedFeatureDeltas.size();
        if (cachedIndices.length < requiredCapacity)
        {
          int newCapacity = Math.max(requiredCapacity, cachedIndices.length * 2);

          int[] newIndices = new int[newCapacity];
          System.arraycopy(cachedIndices, 0, newIndices, 0, cachedIndices.length);
          cachedIndices = newIndices;

          ListTargetAdding[] newSources = new ListTargetAdding[newCapacity];
          System.arraycopy(cachedSources, 0, newSources, 0, cachedSources.length);
          cachedSources = newSources;
        }
      }

      List<CDOFeatureDelta> featureDeltasToBeProcessed = unprocessedFeatureDeltas == null ? featureDeltas
          : unprocessedFeatureDeltas;
      for (CDOFeatureDelta featureDelta : featureDeltasToBeProcessed)
      {
        if (featureDelta instanceof ListIndexAffecting)
        {
          ListIndexAffecting affecting = (ListIndexAffecting)featureDelta;
          affecting.affectIndices(cachedSources, cachedIndices);
        }

        if (featureDelta instanceof ListTargetAdding)
        {
          cachedIndices[++cachedIndices[0]] = ((ListTargetAdding)featureDelta).getIndex();
          cachedSources[cachedIndices[0]] = (ListTargetAdding)featureDelta;
        }
      }

      unprocessedFeatureDeltas = null;
    }
  }

  private boolean cleanupWithNewDelta(CDOFeatureDelta featureDelta)
  {
    EStructuralFeature feature = getFeature();
    if (!feature.isOrdered()) {
    	return true;
    }
    if ((feature instanceof EReference || FeatureMapUtil.isFeatureMap(feature))
        && featureDelta instanceof CDORemoveFeatureDelta)
    {
      int indexToRemove = ((CDORemoveFeatureDelta)featureDelta).getIndex();
      reconstructAddedIndicesWithNoCopy();

      for (int i = 1; i <= cachedIndices[0]; i++)
      {
        int index = cachedIndices[i];
        if (indexToRemove == index)
        {
          // The previous implementation set the value of the feature delta to CDOID.NULL. Databinding and probably
          // others don't really like it. We now remove the ADD (or SET which seems to appear in CDOListFeatureDelta
          // during opposite adjustment!? Why???) and patch the other feature deltas.
          // See https://bugs.eclipse.org/bugs/show_bug.cgi?id=310574

          ListTargetAdding delta = cachedSources[i];

          // We use a "floating" index which is the index (in the list) of the item to remove at the time when the
          // object was still in the list. This index evolves with the feature deltas.
          int floatingIndex = delta.getIndex();

          // First updates cachedSources and cachedIndices using CDORemoveFeatureDelta.
          ListIndexAffecting affecting = (ListIndexAffecting)featureDelta;
          affecting.affectIndices(cachedSources, cachedIndices);

          // Then adjusts the remaining feature deltas.
          boolean skip = true;
          ListIterator<CDOFeatureDelta> iterator = featureDeltas.listIterator();

          while (iterator.hasNext())
          {
            CDOFeatureDelta fd = iterator.next();

            // We only need to process feature deltas that come after the ADD (or SET) to be removed.
            if (skip)
            {
              if (fd == delta)
              {
                // Found the ADD (or SET) feature delta that we need to remove. So remove it from the list and start
                // processing the next feature deltas.
                skip = false;
                iterator.remove();

                // SET
                if (fd instanceof CDOSetFeatureDelta)
                {
                  // If the removed delta is SET we add the REMOVE to the feature deltas. We do not need to adjust the
                  // other feature deltas because SET do not modify the list.
                  return true;
                }
              }

              continue;
            }

            // ADD
            if (fd instanceof CDOAddFeatureDelta)
            {
              // Increases the floating index if the ADD came in front of the item.
              if (((CDOAddFeatureDelta)fd).getIndex() <= floatingIndex)
              {
                ++floatingIndex;
              }

              // Adjusts the feature delta too.
              ((WithIndex)fd).adjustAfterRemoval(floatingIndex);
            }

            // REMOVE
            else if (fd instanceof CDORemoveFeatureDelta)
            {
              int idx = floatingIndex;
              // Decreases the floating index if the REMOVE came in front of the item.
              if (((CDORemoveFeatureDelta)fd).getIndex() <= floatingIndex)
              {
                --floatingIndex;
              }

              // Adjusts the feature delta too.
              ((WithIndex)fd).adjustAfterRemoval(idx);
            }

            // MOVE
            else if (fd instanceof CDOMoveFeatureDelta)
            {
              // Remembers the positions before we patch them.
              int from = ((CDOMoveFeatureDelta)fd).getOldPosition();
              int to = ((CDOMoveFeatureDelta)fd).getNewPosition();

              if (floatingIndex == from)
              {
                // We are moving the "to be deleted" item. So we update our floating index and remove the MOVE. It has
                // no effect on the list.
                floatingIndex = to;
                iterator.remove();
              }
              else
              {
                // In the other cases, we need to patch the positions.

                // If the old position is greater or equal to the current position of the item to be removed (remember,
                // that's our floating index), decrease the position.
                int patchedFrom = floatingIndex <= from ? from - 1 : from;

                // The new position requires more care. We need to know the direction of the move (left-to-right or
                // right-to-left).
                int patchedTo;
                if (from > to)
                {
                  // left-to-right. Only decreases the position if it is strictly greater than the current item
                  // position.
                  patchedTo = floatingIndex < to ? to - 1 : to;
                }
                else
                {
                  // right-to-left. Decreases the position if it is greater or equal than the current item position.
                  patchedTo = floatingIndex <= to ? to - 1 : to;
                }

                // We can now update our floating index. We use the original positions because the floating index
                // represents the item "to be deleted" before it was actually removed.
                if (from < floatingIndex && floatingIndex <= to)
                {
                  --floatingIndex;
                }
                else if (to <= floatingIndex && floatingIndex < from)
                {
                  ++floatingIndex;
                }

                // And finally adjust the feature delta.
                if (patchedFrom == patchedTo)
                {
                  // Source and destination are the same so just remove the feature delta.
                  iterator.remove();
                }
                else
                {
                  ((CDOMoveFeatureDeltaImpl)fd).setOldPosition(patchedFrom);
                  ((CDOMoveFeatureDeltaImpl)fd).setNewPosition(patchedTo);
                }
              }
            }

            // SET
            else if (fd instanceof CDOSetFeatureDelta)
            {
              // Adjusts the feature delta too.
              ((WithIndex)fd).adjustAfterRemoval(floatingIndex);
            }
          }

          // If the removed delta was ADD so we do not add the REMOVE to the feature deltas.
          return false;
        }
      }
    }

    if (cachedIndices != null)
    {
      if (unprocessedFeatureDeltas == null)
      {
        unprocessedFeatureDeltas = new ArrayList<CDOFeatureDelta>();
      }

      unprocessedFeatureDeltas.add(featureDelta);
    }

    return true;
  }

  public void add(CDOFeatureDelta featureDelta)
  {
    // Only adds the feature delta to the list if required.
    if (cleanupWithNewDelta(featureDelta))
    {
      featureDeltas.add(featureDelta);
    }
  }

  public void apply(CDORevision revision)
  {
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      ((CDOFeatureDeltaImpl)featureDelta).apply(revision);
    }
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster adjuster)
  {
    boolean changed = false;
    for (CDOFeatureDelta featureDelta : featureDeltas)
    {
      changed |= ((CDOFeatureDeltaImpl)featureDelta).adjustReferences(adjuster);
    }

    return changed;
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  @Override
  public boolean isStructurallyEqual(Object obj)
  {
    if (!super.isStructurallyEqual(obj))
    {
      return false;
    }

    CDOListFeatureDelta that = (CDOListFeatureDelta)obj;
    return ObjectUtil.equals(featureDeltas, that.getListChanges());
  }

  @Override
  protected String toStringAdditional()
  {
    return "list=" + featureDeltas; //$NON-NLS-1$
  }

  /**
   * Copied from JAVA 1.6 {@link Arrays Arrays.copyOf}.
   */
  private static <T, U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType)
  {
    @SuppressWarnings("unchecked")
    T[] copy = (Object)newType == (Object)Object[].class ? (T[])new Object[newLength] : (T[])Array.newInstance(
        newType.getComponentType(), newLength);
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
  }

  /**
   * Copied from JAVA 1.6 {@link Arrays Arrays.copyOf}.
   */
  private static int[] copyOf(int[] original, int newLength)
  {
    int[] copy = new int[newLength];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, newLength));
    return copy;
  }
}
