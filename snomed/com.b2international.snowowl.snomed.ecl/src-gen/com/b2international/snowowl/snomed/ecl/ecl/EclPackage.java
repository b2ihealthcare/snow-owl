/**
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.ecl.ecl;

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
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclFactory
 * @model kind="package"
 * @generated
 */
public interface EclPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "ecl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.b2international.com/snowowl/snomed/Ecl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "ecl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EclPackage eINSTANCE = com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl <em>Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExpressionConstraint()
   * @generated
   */
  int EXPRESSION_CONSTRAINT = 0;

  /**
   * The number of structural features of the '<em>Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_CONSTRAINT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ChildOfImpl <em>Child Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ChildOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getChildOf()
   * @generated
   */
  int CHILD_OF = 1;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHILD_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Child Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CHILD_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl <em>Descendant Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOf()
   * @generated
   */
  int DESCENDANT_OF = 2;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Descendant Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl <em>Descendant Or Self Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOrSelfOf()
   * @generated
   */
  int DESCENDANT_OR_SELF_OF = 3;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OR_SELF_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Descendant Or Self Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DESCENDANT_OR_SELF_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ParentOfImpl <em>Parent Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ParentOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getParentOf()
   * @generated
   */
  int PARENT_OF = 4;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARENT_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Parent Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int PARENT_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOfImpl <em>Ancestor Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAncestorOf()
   * @generated
   */
  int ANCESTOR_OF = 5;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANCESTOR_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Ancestor Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANCESTOR_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOrSelfOfImpl <em>Ancestor Or Self Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOrSelfOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAncestorOrSelfOf()
   * @generated
   */
  int ANCESTOR_OR_SELF_OF = 6;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANCESTOR_OR_SELF_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Ancestor Or Self Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANCESTOR_OR_SELF_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl <em>Member Of</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getMemberOf()
   * @generated
   */
  int MEMBER_OF = 7;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_OF__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Member Of</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int MEMBER_OF_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getConceptReference()
   * @generated
   */
  int CONCEPT_REFERENCE = 8;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__ID = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__TERM = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Concept Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl <em>Any</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAny()
   * @generated
   */
  int ANY = 9;

  /**
   * The number of structural features of the '<em>Any</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ANY_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.RefinementImpl <em>Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.RefinementImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getRefinement()
   * @generated
   */
  int REFINEMENT = 10;

  /**
   * The number of structural features of the '<em>Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENT_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.NestedRefinementImpl <em>Nested Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.NestedRefinementImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getNestedRefinement()
   * @generated
   */
  int NESTED_REFINEMENT = 11;

  /**
   * The feature id for the '<em><b>Nested</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTED_REFINEMENT__NESTED = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Nested Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTED_REFINEMENT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeGroupImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeGroup()
   * @generated
   */
  int ATTRIBUTE_GROUP = 12;

  /**
   * The feature id for the '<em><b>Cardinality</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP__CARDINALITY = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP__REFINEMENT = REFINEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Attribute Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl <em>Attribute Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeConstraint()
   * @generated
   */
  int ATTRIBUTE_CONSTRAINT = 13;

  /**
   * The feature id for the '<em><b>Cardinality</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_CONSTRAINT__CARDINALITY = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Reversed</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_CONSTRAINT__REVERSED = REFINEMENT_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_CONSTRAINT__ATTRIBUTE = REFINEMENT_FEATURE_COUNT + 2;

  /**
   * The feature id for the '<em><b>Comparison</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_CONSTRAINT__COMPARISON = REFINEMENT_FEATURE_COUNT + 3;

  /**
   * The number of structural features of the '<em>Attribute Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_CONSTRAINT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 4;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.CardinalityImpl <em>Cardinality</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.CardinalityImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getCardinality()
   * @generated
   */
  int CARDINALITY = 14;

  /**
   * The feature id for the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CARDINALITY__MIN = 0;

  /**
   * The feature id for the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CARDINALITY__MAX = 1;

  /**
   * The number of structural features of the '<em>Cardinality</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CARDINALITY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ComparisonImpl <em>Comparison</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ComparisonImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getComparison()
   * @generated
   */
  int COMPARISON = 15;

  /**
   * The number of structural features of the '<em>Comparison</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int COMPARISON_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeComparisonImpl <em>Attribute Comparison</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeComparisonImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeComparison()
   * @generated
   */
  int ATTRIBUTE_COMPARISON = 16;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_COMPARISON__CONSTRAINT = COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Attribute Comparison</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_COMPARISON_FEATURE_COUNT = COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DataTypeComparisonImpl <em>Data Type Comparison</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DataTypeComparisonImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDataTypeComparison()
   * @generated
   */
  int DATA_TYPE_COMPARISON = 17;

  /**
   * The number of structural features of the '<em>Data Type Comparison</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DATA_TYPE_COMPARISON_FEATURE_COUNT = COMPARISON_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueEqualsImpl <em>Attribute Value Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeValueEquals()
   * @generated
   */
  int ATTRIBUTE_VALUE_EQUALS = 18;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_EQUALS__CONSTRAINT = ATTRIBUTE_COMPARISON__CONSTRAINT;

  /**
   * The number of structural features of the '<em>Attribute Value Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_EQUALS_FEATURE_COUNT = ATTRIBUTE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueNotEqualsImpl <em>Attribute Value Not Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueNotEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeValueNotEquals()
   * @generated
   */
  int ATTRIBUTE_VALUE_NOT_EQUALS = 19;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_NOT_EQUALS__CONSTRAINT = ATTRIBUTE_COMPARISON__CONSTRAINT;

  /**
   * The number of structural features of the '<em>Attribute Value Not Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_NOT_EQUALS_FEATURE_COUNT = ATTRIBUTE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueEqualsImpl <em>String Value Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getStringValueEquals()
   * @generated
   */
  int STRING_VALUE_EQUALS = 20;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Value Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueNotEqualsImpl <em>String Value Not Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueNotEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getStringValueNotEquals()
   * @generated
   */
  int STRING_VALUE_NOT_EQUALS = 21;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_NOT_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Value Not Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_NOT_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueEqualsImpl <em>Integer Value Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueEquals()
   * @generated
   */
  int INTEGER_VALUE_EQUALS = 22;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueNotEqualsImpl <em>Integer Value Not Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueNotEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueNotEquals()
   * @generated
   */
  int INTEGER_VALUE_NOT_EQUALS = 23;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_NOT_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value Not Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_NOT_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanImpl <em>Integer Value Greater Than</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueGreaterThan()
   * @generated
   */
  int INTEGER_VALUE_GREATER_THAN = 24;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_GREATER_THAN__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value Greater Than</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_GREATER_THAN_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanImpl <em>Integer Value Less Than</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueLessThan()
   * @generated
   */
  int INTEGER_VALUE_LESS_THAN = 25;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_LESS_THAN__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value Less Than</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_LESS_THAN_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanEqualsImpl <em>Integer Value Greater Than Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueGreaterThanEquals()
   * @generated
   */
  int INTEGER_VALUE_GREATER_THAN_EQUALS = 26;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_GREATER_THAN_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value Greater Than Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_GREATER_THAN_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanEqualsImpl <em>Integer Value Less Than Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueLessThanEquals()
   * @generated
   */
  int INTEGER_VALUE_LESS_THAN_EQUALS = 27;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_LESS_THAN_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value Less Than Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_LESS_THAN_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueEqualsImpl <em>Decimal Value Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueEquals()
   * @generated
   */
  int DECIMAL_VALUE_EQUALS = 28;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueNotEqualsImpl <em>Decimal Value Not Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueNotEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueNotEquals()
   * @generated
   */
  int DECIMAL_VALUE_NOT_EQUALS = 29;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_NOT_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value Not Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_NOT_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanImpl <em>Decimal Value Greater Than</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueGreaterThan()
   * @generated
   */
  int DECIMAL_VALUE_GREATER_THAN = 30;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_GREATER_THAN__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value Greater Than</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_GREATER_THAN_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanImpl <em>Decimal Value Less Than</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueLessThan()
   * @generated
   */
  int DECIMAL_VALUE_LESS_THAN = 31;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_LESS_THAN__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value Less Than</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_LESS_THAN_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanEqualsImpl <em>Decimal Value Greater Than Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueGreaterThanEquals()
   * @generated
   */
  int DECIMAL_VALUE_GREATER_THAN_EQUALS = 32;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_GREATER_THAN_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value Greater Than Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_GREATER_THAN_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanEqualsImpl <em>Decimal Value Less Than Equals</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanEqualsImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueLessThanEquals()
   * @generated
   */
  int DECIMAL_VALUE_LESS_THAN_EQUALS = 33;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_LESS_THAN_EQUALS__VALUE = DATA_TYPE_COMPARISON_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value Less Than Equals</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_LESS_THAN_EQUALS_FEATURE_COUNT = DATA_TYPE_COMPARISON_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.NestedExpressionImpl <em>Nested Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.NestedExpressionImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getNestedExpression()
   * @generated
   */
  int NESTED_EXPRESSION = 34;

  /**
   * The feature id for the '<em><b>Nested</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTED_EXPRESSION__NESTED = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Nested Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int NESTED_EXPRESSION_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl <em>Or Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getOrExpressionConstraint()
   * @generated
   */
  int OR_EXPRESSION_CONSTRAINT = 35;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_EXPRESSION_CONSTRAINT__LEFT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_EXPRESSION_CONSTRAINT__RIGHT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Or Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl <em>And Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAndExpressionConstraint()
   * @generated
   */
  int AND_EXPRESSION_CONSTRAINT = 36;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_EXPRESSION_CONSTRAINT__LEFT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_EXPRESSION_CONSTRAINT__RIGHT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>And Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl <em>Exclusion Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExclusionExpressionConstraint()
   * @generated
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT = 37;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT__LEFT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Exclusion Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXCLUSION_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.RefinedExpressionConstraintImpl <em>Refined Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.RefinedExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getRefinedExpressionConstraint()
   * @generated
   */
  int REFINED_EXPRESSION_CONSTRAINT = 38;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINED_EXPRESSION_CONSTRAINT__REFINEMENT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Refined Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINED_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DottedExpressionConstraintImpl <em>Dotted Expression Constraint</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DottedExpressionConstraintImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDottedExpressionConstraint()
   * @generated
   */
  int DOTTED_EXPRESSION_CONSTRAINT = 39;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOTTED_EXPRESSION_CONSTRAINT__CONSTRAINT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attribute</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOTTED_EXPRESSION_CONSTRAINT__ATTRIBUTE = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Dotted Expression Constraint</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DOTTED_EXPRESSION_CONSTRAINT_FEATURE_COUNT = EXPRESSION_CONSTRAINT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.OrRefinementImpl <em>Or Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.OrRefinementImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getOrRefinement()
   * @generated
   */
  int OR_REFINEMENT = 40;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_REFINEMENT__LEFT = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_REFINEMENT__RIGHT = REFINEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Or Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int OR_REFINEMENT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AndRefinementImpl <em>And Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AndRefinementImpl
   * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAndRefinement()
   * @generated
   */
  int AND_REFINEMENT = 41;

  /**
   * The feature id for the '<em><b>Left</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_REFINEMENT__LEFT = REFINEMENT_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Right</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_REFINEMENT__RIGHT = REFINEMENT_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>And Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int AND_REFINEMENT_FEATURE_COUNT = REFINEMENT_FEATURE_COUNT + 2;


  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint <em>Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExpressionConstraint
   * @generated
   */
  EClass getExpressionConstraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ChildOf <em>Child Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Child Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ChildOf
   * @generated
   */
  EClass getChildOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ChildOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ChildOf#getConstraint()
   * @see #getChildOf()
   * @generated
   */
  EReference getChildOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOf <em>Descendant Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Descendant Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOf
   * @generated
   */
  EClass getDescendantOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOf#getConstraint()
   * @see #getDescendantOf()
   * @generated
   */
  EReference getDescendantOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf <em>Descendant Or Self Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Descendant Or Self Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf
   * @generated
   */
  EClass getDescendantOrSelfOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DescendantOrSelfOf#getConstraint()
   * @see #getDescendantOrSelfOf()
   * @generated
   */
  EReference getDescendantOrSelfOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ParentOf <em>Parent Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Parent Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ParentOf
   * @generated
   */
  EClass getParentOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ParentOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ParentOf#getConstraint()
   * @see #getParentOf()
   * @generated
   */
  EReference getParentOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOf <em>Ancestor Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ancestor Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AncestorOf
   * @generated
   */
  EClass getAncestorOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AncestorOf#getConstraint()
   * @see #getAncestorOf()
   * @generated
   */
  EReference getAncestorOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf <em>Ancestor Or Self Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Ancestor Or Self Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf
   * @generated
   */
  EClass getAncestorOrSelfOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AncestorOrSelfOf#getConstraint()
   * @see #getAncestorOrSelfOf()
   * @generated
   */
  EReference getAncestorOrSelfOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf <em>Member Of</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Member Of</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.MemberOf
   * @generated
   */
  EClass getMemberOf();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.MemberOf#getConstraint()
   * @see #getMemberOf()
   * @generated
   */
  EReference getMemberOf_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference <em>Concept Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Reference</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference
   * @generated
   */
  EClass getConceptReference();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getId()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ConceptReference#getTerm()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Term();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.Any <em>Any</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Any</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Any
   * @generated
   */
  EClass getAny();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.Refinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Refinement
   * @generated
   */
  EClass getRefinement();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement <em>Nested Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nested Refinement</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement
   * @generated
   */
  EClass getNestedRefinement();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement#getNested <em>Nested</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Nested</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestedRefinement#getNested()
   * @see #getNestedRefinement()
   * @generated
   */
  EReference getNestedRefinement_Nested();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Group</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup
   * @generated
   */
  EClass getAttributeGroup();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup#getCardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Cardinality</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup#getCardinality()
   * @see #getAttributeGroup()
   * @generated
   */
  EReference getAttributeGroup_Cardinality();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup#getRefinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeGroup#getRefinement()
   * @see #getAttributeGroup()
   * @generated
   */
  EReference getAttributeGroup_Refinement();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint <em>Attribute Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint
   * @generated
   */
  EClass getAttributeConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getCardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Cardinality</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getCardinality()
   * @see #getAttributeConstraint()
   * @generated
   */
  EReference getAttributeConstraint_Cardinality();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#isReversed <em>Reversed</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Reversed</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#isReversed()
   * @see #getAttributeConstraint()
   * @generated
   */
  EAttribute getAttributeConstraint_Reversed();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Attribute</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getAttribute()
   * @see #getAttributeConstraint()
   * @generated
   */
  EReference getAttributeConstraint_Attribute();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getComparison <em>Comparison</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Comparison</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeConstraint#getComparison()
   * @see #getAttributeConstraint()
   * @generated
   */
  EReference getAttributeConstraint_Comparison();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Cardinality</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Cardinality
   * @generated
   */
  EClass getCardinality();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMin <em>Min</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Min</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMin()
   * @see #getCardinality()
   * @generated
   */
  EAttribute getCardinality_Min();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMax <em>Max</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Max</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Cardinality#getMax()
   * @see #getCardinality()
   * @generated
   */
  EAttribute getCardinality_Max();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.Comparison <em>Comparison</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Comparison</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.Comparison
   * @generated
   */
  EClass getComparison();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison <em>Attribute Comparison</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Comparison</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison
   * @generated
   */
  EClass getAttributeComparison();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeComparison#getConstraint()
   * @see #getAttributeComparison()
   * @generated
   */
  EReference getAttributeComparison_Constraint();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DataTypeComparison <em>Data Type Comparison</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Data Type Comparison</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DataTypeComparison
   * @generated
   */
  EClass getDataTypeComparison();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals <em>Attribute Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Value Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeValueEquals
   * @generated
   */
  EClass getAttributeValueEquals();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals <em>Attribute Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Value Not Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AttributeValueNotEquals
   * @generated
   */
  EClass getAttributeValueNotEquals();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals <em>String Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Value Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals
   * @generated
   */
  EClass getStringValueEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.StringValueEquals#getValue()
   * @see #getStringValueEquals()
   * @generated
   */
  EAttribute getStringValueEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals <em>String Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Value Not Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals
   * @generated
   */
  EClass getStringValueNotEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.StringValueNotEquals#getValue()
   * @see #getStringValueNotEquals()
   * @generated
   */
  EAttribute getStringValueNotEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals <em>Integer Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals
   * @generated
   */
  EClass getIntegerValueEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueEquals#getValue()
   * @see #getIntegerValueEquals()
   * @generated
   */
  EAttribute getIntegerValueEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals <em>Integer Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value Not Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals
   * @generated
   */
  EClass getIntegerValueNotEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueNotEquals#getValue()
   * @see #getIntegerValueNotEquals()
   * @generated
   */
  EAttribute getIntegerValueNotEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan <em>Integer Value Greater Than</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value Greater Than</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan
   * @generated
   */
  EClass getIntegerValueGreaterThan();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThan#getValue()
   * @see #getIntegerValueGreaterThan()
   * @generated
   */
  EAttribute getIntegerValueGreaterThan_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan <em>Integer Value Less Than</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value Less Than</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan
   * @generated
   */
  EClass getIntegerValueLessThan();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThan#getValue()
   * @see #getIntegerValueLessThan()
   * @generated
   */
  EAttribute getIntegerValueLessThan_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals <em>Integer Value Greater Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value Greater Than Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals
   * @generated
   */
  EClass getIntegerValueGreaterThanEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueGreaterThanEquals#getValue()
   * @see #getIntegerValueGreaterThanEquals()
   * @generated
   */
  EAttribute getIntegerValueGreaterThanEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals <em>Integer Value Less Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value Less Than Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals
   * @generated
   */
  EClass getIntegerValueLessThanEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.IntegerValueLessThanEquals#getValue()
   * @see #getIntegerValueLessThanEquals()
   * @generated
   */
  EAttribute getIntegerValueLessThanEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals <em>Decimal Value Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals
   * @generated
   */
  EClass getDecimalValueEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueEquals#getValue()
   * @see #getDecimalValueEquals()
   * @generated
   */
  EAttribute getDecimalValueEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals <em>Decimal Value Not Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value Not Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals
   * @generated
   */
  EClass getDecimalValueNotEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueNotEquals#getValue()
   * @see #getDecimalValueNotEquals()
   * @generated
   */
  EAttribute getDecimalValueNotEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan <em>Decimal Value Greater Than</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value Greater Than</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan
   * @generated
   */
  EClass getDecimalValueGreaterThan();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThan#getValue()
   * @see #getDecimalValueGreaterThan()
   * @generated
   */
  EAttribute getDecimalValueGreaterThan_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan <em>Decimal Value Less Than</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value Less Than</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan
   * @generated
   */
  EClass getDecimalValueLessThan();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThan#getValue()
   * @see #getDecimalValueLessThan()
   * @generated
   */
  EAttribute getDecimalValueLessThan_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals <em>Decimal Value Greater Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value Greater Than Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals
   * @generated
   */
  EClass getDecimalValueGreaterThanEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueGreaterThanEquals#getValue()
   * @see #getDecimalValueGreaterThanEquals()
   * @generated
   */
  EAttribute getDecimalValueGreaterThanEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals <em>Decimal Value Less Than Equals</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value Less Than Equals</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals
   * @generated
   */
  EClass getDecimalValueLessThanEquals();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DecimalValueLessThanEquals#getValue()
   * @see #getDecimalValueLessThanEquals()
   * @generated
   */
  EAttribute getDecimalValueLessThanEquals_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedExpression <em>Nested Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Nested Expression</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestedExpression
   * @generated
   */
  EClass getNestedExpression();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.NestedExpression#getNested <em>Nested</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Nested</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.NestedExpression#getNested()
   * @see #getNestedExpression()
   * @generated
   */
  EReference getNestedExpression_Nested();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint <em>Or Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint
   * @generated
   */
  EClass getOrExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getLeft()
   * @see #getOrExpressionConstraint()
   * @generated
   */
  EReference getOrExpressionConstraint_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrExpressionConstraint#getRight()
   * @see #getOrExpressionConstraint()
   * @generated
   */
  EReference getOrExpressionConstraint_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint <em>And Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint
   * @generated
   */
  EClass getAndExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getLeft()
   * @see #getAndExpressionConstraint()
   * @generated
   */
  EReference getAndExpressionConstraint_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndExpressionConstraint#getRight()
   * @see #getAndExpressionConstraint()
   * @generated
   */
  EReference getAndExpressionConstraint_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint <em>Exclusion Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Exclusion Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint
   * @generated
   */
  EClass getExclusionExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getLeft()
   * @see #getExclusionExpressionConstraint()
   * @generated
   */
  EReference getExclusionExpressionConstraint_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.ExclusionExpressionConstraint#getRight()
   * @see #getExclusionExpressionConstraint()
   * @generated
   */
  EReference getExclusionExpressionConstraint_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint <em>Refined Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Refined Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint
   * @generated
   */
  EClass getRefinedExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getConstraint()
   * @see #getRefinedExpressionConstraint()
   * @generated
   */
  EReference getRefinedExpressionConstraint_Constraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getRefinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.RefinedExpressionConstraint#getRefinement()
   * @see #getRefinedExpressionConstraint()
   * @generated
   */
  EReference getRefinedExpressionConstraint_Refinement();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint <em>Dotted Expression Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Dotted Expression Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint
   * @generated
   */
  EClass getDottedExpressionConstraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint#getConstraint()
   * @see #getDottedExpressionConstraint()
   * @generated
   */
  EReference getDottedExpressionConstraint_Constraint();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint#getAttribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Attribute</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.DottedExpressionConstraint#getAttribute()
   * @see #getDottedExpressionConstraint()
   * @generated
   */
  EReference getDottedExpressionConstraint_Attribute();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.OrRefinement <em>Or Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Or Refinement</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrRefinement
   * @generated
   */
  EClass getOrRefinement();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.OrRefinement#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrRefinement#getLeft()
   * @see #getOrRefinement()
   * @generated
   */
  EReference getOrRefinement_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.OrRefinement#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.OrRefinement#getRight()
   * @see #getOrRefinement()
   * @generated
   */
  EReference getOrRefinement_Right();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement <em>And Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>And Refinement</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndRefinement
   * @generated
   */
  EClass getAndRefinement();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getLeft <em>Left</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Left</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getLeft()
   * @see #getAndRefinement()
   * @generated
   */
  EReference getAndRefinement_Left();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getRight <em>Right</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Right</em>'.
   * @see com.b2international.snowowl.snomed.ecl.ecl.AndRefinement#getRight()
   * @see #getAndRefinement()
   * @generated
   */
  EReference getAndRefinement_Right();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EclFactory getEclFactory();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl <em>Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExpressionConstraint()
     * @generated
     */
    EClass EXPRESSION_CONSTRAINT = eINSTANCE.getExpressionConstraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ChildOfImpl <em>Child Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ChildOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getChildOf()
     * @generated
     */
    EClass CHILD_OF = eINSTANCE.getChildOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CHILD_OF__CONSTRAINT = eINSTANCE.getChildOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl <em>Descendant Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOf()
     * @generated
     */
    EClass DESCENDANT_OF = eINSTANCE.getDescendantOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCENDANT_OF__CONSTRAINT = eINSTANCE.getDescendantOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl <em>Descendant Or Self Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DescendantOrSelfOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDescendantOrSelfOf()
     * @generated
     */
    EClass DESCENDANT_OR_SELF_OF = eINSTANCE.getDescendantOrSelfOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DESCENDANT_OR_SELF_OF__CONSTRAINT = eINSTANCE.getDescendantOrSelfOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ParentOfImpl <em>Parent Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ParentOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getParentOf()
     * @generated
     */
    EClass PARENT_OF = eINSTANCE.getParentOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference PARENT_OF__CONSTRAINT = eINSTANCE.getParentOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOfImpl <em>Ancestor Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAncestorOf()
     * @generated
     */
    EClass ANCESTOR_OF = eINSTANCE.getAncestorOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ANCESTOR_OF__CONSTRAINT = eINSTANCE.getAncestorOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOrSelfOfImpl <em>Ancestor Or Self Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AncestorOrSelfOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAncestorOrSelfOf()
     * @generated
     */
    EClass ANCESTOR_OR_SELF_OF = eINSTANCE.getAncestorOrSelfOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ANCESTOR_OR_SELF_OF__CONSTRAINT = eINSTANCE.getAncestorOrSelfOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl <em>Member Of</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.MemberOfImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getMemberOf()
     * @generated
     */
    EClass MEMBER_OF = eINSTANCE.getMemberOf();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference MEMBER_OF__CONSTRAINT = eINSTANCE.getMemberOf_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ConceptReferenceImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getConceptReference()
     * @generated
     */
    EClass CONCEPT_REFERENCE = eINSTANCE.getConceptReference();

    /**
     * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT_REFERENCE__ID = eINSTANCE.getConceptReference_Id();

    /**
     * The meta object literal for the '<em><b>Term</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT_REFERENCE__TERM = eINSTANCE.getConceptReference_Term();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl <em>Any</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AnyImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAny()
     * @generated
     */
    EClass ANY = eINSTANCE.getAny();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.RefinementImpl <em>Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.RefinementImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getRefinement()
     * @generated
     */
    EClass REFINEMENT = eINSTANCE.getRefinement();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.NestedRefinementImpl <em>Nested Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.NestedRefinementImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getNestedRefinement()
     * @generated
     */
    EClass NESTED_REFINEMENT = eINSTANCE.getNestedRefinement();

    /**
     * The meta object literal for the '<em><b>Nested</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NESTED_REFINEMENT__NESTED = eINSTANCE.getNestedRefinement_Nested();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeGroupImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeGroup()
     * @generated
     */
    EClass ATTRIBUTE_GROUP = eINSTANCE.getAttributeGroup();

    /**
     * The meta object literal for the '<em><b>Cardinality</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_GROUP__CARDINALITY = eINSTANCE.getAttributeGroup_Cardinality();

    /**
     * The meta object literal for the '<em><b>Refinement</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_GROUP__REFINEMENT = eINSTANCE.getAttributeGroup_Refinement();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl <em>Attribute Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeConstraint()
     * @generated
     */
    EClass ATTRIBUTE_CONSTRAINT = eINSTANCE.getAttributeConstraint();

    /**
     * The meta object literal for the '<em><b>Cardinality</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_CONSTRAINT__CARDINALITY = eINSTANCE.getAttributeConstraint_Cardinality();

    /**
     * The meta object literal for the '<em><b>Reversed</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ATTRIBUTE_CONSTRAINT__REVERSED = eINSTANCE.getAttributeConstraint_Reversed();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_CONSTRAINT__ATTRIBUTE = eINSTANCE.getAttributeConstraint_Attribute();

    /**
     * The meta object literal for the '<em><b>Comparison</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_CONSTRAINT__COMPARISON = eINSTANCE.getAttributeConstraint_Comparison();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.CardinalityImpl <em>Cardinality</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.CardinalityImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getCardinality()
     * @generated
     */
    EClass CARDINALITY = eINSTANCE.getCardinality();

    /**
     * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CARDINALITY__MIN = eINSTANCE.getCardinality_Min();

    /**
     * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CARDINALITY__MAX = eINSTANCE.getCardinality_Max();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ComparisonImpl <em>Comparison</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ComparisonImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getComparison()
     * @generated
     */
    EClass COMPARISON = eINSTANCE.getComparison();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeComparisonImpl <em>Attribute Comparison</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeComparisonImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeComparison()
     * @generated
     */
    EClass ATTRIBUTE_COMPARISON = eINSTANCE.getAttributeComparison();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_COMPARISON__CONSTRAINT = eINSTANCE.getAttributeComparison_Constraint();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DataTypeComparisonImpl <em>Data Type Comparison</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DataTypeComparisonImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDataTypeComparison()
     * @generated
     */
    EClass DATA_TYPE_COMPARISON = eINSTANCE.getDataTypeComparison();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueEqualsImpl <em>Attribute Value Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeValueEquals()
     * @generated
     */
    EClass ATTRIBUTE_VALUE_EQUALS = eINSTANCE.getAttributeValueEquals();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueNotEqualsImpl <em>Attribute Value Not Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AttributeValueNotEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAttributeValueNotEquals()
     * @generated
     */
    EClass ATTRIBUTE_VALUE_NOT_EQUALS = eINSTANCE.getAttributeValueNotEquals();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueEqualsImpl <em>String Value Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getStringValueEquals()
     * @generated
     */
    EClass STRING_VALUE_EQUALS = eINSTANCE.getStringValueEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VALUE_EQUALS__VALUE = eINSTANCE.getStringValueEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueNotEqualsImpl <em>String Value Not Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.StringValueNotEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getStringValueNotEquals()
     * @generated
     */
    EClass STRING_VALUE_NOT_EQUALS = eINSTANCE.getStringValueNotEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VALUE_NOT_EQUALS__VALUE = eINSTANCE.getStringValueNotEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueEqualsImpl <em>Integer Value Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueEquals()
     * @generated
     */
    EClass INTEGER_VALUE_EQUALS = eINSTANCE.getIntegerValueEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE_EQUALS__VALUE = eINSTANCE.getIntegerValueEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueNotEqualsImpl <em>Integer Value Not Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueNotEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueNotEquals()
     * @generated
     */
    EClass INTEGER_VALUE_NOT_EQUALS = eINSTANCE.getIntegerValueNotEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE_NOT_EQUALS__VALUE = eINSTANCE.getIntegerValueNotEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanImpl <em>Integer Value Greater Than</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueGreaterThan()
     * @generated
     */
    EClass INTEGER_VALUE_GREATER_THAN = eINSTANCE.getIntegerValueGreaterThan();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE_GREATER_THAN__VALUE = eINSTANCE.getIntegerValueGreaterThan_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanImpl <em>Integer Value Less Than</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueLessThan()
     * @generated
     */
    EClass INTEGER_VALUE_LESS_THAN = eINSTANCE.getIntegerValueLessThan();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE_LESS_THAN__VALUE = eINSTANCE.getIntegerValueLessThan_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanEqualsImpl <em>Integer Value Greater Than Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueGreaterThanEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueGreaterThanEquals()
     * @generated
     */
    EClass INTEGER_VALUE_GREATER_THAN_EQUALS = eINSTANCE.getIntegerValueGreaterThanEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE_GREATER_THAN_EQUALS__VALUE = eINSTANCE.getIntegerValueGreaterThanEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanEqualsImpl <em>Integer Value Less Than Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.IntegerValueLessThanEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getIntegerValueLessThanEquals()
     * @generated
     */
    EClass INTEGER_VALUE_LESS_THAN_EQUALS = eINSTANCE.getIntegerValueLessThanEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE_LESS_THAN_EQUALS__VALUE = eINSTANCE.getIntegerValueLessThanEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueEqualsImpl <em>Decimal Value Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueEquals()
     * @generated
     */
    EClass DECIMAL_VALUE_EQUALS = eINSTANCE.getDecimalValueEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE_EQUALS__VALUE = eINSTANCE.getDecimalValueEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueNotEqualsImpl <em>Decimal Value Not Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueNotEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueNotEquals()
     * @generated
     */
    EClass DECIMAL_VALUE_NOT_EQUALS = eINSTANCE.getDecimalValueNotEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE_NOT_EQUALS__VALUE = eINSTANCE.getDecimalValueNotEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanImpl <em>Decimal Value Greater Than</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueGreaterThan()
     * @generated
     */
    EClass DECIMAL_VALUE_GREATER_THAN = eINSTANCE.getDecimalValueGreaterThan();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE_GREATER_THAN__VALUE = eINSTANCE.getDecimalValueGreaterThan_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanImpl <em>Decimal Value Less Than</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueLessThan()
     * @generated
     */
    EClass DECIMAL_VALUE_LESS_THAN = eINSTANCE.getDecimalValueLessThan();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE_LESS_THAN__VALUE = eINSTANCE.getDecimalValueLessThan_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanEqualsImpl <em>Decimal Value Greater Than Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueGreaterThanEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueGreaterThanEquals()
     * @generated
     */
    EClass DECIMAL_VALUE_GREATER_THAN_EQUALS = eINSTANCE.getDecimalValueGreaterThanEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE_GREATER_THAN_EQUALS__VALUE = eINSTANCE.getDecimalValueGreaterThanEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanEqualsImpl <em>Decimal Value Less Than Equals</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DecimalValueLessThanEqualsImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDecimalValueLessThanEquals()
     * @generated
     */
    EClass DECIMAL_VALUE_LESS_THAN_EQUALS = eINSTANCE.getDecimalValueLessThanEquals();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE_LESS_THAN_EQUALS__VALUE = eINSTANCE.getDecimalValueLessThanEquals_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.NestedExpressionImpl <em>Nested Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.NestedExpressionImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getNestedExpression()
     * @generated
     */
    EClass NESTED_EXPRESSION = eINSTANCE.getNestedExpression();

    /**
     * The meta object literal for the '<em><b>Nested</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference NESTED_EXPRESSION__NESTED = eINSTANCE.getNestedExpression_Nested();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl <em>Or Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.OrExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getOrExpressionConstraint()
     * @generated
     */
    EClass OR_EXPRESSION_CONSTRAINT = eINSTANCE.getOrExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_EXPRESSION_CONSTRAINT__LEFT = eINSTANCE.getOrExpressionConstraint_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_EXPRESSION_CONSTRAINT__RIGHT = eINSTANCE.getOrExpressionConstraint_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl <em>And Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AndExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAndExpressionConstraint()
     * @generated
     */
    EClass AND_EXPRESSION_CONSTRAINT = eINSTANCE.getAndExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_EXPRESSION_CONSTRAINT__LEFT = eINSTANCE.getAndExpressionConstraint_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_EXPRESSION_CONSTRAINT__RIGHT = eINSTANCE.getAndExpressionConstraint_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl <em>Exclusion Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.ExclusionExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getExclusionExpressionConstraint()
     * @generated
     */
    EClass EXCLUSION_EXPRESSION_CONSTRAINT = eINSTANCE.getExclusionExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION_EXPRESSION_CONSTRAINT__LEFT = eINSTANCE.getExclusionExpressionConstraint_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXCLUSION_EXPRESSION_CONSTRAINT__RIGHT = eINSTANCE.getExclusionExpressionConstraint_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.RefinedExpressionConstraintImpl <em>Refined Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.RefinedExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getRefinedExpressionConstraint()
     * @generated
     */
    EClass REFINED_EXPRESSION_CONSTRAINT = eINSTANCE.getRefinedExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFINED_EXPRESSION_CONSTRAINT__CONSTRAINT = eINSTANCE.getRefinedExpressionConstraint_Constraint();

    /**
     * The meta object literal for the '<em><b>Refinement</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFINED_EXPRESSION_CONSTRAINT__REFINEMENT = eINSTANCE.getRefinedExpressionConstraint_Refinement();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.DottedExpressionConstraintImpl <em>Dotted Expression Constraint</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.DottedExpressionConstraintImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getDottedExpressionConstraint()
     * @generated
     */
    EClass DOTTED_EXPRESSION_CONSTRAINT = eINSTANCE.getDottedExpressionConstraint();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DOTTED_EXPRESSION_CONSTRAINT__CONSTRAINT = eINSTANCE.getDottedExpressionConstraint_Constraint();

    /**
     * The meta object literal for the '<em><b>Attribute</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DOTTED_EXPRESSION_CONSTRAINT__ATTRIBUTE = eINSTANCE.getDottedExpressionConstraint_Attribute();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.OrRefinementImpl <em>Or Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.OrRefinementImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getOrRefinement()
     * @generated
     */
    EClass OR_REFINEMENT = eINSTANCE.getOrRefinement();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_REFINEMENT__LEFT = eINSTANCE.getOrRefinement_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference OR_REFINEMENT__RIGHT = eINSTANCE.getOrRefinement_Right();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.ecl.ecl.impl.AndRefinementImpl <em>And Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.AndRefinementImpl
     * @see com.b2international.snowowl.snomed.ecl.ecl.impl.EclPackageImpl#getAndRefinement()
     * @generated
     */
    EClass AND_REFINEMENT = eINSTANCE.getAndRefinement();

    /**
     * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_REFINEMENT__LEFT = eINSTANCE.getAndRefinement_Left();

    /**
     * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference AND_REFINEMENT__RIGHT = eINSTANCE.getAndRefinement_Right();

  }

} //EclPackage
