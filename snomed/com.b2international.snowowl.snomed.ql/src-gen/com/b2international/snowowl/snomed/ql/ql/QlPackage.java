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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ql.ql.QlFactory
 * @model kind="package"
 * @generated
 */
public interface QlPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "ql";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.b2international.com/snowowl/snomed/QL";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "ql";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  QlPackage eINSTANCE = com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.QueryImpl <em>Query</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QueryImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getQuery()
   * @generated
   */
  int QUERY = 0;

  /**
   * The feature id for the '<em><b>Ecl</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__ECL = 0;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__CONSTRAINT = 1;

  /**
   * The number of structural features of the '<em>Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ConstraintImpl <em>Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ConstraintImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getConstraint()
   * @generated
   */
  int CONSTRAINT = 1;

  /**
   * The number of structural features of the '<em>Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONSTRAINT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.FilterImpl <em>Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.FilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getFilter()
   * @generated
   */
  int FILTER = 3;

  /**
   * The number of structural features of the '<em>Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FILTER_FEATURE_COUNT = CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.NestedFilterImpl <em>Nested Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.NestedFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getNestedFilter()
   * @generated
   */
  int NESTED_FILTER = 2;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTED_FILTER__CONSTRAINT = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Nested Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTED_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl <em>Active Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getActiveFilter()
   * @generated
   */
  int ACTIVE_FILTER = 4;

  /**
   * The feature id for the '<em><b>Domain</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_FILTER__DOMAIN = FILTER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_FILTER__ACTIVE = FILTER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Active Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ModuleFilterImpl <em>Module Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ModuleFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getModuleFilter()
   * @generated
   */
  int MODULE_FILTER = 5;

  /**
   * The feature id for the '<em><b>Domain</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER__DOMAIN = FILTER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Module Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER__MODULE_ID = FILTER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Module Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MODULE_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.TermFilterImpl <em>Term Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.TermFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getTermFilter()
   * @generated
   */
  int TERM_FILTER = 6;

  /**
   * The feature id for the '<em><b>Lexical Search Type</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERM_FILTER__LEXICAL_SEARCH_TYPE = FILTER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERM_FILTER__TERM = FILTER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Term Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERM_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.PreferredInFilterImpl <em>Preferred In Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.PreferredInFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getPreferredInFilter()
   * @generated
   */
  int PREFERRED_IN_FILTER = 7;

  /**
   * The feature id for the '<em><b>Language Ref Set Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Preferred In Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PREFERRED_IN_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.AcceptableInFilterImpl <em>Acceptable In Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.AcceptableInFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getAcceptableInFilter()
   * @generated
   */
  int ACCEPTABLE_IN_FILTER = 8;

  /**
   * The feature id for the '<em><b>Language Ref Set Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCEPTABLE_IN_FILTER__LANGUAGE_REF_SET_ID = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Acceptable In Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACCEPTABLE_IN_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.LanguageRefSetFilterImpl <em>Language Ref Set Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.LanguageRefSetFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getLanguageRefSetFilter()
   * @generated
   */
  int LANGUAGE_REF_SET_FILTER = 9;

  /**
   * The feature id for the '<em><b>Language Ref Set Id</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LANGUAGE_REF_SET_FILTER__LANGUAGE_REF_SET_ID = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Language Ref Set Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LANGUAGE_REF_SET_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.TypeFilterImpl <em>Type Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.TypeFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getTypeFilter()
   * @generated
   */
  int TYPE_FILTER = 10;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE_FILTER__TYPE = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Type Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TYPE_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DisjunctionImpl <em>Disjunction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.DisjunctionImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDisjunction()
   * @generated
   */
  int DISJUNCTION = 11;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DISJUNCTION__LEFT = CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DISJUNCTION__RIGHT = CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Disjunction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DISJUNCTION_FEATURE_COUNT = CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ConjunctionImpl <em>Conjunction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ConjunctionImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getConjunction()
   * @generated
   */
  int CONJUNCTION = 12;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONJUNCTION__LEFT = CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONJUNCTION__RIGHT = CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Conjunction</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONJUNCTION_FEATURE_COUNT = CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ExclusionImpl <em>Exclusion</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ExclusionImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getExclusion()
   * @generated
   */
  int EXCLUSION = 13;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION__LEFT = CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION__RIGHT = CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Exclusion</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_FEATURE_COUNT = CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.LexicalSearchType <em>Lexical Search Type</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.LexicalSearchType
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getLexicalSearchType()
   * @generated
   */
  int LEXICAL_SEARCH_TYPE = 14;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.Domain <em>Domain</em>}' enum.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.Domain
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDomain()
   * @generated
   */
  int DOMAIN = 15;


  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Query <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Query</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Query
   * @generated
   */
  EClass getQuery();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Query#getEcl <em>Ecl</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Ecl</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Query#getEcl()
   * @see #getQuery()
   * @generated
   */
  EReference getQuery_Ecl();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Query#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Query#getConstraint()
   * @see #getQuery()
   * @generated
   */
  EReference getQuery_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Constraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Constraint
   * @generated
   */
  EClass getConstraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.NestedFilter <em>Nested Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nested Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.NestedFilter
   * @generated
   */
  EClass getNestedFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.NestedFilter#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.NestedFilter#getConstraint()
   * @see #getNestedFilter()
   * @generated
   */
  EReference getNestedFilter_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Filter <em>Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Filter
   * @generated
   */
  EClass getFilter();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.ActiveFilter <em>Active Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Active Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveFilter
   * @generated
   */
  EClass getActiveFilter();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.ActiveFilter#getDomain <em>Domain</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Domain</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveFilter#getDomain()
   * @see #getActiveFilter()
   * @generated
   */
  EAttribute getActiveFilter_Domain();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.ActiveFilter#isActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveFilter#isActive()
   * @see #getActiveFilter()
   * @generated
   */
  EAttribute getActiveFilter_Active();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter <em>Module Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Module Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ModuleFilter
   * @generated
   */
  EClass getModuleFilter();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getDomain <em>Domain</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Domain</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getDomain()
   * @see #getModuleFilter()
   * @generated
   */
  EAttribute getModuleFilter_Domain();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getModuleId <em>Module Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Module Id</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ModuleFilter#getModuleId()
   * @see #getModuleFilter()
   * @generated
   */
  EReference getModuleFilter_ModuleId();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.TermFilter <em>Term Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Term Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.TermFilter
   * @generated
   */
  EClass getTermFilter();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.TermFilter#getLexicalSearchType <em>Lexical Search Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Lexical Search Type</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.TermFilter#getLexicalSearchType()
   * @see #getTermFilter()
   * @generated
   */
  EAttribute getTermFilter_LexicalSearchType();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.TermFilter#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.TermFilter#getTerm()
   * @see #getTermFilter()
   * @generated
   */
  EAttribute getTermFilter_Term();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.PreferredInFilter <em>Preferred In Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Preferred In Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.PreferredInFilter
   * @generated
   */
  EClass getPreferredInFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.PreferredInFilter#getLanguageRefSetId <em>Language Ref Set Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Language Ref Set Id</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.PreferredInFilter#getLanguageRefSetId()
   * @see #getPreferredInFilter()
   * @generated
   */
  EReference getPreferredInFilter_LanguageRefSetId();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter <em>Acceptable In Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Acceptable In Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter
   * @generated
   */
  EClass getAcceptableInFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter#getLanguageRefSetId <em>Language Ref Set Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Language Ref Set Id</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.AcceptableInFilter#getLanguageRefSetId()
   * @see #getAcceptableInFilter()
   * @generated
   */
  EReference getAcceptableInFilter_LanguageRefSetId();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter <em>Language Ref Set Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Language Ref Set Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter
   * @generated
   */
  EClass getLanguageRefSetFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter#getLanguageRefSetId <em>Language Ref Set Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Language Ref Set Id</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.LanguageRefSetFilter#getLanguageRefSetId()
   * @see #getLanguageRefSetFilter()
   * @generated
   */
  EReference getLanguageRefSetFilter_LanguageRefSetId();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.TypeFilter <em>Type Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Type Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.TypeFilter
   * @generated
   */
  EClass getTypeFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.TypeFilter#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.TypeFilter#getType()
   * @see #getTypeFilter()
   * @generated
   */
  EReference getTypeFilter_Type();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Disjunction <em>Disjunction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Disjunction</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Disjunction
   * @generated
   */
  EClass getDisjunction();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Disjunction#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Disjunction#getLeft()
   * @see #getDisjunction()
   * @generated
   */
  EReference getDisjunction_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Disjunction#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Disjunction#getRight()
   * @see #getDisjunction()
   * @generated
   */
  EReference getDisjunction_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Conjunction <em>Conjunction</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Conjunction</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Conjunction
   * @generated
   */
  EClass getConjunction();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Conjunction#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Conjunction#getLeft()
   * @see #getConjunction()
   * @generated
   */
  EReference getConjunction_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Conjunction#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Conjunction#getRight()
   * @see #getConjunction()
   * @generated
   */
  EReference getConjunction_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Exclusion <em>Exclusion</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Exclusion</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Exclusion
   * @generated
   */
  EClass getExclusion();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Exclusion#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Exclusion#getLeft()
   * @see #getExclusion()
   * @generated
   */
  EReference getExclusion_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Exclusion#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Exclusion#getRight()
   * @see #getExclusion()
   * @generated
   */
  EReference getExclusion_Right();

  /**
   * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.ql.ql.LexicalSearchType <em>Lexical Search Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Lexical Search Type</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.LexicalSearchType
   * @generated
   */
  EEnum getLexicalSearchType();

  /**
   * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.ql.ql.Domain <em>Domain</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for enum '<em>Domain</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Domain
   * @generated
   */
  EEnum getDomain();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  QlFactory getQlFactory();

  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   * @generated
   */
  interface Literals
  {
    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.QueryImpl <em>Query</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QueryImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getQuery()
     * @generated
     */
    EClass QUERY = eINSTANCE.getQuery();

    /**
     * The meta object literal for the '<em><b>Ecl</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference QUERY__ECL = eINSTANCE.getQuery_Ecl();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference QUERY__CONSTRAINT = eINSTANCE.getQuery_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ConstraintImpl <em>Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.ConstraintImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getConstraint()
     * @generated
     */
    EClass CONSTRAINT = eINSTANCE.getConstraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.NestedFilterImpl <em>Nested Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.NestedFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getNestedFilter()
     * @generated
     */
    EClass NESTED_FILTER = eINSTANCE.getNestedFilter();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NESTED_FILTER__CONSTRAINT = eINSTANCE.getNestedFilter_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.FilterImpl <em>Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.FilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getFilter()
     * @generated
     */
    EClass FILTER = eINSTANCE.getFilter();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl <em>Active Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getActiveFilter()
     * @generated
     */
    EClass ACTIVE_FILTER = eINSTANCE.getActiveFilter();

    /**
     * The meta object literal for the '<em><b>Domain</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIVE_FILTER__DOMAIN = eINSTANCE.getActiveFilter_Domain();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIVE_FILTER__ACTIVE = eINSTANCE.getActiveFilter_Active();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ModuleFilterImpl <em>Module Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.ModuleFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getModuleFilter()
     * @generated
     */
    EClass MODULE_FILTER = eINSTANCE.getModuleFilter();

    /**
     * The meta object literal for the '<em><b>Domain</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute MODULE_FILTER__DOMAIN = eINSTANCE.getModuleFilter_Domain();

    /**
     * The meta object literal for the '<em><b>Module Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MODULE_FILTER__MODULE_ID = eINSTANCE.getModuleFilter_ModuleId();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.TermFilterImpl <em>Term Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.TermFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getTermFilter()
     * @generated
     */
    EClass TERM_FILTER = eINSTANCE.getTermFilter();

    /**
     * The meta object literal for the '<em><b>Lexical Search Type</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TERM_FILTER__LEXICAL_SEARCH_TYPE = eINSTANCE.getTermFilter_LexicalSearchType();

    /**
     * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TERM_FILTER__TERM = eINSTANCE.getTermFilter_Term();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.PreferredInFilterImpl <em>Preferred In Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.PreferredInFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getPreferredInFilter()
     * @generated
     */
    EClass PREFERRED_IN_FILTER = eINSTANCE.getPreferredInFilter();

    /**
     * The meta object literal for the '<em><b>Language Ref Set Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PREFERRED_IN_FILTER__LANGUAGE_REF_SET_ID = eINSTANCE.getPreferredInFilter_LanguageRefSetId();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.AcceptableInFilterImpl <em>Acceptable In Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.AcceptableInFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getAcceptableInFilter()
     * @generated
     */
    EClass ACCEPTABLE_IN_FILTER = eINSTANCE.getAcceptableInFilter();

    /**
     * The meta object literal for the '<em><b>Language Ref Set Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ACCEPTABLE_IN_FILTER__LANGUAGE_REF_SET_ID = eINSTANCE.getAcceptableInFilter_LanguageRefSetId();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.LanguageRefSetFilterImpl <em>Language Ref Set Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.LanguageRefSetFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getLanguageRefSetFilter()
     * @generated
     */
    EClass LANGUAGE_REF_SET_FILTER = eINSTANCE.getLanguageRefSetFilter();

    /**
     * The meta object literal for the '<em><b>Language Ref Set Id</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference LANGUAGE_REF_SET_FILTER__LANGUAGE_REF_SET_ID = eINSTANCE.getLanguageRefSetFilter_LanguageRefSetId();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.TypeFilterImpl <em>Type Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.TypeFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getTypeFilter()
     * @generated
     */
    EClass TYPE_FILTER = eINSTANCE.getTypeFilter();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TYPE_FILTER__TYPE = eINSTANCE.getTypeFilter_Type();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DisjunctionImpl <em>Disjunction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.DisjunctionImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDisjunction()
     * @generated
     */
    EClass DISJUNCTION = eINSTANCE.getDisjunction();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DISJUNCTION__LEFT = eINSTANCE.getDisjunction_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DISJUNCTION__RIGHT = eINSTANCE.getDisjunction_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ConjunctionImpl <em>Conjunction</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.ConjunctionImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getConjunction()
     * @generated
     */
    EClass CONJUNCTION = eINSTANCE.getConjunction();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONJUNCTION__LEFT = eINSTANCE.getConjunction_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONJUNCTION__RIGHT = eINSTANCE.getConjunction_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ExclusionImpl <em>Exclusion</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.ExclusionImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getExclusion()
     * @generated
     */
    EClass EXCLUSION = eINSTANCE.getExclusion();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION__LEFT = eINSTANCE.getExclusion_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION__RIGHT = eINSTANCE.getExclusion_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.LexicalSearchType <em>Lexical Search Type</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.LexicalSearchType
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getLexicalSearchType()
     * @generated
     */
    EEnum LEXICAL_SEARCH_TYPE = eINSTANCE.getLexicalSearchType();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.Domain <em>Domain</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.Domain
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDomain()
     * @generated
     */
    EEnum DOMAIN = eINSTANCE.getDomain();

  }

} //QlPackage
