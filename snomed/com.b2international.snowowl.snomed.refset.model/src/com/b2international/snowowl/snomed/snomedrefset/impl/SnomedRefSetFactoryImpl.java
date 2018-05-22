/**
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.snomedrefset.impl;

import com.b2international.snowowl.snomed.snomedrefset.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SnomedRefSetFactoryImpl extends EFactoryImpl implements SnomedRefSetFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static SnomedRefSetFactory init() {
		try {
			SnomedRefSetFactory theSnomedRefSetFactory = (SnomedRefSetFactory)EPackage.Registry.INSTANCE.getEFactory(SnomedRefSetPackage.eNS_URI);
			if (theSnomedRefSetFactory != null) {
				return theSnomedRefSetFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new SnomedRefSetFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case SnomedRefSetPackage.SNOMED_REGULAR_REF_SET: return (EObject)createSnomedRegularRefSet();
			case SnomedRefSetPackage.SNOMED_STRUCTURAL_REF_SET: return (EObject)createSnomedStructuralRefSet();
			case SnomedRefSetPackage.SNOMED_MAPPING_REF_SET: return (EObject)createSnomedMappingRefSet();
			case SnomedRefSetPackage.SNOMED_CONCRETE_DATA_TYPE_REF_SET: return (EObject)createSnomedConcreteDataTypeRefSet();
			case SnomedRefSetPackage.SNOMED_REF_SET_MEMBER: return (EObject)createSnomedRefSetMember();
			case SnomedRefSetPackage.SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER: return (EObject)createSnomedDescriptionTypeRefSetMember();
			case SnomedRefSetPackage.SNOMED_SIMPLE_MAP_REF_SET_MEMBER: return (EObject)createSnomedSimpleMapRefSetMember();
			case SnomedRefSetPackage.SNOMED_COMPLEX_MAP_REF_SET_MEMBER: return (EObject)createSnomedComplexMapRefSetMember();
			case SnomedRefSetPackage.SNOMED_QUERY_REF_SET_MEMBER: return (EObject)createSnomedQueryRefSetMember();
			case SnomedRefSetPackage.SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER: return (EObject)createSnomedAttributeValueRefSetMember();
			case SnomedRefSetPackage.SNOMED_LANGUAGE_REF_SET_MEMBER: return (EObject)createSnomedLanguageRefSetMember();
			case SnomedRefSetPackage.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER: return (EObject)createSnomedConcreteDataTypeRefSetMember();
			case SnomedRefSetPackage.SNOMED_ASSOCIATION_REF_SET_MEMBER: return (EObject)createSnomedAssociationRefSetMember();
			case SnomedRefSetPackage.SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER: return (EObject)createSnomedModuleDependencyRefSetMember();
			case SnomedRefSetPackage.SNOMED_ANNOTATION_REF_SET_MEMBER: return (EObject)createSnomedAnnotationRefSetMember();
			case SnomedRefSetPackage.SNOMED_OWL_EXPRESSION_REF_SET_MEMBER: return (EObject)createSnomedOWLExpressionRefSetMember();
			case SnomedRefSetPackage.SNOMED_MRCM_DOMAIN_REF_SET_MEMBER: return (EObject)createSnomedMRCMDomainRefSetMember();
			case SnomedRefSetPackage.SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER: return (EObject)createSnomedMRCMAttributeDomainRefSetMember();
			case SnomedRefSetPackage.SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER: return (EObject)createSnomedMRCMAttributeRangeRefSetMember();
			case SnomedRefSetPackage.SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER: return (EObject)createSnomedMRCMModuleScopeRefSetMember();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object createFromString(EDataType eDataType, String initialValue) {
		switch (eDataType.getClassifierID()) {
			case SnomedRefSetPackage.SNOMED_REF_SET_TYPE:
				return createSnomedRefSetTypeFromString(eDataType, initialValue);
			case SnomedRefSetPackage.DATA_TYPE:
				return createDataTypeFromString(eDataType, initialValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String convertToString(EDataType eDataType, Object instanceValue) {
		switch (eDataType.getClassifierID()) {
			case SnomedRefSetPackage.SNOMED_REF_SET_TYPE:
				return convertSnomedRefSetTypeToString(eDataType, instanceValue);
			case SnomedRefSetPackage.DATA_TYPE:
				return convertDataTypeToString(eDataType, instanceValue);
			default:
				throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRegularRefSet createSnomedRegularRefSet() {
		SnomedRegularRefSetImpl snomedRegularRefSet = new SnomedRegularRefSetImpl();
		return snomedRegularRefSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedStructuralRefSet createSnomedStructuralRefSet() {
		SnomedStructuralRefSetImpl snomedStructuralRefSet = new SnomedStructuralRefSetImpl();
		return snomedStructuralRefSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedMappingRefSet createSnomedMappingRefSet() {
		SnomedMappingRefSetImpl snomedMappingRefSet = new SnomedMappingRefSetImpl();
		return snomedMappingRefSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedConcreteDataTypeRefSet createSnomedConcreteDataTypeRefSet() {
		SnomedConcreteDataTypeRefSetImpl snomedConcreteDataTypeRefSet = new SnomedConcreteDataTypeRefSetImpl();
		return snomedConcreteDataTypeRefSet;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetMember createSnomedRefSetMember() {
		SnomedRefSetMemberImpl snomedRefSetMember = new SnomedRefSetMemberImpl();
		return snomedRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedDescriptionTypeRefSetMember createSnomedDescriptionTypeRefSetMember() {
		SnomedDescriptionTypeRefSetMemberImpl snomedDescriptionTypeRefSetMember = new SnomedDescriptionTypeRefSetMemberImpl();
		return snomedDescriptionTypeRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedSimpleMapRefSetMember createSnomedSimpleMapRefSetMember() {
		SnomedSimpleMapRefSetMemberImpl snomedSimpleMapRefSetMember = new SnomedSimpleMapRefSetMemberImpl();
		return snomedSimpleMapRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedComplexMapRefSetMember createSnomedComplexMapRefSetMember() {
		SnomedComplexMapRefSetMemberImpl snomedComplexMapRefSetMember = new SnomedComplexMapRefSetMemberImpl();
		return snomedComplexMapRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedQueryRefSetMember createSnomedQueryRefSetMember() {
		SnomedQueryRefSetMemberImpl snomedQueryRefSetMember = new SnomedQueryRefSetMemberImpl();
		return snomedQueryRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedAttributeValueRefSetMember createSnomedAttributeValueRefSetMember() {
		SnomedAttributeValueRefSetMemberImpl snomedAttributeValueRefSetMember = new SnomedAttributeValueRefSetMemberImpl();
		return snomedAttributeValueRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedLanguageRefSetMember createSnomedLanguageRefSetMember() {
		SnomedLanguageRefSetMemberImpl snomedLanguageRefSetMember = new SnomedLanguageRefSetMemberImpl();
		return snomedLanguageRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedConcreteDataTypeRefSetMember createSnomedConcreteDataTypeRefSetMember() {
		SnomedConcreteDataTypeRefSetMemberImpl snomedConcreteDataTypeRefSetMember = new SnomedConcreteDataTypeRefSetMemberImpl();
		return snomedConcreteDataTypeRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedAssociationRefSetMember createSnomedAssociationRefSetMember() {
		SnomedAssociationRefSetMemberImpl snomedAssociationRefSetMember = new SnomedAssociationRefSetMemberImpl();
		return snomedAssociationRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedModuleDependencyRefSetMember createSnomedModuleDependencyRefSetMember() {
		SnomedModuleDependencyRefSetMemberImpl snomedModuleDependencyRefSetMember = new SnomedModuleDependencyRefSetMemberImpl();
		return snomedModuleDependencyRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedAnnotationRefSetMember createSnomedAnnotationRefSetMember() {
		SnomedAnnotationRefSetMemberImpl snomedAnnotationRefSetMember = new SnomedAnnotationRefSetMemberImpl();
		return snomedAnnotationRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedOWLExpressionRefSetMember createSnomedOWLExpressionRefSetMember() {
		SnomedOWLExpressionRefSetMemberImpl snomedOWLExpressionRefSetMember = new SnomedOWLExpressionRefSetMemberImpl();
		return snomedOWLExpressionRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedMRCMDomainRefSetMember createSnomedMRCMDomainRefSetMember() {
		SnomedMRCMDomainRefSetMemberImpl snomedMRCMDomainRefSetMember = new SnomedMRCMDomainRefSetMemberImpl();
		return snomedMRCMDomainRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedMRCMAttributeDomainRefSetMember createSnomedMRCMAttributeDomainRefSetMember() {
		SnomedMRCMAttributeDomainRefSetMemberImpl snomedMRCMAttributeDomainRefSetMember = new SnomedMRCMAttributeDomainRefSetMemberImpl();
		return snomedMRCMAttributeDomainRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedMRCMAttributeRangeRefSetMember createSnomedMRCMAttributeRangeRefSetMember() {
		SnomedMRCMAttributeRangeRefSetMemberImpl snomedMRCMAttributeRangeRefSetMember = new SnomedMRCMAttributeRangeRefSetMemberImpl();
		return snomedMRCMAttributeRangeRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedMRCMModuleScopeRefSetMember createSnomedMRCMModuleScopeRefSetMember() {
		SnomedMRCMModuleScopeRefSetMemberImpl snomedMRCMModuleScopeRefSetMember = new SnomedMRCMModuleScopeRefSetMemberImpl();
		return snomedMRCMModuleScopeRefSetMember;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetType createSnomedRefSetTypeFromString(EDataType eDataType, String initialValue) {
		SnomedRefSetType result = SnomedRefSetType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertSnomedRefSetTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public DataType createDataTypeFromString(EDataType eDataType, String initialValue) {
		DataType result = DataType.get(initialValue);
		if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
		return result;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String convertDataTypeToString(EDataType eDataType, Object instanceValue) {
		return instanceValue == null ? null : instanceValue.toString();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetPackage getSnomedRefSetPackage() {
		return (SnomedRefSetPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static SnomedRefSetPackage getPackage() {
		return SnomedRefSetPackage.eINSTANCE;
	}

} //SnomedRefSetFactoryImpl
