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
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY__CONSTRAINT = 0;

  /**
   * The number of structural features of the '<em>Query</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int QUERY_FEATURE_COUNT = 1;

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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.EclFilterImpl <em>Ecl Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.EclFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getEclFilter()
   * @generated
   */
  int ECL_FILTER = 4;

  /**
   * The feature id for the '<em><b>Ecl</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECL_FILTER__ECL = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Ecl Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ECL_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl <em>Active Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ActiveFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getActiveFilter()
   * @generated
   */
  int ACTIVE_FILTER = 5;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_FILTER__ACTIVE = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Active Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_FILTER_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DescriptionImpl <em>Description</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.DescriptionImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDescription()
   * @generated
   */
  int DESCRIPTION = 6;

  /**
   * The feature id for the '<em><b>Filter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION__FILTER = FILTER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Description</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION_FEATURE_COUNT = FILTER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DescriptionFilterImpl <em>Description Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.DescriptionFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDescriptionFilter()
   * @generated
   */
  int DESCRIPTION_FILTER = 7;

  /**
   * The feature id for the '<em><b>Term Filter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION_FILTER__TERM_FILTER = 0;

  /**
   * The feature id for the '<em><b>Active</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION_FILTER__ACTIVE = 1;

  /**
   * The feature id for the '<em><b>Type</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION_FILTER__TYPE = 2;

  /**
   * The feature id for the '<em><b>Regex</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION_FILTER__REGEX = 3;

  /**
   * The number of structural features of the '<em>Description Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTION_FILTER_FEATURE_COUNT = 4;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.TermFilterImpl <em>Term Filter</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.TermFilterImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getTermFilter()
   * @generated
   */
  int TERM_FILTER = 8;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERM_FILTER__TERM = 0;

  /**
   * The number of structural features of the '<em>Term Filter</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TERM_FILTER_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.RegularExpressionImpl <em>Regular Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.RegularExpressionImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getRegularExpression()
   * @generated
   */
  int REGULAR_EXPRESSION = 9;

  /**
   * The feature id for the '<em><b>Regex</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REGULAR_EXPRESSION__REGEX = 0;

  /**
   * The number of structural features of the '<em>Regular Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REGULAR_EXPRESSION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DescriptiontypeImpl <em>Descriptiontype</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.DescriptiontypeImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDescriptiontype()
   * @generated
   */
  int DESCRIPTIONTYPE = 10;

  /**
   * The feature id for the '<em><b>Ecl</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTIONTYPE__ECL = 0;

  /**
   * The number of structural features of the '<em>Descriptiontype</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCRIPTIONTYPE_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveTermImpl <em>Active Term</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.ActiveTermImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getActiveTerm()
   * @generated
   */
  int ACTIVE_TERM = 11;

  /**
   * The feature id for the '<em><b>Active</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_TERM__ACTIVE = 0;

  /**
   * The number of structural features of the '<em>Active Term</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ACTIVE_TERM_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DisjunctionImpl <em>Disjunction</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ql.ql.impl.DisjunctionImpl
   * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDisjunction()
   * @generated
   */
  int DISJUNCTION = 12;

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
  int CONJUNCTION = 13;

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
  int EXCLUSION = 14;

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
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Query <em>Query</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Query</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Query
   * @generated
   */
  EClass getQuery();

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
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.EclFilter <em>Ecl Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ecl Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.EclFilter
   * @generated
   */
  EClass getEclFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.EclFilter#getEcl <em>Ecl</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Ecl</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.EclFilter#getEcl()
   * @see #getEclFilter()
   * @generated
   */
  EReference getEclFilter_Ecl();

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
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.ActiveFilter#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveFilter#getActive()
   * @see #getActiveFilter()
   * @generated
   */
  EAttribute getActiveFilter_Active();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Description <em>Description</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Description</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Description
   * @generated
   */
  EClass getDescription();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Description#getFilter <em>Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Description#getFilter()
   * @see #getDescription()
   * @generated
   */
  EReference getDescription_Filter();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter <em>Description Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Description Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.DescriptionFilter
   * @generated
   */
  EClass getDescriptionFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getTermFilter <em>Term Filter</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Term Filter</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getTermFilter()
   * @see #getDescriptionFilter()
   * @generated
   */
  EReference getDescriptionFilter_TermFilter();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Active</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getActive()
   * @see #getDescriptionFilter()
   * @generated
   */
  EReference getDescriptionFilter_Active();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getType <em>Type</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Type</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getType()
   * @see #getDescriptionFilter()
   * @generated
   */
  EReference getDescriptionFilter_Type();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getRegex <em>Regex</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Regex</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.DescriptionFilter#getRegex()
   * @see #getDescriptionFilter()
   * @generated
   */
  EReference getDescriptionFilter_Regex();

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
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.RegularExpression <em>Regular Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Regular Expression</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.RegularExpression
   * @generated
   */
  EClass getRegularExpression();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.RegularExpression#getRegex <em>Regex</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Regex</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.RegularExpression#getRegex()
   * @see #getRegularExpression()
   * @generated
   */
  EAttribute getRegularExpression_Regex();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.Descriptiontype <em>Descriptiontype</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Descriptiontype</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Descriptiontype
   * @generated
   */
  EClass getDescriptiontype();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ql.ql.Descriptiontype#getEcl <em>Ecl</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Ecl</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.Descriptiontype#getEcl()
   * @see #getDescriptiontype()
   * @generated
   */
  EReference getDescriptiontype_Ecl();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ql.ql.ActiveTerm <em>Active Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Active Term</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveTerm
   * @generated
   */
  EClass getActiveTerm();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ql.ql.ActiveTerm#getActive <em>Active</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Active</em>'.
   * @see com.b2international.snowowl.snomed.ql.ql.ActiveTerm#getActive()
   * @see #getActiveTerm()
   * @generated
   */
  EAttribute getActiveTerm_Active();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.EclFilterImpl <em>Ecl Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.EclFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getEclFilter()
     * @generated
     */
    EClass ECL_FILTER = eINSTANCE.getEclFilter();

    /**
     * The meta object literal for the '<em><b>Ecl</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ECL_FILTER__ECL = eINSTANCE.getEclFilter_Ecl();

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
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIVE_FILTER__ACTIVE = eINSTANCE.getActiveFilter_Active();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DescriptionImpl <em>Description</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.DescriptionImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDescription()
     * @generated
     */
    EClass DESCRIPTION = eINSTANCE.getDescription();

    /**
     * The meta object literal for the '<em><b>Filter</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCRIPTION__FILTER = eINSTANCE.getDescription_Filter();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DescriptionFilterImpl <em>Description Filter</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.DescriptionFilterImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDescriptionFilter()
     * @generated
     */
    EClass DESCRIPTION_FILTER = eINSTANCE.getDescriptionFilter();

    /**
     * The meta object literal for the '<em><b>Term Filter</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCRIPTION_FILTER__TERM_FILTER = eINSTANCE.getDescriptionFilter_TermFilter();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCRIPTION_FILTER__ACTIVE = eINSTANCE.getDescriptionFilter_Active();

    /**
     * The meta object literal for the '<em><b>Type</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCRIPTION_FILTER__TYPE = eINSTANCE.getDescriptionFilter_Type();

    /**
     * The meta object literal for the '<em><b>Regex</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCRIPTION_FILTER__REGEX = eINSTANCE.getDescriptionFilter_Regex();

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
     * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TERM_FILTER__TERM = eINSTANCE.getTermFilter_Term();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.RegularExpressionImpl <em>Regular Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.RegularExpressionImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getRegularExpression()
     * @generated
     */
    EClass REGULAR_EXPRESSION = eINSTANCE.getRegularExpression();

    /**
     * The meta object literal for the '<em><b>Regex</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REGULAR_EXPRESSION__REGEX = eINSTANCE.getRegularExpression_Regex();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.DescriptiontypeImpl <em>Descriptiontype</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.DescriptiontypeImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getDescriptiontype()
     * @generated
     */
    EClass DESCRIPTIONTYPE = eINSTANCE.getDescriptiontype();

    /**
     * The meta object literal for the '<em><b>Ecl</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCRIPTIONTYPE__ECL = eINSTANCE.getDescriptiontype_Ecl();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ql.ql.impl.ActiveTermImpl <em>Active Term</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ql.ql.impl.ActiveTermImpl
     * @see com.b2international.snowowl.snomed.ql.ql.impl.QlPackageImpl#getActiveTerm()
     * @generated
     */
    EClass ACTIVE_TERM = eINSTANCE.getActiveTerm();

    /**
     * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ACTIVE_TERM__ACTIVE = eINSTANCE.getActiveTerm_Active();

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

  }

} //QlPackage
