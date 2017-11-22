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
package com.b2international.snowowl.snomed.snomedrefset;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage
 * @generated
 */
public interface SnomedRefSetFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SnomedRefSetFactory eINSTANCE = com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Snomed Regular Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Regular Ref Set</em>'.
	 * @generated
	 */
	SnomedRegularRefSet createSnomedRegularRefSet();

	/**
	 * Returns a new object of class '<em>Snomed Structural Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Structural Ref Set</em>'.
	 * @generated
	 */
	SnomedStructuralRefSet createSnomedStructuralRefSet();

	/**
	 * Returns a new object of class '<em>Snomed Mapping Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Mapping Ref Set</em>'.
	 * @generated
	 */
	SnomedMappingRefSet createSnomedMappingRefSet();

	/**
	 * Returns a new object of class '<em>Snomed Concrete Data Type Ref Set</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Concrete Data Type Ref Set</em>'.
	 * @generated
	 */
	SnomedConcreteDataTypeRefSet createSnomedConcreteDataTypeRefSet();

	/**
	 * Returns a new object of class '<em>Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Member</em>'.
	 * @generated
	 */
	SnomedRefSetMember createSnomedRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Description Type Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Description Type Ref Set Member</em>'.
	 * @generated
	 */
	SnomedDescriptionTypeRefSetMember createSnomedDescriptionTypeRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Simple Map Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Simple Map Ref Set Member</em>'.
	 * @generated
	 */
	SnomedSimpleMapRefSetMember createSnomedSimpleMapRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Complex Map Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Complex Map Ref Set Member</em>'.
	 * @generated
	 */
	SnomedComplexMapRefSetMember createSnomedComplexMapRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Query Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Query Ref Set Member</em>'.
	 * @generated
	 */
	SnomedQueryRefSetMember createSnomedQueryRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Attribute Value Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Attribute Value Ref Set Member</em>'.
	 * @generated
	 */
	SnomedAttributeValueRefSetMember createSnomedAttributeValueRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Language Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Language Ref Set Member</em>'.
	 * @generated
	 */
	SnomedLanguageRefSetMember createSnomedLanguageRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Concrete Data Type Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Concrete Data Type Ref Set Member</em>'.
	 * @generated
	 */
	SnomedConcreteDataTypeRefSetMember createSnomedConcreteDataTypeRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Association Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Association Ref Set Member</em>'.
	 * @generated
	 */
	SnomedAssociationRefSetMember createSnomedAssociationRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Module Dependency Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Module Dependency Ref Set Member</em>'.
	 * @generated
	 */
	SnomedModuleDependencyRefSetMember createSnomedModuleDependencyRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed Annotation Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed Annotation Ref Set Member</em>'.
	 * @generated
	 */
	SnomedAnnotationRefSetMember createSnomedAnnotationRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed MRCM Domain Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed MRCM Domain Ref Set Member</em>'.
	 * @generated
	 */
	SnomedMRCMDomainRefSetMember createSnomedMRCMDomainRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed MRCM Attribute Domain Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed MRCM Attribute Domain Ref Set Member</em>'.
	 * @generated
	 */
	SnomedMRCMAttributeDomainRefSetMember createSnomedMRCMAttributeDomainRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed MRCM Attribute Range Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed MRCM Attribute Range Ref Set Member</em>'.
	 * @generated
	 */
	SnomedMRCMAttributeRangeRefSetMember createSnomedMRCMAttributeRangeRefSetMember();

	/**
	 * Returns a new object of class '<em>Snomed MRCM Module Scope Ref Set Member</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Snomed MRCM Module Scope Ref Set Member</em>'.
	 * @generated
	 */
	SnomedMRCMModuleScopeRefSetMember createSnomedMRCMModuleScopeRefSetMember();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	SnomedRefSetPackage getSnomedRefSetPackage();

} //SnomedRefSetFactory