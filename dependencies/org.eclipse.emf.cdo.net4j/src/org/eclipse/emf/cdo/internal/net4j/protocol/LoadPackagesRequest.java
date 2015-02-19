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
package org.eclipse.emf.cdo.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.internal.net4j.bundle.OM;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class LoadPackagesRequest extends CDOClientRequest<EPackage[]>
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_PROTOCOL, LoadPackagesRequest.class);

  private InternalCDOPackageUnit packageUnit;

  public LoadPackagesRequest(CDOClientProtocol protocol, InternalCDOPackageUnit packageUnit)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_LOAD_PACKAGES);
    this.packageUnit = packageUnit;
  }

  @Override
  protected void requesting(CDODataOutput out) throws IOException
  {
    String packageUnitID = packageUnit.getID();
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing packageUnitID: {0}", packageUnitID); //$NON-NLS-1$
    }

    out.writeCDOPackageURI(packageUnitID);
  }

  @Override
  protected EPackage[] confirming(CDODataInput in) throws IOException
  {
    ResourceSet resourceSet = EMFUtil.newEcoreResourceSet(packageUnit.getPackageRegistry());
    EPackage ePackage = CDOModelUtil.readPackage(in, resourceSet, false);
    return EMFUtil.getAllPackages(ePackage);
  }
}
