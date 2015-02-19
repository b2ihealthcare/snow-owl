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
 * Representation of a SNOMED&nbsp;CT language type reference set member. The purpose of this type of reference set
 * member is to support the creation of sets of descriptions for one or more dialects of a language.
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link com.b2international.snowowl.snomed.snomedrefset.SnomedLanguageRefSetMember#getAcceptabilityId <em>Acceptability Id</em>}</li>
 * </ul>
 * </p>
 *
 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedLanguageRefSetMember()
 * @model
 * @generated
 */
public interface SnomedLanguageRefSetMember extends SnomedRefSetMember {
	/**
	 * The unique ID of the 'Acceptability' concept in the metadata hierarchy.
	 * @return the unique ID of the SNOMED&nbsp;CT 'Acceptability' concept. 
	 * @see #setAcceptabilityId(String)
	 * @see com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetPackage#getSnomedLanguageRefSetMember_AcceptabilityId()
	 * @model required="true"
	 * @generated
	 */
	String getAcceptabilityId();

	/**
	 * Counterpart of the {@link #getAcceptabilityId()}.
	 * @param value the unique SNOMED&nbsp;CT identifier of the 'Acceptability' concept.
	 * @see #getAcceptabilityId()
	 * @generated
	 */
	void setAcceptabilityId(String value);

} // SnomedLanguageRefSetMember