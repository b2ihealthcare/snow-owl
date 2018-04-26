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
 * A representation of the model object '<em><b>Snomed MRCM Attribute Range Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRangeConstraint <em>Range Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getAttributeRule <em>Attribute Rule</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRuleStrengthId <em>Rule Strength Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getContentTypeId <em>Content Type Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeRangeRefSetMember()
 * @model
 * @generated
 */
public interface SnomedMRCMAttributeRangeRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns the value of the '<em><b>Range Constraint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Range Constraint</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Range Constraint</em>' attribute.
	 * @see #setRangeConstraint(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeRangeRefSetMember_RangeConstraint()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getRangeConstraint();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRangeConstraint <em>Range Constraint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Range Constraint</em>' attribute.
	 * @see #getRangeConstraint()
	 * @generated
	 */
	void setRangeConstraint(String value);

	/**
	 * Returns the value of the '<em><b>Attribute Rule</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Rule</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Rule</em>' attribute.
	 * @see #setAttributeRule(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeRangeRefSetMember_AttributeRule()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getAttributeRule();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getAttributeRule <em>Attribute Rule</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attribute Rule</em>' attribute.
	 * @see #getAttributeRule()
	 * @generated
	 */
	void setAttributeRule(String value);

	/**
	 * Returns the value of the '<em><b>Rule Strength Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Rule Strength Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Rule Strength Id</em>' attribute.
	 * @see #setRuleStrengthId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeRangeRefSetMember_RuleStrengthId()
	 * @model required="true"
	 * @generated
	 */
	String getRuleStrengthId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRuleStrengthId <em>Rule Strength Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Rule Strength Id</em>' attribute.
	 * @see #getRuleStrengthId()
	 * @generated
	 */
	void setRuleStrengthId(String value);

	/**
	 * Returns the value of the '<em><b>Content Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Content Type Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Content Type Id</em>' attribute.
	 * @see #setContentTypeId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeRangeRefSetMember_ContentTypeId()
	 * @model required="true"
	 * @generated
	 */
	String getContentTypeId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getContentTypeId <em>Content Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Type Id</em>' attribute.
	 * @see #getContentTypeId()
	 * @generated
	 */
	void setContentTypeId(String value);

} // SnomedMRCMAttributeRangeRefSetMember
