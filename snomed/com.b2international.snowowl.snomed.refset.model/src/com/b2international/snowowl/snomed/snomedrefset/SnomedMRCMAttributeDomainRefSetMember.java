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
 * A representation of the model object '<em><b>Snomed MRCM Attribute Domain Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getDomainId <em>Domain Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#isGrouped <em>Grouped</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeCardinality <em>Attribute Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeInGroupCardinality <em>Attribute In Group Cardinality</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getRuleStrengthId <em>Rule Strength Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getContentTypeId <em>Content Type Id</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember()
 * @model
 * @generated
 */
public interface SnomedMRCMAttributeDomainRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns the value of the '<em><b>Domain Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain Id</em>' attribute.
	 * @see #setDomainId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember_DomainId()
	 * @model required="true"
	 * @generated
	 */
	String getDomainId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getDomainId <em>Domain Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Domain Id</em>' attribute.
	 * @see #getDomainId()
	 * @generated
	 */
	void setDomainId(String value);

	/**
	 * Returns the value of the '<em><b>Grouped</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Grouped</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Grouped</em>' attribute.
	 * @see #setGrouped(boolean)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember_Grouped()
	 * @model required="true"
	 * @generated
	 */
	boolean isGrouped();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#isGrouped <em>Grouped</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Grouped</em>' attribute.
	 * @see #isGrouped()
	 * @generated
	 */
	void setGrouped(boolean value);

	/**
	 * Returns the value of the '<em><b>Attribute Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute Cardinality</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute Cardinality</em>' attribute.
	 * @see #setAttributeCardinality(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember_AttributeCardinality()
	 * @model required="true"
	 * @generated
	 */
	String getAttributeCardinality();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeCardinality <em>Attribute Cardinality</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attribute Cardinality</em>' attribute.
	 * @see #getAttributeCardinality()
	 * @generated
	 */
	void setAttributeCardinality(String value);

	/**
	 * Returns the value of the '<em><b>Attribute In Group Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attribute In Group Cardinality</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attribute In Group Cardinality</em>' attribute.
	 * @see #setAttributeInGroupCardinality(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember_AttributeInGroupCardinality()
	 * @model required="true"
	 * @generated
	 */
	String getAttributeInGroupCardinality();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeInGroupCardinality <em>Attribute In Group Cardinality</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Attribute In Group Cardinality</em>' attribute.
	 * @see #getAttributeInGroupCardinality()
	 * @generated
	 */
	void setAttributeInGroupCardinality(String value);

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
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember_RuleStrengthId()
	 * @model required="true"
	 * @generated
	 */
	String getRuleStrengthId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getRuleStrengthId <em>Rule Strength Id</em>}' attribute.
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
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMAttributeDomainRefSetMember_ContentTypeId()
	 * @model required="true"
	 * @generated
	 */
	String getContentTypeId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getContentTypeId <em>Content Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Content Type Id</em>' attribute.
	 * @see #getContentTypeId()
	 * @generated
	 */
	void setContentTypeId(String value);

} // SnomedMRCMAttributeDomainRefSetMember
