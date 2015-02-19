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
package com.b2international.snowowl.snomed.dsl.query.queryast.impl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Numeric Data Clause</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl#getConcepts <em>Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl#getOperator <em>Operator</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl#getValue <em>Value</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataClauseImpl#getUnitType <em>Unit Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NumericDataClauseImpl extends RValueImpl implements NumericDataClause {
	/**
	 * The cached value of the '{@link #getConcepts() <em>Concepts</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getConcepts()
	 * @generated
	 * @ordered
	 */
	protected RValue concepts;

	/**
	 * The default value of the '{@link #getOperator() <em>Operator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperator()
	 * @generated
	 * @ordered
	 */
	protected static final String OPERATOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getOperator() <em>Operator</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOperator()
	 * @generated
	 * @ordered
	 */
	protected String operator = OPERATOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected static final int VALUE_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected int value = VALUE_EDEFAULT;

	/**
	 * The default value of the '{@link #getUnitType() <em>Unit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnitType()
	 * @generated
	 * @ordered
	 */
	protected static final String UNIT_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getUnitType() <em>Unit Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getUnitType()
	 * @generated
	 * @ordered
	 */
	protected String unitType = UNIT_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NumericDataClauseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ecoreastPackage.Literals.NUMERIC_DATA_CLAUSE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOperator() {
		return operator;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOperator(String newOperator) {
		String oldOperator = operator;
		operator = newOperator;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_CLAUSE__OPERATOR, oldOperator, operator));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(int newValue) {
		int oldValue = value;
		value = newValue;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_CLAUSE__VALUE, oldValue, value));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUnitType() {
		return unitType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUnitType(String newUnitType) {
		String oldUnitType = unitType;
		unitType = newUnitType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_CLAUSE__UNIT_TYPE, oldUnitType, unitType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue getConcepts() {
		if (concepts != null && concepts.eIsProxy()) {
			InternalEObject oldConcepts = (InternalEObject)concepts;
			concepts = (RValue)eResolveProxy(oldConcepts);
			if (concepts != oldConcepts) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ecoreastPackage.NUMERIC_DATA_CLAUSE__CONCEPTS, oldConcepts, concepts));
			}
		}
		return concepts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue basicGetConcepts() {
		return concepts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConcepts(RValue newConcepts) {
		RValue oldConcepts = concepts;
		concepts = newConcepts;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_CLAUSE__CONCEPTS, oldConcepts, concepts));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__CONCEPTS:
				if (resolve) return getConcepts();
				return basicGetConcepts();
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__OPERATOR:
				return getOperator();
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__VALUE:
				return getValue();
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__UNIT_TYPE:
				return getUnitType();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__CONCEPTS:
				setConcepts((RValue)newValue);
				return;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__OPERATOR:
				setOperator((String)newValue);
				return;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__VALUE:
				setValue((Integer)newValue);
				return;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__UNIT_TYPE:
				setUnitType((String)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__CONCEPTS:
				setConcepts((RValue)null);
				return;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__OPERATOR:
				setOperator(OPERATOR_EDEFAULT);
				return;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__VALUE:
				setValue(VALUE_EDEFAULT);
				return;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__UNIT_TYPE:
				setUnitType(UNIT_TYPE_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__CONCEPTS:
				return concepts != null;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__OPERATOR:
				return OPERATOR_EDEFAULT == null ? operator != null : !OPERATOR_EDEFAULT.equals(operator);
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__VALUE:
				return value != VALUE_EDEFAULT;
			case ecoreastPackage.NUMERIC_DATA_CLAUSE__UNIT_TYPE:
				return UNIT_TYPE_EDEFAULT == null ? unitType != null : !UNIT_TYPE_EDEFAULT.equals(unitType);
		}
		return super.eIsSet(featureID);
	}

	@Override
	public StringBuilder toString(StringBuilder buf) {
		buf.append(" HAS STRENGTH");
		buf.append(" ");
		buf.append(operator);
		buf.append(" ");
		buf.append(value);
		buf.append(" ");
		buf.append(unitType);
		return buf;
	}

} //NumericDataClauseImpl