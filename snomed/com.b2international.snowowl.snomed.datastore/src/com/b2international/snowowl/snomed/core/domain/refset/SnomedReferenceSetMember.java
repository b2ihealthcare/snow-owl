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
package com.b2international.snowowl.snomed.core.domain.refset;

import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedCoreComponent;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.fasterxml.jackson.annotation.JsonAnyGetter;

/**
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
