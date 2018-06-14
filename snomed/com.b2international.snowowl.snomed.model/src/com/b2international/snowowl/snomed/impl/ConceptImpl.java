/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Concept</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getInactivationIndicatorRefSetMembers <em>Inactivation Indicator Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getAssociationRefSetMembers <em>Association Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getConcreteDomainRefSetMembers <em>Concrete Domain Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getDefinitionStatus <em>Definition Status</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getOutboundRelationships <em>Outbound Relationships</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getDescriptions <em>Descriptions</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#isExhaustive <em>Exhaustive</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#getFullySpecifiedName <em>Fully Specified Name</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.ConceptImpl#isPrimitive <em>Primitive</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ConceptImpl extends ComponentImpl implements Concept {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ConceptImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedPackage.Literals.CONCEPT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedAttributeValueRefSetMember> getInactivationIndicatorRefSetMembers() {
		return (EList<SnomedAttributeValueRefSetMember>)eGet(SnomedPackage.Literals.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedAssociationRefSetMember> getAssociationRefSetMembers() {
		return (EList<SnomedAssociationRefSetMember>)eGet(SnomedPackage.Literals.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS, true);
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
	public Concept getDefinitionStatus() {
		return (Concept)eGet(SnomedPackage.Literals.CONCEPT__DEFINITION_STATUS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDefinitionStatus(Concept newDefinitionStatus) {
		eSet(SnomedPackage.Literals.CONCEPT__DEFINITION_STATUS, newDefinitionStatus);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<Relationship> getOutboundRelationships() {
		return (EList<Relationship>)eGet(SnomedPackage.Literals.CONCEPT__OUTBOUND_RELATIONSHIPS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<Description> getDescriptions() {
		return (EList<Description>)eGet(SnomedPackage.Literals.CONCEPT__DESCRIPTIONS, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isExhaustive() {
		return (Boolean)eGet(SnomedPackage.Literals.CONCEPT__EXHAUSTIVE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExhaustive(boolean newExhaustive) {
		eSet(SnomedPackage.Literals.CONCEPT__EXHAUSTIVE, newExhaustive);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getFullySpecifiedName() {
		
		for (Description desc : getDescriptions()) {
			
			if (Concepts.FULLY_SPECIFIED_NAME.equals(desc.getType().getId()) && desc.isActive()) {
				return desc.getTerm();
			}
		}
		
		// If no active FSN is available, get the first inactive one
		for (Description desc : getDescriptions()) {
			
			if (Concepts.FULLY_SPECIFIED_NAME.equals(desc.getType().getId())) {
				return desc.getTerm();
			}
		}

		// Fallback: return the concept ID as the FSN
		return getId();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean isPrimitive() {
		
		if (null == getDefinitionStatus()) {
			throw new NullPointerException("Concept definition status was null.");
		}
		
		return getDefinitionStatus().getId().equals(Concepts.PRIMITIVE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == Inactivatable.class) {
			switch (derivedFeatureID) {
				case SnomedPackage.CONCEPT__INACTIVATION_INDICATOR_REF_SET_MEMBERS: return SnomedPackage.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS;
				case SnomedPackage.CONCEPT__ASSOCIATION_REF_SET_MEMBERS: return SnomedPackage.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS;
				default: return -1;
			}
		}
		if (baseClass == Annotatable.class) {
			switch (derivedFeatureID) {
				case SnomedPackage.CONCEPT__CONCRETE_DOMAIN_REF_SET_MEMBERS: return SnomedPackage.ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS;
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
		if (baseClass == Inactivatable.class) {
			switch (baseFeatureID) {
				case SnomedPackage.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS: return SnomedPackage.CONCEPT__INACTIVATION_INDICATOR_REF_SET_MEMBERS;
				case SnomedPackage.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS: return SnomedPackage.CONCEPT__ASSOCIATION_REF_SET_MEMBERS;
				default: return -1;
			}
		}
		if (baseClass == Annotatable.class) {
			switch (baseFeatureID) {
				case SnomedPackage.ANNOTATABLE__CONCRETE_DOMAIN_REF_SET_MEMBERS: return SnomedPackage.CONCEPT__CONCRETE_DOMAIN_REF_SET_MEMBERS;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

} //ConceptImpl