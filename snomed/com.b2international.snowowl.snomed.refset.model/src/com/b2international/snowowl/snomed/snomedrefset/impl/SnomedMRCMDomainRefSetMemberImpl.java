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

import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed MRCM Domain Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getDomainConstraint <em>Domain Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getParentDomain <em>Parent Domain</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getProximalPrimitiveConstraint <em>Proximal Primitive Constraint</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getProximalPrimitiveRefinement <em>Proximal Primitive Refinement</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getDomainTemplateForPrecoordination <em>Domain Template For Precoordination</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getDomainTemplateForPostcoordination <em>Domain Template For Postcoordination</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl#getEditorialGuideReference <em>Editorial Guide Reference</em>}</li>
 * </ul>
 *
 * @generated
 */
public class SnomedMRCMDomainRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedMRCMDomainRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedMRCMDomainRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDomainConstraint() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_CONSTRAINT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDomainConstraint(String newDomainConstraint) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_CONSTRAINT, newDomainConstraint);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getParentDomain() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PARENT_DOMAIN, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setParentDomain(String newParentDomain) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PARENT_DOMAIN, newParentDomain);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getProximalPrimitiveConstraint() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_CONSTRAINT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProximalPrimitiveConstraint(String newProximalPrimitiveConstraint) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_CONSTRAINT, newProximalPrimitiveConstraint);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getProximalPrimitiveRefinement() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_REFINEMENT, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setProximalPrimitiveRefinement(String newProximalPrimitiveRefinement) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_REFINEMENT, newProximalPrimitiveRefinement);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDomainTemplateForPrecoordination() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_PRECOORDINATION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDomainTemplateForPrecoordination(String newDomainTemplateForPrecoordination) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_PRECOORDINATION, newDomainTemplateForPrecoordination);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getDomainTemplateForPostcoordination() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setDomainTemplateForPostcoordination(String newDomainTemplateForPostcoordination) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_POSTCOORDINATION, newDomainTemplateForPostcoordination);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getEditorialGuideReference() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__EDITORIAL_GUIDE_REFERENCE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void setEditorialGuideReference(String newEditorialGuideReference) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__EDITORIAL_GUIDE_REFERENCE, newEditorialGuideReference);
	}

} //SnomedMRCMDomainRefSetMemberImpl
