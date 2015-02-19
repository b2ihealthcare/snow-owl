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
 * Represents a SNOMED&nbsp;CT attribute value reference set member. The purpose of this reference set member
 * is to allow a value from a given range to be associated with a SNOMED&nbsp;CT component.
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedAttributeValueRefSetMember#getValueId <em>Value Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedAttributeValueRefSetMember()
 * @model
 * @generated
 */
public interface SnomedAttributeValueRefSetMember extends SnomedRefSetMember {
	/**
	 * Returns with the SNOMED&nbsp;CT concept ID of value of the member. 
	 * @return the concept ID of the value.
	 * @see #setValueId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedAttributeValueRefSetMember_ValueId()
	 * @model required="true"
	 * @generated
	 */
	String getValueId();

	/**
	 * Counterpart of the {@link #getValueId()}.
	 * @param value the unique SNOMEd&nbsp;CT ID of the value of the reference set member.
	 * @see #getValueId()
	 * @generated
	 */
	void setValueId(String value);

} // SnomedAttributeValueRefSetMember