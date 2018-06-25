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
 * A representation of the model object '<em><b>Snomed MRCM Domain Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainConstraint <em>Domain Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getParentDomain <em>Parent Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveConstraint <em>Proximal Primitive Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveRefinement <em>Proximal Primitive Refinement</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPrecoordination <em>Domain Template For Precoordination</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPostcoordination <em>Domain Template For Postcoordination</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getEditorialGuideReference <em>Editorial Guide Reference</em>}</li>
 * </ul>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember()
 * @model
 * @generated
 */
public interface SnomedMRCMDomainRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns the value of the '<em><b>Domain Constraint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain Constraint</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain Constraint</em>' attribute.
	 * @see #setDomainConstraint(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_DomainConstraint()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getDomainConstraint();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainConstraint <em>Domain Constraint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Domain Constraint</em>' attribute.
	 * @see #getDomainConstraint()
	 * @generated
	 */
	void setDomainConstraint(String value);

	/**
	 * Returns the value of the '<em><b>Parent Domain</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent Domain</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent Domain</em>' attribute.
	 * @see #setParentDomain(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_ParentDomain()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getParentDomain();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getParentDomain <em>Parent Domain</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent Domain</em>' attribute.
	 * @see #getParentDomain()
	 * @generated
	 */
	void setParentDomain(String value);

	/**
	 * Returns the value of the '<em><b>Proximal Primitive Constraint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Proximal Primitive Constraint</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Proximal Primitive Constraint</em>' attribute.
	 * @see #setProximalPrimitiveConstraint(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_ProximalPrimitiveConstraint()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getProximalPrimitiveConstraint();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveConstraint <em>Proximal Primitive Constraint</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Proximal Primitive Constraint</em>' attribute.
	 * @see #getProximalPrimitiveConstraint()
	 * @generated
	 */
	void setProximalPrimitiveConstraint(String value);

	/**
	 * Returns the value of the '<em><b>Proximal Primitive Refinement</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Proximal Primitive Refinement</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Proximal Primitive Refinement</em>' attribute.
	 * @see #setProximalPrimitiveRefinement(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_ProximalPrimitiveRefinement()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getProximalPrimitiveRefinement();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveRefinement <em>Proximal Primitive Refinement</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Proximal Primitive Refinement</em>' attribute.
	 * @see #getProximalPrimitiveRefinement()
	 * @generated
	 */
	void setProximalPrimitiveRefinement(String value);

	/**
	 * Returns the value of the '<em><b>Domain Template For Precoordination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain Template For Precoordination</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain Template For Precoordination</em>' attribute.
	 * @see #setDomainTemplateForPrecoordination(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_DomainTemplateForPrecoordination()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getDomainTemplateForPrecoordination();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPrecoordination <em>Domain Template For Precoordination</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Domain Template For Precoordination</em>' attribute.
	 * @see #getDomainTemplateForPrecoordination()
	 * @generated
	 */
	void setDomainTemplateForPrecoordination(String value);

	/**
	 * Returns the value of the '<em><b>Domain Template For Postcoordination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Domain Template For Postcoordination</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Domain Template For Postcoordination</em>' attribute.
	 * @see #setDomainTemplateForPostcoordination(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_DomainTemplateForPostcoordination()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnType='LONG VARCHAR' columnLength='32768'"
	 * @generated
	 */
	String getDomainTemplateForPostcoordination();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPostcoordination <em>Domain Template For Postcoordination</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Domain Template For Postcoordination</em>' attribute.
	 * @see #getDomainTemplateForPostcoordination()
	 * @generated
	 */
	void setDomainTemplateForPostcoordination(String value);

	/**
	 * Returns the value of the '<em><b>Editorial Guide Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Editorial Guide Reference</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Editorial Guide Reference</em>' attribute.
	 * @see #setEditorialGuideReference(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMRCMDomainRefSetMember_EditorialGuideReference()
	 * @model required="true"
	 * @generated
	 */
	String getEditorialGuideReference();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getEditorialGuideReference <em>Editorial Guide Reference</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Editorial Guide Reference</em>' attribute.
	 * @see #getEditorialGuideReference()
	 * @generated
	 */
	void setEditorialGuideReference(String value);

} // SnomedMRCMDomainRefSetMember
