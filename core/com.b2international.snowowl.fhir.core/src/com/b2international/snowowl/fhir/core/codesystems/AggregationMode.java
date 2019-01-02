/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.codesystems;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.ResourceNarrative;

/**
 * FHIR Aggregation Mode code system
 * https://www.hl7.org/fhir/codesystem-resource-aggregation-mode.html#AggregationMode
 * 
 * @since 7.1
 */
@ResourceNarrative("How resource references can be aggregated.")
public enum AggregationMode implements FhirCodeSystem {
	
	//The reference is a local reference to a contained resource.
	CONTAINED,
	
	//The reference to a resource that has to be resolved externally to the resource that includes the reference.
	REFERENCED,
	
	//The resource the reference points to will be found in the same bundle as the resource that includes the reference.
	BUNDLED;
	
	public final static String CODE_SYSTEM_URI = "http://hl7.org/fhir/resource-aggregation-mode"; //$NON-NLS-N$
	
	public String getCodeValue() {
		return name().toLowerCase();
	}

	@Override
	public String getCodeSystemUri() {
		return CODE_SYSTEM_URI;
	}

	@Override
	public String getDisplayName() {
		return StringUtils.capitalizeFirstLetter(name().toLowerCase());
	}

}
