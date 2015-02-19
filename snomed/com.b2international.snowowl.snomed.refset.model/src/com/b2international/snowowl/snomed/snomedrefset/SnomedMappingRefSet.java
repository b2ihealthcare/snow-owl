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
 * Representation of a SNOMED&nbsp;CT simple map reference set. The purpose of this type of reference set
 * is to support mapping between SNOMED&nbsp;CT ontology and any other terminologies.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedMappingRefSet#getMapTargetComponentType <em>Map Target Component Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMappingRefSet()
 * @model
 * @generated
 */
public interface SnomedMappingRefSet extends SnomedRegularRefSet {
	/**
	 * Returns with the terminology specific identifier of the map target component of all contained reference set members,
	 * @return the terminology specific unique identifier of a component as the map target of all contained reference set members.
	 * @see #setMapTargetComponentType(short)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedMappingRefSet_MapTargetComponentType()
	 * @model default="-1" required="true"
	 * @generated
	 */
	short getMapTargetComponentType();

	/**
	 * Counterpart of {@link #getMapTargetComponentType()}.
	 * @param value the application specific component type.
	 * @see #getMapTargetComponentType()
	 * @generated
	 */
	void setMapTargetComponentType(short value);

} // SnomedMappingRefSet