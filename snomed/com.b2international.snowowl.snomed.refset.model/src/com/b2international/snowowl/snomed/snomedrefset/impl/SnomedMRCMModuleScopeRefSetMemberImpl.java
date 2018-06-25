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

import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed MRCM Module Scope Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMModuleScopeRefSetMemberImpl#getMrcmRuleRefsetId <em>Mrcm Rule Refset Id</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedMRCMModuleScopeRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedMRCMModuleScopeRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedMRCMModuleScopeRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getMrcmRuleRefsetId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__MRCM_RULE_REFSET_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMrcmRuleRefsetId(String newMrcmRuleRefsetId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__MRCM_RULE_REFSET_ID, newMrcmRuleRefsetId);
	}

} //SnomedMRCMModuleScopeRefSetMemberImpl
