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
package org.eclipse.emf.cdo.spi.common.model;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

/**
 * @author Eike Stepper
 * @since 4.0
 */
@Deprecated
public abstract class DelegatingCDOPackageRegistry extends Lifecycle implements InternalCDOPackageRegistry
{
  public DelegatingCDOPackageRegistry()
  {
  }

  protected abstract InternalCDOPackageRegistry getDelegate();

  public Object basicPut(String nsURI, Object value)
  {
    return getDelegate().basicPut(nsURI, value);
  }

  public void clear()
  {
    getDelegate().clear();
  }

  public boolean containsKey(Object key)
  {
    return getDelegate().containsKey(key);
  }

  public boolean containsValue(Object value)
  {
    return getDelegate().containsValue(value);
  }

  public Set<java.util.Map.Entry<String, Object>> entrySet()
  {
    return getDelegate().entrySet();
  }

  public Object get(Object key)
  {
    return getDelegate().get(key);
  }

  public EFactory getEFactory(String nsURI)
  {
    return getDelegate().getEFactory(nsURI);
  }

  public EPackage getEPackage(String nsURI)
  {
    return getDelegate().getEPackage(nsURI);
  }

  public EPackage[] getEPackages()
  {
    return getDelegate().getEPackages();
  }

  public InternalCDOPackageInfo getPackageInfo(EPackage ePackage)
  {
    return getDelegate().getPackageInfo(ePackage);
  }

  public InternalCDOPackageInfo[] getPackageInfos()
  {
    return getDelegate().getPackageInfos();
  }

  public PackageLoader getPackageLoader()
  {
    return getDelegate().getPackageLoader();
  }

  public PackageProcessor getPackageProcessor()
  {
    return getDelegate().getPackageProcessor();
  }

  public InternalCDOPackageUnit getPackageUnit(EPackage ePackage)
  {
    return getDelegate().getPackageUnit(ePackage);
  }

  public InternalCDOPackageUnit[] getPackageUnits()
  {
    return getDelegate().getPackageUnits();
  }

  public boolean isEmpty()
  {
    return getDelegate().isEmpty();
  }

  public boolean isReplacingDescriptors()
  {
    return getDelegate().isReplacingDescriptors();
  }

  public Set<String> keySet()
  {
    return getDelegate().keySet();
  }

  public Object put(String key, Object value)
  {
    return getDelegate().put(key, value);
  }

  public void putAll(Map<? extends String, ? extends Object> t)
  {
    getDelegate().putAll(t);
  }

  public Object putEPackage(EPackage ePackage)
  {
    return getDelegate().putEPackage(ePackage);
  }

  public void putPackageUnit(InternalCDOPackageUnit packageUnit)
  {
    getDelegate().putPackageUnit(packageUnit);
  }

  public Object remove(Object key)
  {
    return getDelegate().remove(key);
  }

  public void setPackageLoader(PackageLoader packageLoader)
  {
    getDelegate().setPackageLoader(packageLoader);
  }

  public void setPackageProcessor(PackageProcessor packageProcessor)
  {
    getDelegate().setPackageProcessor(packageProcessor);
  }

  public void setReplacingDescriptors(boolean replacingDescriptors)
  {
    getDelegate().setReplacingDescriptors(replacingDescriptors);
  }

  public int size()
  {
    return getDelegate().size();
  }

  public Collection<Object> values()
  {
    return getDelegate().values();
  }
}
