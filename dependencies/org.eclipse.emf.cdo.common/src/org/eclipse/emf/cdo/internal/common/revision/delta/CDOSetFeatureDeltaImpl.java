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
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDeltaVisitor;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDOFeatureDelta.ListTargetAdding;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * @author Simon McDuff
 */
public class CDOSetFeatureDeltaImpl extends CDOSingleValueFeatureDeltaImpl implements CDOSetFeatureDelta,
    ListTargetAdding
{
  private Object oldValue = CDOSetFeatureDelta.UNSPECIFIED;

  public CDOSetFeatureDeltaImpl(EStructuralFeature feature, int index, Object value)
  {
    super(feature, index, value);
  }

  public CDOSetFeatureDeltaImpl(EStructuralFeature feature, int index, Object value, Object oldValue)
  {
    super(feature, index, value);
    this.oldValue = oldValue;
  }

  public CDOSetFeatureDeltaImpl(CDODataInput in, EClass eClass) throws IOException
  {
    super(in, eClass);
  }

  public Type getType()
  {
    return Type.SET;
  }

  public CDOFeatureDelta copy()
  {
    return new CDOSetFeatureDeltaImpl(getFeature(), getIndex(), getValue(), getOldValue());
  }

  public void apply(CDORevision revision)
  {
    ((InternalCDORevision)revision).set(getFeature(), getIndex(), getValue());
  }

  public void accept(CDOFeatureDeltaVisitor visitor)
  {
    visitor.visit(this);
  }

  public Object getOldValue()
  {
    return oldValue;
  }

  public void setOldValue(Object oldValue)
  {
    this.oldValue = oldValue;
  }

  @Override
  protected String toStringAdditional()
  {
    String oldValueForMessage;
    if (oldValue != CDOSetFeatureDelta.UNSPECIFIED)
    {
      oldValueForMessage = oldValue == null ? "null" : oldValue.toString();
    }
    else
    {
      oldValueForMessage = "UNSPECIFIED"; //$NON-NLS-1$
    }

    return super.toStringAdditional() + MessageFormat.format(", oldValue={0}", oldValueForMessage); //$NON-NLS-1$
  }
}
