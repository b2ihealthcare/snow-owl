/**
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.etl.etl;

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
 * @see com.b2international.snowowl.snomed.etl.etl.EtlFactory
 * @model kind="package"
 * @generated
 */
public interface EtlPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "etl";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.b2international.com/snowowl/snomed/etl/Etl";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "etl";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EtlPackage eINSTANCE = com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl <em>Expression Template</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getExpressionTemplate()
   * @generated
   */
  int EXPRESSION_TEMPLATE = 0;

  /**
   * The feature id for the '<em><b>Primitive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_TEMPLATE__PRIMITIVE = 0;

  /**
   * The feature id for the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_TEMPLATE__SLOT = 1;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_TEMPLATE__EXPRESSION = 2;

  /**
   * The number of structural features of the '<em>Expression Template</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_TEMPLATE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeValueImpl <em>Attribute Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.AttributeValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getAttributeValue()
   * @generated
   */
  int ATTRIBUTE_VALUE = 6;

  /**
   * The number of structural features of the '<em>Attribute Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SubExpressionImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSubExpression()
   * @generated
   */
  int SUB_EXPRESSION = 1;

  /**
   * The feature id for the '<em><b>Focus Concepts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_EXPRESSION__FOCUS_CONCEPTS = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Refinement</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_EXPRESSION__REFINEMENT = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Sub Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SUB_EXPRESSION_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.FocusConceptImpl <em>Focus Concept</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.FocusConceptImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getFocusConcept()
   * @generated
   */
  int FOCUS_CONCEPT = 2;

  /**
   * The feature id for the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOCUS_CONCEPT__SLOT = 0;

  /**
   * The feature id for the '<em><b>Concept</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOCUS_CONCEPT__CONCEPT = 1;

  /**
   * The number of structural features of the '<em>Focus Concept</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int FOCUS_CONCEPT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.RefinementImpl <em>Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.RefinementImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getRefinement()
   * @generated
   */
  int REFINEMENT = 3;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENT__ATTRIBUTES = 0;

  /**
   * The feature id for the '<em><b>Groups</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENT__GROUPS = 1;

  /**
   * The number of structural features of the '<em>Refinement</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int REFINEMENT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.AttributeGroupImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getAttributeGroup()
   * @generated
   */
  int ATTRIBUTE_GROUP = 4;

  /**
   * The feature id for the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP__SLOT = 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP__ATTRIBUTES = 1;

  /**
   * The number of structural features of the '<em>Attribute Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.AttributeImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 5;

  /**
   * The feature id for the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__SLOT = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__NAME = 1;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__VALUE = 2;

  /**
   * The number of structural features of the '<em>Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_FEATURE_COUNT = 3;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReplacementSlotImpl <em>Concept Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.ConceptReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConceptReplacementSlot()
   * @generated
   */
  int CONCEPT_REPLACEMENT_SLOT = 16;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REPLACEMENT_SLOT__CONSTRAINT = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REPLACEMENT_SLOT__NAME = 1;

  /**
   * The number of structural features of the '<em>Concept Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REPLACEMENT_SLOT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptIdReplacementSlotImpl <em>Concept Id Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.ConceptIdReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConceptIdReplacementSlot()
   * @generated
   */
  int CONCEPT_ID_REPLACEMENT_SLOT = 7;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_ID_REPLACEMENT_SLOT__CONSTRAINT = CONCEPT_REPLACEMENT_SLOT__CONSTRAINT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_ID_REPLACEMENT_SLOT__NAME = CONCEPT_REPLACEMENT_SLOT__NAME;

  /**
   * The number of structural features of the '<em>Concept Id Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_ID_REPLACEMENT_SLOT_FEATURE_COUNT = CONCEPT_REPLACEMENT_SLOT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionReplacementSlotImpl <em>Expression Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.ExpressionReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getExpressionReplacementSlot()
   * @generated
   */
  int EXPRESSION_REPLACEMENT_SLOT = 8;

  /**
   * The feature id for the '<em><b>Constraint</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_REPLACEMENT_SLOT__CONSTRAINT = CONCEPT_REPLACEMENT_SLOT__CONSTRAINT;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_REPLACEMENT_SLOT__NAME = CONCEPT_REPLACEMENT_SLOT__NAME;

  /**
   * The number of structural features of the '<em>Expression Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_REPLACEMENT_SLOT_FEATURE_COUNT = CONCEPT_REPLACEMENT_SLOT_FEATURE_COUNT + 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.TokenReplacementSlotImpl <em>Token Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.TokenReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getTokenReplacementSlot()
   * @generated
   */
  int TOKEN_REPLACEMENT_SLOT = 9;

  /**
   * The feature id for the '<em><b>Tokens</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOKEN_REPLACEMENT_SLOT__TOKENS = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOKEN_REPLACEMENT_SLOT__NAME = 1;

  /**
   * The number of structural features of the '<em>Token Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TOKEN_REPLACEMENT_SLOT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.TemplateInformationSlotImpl <em>Template Information Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.TemplateInformationSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getTemplateInformationSlot()
   * @generated
   */
  int TEMPLATE_INFORMATION_SLOT = 10;

  /**
   * The feature id for the '<em><b>Cardinality</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEMPLATE_INFORMATION_SLOT__CARDINALITY = 0;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEMPLATE_INFORMATION_SLOT__NAME = 1;

  /**
   * The number of structural features of the '<em>Template Information Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int TEMPLATE_INFORMATION_SLOT_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConcreteValueReplacementSlotImpl <em>Concrete Value Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.ConcreteValueReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConcreteValueReplacementSlot()
   * @generated
   */
  int CONCRETE_VALUE_REPLACEMENT_SLOT = 11;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCRETE_VALUE_REPLACEMENT_SLOT__NAME = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Concrete Value Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.StringReplacementSlotImpl <em>String Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.StringReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getStringReplacementSlot()
   * @generated
   */
  int STRING_REPLACEMENT_SLOT = 12;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_REPLACEMENT_SLOT__NAME = CONCRETE_VALUE_REPLACEMENT_SLOT__NAME;

  /**
   * The feature id for the '<em><b>Values</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_REPLACEMENT_SLOT__VALUES = CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_REPLACEMENT_SLOT_FEATURE_COUNT = CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.IntegerReplacementSlotImpl <em>Integer Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.IntegerReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getIntegerReplacementSlot()
   * @generated
   */
  int INTEGER_REPLACEMENT_SLOT = 13;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_REPLACEMENT_SLOT__NAME = CONCRETE_VALUE_REPLACEMENT_SLOT__NAME;

  /**
   * The feature id for the '<em><b>Values</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_REPLACEMENT_SLOT__VALUES = CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_REPLACEMENT_SLOT_FEATURE_COUNT = CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.DecimalReplacementSlotImpl <em>Decimal Replacement Slot</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.DecimalReplacementSlotImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getDecimalReplacementSlot()
   * @generated
   */
  int DECIMAL_REPLACEMENT_SLOT = 14;

  /**
   * The feature id for the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_REPLACEMENT_SLOT__NAME = CONCRETE_VALUE_REPLACEMENT_SLOT__NAME;

  /**
   * The feature id for the '<em><b>Values</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_REPLACEMENT_SLOT__VALUES = CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Replacement Slot</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_REPLACEMENT_SLOT_FEATURE_COUNT = CONCRETE_VALUE_REPLACEMENT_SLOT_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.EtlCardinalityImpl <em>Cardinality</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlCardinalityImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getEtlCardinality()
   * @generated
   */
  int ETL_CARDINALITY = 15;

  /**
   * The feature id for the '<em><b>Min</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ETL_CARDINALITY__MIN = 0;

  /**
   * The feature id for the '<em><b>Max</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ETL_CARDINALITY__MAX = 1;

  /**
   * The number of structural features of the '<em>Cardinality</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ETL_CARDINALITY_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConceptReference()
   * @generated
   */
  int CONCEPT_REFERENCE = 17;

  /**
   * The feature id for the '<em><b>Slot</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__SLOT = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__ID = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__TERM = ATTRIBUTE_VALUE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Concept Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.StringValueImpl <em>String Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.StringValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getStringValue()
   * @generated
   */
  int STRING_VALUE = 18;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE__VALUE = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>String Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int STRING_VALUE_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.IntegerValueImpl <em>Integer Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.IntegerValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getIntegerValue()
   * @generated
   */
  int INTEGER_VALUE = 19;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE__VALUE = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Integer Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int INTEGER_VALUE_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.DecimalValueImpl <em>Decimal Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.DecimalValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getDecimalValue()
   * @generated
   */
  int DECIMAL_VALUE = 20;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE__VALUE = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Decimal Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int DECIMAL_VALUE_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerImpl <em>Slot Integer</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotInteger()
   * @generated
   */
  int SLOT_INTEGER = 21;

  /**
   * The number of structural features of the '<em>Slot Integer</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerValueImpl <em>Slot Integer Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerValue()
   * @generated
   */
  int SLOT_INTEGER_VALUE = 22;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_VALUE__VALUE = SLOT_INTEGER_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Slot Integer Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_VALUE_FEATURE_COUNT = SLOT_INTEGER_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerRangeImpl <em>Slot Integer Range</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerRangeImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerRange()
   * @generated
   */
  int SLOT_INTEGER_RANGE = 23;

  /**
   * The feature id for the '<em><b>Minimum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_RANGE__MINIMUM = SLOT_INTEGER_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Maximum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_RANGE__MAXIMUM = SLOT_INTEGER_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Slot Integer Range</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_RANGE_FEATURE_COUNT = SLOT_INTEGER_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMinimumValueImpl <em>Slot Integer Minimum Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMinimumValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerMinimumValue()
   * @generated
   */
  int SLOT_INTEGER_MINIMUM_VALUE = 24;

  /**
   * The feature id for the '<em><b>Exclusive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_MINIMUM_VALUE__EXCLUSIVE = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_MINIMUM_VALUE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Slot Integer Minimum Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_MINIMUM_VALUE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMaximumValueImpl <em>Slot Integer Maximum Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMaximumValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerMaximumValue()
   * @generated
   */
  int SLOT_INTEGER_MAXIMUM_VALUE = 25;

  /**
   * The feature id for the '<em><b>Exclusive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_MAXIMUM_VALUE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Slot Integer Maximum Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_INTEGER_MAXIMUM_VALUE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalImpl <em>Slot Decimal</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimal()
   * @generated
   */
  int SLOT_DECIMAL = 26;

  /**
   * The number of structural features of the '<em>Slot Decimal</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalValueImpl <em>Slot Decimal Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalValue()
   * @generated
   */
  int SLOT_DECIMAL_VALUE = 27;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_VALUE__VALUE = SLOT_DECIMAL_FEATURE_COUNT + 0;

  /**
   * The number of structural features of the '<em>Slot Decimal Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_VALUE_FEATURE_COUNT = SLOT_DECIMAL_FEATURE_COUNT + 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalRangeImpl <em>Slot Decimal Range</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalRangeImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalRange()
   * @generated
   */
  int SLOT_DECIMAL_RANGE = 28;

  /**
   * The feature id for the '<em><b>Minimum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_RANGE__MINIMUM = SLOT_DECIMAL_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Maximum</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_RANGE__MAXIMUM = SLOT_DECIMAL_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Slot Decimal Range</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_RANGE_FEATURE_COUNT = SLOT_DECIMAL_FEATURE_COUNT + 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMinimumValueImpl <em>Slot Decimal Minimum Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMinimumValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalMinimumValue()
   * @generated
   */
  int SLOT_DECIMAL_MINIMUM_VALUE = 29;

  /**
   * The feature id for the '<em><b>Exclusive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_MINIMUM_VALUE__EXCLUSIVE = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_MINIMUM_VALUE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Slot Decimal Minimum Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_MINIMUM_VALUE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMaximumValueImpl <em>Slot Decimal Maximum Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMaximumValueImpl
   * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalMaximumValue()
   * @generated
   */
  int SLOT_DECIMAL_MAXIMUM_VALUE = 30;

  /**
   * The feature id for the '<em><b>Exclusive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_MAXIMUM_VALUE__EXCLUSIVE = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_MAXIMUM_VALUE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Slot Decimal Maximum Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int SLOT_DECIMAL_MAXIMUM_VALUE_FEATURE_COUNT = 2;


  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate <em>Expression Template</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression Template</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate
   * @generated
   */
  EClass getExpressionTemplate();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#isPrimitive <em>Primitive</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Primitive</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#isPrimitive()
   * @see #getExpressionTemplate()
   * @generated
   */
  EAttribute getExpressionTemplate_Primitive();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getSlot <em>Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getSlot()
   * @see #getExpressionTemplate()
   * @generated
   */
  EReference getExpressionTemplate_Slot();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionTemplate#getExpression()
   * @see #getExpressionTemplate()
   * @generated
   */
  EReference getExpressionTemplate_Expression();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SubExpression <em>Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Sub Expression</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SubExpression
   * @generated
   */
  EClass getSubExpression();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.etl.etl.SubExpression#getFocusConcepts <em>Focus Concepts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Focus Concepts</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SubExpression#getFocusConcepts()
   * @see #getSubExpression()
   * @generated
   */
  EReference getSubExpression_FocusConcepts();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.SubExpression#getRefinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SubExpression#getRefinement()
   * @see #getSubExpression()
   * @generated
   */
  EReference getSubExpression_Refinement();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept <em>Focus Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Focus Concept</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.FocusConcept
   * @generated
   */
  EClass getFocusConcept();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept#getSlot <em>Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.FocusConcept#getSlot()
   * @see #getFocusConcept()
   * @generated
   */
  EReference getFocusConcept_Slot();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.FocusConcept#getConcept <em>Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Concept</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.FocusConcept#getConcept()
   * @see #getFocusConcept()
   * @generated
   */
  EReference getFocusConcept_Concept();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.Refinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Refinement
   * @generated
   */
  EClass getRefinement();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.etl.etl.Refinement#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Refinement#getAttributes()
   * @see #getRefinement()
   * @generated
   */
  EReference getRefinement_Attributes();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.etl.etl.Refinement#getGroups <em>Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Groups</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Refinement#getGroups()
   * @see #getRefinement()
   * @generated
   */
  EReference getRefinement_Groups();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Group</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.AttributeGroup
   * @generated
   */
  EClass getAttributeGroup();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getSlot <em>Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getSlot()
   * @see #getAttributeGroup()
   * @generated
   */
  EReference getAttributeGroup_Slot();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.AttributeGroup#getAttributes()
   * @see #getAttributeGroup()
   * @generated
   */
  EReference getAttributeGroup_Attributes();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Attribute
   * @generated
   */
  EClass getAttribute();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.Attribute#getSlot <em>Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Attribute#getSlot()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Slot();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.Attribute#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Attribute#getName()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Name();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.Attribute#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.Attribute#getValue()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.AttributeValue <em>Attribute Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.AttributeValue
   * @generated
   */
  EClass getAttributeValue();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.ConceptIdReplacementSlot <em>Concept Id Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Id Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptIdReplacementSlot
   * @generated
   */
  EClass getConceptIdReplacementSlot();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot <em>Expression Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ExpressionReplacementSlot
   * @generated
   */
  EClass getExpressionReplacementSlot();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot <em>Token Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Token Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot
   * @generated
   */
  EClass getTokenReplacementSlot();

  /**
   * Returns the meta object for the attribute list '{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getTokens <em>Tokens</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Tokens</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getTokens()
   * @see #getTokenReplacementSlot()
   * @generated
   */
  EAttribute getTokenReplacementSlot_Tokens();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.TokenReplacementSlot#getName()
   * @see #getTokenReplacementSlot()
   * @generated
   */
  EAttribute getTokenReplacementSlot_Name();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot <em>Template Information Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Template Information Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot
   * @generated
   */
  EClass getTemplateInformationSlot();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot#getCardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Cardinality</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot#getCardinality()
   * @see #getTemplateInformationSlot()
   * @generated
   */
  EReference getTemplateInformationSlot_Cardinality();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.TemplateInformationSlot#getName()
   * @see #getTemplateInformationSlot()
   * @generated
   */
  EAttribute getTemplateInformationSlot_Name();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot <em>Concrete Value Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concrete Value Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot
   * @generated
   */
  EClass getConcreteValueReplacementSlot();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConcreteValueReplacementSlot#getName()
   * @see #getConcreteValueReplacementSlot()
   * @generated
   */
  EAttribute getConcreteValueReplacementSlot_Name();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot <em>String Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot
   * @generated
   */
  EClass getStringReplacementSlot();

  /**
   * Returns the meta object for the attribute list '{@link com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot#getValues <em>Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Values</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.StringReplacementSlot#getValues()
   * @see #getStringReplacementSlot()
   * @generated
   */
  EAttribute getStringReplacementSlot_Values();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot <em>Integer Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot
   * @generated
   */
  EClass getIntegerReplacementSlot();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot#getValues <em>Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Values</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.IntegerReplacementSlot#getValues()
   * @see #getIntegerReplacementSlot()
   * @generated
   */
  EReference getIntegerReplacementSlot_Values();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot <em>Decimal Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot
   * @generated
   */
  EClass getDecimalReplacementSlot();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot#getValues <em>Values</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Values</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.DecimalReplacementSlot#getValues()
   * @see #getDecimalReplacementSlot()
   * @generated
   */
  EReference getDecimalReplacementSlot_Values();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality <em>Cardinality</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Cardinality</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlCardinality
   * @generated
   */
  EClass getEtlCardinality();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMin <em>Min</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Min</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMin()
   * @see #getEtlCardinality()
   * @generated
   */
  EAttribute getEtlCardinality_Min();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMax <em>Max</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Max</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.EtlCardinality#getMax()
   * @see #getEtlCardinality()
   * @generated
   */
  EAttribute getEtlCardinality_Max();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot <em>Concept Replacement Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Replacement Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot
   * @generated
   */
  EClass getConceptReplacementSlot();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot#getConstraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Constraint</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot#getConstraint()
   * @see #getConceptReplacementSlot()
   * @generated
   */
  EReference getConceptReplacementSlot_Constraint();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Name</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReplacementSlot#getName()
   * @see #getConceptReplacementSlot()
   * @generated
   */
  EAttribute getConceptReplacementSlot_Name();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference <em>Concept Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Reference</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReference
   * @generated
   */
  EClass getConceptReference();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getSlot <em>Slot</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Slot</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReference#getSlot()
   * @see #getConceptReference()
   * @generated
   */
  EReference getConceptReference_Slot();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReference#getId()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.ConceptReference#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.ConceptReference#getTerm()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Term();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.StringValue <em>String Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.StringValue
   * @generated
   */
  EClass getStringValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.StringValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.StringValue#getValue()
   * @see #getStringValue()
   * @generated
   */
  EAttribute getStringValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.IntegerValue <em>Integer Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.IntegerValue
   * @generated
   */
  EClass getIntegerValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.IntegerValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.IntegerValue#getValue()
   * @see #getIntegerValue()
   * @generated
   */
  EAttribute getIntegerValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.DecimalValue <em>Decimal Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.DecimalValue
   * @generated
   */
  EClass getDecimalValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.DecimalValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.DecimalValue#getValue()
   * @see #getDecimalValue()
   * @generated
   */
  EAttribute getDecimalValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotInteger <em>Slot Integer</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Integer</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotInteger
   * @generated
   */
  EClass getSlotInteger();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue <em>Slot Integer Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Integer Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue
   * @generated
   */
  EClass getSlotIntegerValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerValue#getValue()
   * @see #getSlotIntegerValue()
   * @generated
   */
  EAttribute getSlotIntegerValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange <em>Slot Integer Range</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Integer Range</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange
   * @generated
   */
  EClass getSlotIntegerRange();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMinimum <em>Minimum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Minimum</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMinimum()
   * @see #getSlotIntegerRange()
   * @generated
   */
  EReference getSlotIntegerRange_Minimum();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMaximum <em>Maximum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Maximum</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerRange#getMaximum()
   * @see #getSlotIntegerRange()
   * @generated
   */
  EReference getSlotIntegerRange_Maximum();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue <em>Slot Integer Minimum Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Integer Minimum Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue
   * @generated
   */
  EClass getSlotIntegerMinimumValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue#isExclusive <em>Exclusive</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exclusive</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue#isExclusive()
   * @see #getSlotIntegerMinimumValue()
   * @generated
   */
  EAttribute getSlotIntegerMinimumValue_Exclusive();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMinimumValue#getValue()
   * @see #getSlotIntegerMinimumValue()
   * @generated
   */
  EAttribute getSlotIntegerMinimumValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue <em>Slot Integer Maximum Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Integer Maximum Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue
   * @generated
   */
  EClass getSlotIntegerMaximumValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue#isExclusive <em>Exclusive</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exclusive</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue#isExclusive()
   * @see #getSlotIntegerMaximumValue()
   * @generated
   */
  EAttribute getSlotIntegerMaximumValue_Exclusive();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotIntegerMaximumValue#getValue()
   * @see #getSlotIntegerMaximumValue()
   * @generated
   */
  EAttribute getSlotIntegerMaximumValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimal <em>Slot Decimal</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Decimal</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimal
   * @generated
   */
  EClass getSlotDecimal();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue <em>Slot Decimal Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Decimal Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue
   * @generated
   */
  EClass getSlotDecimalValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalValue#getValue()
   * @see #getSlotDecimalValue()
   * @generated
   */
  EAttribute getSlotDecimalValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange <em>Slot Decimal Range</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Decimal Range</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange
   * @generated
   */
  EClass getSlotDecimalRange();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange#getMinimum <em>Minimum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Minimum</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange#getMinimum()
   * @see #getSlotDecimalRange()
   * @generated
   */
  EReference getSlotDecimalRange_Minimum();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange#getMaximum <em>Maximum</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Maximum</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalRange#getMaximum()
   * @see #getSlotDecimalRange()
   * @generated
   */
  EReference getSlotDecimalRange_Maximum();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue <em>Slot Decimal Minimum Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Decimal Minimum Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue
   * @generated
   */
  EClass getSlotDecimalMinimumValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue#isExclusive <em>Exclusive</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exclusive</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue#isExclusive()
   * @see #getSlotDecimalMinimumValue()
   * @generated
   */
  EAttribute getSlotDecimalMinimumValue_Exclusive();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMinimumValue#getValue()
   * @see #getSlotDecimalMinimumValue()
   * @generated
   */
  EAttribute getSlotDecimalMinimumValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue <em>Slot Decimal Maximum Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Slot Decimal Maximum Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue
   * @generated
   */
  EClass getSlotDecimalMaximumValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue#isExclusive <em>Exclusive</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Exclusive</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue#isExclusive()
   * @see #getSlotDecimalMaximumValue()
   * @generated
   */
  EAttribute getSlotDecimalMaximumValue_Exclusive();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.etl.etl.SlotDecimalMaximumValue#getValue()
   * @see #getSlotDecimalMaximumValue()
   * @generated
   */
  EAttribute getSlotDecimalMaximumValue_Value();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  EtlFactory getEtlFactory();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl <em>Expression Template</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.ExpressionTemplateImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getExpressionTemplate()
     * @generated
     */
    EClass EXPRESSION_TEMPLATE = eINSTANCE.getExpressionTemplate();

    /**
     * The meta object literal for the '<em><b>Primitive</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EXPRESSION_TEMPLATE__PRIMITIVE = eINSTANCE.getExpressionTemplate_Primitive();

    /**
     * The meta object literal for the '<em><b>Slot</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION_TEMPLATE__SLOT = eINSTANCE.getExpressionTemplate_Slot();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION_TEMPLATE__EXPRESSION = eINSTANCE.getExpressionTemplate_Expression();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SubExpressionImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSubExpression()
     * @generated
     */
    EClass SUB_EXPRESSION = eINSTANCE.getSubExpression();

    /**
     * The meta object literal for the '<em><b>Focus Concepts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUB_EXPRESSION__FOCUS_CONCEPTS = eINSTANCE.getSubExpression_FocusConcepts();

    /**
     * The meta object literal for the '<em><b>Refinement</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SUB_EXPRESSION__REFINEMENT = eINSTANCE.getSubExpression_Refinement();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.FocusConceptImpl <em>Focus Concept</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.FocusConceptImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getFocusConcept()
     * @generated
     */
    EClass FOCUS_CONCEPT = eINSTANCE.getFocusConcept();

    /**
     * The meta object literal for the '<em><b>Slot</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FOCUS_CONCEPT__SLOT = eINSTANCE.getFocusConcept_Slot();

    /**
     * The meta object literal for the '<em><b>Concept</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference FOCUS_CONCEPT__CONCEPT = eINSTANCE.getFocusConcept_Concept();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.RefinementImpl <em>Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.RefinementImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getRefinement()
     * @generated
     */
    EClass REFINEMENT = eINSTANCE.getRefinement();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFINEMENT__ATTRIBUTES = eINSTANCE.getRefinement_Attributes();

    /**
     * The meta object literal for the '<em><b>Groups</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference REFINEMENT__GROUPS = eINSTANCE.getRefinement_Groups();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.AttributeGroupImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getAttributeGroup()
     * @generated
     */
    EClass ATTRIBUTE_GROUP = eINSTANCE.getAttributeGroup();

    /**
     * The meta object literal for the '<em><b>Slot</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_GROUP__SLOT = eINSTANCE.getAttributeGroup_Slot();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_GROUP__ATTRIBUTES = eINSTANCE.getAttributeGroup_Attributes();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.AttributeImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getAttribute()
     * @generated
     */
    EClass ATTRIBUTE = eINSTANCE.getAttribute();

    /**
     * The meta object literal for the '<em><b>Slot</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__SLOT = eINSTANCE.getAttribute_Slot();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__NAME = eINSTANCE.getAttribute_Name();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE__VALUE = eINSTANCE.getAttribute_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.AttributeValueImpl <em>Attribute Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.AttributeValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getAttributeValue()
     * @generated
     */
    EClass ATTRIBUTE_VALUE = eINSTANCE.getAttributeValue();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptIdReplacementSlotImpl <em>Concept Id Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.ConceptIdReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConceptIdReplacementSlot()
     * @generated
     */
    EClass CONCEPT_ID_REPLACEMENT_SLOT = eINSTANCE.getConceptIdReplacementSlot();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ExpressionReplacementSlotImpl <em>Expression Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.ExpressionReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getExpressionReplacementSlot()
     * @generated
     */
    EClass EXPRESSION_REPLACEMENT_SLOT = eINSTANCE.getExpressionReplacementSlot();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.TokenReplacementSlotImpl <em>Token Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.TokenReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getTokenReplacementSlot()
     * @generated
     */
    EClass TOKEN_REPLACEMENT_SLOT = eINSTANCE.getTokenReplacementSlot();

    /**
     * The meta object literal for the '<em><b>Tokens</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOKEN_REPLACEMENT_SLOT__TOKENS = eINSTANCE.getTokenReplacementSlot_Tokens();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TOKEN_REPLACEMENT_SLOT__NAME = eINSTANCE.getTokenReplacementSlot_Name();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.TemplateInformationSlotImpl <em>Template Information Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.TemplateInformationSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getTemplateInformationSlot()
     * @generated
     */
    EClass TEMPLATE_INFORMATION_SLOT = eINSTANCE.getTemplateInformationSlot();

    /**
     * The meta object literal for the '<em><b>Cardinality</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference TEMPLATE_INFORMATION_SLOT__CARDINALITY = eINSTANCE.getTemplateInformationSlot_Cardinality();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute TEMPLATE_INFORMATION_SLOT__NAME = eINSTANCE.getTemplateInformationSlot_Name();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConcreteValueReplacementSlotImpl <em>Concrete Value Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.ConcreteValueReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConcreteValueReplacementSlot()
     * @generated
     */
    EClass CONCRETE_VALUE_REPLACEMENT_SLOT = eINSTANCE.getConcreteValueReplacementSlot();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCRETE_VALUE_REPLACEMENT_SLOT__NAME = eINSTANCE.getConcreteValueReplacementSlot_Name();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.StringReplacementSlotImpl <em>String Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.StringReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getStringReplacementSlot()
     * @generated
     */
    EClass STRING_REPLACEMENT_SLOT = eINSTANCE.getStringReplacementSlot();

    /**
     * The meta object literal for the '<em><b>Values</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_REPLACEMENT_SLOT__VALUES = eINSTANCE.getStringReplacementSlot_Values();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.IntegerReplacementSlotImpl <em>Integer Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.IntegerReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getIntegerReplacementSlot()
     * @generated
     */
    EClass INTEGER_REPLACEMENT_SLOT = eINSTANCE.getIntegerReplacementSlot();

    /**
     * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference INTEGER_REPLACEMENT_SLOT__VALUES = eINSTANCE.getIntegerReplacementSlot_Values();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.DecimalReplacementSlotImpl <em>Decimal Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.DecimalReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getDecimalReplacementSlot()
     * @generated
     */
    EClass DECIMAL_REPLACEMENT_SLOT = eINSTANCE.getDecimalReplacementSlot();

    /**
     * The meta object literal for the '<em><b>Values</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference DECIMAL_REPLACEMENT_SLOT__VALUES = eINSTANCE.getDecimalReplacementSlot_Values();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.EtlCardinalityImpl <em>Cardinality</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlCardinalityImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getEtlCardinality()
     * @generated
     */
    EClass ETL_CARDINALITY = eINSTANCE.getEtlCardinality();

    /**
     * The meta object literal for the '<em><b>Min</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ETL_CARDINALITY__MIN = eINSTANCE.getEtlCardinality_Min();

    /**
     * The meta object literal for the '<em><b>Max</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute ETL_CARDINALITY__MAX = eINSTANCE.getEtlCardinality_Max();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReplacementSlotImpl <em>Concept Replacement Slot</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.ConceptReplacementSlotImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConceptReplacementSlot()
     * @generated
     */
    EClass CONCEPT_REPLACEMENT_SLOT = eINSTANCE.getConceptReplacementSlot();

    /**
     * The meta object literal for the '<em><b>Constraint</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONCEPT_REPLACEMENT_SLOT__CONSTRAINT = eINSTANCE.getConceptReplacementSlot_Constraint();

    /**
     * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT_REPLACEMENT_SLOT__NAME = eINSTANCE.getConceptReplacementSlot_Name();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.ConceptReferenceImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getConceptReference()
     * @generated
     */
    EClass CONCEPT_REFERENCE = eINSTANCE.getConceptReference();

    /**
     * The meta object literal for the '<em><b>Slot</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference CONCEPT_REFERENCE__SLOT = eINSTANCE.getConceptReference_Slot();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.StringValueImpl <em>String Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.StringValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getStringValue()
     * @generated
     */
    EClass STRING_VALUE = eINSTANCE.getStringValue();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute STRING_VALUE__VALUE = eINSTANCE.getStringValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.IntegerValueImpl <em>Integer Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.IntegerValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getIntegerValue()
     * @generated
     */
    EClass INTEGER_VALUE = eINSTANCE.getIntegerValue();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute INTEGER_VALUE__VALUE = eINSTANCE.getIntegerValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.DecimalValueImpl <em>Decimal Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.DecimalValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getDecimalValue()
     * @generated
     */
    EClass DECIMAL_VALUE = eINSTANCE.getDecimalValue();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute DECIMAL_VALUE__VALUE = eINSTANCE.getDecimalValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerImpl <em>Slot Integer</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotInteger()
     * @generated
     */
    EClass SLOT_INTEGER = eINSTANCE.getSlotInteger();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerValueImpl <em>Slot Integer Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerValue()
     * @generated
     */
    EClass SLOT_INTEGER_VALUE = eINSTANCE.getSlotIntegerValue();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_INTEGER_VALUE__VALUE = eINSTANCE.getSlotIntegerValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerRangeImpl <em>Slot Integer Range</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerRangeImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerRange()
     * @generated
     */
    EClass SLOT_INTEGER_RANGE = eINSTANCE.getSlotIntegerRange();

    /**
     * The meta object literal for the '<em><b>Minimum</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SLOT_INTEGER_RANGE__MINIMUM = eINSTANCE.getSlotIntegerRange_Minimum();

    /**
     * The meta object literal for the '<em><b>Maximum</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SLOT_INTEGER_RANGE__MAXIMUM = eINSTANCE.getSlotIntegerRange_Maximum();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMinimumValueImpl <em>Slot Integer Minimum Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMinimumValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerMinimumValue()
     * @generated
     */
    EClass SLOT_INTEGER_MINIMUM_VALUE = eINSTANCE.getSlotIntegerMinimumValue();

    /**
     * The meta object literal for the '<em><b>Exclusive</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_INTEGER_MINIMUM_VALUE__EXCLUSIVE = eINSTANCE.getSlotIntegerMinimumValue_Exclusive();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_INTEGER_MINIMUM_VALUE__VALUE = eINSTANCE.getSlotIntegerMinimumValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMaximumValueImpl <em>Slot Integer Maximum Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotIntegerMaximumValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotIntegerMaximumValue()
     * @generated
     */
    EClass SLOT_INTEGER_MAXIMUM_VALUE = eINSTANCE.getSlotIntegerMaximumValue();

    /**
     * The meta object literal for the '<em><b>Exclusive</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_INTEGER_MAXIMUM_VALUE__EXCLUSIVE = eINSTANCE.getSlotIntegerMaximumValue_Exclusive();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_INTEGER_MAXIMUM_VALUE__VALUE = eINSTANCE.getSlotIntegerMaximumValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalImpl <em>Slot Decimal</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimal()
     * @generated
     */
    EClass SLOT_DECIMAL = eINSTANCE.getSlotDecimal();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalValueImpl <em>Slot Decimal Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalValue()
     * @generated
     */
    EClass SLOT_DECIMAL_VALUE = eINSTANCE.getSlotDecimalValue();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_DECIMAL_VALUE__VALUE = eINSTANCE.getSlotDecimalValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalRangeImpl <em>Slot Decimal Range</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalRangeImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalRange()
     * @generated
     */
    EClass SLOT_DECIMAL_RANGE = eINSTANCE.getSlotDecimalRange();

    /**
     * The meta object literal for the '<em><b>Minimum</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SLOT_DECIMAL_RANGE__MINIMUM = eINSTANCE.getSlotDecimalRange_Minimum();

    /**
     * The meta object literal for the '<em><b>Maximum</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference SLOT_DECIMAL_RANGE__MAXIMUM = eINSTANCE.getSlotDecimalRange_Maximum();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMinimumValueImpl <em>Slot Decimal Minimum Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMinimumValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalMinimumValue()
     * @generated
     */
    EClass SLOT_DECIMAL_MINIMUM_VALUE = eINSTANCE.getSlotDecimalMinimumValue();

    /**
     * The meta object literal for the '<em><b>Exclusive</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_DECIMAL_MINIMUM_VALUE__EXCLUSIVE = eINSTANCE.getSlotDecimalMinimumValue_Exclusive();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_DECIMAL_MINIMUM_VALUE__VALUE = eINSTANCE.getSlotDecimalMinimumValue_Value();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMaximumValueImpl <em>Slot Decimal Maximum Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.etl.etl.impl.SlotDecimalMaximumValueImpl
     * @see com.b2international.snowowl.snomed.etl.etl.impl.EtlPackageImpl#getSlotDecimalMaximumValue()
     * @generated
     */
    EClass SLOT_DECIMAL_MAXIMUM_VALUE = eINSTANCE.getSlotDecimalMaximumValue();

    /**
     * The meta object literal for the '<em><b>Exclusive</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_DECIMAL_MAXIMUM_VALUE__EXCLUSIVE = eINSTANCE.getSlotDecimalMaximumValue_Exclusive();

    /**
     * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute SLOT_DECIMAL_MAXIMUM_VALUE__VALUE = eINSTANCE.getSlotDecimalMaximumValue_Value();

  }

} //EtlPackage
