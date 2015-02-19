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
package com.b2international.snowowl.snomed.dsl.query.queryast;

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
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastFactory
 * @model kind="package"
 * @generated
 */
public interface ecoreastPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "queryast";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.b2international.com/snowowl/dsl/ast";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "ast";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ecoreastPackage eINSTANCE = com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.RValueImpl <em>RValue</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.RValueImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getRValue()
	 * @generated
	 */
	int RVALUE = 0;

	/**
	 * The number of structural features of the '<em>RValue</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int RVALUE_FEATURE_COUNT = 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.UnaryRValueImpl <em>Unary RValue</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.UnaryRValueImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getUnaryRValue()
	 * @generated
	 */
	int UNARY_RVALUE = 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNARY_RVALUE__VALUE = RVALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Unary RValue</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int UNARY_RVALUE_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.BinaryRValueImpl <em>Binary RValue</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.BinaryRValueImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getBinaryRValue()
	 * @generated
	 */
	int BINARY_RVALUE = 2;

	/**
	 * The feature id for the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_RVALUE__LEFT = RVALUE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_RVALUE__RIGHT = RVALUE_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Binary RValue</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int BINARY_RVALUE_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.ConceptRefImpl <em>Concept Ref</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ConceptRefImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getConceptRef()
	 * @generated
	 */
	int CONCEPT_REF = 3;

	/**
	 * The feature id for the '<em><b>Quantifier</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_REF__QUANTIFIER = RVALUE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Concept Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_REF__CONCEPT_ID = RVALUE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_REF__LABEL = RVALUE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Concept Ref</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CONCEPT_REF_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.RefSetImpl <em>Ref Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.RefSetImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getRefSet()
	 * @generated
	 */
	int REF_SET = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REF_SET__ID = RVALUE_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Ref Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int REF_SET_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NotClauseImpl <em>Not Clause</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.NotClauseImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getNotClause()
	 * @generated
	 */
	int NOT_CLAUSE = 5;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOT_CLAUSE__VALUE = UNARY_RVALUE__VALUE;

	/**
	 * The number of structural features of the '<em>Not Clause</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NOT_CLAUSE_FEATURE_COUNT = UNARY_RVALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.SubExpressionImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getSubExpression()
	 * @generated
	 */
	int SUB_EXPRESSION = 6;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_EXPRESSION__VALUE = UNARY_RVALUE__VALUE;

	/**
	 * The number of structural features of the '<em>Sub Expression</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SUB_EXPRESSION_FEATURE_COUNT = UNARY_RVALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.AndClauseImpl <em>And Clause</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.AndClauseImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getAndClause()
	 * @generated
	 */
	int AND_CLAUSE = 7;

	/**
	 * The feature id for the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AND_CLAUSE__LEFT = BINARY_RVALUE__LEFT;

	/**
	 * The feature id for the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AND_CLAUSE__RIGHT = BINARY_RVALUE__RIGHT;

	/**
	 * The number of structural features of the '<em>And Clause</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int AND_CLAUSE_FEATURE_COUNT = BINARY_RVALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.OrClauseImpl <em>Or Clause</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.OrClauseImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getOrClause()
	 * @generated
	 */
	int OR_CLAUSE = 8;

	/**
	 * The feature id for the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OR_CLAUSE__LEFT = BINARY_RVALUE__LEFT;

	/**
	 * The feature id for the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OR_CLAUSE__RIGHT = BINARY_RVALUE__RIGHT;

	/**
	 * The number of structural features of the '<em>Or Clause</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int OR_CLAUSE_FEATURE_COUNT = BINARY_RVALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseImpl <em>Attribute Clause</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getAttributeClause()
	 * @generated
	 */
	int ATTRIBUTE_CLAUSE = 9;

	/**
	 * The feature id for the '<em><b>Left</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CLAUSE__LEFT = BINARY_RVALUE__LEFT;

	/**
	 * The feature id for the '<em><b>Right</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CLAUSE__RIGHT = BINARY_RVALUE__RIGHT;

	/**
	 * The number of structural features of the '<em>Attribute Clause</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CLAUSE_FEATURE_COUNT = BINARY_RVALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseGroupImpl <em>Attribute Clause Group</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseGroupImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getAttributeClauseGroup()
	 * @generated
	 */
	int ATTRIBUTE_CLAUSE_GROUP = 10;

	/**
	 * The feature id for the '<em><b>Value</b></em>' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CLAUSE_GROUP__VALUE = UNARY_RVALUE__VALUE;

	/**
	 * The number of structural features of the '<em>Attribute Clause Group</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int ATTRIBUTE_CLAUSE_GROUP_FEATURE_COUNT = UNARY_RVALUE_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl <em>Numeric Data Clause</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getNumericDataClause()
	 * @generated
	 */
	int NUMERIC_DATA_CLAUSE = 11;

	/**
	 * The feature id for the '<em><b>Concepts</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_CLAUSE__CONCEPTS = RVALUE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Operator</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_CLAUSE__OPERATOR = RVALUE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_CLAUSE__VALUE = RVALUE_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Unit Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_CLAUSE__UNIT_TYPE = RVALUE_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Numeric Data Clause</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_CLAUSE_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl <em>Numeric Data Group Clause</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getNumericDataGroupClause()
	 * @generated
	 */
	int NUMERIC_DATA_GROUP_CLAUSE = 12;

	/**
	 * The feature id for the '<em><b>Concepts</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS = RVALUE_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Numeric Data</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA = RVALUE_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Substance</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE = RVALUE_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Numeric Data Group Clause</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int NUMERIC_DATA_GROUP_CLAUSE_FEATURE_COUNT = RVALUE_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier <em>Subsumption Quantifier</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getSubsumptionQuantifier()
	 * @generated
	 */
	int SUBSUMPTION_QUANTIFIER = 13;


	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.RValue <em>RValue</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>RValue</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.RValue
	 * @generated
	 */
	EClass getRValue();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue <em>Unary RValue</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Unary RValue</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue
	 * @generated
	 */
	EClass getUnaryRValue();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Value</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue#getValue()
	 * @see #getUnaryRValue()
	 * @generated
	 */
	EReference getUnaryRValue_Value();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue <em>Binary RValue</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Binary RValue</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue
	 * @generated
	 */
	EClass getBinaryRValue();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue#getLeft <em>Left</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Left</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue#getLeft()
	 * @see #getBinaryRValue()
	 * @generated
	 */
	EReference getBinaryRValue_Left();

	/**
	 * Returns the meta object for the containment reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue#getRight <em>Right</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference '<em>Right</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue#getRight()
	 * @see #getBinaryRValue()
	 * @generated
	 */
	EReference getBinaryRValue_Right();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef <em>Concept Ref</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Concept Ref</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef
	 * @generated
	 */
	EClass getConceptRef();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getQuantifier <em>Quantifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Quantifier</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getQuantifier()
	 * @see #getConceptRef()
	 * @generated
	 */
	EAttribute getConceptRef_Quantifier();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getConceptId <em>Concept Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Concept Id</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getConceptId()
	 * @see #getConceptRef()
	 * @generated
	 */
	EAttribute getConceptRef_ConceptId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef#getLabel()
	 * @see #getConceptRef()
	 * @generated
	 */
	EAttribute getConceptRef_Label();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.RefSet <em>Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.RefSet
	 * @generated
	 */
	EClass getRefSet();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.RefSet#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.RefSet#getId()
	 * @see #getRefSet()
	 * @generated
	 */
	EAttribute getRefSet_Id();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NotClause <em>Not Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Not Clause</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NotClause
	 * @generated
	 */
	EClass getNotClause();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression <em>Sub Expression</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Sub Expression</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression
	 * @generated
	 */
	EClass getSubExpression();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.AndClause <em>And Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>And Clause</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.AndClause
	 * @generated
	 */
	EClass getAndClause();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.OrClause <em>Or Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Or Clause</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.OrClause
	 * @generated
	 */
	EClass getOrClause();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause <em>Attribute Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute Clause</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause
	 * @generated
	 */
	EClass getAttributeClause();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup <em>Attribute Clause Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Attribute Clause Group</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup
	 * @generated
	 */
	EClass getAttributeClauseGroup();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause <em>Numeric Data Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Numeric Data Clause</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause
	 * @generated
	 */
	EClass getNumericDataClause();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getOperator <em>Operator</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operator</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getOperator()
	 * @see #getNumericDataClause()
	 * @generated
	 */
	EAttribute getNumericDataClause_Operator();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getValue()
	 * @see #getNumericDataClause()
	 * @generated
	 */
	EAttribute getNumericDataClause_Value();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getUnitType <em>Unit Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Unit Type</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getUnitType()
	 * @see #getNumericDataClause()
	 * @generated
	 */
	EAttribute getNumericDataClause_UnitType();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause <em>Numeric Data Group Clause</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Numeric Data Group Clause</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause
	 * @generated
	 */
	EClass getNumericDataGroupClause();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getConcepts <em>Concepts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Concepts</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getConcepts()
	 * @see #getNumericDataGroupClause()
	 * @generated
	 */
	EReference getNumericDataGroupClause_Concepts();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getNumericData <em>Numeric Data</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Numeric Data</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getNumericData()
	 * @see #getNumericDataGroupClause()
	 * @generated
	 */
	EReference getNumericDataGroupClause_NumericData();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getSubstance <em>Substance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Substance</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause#getSubstance()
	 * @see #getNumericDataGroupClause()
	 * @generated
	 */
	EReference getNumericDataGroupClause_Substance();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getConcepts <em>Concepts</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Concepts</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause#getConcepts()
	 * @see #getNumericDataClause()
	 * @generated
	 */
	EReference getNumericDataClause_Concepts();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier <em>Subsumption Quantifier</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Subsumption Quantifier</em>'.
	 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier
	 * @generated
	 */
	EEnum getSubsumptionQuantifier();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ecoreastFactory getecoreastFactory();

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
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.RValueImpl <em>RValue</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.RValueImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getRValue()
		 * @generated
		 */
		EClass RVALUE = eINSTANCE.getRValue();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.UnaryRValueImpl <em>Unary RValue</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.UnaryRValueImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getUnaryRValue()
		 * @generated
		 */
		EClass UNARY_RVALUE = eINSTANCE.getUnaryRValue();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference UNARY_RVALUE__VALUE = eINSTANCE.getUnaryRValue_Value();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.BinaryRValueImpl <em>Binary RValue</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.BinaryRValueImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getBinaryRValue()
		 * @generated
		 */
		EClass BINARY_RVALUE = eINSTANCE.getBinaryRValue();

		/**
		 * The meta object literal for the '<em><b>Left</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINARY_RVALUE__LEFT = eINSTANCE.getBinaryRValue_Left();

		/**
		 * The meta object literal for the '<em><b>Right</b></em>' containment reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference BINARY_RVALUE__RIGHT = eINSTANCE.getBinaryRValue_Right();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.ConceptRefImpl <em>Concept Ref</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ConceptRefImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getConceptRef()
		 * @generated
		 */
		EClass CONCEPT_REF = eINSTANCE.getConceptRef();

		/**
		 * The meta object literal for the '<em><b>Quantifier</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_REF__QUANTIFIER = eINSTANCE.getConceptRef_Quantifier();

		/**
		 * The meta object literal for the '<em><b>Concept Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_REF__CONCEPT_ID = eINSTANCE.getConceptRef_ConceptId();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CONCEPT_REF__LABEL = eINSTANCE.getConceptRef_Label();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.RefSetImpl <em>Ref Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.RefSetImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getRefSet()
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
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NotClauseImpl <em>Not Clause</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.NotClauseImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getNotClause()
		 * @generated
		 */
		EClass NOT_CLAUSE = eINSTANCE.getNotClause();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.SubExpressionImpl <em>Sub Expression</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.SubExpressionImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getSubExpression()
		 * @generated
		 */
		EClass SUB_EXPRESSION = eINSTANCE.getSubExpression();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.AndClauseImpl <em>And Clause</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.AndClauseImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getAndClause()
		 * @generated
		 */
		EClass AND_CLAUSE = eINSTANCE.getAndClause();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.OrClauseImpl <em>Or Clause</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.OrClauseImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getOrClause()
		 * @generated
		 */
		EClass OR_CLAUSE = eINSTANCE.getOrClause();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseImpl <em>Attribute Clause</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getAttributeClause()
		 * @generated
		 */
		EClass ATTRIBUTE_CLAUSE = eINSTANCE.getAttributeClause();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseGroupImpl <em>Attribute Clause Group</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.AttributeClauseGroupImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getAttributeClauseGroup()
		 * @generated
		 */
		EClass ATTRIBUTE_CLAUSE_GROUP = eINSTANCE.getAttributeClauseGroup();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl <em>Numeric Data Clause</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getNumericDataClause()
		 * @generated
		 */
		EClass NUMERIC_DATA_CLAUSE = eINSTANCE.getNumericDataClause();

		/**
		 * The meta object literal for the '<em><b>Operator</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_DATA_CLAUSE__OPERATOR = eINSTANCE.getNumericDataClause_Operator();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_DATA_CLAUSE__VALUE = eINSTANCE.getNumericDataClause_Value();

		/**
		 * The meta object literal for the '<em><b>Unit Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute NUMERIC_DATA_CLAUSE__UNIT_TYPE = eINSTANCE.getNumericDataClause_UnitType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl <em>Numeric Data Group Clause</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getNumericDataGroupClause()
		 * @generated
		 */
		EClass NUMERIC_DATA_GROUP_CLAUSE = eINSTANCE.getNumericDataGroupClause();

		/**
		 * The meta object literal for the '<em><b>Concepts</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS = eINSTANCE.getNumericDataGroupClause_Concepts();

		/**
		 * The meta object literal for the '<em><b>Numeric Data</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA = eINSTANCE.getNumericDataGroupClause_NumericData();

		/**
		 * The meta object literal for the '<em><b>Substance</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE = eINSTANCE.getNumericDataGroupClause_Substance();

		/**
		 * The meta object literal for the '<em><b>Concepts</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference NUMERIC_DATA_CLAUSE__CONCEPTS = eINSTANCE.getNumericDataClause_Concepts();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier <em>Subsumption Quantifier</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.SubsumptionQuantifier
		 * @see com.b2international.snowowl.snomed.dsl.query.queryast.impl.ecoreastPackageImpl#getSubsumptionQuantifier()
		 * @generated
		 */
		EEnum SUBSUMPTION_QUANTIFIER = eINSTANCE.getSubsumptionQuantifier();

	}

} //ecoreastPackage