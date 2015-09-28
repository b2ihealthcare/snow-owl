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
package org.eclipse.emf.cdo.common.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.cdo.common.model.CDOPackageUnit.Type;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.om.OMPlatform;

/**
 * A {@link #INSTANCE singleton} registry for the {@link Type package unit types} of EMF {@link EPackage packages}.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @apiviz.exclude
 */
public final class CDOPackageTypeRegistry
{
  public static final CDOPackageTypeRegistry INSTANCE = new CDOPackageTypeRegistry();

  private static final String ECORE_ID = "org.eclipse.emf.ecore"; //$NON-NLS-1$

  private static final String PPID = EcorePlugin.GENERATED_PACKAGE_PPID;

  private static final String MARKER_FILE = "META-INF/CDO.MF"; //$NON-NLS-1$

  private Map<String, CDOPackageUnit.Type> types = new HashMap<String, CDOPackageUnit.Type>();

  private Map<String, CDOPackageUnit.Type> bundles = new HashMap<String, CDOPackageUnit.Type>();

  private CDOPackageTypeRegistry()
  {
  }

  public synchronized CDOPackageUnit.Type register(EPackage ePackage)
  {
    CDOPackageUnit.Type type = getPackageType(ePackage);
    types.put(ePackage.getNsURI(), type);
    return type;
  }

  public synchronized void registerNative(String packageURI)
  {
    types.put(packageURI, CDOPackageUnit.Type.NATIVE);
  }

  public synchronized void registerLegacy(String packageURI)
  {
    types.put(packageURI, CDOPackageUnit.Type.LEGACY);
  }

  public synchronized void registerDynamic(String packageURI)
  {
    types.put(packageURI, CDOPackageUnit.Type.DYNAMIC);
  }

  public synchronized CDOPackageUnit.Type deregister(String packageURI)
  {
    return types.remove(packageURI);
  }

  public synchronized CDOPackageUnit.Type lookup(String packageURI)
  {
    CDOPackageUnit.Type type = types.get(packageURI);
    if (type == null)
    {
      Object value = EPackage.Registry.INSTANCE.get(packageURI);
      if (value instanceof EPackage)
      {
        EPackage ePackage = (EPackage)value;
        type = register(ePackage);
      }

      if (type == null && OMPlatform.INSTANCE.isExtensionRegistryAvailable())
      {
        type = getTypeFromBundle(packageURI);
        types.put(packageURI, type);
      }
    }

    return type;
  }

  public synchronized CDOPackageUnit.Type lookup(EPackage ePackage)
  {
    String packageURI = ePackage.getNsURI();
    CDOPackageUnit.Type type = types.get(packageURI);
    if (type == null)
    {
      type = register(ePackage);
    }

    return type;
  }

  public synchronized void reset()
  {
    types.clear();
    bundles.clear();
  }

  private CDOPackageUnit.Type getTypeFromBundle(String packageURI)
  {
    String bundleID = getBundleID(packageURI);
    if (bundleID == null)
    {
      return CDOPackageUnit.Type.UNKNOWN;
    }

    CDOPackageUnit.Type type = bundles.get(bundleID);
    if (type == null)
    {
      org.osgi.framework.Bundle bundle = org.eclipse.core.runtime.Platform.getBundle(bundleID);
      if (bundle == null)
      {
        type = CDOPackageUnit.Type.UNKNOWN;
      }
      else if (bundle.getEntry(MARKER_FILE) != null)
      {
        type = CDOPackageUnit.Type.NATIVE;
      }
      else
      {
        type = CDOPackageUnit.Type.LEGACY;
      }

      bundles.put(bundleID, type);
    }

    return type;
  }

  private static String getBundleID(String packageURI)
  {
    org.eclipse.core.runtime.IExtensionRegistry registry = org.eclipse.core.runtime.Platform.getExtensionRegistry();
    for (org.eclipse.core.runtime.IConfigurationElement element : registry.getConfigurationElementsFor(ECORE_ID, PPID))
    {
      String uri = element.getAttribute("uri"); //$NON-NLS-1$
      if (ObjectUtil.equals(uri, packageURI))
      {
        return element.getContributor().getName();
      }
    }

    return null;
  }

  private static CDOPackageUnit.Type getPackageType(EPackage ePackage)
  {
    if (ePackage.getClass() == EPackageImpl.class)
    {
      return CDOPackageUnit.Type.DYNAMIC;
    }

    EPackage topLevelPackage = EMFUtil.getTopLevelPackage(ePackage);
    EClass eClass = getAnyConcreteEClass(topLevelPackage);
    if (eClass != null)
    {
      EObject testObject = EcoreUtil.create(eClass);
      if (testObject instanceof CDOObjectMarker)
      {
        return CDOPackageUnit.Type.NATIVE;
      }

      return CDOPackageUnit.Type.LEGACY;
    }

    return null;
  }

  private static EClass getAnyConcreteEClass(EPackage ePackage)
  {
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        if (!(eClass.isAbstract() || eClass.isInterface()))
        {
          return eClass;
        }
      }
    }

    for (EPackage subpackage : ePackage.getESubpackages())
    {
      EClass eClass = getAnyConcreteEClass(subpackage);
      if (eClass != null)
      {
        return eClass;
      }
    }

    return null;
  }

  /**
   * A common marker interface for CDO (native) objects.
   * 
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   * @apiviz.exclude
   */
  public static interface CDOObjectMarker
  {
  }
}
