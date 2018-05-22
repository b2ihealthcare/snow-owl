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

import com.b2international.snowowl.snomed.mrcm.AttributeConstraint;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConstraintForm;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Attribute Constraint</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl#getForm <em>Form</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl#getDomain <em>Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.AttributeConstraintImpl#getPredicate <em>Predicate</em>}</li>
 * </ul>
 *
 * @generated
 */
public class AttributeConstraintImpl extends ConstraintBaseImpl implements AttributeConstraint {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected AttributeConstraintImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConstraintForm getForm() {
		return (ConstraintForm)eGet(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT__FORM, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setForm(ConstraintForm newForm) {
		eSet(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT__FORM, newForm);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptSetDefinition getDomain() {
		return (ConceptSetDefinition)eGet(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT__DOMAIN, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDomain(ConceptSetDefinition newDomain) {
		eSet(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT__DOMAIN, newDomain);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptModelPredicate getPredicate() {
		return (ConceptModelPredicate)eGet(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT__PREDICATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPredicate(ConceptModelPredicate newPredicate) {
		eSet(MrcmPackage.Literals.ATTRIBUTE_CONSTRAINT__PREDICATE, newPredicate);
	}

} //AttributeConstraintImpl
