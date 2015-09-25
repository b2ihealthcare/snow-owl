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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOPackageTypeRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageInfo;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry.PackageLoader;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

/**
 * @author Eike Stepper
 */
public class CDOPackageUnitImpl implements InternalCDOPackageUnit
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOPackageUnitImpl.class);

  private InternalCDOPackageRegistry packageRegistry;

  private State state = State.PROXY;

  private Type type;

  private Type originalType;

  private long timeStamp;

  private InternalCDOPackageInfo[] packageInfos;

  public CDOPackageUnitImpl()
  {
  }

  public InternalCDOPackageRegistry getPackageRegistry()
  {
    return packageRegistry;
  }

  public void setPackageRegistry(InternalCDOPackageRegistry packageRegistry)
  {
    this.packageRegistry = packageRegistry;
  }

  public String getID()
  {
    try
    {
      return getTopLevelPackageInfo().getPackageURI();
    }
    catch (RuntimeException ex)
    {
      return Messages.getString("CDOPackageUnitImpl.0"); //$NON-NLS-1$
    }
  }

  public State getState()
  {
    return state;
  }

  public void setState(State state)
  {
    this.state = state;
    if (state == State.LOADED)
    {
      type = null;
    }
  }

  public Type getType()
  {
    if (getOriginalType() == Type.DYNAMIC)
    {
      type = Type.DYNAMIC;
    }
    else if (type == null || type == Type.UNKNOWN)
    {
      if (state == State.PROXY)
      {
        type = CDOPackageTypeRegistry.INSTANCE.lookup(getID());
      }
      else
      {
        InternalCDOPackageInfo packageInfo = getTopLevelPackageInfo();
        EPackage ePackage = packageInfo.getEPackage();
        type = CDOPackageTypeRegistry.INSTANCE.lookup(ePackage);
      }

      if (type == null)
      {
        type = Type.UNKNOWN;
      }
    }

    return type;
  }

  public Type getOriginalType()
  {
    return originalType;
  }

  public void setOriginalType(Type originalType)
  {
    this.originalType = originalType;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public void setTimeStamp(long timeStamp)
  {
    this.timeStamp = timeStamp;
  }

  public InternalCDOPackageInfo getTopLevelPackageInfo()
  {
    if (packageInfos == null || packageInfos.length == 0)
    {
      throw new IllegalStateException(Messages.getString("CDOPackageUnitImpl.1")); //$NON-NLS-1$
    }

    return packageInfos[0];
  }

  public InternalCDOPackageInfo getPackageInfo(String packageURI)
  {
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      if (packageInfo.getPackageURI().equals(packageURI))
      {
        return packageInfo;
      }
    }

    return null;
  }

  public InternalCDOPackageInfo[] getPackageInfos()
  {
    return packageInfos;
  }

  public void setPackageInfos(InternalCDOPackageInfo[] packageInfos)
  {
    this.packageInfos = packageInfos;
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      packageInfo.setPackageUnit(this);
    }
  }

  public EPackage[] getEPackages(boolean loadOnDemand)
  {
    List<EPackage> result = new ArrayList<EPackage>();
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      EPackage ePackage = packageInfo.getEPackage(loadOnDemand);
      if (ePackage != null)
      {
        result.add(ePackage);
      }
    }

    return result.toArray(new EPackage[result.size()]);
  }

  public boolean isSystem()
  {
    return getTopLevelPackageInfo().isSystemPackage();
  }

  public boolean isResource()
  {
    return getTopLevelPackageInfo().isResourcePackage();
  }

  public void init(EPackage ePackage)
  {
    EPackage topLevelPackage = EMFUtil.getTopLevelPackage(ePackage);
    List<InternalCDOPackageInfo> result = new ArrayList<InternalCDOPackageInfo>();
    initPackageInfos(topLevelPackage, result);
    packageInfos = result.toArray(new InternalCDOPackageInfo[result.size()]);

    setState(State.NEW);
    setOriginalType(getType());
  }

  public void dispose()
  {
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      EPackage ePackage = packageInfo.getEPackage(false);
      if (ePackage != null)
      {
        synchronized (ePackage)
        {
          ePackage.eAdapters().remove(packageInfo);
        }
      }
    }

    packageInfos = null;
    setState(State.DISPOSED);
  }

  public synchronized void load(boolean resolve)
  {
    load(packageRegistry.getPackageLoader(), resolve);
  }

  public synchronized void load(PackageLoader packageLoader, boolean resolve)
  {
    if (state == State.PROXY)
    {
      EPackage[] ePackages = null;
      ePackages = loadPackagesFromGlobalRegistry();
      if (ePackages == null)
      {
        ePackages = packageLoader.loadPackages(this);
      }

      for (EPackage ePackage : ePackages)
      {
        String packageURI = ePackage.getNsURI();
        InternalCDOPackageInfo packageInfo = getPackageInfo(packageURI);
        synchronized (ePackage)
        {
          EMFUtil.addAdapter(ePackage, packageInfo);
          if (resolve)
          {
            EcoreUtil.resolveAll(ePackage);
          }
        }
      }

      setState(State.LOADED);
    }
  }

  public void write(CDODataOutput out, boolean withPackages) throws IOException
  {
    if (TRACER.isEnabled())
    {
      TRACER.format("Writing {0}", this); //$NON-NLS-1$
    }

    out.writeBoolean(withPackages);
    if (withPackages)
    {
      CDOModelUtil.writePackage(out, packageInfos[0].getEPackage(), true, packageRegistry);
    }

    out.writeCDOPackageUnitType(originalType);
    out.writeLong(timeStamp);
    out.writeInt(packageInfos.length);
    for (InternalCDOPackageInfo packageInfo : packageInfos)
    {
      out.writeCDOPackageInfo(packageInfo);
    }
  }

  public void read(CDODataInput in, ResourceSet resourceSet) throws IOException
  {
    EPackage ePackage = null;
    boolean withPackages = in.readBoolean();
    if (withPackages)
    {
      CheckUtil.checkArg(resourceSet, "resourceSet"); //$NON-NLS-1$
      CheckUtil.checkNull(resourceSet.getPackageRegistry(), "ResourceSet's packageRegistry == null");
      ePackage = CDOModelUtil.readPackage(in, resourceSet, true);
      EPackage globalPackage = loadPackageFromGlobalRegistry(ePackage.getNsURI());
      if (globalPackage != null)
      {
        ePackage = globalPackage;
      }

      setState(State.LOADED);
    }

    originalType = in.readCDOPackageUnitType();
    timeStamp = in.readLong();
    packageInfos = new InternalCDOPackageInfo[in.readInt()];
    for (int i = 0; i < packageInfos.length; i++)
    {
      packageInfos[i] = (InternalCDOPackageInfo)in.readCDOPackageInfo();
      packageInfos[i].setPackageUnit(this);
    }

    if (ePackage != null)
    {
      attachPackageInfos(ePackage);
    }

    if (TRACER.isEnabled())
    {
      TRACER.format("Read {0}", this); //$NON-NLS-1$
    }
  }

  public int compareTo(CDOPackageUnit o)
  {
    return getID().compareTo(o.getID());
  }

  @Override
  public String toString()
  {
    String fmt = "CDOPackageUnit[id={0}, state={1}, type={2}, originalType={3}, timeStamp={4}]"; //$NON-NLS-1$
    return MessageFormat.format(fmt, getID(), getState(), getType(), getOriginalType(),
        CDOCommonUtil.formatTimeStamp(getTimeStamp()));
  }

  private void initPackageInfos(EPackage ePackage, List<InternalCDOPackageInfo> result)
  {
    InternalCDOPackageInfo packageInfo = (InternalCDOPackageInfo)CDOModelUtil.createPackageInfo();
    packageInfo.setPackageUnit(this);
    packageInfo.setPackageURI(ePackage.getNsURI());
    packageInfo.setParentURI(ePackage.getESuperPackage() == null ? null : ePackage.getESuperPackage().getNsURI());
    EMFUtil.addAdapter(ePackage, packageInfo);

    packageRegistry.basicPut(ePackage.getNsURI(), ePackage);
    result.add(packageInfo);
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      initPackageInfos(subPackage, result);
    }
  }

  private void attachPackageInfos(EPackage ePackage)
  {
    InternalCDOPackageInfo packageInfo = getPackageInfo(ePackage.getNsURI());
    if (packageInfo != null)
    {
      EMFUtil.addAdapter(ePackage, packageInfo);
    }

    for (EPackage subPackage : ePackage.getESubpackages())
    {
      attachPackageInfos(subPackage);
    }
  }

  private EPackage[] loadPackagesFromGlobalRegistry()
  {
    EPackage[] ePackages = new EPackage[packageInfos.length];
    for (int i = 0; i < ePackages.length; i++)
    {
      ePackages[i] = loadPackageFromGlobalRegistry(packageInfos[i].getPackageURI());
      if (ePackages[i] == null)
      {
        return null;
      }
    }

    return ePackages;
  }

  private EPackage loadPackageFromGlobalRegistry(String packageURI)
  {
    return EPackage.Registry.INSTANCE.getEPackage(packageURI);
  }
}
