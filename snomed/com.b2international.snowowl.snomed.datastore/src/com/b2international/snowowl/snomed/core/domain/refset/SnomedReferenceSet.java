/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.refset;

import com.b2international.snowowl.datastore.request.BaseResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * Represents a SNOMED&nbsp;CT Reference Set.
 * <br>
 * Reference sets returned by search requests are populated based on the expand parameters passed into the {@link BaseResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>members()</li> - returns the members of this reference set as well.
 * </ul>
 * 
 * Expand parameters can be nested to further expand or filter the details returned. 
 * 
 * @see SnomedConcept
 * @see SnomedDescription
 * @see SnomedRelationship
 * @see SnomedReferenceSetMember
 * 
 * @since 4.5
 */
public interface SnomedReferenceSet extends SnomedComponent {

	/**
	 * Returns the type of the reference set.
	 * 
	 * @return
	 */
	SnomedRefSetType getType();

	/**
	 * Returns the type of the referenced component.
	 * 
	 * @return
	 */
	String getReferencedComponentType();

	/**
	 * Returns all members of the reference set.
	 * 
	 * @return
	 */
	SnomedReferenceSetMembers getMembers();

}
