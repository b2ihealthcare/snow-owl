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
package org.eclipse.emf.cdo.internal.common.model;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDOPackageInfoImpl extends AdapterImpl implements InternalCDOPackageInfo
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOPackageInfoImpl.class);

  private InternalCDOPackageUnit packageUnit;

  private String packageURI;

  private String parentURI;

  public CDOPackageInfoImpl()
  {
  }

  public InternalCDOPackageUnit getPackageUnit()
  {
    return packageUnit;
  }

  public void setPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    this.packageUnit = packageUnit;
  }

  public String getPackageURI()
  {
    return packageURI;
  }

  public void setPackageURI(String packageURI)
  {
    this.packageURI = packageURI;
  }

  public String getParentURI()
  {
    return parentURI;
  }

  public void setParentURI(String parentURI)
  {
    this.parentURI = parentURI;
  }

  public void write(CDODataOutput out) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0}", this); //$NON-NLS-1$
    }

    out.writeCDOPackageURI(packageURI);
    out.writeCDOPackageURI(parentURI);
  }

  public void read(CDODataInput in) throws IOException
  {
    packageURI = in.readCDOPackageURI();
    parentURI = in.readCDOPackageURI();
    if (TRACER.isEnabled())
    {
      TRACER.format("Read {0}", this); //$NON-NLS-1$
    }
  }

  public EFactory getEFactory()
  {
    return getEPackage().getEFactoryInstance();
  }

  public EPackage getEPackage()
  {
    return getEPackage(true);
  }

  public EPackage getEPackage(boolean loadOnDemand)
  {
    return doGetEPackage(loadOnDemand);
  }

  public EPackage doGetEPackage(boolean loadOnDemand)
  {
    EPackage ePackage = (EPackage)getTarget();
    if (ePackage != null)
    {
      return ePackage;
    }

    if (loadOnDemand)
    {
      packageUnit.load(true); // TODO (CD) Dubious: is resolution-on-load really a good idea?
      return (EPackage)getTarget();
    }

    return null;
  }

  public boolean isCorePackage()
  {
    return CDOModelUtil.isCorePackage(getEPackage());
  }

  public boolean isResourcePackage()
  {
    return CDOModelUtil.isResourcePackage(getEPackage());
  }

  public boolean isTypePackage()
  {
    return CDOModelUtil.isTypesPackage(getEPackage());
  }

  public boolean isSystemPackage()
  {
    return CDOModelUtil.isSystemPackage(getEPackage());
  }

  public int compareTo(CDOPackageInfo o)
  {
    return getPackageURI().compareTo(o.getPackageURI());
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("CDOPackageInfo[packageURI={0}, parentURI={1}]", packageURI, parentURI); //$NON-NLS-1$
  }
}
