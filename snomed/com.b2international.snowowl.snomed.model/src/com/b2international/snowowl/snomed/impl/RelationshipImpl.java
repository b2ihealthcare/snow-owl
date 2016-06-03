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
package com.b2international.snowowl.snomed.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.snomed.Annotatable;
import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Relationship</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getConcreteDomainRefSetMembers <em>Concrete Domain Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getGroup <em>Group</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getUnionGroup <em>Union Group</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#isDestinationNegated <em>Destination Negated</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getSource <em>Source</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getDestination <em>Destination</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getCharacteristicType <em>Characteristic Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getModifier <em>Modifier</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.RelationshipImpl#getRefinabilityRefSetMembers <em>Refinability Ref Set Members</em>}</li>
 * </ul>
 *
 * @generated
 */
public class RelationshipImpl extends ComponentImpl implements Relationship {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected RelationshipImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedPackage.Literals.RELATIONSHIP;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedConcreteDataTypeRefSetMember> getConcreteDomainRefSetMembers() {
		return (EList<SnomedConcreteDataTypeRefSetMember>)eGet(SnomedPackage.Literals.ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getGroup() {
		return (Integer)eGet(SnomedPackage.Literals.RELATIONSHIP__GROUP, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGroup(int newGroup) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__GROUP, newGroup);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getUnionGroup() {
		return (Integer)eGet(SnomedPackage.Literals.RELATIONSHIP__UNION_GROUP, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUnionGroup(int newUnionGroup) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__UNION_GROUP, newUnionGroup);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDestinationNegated() {
		return (Boolean)eGet(SnomedPackage.Literals.RELATIONSHIP__DESTINATION_NEGATED, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDestinationNegated(boolean newDestinationNegated) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__DESTINATION_NEGATED, newDestinationNegated);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getSource() {
		return (Concept)eGet(SnomedPackage.Literals.RELATIONSHIP__SOURCE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSource(Concept newSource) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__SOURCE, newSource);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getDestination() {
		return (Concept)eGet(SnomedPackage.Literals.RELATIONSHIP__DESTINATION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDestination(Concept newDestination) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__DESTINATION, newDestination);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getType() {
		return (Concept)eGet(SnomedPackage.Literals.RELATIONSHIP__TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(Concept newType) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__TYPE, newType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getCharacteristicType() {
		return (Concept)eGet(SnomedPackage.Literals.RELATIONSHIP__CHARACTERISTIC_TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCharacteristicType(Concept newCharacteristicType) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__CHARACTERISTIC_TYPE, newCharacteristicType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getModifier() {
		return (Concept)eGet(SnomedPackage.Literals.RELATIONSHIP__MODIFIER, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModifier(Concept newModifier) {
		eSet(SnomedPackage.Literals.RELATIONSHIP__MODIFIER, newModifier);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedAttributeValueRefSetMember> getRefinabilityRefSetMembers() {
		return (EList<SnomedAttributeValueRefSetMember>)eGet(SnomedPackage.Literals.RELATIONSHIP__REFINABILITY_REF_SET_MEMBERS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == Annotatable.class) {
			switch (derivedFeatureID) {
				case SnomedPackage.RELATIONSHIP__CONCRETE_DOMAIN_REF_SET_MEMBERS: return SnomedPackage.ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == Annotatable.class) {
			switch (baseFeatureID) {
				case SnomedPackage.ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS: return SnomedPackage.RELATIONSHIP__CONCRETE_DOMAIN_REF_SET_MEMBERS;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

} //RelationshipImpl