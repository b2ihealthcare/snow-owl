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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.b2international.snowowl.snomed.dsl.query.queryast.RValue;
import com.b2international.snowowl.snomed.dsl.query.queryast.ecoreastPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>RValue</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public abstract class RValueImpl extends EObjectImpl implements RValue {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RValueImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ecoreastPackage.Literals.RVALUE;
	}
	
	protected void appendRValue(StringBuilder buf, RValue rValue) {
		if(rValue == null) {
			buf.append("null");
		} else {
			rValue.toString(buf);
		}
	}

	public final String toString() {
		return toString(new StringBuilder()).toString();
	}		
} //RValueImpl