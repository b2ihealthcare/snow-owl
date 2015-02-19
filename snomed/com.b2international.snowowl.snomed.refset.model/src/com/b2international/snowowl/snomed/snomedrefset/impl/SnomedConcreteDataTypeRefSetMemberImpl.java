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
package com.b2international.snowowl.snomed.snomedrefset.impl;

import org.eclipse.emf.ecore.EClass;

import com.b2international.snowowl.snomed.snomedrefset.DataType;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snomed Concrete Data Type Ref Set Member</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl#getSerializedValue <em>Serialized Value</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl#getLabel <em>Label</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl#getUomComponentId <em>Uom Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl#getOperatorComponentId <em>Operator Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl#getDataType <em>Data Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.impl.SnomedConcreteDataTypeRefSetMemberImpl#getCharacteristicTypeId <em>Characteristic Type Id</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SnomedConcreteDataTypeRefSetMemberImpl extends SnomedRefSetMemberImpl implements SnomedConcreteDataTypeRefSetMember {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SnomedConcreteDataTypeRefSetMemberImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSerializedValue() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__SERIALIZED_VALUE, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSerializedValue(String newSerializedValue) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__SERIALIZED_VALUE, newSerializedValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLabel() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__LABEL, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLabel(String newLabel) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__LABEL, newLabel);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getUomComponentId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__UOM_COMPONENT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setUomComponentId(String newUomComponentId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__UOM_COMPONENT_ID, newUomComponentId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getOperatorComponentId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__OPERATOR_COMPONENT_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setOperatorComponentId(String newOperatorComponentId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__OPERATOR_COMPONENT_ID, newOperatorComponentId);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public DataType getDataType() {
		
		if (!(getRefSet() instanceof SnomedConcreteDataTypeRefSet)) {
			throw new RuntimeException("SNOMED CT reference set member '" + getUuid() + "' is not contained in a reference set.");
		}
		
		return ((SnomedConcreteDataTypeRefSet) getRefSet()).getDataType();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCharacteristicTypeId() {
		return (String)eGet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__CHARACTERISTIC_TYPE_ID, true);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCharacteristicTypeId(String newCharacteristicTypeId) {
		eSet(SnomedRefSetPackage.Literals.SNOMED_CONCRETE_DATA_TYPE_REF_SET_MEMBER__CHARACTERISTIC_TYPE_ID, newCharacteristicTypeId);
	}

} //SnomedConcreteDataTypeRefSetMemberImpl