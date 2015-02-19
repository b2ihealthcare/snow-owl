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
 * Representation of a SNOMED&nbsp;CT simple map type reference set member. The purpose of this kind of reference set member
 * is to support mapping between SNOMED&nbsp;CT ontology and any other terminologies by pointing terminology components from
 * different terminologies.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentId <em>Map Target Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedSimpleMapRefSetMember#getMapTargetComponentType <em>Map Target Component Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedSimpleMapRefSetMember()
 * @model
 * @generated
 */
public interface SnomedSimpleMapRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns with the terminology specific identifier of the map target component of the reference set members,
	 * @return the terminology specific unique identifier of a component.
	 * @see #setMapTargetComponentId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedSimpleMapRefSetMember_MapTargetComponentId()
	 * @model required="true"
	 * @generated
	 */
	String getMapTargetComponentId();

	/**
	 * Counterpart of the {@link #getMapTargetComponentId()}.
	 * @param value sets the terminology specific unique identifier of a component.
	 * @see #getMapTargetComponentId()
	 * @generated
	 */
	void setMapTargetComponentId(String value);

	/**
	 * Returns with the application specific unique terminology component identifier of the map target component.
	 * @return the application specific terminology component identifier.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedSimpleMapRefSetMember_MapTargetComponentType()
	 * @model required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	short getMapTargetComponentType();

	/**
	 * Returns with an optional description for the map target component. Can be {@code null}.
	 * @return an optional description for the map target component.  
	 * @see #setMapTargetComponentDescription(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedSimpleMapRefSetMember_MapTargetComponentDescription()
	 * @model required="true"
	 * @generated
	 */
	String getMapTargetComponentDescription();

	/**
	 * Counterpart of the {@link #getMapTargetComponentDescription()}.
	 * @param value sets the map target component description on the reference set member.
	 * @see #getMapTargetComponentDescription()
	 * @generated
	 */
	void setMapTargetComponentDescription(String value);

} // SnomedSimpleMapRefSetMember