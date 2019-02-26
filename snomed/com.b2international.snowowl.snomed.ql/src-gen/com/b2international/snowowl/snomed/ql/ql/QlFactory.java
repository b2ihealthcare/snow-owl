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
package com.b2international.snowowl.snomed.ql.ql;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ql.ql.QlPackage
 * @generated
 */
public interface QlFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  QlFactory eINSTANCE = com.b2international.snowowl.snomed.ql.ql.impl.QlFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Query</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query</em>'.
   * @generated
   */
  Query createQuery();

  /**
   * Returns a new object of class '<em>Query Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query Constraint</em>'.
   * @generated
   */
  QueryConstraint createQueryConstraint();

  /**
   * Returns a new object of class '<em>Sub Query</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Sub Query</em>'.
   * @generated
   */
  SubQuery createSubQuery();

  /**
   * Returns a new object of class '<em>Domain Query</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Domain Query</em>'.
   * @generated
   */
  DomainQuery createDomainQuery();

  /**
   * Returns a new object of class '<em>Nested Query</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nested Query</em>'.
   * @generated
   */
  NestedQuery createNestedQuery();

  /**
   * Returns a new object of class '<em>Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Filter</em>'.
   * @generated
   */
  Filter createFilter();

  /**
   * Returns a new object of class '<em>Nested Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nested Filter</em>'.
   * @generated
   */
  NestedFilter createNestedFilter();

  /**
   * Returns a new object of class '<em>Property Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Property Filter</em>'.
   * @generated
   */
  PropertyFilter createPropertyFilter();

  /**
   * Returns a new object of class '<em>Active Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Active Filter</em>'.
   * @generated
   */
  ActiveFilter createActiveFilter();

  /**
   * Returns a new object of class '<em>Module Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Module Filter</em>'.
   * @generated
   */
  ModuleFilter createModuleFilter();

  /**
   * Returns a new object of class '<em>Term Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Term Filter</em>'.
   * @generated
   */
  TermFilter createTermFilter();

  /**
   * Returns a new object of class '<em>Preferred In Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Preferred In Filter</em>'.
   * @generated
   */
  PreferredInFilter createPreferredInFilter();

  /**
   * Returns a new object of class '<em>Acceptable In Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Acceptable In Filter</em>'.
   * @generated
   */
  AcceptableInFilter createAcceptableInFilter();

  /**
   * Returns a new object of class '<em>Language Ref Set Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Language Ref Set Filter</em>'.
   * @generated
   */
  LanguageRefSetFilter createLanguageRefSetFilter();

  /**
   * Returns a new object of class '<em>Type Filter</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Type Filter</em>'.
   * @generated
   */
  TypeFilter createTypeFilter();

  /**
   * Returns a new object of class '<em>Query Disjunction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query Disjunction</em>'.
   * @generated
   */
  QueryDisjunction createQueryDisjunction();

  /**
   * Returns a new object of class '<em>Query Conjunction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query Conjunction</em>'.
   * @generated
   */
  QueryConjunction createQueryConjunction();

  /**
   * Returns a new object of class '<em>Query Exclusion</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Query Exclusion</em>'.
   * @generated
   */
  QueryExclusion createQueryExclusion();

  /**
   * Returns a new object of class '<em>Disjunction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Disjunction</em>'.
   * @generated
   */
  Disjunction createDisjunction();

  /**
   * Returns a new object of class '<em>Conjunction</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Conjunction</em>'.
   * @generated
   */
  Conjunction createConjunction();

  /**
   * Returns a new object of class '<em>Exclusion</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Exclusion</em>'.
   * @generated
   */
  Exclusion createExclusion();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  QlPackage getQlPackage();

} //QlFactory
