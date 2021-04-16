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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.etl.etl.EtlPackage
 * @generated
 */
public interface EtlFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EtlFactory eINSTANCE = com.b2international.snowowl.snomed.etl.etl.impl.EtlFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Expression Template</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression Template</em>'.
   * @generated
   */
  ExpressionTemplate createExpressionTemplate();

  /**
   * Returns a new object of class '<em>Sub Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Sub Expression</em>'.
   * @generated
   */
  SubExpression createSubExpression();

  /**
   * Returns a new object of class '<em>Focus Concept</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Focus Concept</em>'.
   * @generated
   */
  FocusConcept createFocusConcept();

  /**
   * Returns a new object of class '<em>Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Refinement</em>'.
   * @generated
   */
  Refinement createRefinement();

  /**
   * Returns a new object of class '<em>Attribute Group</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Group</em>'.
   * @generated
   */
  AttributeGroup createAttributeGroup();

  /**
   * Returns a new object of class '<em>Attribute</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute</em>'.
   * @generated
   */
  Attribute createAttribute();

  /**
   * Returns a new object of class '<em>Attribute Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Value</em>'.
   * @generated
   */
  AttributeValue createAttributeValue();

  /**
   * Returns a new object of class '<em>Concept Id Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Concept Id Replacement Slot</em>'.
   * @generated
   */
  ConceptIdReplacementSlot createConceptIdReplacementSlot();

  /**
   * Returns a new object of class '<em>Expression Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression Replacement Slot</em>'.
   * @generated
   */
  ExpressionReplacementSlot createExpressionReplacementSlot();

  /**
   * Returns a new object of class '<em>Token Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Token Replacement Slot</em>'.
   * @generated
   */
  TokenReplacementSlot createTokenReplacementSlot();

  /**
   * Returns a new object of class '<em>Template Information Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Template Information Slot</em>'.
   * @generated
   */
  TemplateInformationSlot createTemplateInformationSlot();

  /**
   * Returns a new object of class '<em>Concrete Value Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Concrete Value Replacement Slot</em>'.
   * @generated
   */
  ConcreteValueReplacementSlot createConcreteValueReplacementSlot();

  /**
   * Returns a new object of class '<em>String Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Replacement Slot</em>'.
   * @generated
   */
  StringReplacementSlot createStringReplacementSlot();

  /**
   * Returns a new object of class '<em>Integer Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Replacement Slot</em>'.
   * @generated
   */
  IntegerReplacementSlot createIntegerReplacementSlot();

  /**
   * Returns a new object of class '<em>Decimal Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Replacement Slot</em>'.
   * @generated
   */
  DecimalReplacementSlot createDecimalReplacementSlot();

  /**
   * Returns a new object of class '<em>Cardinality</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Cardinality</em>'.
   * @generated
   */
  EtlCardinality createEtlCardinality();

  /**
   * Returns a new object of class '<em>Concept Replacement Slot</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Concept Replacement Slot</em>'.
   * @generated
   */
  ConceptReplacementSlot createConceptReplacementSlot();

  /**
   * Returns a new object of class '<em>Concept Reference</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Concept Reference</em>'.
   * @generated
   */
  ConceptReference createConceptReference();

  /**
   * Returns a new object of class '<em>String Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Value</em>'.
   * @generated
   */
  StringValue createStringValue();

  /**
   * Returns a new object of class '<em>Integer Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value</em>'.
   * @generated
   */
  IntegerValue createIntegerValue();

  /**
   * Returns a new object of class '<em>Decimal Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value</em>'.
   * @generated
   */
  DecimalValue createDecimalValue();

  /**
   * Returns a new object of class '<em>Slot Integer</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Integer</em>'.
   * @generated
   */
  SlotInteger createSlotInteger();

  /**
   * Returns a new object of class '<em>Slot Integer Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Integer Value</em>'.
   * @generated
   */
  SlotIntegerValue createSlotIntegerValue();

  /**
   * Returns a new object of class '<em>Slot Integer Range</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Integer Range</em>'.
   * @generated
   */
  SlotIntegerRange createSlotIntegerRange();

  /**
   * Returns a new object of class '<em>Slot Integer Minimum Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Integer Minimum Value</em>'.
   * @generated
   */
  SlotIntegerMinimumValue createSlotIntegerMinimumValue();

  /**
   * Returns a new object of class '<em>Slot Integer Maximum Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Integer Maximum Value</em>'.
   * @generated
   */
  SlotIntegerMaximumValue createSlotIntegerMaximumValue();

  /**
   * Returns a new object of class '<em>Slot Decimal</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Decimal</em>'.
   * @generated
   */
  SlotDecimal createSlotDecimal();

  /**
   * Returns a new object of class '<em>Slot Decimal Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Decimal Value</em>'.
   * @generated
   */
  SlotDecimalValue createSlotDecimalValue();

  /**
   * Returns a new object of class '<em>Slot Decimal Range</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Decimal Range</em>'.
   * @generated
   */
  SlotDecimalRange createSlotDecimalRange();

  /**
   * Returns a new object of class '<em>Slot Decimal Minimum Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Decimal Minimum Value</em>'.
   * @generated
   */
  SlotDecimalMinimumValue createSlotDecimalMinimumValue();

  /**
   * Returns a new object of class '<em>Slot Decimal Maximum Value</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Slot Decimal Maximum Value</em>'.
   * @generated
   */
  SlotDecimalMaximumValue createSlotDecimalMaximumValue();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  EtlPackage getEtlPackage();

} //EtlFactory
