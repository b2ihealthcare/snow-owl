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

import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedOWLExpressionRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SnomedRefSetPackageImpl extends EPackageImpl implements SnomedRefSetPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedRefSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedRegularRefSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedStructuralRefSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedMappingRefSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedConcreteDataTypeRefSetEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedDescriptionTypeRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedSimpleMapRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedComplexMapRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedQueryRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedAttributeValueRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedLanguageRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedConcreteDataTypeRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedAssociationRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedModuleDependencyRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedAnnotationRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedOWLExpressionRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedMRCMDomainRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedMRCMAttributeDomainRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedMRCMAttributeRangeRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass snomedMRCMModuleScopeRefSetMemberEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum snomedRefSetTypeEEnum = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EEnum dataTypeEEnum = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private SnomedRefSetPackageImpl() {
		super(eNS_URI, SnomedRefSetFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link SnomedRefSetPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static SnomedRefSetPackage init() {
		if (isInited) return (SnomedRefSetPackage)EPackage.Registry.INSTANCE.getEPackage(SnomedRefSetPackage.eNS_URI);

		// Obtain or create and register package
		SnomedRefSetPackageImpl theSnomedRefSetPackage = (SnomedRefSetPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SnomedRefSetPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new SnomedRefSetPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theSnomedRefSetPackage.createPackageContents();

		// Initialize created meta-data
		theSnomedRefSetPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theSnomedRefSetPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(SnomedRefSetPackage.eNS_URI, theSnomedRefSetPackage);
		return theSnomedRefSetPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedRefSet() {
		return snomedRefSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSet_Type() {
		return (EAttribute)snomedRefSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSet_ReferencedComponentType() {
		return (EAttribute)snomedRefSetEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSet_IdentifierId() {
		return (EAttribute)snomedRefSetEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedRegularRefSet() {
		return snomedRegularRefSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSnomedRegularRefSet_Members() {
		return (EReference)snomedRegularRefSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedStructuralRefSet() {
		return snomedStructuralRefSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedMappingRefSet() {
		return snomedMappingRefSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMappingRefSet_MapTargetComponentType() {
		return (EAttribute)snomedMappingRefSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedConcreteDataTypeRefSet() {
		return snomedConcreteDataTypeRefSetEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSet_DataType() {
		return (EAttribute)snomedConcreteDataTypeRefSetEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedRefSetMember() {
		return snomedRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_ReferencedComponentType() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_EffectiveTime() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_Active() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getSnomedRefSetMember_RefSet() {
		return (EReference)snomedRefSetMemberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_Released() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_ReferencedComponentId() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_ModuleId() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_RefSetIdentifierId() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(7);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedRefSetMember_Uuid() {
		return (EAttribute)snomedRefSetMemberEClass.getEStructuralFeatures().get(8);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedDescriptionTypeRefSetMember() {
		return snomedDescriptionTypeRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedDescriptionTypeRefSetMember_DescriptionFormat() {
		return (EAttribute)snomedDescriptionTypeRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedDescriptionTypeRefSetMember_DescriptionLength() {
		return (EAttribute)snomedDescriptionTypeRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedSimpleMapRefSetMember() {
		return snomedSimpleMapRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedSimpleMapRefSetMember_MapTargetComponentId() {
		return (EAttribute)snomedSimpleMapRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedSimpleMapRefSetMember_MapTargetComponentType() {
		return (EAttribute)snomedSimpleMapRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedSimpleMapRefSetMember_MapTargetComponentDescription() {
		return (EAttribute)snomedSimpleMapRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedComplexMapRefSetMember() {
		return snomedComplexMapRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedComplexMapRefSetMember_MapGroup() {
		return (EAttribute)snomedComplexMapRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedComplexMapRefSetMember_MapPriority() {
		return (EAttribute)snomedComplexMapRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedComplexMapRefSetMember_MapRule() {
		return (EAttribute)snomedComplexMapRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedComplexMapRefSetMember_MapAdvice() {
		return (EAttribute)snomedComplexMapRefSetMemberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedComplexMapRefSetMember_CorrelationId() {
		return (EAttribute)snomedComplexMapRefSetMemberEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedComplexMapRefSetMember_MapCategoryId() {
		return (EAttribute)snomedComplexMapRefSetMemberEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedQueryRefSetMember() {
		return snomedQueryRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedQueryRefSetMember_Query() {
		return (EAttribute)snomedQueryRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedAttributeValueRefSetMember() {
		return snomedAttributeValueRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedAttributeValueRefSetMember_ValueId() {
		return (EAttribute)snomedAttributeValueRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedLanguageRefSetMember() {
		return snomedLanguageRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedLanguageRefSetMember_AcceptabilityId() {
		return (EAttribute)snomedLanguageRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedConcreteDataTypeRefSetMember() {
		return snomedConcreteDataTypeRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSetMember_SerializedValue() {
		return (EAttribute)snomedConcreteDataTypeRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSetMember_Label() {
		return (EAttribute)snomedConcreteDataTypeRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSetMember_UomComponentId() {
		return (EAttribute)snomedConcreteDataTypeRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSetMember_OperatorComponentId() {
		return (EAttribute)snomedConcreteDataTypeRefSetMemberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSetMember_DataType() {
		return (EAttribute)snomedConcreteDataTypeRefSetMemberEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedConcreteDataTypeRefSetMember_CharacteristicTypeId() {
		return (EAttribute)snomedConcreteDataTypeRefSetMemberEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedAssociationRefSetMember() {
		return snomedAssociationRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedAssociationRefSetMember_TargetComponentId() {
		return (EAttribute)snomedAssociationRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedAssociationRefSetMember_TargetComponentType() {
		return (EAttribute)snomedAssociationRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedModuleDependencyRefSetMember() {
		return snomedModuleDependencyRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedModuleDependencyRefSetMember_SourceEffectiveTime() {
		return (EAttribute)snomedModuleDependencyRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedModuleDependencyRefSetMember_TargetEffectiveTime() {
		return (EAttribute)snomedModuleDependencyRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedAnnotationRefSetMember() {
		return snomedAnnotationRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedAnnotationRefSetMember_Annotation() {
		return (EAttribute)snomedAnnotationRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedOWLExpressionRefSetMember() {
		return snomedOWLExpressionRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedOWLExpressionRefSetMember_OwlExpression() {
		return (EAttribute)snomedOWLExpressionRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedMRCMDomainRefSetMember() {
		return snomedMRCMDomainRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_DomainConstraint() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_ParentDomain() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_ProximalPrimitiveConstraint() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_ProximalPrimitiveRefinement() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_DomainTemplateForPrecoordination() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_DomainTemplateForPostcoordination() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMDomainRefSetMember_EditorialGuideReference() {
		return (EAttribute)snomedMRCMDomainRefSetMemberEClass.getEStructuralFeatures().get(6);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedMRCMAttributeDomainRefSetMember() {
		return snomedMRCMAttributeDomainRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeDomainRefSetMember_DomainId() {
		return (EAttribute)snomedMRCMAttributeDomainRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeDomainRefSetMember_Grouped() {
		return (EAttribute)snomedMRCMAttributeDomainRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeDomainRefSetMember_AttributeCardinality() {
		return (EAttribute)snomedMRCMAttributeDomainRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeDomainRefSetMember_AttributeInGroupCardinality() {
		return (EAttribute)snomedMRCMAttributeDomainRefSetMemberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeDomainRefSetMember_RuleStrengthId() {
		return (EAttribute)snomedMRCMAttributeDomainRefSetMemberEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeDomainRefSetMember_ContentTypeId() {
		return (EAttribute)snomedMRCMAttributeDomainRefSetMemberEClass.getEStructuralFeatures().get(5);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedMRCMAttributeRangeRefSetMember() {
		return snomedMRCMAttributeRangeRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeRangeRefSetMember_RangeConstraint() {
		return (EAttribute)snomedMRCMAttributeRangeRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeRangeRefSetMember_AttributeRule() {
		return (EAttribute)snomedMRCMAttributeRangeRefSetMemberEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeRangeRefSetMember_RuleStrengthId() {
		return (EAttribute)snomedMRCMAttributeRangeRefSetMemberEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMAttributeRangeRefSetMember_ContentTypeId() {
		return (EAttribute)snomedMRCMAttributeRangeRefSetMemberEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getSnomedMRCMModuleScopeRefSetMember() {
		return snomedMRCMModuleScopeRefSetMemberEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getSnomedMRCMModuleScopeRefSetMember_MrcmRuleRefsetId() {
		return (EAttribute)snomedMRCMModuleScopeRefSetMemberEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getSnomedRefSetType() {
		return snomedRefSetTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EEnum getDataType() {
		return dataTypeEEnum;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SnomedRefSetFactory getSnomedRefSetFactory() {
		return (SnomedRefSetFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		snomedRefSetEClass = createEClass(SNOMED_REF_SET);
		createEAttribute(snomedRefSetEClass, SNOMED_REF_SET__TYPE);
		createEAttribute(snomedRefSetEClass, SNOMED_REF_SET__REFERENCED_COMPONENT_TYPE);
		createEAttribute(snomedRefSetEClass, SNOMED_REF_SET__IDENTIFIER_ID);

		snomedRegularRefSetEClass = createEClass(SNOMED_REGULAR_REF_SET);
		createEReference(snomedRegularRefSetEClass, SNOMED_REGULAR_REF_SET__MEMBERS);

		snomedStructuralRefSetEClass = createEClass(SNOMED_STRUCTURAL_REF_SET);

		snomedMappingRefSetEClass = createEClass(SNOMED_MAPPING_REF_SET);
		createEAttribute(snomedMappingRefSetEClass, SNOMED_MAPPING_REF_SET__MAP_TARGET_COMPONENT_TYPE);

		snomedConcreteDataTypeRefSetEClass = createEClass(SNOMED_CONCRETE_DATA_TYPE_REF_SET);
		createEAttribute(snomedConcreteDataTypeRefSetEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET__DATA_TYPE);

		snomedRefSetMemberEClass = createEClass(SNOMED_REF_SET_MEMBER);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__ACTIVE);
		createEReference(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__REF_SET);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__RELEASED);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__MODULE_ID);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID);
		createEAttribute(snomedRefSetMemberEClass, SNOMED_REF_SET_MEMBER__UUID);

		snomedDescriptionTypeRefSetMemberEClass = createEClass(SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER);
		createEAttribute(snomedDescriptionTypeRefSetMemberEClass, SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_FORMAT);
		createEAttribute(snomedDescriptionTypeRefSetMemberEClass, SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_LENGTH);

		snomedSimpleMapRefSetMemberEClass = createEClass(SNOMED_SIMPLE_MAP_REF_SET_MEMBER);
		createEAttribute(snomedSimpleMapRefSetMemberEClass, SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID);
		createEAttribute(snomedSimpleMapRefSetMemberEClass, SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_TYPE);
		createEAttribute(snomedSimpleMapRefSetMemberEClass, SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION);

		snomedComplexMapRefSetMemberEClass = createEClass(SNOMED_COMPLEX_MAP_REF_SET_MEMBER);
		createEAttribute(snomedComplexMapRefSetMemberEClass, SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_GROUP);
		createEAttribute(snomedComplexMapRefSetMemberEClass, SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_PRIORITY);
		createEAttribute(snomedComplexMapRefSetMemberEClass, SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_RULE);
		createEAttribute(snomedComplexMapRefSetMemberEClass, SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_ADVICE);
		createEAttribute(snomedComplexMapRefSetMemberEClass, SNOMED_COMPLEX_MAP_REF_SET_MEMBER__CORRELATION_ID);
		createEAttribute(snomedComplexMapRefSetMemberEClass, SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_CATEGORY_ID);

		snomedQueryRefSetMemberEClass = createEClass(SNOMED_QUERY_REF_SET_MEMBER);
		createEAttribute(snomedQueryRefSetMemberEClass, SNOMED_QUERY_REF_SET_MEMBER__QUERY);

		snomedAttributeValueRefSetMemberEClass = createEClass(SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER);
		createEAttribute(snomedAttributeValueRefSetMemberEClass, SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__VALUE_ID);

		snomedLanguageRefSetMemberEClass = createEClass(SNOMED_LANGUAGE_REF_SET_MEMBER);
		createEAttribute(snomedLanguageRefSetMemberEClass, SNOMED_LANGUAGE_REF_SET_MEMBER__ACCEPTABILITY_ID);

		snomedConcreteDataTypeRefSetMemberEClass = createEClass(SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER);
		createEAttribute(snomedConcreteDataTypeRefSetMemberEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__SERIALIZED_VALUE);
		createEAttribute(snomedConcreteDataTypeRefSetMemberEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__LABEL);
		createEAttribute(snomedConcreteDataTypeRefSetMemberEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__UOM_COMPONENT_ID);
		createEAttribute(snomedConcreteDataTypeRefSetMemberEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__OPERATOR_COMPONENT_ID);
		createEAttribute(snomedConcreteDataTypeRefSetMemberEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__DATA_TYPE);
		createEAttribute(snomedConcreteDataTypeRefSetMemberEClass, SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__CHARACTERISTIC_TYPE_ID);

		snomedAssociationRefSetMemberEClass = createEClass(SNOMED_ASSOCIATION_REF_SET_MEMBER);
		createEAttribute(snomedAssociationRefSetMemberEClass, SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_ID);
		createEAttribute(snomedAssociationRefSetMemberEClass, SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_TYPE);

		snomedModuleDependencyRefSetMemberEClass = createEClass(SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER);
		createEAttribute(snomedModuleDependencyRefSetMemberEClass, SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__SOURCE_EFFECTIVE_TIME);
		createEAttribute(snomedModuleDependencyRefSetMemberEClass, SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__TARGET_EFFECTIVE_TIME);

		snomedAnnotationRefSetMemberEClass = createEClass(SNOMED_ANNOTATION_REF_SET_MEMBER);
		createEAttribute(snomedAnnotationRefSetMemberEClass, SNOMED_ANNOTATION_REF_SET_MEMBER__ANNOTATION);

		snomedOWLExpressionRefSetMemberEClass = createEClass(SNOMED_OWL_EXPRESSION_REF_SET_MEMBER);
		createEAttribute(snomedOWLExpressionRefSetMemberEClass, SNOMED_OWL_EXPRESSION_REF_SET_MEMBER__OWL_EXPRESSION);

		snomedMRCMDomainRefSetMemberEClass = createEClass(SNOMED_MRCM_DOMAIN_REF_SET_MEMBER);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_CONSTRAINT);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PARENT_DOMAIN);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_CONSTRAINT);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_REFINEMENT);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_PRECOORDINATION);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_POSTCOORDINATION);
		createEAttribute(snomedMRCMDomainRefSetMemberEClass, SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__EDITORIAL_GUIDE_REFERENCE);

		snomedMRCMAttributeDomainRefSetMemberEClass = createEClass(SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER);
		createEAttribute(snomedMRCMAttributeDomainRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__DOMAIN_ID);
		createEAttribute(snomedMRCMAttributeDomainRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__GROUPED);
		createEAttribute(snomedMRCMAttributeDomainRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_CARDINALITY);
		createEAttribute(snomedMRCMAttributeDomainRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_IN_GROUP_CARDINALITY);
		createEAttribute(snomedMRCMAttributeDomainRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__RULE_STRENGTH_ID);
		createEAttribute(snomedMRCMAttributeDomainRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__CONTENT_TYPE_ID);

		snomedMRCMAttributeRangeRefSetMemberEClass = createEClass(SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER);
		createEAttribute(snomedMRCMAttributeRangeRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RANGE_CONSTRAINT);
		createEAttribute(snomedMRCMAttributeRangeRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__ATTRIBUTE_RULE);
		createEAttribute(snomedMRCMAttributeRangeRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RULE_STRENGTH_ID);
		createEAttribute(snomedMRCMAttributeRangeRefSetMemberEClass, SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__CONTENT_TYPE_ID);

		snomedMRCMModuleScopeRefSetMemberEClass = createEClass(SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER);
		createEAttribute(snomedMRCMModuleScopeRefSetMemberEClass, SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__MRCM_RULE_REFSET_ID);

		// Create enums
		snomedRefSetTypeEEnum = createEEnum(SNOMED_REF_SET_TYPE);
		dataTypeEEnum = createEEnum(DATA_TYPE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes
		snomedRegularRefSetEClass.getESuperTypes().add(this.getSnomedRefSet());
		snomedStructuralRefSetEClass.getESuperTypes().add(this.getSnomedRefSet());
		snomedMappingRefSetEClass.getESuperTypes().add(this.getSnomedRegularRefSet());
		snomedConcreteDataTypeRefSetEClass.getESuperTypes().add(this.getSnomedStructuralRefSet());
		snomedDescriptionTypeRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedSimpleMapRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedComplexMapRefSetMemberEClass.getESuperTypes().add(this.getSnomedSimpleMapRefSetMember());
		snomedQueryRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedAttributeValueRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedLanguageRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedConcreteDataTypeRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedAssociationRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedModuleDependencyRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedAnnotationRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedOWLExpressionRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedMRCMDomainRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedMRCMAttributeDomainRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedMRCMAttributeRangeRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());
		snomedMRCMModuleScopeRefSetMemberEClass.getESuperTypes().add(this.getSnomedRefSetMember());

		// Initialize classes and features; add operations and parameters
		initEClass(snomedRefSetEClass, SnomedRefSet.class, "SnomedRefSet", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedRefSet_Type(), this.getSnomedRefSetType(), "type", null, 1, 1, SnomedRefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSet_ReferencedComponentType(), ecorePackage.getEShort(), "referencedComponentType", null, 1, 1, SnomedRefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSet_IdentifierId(), ecorePackage.getEString(), "identifierId", null, 1, 1, SnomedRefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedRegularRefSetEClass, SnomedRegularRefSet.class, "SnomedRegularRefSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEReference(getSnomedRegularRefSet_Members(), this.getSnomedRefSetMember(), null, "members", null, 0, -1, SnomedRegularRefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);

		initEClass(snomedStructuralRefSetEClass, SnomedStructuralRefSet.class, "SnomedStructuralRefSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

		initEClass(snomedMappingRefSetEClass, SnomedMappingRefSet.class, "SnomedMappingRefSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedMappingRefSet_MapTargetComponentType(), ecorePackage.getEShort(), "mapTargetComponentType", "-1", 1, 1, SnomedMappingRefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedConcreteDataTypeRefSetEClass, SnomedConcreteDataTypeRefSet.class, "SnomedConcreteDataTypeRefSet", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedConcreteDataTypeRefSet_DataType(), this.getDataType(), "dataType", null, 1, 1, SnomedConcreteDataTypeRefSet.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedRefSetMemberEClass, SnomedRefSetMember.class, "SnomedRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedRefSetMember_ReferencedComponentType(), ecorePackage.getEShort(), "referencedComponentType", null, 1, 1, SnomedRefSetMember.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_EffectiveTime(), ecorePackage.getEDate(), "effectiveTime", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_Active(), ecorePackage.getEBoolean(), "active", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEReference(getSnomedRefSetMember_RefSet(), this.getSnomedRefSet(), null, "refSet", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_Released(), ecorePackage.getEBoolean(), "released", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_ReferencedComponentId(), ecorePackage.getEString(), "referencedComponentId", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_ModuleId(), ecorePackage.getEString(), "moduleId", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_RefSetIdentifierId(), ecorePackage.getEString(), "refSetIdentifierId", "", 1, 1, SnomedRefSetMember.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedRefSetMember_Uuid(), ecorePackage.getEString(), "uuid", null, 1, 1, SnomedRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedDescriptionTypeRefSetMemberEClass, SnomedDescriptionTypeRefSetMember.class, "SnomedDescriptionTypeRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedDescriptionTypeRefSetMember_DescriptionFormat(), ecorePackage.getEString(), "descriptionFormat", null, 1, 1, SnomedDescriptionTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedDescriptionTypeRefSetMember_DescriptionLength(), ecorePackage.getEInt(), "descriptionLength", "0", 1, 1, SnomedDescriptionTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedSimpleMapRefSetMemberEClass, SnomedSimpleMapRefSetMember.class, "SnomedSimpleMapRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedSimpleMapRefSetMember_MapTargetComponentId(), ecorePackage.getEString(), "mapTargetComponentId", null, 1, 1, SnomedSimpleMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedSimpleMapRefSetMember_MapTargetComponentType(), ecorePackage.getEShort(), "mapTargetComponentType", null, 1, 1, SnomedSimpleMapRefSetMember.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedSimpleMapRefSetMember_MapTargetComponentDescription(), ecorePackage.getEString(), "mapTargetComponentDescription", null, 1, 1, SnomedSimpleMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedComplexMapRefSetMemberEClass, SnomedComplexMapRefSetMember.class, "SnomedComplexMapRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedComplexMapRefSetMember_MapGroup(), ecorePackage.getEInt(), "mapGroup", null, 1, 1, SnomedComplexMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedComplexMapRefSetMember_MapPriority(), ecorePackage.getEInt(), "mapPriority", null, 1, 1, SnomedComplexMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedComplexMapRefSetMember_MapRule(), ecorePackage.getEString(), "mapRule", null, 1, 1, SnomedComplexMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedComplexMapRefSetMember_MapAdvice(), ecorePackage.getEString(), "mapAdvice", null, 1, 1, SnomedComplexMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedComplexMapRefSetMember_CorrelationId(), ecorePackage.getEString(), "correlationId", null, 1, 1, SnomedComplexMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedComplexMapRefSetMember_MapCategoryId(), ecorePackage.getEString(), "mapCategoryId", null, 1, 1, SnomedComplexMapRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedQueryRefSetMemberEClass, SnomedQueryRefSetMember.class, "SnomedQueryRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedQueryRefSetMember_Query(), ecorePackage.getEString(), "query", null, 1, 1, SnomedQueryRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedAttributeValueRefSetMemberEClass, SnomedAttributeValueRefSetMember.class, "SnomedAttributeValueRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedAttributeValueRefSetMember_ValueId(), ecorePackage.getEString(), "valueId", null, 1, 1, SnomedAttributeValueRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedLanguageRefSetMemberEClass, SnomedLanguageRefSetMember.class, "SnomedLanguageRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedLanguageRefSetMember_AcceptabilityId(), ecorePackage.getEString(), "acceptabilityId", null, 1, 1, SnomedLanguageRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedConcreteDataTypeRefSetMemberEClass, SnomedConcreteDataTypeRefSetMember.class, "SnomedConcreteDataTypeRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedConcreteDataTypeRefSetMember_SerializedValue(), ecorePackage.getEString(), "serializedValue", null, 1, 1, SnomedConcreteDataTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedConcreteDataTypeRefSetMember_Label(), ecorePackage.getEString(), "label", null, 1, 1, SnomedConcreteDataTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedConcreteDataTypeRefSetMember_UomComponentId(), ecorePackage.getEString(), "uomComponentId", null, 1, 1, SnomedConcreteDataTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedConcreteDataTypeRefSetMember_OperatorComponentId(), ecorePackage.getEString(), "operatorComponentId", null, 1, 1, SnomedConcreteDataTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedConcreteDataTypeRefSetMember_DataType(), this.getDataType(), "dataType", null, 1, 1, SnomedConcreteDataTypeRefSetMember.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedConcreteDataTypeRefSetMember_CharacteristicTypeId(), ecorePackage.getEString(), "characteristicTypeId", null, 1, 1, SnomedConcreteDataTypeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedAssociationRefSetMemberEClass, SnomedAssociationRefSetMember.class, "SnomedAssociationRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedAssociationRefSetMember_TargetComponentId(), ecorePackage.getEString(), "targetComponentId", null, 1, 1, SnomedAssociationRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedAssociationRefSetMember_TargetComponentType(), ecorePackage.getEShort(), "targetComponentType", null, 1, 1, SnomedAssociationRefSetMember.class, IS_TRANSIENT, !IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED);

		initEClass(snomedModuleDependencyRefSetMemberEClass, SnomedModuleDependencyRefSetMember.class, "SnomedModuleDependencyRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedModuleDependencyRefSetMember_SourceEffectiveTime(), ecorePackage.getEDate(), "sourceEffectiveTime", null, 1, 1, SnomedModuleDependencyRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedModuleDependencyRefSetMember_TargetEffectiveTime(), ecorePackage.getEDate(), "targetEffectiveTime", null, 1, 1, SnomedModuleDependencyRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedAnnotationRefSetMemberEClass, SnomedAnnotationRefSetMember.class, "SnomedAnnotationRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedAnnotationRefSetMember_Annotation(), ecorePackage.getEString(), "annotation", null, 1, 1, SnomedAnnotationRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedOWLExpressionRefSetMemberEClass, SnomedOWLExpressionRefSetMember.class, "SnomedOWLExpressionRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedOWLExpressionRefSetMember_OwlExpression(), ecorePackage.getEString(), "owlExpression", null, 1, 1, SnomedOWLExpressionRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedMRCMDomainRefSetMemberEClass, SnomedMRCMDomainRefSetMember.class, "SnomedMRCMDomainRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedMRCMDomainRefSetMember_DomainConstraint(), ecorePackage.getEString(), "domainConstraint", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMDomainRefSetMember_ParentDomain(), ecorePackage.getEString(), "parentDomain", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMDomainRefSetMember_ProximalPrimitiveConstraint(), ecorePackage.getEString(), "proximalPrimitiveConstraint", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMDomainRefSetMember_ProximalPrimitiveRefinement(), ecorePackage.getEString(), "proximalPrimitiveRefinement", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMDomainRefSetMember_DomainTemplateForPrecoordination(), ecorePackage.getEString(), "domainTemplateForPrecoordination", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMDomainRefSetMember_DomainTemplateForPostcoordination(), ecorePackage.getEString(), "domainTemplateForPostcoordination", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMDomainRefSetMember_EditorialGuideReference(), ecorePackage.getEString(), "editorialGuideReference", null, 1, 1, SnomedMRCMDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedMRCMAttributeDomainRefSetMemberEClass, SnomedMRCMAttributeDomainRefSetMember.class, "SnomedMRCMAttributeDomainRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedMRCMAttributeDomainRefSetMember_DomainId(), ecorePackage.getEString(), "domainId", null, 1, 1, SnomedMRCMAttributeDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeDomainRefSetMember_Grouped(), ecorePackage.getEBoolean(), "grouped", null, 1, 1, SnomedMRCMAttributeDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeDomainRefSetMember_AttributeCardinality(), ecorePackage.getEString(), "attributeCardinality", null, 1, 1, SnomedMRCMAttributeDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeDomainRefSetMember_AttributeInGroupCardinality(), ecorePackage.getEString(), "attributeInGroupCardinality", null, 1, 1, SnomedMRCMAttributeDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeDomainRefSetMember_RuleStrengthId(), ecorePackage.getEString(), "ruleStrengthId", null, 1, 1, SnomedMRCMAttributeDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeDomainRefSetMember_ContentTypeId(), ecorePackage.getEString(), "contentTypeId", null, 1, 1, SnomedMRCMAttributeDomainRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedMRCMAttributeRangeRefSetMemberEClass, SnomedMRCMAttributeRangeRefSetMember.class, "SnomedMRCMAttributeRangeRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedMRCMAttributeRangeRefSetMember_RangeConstraint(), ecorePackage.getEString(), "rangeConstraint", null, 1, 1, SnomedMRCMAttributeRangeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeRangeRefSetMember_AttributeRule(), ecorePackage.getEString(), "attributeRule", null, 1, 1, SnomedMRCMAttributeRangeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeRangeRefSetMember_RuleStrengthId(), ecorePackage.getEString(), "ruleStrengthId", null, 1, 1, SnomedMRCMAttributeRangeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
		initEAttribute(getSnomedMRCMAttributeRangeRefSetMember_ContentTypeId(), ecorePackage.getEString(), "contentTypeId", null, 1, 1, SnomedMRCMAttributeRangeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		initEClass(snomedMRCMModuleScopeRefSetMemberEClass, SnomedMRCMModuleScopeRefSetMember.class, "SnomedMRCMModuleScopeRefSetMember", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
		initEAttribute(getSnomedMRCMModuleScopeRefSetMember_MrcmRuleRefsetId(), ecorePackage.getEString(), "mrcmRuleRefsetId", null, 1, 1, SnomedMRCMModuleScopeRefSetMember.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

		// Initialize enums and add enum literals
		initEEnum(snomedRefSetTypeEEnum, SnomedRefSetType.class, "SnomedRefSetType");
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.SIMPLE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.SIMPLE_MAP);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.LANGUAGE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.ATTRIBUTE_VALUE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.QUERY);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.COMPLEX_MAP);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.DESCRIPTION_TYPE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.CONCRETE_DATA_TYPE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.ASSOCIATION);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.MODULE_DEPENDENCY);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.EXTENDED_MAP);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.SIMPLE_MAP_WITH_DESCRIPTION);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.OWL_AXIOM);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.OWL_ONTOLOGY);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.MRCM_DOMAIN);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.MRCM_ATTRIBUTE_DOMAIN);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.MRCM_ATTRIBUTE_RANGE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.MRCM_MODULE_SCOPE);
		addEEnumLiteral(snomedRefSetTypeEEnum, SnomedRefSetType.ANNOTATION);

		initEEnum(dataTypeEEnum, DataType.class, "DataType");
		addEEnumLiteral(dataTypeEEnum, DataType.INTEGER);
		addEEnumLiteral(dataTypeEEnum, DataType.DECIMAL);
		addEEnumLiteral(dataTypeEEnum, DataType.BOOLEAN);
		addEEnumLiteral(dataTypeEEnum, DataType.DATE);
		addEEnumLiteral(dataTypeEEnum, DataType.STRING);

		// Create resource
		createResource(eNS_URI);

		// Create annotations
		// http://www.eclipse.org/CDO/DBStore
		createDBStoreAnnotations();
	}

	/**
	 * Initializes the annotations for <b>http://www.eclipse.org/CDO/DBStore</b>.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void createDBStoreAnnotations() {
		String source = "http://www.eclipse.org/CDO/DBStore";	
		addAnnotation
		  (getSnomedComplexMapRefSetMember_MapRule(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedComplexMapRefSetMember_MapAdvice(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedConcreteDataTypeRefSetMember_Label(), 
		   source, 
		   new String[] {
			 "columnName", "label0"
		   });	
		addAnnotation
		  (getSnomedAnnotationRefSetMember_Annotation(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedOWLExpressionRefSetMember_OwlExpression(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMDomainRefSetMember_DomainConstraint(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMDomainRefSetMember_ParentDomain(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMDomainRefSetMember_ProximalPrimitiveConstraint(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMDomainRefSetMember_ProximalPrimitiveRefinement(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMDomainRefSetMember_DomainTemplateForPrecoordination(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMDomainRefSetMember_DomainTemplateForPostcoordination(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMAttributeRangeRefSetMember_RangeConstraint(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });	
		addAnnotation
		  (getSnomedMRCMAttributeRangeRefSetMember_AttributeRule(), 
		   source, 
		   new String[] {
			 "columnType", "LONG VARCHAR",
			 "columnLength", "32768"
		   });
	}

} //SnomedRefSetPackageImpl
