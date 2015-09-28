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
package org.eclipse.emf.cdo.common.id;

import java.io.IOException;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * Represents a {@link CDOID} typed reference from one object to another object.
 * 
 * @author Eike Stepper
 * @since 4.0
 * @noextend This interface is not intended to be extended by clients.
 * @noinstantiate This class is not intended to be instantiated by clients.
 * @apiviz.has {@link CDOID} oneway - - source
 * @apiviz.has {@link CDOID} oneway - - target
 */
public class CDOIDReference implements CDOReference<CDOID>
{
  private CDOID targetID;

  private CDOID sourceID;

  private EStructuralFeature sourceFeature;

  private int sourceIndex;

  public CDOIDReference(CDOID targetID, CDOID sourceID, EStructuralFeature sourceFeature, int sourceIndex)
  {
    this.targetID = targetID;
    this.sourceID = sourceID;
    this.sourceFeature = sourceFeature;
    this.sourceIndex = sourceIndex;
  }

  public CDOIDReference(CDODataInput in) throws IOException
  {
    targetID = in.readCDOID();
    sourceID = in.readCDOID();

    EClass eClass = (EClass)in.readCDOClassifierRefAndResolve();
    String featureName = in.readString();
    sourceFeature = eClass.getEStructuralFeature(featureName);

    sourceIndex = in.readInt();
  }

  public void write(CDODataOutput out) throws IOException
  {
    out.writeCDOID(targetID);
    out.writeCDOID(sourceID);
    out.writeCDOClassifierRef(sourceFeature.getEContainingClass());
    out.writeString(sourceFeature.getName());
    out.writeInt(sourceIndex);
  }

  public CDOID getTargetObject()
  {
    return targetID;
  }

  public CDOID getSourceObject()
  {
    return sourceID;
  }

  public EStructuralFeature getSourceFeature()
  {
    return sourceFeature;
  }

  public int getSourceIndex()
  {
    return sourceIndex;
  }

  @Override
  public String toString()
  {
    StringBuilder builder = new StringBuilder();
    builder.append(sourceID);
    builder.append(".");
    builder.append(sourceFeature.getName());
    if (sourceIndex != NO_INDEX)
    {
      builder.append("[");
      builder.append(sourceIndex);
      builder.append("]");
    }

    builder.append(" --> ");
    builder.append(targetID);
    return builder.toString();
  }
}
