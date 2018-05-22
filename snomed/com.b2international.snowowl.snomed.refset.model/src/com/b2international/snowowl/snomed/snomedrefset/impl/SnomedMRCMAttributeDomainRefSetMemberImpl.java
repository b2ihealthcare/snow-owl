/**
 *  Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.b2international.snowowl.snomed.snomedrefset.impl;

import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed MRCM Attribute Domain Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl#getDomainId <em>Domain Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl#isGrouped <em>Grouped</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl#getAttributeCardinality <em>Attribute Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl#getAttributeInGroupCardinality <em>Attribute In Group Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl#getRuleStrengthId <em>Rule Strength Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl#getContentTypeId <em>Content Type Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedMRCMAttributeDomainRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedMRCMAttributeDomainRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedMRCMAttributeDomainRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDomainId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__DOMAIN_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDomainId(String newDomainId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__DOMAIN_ID, newDomainId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isGrouped() {
		return (Boolean)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__GROUPED, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGrouped(boolean newGrouped) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__GROUPED, newGrouped);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAttributeCardinality() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_CARDINALITY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttributeCardinality(String newAttributeCardinality) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_CARDINALITY, newAttributeCardinality);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAttributeInGroupCardinality() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_IN_GROUP_CARDINALITY, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAttributeInGroupCardinality(String newAttributeInGroupCardinality) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_IN_GROUP_CARDINALITY, newAttributeInGroupCardinality);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRuleStrengthId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__RULE_STRENGTH_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRuleStrengthId(String newRuleStrengthId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__RULE_STRENGTH_ID, newRuleStrengthId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getContentTypeId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__CONTENT_TYPE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setContentTypeId(String newContentTypeId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__CONTENT_TYPE_ID, newContentTypeId);
	}

} //SnomedMRCMAttributeDomainRefSetMemberImpl
