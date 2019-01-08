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

import com.b2international.snowowl.snomed.mrcm.ConceptSetDefinition;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import com.b2international.snowowl.snomed.snomedrefset.DataType;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concrete Domain Element Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl#getAttribute <em>Attribute</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl#getRange <em>Range</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl#getCharacteristicTypeConceptId <em>Characteristic Type Concept Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConcreteDomainElementPredicateImpl extends ConceptModelPredicateImpl implements ConcreteDomainElementPredicate {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConcreteDomainElementPredicateImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptSetDefinition getAttribute() {
		return (ConceptSetDefinition)eGet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__ATTRIBUTE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttribute(ConceptSetDefinition newAttribute) {
		eSet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__ATTRIBUTE, newAttribute);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataType getRange() {
		return (DataType)eGet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__RANGE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRange(DataType newRange) {
		eSet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__RANGE, newRange);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCharacteristicTypeConceptId() {
		return (String)eGet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCharacteristicTypeConceptId(String newCharacteristicTypeConceptId) {
		eSet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__CHARACTERISTIC_TYPE_CONCEPT_ID, newCharacteristicTypeConceptId);
	}

} //ConcreteDomainElementPredicateImpl
