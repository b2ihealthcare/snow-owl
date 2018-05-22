/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.mrcm.impl;

import com.b2international.snowowl.snomed.mrcm.ConstraintBase;
import com.b2international.snowowl.snomed.mrcm.ConstraintStrength;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Constraint Base</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl#getStrength <em>Strength</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl#getValidationMessage <em>Validation Message</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConstraintBaseImpl#getDescription <em>Description</em>}</li>
 * </ul>
 *
 * @generated
 */
public abstract class ConstraintBaseImpl extends ConceptModelComponentImpl implements ConstraintBase {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConstraintBaseImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.CONSTRAINT_BASE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstraintStrength getStrength() {
		return (ConstraintStrength)eGet(MrcmPackage.Literals.CONSTRAINT_BASE__STRENGTH, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setStrength(ConstraintStrength newStrength) {
		eSet(MrcmPackage.Literals.CONSTRAINT_BASE__STRENGTH, newStrength);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValidationMessage() {
		return (String)eGet(MrcmPackage.Literals.CONSTRAINT_BASE__VALIDATION_MESSAGE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValidationMessage(String newValidationMessage) {
		eSet(MrcmPackage.Literals.CONSTRAINT_BASE__VALIDATION_MESSAGE, newValidationMessage);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDescription() {
		return (String)eGet(MrcmPackage.Literals.CONSTRAINT_BASE__DESCRIPTION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDescription(String newDescription) {
		eSet(MrcmPackage.Literals.CONSTRAINT_BASE__DESCRIPTION, newDescription);
	}

} //ConstraintBaseImpl
