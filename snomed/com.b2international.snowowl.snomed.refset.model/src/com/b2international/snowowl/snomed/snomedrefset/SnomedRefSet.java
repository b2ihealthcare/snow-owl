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

import org.eclipse.emf.cdo.CDOObject;

/**
 * Base representation of a SNOMED&nbsp;CT reference set.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getType <em>Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getReferencedComponentType <em>Referenced Component Type</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet#getIdentifierId <em>Identifier Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSet()
 * @model abstract="true"
 * @extends CDOObject
 * @generated
 */
public interface SnomedRefSet extends CDOObject {
	/**
	 * Returns with the {@link SnomedRefSetType SNOMED&nbsp;CT reference set type} of the reference set.
	 * @return the reference set type.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType
	 * @see #setType(SnomedRefSetType)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSet_Type()
	 * @model required="true"
	 * @generated
	 */
	SnomedRefSetType getType();

	/**
	 * Counterpart of the {@link #getType()}.
	 * @param value the new value of the reference set type.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType
	 * @see #getType()
	 * @generated
	 */
	void setType(SnomedRefSetType value);

	/**
	 * Returns with the application specific type of the component referenced by all contained reference set members.
	 * @return the application specific type of the component referenced by the members.
	 * @see #setReferencedComponentType(short)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSet_ReferencedComponentType()
	 * @model required="true"
	 * @generated
	 */
	short getReferencedComponentType();

	/**
	 * Counterpart of {@link #getReferencedComponentType()}.
	 * @param value the new value of the referenced component type for the reference set.
	 * @see #getReferencedComponentType()
	 * @generated
	 */
	void setReferencedComponentType(short value);

	/**
	 * Returns with the reference set identifier concept ID.
	 * @return the unique ID of the reference set identifier concept.
	 * @see #setIdentifierId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedRefSet_IdentifierId()
	 * @model required="true"
	 * @generated
	 */
	String getIdentifierId();

	/**
	 * Counterpart of the {@link #getIdentifierId()}.
	 * @param value the ID of the reference set identifier concept.
	 * @see #getIdentifierId()
	 * @generated
	 */
	void setIdentifierId(String value);

} // SnomedRefSet