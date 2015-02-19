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
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.UnaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Unary RValue</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.UnaryRValueImpl#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class UnaryRValueImpl extends RValueImpl implements UnaryRValue {
	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected RValue value;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected UnaryRValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ecoreastPackage.Literals.UNARY_RVALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue getValue() {
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetValue(RValue newValue, NotificationChain msgs) {
		RValue oldValue = value;
		value = newValue;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ecoreastPackage.UNARY_RVALUE__VALUE, oldValue, newValue);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValue(RValue newValue) {
		if (newValue != value) {
			NotificationChain msgs = null;
			if (value != null)
				msgs = ((InternalEObject)value).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ecoreastPackage.UNARY_RVALUE__VALUE, null, msgs);
			if (newValue != null)
				msgs = ((InternalEObject)newValue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ecoreastPackage.UNARY_RVALUE__VALUE, null, msgs);
			msgs = basicSetValue(newValue, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.UNARY_RVALUE__VALUE, newValue, newValue));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ecoreastPackage.UNARY_RVALUE__VALUE:
				return basicSetValue(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ecoreastPackage.UNARY_RVALUE__VALUE:
				return getValue();
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
			case ecoreastPackage.UNARY_RVALUE__VALUE:
				setValue((RValue)newValue);
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
			case ecoreastPackage.UNARY_RVALUE__VALUE:
				setValue((RValue)null);
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
			case ecoreastPackage.UNARY_RVALUE__VALUE:
				return value != null;
		}
		return super.eIsSet(featureID);
	}

} //UnaryRValueImpl