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

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.WithIndex;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.net4j.util.ObjectUtil;

/**
 * @author Simon McDuff
 */
public abstract class CDOSingleValueFeatureDeltaImpl extends CDOFeatureDeltaImpl implements WithIndex
{
  private int index;

  private Object value;

  public CDOSingleValueFeatureDeltaImpl(EStructuralFeature feature, int index, Object value)
  {
    super(feature);
    this.index = index;
    this.value = value;
  }

  public CDOSingleValueFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
    index = in.readInt();
    value = readValue(in, eClass);
  }

  @Override
  public void write(CDODataOutput out, EClass eClass) throws IOException
  {
    super.write(out, eClass);
    out.writeInt(index);
    writeValue(out, eClass);
  }

  protected void writeValue(CDODataOutput out, EClass eClass) throws IOException
  {
    Object valueToWrite = value;
    if (valueToWrite == UNKNOWN_VALUE)
    {
      throw new IOException("Value is unknown");
    }

    EStructuralFeature feature = getFeature();
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      FeatureMap.Entry entry = (Entry)valueToWrite;
      feature = entry.getEStructuralFeature();
      valueToWrite = entry.getValue();

      int featureID = eClass.getFeatureID(feature);
      out.writeInt(featureID);
    }

    if (valueToWrite != null && feature instanceof EReference)
    {
      valueToWrite = out.getIDProvider().provideCDOID(value);
    }

    out.writeCDOFeatureValue(feature, valueToWrite);
  }

  protected Object readValue(CDODataInput in, EClass eClass) throws IOException
  {
    EStructuralFeature feature = getFeature();
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      int featureID = in.readInt();
      feature = eClass.getEStructuralFeature(featureID);
      Object innerValue = in.readCDOFeatureValue(feature);
      return CDORevisionUtil.createFeatureMapEntry(feature, innerValue);
    }

    return in.readCDOFeatureValue(feature);
  }

  public int getIndex()
  {
    return index;
  }

  public void setIndex(int index)
  {
    this.index = index;
  }

  public Object getValue()
  {
    return value;
  }

  public void setValue(Object value)
  {
    this.value = value;
  }

  public void clear()
  {
    setValue(CDOID.NULL);
  }

  public void adjustAfterAddition(int index)
  {
    if (index <= this.index)
    {
      ++this.index;
    }
  }

  public void adjustAfterRemoval(int index)
  {
    if (index < this.index && this.index > 0)
    {
      --this.index;
    }
  }

  @Override
  public boolean adjustReferences(CDOReferenceAdjuster referenceAdjuster)
  {
    if (value != UNKNOWN_VALUE)
    {
      Object adjustedValue = referenceAdjuster.adjustReference(value, getFeature(), NO_INDEX);
      if (adjustedValue != value)
      {
        value = adjustedValue;
        return true;
      }
    }

    return false;
  }

  @Override
  public boolean isStructurallyEqual(Object obj)
  {
    if (!super.isStructurallyEqual(obj))
    {
      return false;
    }

    CDOSingleValueFeatureDeltaImpl that = (CDOSingleValueFeatureDeltaImpl)obj;
    return index == that.getIndex() && ObjectUtil.equals(value, that.getValue());
  }

  @Override
  protected String toStringAdditional()
  {
    if (index == Notification.NO_INDEX)
    {
      return MessageFormat.format("value={0}", value); //$NON-NLS-1$
    }

    return MessageFormat.format("value={0}, index={1}", value, index); //$NON-NLS-1$
  }
}
