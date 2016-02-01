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
package com.b2international.snowowl.snomed.mrcm.impl;

import org.eclipse.emf.ecore.EClass;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.snomed.mrcm.ConcreteDomainElementPredicate;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;
import com.b2international.snowowl.snomed.snomedrefset.DataType;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concrete Domain Element Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl#getName <em>Name</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.ConcreteDomainElementPredicateImpl#getType <em>Type</em>}</li>
 * </ul>
 * </p>
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
	public String getName() {
		return (String)eGet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__NAME, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		eSet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__NAME, newName);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getLabel() {
		final String label = (String)eGet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__LABEL, true);
		if (StringUtils.isEmpty(label)) {
			final String name = getName();
			if (StringUtils.isEmpty(name)) {
				return "";
			}
			return StringUtils.capitalizeFirstLetter(StringUtils.splitCamelCase(name.replaceFirst("is|does|has", "")).toLowerCase());
		} else {
			return label;
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(String newLabel) {
		eSet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__LABEL, newLabel);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataType getType() {
		return (DataType)eGet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(DataType newType) {
		eSet(MrcmPackage.Literals.CONCRETE_DOMAIN_ELEMENT_PREDICATE__TYPE, newType);
	}

} //ConcreteDomainElementPredicateImpl