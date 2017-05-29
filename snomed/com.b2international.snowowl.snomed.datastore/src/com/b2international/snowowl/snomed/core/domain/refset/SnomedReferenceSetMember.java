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

import java.util.Map;

import com.b2international.snowowl.datastore.request.BaseResourceRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
 * Represents a SNOMED&nbsp;CT Reference Set Member.
 * <br>
 * Reference sets returned by search requests are populated based on the expand parameters passed into the {@link BaseResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>targetComponent()</li> - returns the target component of the member
 * <li>referencedComponent()</li> - returns the referenced component of the member
 * </ul>
 * 
 * Expand parameters can be nested to further expand or filter the details returned. For example:
 * <p>
 * <i>referencedComponent(expand(pt()))</i>, would return the preferred term of a <i>Concept</i> type referenced component.
 * 
 * @see SnomedConcept
 * @see SnomedDescription
 * @see SnomedRelationship
 * @see SnomedReferenceSet
 * 
 * @since 4.5
 */
public interface SnomedReferenceSetMember extends SnomedComponent {

	/**
	 * @return the containing reference set's type
	 */
	SnomedRefSetType type();
	
	/**
	 * Returns the component referenced by this SNOMED CT Reference Set Member. It includes only the SNOMED CT ID property by default, see
	 * {@link SnomedCoreComponent#getId()}.
	 * 
	 * @return
	 */
	SnomedCoreComponent getReferencedComponent();

	/**
	 * Returns the identifier of the SNOMED CT Reference Set this SNOMED CT Reference Set Member belongs to.
	 * 
	 * @return
	 */
	String getReferenceSetId();

	/**
	 * Returns special properties of the SNOMED CT Reference Set or an empty {@link Map} if none found.
	 * 
	 * @return
	 */
	@JsonAnyGetter
	Map<String, Object> getProperties();

}
