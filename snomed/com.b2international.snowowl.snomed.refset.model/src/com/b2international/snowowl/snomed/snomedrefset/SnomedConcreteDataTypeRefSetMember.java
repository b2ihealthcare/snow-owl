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
	 * Returns with the human readable label of the reference set member.
	 * @return the human readable format of the reference set member.
	 * @see #setLabel(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_Label()
	 * @model required="true"
	 *        annotation="http://www.eclipse.org/CDO/DBStore columnName='label0'"
	 * @generated
	 */
	String getLabel();

	/**
	 * Counterpart of {@link #getLabel()}.
	 * @param value the new label for the reference set member.
	 * @see #getLabel()
	 * @generated
	 */
	void setLabel(String value);

	/**
	 * Returns with the unique SNOMED&nbsp;CT ID of the unit of measure concept.
	 * @return the UOM concept ID.
	 * @see #setUomComponentId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_UomComponentId()
	 * @model required="true"
	 * @generated
	 */
	String getUomComponentId();

	/**
	 * Counterpart of {@link #getUomComponentId()}.
	 * @param value the UOM concept ID.
	 * @see #getUomComponentId()
	 * @generated
	 */
	void setUomComponentId(String value);

	/**
	 * Returns with the unique SNOMED&nbsp;CT ID of the operator concept.
	 * @return the unique identifier of the operator concept.
	 * @see #setOperatorComponentId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSetMember_OperatorComponentId()
	 * @model required="true"
	 * @generated
	 */
	String getOperatorComponentId();

	/**
	 * Counterpart of {@link #getOperatorComponentId()}.
	 * @param value the unique ID of the operator concept.
	 * @see #getOperatorComponentId()
	 * @generated
	 */
	void setOperatorComponentId(String value);

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