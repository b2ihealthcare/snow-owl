/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.common.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.net4j.util.WrappedException;

/**
 * Various static helper methods for dealing with EMF meta models.
 * 
 * @author Eike Stepper
 * @since 2.0
 * @apiviz.exclude
 */
public final class EMFUtil
{
  /**
   * @since 3.0
   */
  public static final String CDO_ANNOTATION_SOURCE = "http://www.eclipse.org/emf/CDO";

  /**
   * @since 3.0
   */
  public static final String CDO_ANNOTATION_KEY_PERSISTENT = "persistent";

  private static final EReference EOPERATION_EEXCEPTIONS = EcorePackage.eINSTANCE.getEOperation_EExceptions();

  private static final EReference ETYPED_ELEMENT_ETYPE = EcorePackage.eINSTANCE.getETypedElement_EType();

  private static final EReference ECLASS_ESUPER_TYPES = EcorePackage.eINSTANCE.getEClass_ESuperTypes();

  private EMFUtil()
  {
  }

  public static EPackage getGeneratedEPackage(EPackage ePackage)
  {
    String packageURI = ePackage.getNsURI();
    if (packageURI.equals(EcorePackage.eINSTANCE.getNsURI()))
    {
      return EcorePackage.eINSTANCE;
    }

    EPackage.Registry registry = EPackage.Registry.INSTANCE;
    return registry.getEPackage(packageURI);
  }

  public static Map.Entry<String, Object>[] getSortedRegistryEntries(EPackage.Registry packageRegistry)
  {
    Set<Map.Entry<String, Object>> entries = packageRegistry.entrySet();
    @SuppressWarnings("unchecked")
    Map.Entry<String, Object>[] array = entries.toArray(new Entry[entries.size()]);
    Arrays.sort(array, new Comparator<Map.Entry<String, Object>>()
    {
      public int compare(Map.Entry<String, Object> o1, Map.Entry<String, Object> o2)
      {
        return o1.getKey().compareTo(o2.getKey());
      }
    });

    return array;
  }

  public static EPackage getTopLevelPackage(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    return superPackage == null ? ePackage : getTopLevelPackage(superPackage);
  }

  /**
   * @since 2.0
   */
  public static EPackage createEPackage(String name, String nsPrefix, String nsURI)
  {
    EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
    ePackage.setName(name);
    ePackage.setNsPrefix(nsPrefix);
    ePackage.setNsURI(nsURI);
    return ePackage;
  }

  /**
   * @since 2.0
   */
  public static EClass createEClass(EPackage ePackage, String name, boolean isAbstract, boolean isInterface)
  {
    EClass eClass = EcoreFactory.eINSTANCE.createEClass();
    eClass.setName(name);
    eClass.setAbstract(isAbstract);
    eClass.setInterface(isInterface);
    ePackage.getEClassifiers().add(eClass);
    return eClass;
  }

  /**
   * @since 2.0
   */
  public static EAttribute createEAttribute(EClass eClass, String name, EClassifier type)
  {
    EAttribute eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
    eAttribute.setName(name);
    eAttribute.setEType(type);
    eClass.getEStructuralFeatures().add(eAttribute);
    return eAttribute;
  }

  /**
   * @since 2.0
   */
  public static EReference createEReference(EClass eClass, String name, EClassifier type, boolean isRequired,
      boolean isMany)
  {
    EReference eReference = EcoreFactory.eINSTANCE.createEReference();
    eReference.setName(name);
    eReference.setEType(type);
    eReference.setLowerBound(isRequired ? 1 : 0);
    eReference.setUpperBound(isMany ? -1 : 0);
    eClass.getEStructuralFeatures().add(eReference);
    return eReference;
  }

  public static EClass[] getPersistentClasses(EPackage ePackage)
  {
    List<EClass> result = new ArrayList<EClass>();
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        result.add((EClass)classifier);
      }
    }

    return result.toArray(new EClass[result.size()]);
  }

  /**
   * @since 3.0
   */
  public static List<EStructuralFeature> getPersistentFeatures(EList<EStructuralFeature> eFeatures)
  {
    List<EStructuralFeature> result = new ArrayList<EStructuralFeature>();
    for (EStructuralFeature feature : eFeatures)
    {
      if (isPersistent(feature))
      {
        result.add(feature);
      }
    }

    return result;
  }

  /**
   * @since 3.0
   */
  public static boolean isPersistent(EStructuralFeature feature)
  {
    if (feature == ECLASS_ESUPER_TYPES || feature == ETYPED_ELEMENT_ETYPE || feature == EOPERATION_EEXCEPTIONS)
    {
      // http://www.eclipse.org/newsportal/article.php?id=26780&group=eclipse.tools.emf#26780
      return false;
    }

    String persistent = EcoreUtil.getAnnotation(feature, CDO_ANNOTATION_SOURCE, CDO_ANNOTATION_KEY_PERSISTENT);
    if (persistent != null)
    {
      return "true".equalsIgnoreCase(persistent);
    }

    if (feature.isTransient())
    {
      // Bug 333950: Transient eRefs with a persistent eOpposite, must be considered persistent
      if (feature instanceof EReference)
      {
        EReference eOpposite = ((EReference)feature).getEOpposite();
        if (eOpposite != null && !eOpposite.isTransient())
        {
          return true;
        }
      }

      return false;
    }

    return true;
  }

  public static boolean isDynamicEPackage(Object value)
  {
    return value.getClass() == EPackageImpl.class;
  }

  public static String getParentURI(EPackage ePackage)
  {
    EPackage superPackage = ePackage.getESuperPackage();
    String parentURI = superPackage == null ? null : superPackage.getNsURI();
    return parentURI;
  }

  public static void registerPackage(EPackage ePackage, EPackage.Registry... packageRegistries)
  {
    ePackage.getClass(); // Initialize package in standalone mode
    if (packageRegistries == null || packageRegistries.length == 0)
    {
      EPackage.Registry[] globalRegistry = { EPackage.Registry.INSTANCE };
      packageRegistries = globalRegistry;
    }

    for (EPackage.Registry packageRegistry : packageRegistries)
    {
      packageRegistry.put(ePackage.getNsURI(), ePackage);
    }
  }

  public static byte[] getEPackageBytes(EPackage ePackage, boolean zipped, EPackage.Registry packageRegistry)
  {
    try
    {
      Resource resource = ePackage.eResource();
      if (resource == null)
      {
        // Happens e.g. for dynamic packages that were not loaded from a resource
        ResourceSet resourceSet = newEcoreResourceSet(packageRegistry);
        resource = resourceSet.createResource(URI.createURI(ePackage.getNsURI()));
        resource.getContents().add(ePackage);
      }

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      resource.save(baos, createResourceOptions(zipped));
      return baos.toByteArray();
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  /**
   * @since 3.0
   */
  public static EPackage createEPackage(String uri, byte[] bytes, boolean zipped, ResourceSet resourceSet,
      boolean lookForResource)
  {
    try
    {
      Resource resource = null;
      if (lookForResource)
      {
        resource = resourceSet.getResource(URI.createURI(uri), true);
      }

      if (resource == null)
      {
        resource = resourceSet.createResource(URI.createURI(uri));
      }

      ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
      resource.load(bais, createResourceOptions(zipped));

      EList<EObject> contents = resource.getContents();
      return (EPackage)contents.get(0);
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
  }

  private static Map<String, Object> createResourceOptions(boolean zipped)
  {
    Map<String, Object> options = new HashMap<String, Object>();
    if (zipped)
    {
      options.put(Resource.OPTION_ZIP, true);
    }

    return options;
  }

  public static void addAdapter(Notifier notifier, Adapter adapter)
  {
    synchronized (notifier)
    {
      EList<Adapter> adapters = notifier.eAdapters();
      adapters.add(adapter);
    }
  }

  public static EPackage[] getAllPackages(EPackage ePackage)
  {
    List<EPackage> result = new ArrayList<EPackage>();
    getAllPackages(ePackage, result);
    return result.toArray(new EPackage[result.size()]);
  }

  private static void getAllPackages(EPackage ePackage, List<EPackage> result)
  {
    result.add(ePackage);
    for (EPackage subPackage : ePackage.getESubpackages())
    {
      getAllPackages(subPackage, result);
    }
  }

  public static String getQualifiedName(EPackage ePackage, String separator)
  {
    StringBuilder builder = new StringBuilder();
    EPackage eSuperPackage = ePackage.getESuperPackage();
    if (eSuperPackage != null)
    {
      builder.append(getQualifiedName(eSuperPackage, separator));
      builder.append(separator);
    }

    builder.append(ePackage.getName());
    return builder.toString();
  }

  public static String getQualifiedName(EClassifier classifier, String separator)
  {
    StringBuilder builder = new StringBuilder();
    EPackage ePackage = classifier.getEPackage();
    if (ePackage != null)
    {
      builder.append(getQualifiedName(ePackage, separator));
      builder.append(separator);
    }

    builder.append(classifier.getName());
    return builder.toString();
  }

  public static ResourceSet newResourceSet(Resource.Factory resourceFactory)
  {
    ResourceSet resourceSet = new ResourceSetImpl();
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet(EPackage.Registry packageRegistry)
  {
    ResourceSet resourceSet = newResourceSet(new EcoreResourceFactoryImpl());
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }

  public static ResourceSet newEcoreResourceSet()
  {
    return newEcoreResourceSet(EPackage.Registry.INSTANCE);
  }

  /**
   * @since 3.0
   */
  public static EObject safeResolve(EObject proxy, ResourceSet resourceSet)
  {
    if (!proxy.eIsProxy())
    {
      return proxy;
    }

    EObject resolved = EcoreUtil.resolve(proxy, resourceSet);
    if (resolved == proxy)
    {
      throw new IllegalStateException("Unresolvable proxy: " + ((InternalEObject)proxy).eProxyURI());
    }

    return resolved;
  }

  /**
   * @since 3.0
   */
  public static void safeResolveAll(ResourceSet resourceSet)
  {
    TreeIterator<Notifier> it = resourceSet.getAllContents();
    while (it.hasNext())
    {
      Notifier notifier = it.next();
      if (notifier instanceof EObject)
      {
        safeResolve((EObject)notifier, resourceSet);
        Iterator<EObject> it2 = ((EObject)notifier).eCrossReferences().iterator();
        while (it2.hasNext())
        {
          safeResolve(it2.next(), resourceSet);
        }
      }
    }
  }

  /**
   * @see ExtResourceSet
   * @since 4.0
   */
  public static ExtResourceSet createExtResourceSet(InternalCDOPackageRegistry packageRegistry, boolean delegating,
      boolean demandLoading)
  {
    Resource.Factory resourceFactory = new EcoreResourceFactoryImpl();

    ExtResourceSet resourceSet = new ExtResourceSet(delegating, demandLoading);
    resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", resourceFactory); //$NON-NLS-1$
    resourceSet.setPackageRegistry(packageRegistry);
    return resourceSet;
  }

  /**
   * An extension of {@link ResourceSetImpl} that allows demandLoading of resources and delegation of resource lookups,
   * to be switched on/off as desired.
   * 
   * @since 4.0
   * @apiviz.exclude
   */
  public static class ExtResourceSet extends ResourceSetImpl
  {
    private boolean delegating;

    private boolean demandLoading;

    ExtResourceSet(boolean delegating, boolean demandLoading)
    {
      this.delegating = delegating;
      this.demandLoading = demandLoading;
    }

    public boolean isDelegating()
    {
      return delegating;
    }

    public void setDelegating(boolean delegating)
    {
      this.delegating = delegating;
    }

    public boolean isDemandLoading()
    {
      return demandLoading;
    }

    public void setDemandLoading(boolean demandLoading)
    {
      this.demandLoading = demandLoading;
    }

    @Override
    protected void demandLoad(Resource resource) throws IOException
    {
      if (demandLoading)
      {
        super.demandLoad(resource);
      }
    }

    @Override
    protected Resource delegatedGetResource(URI uri, boolean loadOnDemand)
    {
      if (delegating)
      {
        return super.delegatedGetResource(uri, loadOnDemand);
      }

      return null;
    }
  }
}
