/**
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.ecl.ecl.EclPackage
 * @generated
 */
public interface EclFactory extends EFactory
{
  /**
   * The singleton instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  EclFactory eINSTANCE = com.b2international.snowowl.snomed.ecl.ecl.impl.EclFactoryImpl.init();

  /**
   * Returns a new object of class '<em>Script</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Script</em>'.
   * @generated
   */
  Script createScript();

  /**
   * Returns a new object of class '<em>Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Expression Constraint</em>'.
   * @generated
   */
  ExpressionConstraint createExpressionConstraint();

  /**
   * Returns a new object of class '<em>Child Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Child Of</em>'.
   * @generated
   */
  ChildOf createChildOf();

  /**
   * Returns a new object of class '<em>Descendant Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Descendant Of</em>'.
   * @generated
   */
  DescendantOf createDescendantOf();

  /**
   * Returns a new object of class '<em>Descendant Or Self Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Descendant Or Self Of</em>'.
   * @generated
   */
  DescendantOrSelfOf createDescendantOrSelfOf();

  /**
   * Returns a new object of class '<em>Parent Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Parent Of</em>'.
   * @generated
   */
  ParentOf createParentOf();

  /**
   * Returns a new object of class '<em>Ancestor Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Ancestor Of</em>'.
   * @generated
   */
  AncestorOf createAncestorOf();

  /**
   * Returns a new object of class '<em>Ancestor Or Self Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Ancestor Or Self Of</em>'.
   * @generated
   */
  AncestorOrSelfOf createAncestorOrSelfOf();

  /**
   * Returns a new object of class '<em>Member Of</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Member Of</em>'.
   * @generated
   */
  MemberOf createMemberOf();

  /**
   * Returns a new object of class '<em>Concept Reference</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Concept Reference</em>'.
   * @generated
   */
  ConceptReference createConceptReference();

  /**
   * Returns a new object of class '<em>Any</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Any</em>'.
   * @generated
   */
  Any createAny();

  /**
   * Returns a new object of class '<em>Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Refinement</em>'.
   * @generated
   */
  Refinement createRefinement();

  /**
   * Returns a new object of class '<em>Nested Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nested Refinement</em>'.
   * @generated
   */
  NestedRefinement createNestedRefinement();

  /**
   * Returns a new object of class '<em>Attribute Group</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Group</em>'.
   * @generated
   */
  AttributeGroup createAttributeGroup();

  /**
   * Returns a new object of class '<em>Attribute Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Constraint</em>'.
   * @generated
   */
  AttributeConstraint createAttributeConstraint();

  /**
   * Returns a new object of class '<em>Cardinality</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Cardinality</em>'.
   * @generated
   */
  Cardinality createCardinality();

  /**
   * Returns a new object of class '<em>Comparison</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Comparison</em>'.
   * @generated
   */
  Comparison createComparison();

  /**
   * Returns a new object of class '<em>Attribute Comparison</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Comparison</em>'.
   * @generated
   */
  AttributeComparison createAttributeComparison();

  /**
   * Returns a new object of class '<em>Data Type Comparison</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Data Type Comparison</em>'.
   * @generated
   */
  DataTypeComparison createDataTypeComparison();

  /**
   * Returns a new object of class '<em>Attribute Value Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Value Equals</em>'.
   * @generated
   */
  AttributeValueEquals createAttributeValueEquals();

  /**
   * Returns a new object of class '<em>Attribute Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Attribute Value Not Equals</em>'.
   * @generated
   */
  AttributeValueNotEquals createAttributeValueNotEquals();

  /**
   * Returns a new object of class '<em>String Value Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Value Equals</em>'.
   * @generated
   */
  StringValueEquals createStringValueEquals();

  /**
   * Returns a new object of class '<em>String Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>String Value Not Equals</em>'.
   * @generated
   */
  StringValueNotEquals createStringValueNotEquals();

  /**
   * Returns a new object of class '<em>Integer Value Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value Equals</em>'.
   * @generated
   */
  IntegerValueEquals createIntegerValueEquals();

  /**
   * Returns a new object of class '<em>Integer Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value Not Equals</em>'.
   * @generated
   */
  IntegerValueNotEquals createIntegerValueNotEquals();

  /**
   * Returns a new object of class '<em>Integer Value Greater Than</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value Greater Than</em>'.
   * @generated
   */
  IntegerValueGreaterThan createIntegerValueGreaterThan();

  /**
   * Returns a new object of class '<em>Integer Value Less Than</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value Less Than</em>'.
   * @generated
   */
  IntegerValueLessThan createIntegerValueLessThan();

  /**
   * Returns a new object of class '<em>Integer Value Greater Than Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value Greater Than Equals</em>'.
   * @generated
   */
  IntegerValueGreaterThanEquals createIntegerValueGreaterThanEquals();

  /**
   * Returns a new object of class '<em>Integer Value Less Than Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Integer Value Less Than Equals</em>'.
   * @generated
   */
  IntegerValueLessThanEquals createIntegerValueLessThanEquals();

  /**
   * Returns a new object of class '<em>Decimal Value Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value Equals</em>'.
   * @generated
   */
  DecimalValueEquals createDecimalValueEquals();

  /**
   * Returns a new object of class '<em>Decimal Value Not Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value Not Equals</em>'.
   * @generated
   */
  DecimalValueNotEquals createDecimalValueNotEquals();

  /**
   * Returns a new object of class '<em>Decimal Value Greater Than</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value Greater Than</em>'.
   * @generated
   */
  DecimalValueGreaterThan createDecimalValueGreaterThan();

  /**
   * Returns a new object of class '<em>Decimal Value Less Than</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value Less Than</em>'.
   * @generated
   */
  DecimalValueLessThan createDecimalValueLessThan();

  /**
   * Returns a new object of class '<em>Decimal Value Greater Than Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value Greater Than Equals</em>'.
   * @generated
   */
  DecimalValueGreaterThanEquals createDecimalValueGreaterThanEquals();

  /**
   * Returns a new object of class '<em>Decimal Value Less Than Equals</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Decimal Value Less Than Equals</em>'.
   * @generated
   */
  DecimalValueLessThanEquals createDecimalValueLessThanEquals();

  /**
   * Returns a new object of class '<em>Nested Expression</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Nested Expression</em>'.
   * @generated
   */
  NestedExpression createNestedExpression();

  /**
   * Returns a new object of class '<em>Or Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Expression Constraint</em>'.
   * @generated
   */
  OrExpressionConstraint createOrExpressionConstraint();

  /**
   * Returns a new object of class '<em>And Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Expression Constraint</em>'.
   * @generated
   */
  AndExpressionConstraint createAndExpressionConstraint();

  /**
   * Returns a new object of class '<em>Exclusion Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Exclusion Expression Constraint</em>'.
   * @generated
   */
  ExclusionExpressionConstraint createExclusionExpressionConstraint();

  /**
   * Returns a new object of class '<em>Refined Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Refined Expression Constraint</em>'.
   * @generated
   */
  RefinedExpressionConstraint createRefinedExpressionConstraint();

  /**
   * Returns a new object of class '<em>Dotted Expression Constraint</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Dotted Expression Constraint</em>'.
   * @generated
   */
  DottedExpressionConstraint createDottedExpressionConstraint();

  /**
   * Returns a new object of class '<em>Or Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>Or Refinement</em>'.
   * @generated
   */
  OrRefinement createOrRefinement();

  /**
   * Returns a new object of class '<em>And Refinement</em>'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return a new object of class '<em>And Refinement</em>'.
   * @generated
   */
  AndRefinement createAndRefinement();

  /**
   * Returns the package supported by this factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the package supported by this factory.
   * @generated
   */
  EclPackage getEclPackage();

} //EclFactory
