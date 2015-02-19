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
 * Represents a SNOMED&nbsp;CT data type reference set. 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedConcreteDataTypeRefSet#getDataType <em>Data Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSet()
 * @model
 * @generated
 */
public interface SnomedConcreteDataTypeRefSet extends SnomedStructuralRefSet {
	/**
	 * Returns with the data type of the reference set.
	 * @return the data type of the reference set.
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @see #setDataType(DataType)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedConcreteDataTypeRefSet_DataType()
	 * @model required="true"
	 * @generated
	 */
	DataType getDataType();

	/**
	 * Counterpart of {@link #getDataType()}.
	 * @param value the new data type value.
	 * @see com.b2international.snowowl.snomed.snomedrefset.DataType
	 * @see #getDataType()
	 * @generated
	 */
	void setDataType(DataType value);

} // SnomedConcreteDataTypeRefSet