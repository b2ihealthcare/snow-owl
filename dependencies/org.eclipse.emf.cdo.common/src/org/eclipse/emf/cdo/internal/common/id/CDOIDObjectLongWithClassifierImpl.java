/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Taal - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.internal.common.id;

import java.io.IOException;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * @author Martin Taal
 * @since 3.0
 */
public class CDOIDObjectLongWithClassifierImpl extends CDOIDObjectLongImpl implements CDOClassifierRef.Provider
{
  private static final long serialVersionUID = 1L;

  private CDOClassifierRef classifierRef;

  public CDOIDObjectLongWithClassifierImpl()
  {
  }

  public CDOIDObjectLongWithClassifierImpl(CDOClassifierRef classifierRef, long value)
  {
    super(value);
    this.classifierRef = classifierRef;
  }

  public CDOClassifierRef getClassifierRef()
  {
    return classifierRef;
  }

  @Override
  public Type getType()
  {
    return Type.OBJECT;
  }

  @Override
  public CDOID.ObjectType getSubType()
  {
    return CDOID.ObjectType.LONG_WITH_CLASSIFIER;
  }

  @Override
  public String toURIFragment()
  {
    return getClassifierRef().getPackageURI() + CDOClassifierRef.URI_SEPARATOR + getClassifierRef().getClassifierName()
        + CDOClassifierRef.URI_SEPARATOR + super.toURIFragment();
  }

  @Override
  public void read(String fragmentPart)
  {
    // get the CDOClassifierRef part
    int index1 = fragmentPart.indexOf(CDOClassifierRef.URI_SEPARATOR);
    int index2 = fragmentPart.indexOf(CDOClassifierRef.URI_SEPARATOR, index1 + 1);
    if (index1 == -1 || index2 == -1)
    {
      throw new IllegalArgumentException("The fragment " + fragmentPart + " is not a valid fragment");
    }

    classifierRef = new CDOClassifierRef(fragmentPart.substring(0, index1), fragmentPart.substring(index1 + 1, index2));

    // let the super take care of the rest
    super.read(fragmentPart.substring(index2 + 1));
  }

  @Override
  public void read(ExtendedDataInput in) throws IOException
  {
    // TODO: change the parameter to prevent casting to CDODataInput
    CDODataInput cdoDataInput = (CDODataInput)in;
    classifierRef = cdoDataInput.readCDOClassifierRef();

    // and let the super take care of the rest
    super.read(in);
  }

  @Override
  public void write(ExtendedDataOutput out) throws IOException
  {
    // TODO: change the parameter to prevent casting to CDODataInput
    CDODataOutput cdoDataOutput = (CDODataOutput)out;
    cdoDataOutput.writeCDOClassifierRef(classifierRef);

    // and let the super write the rest
    super.write(out);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj != null && obj.getClass() == getClass())
    {
      CDOIDObjectLongWithClassifierImpl that = (CDOIDObjectLongWithClassifierImpl)obj;
      return ObjectUtil.equals(classifierRef, that.classifierRef) && getLongValue() == that.getLongValue();
    }

    return false;
  }

  @Override
  public int hashCode()
  {
    int hashCode = classifierRef.hashCode() ^ ObjectUtil.hashCode(getLongValue());
    return getClass().hashCode() ^ hashCode;
  }

  @Override
  public String toString()
  {
    return "OID:" + toURIFragment(); //$NON-NLS-1$
  }

  @Override
  protected int doCompareTo(CDOID o) throws ClassCastException
  {
    // conversion to uri fragment is pretty heavy but afaics the compareTo
    // is not used in a critical place.
    return toURIFragment().compareTo(o.toURIFragment());
  }
}
