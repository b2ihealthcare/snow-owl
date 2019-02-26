/**
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.ql.ql.util;

import com.b2international.snowowl.snomed.ql.ql.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage
 * @generated
 */
public class QlSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static QlPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public QlSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = QlPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case QlPackage.QUERY:
      {
        Query query = (Query)theEObject;
        T result = caseQuery(query);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.QUERY_CONSTRAINT:
      {
        QueryConstraint queryConstraint = (QueryConstraint)theEObject;
        T result = caseQueryConstraint(queryConstraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.SUB_QUERY:
      {
        SubQuery subQuery = (SubQuery)theEObject;
        T result = caseSubQuery(subQuery);
        if (result == null) result = caseQueryConstraint(subQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.DOMAIN_QUERY:
      {
        DomainQuery domainQuery = (DomainQuery)theEObject;
        T result = caseDomainQuery(domainQuery);
        if (result == null) result = caseSubQuery(domainQuery);
        if (result == null) result = caseQueryConstraint(domainQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.NESTED_QUERY:
      {
        NestedQuery nestedQuery = (NestedQuery)theEObject;
        T result = caseNestedQuery(nestedQuery);
        if (result == null) result = caseSubQuery(nestedQuery);
        if (result == null) result = caseQueryConstraint(nestedQuery);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.FILTER:
      {
        Filter filter = (Filter)theEObject;
        T result = caseFilter(filter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.NESTED_FILTER:
      {
        NestedFilter nestedFilter = (NestedFilter)theEObject;
        T result = caseNestedFilter(nestedFilter);
        if (result == null) result = casePropertyFilter(nestedFilter);
        if (result == null) result = caseFilter(nestedFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.PROPERTY_FILTER:
      {
        PropertyFilter propertyFilter = (PropertyFilter)theEObject;
        T result = casePropertyFilter(propertyFilter);
        if (result == null) result = caseFilter(propertyFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.ACTIVE_FILTER:
      {
        ActiveFilter activeFilter = (ActiveFilter)theEObject;
        T result = caseActiveFilter(activeFilter);
        if (result == null) result = casePropertyFilter(activeFilter);
        if (result == null) result = caseFilter(activeFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.MODULE_FILTER:
      {
        ModuleFilter moduleFilter = (ModuleFilter)theEObject;
        T result = caseModuleFilter(moduleFilter);
        if (result == null) result = casePropertyFilter(moduleFilter);
        if (result == null) result = caseFilter(moduleFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.TERM_FILTER:
      {
        TermFilter termFilter = (TermFilter)theEObject;
        T result = caseTermFilter(termFilter);
        if (result == null) result = casePropertyFilter(termFilter);
        if (result == null) result = caseFilter(termFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.PREFERRED_IN_FILTER:
      {
        PreferredInFilter preferredInFilter = (PreferredInFilter)theEObject;
        T result = casePreferredInFilter(preferredInFilter);
        if (result == null) result = casePropertyFilter(preferredInFilter);
        if (result == null) result = caseFilter(preferredInFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.ACCEPTABLE_IN_FILTER:
      {
        AcceptableInFilter acceptableInFilter = (AcceptableInFilter)theEObject;
        T result = caseAcceptableInFilter(acceptableInFilter);
        if (result == null) result = casePropertyFilter(acceptableInFilter);
        if (result == null) result = caseFilter(acceptableInFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.LANGUAGE_REF_SET_FILTER:
      {
        LanguageRefSetFilter languageRefSetFilter = (LanguageRefSetFilter)theEObject;
        T result = caseLanguageRefSetFilter(languageRefSetFilter);
        if (result == null) result = casePropertyFilter(languageRefSetFilter);
        if (result == null) result = caseFilter(languageRefSetFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.TYPE_FILTER:
      {
        TypeFilter typeFilter = (TypeFilter)theEObject;
        T result = caseTypeFilter(typeFilter);
        if (result == null) result = casePropertyFilter(typeFilter);
        if (result == null) result = caseFilter(typeFilter);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.QUERY_DISJUNCTION:
      {
        QueryDisjunction queryDisjunction = (QueryDisjunction)theEObject;
        T result = caseQueryDisjunction(queryDisjunction);
        if (result == null) result = caseQueryConstraint(queryDisjunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.QUERY_CONJUNCTION:
      {
        QueryConjunction queryConjunction = (QueryConjunction)theEObject;
        T result = caseQueryConjunction(queryConjunction);
        if (result == null) result = caseQueryConstraint(queryConjunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.QUERY_EXCLUSION:
      {
        QueryExclusion queryExclusion = (QueryExclusion)theEObject;
        T result = caseQueryExclusion(queryExclusion);
        if (result == null) result = caseQueryConstraint(queryExclusion);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.DISJUNCTION:
      {
        Disjunction disjunction = (Disjunction)theEObject;
        T result = caseDisjunction(disjunction);
        if (result == null) result = caseFilter(disjunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.CONJUNCTION:
      {
        Conjunction conjunction = (Conjunction)theEObject;
        T result = caseConjunction(conjunction);
        if (result == null) result = caseFilter(conjunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case QlPackage.EXCLUSION:
      {
        Exclusion exclusion = (Exclusion)theEObject;
        T result = caseExclusion(exclusion);
        if (result == null) result = caseFilter(exclusion);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQuery(Query object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQueryConstraint(QueryConstraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sub Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sub Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSubQuery(SubQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Domain Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Domain Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDomainQuery(DomainQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Nested Query</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Nested Query</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNestedQuery(NestedQuery object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFilter(Filter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Nested Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Nested Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseNestedFilter(NestedFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Property Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Property Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePropertyFilter(PropertyFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Active Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Active Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseActiveFilter(ActiveFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Module Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Module Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModuleFilter(ModuleFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Term Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Term Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTermFilter(TermFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Preferred In Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Preferred In Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePreferredInFilter(PreferredInFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Acceptable In Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Acceptable In Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAcceptableInFilter(AcceptableInFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Language Ref Set Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Language Ref Set Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLanguageRefSetFilter(LanguageRefSetFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Type Filter</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Type Filter</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTypeFilter(TypeFilter object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query Disjunction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query Disjunction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQueryDisjunction(QueryDisjunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query Conjunction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query Conjunction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQueryConjunction(QueryConjunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Query Exclusion</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Query Exclusion</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseQueryExclusion(QueryExclusion object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Disjunction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Disjunction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDisjunction(Disjunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Conjunction</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Conjunction</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConjunction(Conjunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Exclusion</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Exclusion</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExclusion(Exclusion object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //QlSwitch
