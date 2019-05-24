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

import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed MRCM Attribute Range Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl#getRangeConstraint <em>Range Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl#getAttributeRule <em>Attribute Rule</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl#getRuleStrengthId <em>Rule Strength Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl#getContentTypeId <em>Content Type Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedMRCMAttributeRangeRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedMRCMAttributeRangeRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedMRCMAttributeRangeRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRangeConstraint() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RANGE_CONSTRAINT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRangeConstraint(String newRangeConstraint) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RANGE_CONSTRAINT, newRangeConstraint);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getAttributeRule() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__ATTRIBUTE_RULE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setAttributeRule(String newAttributeRule) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__ATTRIBUTE_RULE, newAttributeRule);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getRuleStrengthId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RULE_STRENGTH_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setRuleStrengthId(String newRuleStrengthId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RULE_STRENGTH_ID, newRuleStrengthId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getContentTypeId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__CONTENT_TYPE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setContentTypeId(String newContentTypeId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__CONTENT_TYPE_ID, newContentTypeId);
	}

} //SnomedMRCMAttributeRangeRefSetMemberImpl
