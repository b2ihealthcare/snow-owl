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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetFactory
 * @model kind="package"
 * @generated
 */
public interface SnomedRefSetPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "snomedrefset";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://b2international.com/snowowl/snomed/refset/1.0";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "sctrefset";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	SnomedRefSetPackage eINSTANCE = com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl.init();

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetImpl <em>Snomed Ref Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRefSet()
	 * @generated
	 */
	int SNOMED_REF_SET = 0;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET__TYPE = 0;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET__REFERENCED_COMPONENT_TYPE = 1;

	/**
	 * The feature id for the '<em><b>Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET__IDENTIFIER_ID = 2;

	/**
	 * The number of structural features of the '<em>Snomed Ref Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRegularRefSetImpl <em>Snomed Regular Ref Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRegularRefSetImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRegularRefSet()
	 * @generated
	 */
	int SNOMED_REGULAR_REF_SET = 1;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REGULAR_REF_SET__TYPE = SNOMED_REF_SET__TYPE;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REGULAR_REF_SET__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REGULAR_REF_SET__IDENTIFIER_ID = SNOMED_REF_SET__IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REGULAR_REF_SET__MEMBERS = SNOMED_REF_SET_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Regular Ref Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REGULAR_REF_SET_FEATURE_COUNT = SNOMED_REF_SET_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedStructuralRefSetImpl <em>Snomed Structural Ref Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedStructuralRefSetImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedStructuralRefSet()
	 * @generated
	 */
	int SNOMED_STRUCTURAL_REF_SET = 2;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_STRUCTURAL_REF_SET__TYPE = SNOMED_REF_SET__TYPE;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_STRUCTURAL_REF_SET__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_STRUCTURAL_REF_SET__IDENTIFIER_ID = SNOMED_REF_SET__IDENTIFIER_ID;

	/**
	 * The number of structural features of the '<em>Snomed Structural Ref Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_STRUCTURAL_REF_SET_FEATURE_COUNT = SNOMED_REF_SET_FEATURE_COUNT + 0;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMappingRefSetImpl <em>Snomed Mapping Ref Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMappingRefSetImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMappingRefSet()
	 * @generated
	 */
	int SNOMED_MAPPING_REF_SET = 3;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MAPPING_REF_SET__TYPE = SNOMED_REGULAR_REF_SET__TYPE;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MAPPING_REF_SET__REFERENCED_COMPONENT_TYPE = SNOMED_REGULAR_REF_SET__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MAPPING_REF_SET__IDENTIFIER_ID = SNOMED_REGULAR_REF_SET__IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Members</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MAPPING_REF_SET__MEMBERS = SNOMED_REGULAR_REF_SET__MEMBERS;

	/**
	 * The feature id for the '<em><b>Map Target Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MAPPING_REF_SET__MAP_TARGET_COMPONENT_TYPE = SNOMED_REGULAR_REF_SET_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Mapping Ref Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MAPPING_REF_SET_FEATURE_COUNT = SNOMED_REGULAR_REF_SET_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetImpl <em>Snomed Concrete Data Type Ref Set</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedConcreteDataTypeRefSet()
	 * @generated
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET = 4;

	/**
	 * The feature id for the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET__TYPE = SNOMED_STRUCTURAL_REF_SET__TYPE;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET__REFERENCED_COMPONENT_TYPE = SNOMED_STRUCTURAL_REF_SET__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET__IDENTIFIER_ID = SNOMED_STRUCTURAL_REF_SET__IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Data Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET__DATA_TYPE = SNOMED_STRUCTURAL_REF_SET_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Concrete Data Type Ref Set</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_FEATURE_COUNT = SNOMED_STRUCTURAL_REF_SET_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl <em>Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRefSetMember()
	 * @generated
	 */
	int SNOMED_REF_SET_MEMBER = 5;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = 0;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME = 1;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__ACTIVE = 2;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__REF_SET = 3;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__RELEASED = 4;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = 5;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__MODULE_ID = 6;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = 7;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER__UUID = 8;

	/**
	 * The number of structural features of the '<em>Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_REF_SET_MEMBER_FEATURE_COUNT = 9;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedDescriptionTypeRefSetMemberImpl <em>Snomed Description Type Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedDescriptionTypeRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedDescriptionTypeRefSetMember()
	 * @generated
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER = 6;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Description Format</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_FORMAT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Description Length</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_LENGTH = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Snomed Description Type Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl <em>Snomed Simple Map Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedSimpleMapRefSetMember()
	 * @generated
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER = 7;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Map Target Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Map Target Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Map Target Component Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The number of structural features of the '<em>Snomed Simple Map Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 3;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl <em>Snomed Complex Map Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER = 8;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__ACTIVE = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__REF_SET = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__RELEASED = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MODULE_ID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__UUID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Map Target Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Map Target Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_TYPE = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Map Target Component Description</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION = SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION;

	/**
	 * The feature id for the '<em><b>Map Group</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_GROUP = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Map Priority</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_PRIORITY = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Map Rule</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_RULE = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Map Advice</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_ADVICE = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Correlation Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__CORRELATION_ID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Map Category Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_CATEGORY_ID = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Snomed Complex Map Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_COMPLEX_MAP_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_SIMPLE_MAP_REF_SET_MEMBER_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedQueryRefSetMemberImpl <em>Snomed Query Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedQueryRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedQueryRefSetMember()
	 * @generated
	 */
	int SNOMED_QUERY_REF_SET_MEMBER = 9;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Query</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER__QUERY = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Query Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_QUERY_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAttributeValueRefSetMemberImpl <em>Snomed Attribute Value Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAttributeValueRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedAttributeValueRefSetMember()
	 * @generated
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER = 10;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Value Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__VALUE_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Attribute Value Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedLanguageRefSetMemberImpl <em>Snomed Language Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedLanguageRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedLanguageRefSetMember()
	 * @generated
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER = 11;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Acceptability Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER__ACCEPTABILITY_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Language Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_LANGUAGE_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl <em>Snomed Concrete Data Type Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER = 12;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Serialized Value</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__SERIALIZED_VALUE = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Label</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__LABEL = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Uom Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__UOM_COMPONENT_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Operator Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__OPERATOR_COMPONENT_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Data Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__DATA_TYPE = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Characteristic Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__CHARACTERISTIC_TYPE_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Snomed Concrete Data Type Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAssociationRefSetMemberImpl <em>Snomed Association Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAssociationRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedAssociationRefSetMember()
	 * @generated
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER = 13;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Target Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Snomed Association Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ASSOCIATION_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedModuleDependencyRefSetMemberImpl <em>Snomed Module Dependency Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedModuleDependencyRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedModuleDependencyRefSetMember()
	 * @generated
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER = 14;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Source Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__SOURCE_EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Target Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__TARGET_EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The number of structural features of the '<em>Snomed Module Dependency Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAnnotationRefSetMemberImpl <em>Snomed Annotation Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAnnotationRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedAnnotationRefSetMember()
	 * @generated
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER = 15;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Annotation</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER__ANNOTATION = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed Annotation Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_ANNOTATION_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl <em>Snomed MRCM Domain Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER = 16;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Domain Constraint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_CONSTRAINT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Parent Domain</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PARENT_DOMAIN = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Proximal Primitive Constraint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_CONSTRAINT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Proximal Primitive Refinement</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_REFINEMENT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Domain Template For Precoordination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_PRECOORDINATION = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Domain Template For Postcoordination</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_POSTCOORDINATION = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 5;

	/**
	 * The feature id for the '<em><b>Editorial Guide Reference</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__EDITORIAL_GUIDE_REFERENCE = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 6;

	/**
	 * The number of structural features of the '<em>Snomed MRCM Domain Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_DOMAIN_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 7;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl <em>Snomed MRCM Attribute Domain Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER = 17;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Domain Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__DOMAIN_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Grouped</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__GROUPED = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Attribute Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_CARDINALITY = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Attribute In Group Cardinality</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_IN_GROUP_CARDINALITY = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 3;

	/**
	 * The feature id for the '<em><b>Rule Strength Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__RULE_STRENGTH_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 4;

	/**
	 * The feature id for the '<em><b>Content Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__CONTENT_TYPE_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 5;

	/**
	 * The number of structural features of the '<em>Snomed MRCM Attribute Domain Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 6;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl <em>Snomed MRCM Attribute Range Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMAttributeRangeRefSetMember()
	 * @generated
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER = 18;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Range Constraint</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RANGE_CONSTRAINT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The feature id for the '<em><b>Attribute Rule</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__ATTRIBUTE_RULE = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The feature id for the '<em><b>Rule Strength Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RULE_STRENGTH_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 2;

	/**
	 * The feature id for the '<em><b>Content Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__CONTENT_TYPE_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 3;

	/**
	 * The number of structural features of the '<em>Snomed MRCM Attribute Range Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 4;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMModuleScopeRefSetMemberImpl <em>Snomed MRCM Module Scope Ref Set Member</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMModuleScopeRefSetMemberImpl
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMModuleScopeRefSetMember()
	 * @generated
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER = 19;

	/**
	 * The feature id for the '<em><b>Referenced Component Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE;

	/**
	 * The feature id for the '<em><b>Effective Time</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__EFFECTIVE_TIME = SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME;

	/**
	 * The feature id for the '<em><b>Active</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__ACTIVE = SNOMED_REF_SET_MEMBER__ACTIVE;

	/**
	 * The feature id for the '<em><b>Ref Set</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__REF_SET = SNOMED_REF_SET_MEMBER__REF_SET;

	/**
	 * The feature id for the '<em><b>Released</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__RELEASED = SNOMED_REF_SET_MEMBER__RELEASED;

	/**
	 * The feature id for the '<em><b>Referenced Component Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID;

	/**
	 * The feature id for the '<em><b>Module Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__MODULE_ID = SNOMED_REF_SET_MEMBER__MODULE_ID;

	/**
	 * The feature id for the '<em><b>Ref Set Identifier Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID;

	/**
	 * The feature id for the '<em><b>Uuid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__UUID = SNOMED_REF_SET_MEMBER__UUID;

	/**
	 * The feature id for the '<em><b>Mrcm Rule Refset Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__MRCM_RULE_REFSET_ID = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 0;

	/**
	 * The number of structural features of the '<em>Snomed MRCM Module Scope Ref Set Member</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER_FEATURE_COUNT = SNOMED_REF_SET_MEMBER_FEATURE_COUNT + 1;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType <em>Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRefSetType()
	 * @generated
	 */
	int SNOMED_REF_SET_TYPE = 20;

	/**
	 * The meta object id for the '{@link com.b2international.snowowl.snomed.snomedrefset.DataType <em>Data Type</em>}' enum.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getDataType()
	 * @generated
	 */
	int DATA_TYPE = 21;


	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet <em>Snomed Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet
	 * @generated
	 */
	EClass getSnomedRefSet();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getType()
	 * @see #getSnomedRefSet()
	 * @generated
	 */
	EAttribute getSnomedRefSet_Type();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getReferencedComponentType <em>Referenced Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Referenced Component Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getReferencedComponentType()
	 * @see #getSnomedRefSet()
	 * @generated
	 */
	EAttribute getSnomedRefSet_ReferencedComponentType();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getIdentifierId <em>Identifier Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Identifier Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getIdentifierId()
	 * @see #getSnomedRefSet()
	 * @generated
	 */
	EAttribute getSnomedRefSet_IdentifierId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet <em>Snomed Regular Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Regular Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet
	 * @generated
	 */
	EClass getSnomedRegularRefSet();

	/**
	 * Returns the meta object for the containment reference list '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet#getMembers <em>Members</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Members</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRegularRefSet#getMembers()
	 * @see #getSnomedRegularRefSet()
	 * @generated
	 */
	EReference getSnomedRegularRefSet_Members();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet <em>Snomed Structural Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Structural Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedStructuralRefSet
	 * @generated
	 */
	EClass getSnomedStructuralRefSet();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet <em>Snomed Mapping Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Mapping Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet
	 * @generated
	 */
	EClass getSnomedMappingRefSet();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet#getMapTargetComponentType <em>Map Target Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Target Component Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet#getMapTargetComponentType()
	 * @see #getSnomedMappingRefSet()
	 * @generated
	 */
	EAttribute getSnomedMappingRefSet_MapTargetComponentType();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet <em>Snomed Concrete Data Type Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Concrete Data Type Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet
	 * @generated
	 */
	EClass getSnomedConcreteDataTypeRefSet();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet#getDataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet#getDataType()
	 * @see #getSnomedConcreteDataTypeRefSet()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSet_DataType();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember <em>Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember
	 * @generated
	 */
	EClass getSnomedRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getReferencedComponentType <em>Referenced Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Referenced Component Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getReferencedComponentType()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_ReferencedComponentType();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getEffectiveTime <em>Effective Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Effective Time</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getEffectiveTime()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_EffectiveTime();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#isActive <em>Active</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Active</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#isActive()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_Active();

	/**
	 * Returns the meta object for the reference '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getRefSet <em>Ref Set</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Ref Set</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getRefSet()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EReference getSnomedRefSetMember_RefSet();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#isReleased <em>Released</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Released</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#isReleased()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_Released();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getReferencedComponentId <em>Referenced Component Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Referenced Component Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getReferencedComponentId()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_ReferencedComponentId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getModuleId <em>Module Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Module Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getModuleId()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_ModuleId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getRefSetIdentifierId <em>Ref Set Identifier Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Ref Set Identifier Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getRefSetIdentifierId()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_RefSetIdentifierId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getUuid <em>Uuid</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uuid</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetMember#getUuid()
	 * @see #getSnomedRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedRefSetMember_Uuid();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember <em>Snomed Description Type Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Description Type Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember
	 * @generated
	 */
	EClass getSnomedDescriptionTypeRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember#getDescriptionFormat <em>Description Format</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description Format</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember#getDescriptionFormat()
	 * @see #getSnomedDescriptionTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedDescriptionTypeRefSetMember_DescriptionFormat();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember#getDescriptionLength <em>Description Length</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Description Length</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember#getDescriptionLength()
	 * @see #getSnomedDescriptionTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedDescriptionTypeRefSetMember_DescriptionLength();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember <em>Snomed Simple Map Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Simple Map Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember
	 * @generated
	 */
	EClass getSnomedSimpleMapRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentId <em>Map Target Component Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Target Component Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentId()
	 * @see #getSnomedSimpleMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedSimpleMapRefSetMember_MapTargetComponentId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentType <em>Map Target Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Target Component Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentType()
	 * @see #getSnomedSimpleMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedSimpleMapRefSetMember_MapTargetComponentType();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentDescription <em>Map Target Component Description</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Target Component Description</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentDescription()
	 * @see #getSnomedSimpleMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedSimpleMapRefSetMember_MapTargetComponentDescription();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember <em>Snomed Complex Map Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Complex Map Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember
	 * @generated
	 */
	EClass getSnomedComplexMapRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapGroup <em>Map Group</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Group</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapGroup()
	 * @see #getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedComplexMapRefSetMember_MapGroup();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapPriority <em>Map Priority</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Priority</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapPriority()
	 * @see #getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedComplexMapRefSetMember_MapPriority();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapRule <em>Map Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Rule</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapRule()
	 * @see #getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedComplexMapRefSetMember_MapRule();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapAdvice <em>Map Advice</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Advice</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapAdvice()
	 * @see #getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedComplexMapRefSetMember_MapAdvice();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getCorrelationId <em>Correlation Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Correlation Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getCorrelationId()
	 * @see #getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedComplexMapRefSetMember_CorrelationId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapCategoryId <em>Map Category Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Map Category Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedComplexMapRefSetMember#getMapCategoryId()
	 * @see #getSnomedComplexMapRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedComplexMapRefSetMember_MapCategoryId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember <em>Snomed Query Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Query Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember
	 * @generated
	 */
	EClass getSnomedQueryRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember#getQuery <em>Query</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Query</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedQueryRefSetMember#getQuery()
	 * @see #getSnomedQueryRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedQueryRefSetMember_Query();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember <em>Snomed Attribute Value Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Attribute Value Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember
	 * @generated
	 */
	EClass getSnomedAttributeValueRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember#getValueId <em>Value Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Value Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember#getValueId()
	 * @see #getSnomedAttributeValueRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedAttributeValueRefSetMember_ValueId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember <em>Snomed Language Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Language Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember
	 * @generated
	 */
	EClass getSnomedLanguageRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember#getAcceptabilityId <em>Acceptability Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Acceptability Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember#getAcceptabilityId()
	 * @see #getSnomedLanguageRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedLanguageRefSetMember_AcceptabilityId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember <em>Snomed Concrete Data Type Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Concrete Data Type Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember
	 * @generated
	 */
	EClass getSnomedConcreteDataTypeRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getSerializedValue <em>Serialized Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Serialized Value</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getSerializedValue()
	 * @see #getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSetMember_SerializedValue();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getLabel <em>Label</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Label</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getLabel()
	 * @see #getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSetMember_Label();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getUomComponentId <em>Uom Component Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Uom Component Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getUomComponentId()
	 * @see #getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSetMember_UomComponentId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getOperatorComponentId <em>Operator Component Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Operator Component Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getOperatorComponentId()
	 * @see #getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSetMember_OperatorComponentId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getDataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Data Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getDataType()
	 * @see #getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSetMember_DataType();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getCharacteristicTypeId <em>Characteristic Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Characteristic Type Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getCharacteristicTypeId()
	 * @see #getSnomedConcreteDataTypeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedConcreteDataTypeRefSetMember_CharacteristicTypeId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember <em>Snomed Association Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Association Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember
	 * @generated
	 */
	EClass getSnomedAssociationRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember#getTargetComponentId <em>Target Component Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Component Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember#getTargetComponentId()
	 * @see #getSnomedAssociationRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedAssociationRefSetMember_TargetComponentId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember#getTargetComponentType <em>Target Component Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Component Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember#getTargetComponentType()
	 * @see #getSnomedAssociationRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedAssociationRefSetMember_TargetComponentType();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember <em>Snomed Module Dependency Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Module Dependency Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember
	 * @generated
	 */
	EClass getSnomedModuleDependencyRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember#getSourceEffectiveTime <em>Source Effective Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Source Effective Time</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember#getSourceEffectiveTime()
	 * @see #getSnomedModuleDependencyRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedModuleDependencyRefSetMember_SourceEffectiveTime();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember#getTargetEffectiveTime <em>Target Effective Time</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Target Effective Time</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedModuleDependencyRefSetMember#getTargetEffectiveTime()
	 * @see #getSnomedModuleDependencyRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedModuleDependencyRefSetMember_TargetEffectiveTime();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember <em>Snomed Annotation Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed Annotation Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember
	 * @generated
	 */
	EClass getSnomedAnnotationRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember#getAnnotation <em>Annotation</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Annotation</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedAnnotationRefSetMember#getAnnotation()
	 * @see #getSnomedAnnotationRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedAnnotationRefSetMember_Annotation();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember <em>Snomed MRCM Domain Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed MRCM Domain Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember
	 * @generated
	 */
	EClass getSnomedMRCMDomainRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainConstraint <em>Domain Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Domain Constraint</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainConstraint()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_DomainConstraint();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getParentDomain <em>Parent Domain</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Parent Domain</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getParentDomain()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_ParentDomain();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveConstraint <em>Proximal Primitive Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Proximal Primitive Constraint</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveConstraint()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_ProximalPrimitiveConstraint();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveRefinement <em>Proximal Primitive Refinement</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Proximal Primitive Refinement</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getProximalPrimitiveRefinement()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_ProximalPrimitiveRefinement();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPrecoordination <em>Domain Template For Precoordination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Domain Template For Precoordination</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPrecoordination()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_DomainTemplateForPrecoordination();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPostcoordination <em>Domain Template For Postcoordination</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Domain Template For Postcoordination</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getDomainTemplateForPostcoordination()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_DomainTemplateForPostcoordination();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getEditorialGuideReference <em>Editorial Guide Reference</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Editorial Guide Reference</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMDomainRefSetMember#getEditorialGuideReference()
	 * @see #getSnomedMRCMDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMDomainRefSetMember_EditorialGuideReference();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember <em>Snomed MRCM Attribute Domain Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed MRCM Attribute Domain Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember
	 * @generated
	 */
	EClass getSnomedMRCMAttributeDomainRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getDomainId <em>Domain Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Domain Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getDomainId()
	 * @see #getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeDomainRefSetMember_DomainId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#isGrouped <em>Grouped</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Grouped</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#isGrouped()
	 * @see #getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeDomainRefSetMember_Grouped();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeCardinality <em>Attribute Cardinality</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute Cardinality</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeCardinality()
	 * @see #getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeDomainRefSetMember_AttributeCardinality();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeInGroupCardinality <em>Attribute In Group Cardinality</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute In Group Cardinality</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getAttributeInGroupCardinality()
	 * @see #getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeDomainRefSetMember_AttributeInGroupCardinality();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getRuleStrengthId <em>Rule Strength Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rule Strength Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getRuleStrengthId()
	 * @see #getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeDomainRefSetMember_RuleStrengthId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getContentTypeId <em>Content Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content Type Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeDomainRefSetMember#getContentTypeId()
	 * @see #getSnomedMRCMAttributeDomainRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeDomainRefSetMember_ContentTypeId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember <em>Snomed MRCM Attribute Range Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed MRCM Attribute Range Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember
	 * @generated
	 */
	EClass getSnomedMRCMAttributeRangeRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRangeConstraint <em>Range Constraint</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Range Constraint</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRangeConstraint()
	 * @see #getSnomedMRCMAttributeRangeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeRangeRefSetMember_RangeConstraint();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getAttributeRule <em>Attribute Rule</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Attribute Rule</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getAttributeRule()
	 * @see #getSnomedMRCMAttributeRangeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeRangeRefSetMember_AttributeRule();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRuleStrengthId <em>Rule Strength Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Rule Strength Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getRuleStrengthId()
	 * @see #getSnomedMRCMAttributeRangeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeRangeRefSetMember_RuleStrengthId();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getContentTypeId <em>Content Type Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Content Type Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMAttributeRangeRefSetMember#getContentTypeId()
	 * @see #getSnomedMRCMAttributeRangeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMAttributeRangeRefSetMember_ContentTypeId();

	/**
	 * Returns the meta object for class '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember <em>Snomed MRCM Module Scope Ref Set Member</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Snomed MRCM Module Scope Ref Set Member</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember
	 * @generated
	 */
	EClass getSnomedMRCMModuleScopeRefSetMember();

	/**
	 * Returns the meta object for the attribute '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember#getMrcmRuleRefsetId <em>Mrcm Rule Refset Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Mrcm Rule Refset Id</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedMRCMModuleScopeRefSetMember#getMrcmRuleRefsetId()
	 * @see #getSnomedMRCMModuleScopeRefSetMember()
	 * @generated
	 */
	EAttribute getSnomedMRCMModuleScopeRefSetMember_MrcmRuleRefsetId();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType <em>Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType
	 * @generated
	 */
	EEnum getSnomedRefSetType();

	/**
	 * Returns the meta object for enum '{@link com.b2international.snowowl.snomed.snomedrefset.DataType <em>Data Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for enum '<em>Data Type</em>'.
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @generated
	 */
	EEnum getDataType();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	SnomedRefSetFactory getSnomedRefSetFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetImpl <em>Snomed Ref Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRefSet()
		 * @generated
		 */
		EClass SNOMED_REF_SET = eINSTANCE.getSnomedRefSet();

		/**
		 * The meta object literal for the '<em><b>Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET__TYPE = eINSTANCE.getSnomedRefSet_Type();

		/**
		 * The meta object literal for the '<em><b>Referenced Component Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET__REFERENCED_COMPONENT_TYPE = eINSTANCE.getSnomedRefSet_ReferencedComponentType();

		/**
		 * The meta object literal for the '<em><b>Identifier Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET__IDENTIFIER_ID = eINSTANCE.getSnomedRefSet_IdentifierId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRegularRefSetImpl <em>Snomed Regular Ref Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRegularRefSetImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRegularRefSet()
		 * @generated
		 */
		EClass SNOMED_REGULAR_REF_SET = eINSTANCE.getSnomedRegularRefSet();

		/**
		 * The meta object literal for the '<em><b>Members</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SNOMED_REGULAR_REF_SET__MEMBERS = eINSTANCE.getSnomedRegularRefSet_Members();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedStructuralRefSetImpl <em>Snomed Structural Ref Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedStructuralRefSetImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedStructuralRefSet()
		 * @generated
		 */
		EClass SNOMED_STRUCTURAL_REF_SET = eINSTANCE.getSnomedStructuralRefSet();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMappingRefSetImpl <em>Snomed Mapping Ref Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMappingRefSetImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMappingRefSet()
		 * @generated
		 */
		EClass SNOMED_MAPPING_REF_SET = eINSTANCE.getSnomedMappingRefSet();

		/**
		 * The meta object literal for the '<em><b>Map Target Component Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MAPPING_REF_SET__MAP_TARGET_COMPONENT_TYPE = eINSTANCE.getSnomedMappingRefSet_MapTargetComponentType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetImpl <em>Snomed Concrete Data Type Ref Set</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedConcreteDataTypeRefSet()
		 * @generated
		 */
		EClass SNOMED_CONCRETE_DATA_TYPE_REF_SET = eINSTANCE.getSnomedConcreteDataTypeRefSet();

		/**
		 * The meta object literal for the '<em><b>Data Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET__DATA_TYPE = eINSTANCE.getSnomedConcreteDataTypeRefSet_DataType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl <em>Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRefSetMember()
		 * @generated
		 */
		EClass SNOMED_REF_SET_MEMBER = eINSTANCE.getSnomedRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Referenced Component Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_TYPE = eINSTANCE.getSnomedRefSetMember_ReferencedComponentType();

		/**
		 * The meta object literal for the '<em><b>Effective Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__EFFECTIVE_TIME = eINSTANCE.getSnomedRefSetMember_EffectiveTime();

		/**
		 * The meta object literal for the '<em><b>Active</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__ACTIVE = eINSTANCE.getSnomedRefSetMember_Active();

		/**
		 * The meta object literal for the '<em><b>Ref Set</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference SNOMED_REF_SET_MEMBER__REF_SET = eINSTANCE.getSnomedRefSetMember_RefSet();

		/**
		 * The meta object literal for the '<em><b>Released</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__RELEASED = eINSTANCE.getSnomedRefSetMember_Released();

		/**
		 * The meta object literal for the '<em><b>Referenced Component Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__REFERENCED_COMPONENT_ID = eINSTANCE.getSnomedRefSetMember_ReferencedComponentId();

		/**
		 * The meta object literal for the '<em><b>Module Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__MODULE_ID = eINSTANCE.getSnomedRefSetMember_ModuleId();

		/**
		 * The meta object literal for the '<em><b>Ref Set Identifier Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__REF_SET_IDENTIFIER_ID = eINSTANCE.getSnomedRefSetMember_RefSetIdentifierId();

		/**
		 * The meta object literal for the '<em><b>Uuid</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_REF_SET_MEMBER__UUID = eINSTANCE.getSnomedRefSetMember_Uuid();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedDescriptionTypeRefSetMemberImpl <em>Snomed Description Type Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedDescriptionTypeRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedDescriptionTypeRefSetMember()
		 * @generated
		 */
		EClass SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER = eINSTANCE.getSnomedDescriptionTypeRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Description Format</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_FORMAT = eINSTANCE.getSnomedDescriptionTypeRefSetMember_DescriptionFormat();

		/**
		 * The meta object literal for the '<em><b>Description Length</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_DESCRIPTION_TYPE_REF_SET_MEMBER__DESCRIPTION_LENGTH = eINSTANCE.getSnomedDescriptionTypeRefSetMember_DescriptionLength();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl <em>Snomed Simple Map Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedSimpleMapRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedSimpleMapRefSetMember()
		 * @generated
		 */
		EClass SNOMED_SIMPLE_MAP_REF_SET_MEMBER = eINSTANCE.getSnomedSimpleMapRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Map Target Component Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_ID = eINSTANCE.getSnomedSimpleMapRefSetMember_MapTargetComponentId();

		/**
		 * The meta object literal for the '<em><b>Map Target Component Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_TYPE = eINSTANCE.getSnomedSimpleMapRefSetMember_MapTargetComponentType();

		/**
		 * The meta object literal for the '<em><b>Map Target Component Description</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_SIMPLE_MAP_REF_SET_MEMBER__MAP_TARGET_COMPONENT_DESCRIPTION = eINSTANCE.getSnomedSimpleMapRefSetMember_MapTargetComponentDescription();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl <em>Snomed Complex Map Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedComplexMapRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedComplexMapRefSetMember()
		 * @generated
		 */
		EClass SNOMED_COMPLEX_MAP_REF_SET_MEMBER = eINSTANCE.getSnomedComplexMapRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Map Group</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_GROUP = eINSTANCE.getSnomedComplexMapRefSetMember_MapGroup();

		/**
		 * The meta object literal for the '<em><b>Map Priority</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_PRIORITY = eINSTANCE.getSnomedComplexMapRefSetMember_MapPriority();

		/**
		 * The meta object literal for the '<em><b>Map Rule</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_RULE = eINSTANCE.getSnomedComplexMapRefSetMember_MapRule();

		/**
		 * The meta object literal for the '<em><b>Map Advice</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_ADVICE = eINSTANCE.getSnomedComplexMapRefSetMember_MapAdvice();

		/**
		 * The meta object literal for the '<em><b>Correlation Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_COMPLEX_MAP_REF_SET_MEMBER__CORRELATION_ID = eINSTANCE.getSnomedComplexMapRefSetMember_CorrelationId();

		/**
		 * The meta object literal for the '<em><b>Map Category Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_COMPLEX_MAP_REF_SET_MEMBER__MAP_CATEGORY_ID = eINSTANCE.getSnomedComplexMapRefSetMember_MapCategoryId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedQueryRefSetMemberImpl <em>Snomed Query Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedQueryRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedQueryRefSetMember()
		 * @generated
		 */
		EClass SNOMED_QUERY_REF_SET_MEMBER = eINSTANCE.getSnomedQueryRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Query</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_QUERY_REF_SET_MEMBER__QUERY = eINSTANCE.getSnomedQueryRefSetMember_Query();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAttributeValueRefSetMemberImpl <em>Snomed Attribute Value Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAttributeValueRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedAttributeValueRefSetMember()
		 * @generated
		 */
		EClass SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER = eINSTANCE.getSnomedAttributeValueRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Value Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_ATTRIBUTE_VALUE_REF_SET_MEMBER__VALUE_ID = eINSTANCE.getSnomedAttributeValueRefSetMember_ValueId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedLanguageRefSetMemberImpl <em>Snomed Language Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedLanguageRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedLanguageRefSetMember()
		 * @generated
		 */
		EClass SNOMED_LANGUAGE_REF_SET_MEMBER = eINSTANCE.getSnomedLanguageRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Acceptability Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_LANGUAGE_REF_SET_MEMBER__ACCEPTABILITY_ID = eINSTANCE.getSnomedLanguageRefSetMember_AcceptabilityId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl <em>Snomed Concrete Data Type Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedConcreteDataTypeRefSetMember()
		 * @generated
		 */
		EClass SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER = eINSTANCE.getSnomedConcreteDataTypeRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Serialized Value</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__SERIALIZED_VALUE = eINSTANCE.getSnomedConcreteDataTypeRefSetMember_SerializedValue();

		/**
		 * The meta object literal for the '<em><b>Label</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__LABEL = eINSTANCE.getSnomedConcreteDataTypeRefSetMember_Label();

		/**
		 * The meta object literal for the '<em><b>Uom Component Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__UOM_COMPONENT_ID = eINSTANCE.getSnomedConcreteDataTypeRefSetMember_UomComponentId();

		/**
		 * The meta object literal for the '<em><b>Operator Component Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__OPERATOR_COMPONENT_ID = eINSTANCE.getSnomedConcreteDataTypeRefSetMember_OperatorComponentId();

		/**
		 * The meta object literal for the '<em><b>Data Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__DATA_TYPE = eINSTANCE.getSnomedConcreteDataTypeRefSetMember_DataType();

		/**
		 * The meta object literal for the '<em><b>Characteristic Type Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__CHARACTERISTIC_TYPE_ID = eINSTANCE.getSnomedConcreteDataTypeRefSetMember_CharacteristicTypeId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAssociationRefSetMemberImpl <em>Snomed Association Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAssociationRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedAssociationRefSetMember()
		 * @generated
		 */
		EClass SNOMED_ASSOCIATION_REF_SET_MEMBER = eINSTANCE.getSnomedAssociationRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Target Component Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_ID = eINSTANCE.getSnomedAssociationRefSetMember_TargetComponentId();

		/**
		 * The meta object literal for the '<em><b>Target Component Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_ASSOCIATION_REF_SET_MEMBER__TARGET_COMPONENT_TYPE = eINSTANCE.getSnomedAssociationRefSetMember_TargetComponentType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedModuleDependencyRefSetMemberImpl <em>Snomed Module Dependency Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedModuleDependencyRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedModuleDependencyRefSetMember()
		 * @generated
		 */
		EClass SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER = eINSTANCE.getSnomedModuleDependencyRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Source Effective Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__SOURCE_EFFECTIVE_TIME = eINSTANCE.getSnomedModuleDependencyRefSetMember_SourceEffectiveTime();

		/**
		 * The meta object literal for the '<em><b>Target Effective Time</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MODULE_DEPENDENCY_REF_SET_MEMBER__TARGET_EFFECTIVE_TIME = eINSTANCE.getSnomedModuleDependencyRefSetMember_TargetEffectiveTime();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAnnotationRefSetMemberImpl <em>Snomed Annotation Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedAnnotationRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedAnnotationRefSetMember()
		 * @generated
		 */
		EClass SNOMED_ANNOTATION_REF_SET_MEMBER = eINSTANCE.getSnomedAnnotationRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Annotation</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_ANNOTATION_REF_SET_MEMBER__ANNOTATION = eINSTANCE.getSnomedAnnotationRefSetMember_Annotation();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl <em>Snomed MRCM Domain Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMDomainRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMDomainRefSetMember()
		 * @generated
		 */
		EClass SNOMED_MRCM_DOMAIN_REF_SET_MEMBER = eINSTANCE.getSnomedMRCMDomainRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Domain Constraint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_CONSTRAINT = eINSTANCE.getSnomedMRCMDomainRefSetMember_DomainConstraint();

		/**
		 * The meta object literal for the '<em><b>Parent Domain</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PARENT_DOMAIN = eINSTANCE.getSnomedMRCMDomainRefSetMember_ParentDomain();

		/**
		 * The meta object literal for the '<em><b>Proximal Primitive Constraint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_CONSTRAINT = eINSTANCE.getSnomedMRCMDomainRefSetMember_ProximalPrimitiveConstraint();

		/**
		 * The meta object literal for the '<em><b>Proximal Primitive Refinement</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__PROXIMAL_PRIMITIVE_REFINEMENT = eINSTANCE.getSnomedMRCMDomainRefSetMember_ProximalPrimitiveRefinement();

		/**
		 * The meta object literal for the '<em><b>Domain Template For Precoordination</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_PRECOORDINATION = eINSTANCE.getSnomedMRCMDomainRefSetMember_DomainTemplateForPrecoordination();

		/**
		 * The meta object literal for the '<em><b>Domain Template For Postcoordination</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__DOMAIN_TEMPLATE_FOR_POSTCOORDINATION = eINSTANCE.getSnomedMRCMDomainRefSetMember_DomainTemplateForPostcoordination();

		/**
		 * The meta object literal for the '<em><b>Editorial Guide Reference</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_DOMAIN_REF_SET_MEMBER__EDITORIAL_GUIDE_REFERENCE = eINSTANCE.getSnomedMRCMDomainRefSetMember_EditorialGuideReference();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl <em>Snomed MRCM Attribute Domain Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeDomainRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMAttributeDomainRefSetMember()
		 * @generated
		 */
		EClass SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Domain Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__DOMAIN_ID = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember_DomainId();

		/**
		 * The meta object literal for the '<em><b>Grouped</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__GROUPED = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember_Grouped();

		/**
		 * The meta object literal for the '<em><b>Attribute Cardinality</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_CARDINALITY = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember_AttributeCardinality();

		/**
		 * The meta object literal for the '<em><b>Attribute In Group Cardinality</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__ATTRIBUTE_IN_GROUP_CARDINALITY = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember_AttributeInGroupCardinality();

		/**
		 * The meta object literal for the '<em><b>Rule Strength Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__RULE_STRENGTH_ID = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember_RuleStrengthId();

		/**
		 * The meta object literal for the '<em><b>Content Type Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_DOMAIN_REF_SET_MEMBER__CONTENT_TYPE_ID = eINSTANCE.getSnomedMRCMAttributeDomainRefSetMember_ContentTypeId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl <em>Snomed MRCM Attribute Range Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMAttributeRangeRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMAttributeRangeRefSetMember()
		 * @generated
		 */
		EClass SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER = eINSTANCE.getSnomedMRCMAttributeRangeRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Range Constraint</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RANGE_CONSTRAINT = eINSTANCE.getSnomedMRCMAttributeRangeRefSetMember_RangeConstraint();

		/**
		 * The meta object literal for the '<em><b>Attribute Rule</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__ATTRIBUTE_RULE = eINSTANCE.getSnomedMRCMAttributeRangeRefSetMember_AttributeRule();

		/**
		 * The meta object literal for the '<em><b>Rule Strength Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__RULE_STRENGTH_ID = eINSTANCE.getSnomedMRCMAttributeRangeRefSetMember_RuleStrengthId();

		/**
		 * The meta object literal for the '<em><b>Content Type Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_ATTRIBUTE_RANGE_REF_SET_MEMBER__CONTENT_TYPE_ID = eINSTANCE.getSnomedMRCMAttributeRangeRefSetMember_ContentTypeId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMModuleScopeRefSetMemberImpl <em>Snomed MRCM Module Scope Ref Set Member</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedMRCMModuleScopeRefSetMemberImpl
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedMRCMModuleScopeRefSetMember()
		 * @generated
		 */
		EClass SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER = eINSTANCE.getSnomedMRCMModuleScopeRefSetMember();

		/**
		 * The meta object literal for the '<em><b>Mrcm Rule Refset Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute SNOMED_MRCM_MODULE_SCOPE_REF_SET_MEMBER__MRCM_RULE_REFSET_ID = eINSTANCE.getSnomedMRCMModuleScopeRefSetMember_MrcmRuleRefsetId();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType <em>Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getSnomedRefSetType()
		 * @generated
		 */
		EEnum SNOMED_REF_SET_TYPE = eINSTANCE.getSnomedRefSetType();

		/**
		 * The meta object literal for the '{@link com.b2international.snowowl.snomed.snomedrefset.DataType <em>Data Type</em>}' enum.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
		 * @see com.b2international.snowowl.snomed.snomedrefset.impl.SnomedRefSetPackageImpl#getDataType()
		 * @generated
		 */
		EEnum DATA_TYPE = eINSTANCE.getDataType();

	}

} //SnomedRefSetPackage