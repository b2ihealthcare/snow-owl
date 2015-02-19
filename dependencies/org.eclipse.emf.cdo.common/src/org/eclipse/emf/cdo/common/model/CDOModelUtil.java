/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Stefan Winkler - Bug 332912 - Caching subtype-relationships in the CDOPackageRegistry
 *    Erdal Karaca - added support for HASHMAP CDO Type
 */
package org.eclipse.emf.cdo.common.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.bundle.OM;
import org.eclipse.emf.cdo.internal.common.messages.Messages;
import org.eclipse.emf.cdo.internal.common.model.CDOClassInfoImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageInfoImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOPackageUnitImpl;
import org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;

/**
 * Various static helper methods for dealing with CDO meta models.
 *
 * @author Eike Stepper
 * @since 2.0
 * @apiviz.uses {@link CDOType} - - provides
 */
public final class CDOModelUtil implements CDOModelConstants
{ 
  private static CDOType[] coreTypes;

  static
  {
    List<CDOType> types = new ArrayList<CDOType>();
    registerCoreType(types, EcorePackage.eINSTANCE.getEBigDecimal(), CDOType.BIG_DECIMAL);
    registerCoreType(types, EcorePackage.eINSTANCE.getEBigInteger(), CDOType.BIG_INTEGER);
    registerCoreType(types, EcorePackage.eINSTANCE.getEBooleanObject(), CDOType.BOOLEAN_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEBoolean(), CDOType.BOOLEAN);
    registerCoreType(types, EcorePackage.eINSTANCE.getEByteArray(), CDOType.BYTE_ARRAY);
    registerCoreType(types, EcorePackage.eINSTANCE.getEByteObject(), CDOType.BYTE_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEByte(), CDOType.BYTE);
    registerCoreType(types, EcorePackage.eINSTANCE.getECharacterObject(), CDOType.CHARACTER_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEChar(), CDOType.CHAR);
    registerCoreType(types, EcorePackage.eINSTANCE.getEDate(), CDOType.DATE);
    registerCoreType(types, EcorePackage.eINSTANCE.getEDoubleObject(), CDOType.DOUBLE_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEDouble(), CDOType.DOUBLE);
    registerCoreType(types, EcorePackage.eINSTANCE.getEFloatObject(), CDOType.FLOAT_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEFloat(), CDOType.FLOAT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEIntegerObject(), CDOType.INTEGER_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEInt(), CDOType.INT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEJavaClass(), null);
    registerCoreType(types, EcorePackage.eINSTANCE.getEJavaObject(), null);
    registerCoreType(types, EcorePackage.eINSTANCE.getELongObject(), CDOType.LONG_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getELong(), CDOType.LONG);
    registerCoreType(types, EcorePackage.eINSTANCE.getEShortObject(), CDOType.SHORT_OBJECT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEShort(), CDOType.SHORT);
    registerCoreType(types, EcorePackage.eINSTANCE.getEString(), CDOType.STRING);
    coreTypes = types.toArray(new CDOType[types.size()]);
  }

  private static void registerCoreType(List<CDOType> types, EClassifier classifier, CDOType type)
  {
    int index = classifier.getClassifierID();
    while (index >= types.size())
    {
      types.add(null);
    }

    types.set(index, type);
  }

  private CDOModelUtil()
  {
  }

  /**
   * @since 2.0
   */
  public static boolean isCorePackage(EPackage ePackage)
  {
    return CDOModelConstants.CORE_PACKAGE_URI.equals(ePackage.getNsURI());
  }

  /**
   * @since 2.0
   */
  public static boolean isRoot(EClass eClass)
  {
    return isCorePackage(eClass.getEPackage()) && CDOModelConstants.ROOT_CLASS_NAME.equals(eClass.getName());
  }

  /**
   * @since 2.0
   */
  public static boolean isResourcePackage(EPackage ePackage)
  {
    return CDOModelConstants.RESOURCE_PACKAGE_URI.equals(ePackage.getNsURI());
  }

  /**
   * @since 2.0
   */
  public static boolean isResource(EClass eClass)
  {
    return isResourcePackage(eClass.getEPackage()) && CDOModelConstants.RESOURCE_CLASS_NAME.equals(eClass.getName());
  }

  /**
   * @since 2.0
   */
  public static boolean isResourceFolder(EClass eClass)
  {
    return isResourcePackage(eClass.getEPackage())
        && CDOModelConstants.RESOURCE_FOLDER_CLASS_NAME.equals(eClass.getName());
  }

  /**
   * @since 2.0
   */
  public static boolean isResourceNode(EClass eClass)
  {
    return isResourcePackage(eClass.getEPackage())
        && (CDOModelConstants.RESOURCE_NODE_CLASS_NAME.equals(eClass.getName())
            || CDOModelConstants.RESOURCE_CLASS_NAME.equals(eClass.getName()) || CDOModelConstants.RESOURCE_FOLDER_CLASS_NAME
              .equals(eClass.getName()));
  }

  /**
   * @since 4.0
   */
  public static boolean isTypesPackage(EPackage ePackage)
  {
    return CDOModelConstants.TYPES_PACKAGE_URI.equals(ePackage.getNsURI());
  }

  /**
   * @since 2.0
   */
  public static boolean isSystemPackage(EPackage ePackage)
  {
    return isCorePackage(ePackage) || isResourcePackage(ePackage) || isTypesPackage(ePackage);
  }

  /**
   * @since 4.0
   */
  public static boolean isLob(EClassifier eClassifier)
  {
    return isTypesPackage(eClassifier.getEPackage())
        && (CDOModelConstants.BLOB_CLASS_NAME.equals(eClassifier.getName()) || CDOModelConstants.CLOB_CLASS_NAME
            .equals(eClassifier.getName()));
  }

  /**
   * @since 2.0
   */
  public static CDOType getType(EStructuralFeature feature)
  {
    if (FeatureMapUtil.isFeatureMap(feature))
    {
      return CDOType.FEATURE_MAP_ENTRY;
    }

    return CDOModelUtil.getType(feature.getEType());
  }

  /**
   * @since 4.0
   */
  public static CDOType getType(byte typeID)
  {
    return CDOTypeImpl.getType(typeID);
  }

  /**
   * @since 2.0
   */
  public static CDOType getType(EClassifier classifier)
  {
    if (classifier instanceof EClass)
    {
      return CDOType.OBJECT;
    }

    if (classifier instanceof EEnum)
    {
      return CDOType.ENUM_ORDINAL;
    }

    EDataType eDataType = (EDataType)classifier;
    EPackage ePackage = eDataType.getEPackage();

    if (isCorePackage(ePackage))
    {
      CDOType type = getCoreType(eDataType);
      if (type != null)
      {
        return type;
      }
    }
    else if (isTypesPackage(ePackage))
    {
      String name = eDataType.getName();
      if (CDOModelConstants.BLOB_CLASS_NAME.equals(name))
      {
        return CDOType.BLOB;
      }

      if (CDOModelConstants.CLOB_CLASS_NAME.equals(name))
      {
        return CDOType.CLOB;
      }

      throw new IllegalArgumentException("Illegal data type: " + eDataType);
    }

    return CDOType.CUSTOM;
  }

  /**
   * Core types includes also complex data like EAnnotation, and EEnum
   *
   * @since 2.0
   */
  public static CDOType getCoreType(EClassifier eDataType)
  {
    int index = eDataType.getClassifierID();
    if (0 <= index && index < coreTypes.length)
    {
      return coreTypes[index];
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOType getPrimitiveType(Class<? extends Object> primitiveType)
  {
    if (primitiveType == String.class)
    {
      return CDOType.STRING;
    }

    if (primitiveType == Boolean.class)
    {
      return CDOType.BOOLEAN;
    }

    if (primitiveType == Integer.class)
    {
      return CDOType.INT;
    }

    if (primitiveType == Double.class)
    {
      return CDOType.DOUBLE;
    }

    if (primitiveType == Float.class)
    {
      return CDOType.FLOAT;
    }

    if (primitiveType == Long.class)
    {
      return CDOType.LONG;
    }

    if (primitiveType == Date.class)
    {
      return CDOType.DATE;
    }

    if (primitiveType == Byte.class)
    {
      return CDOType.BYTE;
    }

    if (primitiveType == Character.class)
    {
      return CDOType.CHAR;
    }

    if (primitiveType == Object[].class)
    {
      return CDOType.OBJECT_ARRAY;
    }

    if (EEnumLiteral.class.isAssignableFrom(primitiveType) || Enumerator.class.isAssignableFrom(primitiveType))
    {
      return CDOType.ENUM_LITERAL;
    }

    if (Map.class.isAssignableFrom(primitiveType))
    {
      return CDOType.MAP;
    }

    if (Set.class.isAssignableFrom(primitiveType))
    {
      return CDOType.SET;
    }

    if (List.class.isAssignableFrom(primitiveType))
    {
      return CDOType.LIST;
    }

    throw new IllegalArgumentException(MessageFormat.format(Messages.getString("CDOModelUtil.7"), primitiveType)); //$NON-NLS-1$
  }

  /**
   * @since 4.0
   */
  public static CDOType getTypeOfObject(Object object)
  {
    if (object instanceof BigDecimal)
    {
      return CDOType.BIG_DECIMAL;
    }

    if (object instanceof BigInteger)
    {
      return CDOType.BIG_INTEGER;
    }

    if (object instanceof Boolean)
    {
      return CDOType.BOOLEAN_OBJECT;
    }

    if (object instanceof Byte)
    {
      return CDOType.BYTE_OBJECT;
    }

    if (object instanceof byte[])
    {
      return CDOType.BYTE_ARRAY;
    }

    if (object instanceof Character)
    {
      return CDOType.CHARACTER_OBJECT;
    }

    if (object instanceof Date)
    {
      return CDOType.DATE;
    }

    if (object instanceof Double)
    {
      return CDOType.DOUBLE_OBJECT;
    }

    if (object instanceof EEnumLiteral)
    {
      return CDOType.ENUM_LITERAL;
    }

    if (object instanceof FeatureMap.Entry)
    {
      return CDOType.FEATURE_MAP_ENTRY;
    }

    if (object instanceof Float)
    {
      return CDOType.FLOAT_OBJECT;
    }

    if (object instanceof Integer)
    {
      return CDOType.INTEGER_OBJECT;
    }

    if (object instanceof Long)
    {
      return CDOType.LONG_OBJECT;
    }

    if (object instanceof Short)
    {
      return CDOType.SHORT_OBJECT;
    }

    if (object instanceof String)
    {
      return CDOType.STRING;
    }

    if (object instanceof CDOID || object instanceof CDORevision)
    {
      return CDOType.OBJECT;
    }

    if (object == null)
    {
      return CDOType.OBJECT;
    }

    throw new IllegalArgumentException("Object type " + object.getClass().getName() + " is not supported.");
  }

  /**
   * @since 2.0
   */
  public static CDOPackageInfo getPackageInfo(Object value, CDOPackageRegistry packageRegistry)
  {
    if (value instanceof EPackage)
    {
      return packageRegistry.getPackageInfo((EPackage)value);
    }

    if (value instanceof CDOPackageInfo)
    {
      CDOPackageInfo packageInfo = (CDOPackageInfo)value;
      if (packageInfo.getPackageUnit().getPackageRegistry() == packageRegistry)
      {
        return packageInfo;
      }
    }

    return null;
  }

  /**
   * @since 2.0
   */
  public static CDOClassInfo getClassInfo(EClass eClass)
  {
    synchronized (eClass)
    {
      EList<Adapter> adapters = eClass.eAdapters();
      CDOClassInfo classInfo = (CDOClassInfo)EcoreUtil.getAdapter(adapters, CDOClassInfo.class);
      if (classInfo == null)
      {
        classInfo = new CDOClassInfoImpl();
        adapters.add(classInfo);
      }

      return classInfo;
    }
  }

  /**
   * @since 2.0
   */
  public static EStructuralFeature[] getAllPersistentFeatures(EClass eClass)
  {
    CDOClassInfo classInfo = getClassInfo(eClass);
    return classInfo.getAllPersistentFeatures();
  }

  /**
   * @since 2.0
   */
  public static CDOPackageUnit createPackageUnit()
  {
    return new CDOPackageUnitImpl();
  }

  /**
   * @since 2.0
   */
  public static CDOPackageInfo createPackageInfo()
  {
    return new CDOPackageInfoImpl();
  }

  /**
   * @since 3.0
   */
  public static EPackage readPackage(ExtendedDataInput in, ResourceSet resourceSet, boolean lookForResource)
      throws IOException
  {
    String uri = in.readString();
    boolean zipped = in.readBoolean();
    byte[] bytes = in.readByteArray();
    return EMFUtil.createEPackage(uri, bytes, zipped, resourceSet, lookForResource);
  }

  /**
   * @since 2.0
   */
  public static void writePackage(ExtendedDataOutput out, EPackage ePackage, boolean zipped,
      EPackage.Registry packageRegistry) throws IOException
  {
    checkCrossResourceURIs(ePackage);

    byte[] bytes = EMFUtil.getEPackageBytes(ePackage, zipped, packageRegistry);
    out.writeString(ePackage.getNsURI());
    out.writeBoolean(zipped);
    out.writeByteArray(bytes);
  }

  /**
   * @since 3.0
   */
  public static void checkCrossResourceURIs(EPackage ePackage)
  {
    TreeIterator<EObject> it = ePackage.eAllContents();
    while (it.hasNext())
    {
      EObject e = it.next();
      for (EObject r : e.eCrossReferences())
      {
        EObject refTarget = null;

        if (r.eIsProxy())
        {
          String msg = "Package '%s' contains unresolved proxy '%s'";
          msg = String.format(msg, ePackage.getNsURI(), ((InternalEObject)r).eProxyURI());
          throw new IllegalStateException(msg);
        }

        if (r.eResource() != null && r.eResource() != e.eResource())
        {
          // It's a ref into another resource
          EPackage pkg = null;
          if (r instanceof EClassifier)
          {
            refTarget = r;
            pkg = ((EClassifier)r).getEPackage();
          }
          else if (r instanceof EStructuralFeature)
          {
            refTarget = r;
            EStructuralFeature feature = (EStructuralFeature)r;
            EClass ownerClass = (EClass)feature.eContainer();
            pkg = ownerClass.getEPackage();
          }
          else if (r instanceof EGenericType)
          {
            EGenericType genType = (EGenericType)r;
            EClassifier c = genType.getEClassifier();
            if (c != null)
            {
              refTarget = c;
              pkg = c.getEPackage();
            }
          }

          if (pkg == null)
          {
            continue;
          }

          while (pkg.getESuperPackage() != null)
          {
            pkg = pkg.getESuperPackage();
          }

          String resourceURI = refTarget.eResource().getURI().toString();
          if (!resourceURI.toString().equals(pkg.getNsURI()))
          {
            String msg = "URI of the resource (%s) does not match the nsURI (%s) of the top-level package;\n"
                + "this can be fixed by calling Resource.setURI(URI) after loading the packages,\n"
                + "or by configuring a URI mapping from nsURI's to location URI's before loading the packages,\n"
                + "and then loading them with their nsURI's";
            msg = String.format(msg, resourceURI, pkg.getNsURI());
            throw new IllegalStateException(msg);
          }
        }
      }
    }
  }

  /**
   * @since 4.0
   */
  public static Map<EClass, List<EClass>> getSubTypes(EPackage.Registry packageRegistry)
  {
    Map<EClass, List<EClass>> result = new HashMap<EClass, List<EClass>>();
    for (String nsURI : packageRegistry.keySet())
    {
      EPackage ePackage = packageRegistry.getEPackage(nsURI);
      getSubTypes(ePackage, result);
    }

    return result;
  }

  private static void getSubTypes(EPackage ePackage, Map<EClass, List<EClass>> result)
  {
    for (EClassifier classifier : ePackage.getEClassifiers())
    {
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        for (EClass eSuperType : eClass.getEAllSuperTypes())
        {
          if (eSuperType.eIsProxy())
          {
            OM.LOG.warn("getSubTypes encountered a proxy EClass which will be ignored: " + eSuperType);
            continue;
          }

          List<EClass> list = result.get(eSuperType);
          if (list == null)
          {
            list = new ArrayList<EClass>();
            result.put(eSuperType, list);
          }

          list.add(eClass);
        }
      }
    }
  }
}
