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
package org.eclipse.emf.cdo.internal.common.revision.delta;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOMoveFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListIndexAffecting;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.WithIndex;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class CDOMoveFeatureDeltaImpl extends CDOFeatureDeltaImpl implements CDOMoveFeatureDelta, ListIndexAffecting,
    WithIndex
{
  private int oldPosition;

  private int newPosition;

  private Object value;

  public CDOMoveFeatureDeltaImpl(EStructuralFeature feature, int newPosition, int oldPosition)
  {
    super(feature);
    this.newPosition = newPosition;
    this.oldPosition = oldPosition;
    value = UNKNOWN_VALUE;
  }

  public CDOMoveFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    newPosition = in.readInt();
    oldPosition = in.readInt();
    value = UNKNOWN_VALUE;
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(newPosition);
    out.writeInt(oldPosition);
  }

  public int getNewPosition()
  {
    return newPosition;
  }

  public int getOldPosition()
  {
    return oldPosition;
  }

  public Type getType()
  {
    return Type.MOVE;
  }

  public Object getValue()
  {
    return value;
  }

  public void setValue(Object value)
  {
    this.value = value;
  }

  public void setOldPosition(int oldPosition)
  {
    this.oldPosition = oldPosition;
  }

  public void setNewPosition(int newPosition)
  {
    this.newPosition = newPosition;
  }

  public CDOFeatureDelta copy()
  {
    CDOFeatureDelta copy = new CDOMoveFeatureDeltaImpl(getFeature(), newPosition, oldPosition);
    ((CDOMoveFeatureDeltaImpl)copy).setValue(getValue());
    return copy;
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).getList(getFeature()).move(newPosition, oldPosition);
  }

  public void affectIndices(ListTargetAdding[] source, int[] indices)
  {
    if (oldPosition < newPosition)
    {
      for (int i = 1; i <= indices[0]; i++)
      {
        if (oldPosition < indices[i] && indices[i] <= newPosition)
        {
          --indices[i];
        }
        else if (indices[i] == oldPosition)
        {
          indices[i] = newPosition;
        }
      }
    }
    else if (newPosition < oldPosition)
    {
      for (int i = 1; i <= indices[0]; i++)
      {
        if (newPosition <= indices[i] && indices[i] < oldPosition)
        {
          ++indices[i];
        }
        else if (indices[i] == oldPosition)
        {
          indices[i] = newPosition;
        }
      }
    }
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  public void adjustAfterAddition(int index)
  {
    if (index <= oldPosition)
    {
      ++oldPosition;
    }

    if (index <= newPosition)
    {
      ++newPosition;
    }
  }

  public void adjustAfterRemoval(int index)
  {
    if (index < oldPosition && oldPosition > 0)
    {
      --oldPosition;
    }

    // Index fix for moves from left to right.
    if (oldPosition < newPosition)
    {
      --index;
    }

    if (index < newPosition && newPosition > 0)
    {
      --newPosition;
    }
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster adjuster)
  {
    return false;
  }

  @Override
  public boolean isStructurallyEqual(Object obj)
  {
    if (!super.isStructurallyEqual(obj))
    {
      return false;
    }

    CDOMoveFeatureDelta that = (CDOMoveFeatureDelta)obj;
    return oldPosition == that.getOldPosition() && newPosition == that.getNewPosition();
  }

  @Override
  protected String toStringAdditional()
  {
    return MessageFormat.format("from={0}, to={1}, value={2}", oldPosition, newPosition, value);
  }

}
