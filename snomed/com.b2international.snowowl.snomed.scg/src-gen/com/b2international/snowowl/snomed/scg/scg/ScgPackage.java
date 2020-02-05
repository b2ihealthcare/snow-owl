/**
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.scg.scg;

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
 * @see com.b2international.snowowl.snomed.scg.scg.ScgFactory
 * @model kind="package"
 * @generated
 */
public interface ScgPackage extends EPackage
{
  /**
   * The package name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNAME = "scg";

  /**
   * The package namespace URI.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_URI = "http://www.b2international.com/snowowl/snomed/scg/Scg";

  /**
   * The package namespace name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  String eNS_PREFIX = "scg";

  /**
   * The singleton instance of the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  ScgPackage eINSTANCE = com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.ExpressionImpl <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ExpressionImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 0;

  /**
   * The feature id for the '<em><b>Primitive</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__PRIMITIVE = 0;

  /**
   * The feature id for the '<em><b>Expression</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__EXPRESSION = 1;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.AttributeValueImpl <em>Attribute Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.AttributeValueImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getAttributeValue()
   * @generated
   */
  int ATTRIBUTE_VALUE = 5;

  /**
   * The number of structural features of the '<em>Attribute Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.SubExpressionImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getSubExpression()
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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.RefinementImpl <em>Refinement</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.RefinementImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getRefinement()
   * @generated
   */
  int REFINEMENT = 2;

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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.AttributeGroupImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getAttributeGroup()
   * @generated
   */
  int ATTRIBUTE_GROUP = 3;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP__ATTRIBUTES = 0;

  /**
   * The number of structural features of the '<em>Attribute Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_GROUP_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.AttributeImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 4;

  /**
   * The feature id for the '<em><b>Name</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__NAME = 0;

  /**
   * The feature id for the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE__VALUE = 1;

  /**
   * The number of structural features of the '<em>Attribute</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_FEATURE_COUNT = 2;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.StringValueImpl <em>String Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.StringValueImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getStringValue()
   * @generated
   */
  int STRING_VALUE = 6;

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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.IntegerValueImpl <em>Integer Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.IntegerValueImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getIntegerValue()
   * @generated
   */
  int INTEGER_VALUE = 7;

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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.DecimalValueImpl <em>Decimal Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.DecimalValueImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getDecimalValue()
   * @generated
   */
  int DECIMAL_VALUE = 8;

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
   * The meta object id for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ConceptReferenceImpl
   * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getConceptReference()
   * @generated
   */
  int CONCEPT_REFERENCE = 9;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__ID = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE__TERM = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The number of structural features of the '<em>Concept Reference</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_REFERENCE_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 2;


  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Expression
   * @generated
   */
  EClass getExpression();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.scg.scg.Expression#isPrimitive <em>Primitive</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Primitive</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Expression#isPrimitive()
   * @see #getExpression()
   * @generated
   */
  EAttribute getExpression_Primitive();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.scg.scg.Expression#getExpression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Expression</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Expression#getExpression()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Expression();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.SubExpression <em>Sub Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Sub Expression</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.SubExpression
   * @generated
   */
  EClass getSubExpression();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.scg.scg.SubExpression#getFocusConcepts <em>Focus Concepts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Focus Concepts</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.SubExpression#getFocusConcepts()
   * @see #getSubExpression()
   * @generated
   */
  EReference getSubExpression_FocusConcepts();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.scg.scg.SubExpression#getRefinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.SubExpression#getRefinement()
   * @see #getSubExpression()
   * @generated
   */
  EReference getSubExpression_Refinement();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.Refinement <em>Refinement</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Refinement</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Refinement
   * @generated
   */
  EClass getRefinement();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.scg.scg.Refinement#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Refinement#getAttributes()
   * @see #getRefinement()
   * @generated
   */
  EReference getRefinement_Attributes();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.scg.scg.Refinement#getGroups <em>Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Groups</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Refinement#getGroups()
   * @see #getRefinement()
   * @generated
   */
  EReference getRefinement_Groups();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.AttributeGroup <em>Attribute Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Group</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.AttributeGroup
   * @generated
   */
  EClass getAttributeGroup();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.scg.scg.AttributeGroup#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.AttributeGroup#getAttributes()
   * @see #getAttributeGroup()
   * @generated
   */
  EReference getAttributeGroup_Attributes();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Attribute
   * @generated
   */
  EClass getAttribute();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.scg.scg.Attribute#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Attribute#getName()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Name();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.scg.scg.Attribute#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.Attribute#getValue()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.AttributeValue <em>Attribute Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.AttributeValue
   * @generated
   */
  EClass getAttributeValue();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.StringValue <em>String Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>String Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.StringValue
   * @generated
   */
  EClass getStringValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.scg.scg.StringValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.StringValue#getValue()
   * @see #getStringValue()
   * @generated
   */
  EAttribute getStringValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.IntegerValue <em>Integer Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Integer Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.IntegerValue
   * @generated
   */
  EClass getIntegerValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.scg.scg.IntegerValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.IntegerValue#getValue()
   * @see #getIntegerValue()
   * @generated
   */
  EAttribute getIntegerValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.DecimalValue <em>Decimal Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Decimal Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.DecimalValue
   * @generated
   */
  EClass getDecimalValue();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.scg.scg.DecimalValue#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Value</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.DecimalValue#getValue()
   * @see #getDecimalValue()
   * @generated
   */
  EAttribute getDecimalValue_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.snomed.scg.scg.ConceptReference <em>Concept Reference</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept Reference</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.ConceptReference
   * @generated
   */
  EClass getConceptReference();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.scg.scg.ConceptReference#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.ConceptReference#getId()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.scg.scg.ConceptReference#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.snomed.scg.scg.ConceptReference#getTerm()
   * @see #getConceptReference()
   * @generated
   */
  EAttribute getConceptReference_Term();

  /**
   * Returns the factory that creates the instances of the model.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the factory that creates the instances of the model.
   * @generated
   */
  ScgFactory getScgFactory();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.ExpressionImpl <em>Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ExpressionImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getExpression()
     * @generated
     */
    EClass EXPRESSION = eINSTANCE.getExpression();

    /**
     * The meta object literal for the '<em><b>Primitive</b></em>' attribute feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute EXPRESSION__PRIMITIVE = eINSTANCE.getExpression_Primitive();

    /**
     * The meta object literal for the '<em><b>Expression</b></em>' containment reference feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__EXPRESSION = eINSTANCE.getExpression_Expression();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.SubExpressionImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getSubExpression()
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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.RefinementImpl <em>Refinement</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.RefinementImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getRefinement()
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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.AttributeGroupImpl <em>Attribute Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.AttributeGroupImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getAttributeGroup()
     * @generated
     */
    EClass ATTRIBUTE_GROUP = eINSTANCE.getAttributeGroup();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference ATTRIBUTE_GROUP__ATTRIBUTES = eINSTANCE.getAttributeGroup_Attributes();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.AttributeImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getAttribute()
     * @generated
     */
    EClass ATTRIBUTE = eINSTANCE.getAttribute();

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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.AttributeValueImpl <em>Attribute Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.AttributeValueImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getAttributeValue()
     * @generated
     */
    EClass ATTRIBUTE_VALUE = eINSTANCE.getAttributeValue();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.StringValueImpl <em>String Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.StringValueImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getStringValue()
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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.IntegerValueImpl <em>Integer Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.IntegerValueImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getIntegerValue()
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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.DecimalValueImpl <em>Decimal Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.DecimalValueImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getDecimalValue()
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
     * The meta object literal for the '{@link com.b2international.snowowl.snomed.scg.scg.impl.ConceptReferenceImpl <em>Concept Reference</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ConceptReferenceImpl
     * @see com.b2international.snowowl.snomed.scg.scg.impl.ScgPackageImpl#getConceptReference()
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

  }

} //ScgPackage
