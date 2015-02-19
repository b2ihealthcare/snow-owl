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
 * Representation of the description type reference set member. Its purpose is to support description format 
 * and maximum allowed length for different description type concepts.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember#getDescriptionFormat <em>Description Format</em>}</li>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedDescriptionTypeRefSetMember#getDescriptionLength <em>Description Length</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedDescriptionTypeRefSetMember()
 * @model
 * @generated
 */
public interface SnomedDescriptionTypeRefSetMember extends SnomedRefSetMember {
	/**
	 * The unique ID of the 'Description format' concept in the metadata hierarchy.
	 * @return the unique ID of the SNOMED&nbsp;CT 'Description format' concept. 
	 * @see #setDescriptionFormat(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedDescriptionTypeRefSetMember_DescriptionFormat()
	 * @model required="true"
	 * @generated
	 */
	String getDescriptionFormat();

	/**
	 * Counterpart of the {@link #getDescriptionFormat()}.
	 * @param value the unique SNOMED&nbsp;CT identifier of the 'Description format' concept.
	 * @see #getDescriptionFormat()
	 * @generated
	 */
	void setDescriptionFormat(String value);

	/**
	 * Returns with the maximum allowed length in bytes for the SNOMED&nbsp;CT description referenced by the current member.
	 * @return the maximum allowed length (in byte) of a term for this description type. 
	 * @see #setDescriptionLength(int)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedDescriptionTypeRefSetMember_DescriptionLength()
	 * @model default="0" required="true"
	 * @generated
	 */
	int getDescriptionLength();

	/**
	 * Counterpart of {@link #getDescriptionLength()}. Sets the value of the maximum allowed description length.
	 * @param value the new maximum allowed length term length for the referenced description type in bytes.
	 * @see #getDescriptionLength()
	 * @generated
	 */
	void setDescriptionLength(int value);

} // SnomedDescriptionTypeRefSetMember