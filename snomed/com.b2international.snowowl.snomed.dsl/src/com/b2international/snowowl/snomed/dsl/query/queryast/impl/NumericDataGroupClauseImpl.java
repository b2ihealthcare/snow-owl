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
import com.b2international.snowowl.snomed.dsl.query.queryast.NumericDataGroupClause;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Numeric Data Group Clause</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl#getConcepts <em>Concepts</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl#getNumericData <em>Numeric Data</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.NumericDataGroupClauseImpl#getSubstance <em>Substance</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NumericDataGroupClauseImpl extends RValueImpl implements NumericDataGroupClause {
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
	 * The cached value of the '{@link #getNumericData() <em>Numeric Data</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNumericData()
	 * @generated
	 * @ordered
	 */
	protected NumericDataClause numericData;

	/**
	 * The cached value of the '{@link #getSubstance() <em>Substance</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubstance()
	 * @generated
	 * @ordered
	 */
	protected RValue substance;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NumericDataGroupClauseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ecoreastPackage.Literals.NUMERIC_DATA_GROUP_CLAUSE;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS, oldConcepts, concepts));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS, oldConcepts, concepts));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericDataClause getNumericData() {
		if (numericData != null && numericData.eIsProxy()) {
			InternalEObject oldNumericData = (InternalEObject)numericData;
			numericData = (NumericDataClause)eResolveProxy(oldNumericData);
			if (numericData != oldNumericData) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA, oldNumericData, numericData));
			}
		}
		return numericData;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NumericDataClause basicGetNumericData() {
		return numericData;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNumericData(NumericDataClause newNumericData) {
		NumericDataClause oldNumericData = numericData;
		numericData = newNumericData;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA, oldNumericData, numericData));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue getSubstance() {
		if (substance != null && substance.eIsProxy()) {
			InternalEObject oldSubstance = (InternalEObject)substance;
			substance = (RValue)eResolveProxy(oldSubstance);
			if (substance != oldSubstance) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE, oldSubstance, substance));
			}
		}
		return substance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue basicGetSubstance() {
		return substance;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSubstance(RValue newSubstance) {
		RValue oldSubstance = substance;
		substance = newSubstance;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE, oldSubstance, substance));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS:
				if (resolve) return getConcepts();
				return basicGetConcepts();
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA:
				if (resolve) return getNumericData();
				return basicGetNumericData();
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE:
				if (resolve) return getSubstance();
				return basicGetSubstance();
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
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS:
				setConcepts((RValue)newValue);
				return;
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA:
				setNumericData((NumericDataClause)newValue);
				return;
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE:
				setSubstance((RValue)newValue);
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
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS:
				setConcepts((RValue)null);
				return;
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA:
				setNumericData((NumericDataClause)null);
				return;
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE:
				setSubstance((RValue)null);
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
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__CONCEPTS:
				return concepts != null;
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__NUMERIC_DATA:
				return numericData != null;
			case ecoreastPackage.NUMERIC_DATA_GROUP_CLAUSE__SUBSTANCE:
				return substance != null;
		}
		return super.eIsSet(featureID);
	}
	
	@Override
	public StringBuilder toString(StringBuilder buf) {
		buf.append("NumericDataGroup: ");
		buf.append(" ");
		buf.append(numericData);
		buf.append(" ");
		buf.append(substance);
		return buf;
	}


} //NumericDataGroupClauseImpl