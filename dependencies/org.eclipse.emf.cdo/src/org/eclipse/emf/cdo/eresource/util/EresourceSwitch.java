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
package org.eclipse.emf.cdo.eresource.util;

import org.eclipse.emf.cdo.eresource.CDOBinaryResource;
import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.eresource.CDOTextResource;
import org.eclipse.emf.cdo.eresource.EresourcePackage;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

import java.util.List;

/**
 * <!-- begin-user-doc --> The <b>Switch</b> for the model's inheritance hierarchy. It supports the call
 * {@link #doSwitch(EObject) doSwitch(object)} to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object and proceeding up the inheritance hierarchy until a non-null result is
 * returned, which is the result of the switch. <!-- end-user-doc -->
 * 
 * @see org.eclipse.emf.cdo.eresource.EresourcePackage
 * @generated
 */
public class EresourceSwitch<T>
{
  /**
   * The cached model package <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  protected static EresourcePackage modelPackage;

  /**
   * Creates an instance of the switch. <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @generated
   */
  public EresourceSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = EresourcePackage.eINSTANCE;
    }
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  public T doSwitch(EObject theEObject)
  {
    return doSwitch(theEObject.eClass(), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(EClass theEClass, EObject theEObject)
  {
    if (theEClass.eContainer() == modelPackage)
    {
      return doSwitch(theEClass.getClassifierID(), theEObject);
    }
    List<EClass> eSuperTypes = theEClass.getESuperTypes();
    return eSuperTypes.isEmpty() ? defaultCase(theEObject) : doSwitch(eSuperTypes.get(0), theEObject);
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc --> <!-- end-user-doc -->
   * 
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
    case EresourcePackage.CDO_RESOURCE_NODE:
    {
      CDOResourceNode cdoResourceNode = (CDOResourceNode)theEObject;
      T result = caseCDOResourceNode(cdoResourceNode);
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EresourcePackage.CDO_RESOURCE_FOLDER:
    {
      CDOResourceFolder cdoResourceFolder = (CDOResourceFolder)theEObject;
      T result = caseCDOResourceFolder(cdoResourceFolder);
      if (result == null)
      {
        result = caseCDOResourceNode(cdoResourceFolder);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EresourcePackage.CDO_RESOURCE:
    {
      CDOResource cdoResource = (CDOResource)theEObject;
      T result = caseCDOResource(cdoResource);
      if (result == null)
      {
        result = caseCDOResourceLeaf(cdoResource);
      }
      if (result == null)
      {
        result = caseCDOResourceNode(cdoResource);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EresourcePackage.CDO_RESOURCE_LEAF:
    {
      CDOResourceLeaf cdoResourceLeaf = (CDOResourceLeaf)theEObject;
      T result = caseCDOResourceLeaf(cdoResourceLeaf);
      if (result == null)
      {
        result = caseCDOResourceNode(cdoResourceLeaf);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EresourcePackage.CDO_FILE_RESOURCE:
    {
      CDOFileResource<?> cdoFileResource = (CDOFileResource<?>)theEObject;
      T result = caseCDOFileResource(cdoFileResource);
      if (result == null)
      {
        result = caseCDOResourceLeaf(cdoFileResource);
      }
      if (result == null)
      {
        result = caseCDOResourceNode(cdoFileResource);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EresourcePackage.CDO_BINARY_RESOURCE:
    {
      CDOBinaryResource cdoBinaryResource = (CDOBinaryResource)theEObject;
      T result = caseCDOBinaryResource(cdoBinaryResource);
      if (result == null)
      {
        result = caseCDOFileResource(cdoBinaryResource);
      }
      if (result == null)
      {
        result = caseCDOResourceLeaf(cdoBinaryResource);
      }
      if (result == null)
      {
        result = caseCDOResourceNode(cdoBinaryResource);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    case EresourcePackage.CDO_TEXT_RESOURCE:
    {
      CDOTextResource cdoTextResource = (CDOTextResource)theEObject;
      T result = caseCDOTextResource(cdoTextResource);
      if (result == null)
      {
        result = caseCDOFileResource(cdoTextResource);
      }
      if (result == null)
      {
        result = caseCDOResourceLeaf(cdoTextResource);
      }
      if (result == null)
      {
        result = caseCDOResourceNode(cdoTextResource);
      }
      if (result == null)
      {
        result = defaultCase(theEObject);
      }
      return result;
    }
    default:
      return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO Resource Node</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch.
   * 
   * @since 2.0<!-- end-user-doc -->
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO Resource Node</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOResourceNode(CDOResourceNode object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO Resource Folder</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch.
   * 
   * @since 2.0 <!-- end-user-doc -->
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO Resource Folder</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOResourceFolder(CDOResourceFolder object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO Resource</em>'. <!-- begin-user-doc -->
   * This implementation returns null; returning a non-null result will terminate the switch. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO Resource</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOResource(CDOResource object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO Resource Leaf</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO Resource Leaf</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOResourceLeaf(CDOResourceLeaf object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO File Resource</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO File Resource</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOFileResource(CDOFileResource<?> object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO Binary Resource</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO Binary Resource</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOBinaryResource(CDOBinaryResource object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>CDO Text Resource</em>'. <!-- begin-user-doc
   * --> This implementation returns null; returning a non-null result will terminate the switch.
   * 
   * @since 4.1 <!-- end-user-doc -->
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>CDO Text Resource</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCDOTextResource(CDOTextResource object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'. <!-- begin-user-doc --> This
   * implementation returns null; returning a non-null result will terminate the switch, but this is the last case
   * anyway. <!-- end-user-doc -->
   * 
   * @param object
   *          the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  public T defaultCase(EObject object)
  {
    return null;
  }

} // EresourceSwitch
