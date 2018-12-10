/*
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
package com.b2international.snowowl.snomed.snomedrefset;


/**
 * Representation of a SNOMED&nbsp;CT data type reference set member.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getSerializedValue <em>Serialized Value</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getLabel <em>Label</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getUomComponentId <em>Uom Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getOperatorComponentId <em>Operator Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getDataType <em>Data Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getCharacteristicTypeId <em>Characteristic Type Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember()
 * @model
 * @generated
 */
public interface SnomedConcreteDataTypeRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns the value of the '<em><b>Group</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Group</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Group</em>' attribute.
	 * @see #setGroup(int)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_Group()
	 * @model default="0" required="true"
	 * @generated
	 */
	int getGroup();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getGroup <em>Group</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Group</em>' attribute.
	 * @see #getGroup()
	 * @generated
	 */
	void setGroup(int value);

	/**
	 * Returns with the serialized form of the value. 
	 * @return the value in a serialized format.
	 * @see #setSerializedValue(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_SerializedValue()
	 * @model required="true"
	 * @generated
	 */
	String getSerializedValue();

	/**
	 * Counterpart of {@link #getSerializedValue()}.
	 * @param value the new value in its serialized format.
	 * @see #getSerializedValue()
	 * @generated
	 */
	void setSerializedValue(String value);

	/**
	 * Returns the value of the '<em><b>Type Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type Id</em>' attribute.
	 * @see #setTypeId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_TypeId()
	 * @model required="true"
	 * @generated
	 */
	String getTypeId();

	/**
	 * Sets the value of the '{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSetMember#getTypeId <em>Type Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type Id</em>' attribute.
	 * @see #getTypeId()
	 * @generated
	 */
	void setTypeId(String value);

	/**
	 * Returns with the data type of the reference set member.
	 * @return the data type of the member.
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_DataType()
	 * @model required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	DataType getDataType();

	/**
	 * Returns with the ID of a concept indicating that the current member specified defining, qualifying, historical
	 * or an additional characteristic.  
	 * @return the characteristic type concept ID.
	 * @see #setCharacteristicTypeId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_CharacteristicTypeId()
	 * @model required="true"
	 * @generated
	 */
	String getCharacteristicTypeId();

	/**
	 * Counterpart of {@link #getCharacteristicTypeId()}.
	 * @param value the characteristic type concept ID.
	 * @see #getCharacteristicTypeId()
	 * @generated
	 */
	void setCharacteristicTypeId(String value);

} // SnomedConcreteDataTypeRefSetMember