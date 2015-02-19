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

import com.b2international.snowowl.snomed.dsl.query.queryast.BinaryRValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Binary RValue</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.BinaryRValueImpl#getLeft <em>Left</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.dsl.query.queryast.impl.BinaryRValueImpl#getRight <em>Right</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class BinaryRValueImpl extends RValueImpl implements BinaryRValue {
	/**
	 * The cached value of the '{@link #getLeft() <em>Left</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLeft()
	 * @generated
	 * @ordered
	 */
	protected RValue left;

	/**
	 * The cached value of the '{@link #getRight() <em>Right</em>}' containment reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRight()
	 * @generated
	 * @ordered
	 */
	protected RValue right;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected BinaryRValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ecoreastPackage.Literals.BINARY_RVALUE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue getLeft() {
		return left;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetLeft(RValue newLeft, NotificationChain msgs) {
		RValue oldLeft = left;
		left = newLeft;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ecoreastPackage.BINARY_RVALUE__LEFT, oldLeft, newLeft);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLeft(RValue newLeft) {
		if (newLeft != left) {
			NotificationChain msgs = null;
			if (left != null)
				msgs = ((InternalEObject)left).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ecoreastPackage.BINARY_RVALUE__LEFT, null, msgs);
			if (newLeft != null)
				msgs = ((InternalEObject)newLeft).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ecoreastPackage.BINARY_RVALUE__LEFT, null, msgs);
			msgs = basicSetLeft(newLeft, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.BINARY_RVALUE__LEFT, newLeft, newLeft));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public RValue getRight() {
		return right;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetRight(RValue newRight, NotificationChain msgs) {
		RValue oldRight = right;
		right = newRight;
		if (eNotificationRequired()) {
			ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, ecoreastPackage.BINARY_RVALUE__RIGHT, oldRight, newRight);
			if (msgs == null) msgs = notification; else msgs.add(notification);
		}
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRight(RValue newRight) {
		if (newRight != right) {
			NotificationChain msgs = null;
			if (right != null)
				msgs = ((InternalEObject)right).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - ecoreastPackage.BINARY_RVALUE__RIGHT, null, msgs);
			if (newRight != null)
				msgs = ((InternalEObject)newRight).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - ecoreastPackage.BINARY_RVALUE__RIGHT, null, msgs);
			msgs = basicSetRight(newRight, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ecoreastPackage.BINARY_RVALUE__RIGHT, newRight, newRight));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ecoreastPackage.BINARY_RVALUE__LEFT:
				return basicSetLeft(null, msgs);
			case ecoreastPackage.BINARY_RVALUE__RIGHT:
				return basicSetRight(null, msgs);
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
			case ecoreastPackage.BINARY_RVALUE__LEFT:
				return getLeft();
			case ecoreastPackage.BINARY_RVALUE__RIGHT:
				return getRight();
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
			case ecoreastPackage.BINARY_RVALUE__LEFT:
				setLeft((RValue)newValue);
				return;
			case ecoreastPackage.BINARY_RVALUE__RIGHT:
				setRight((RValue)newValue);
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
			case ecoreastPackage.BINARY_RVALUE__LEFT:
				setLeft((RValue)null);
				return;
			case ecoreastPackage.BINARY_RVALUE__RIGHT:
				setRight((RValue)null);
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
			case ecoreastPackage.BINARY_RVALUE__LEFT:
				return left != null;
			case ecoreastPackage.BINARY_RVALUE__RIGHT:
				return right != null;
		}
		return super.eIsSet(featureID);
	}
	
	@Override
	public StringBuilder toString(StringBuilder buf) {
		buf.append('(');
		appendRValue(buf, left);
		appendLeftRightSeparatorForToString(buf);
		appendRValue(buf, right);
		buf.append(')');
		return buf;
	}
	
	protected abstract void appendLeftRightSeparatorForToString(StringBuilder buf);	

} //BinaryRValueImpl