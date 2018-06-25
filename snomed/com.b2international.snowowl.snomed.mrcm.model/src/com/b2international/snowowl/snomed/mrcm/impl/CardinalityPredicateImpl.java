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

import com.b2international.snowowl.snomed.mrcm.CardinalityPredicate;
import com.b2international.snowowl.snomed.mrcm.ConceptModelPredicate;
import com.b2international.snowowl.snomed.mrcm.GroupRule;
import com.b2international.snowowl.snomed.mrcm.MrcmPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Cardinality Predicate</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl#getMinCardinality <em>Min Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl#getMaxCardinality <em>Max Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl#getGroupRule <em>Group Rule</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.mrcm.impl.CardinalityPredicateImpl#getPredicate <em>Predicate</em>}</li>
 * </ul>
 *
 * @generated
 */
public class CardinalityPredicateImpl extends ConceptModelPredicateImpl implements CardinalityPredicate {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected CardinalityPredicateImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return MrcmPackage.Literals.CARDINALITY_PREDICATE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMinCardinality() {
		return (Integer)eGet(MrcmPackage.Literals.CARDINALITY_PREDICATE__MIN_CARDINALITY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMinCardinality(int newMinCardinality) {
		eSet(MrcmPackage.Literals.CARDINALITY_PREDICATE__MIN_CARDINALITY, newMinCardinality);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getMaxCardinality() {
		return (Integer)eGet(MrcmPackage.Literals.CARDINALITY_PREDICATE__MAX_CARDINALITY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMaxCardinality(int newMaxCardinality) {
		eSet(MrcmPackage.Literals.CARDINALITY_PREDICATE__MAX_CARDINALITY, newMaxCardinality);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public GroupRule getGroupRule() {
		return (GroupRule)eGet(MrcmPackage.Literals.CARDINALITY_PREDICATE__GROUP_RULE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGroupRule(GroupRule newGroupRule) {
		eSet(MrcmPackage.Literals.CARDINALITY_PREDICATE__GROUP_RULE, newGroupRule);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ConceptModelPredicate getPredicate() {
		return (ConceptModelPredicate)eGet(MrcmPackage.Literals.CARDINALITY_PREDICATE__PREDICATE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPredicate(ConceptModelPredicate newPredicate) {
		eSet(MrcmPackage.Literals.CARDINALITY_PREDICATE__PREDICATE, newPredicate);
	}

} //CardinalityPredicateImpl
