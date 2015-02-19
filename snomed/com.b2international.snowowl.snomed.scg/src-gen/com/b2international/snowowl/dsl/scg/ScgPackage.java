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
package com.b2international.snowowl.dsl.scg;

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
 * @see com.b2international.snowowl.dsl.scg.ScgFactory
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
  String eNS_URI = "http://www.b2international.com/snowowl/SCG";

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
  ScgPackage eINSTANCE = com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl.init();

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.scg.impl.AttributeValueImpl <em>Attribute Value</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.scg.impl.AttributeValueImpl
   * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getAttributeValue()
   * @generated
   */
  int ATTRIBUTE_VALUE = 4;

  /**
   * The number of structural features of the '<em>Attribute Value</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int ATTRIBUTE_VALUE_FEATURE_COUNT = 0;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.scg.impl.ExpressionImpl <em>Expression</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.scg.impl.ExpressionImpl
   * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getExpression()
   * @generated
   */
  int EXPRESSION = 0;

  /**
   * The feature id for the '<em><b>Concepts</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__CONCEPTS = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__ATTRIBUTES = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Groups</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION__GROUPS = ATTRIBUTE_VALUE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Expression</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int EXPRESSION_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.scg.impl.ConceptImpl <em>Concept</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.scg.impl.ConceptImpl
   * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getConcept()
   * @generated
   */
  int CONCEPT = 1;

  /**
   * The feature id for the '<em><b>Id</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT__ID = ATTRIBUTE_VALUE_FEATURE_COUNT + 0;

  /**
   * The feature id for the '<em><b>Term</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT__TERM = ATTRIBUTE_VALUE_FEATURE_COUNT + 1;

  /**
   * The feature id for the '<em><b>Super Types</b></em>' attribute list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT__SUPER_TYPES = ATTRIBUTE_VALUE_FEATURE_COUNT + 2;

  /**
   * The number of structural features of the '<em>Concept</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int CONCEPT_FEATURE_COUNT = ATTRIBUTE_VALUE_FEATURE_COUNT + 3;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.scg.impl.GroupImpl <em>Group</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.scg.impl.GroupImpl
   * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getGroup()
   * @generated
   */
  int GROUP = 2;

  /**
   * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP__ATTRIBUTES = 0;

  /**
   * The number of structural features of the '<em>Group</em>' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   * @ordered
   */
  int GROUP_FEATURE_COUNT = 1;

  /**
   * The meta object id for the '{@link com.b2international.snowowl.dsl.scg.impl.AttributeImpl <em>Attribute</em>}' class.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see com.b2international.snowowl.dsl.scg.impl.AttributeImpl
   * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getAttribute()
   * @generated
   */
  int ATTRIBUTE = 3;

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
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.scg.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Expression</em>'.
   * @see com.b2international.snowowl.dsl.scg.Expression
   * @generated
   */
  EClass getExpression();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.scg.Expression#getConcepts <em>Concepts</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Concepts</em>'.
   * @see com.b2international.snowowl.dsl.scg.Expression#getConcepts()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Concepts();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.scg.Expression#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.dsl.scg.Expression#getAttributes()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Attributes();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.scg.Expression#getGroups <em>Groups</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Groups</em>'.
   * @see com.b2international.snowowl.dsl.scg.Expression#getGroups()
   * @see #getExpression()
   * @generated
   */
  EReference getExpression_Groups();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.scg.Concept <em>Concept</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Concept</em>'.
   * @see com.b2international.snowowl.dsl.scg.Concept
   * @generated
   */
  EClass getConcept();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.scg.Concept#getId <em>Id</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Id</em>'.
   * @see com.b2international.snowowl.dsl.scg.Concept#getId()
   * @see #getConcept()
   * @generated
   */
  EAttribute getConcept_Id();

  /**
   * Returns the meta object for the attribute '{@link com.b2international.snowowl.dsl.scg.Concept#getTerm <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute '<em>Term</em>'.
   * @see com.b2international.snowowl.dsl.scg.Concept#getTerm()
   * @see #getConcept()
   * @generated
   */
  EAttribute getConcept_Term();

  /**
   * Returns the meta object for the attribute list '{@link com.b2international.snowowl.dsl.scg.Concept#getSuperTypes <em>Super Types</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the attribute list '<em>Super Types</em>'.
   * @see com.b2international.snowowl.dsl.scg.Concept#getSuperTypes()
   * @see #getConcept()
   * @generated
   */
  EAttribute getConcept_SuperTypes();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.scg.Group <em>Group</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Group</em>'.
   * @see com.b2international.snowowl.dsl.scg.Group
   * @generated
   */
  EClass getGroup();

  /**
   * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.dsl.scg.Group#getAttributes <em>Attributes</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference list '<em>Attributes</em>'.
   * @see com.b2international.snowowl.dsl.scg.Group#getAttributes()
   * @see #getGroup()
   * @generated
   */
  EReference getGroup_Attributes();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.scg.Attribute <em>Attribute</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute</em>'.
   * @see com.b2international.snowowl.dsl.scg.Attribute
   * @generated
   */
  EClass getAttribute();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.scg.Attribute#getName <em>Name</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Name</em>'.
   * @see com.b2international.snowowl.dsl.scg.Attribute#getName()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Name();

  /**
   * Returns the meta object for the containment reference '{@link com.b2international.snowowl.dsl.scg.Attribute#getValue <em>Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for the containment reference '<em>Value</em>'.
   * @see com.b2international.snowowl.dsl.scg.Attribute#getValue()
   * @see #getAttribute()
   * @generated
   */
  EReference getAttribute_Value();

  /**
   * Returns the meta object for class '{@link com.b2international.snowowl.dsl.scg.AttributeValue <em>Attribute Value</em>}'.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the meta object for class '<em>Attribute Value</em>'.
   * @see com.b2international.snowowl.dsl.scg.AttributeValue
   * @generated
   */
  EClass getAttributeValue();

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
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.scg.impl.ExpressionImpl <em>Expression</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.scg.impl.ExpressionImpl
     * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getExpression()
     * @generated
     */
    EClass EXPRESSION = eINSTANCE.getExpression();

    /**
     * The meta object literal for the '<em><b>Concepts</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__CONCEPTS = eINSTANCE.getExpression_Concepts();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__ATTRIBUTES = eINSTANCE.getExpression_Attributes();

    /**
     * The meta object literal for the '<em><b>Groups</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference EXPRESSION__GROUPS = eINSTANCE.getExpression_Groups();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.scg.impl.ConceptImpl <em>Concept</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.scg.impl.ConceptImpl
     * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getConcept()
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
     * The meta object literal for the '<em><b>Super Types</b></em>' attribute list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EAttribute CONCEPT__SUPER_TYPES = eINSTANCE.getConcept_SuperTypes();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.scg.impl.GroupImpl <em>Group</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.scg.impl.GroupImpl
     * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getGroup()
     * @generated
     */
    EClass GROUP = eINSTANCE.getGroup();

    /**
     * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    EReference GROUP__ATTRIBUTES = eINSTANCE.getGroup_Attributes();

    /**
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.scg.impl.AttributeImpl <em>Attribute</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.scg.impl.AttributeImpl
     * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getAttribute()
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
     * The meta object literal for the '{@link com.b2international.snowowl.dsl.scg.impl.AttributeValueImpl <em>Attribute Value</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see com.b2international.snowowl.dsl.scg.impl.AttributeValueImpl
     * @see com.b2international.snowowl.dsl.scg.impl.ScgPackageImpl#getAttributeValue()
     * @generated
     */
    EClass ATTRIBUTE_VALUE = eINSTANCE.getAttributeValue();

  }

} //ScgPackage