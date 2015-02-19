/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.dsl.escg;

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
 * @see com.b2international.snowowl.dsl.escg.EscgFactory
 * @model kind="package"
 * @generated
 */
public interface EscgPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "escg";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.b2international.com/snowowl/dsl/ESCG";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "escg";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EscgPackage eINSTANCE = com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.ExpressionImpl <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.ExpressionImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 0;

  /**
   * The feature id for the '<em><b>Sub Expression</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__SUB_EXPRESSION = 0;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.SubExpressionImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getSubExpression()
   * @generated
   */
  int SUB_EXPRESSION = 1;

  /**
   * The feature id for the '<em><b>LValues</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_EXPRESSION__LVALUES = 0;

  /**
   * The feature id for the '<em><b>Refinements</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_EXPRESSION__REFINEMENTS = 1;

  /**
   * The number of structural features of the '<em>Sub Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_EXPRESSION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.RValueImpl <em>RValue</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.RValueImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getRValue()
   * @generated
   */
  int RVALUE = 14;

  /**
   * The number of structural features of the '<em>RValue</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int RVALUE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.LValueImpl <em>LValue</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.LValueImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getLValue()
   * @generated
   */
  int LVALUE = 2;

  /**
   * The feature id for the '<em><b>Negated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LVALUE__NEGATED = RVALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>LValue</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int LVALUE_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.RefSetImpl <em>Ref Set</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.RefSetImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getRefSet()
   * @generated
   */
  int REF_SET = 3;

  /**
   * The feature id for the '<em><b>Negated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REF_SET__NEGATED = LVALUE__NEGATED;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REF_SET__ID = LVALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REF_SET__TERM = LVALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Ref Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REF_SET_FEATURE_COUNT = LVALUE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.ConceptGroupImpl <em>Concept Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.ConceptGroupImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getConceptGroup()
   * @generated
   */
  int CONCEPT_GROUP = 4;

  /**
   * The feature id for the '<em><b>Negated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_GROUP__NEGATED = LVALUE__NEGATED;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_GROUP__CONSTRAINT = LVALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_GROUP__CONCEPT = LVALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Concept Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_GROUP_FEATURE_COUNT = LVALUE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.ConceptImpl <em>Concept</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.ConceptImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getConcept()
   * @generated
   */
  int CONCEPT = 5;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT__ID = 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT__TERM = 1;

  /**
   * The number of structural features of the '<em>Concept</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.RefinementsImpl <em>Refinements</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.RefinementsImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getRefinements()
   * @generated
   */
  int REFINEMENTS = 6;

  /**
   * The feature id for the '<em><b>Attribute Set</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENTS__ATTRIBUTE_SET = 0;

  /**
   * The feature id for the '<em><b>Attribute Groups</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENTS__ATTRIBUTE_GROUPS = 1;

  /**
   * The number of structural features of the '<em>Refinements</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENTS_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.AttributeGroupImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttributeGroup()
   * @generated
   */
  int ATTRIBUTE_GROUP = 7;

  /**
   * The number of structural features of the '<em>Attribute Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeSetImpl <em>Attribute Set</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.AttributeSetImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttributeSet()
   * @generated
   */
  int ATTRIBUTE_SET = 8;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_SET__ATTRIBUTES = ATTRIBUTE_GROUP_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Attribute Set</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_SET_FEATURE_COUNT = ATTRIBUTE_GROUP_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.AttributeImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 9;

  /**
   * The feature id for the '<em><b>Optional</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__OPTIONAL = 0;

  /**
   * The feature id for the '<em><b>Assignment</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__ASSIGNMENT = 1;

  /**
   * The number of structural features of the '<em>Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeAssignmentImpl <em>Attribute Assignment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.AttributeAssignmentImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttributeAssignment()
   * @generated
   */
  int ATTRIBUTE_ASSIGNMENT = 10;

  /**
   * The number of structural features of the '<em>Attribute Assignment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.ConceptAssignmentImpl <em>Concept Assignment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.ConceptAssignmentImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getConceptAssignment()
   * @generated
   */
  int CONCEPT_ASSIGNMENT = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_ASSIGNMENT__NAME = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_ASSIGNMENT__VALUE = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Concept Assignment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_ASSIGNMENT_FEATURE_COUNT = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentImpl <em>Numerical Assignment</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getNumericalAssignment()
   * @generated
   */
  int NUMERICAL_ASSIGNMENT = 12;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT__NAME = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Operator</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT__OPERATOR = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT__VALUE = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Unit</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT__UNIT = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Numerical Assignment</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT_FEATURE_COUNT = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl <em>Numerical Assignment Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getNumericalAssignmentGroup()
   * @generated
   */
  int NUMERICAL_ASSIGNMENT_GROUP = 13;

  /**
   * The feature id for the '<em><b>Ingredient Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Substance</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Numeric Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Numerical Assignment Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NUMERICAL_ASSIGNMENT_GROUP_FEATURE_COUNT = ATTRIBUTE_ASSIGNMENT_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.NegatableSubExpressionImpl <em>Negatable Sub Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.NegatableSubExpressionImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getNegatableSubExpression()
   * @generated
   */
  int NEGATABLE_SUB_EXPRESSION = 15;

  /**
   * The feature id for the '<em><b>Negated</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NEGATABLE_SUB_EXPRESSION__NEGATED = RVALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NEGATABLE_SUB_EXPRESSION__EXPRESSION = RVALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Negatable Sub Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NEGATABLE_SUB_EXPRESSION_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.OrImpl <em>Or</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.OrImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getOr()
   * @generated
   */
  int OR = 16;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__LEFT = RVALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR__RIGHT = RVALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Or</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.escg.impl.AndImpl <em>And</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.escg.impl.AndImpl
   * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAnd()
   * @generated
   */
  int AND = 17;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__LEFT = RVALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND__RIGHT = RVALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>And</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 2;


  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression</em>'.
   * @see com.b2international.snowowl.dsl.escg.Expression
   * @generated
   */
  EClass getExpression();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.escg.Expression#getSubExpression <em>Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Sub Expression</em>'.
   * @see com.b2international.snowowl.dsl.escg.Expression#getSubExpression()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_SubExpression();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.SubExpression <em>Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Sub Expression</em>'.
   * @see com.b2international.snowowl.dsl.escg.SubExpression
   * @generated
   */
  EClass getSubExpression();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.escg.SubExpression#getLValues <em>LValues</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>LValues</em>'.
   * @see com.b2international.snowowl.dsl.escg.SubExpression#getLValues()
   * @see #getSubExpression()
   * @generated
   */
  EReference getSubExpression_LValues();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.SubExpression#getRefinements <em>Refinements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Refinements</em>'.
   * @see com.b2international.snowowl.dsl.escg.SubExpression#getRefinements()
   * @see #getSubExpression()
   * @generated
   */
  EReference getSubExpression_Refinements();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.LValue <em>LValue</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>LValue</em>'.
   * @see com.b2international.snowowl.dsl.escg.LValue
   * @generated
   */
  EClass getLValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.LValue#isNegated <em>Negated</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Negated</em>'.
   * @see com.b2international.snowowl.dsl.escg.LValue#isNegated()
   * @see #getLValue()
   * @generated
   */
  EAttribute getLValue_Negated();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.RefSet <em>Ref Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ref Set</em>'.
   * @see com.b2international.snowowl.dsl.escg.RefSet
   * @generated
   */
  EClass getRefSet();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.RefSet#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.dsl.escg.RefSet#getId()
   * @see #getRefSet()
   * @generated
   */
  EAttribute getRefSet_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.RefSet#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.dsl.escg.RefSet#getTerm()
   * @see #getRefSet()
   * @generated
   */
  EAttribute getRefSet_Term();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.ConceptGroup <em>Concept Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Group</em>'.
   * @see com.b2international.snowowl.dsl.escg.ConceptGroup
   * @generated
   */
  EClass getConceptGroup();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.ConceptGroup#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Constraint</em>'.
   * @see com.b2international.snowowl.dsl.escg.ConceptGroup#getConstraint()
   * @see #getConceptGroup()
   * @generated
   */
  EAttribute getConceptGroup_Constraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.ConceptGroup#getConcept <em>Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Concept</em>'.
   * @see com.b2international.snowowl.dsl.escg.ConceptGroup#getConcept()
   * @see #getConceptGroup()
   * @generated
   */
  EReference getConceptGroup_Concept();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.Concept <em>Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept</em>'.
   * @see com.b2international.snowowl.dsl.escg.Concept
   * @generated
   */
  EClass getConcept();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.Concept#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.dsl.escg.Concept#getId()
   * @see #getConcept()
   * @generated
   */
  EAttribute getConcept_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.Concept#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.dsl.escg.Concept#getTerm()
   * @see #getConcept()
   * @generated
   */
  EAttribute getConcept_Term();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.Refinements <em>Refinements</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Refinements</em>'.
   * @see com.b2international.snowowl.dsl.escg.Refinements
   * @generated
   */
  EClass getRefinements();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.Refinements#getAttributeSet <em>Attribute Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Attribute Set</em>'.
   * @see com.b2international.snowowl.dsl.escg.Refinements#getAttributeSet()
   * @see #getRefinements()
   * @generated
   */
  EReference getRefinements_AttributeSet();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.escg.Refinements#getAttributeGroups <em>Attribute Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attribute Groups</em>'.
   * @see com.b2international.snowowl.dsl.escg.Refinements#getAttributeGroups()
   * @see #getRefinements()
   * @generated
   */
  EReference getRefinements_AttributeGroups();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Group</em>'.
   * @see com.b2international.snowowl.dsl.escg.AttributeGroup
   * @generated
   */
  EClass getAttributeGroup();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.AttributeSet <em>Attribute Set</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Set</em>'.
   * @see com.b2international.snowowl.dsl.escg.AttributeSet
   * @generated
   */
  EClass getAttributeSet();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.escg.AttributeSet#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.dsl.escg.AttributeSet#getAttributes()
   * @see #getAttributeSet()
   * @generated
   */
  EReference getAttributeSet_Attributes();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute</em>'.
   * @see com.b2international.snowowl.dsl.escg.Attribute
   * @generated
   */
  EClass getAttribute();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.Attribute#isOptional <em>Optional</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Optional</em>'.
   * @see com.b2international.snowowl.dsl.escg.Attribute#isOptional()
   * @see #getAttribute()
   * @generated
   */
  EAttribute getAttribute_Optional();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.Attribute#getAssignment <em>Assignment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Assignment</em>'.
   * @see com.b2international.snowowl.dsl.escg.Attribute#getAssignment()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Assignment();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.AttributeAssignment <em>Attribute Assignment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Assignment</em>'.
   * @see com.b2international.snowowl.dsl.escg.AttributeAssignment
   * @generated
   */
  EClass getAttributeAssignment();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.ConceptAssignment <em>Concept Assignment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Assignment</em>'.
   * @see com.b2international.snowowl.dsl.escg.ConceptAssignment
   * @generated
   */
  EClass getConceptAssignment();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.ConceptAssignment#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see com.b2international.snowowl.dsl.escg.ConceptAssignment#getName()
   * @see #getConceptAssignment()
   * @generated
   */
  EReference getConceptAssignment_Name();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.ConceptAssignment#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see com.b2international.snowowl.dsl.escg.ConceptAssignment#getValue()
   * @see #getConceptAssignment()
   * @generated
   */
  EReference getConceptAssignment_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.NumericalAssignment <em>Numerical Assignment</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Numerical Assignment</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignment
   * @generated
   */
  EClass getNumericalAssignment();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.NumericalAssignment#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignment#getName()
   * @see #getNumericalAssignment()
   * @generated
   */
  EReference getNumericalAssignment_Name();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.NumericalAssignment#getOperator <em>Operator</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Operator</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignment#getOperator()
   * @see #getNumericalAssignment()
   * @generated
   */
  EAttribute getNumericalAssignment_Operator();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.NumericalAssignment#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignment#getValue()
   * @see #getNumericalAssignment()
   * @generated
   */
  EAttribute getNumericalAssignment_Value();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.NumericalAssignment#getUnit <em>Unit</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Unit</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignment#getUnit()
   * @see #getNumericalAssignment()
   * @generated
   */
  EAttribute getNumericalAssignment_Unit();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup <em>Numerical Assignment Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Numerical Assignment Group</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup
   * @generated
   */
  EClass getNumericalAssignmentGroup();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getIngredientConcept <em>Ingredient Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Ingredient Concept</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getIngredientConcept()
   * @see #getNumericalAssignmentGroup()
   * @generated
   */
  EReference getNumericalAssignmentGroup_IngredientConcept();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getSubstance <em>Substance</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Substance</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getSubstance()
   * @see #getNumericalAssignmentGroup()
   * @generated
   */
  EReference getNumericalAssignmentGroup_Substance();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getNumericValue <em>Numeric Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Numeric Value</em>'.
   * @see com.b2international.snowowl.dsl.escg.NumericalAssignmentGroup#getNumericValue()
   * @see #getNumericalAssignmentGroup()
   * @generated
   */
  EReference getNumericalAssignmentGroup_NumericValue();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.RValue <em>RValue</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>RValue</em>'.
   * @see com.b2international.snowowl.dsl.escg.RValue
   * @generated
   */
  EClass getRValue();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.NegatableSubExpression <em>Negatable Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Negatable Sub Expression</em>'.
   * @see com.b2international.snowowl.dsl.escg.NegatableSubExpression
   * @generated
   */
  EClass getNegatableSubExpression();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.escg.NegatableSubExpression#isNegated <em>Negated</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Negated</em>'.
   * @see com.b2international.snowowl.dsl.escg.NegatableSubExpression#isNegated()
   * @see #getNegatableSubExpression()
   * @generated
   */
  EAttribute getNegatableSubExpression_Negated();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.NegatableSubExpression#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see com.b2international.snowowl.dsl.escg.NegatableSubExpression#getExpression()
   * @see #getNegatableSubExpression()
   * @generated
   */
  EReference getNegatableSubExpression_Expression();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.Or <em>Or</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or</em>'.
   * @see com.b2international.snowowl.dsl.escg.Or
   * @generated
   */
  EClass getOr();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.Or#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.dsl.escg.Or#getLeft()
   * @see #getOr()
   * @generated
   */
  EReference getOr_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.Or#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.dsl.escg.Or#getRight()
   * @see #getOr()
   * @generated
   */
  EReference getOr_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.escg.And <em>And</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And</em>'.
   * @see com.b2international.snowowl.dsl.escg.And
   * @generated
   */
  EClass getAnd();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.And#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.dsl.escg.And#getLeft()
   * @see #getAnd()
   * @generated
   */
  EReference getAnd_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.escg.And#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.dsl.escg.And#getRight()
   * @see #getAnd()
   * @generated
   */
  EReference getAnd_Right();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EscgFactory getEscgFactory();

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
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.ExpressionImpl <em>Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.ExpressionImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getExpression()
     * @generated
     */
    EClass EXPRESSION = eINSTANCE.getExpression();

    /**
     * The meta object literal for the '<em><b>Sub Expression</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__SUB_EXPRESSION = eINSTANCE.getExpression_SubExpression();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.SubExpressionImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getSubExpression()
     * @generated
     */
    EClass SUB_EXPRESSION = eINSTANCE.getSubExpression();

    /**
     * The meta object literal for the '<em><b>LValues</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUB_EXPRESSION__LVALUES = eINSTANCE.getSubExpression_LValues();

    /**
     * The meta object literal for the '<em><b>Refinements</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUB_EXPRESSION__REFINEMENTS = eINSTANCE.getSubExpression_Refinements();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.LValueImpl <em>LValue</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.LValueImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getLValue()
     * @generated
     */
    EClass LVALUE = eINSTANCE.getLValue();

    /**
     * The meta object literal for the '<em><b>Negated</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute LVALUE__NEGATED = eINSTANCE.getLValue_Negated();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.RefSetImpl <em>Ref Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.RefSetImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getRefSet()
     * @generated
     */
    EClass REF_SET = eINSTANCE.getRefSet();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REF_SET__ID = eINSTANCE.getRefSet_Id();

    /**
     * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute REF_SET__TERM = eINSTANCE.getRefSet_Term();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.ConceptGroupImpl <em>Concept Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.ConceptGroupImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getConceptGroup()
     * @generated
     */
    EClass CONCEPT_GROUP = eINSTANCE.getConceptGroup();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT_GROUP__CONSTRAINT = eINSTANCE.getConceptGroup_Constraint();

    /**
     * The meta object literal for the '<em><b>Concept</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONCEPT_GROUP__CONCEPT = eINSTANCE.getConceptGroup_Concept();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.ConceptImpl <em>Concept</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.ConceptImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getConcept()
     * @generated
     */
    EClass CONCEPT = eINSTANCE.getConcept();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT__ID = eINSTANCE.getConcept_Id();

    /**
     * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT__TERM = eINSTANCE.getConcept_Term();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.RefinementsImpl <em>Refinements</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.RefinementsImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getRefinements()
     * @generated
     */
    EClass REFINEMENTS = eINSTANCE.getRefinements();

    /**
     * The meta object literal for the '<em><b>Attribute Set</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFINEMENTS__ATTRIBUTE_SET = eINSTANCE.getRefinements_AttributeSet();

    /**
     * The meta object literal for the '<em><b>Attribute Groups</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFINEMENTS__ATTRIBUTE_GROUPS = eINSTANCE.getRefinements_AttributeGroups();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.AttributeGroupImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttributeGroup()
     * @generated
     */
    EClass ATTRIBUTE_GROUP = eINSTANCE.getAttributeGroup();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeSetImpl <em>Attribute Set</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.AttributeSetImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttributeSet()
     * @generated
     */
    EClass ATTRIBUTE_SET = eINSTANCE.getAttributeSet();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_SET__ATTRIBUTES = eINSTANCE.getAttributeSet_Attributes();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.AttributeImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttribute()
     * @generated
     */
    EClass ATTRIBUTE = eINSTANCE.getAttribute();

    /**
     * The meta object literal for the '<em><b>Optional</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE__OPTIONAL = eINSTANCE.getAttribute_Optional();

    /**
     * The meta object literal for the '<em><b>Assignment</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__ASSIGNMENT = eINSTANCE.getAttribute_Assignment();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.AttributeAssignmentImpl <em>Attribute Assignment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.AttributeAssignmentImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAttributeAssignment()
     * @generated
     */
    EClass ATTRIBUTE_ASSIGNMENT = eINSTANCE.getAttributeAssignment();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.ConceptAssignmentImpl <em>Concept Assignment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.ConceptAssignmentImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getConceptAssignment()
     * @generated
     */
    EClass CONCEPT_ASSIGNMENT = eINSTANCE.getConceptAssignment();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONCEPT_ASSIGNMENT__NAME = eINSTANCE.getConceptAssignment_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONCEPT_ASSIGNMENT__VALUE = eINSTANCE.getConceptAssignment_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentImpl <em>Numerical Assignment</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getNumericalAssignment()
     * @generated
     */
    EClass NUMERICAL_ASSIGNMENT = eINSTANCE.getNumericalAssignment();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NUMERICAL_ASSIGNMENT__NAME = eINSTANCE.getNumericalAssignment_Name();

    /**
     * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NUMERICAL_ASSIGNMENT__OPERATOR = eINSTANCE.getNumericalAssignment_Operator();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NUMERICAL_ASSIGNMENT__VALUE = eINSTANCE.getNumericalAssignment_Value();

    /**
     * The meta object literal for the '<em><b>Unit</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NUMERICAL_ASSIGNMENT__UNIT = eINSTANCE.getNumericalAssignment_Unit();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl <em>Numerical Assignment Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.NumericalAssignmentGroupImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getNumericalAssignmentGroup()
     * @generated
     */
    EClass NUMERICAL_ASSIGNMENT_GROUP = eINSTANCE.getNumericalAssignmentGroup();

    /**
     * The meta object literal for the '<em><b>Ingredient Concept</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NUMERICAL_ASSIGNMENT_GROUP__INGREDIENT_CONCEPT = eINSTANCE.getNumericalAssignmentGroup_IngredientConcept();

    /**
     * The meta object literal for the '<em><b>Substance</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NUMERICAL_ASSIGNMENT_GROUP__SUBSTANCE = eINSTANCE.getNumericalAssignmentGroup_Substance();

    /**
     * The meta object literal for the '<em><b>Numeric Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NUMERICAL_ASSIGNMENT_GROUP__NUMERIC_VALUE = eINSTANCE.getNumericalAssignmentGroup_NumericValue();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.RValueImpl <em>RValue</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.RValueImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getRValue()
     * @generated
     */
    EClass RVALUE = eINSTANCE.getRValue();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.NegatableSubExpressionImpl <em>Negatable Sub Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.NegatableSubExpressionImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getNegatableSubExpression()
     * @generated
     */
    EClass NEGATABLE_SUB_EXPRESSION = eINSTANCE.getNegatableSubExpression();

    /**
     * The meta object literal for the '<em><b>Negated</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute NEGATABLE_SUB_EXPRESSION__NEGATED = eINSTANCE.getNegatableSubExpression_Negated();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NEGATABLE_SUB_EXPRESSION__EXPRESSION = eINSTANCE.getNegatableSubExpression_Expression();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.OrImpl <em>Or</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.OrImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getOr()
     * @generated
     */
    EClass OR = eINSTANCE.getOr();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR__LEFT = eINSTANCE.getOr_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR__RIGHT = eINSTANCE.getOr_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.escg.impl.AndImpl <em>And</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.escg.impl.AndImpl
     * @see com.b2international.snowowl.dsl.escg.impl.EscgPackageImpl#getAnd()
     * @generated
     */
    EClass AND = eINSTANCE.getAnd();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND__LEFT = eINSTANCE.getAnd_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND__RIGHT = eINSTANCE.getAnd_Right();

  }

} //EscgPackage