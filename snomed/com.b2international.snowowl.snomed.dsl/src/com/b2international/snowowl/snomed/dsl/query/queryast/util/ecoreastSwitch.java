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
package com.b2international.snowowl.snomed.dsl.query.queryast.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.util.Switch;

import com.b2international.snowowl.snomed.dsl.query.queryast.AndClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.AttributeClauseGroup;
import com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ConceptRef;
import com.b2international.snowowl.snomed.dsl.query.queryast.NotClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.OrClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.RefSet;
import com.b2international.snowowl.snomed.dsl.query.queryast.SubExpression;
import com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage
 * @generated
 */
public class ecoreastSwitch<T> extends Switch<T> {
	/**
	 * The cached model package
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected static ecoreastPackage modelPackage;

	/**
	 * Creates an instance of the switch.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ecoreastSwitch() {
		if (modelPackage == null) {
			modelPackage = ecoreastPackage.eINSTANCE;
		}
	}

	/**
	 * Checks whether this is a switch for the given package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @parameter ePackage the package in question.
	 * @return whether this is a switch for the given package.
	 * @generated
	 */
	@Override
	protected boolean isSwitchFor(EPackage ePackage) {
		return ePackage == modelPackage;
	}

	/**
	 * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the first non-null result returned by a <code>caseXXX</code> call.
	 * @generated
	 */
	@Override
	protected T doSwitch(int classifierID, EObject theEObject) {
		switch (classifierID) {
			case ecoreastPackage.RVALUE: {
				RValue rValue = (RValue)theEObject;
				T result = caseRValue(rValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.UNARY_RVALUE: {
				UnaryRValue unaryRValue = (UnaryRValue)theEObject;
				T result = caseUnaryRValue(unaryRValue);
				if (result == null) result = caseRValue(unaryRValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.BINARY_RVALUE: {
				BinaryRValue binaryRValue = (BinaryRValue)theEObject;
				T result = caseBinaryRValue(binaryRValue);
				if (result == null) result = caseRValue(binaryRValue);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.CONCEPT_REF: {
				ConceptRef conceptRef = (ConceptRef)theEObject;
				T result = caseConceptRef(conceptRef);
				if (result == null) result = caseRValue(conceptRef);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.REF_SET: {
				RefSet refSet = (RefSet)theEObject;
				T result = caseRefSet(refSet);
				if (result == null) result = caseRValue(refSet);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.NOT_CLAUSE: {
				NotClause notClause = (NotClause)theEObject;
				T result = caseNotClause(notClause);
				if (result == null) result = caseUnaryRValue(notClause);
				if (result == null) result = caseRValue(notClause);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.SUB_EXPRESSION: {
				SubExpression subExpression = (SubExpression)theEObject;
				T result = caseSubExpression(subExpression);
				if (result == null) result = caseUnaryRValue(subExpression);
				if (result == null) result = caseRValue(subExpression);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.AND_CLAUSE: {
				AndClause andClause = (AndClause)theEObject;
				T result = caseAndClause(andClause);
				if (result == null) result = caseBinaryRValue(andClause);
				if (result == null) result = caseRValue(andClause);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.OR_CLAUSE: {
				OrClause orClause = (OrClause)theEObject;
				T result = caseOrClause(orClause);
				if (result == null) result = caseBinaryRValue(orClause);
				if (result == null) result = caseRValue(orClause);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.ATTRIBUTE_CLAUSE: {
				AttributeClause attributeClause = (AttributeClause)theEObject;
				T result = caseAttributeClause(attributeClause);
				if (result == null) result = caseBinaryRValue(attributeClause);
				if (result == null) result = caseRValue(attributeClause);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.ATTRIBUTE_CLAUSE_GROUP: {
				AttributeClauseGroup attributeClauseGroup = (AttributeClauseGroup)theEObject;
				T result = caseAttributeClauseGroup(attributeClauseGroup);
				if (result == null) result = caseUnaryRValue(attributeClauseGroup);
				if (result == null) result = caseRValue(attributeClauseGroup);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.NUMERIC_DATA_CLAUSE: {
				NumericDataClause numericDataClause = (NumericDataClause)theEObject;
				T result = caseNumericDataClause(numericDataClause);
				if (result == null) result = caseRValue(numericDataClause);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE: {
				NumericDataGroupClause numericDataGroupClause = (NumericDataGroupClause)theEObject;
				T result = caseNumericDataGroupClause(numericDataGroupClause);
				if (result == null) result = caseRValue(numericDataGroupClause);
				if (result == null) result = defaultCase(theEObject);
				return result;
			}
			default: return defaultCase(theEObject);
		}
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>RValue</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>RValue</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRValue(RValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Unary RValue</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Unary RValue</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseUnaryRValue(UnaryRValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Binary RValue</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Binary RValue</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseBinaryRValue(BinaryRValue object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Concept Ref</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Concept Ref</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseConceptRef(ConceptRef object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Ref Set</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseRefSet(RefSet object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Not Clause</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Not Clause</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNotClause(NotClause object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Sub Expression</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Sub Expression</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseSubExpression(SubExpression object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>And Clause</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>And Clause</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAndClause(AndClause object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Or Clause</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Or Clause</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseOrClause(OrClause object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute Clause</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute Clause</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributeClause(AttributeClause object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Attribute Clause Group</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Attribute Clause Group</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseAttributeClauseGroup(AttributeClauseGroup object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Numeric Data Clause</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Numeric Data Clause</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNumericDataClause(NumericDataClause object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>Numeric Data Group Clause</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>Numeric Data Group Clause</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
	 * @generated
	 */
	public T caseNumericDataGroupClause(NumericDataGroupClause object) {
		return null;
	}

	/**
	 * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * <!-- begin-user-doc -->
	 * This implementation returns null;
	 * returning a non-null result will terminate the switch, but this is the last case anyway.
	 * <!-- end-user-doc -->
	 * @param object the target of the switch.
	 * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
	 * @see #doSwitch(org.eclipse.emf.ecore.EObject)
	 * @generated
	 */
	@Override
	public T defaultCase(EObject object) {
		return null;
	}

} //ecoreastSwitch