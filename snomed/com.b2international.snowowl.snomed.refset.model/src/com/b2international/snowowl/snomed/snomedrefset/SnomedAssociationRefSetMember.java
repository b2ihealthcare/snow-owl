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
 * Association reference set member representation. Its purpose is to support unordered associations of particular types between
 * SNOMED&nbsp;CT components. 
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember#getTargetComponentId <em>Target Component Id</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAssociationRefSetMember#getTargetComponentType <em>Target Component Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedAssociationRefSetMember()
 * @model
 * @generated
 */
public interface SnomedAssociationRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns with the unique SNOMED&nbsp;CT ID of the destination component of the association represented by the current reference set member.
	 * @return the SNOMED&nbsp;CT ID of the destination of the association.
	 * @see #setTargetComponentId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedAssociationRefSetMember_TargetComponentId()
	 * @model required="true"
	 * @generated
	 */
	String getTargetComponentId();

	/**
	 * Counterpart of the {@link #getTargetComponentId()}.
	 * @param value the unique SNOMED&nbsp;CT identifier of the destination component of the association.
	 * @see #getTargetComponentId()
	 * @generated
	 */
	void setTargetComponentId(String value);

	/**
	 * Returns with the application specific identifier of the target component type.
	 * @return the target component type identifier.
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedAssociationRefSetMember_TargetComponentType()
	 * @model required="true" transient="true" changeable="false" derived="true"
	 * @generated
	 */
	short getTargetComponentType();

} // SnomedAssociationRefSetMember