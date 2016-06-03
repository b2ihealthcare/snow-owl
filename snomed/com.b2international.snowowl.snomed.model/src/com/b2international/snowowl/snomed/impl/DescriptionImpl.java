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

import com.b2international.snowowl.snomed.Concept;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.Inactivatable;
import com.b2international.snowowl.snomed.SnomedPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Description</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getInactivationIndicatorRefSetMembers <em>Inactivation Indicator Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getAssociationRefSetMembers <em>Association Ref Set Members</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getLanguageCode <em>Language Code</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getTerm <em>Term</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getConcept <em>Concept</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getCaseSignificance <em>Case Significance</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.impl.DescriptionImpl#getLanguageRefSetMembers <em>Language Ref Set Members</em>}</li>
 * </ul>
 *
 * @generated
 */
public class DescriptionImpl extends ComponentImpl implements Description {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected DescriptionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedPackage.Literals.DESCRIPTION;
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
	public String getLanguageCode() {
		return (String)eGet(SnomedPackage.Literals.DESCRIPTION__LANGUAGE_CODE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLanguageCode(String newLanguageCode) {
		eSet(SnomedPackage.Literals.DESCRIPTION__LANGUAGE_CODE, newLanguageCode);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTerm() {
		return (String)eGet(SnomedPackage.Literals.DESCRIPTION__TERM, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTerm(String newTerm) {
		eSet(SnomedPackage.Literals.DESCRIPTION__TERM, newTerm);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getConcept() {
		return (Concept)eGet(SnomedPackage.Literals.DESCRIPTION__CONCEPT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setConcept(Concept newConcept) {
		eSet(SnomedPackage.Literals.DESCRIPTION__CONCEPT, newConcept);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getType() {
		return (Concept)eGet(SnomedPackage.Literals.DESCRIPTION__TYPE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(Concept newType) {
		eSet(SnomedPackage.Literals.DESCRIPTION__TYPE, newType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Concept getCaseSignificance() {
		return (Concept)eGet(SnomedPackage.Literals.DESCRIPTION__CASE_SIGNIFICANCE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCaseSignificance(Concept newCaseSignificance) {
		eSet(SnomedPackage.Literals.DESCRIPTION__CASE_SIGNIFICANCE, newCaseSignificance);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	public EList<SnomedLanguageRefSetMember> getLanguageRefSetMembers() {
		return (EList<SnomedLanguageRefSetMember>)eGet(SnomedPackage.Literals.DESCRIPTION__LANGUAGE_REF_SET_MEMBERS, true);
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
				case SnomedPackage.DESCRIPTION__INACTIVATION_INDICATOR_REF_SET_MEMBERS: return SnomedPackage.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS;
				case SnomedPackage.DESCRIPTION__ASSOCIATION_REF_SET_MEMBERS: return SnomedPackage.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS;
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
				case SnomedPackage.INACTIVATABLE__INACTIVATION_INDICATOR_REF_SET_MEMBERS: return SnomedPackage.DESCRIPTION__INACTIVATION_INDICATOR_REF_SET_MEMBERS;
				case SnomedPackage.INACTIVATABLE__ASSOCIATION_REF_SET_MEMBERS: return SnomedPackage.DESCRIPTION__ASSOCIATION_REF_SET_MEMBERS;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

} //DescriptionImpl