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
package com.b2international.snowowl.snomed.snomedrefset;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Snomed MRCM Module Scope Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember#getMrcmRuleRefsetId <em>Mrcm Rule Refset Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMModuleScopeRefSetMember()
 * @model
 * @generated
 */
public interface SnomedMRCMModuleScopeRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns the value of the '<em><b>Mrcm Rule Refset Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Mrcm Rule Refset Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Mrcm Rule Refset Id</em>' attribute.
	 * @see #setMrcmRuleRefsetId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMModuleScopeRefSetMember_MrcmRuleRefsetId()
	 * @model required="true"
	 * @generated
	 */
	String getMrcmRuleRefsetId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember#getMrcmRuleRefsetId <em>Mrcm Rule Refset Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Mrcm Rule Refset Id</em>' attribute.
	 * @see #getMrcmRuleRefsetId()
	 * @generated
	 */
	void setMrcmRuleRefsetId(String value);

} // SnomedMRCMModuleScopeRefSetMember
