/*
 * Copyright 2022-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.request.valueset;

import java.time.LocalDate;
import java.util.Map;

import org.hl7.fhir.r5.model.ValueSet;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.fhir.core.request.FhirResourceUpdateResult;
import com.b2international.snowowl.fhir.core.request.conceptmap.FhirWriteSupport;

/**
 * Implementing classes provide support for value set interactions and named operations which can not be achieved using tooling-independent building
 * blocks.
 * 
 * @since 8.2.0
 */
public interface FhirValueSetWriteSupport extends FhirWriteSupport {

	/**
	 * Creates a new value set based on the specified input, or updates an existing one if it can be retrieved by ID.
	 * 
	 * @param context
	 *            - the request context to use for creation/update
	 * @param fhirValueSet
	 *            - the input FHIR R5 representation of the value set
	 * @param systemUriOverrides
	 *            - code system resource URIs are taken from this map directly instead of going through a lookup step when the value set refers to a
	 *            system URI (stored in map keys) in any inclusion/exclusion
	 * @param author
	 *            - the commit author
	 * @param owner
	 *            - the resource owner, usually provided via a request header (can be different from the user associated with the service provider)
	 * @param ownerProfileName
	 *            - the owner's display name, stored in resource settings
	 * @param defaultEffectiveDate
	 *            - the default effective date to use if no date information is present on the resource (when not given and a version is present, but
	 *            no effective time is recorded on the value set, an exception will be thrown)
	 * @param bundleId
	 *            - the parent bundle identifier
	 * 
	 * @return indicates whether a new resource has been created or if an existing resource has been updated as part of this interaction
	 */
	public FhirResourceUpdateResult update(ServiceProvider context, ValueSet fhirValueSet, Map<String, ResourceURI> systemUriOverrides, String author,
			String owner, String ownerProfileName, LocalDate defaultEffectiveDate, String bundleId);
}
