/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.request.conceptmap;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.core.version.Version;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;

/**
 * @since 8.7.1
 */
public interface FhirWriteSupport {

	default boolean ensureVersionCanBeCreated(
			final ServiceProvider context, 
			final ResourceURI conceptMapUri, 
			final String versionId, 
			final LocalDate effectiveDate) {
			
			final Optional<Version> latestVersion = ResourceRequests.prepareSearchVersion()
				.setLimit(1)
				.filterByResource(conceptMapUri)
				.sortBy("effectiveTime:desc")
				.buildAsync()
				.execute(context)
				.first();
		
			final Optional<String> latestVersionId = latestVersion.map(lv -> {
				// Allow importing with the same effective date as the latest version
				if (lv.getEffectiveTime().compareTo(effectiveDate) > 0) {
					throw new BadRequestException(String.format("A concept map version for effective time '%s' already exists, can't add content with effective time '%s'.",
						lv.getEffectiveTime().format(DateTimeFormatter.ISO_LOCAL_DATE),
						effectiveDate.format(DateTimeFormatter.ISO_LOCAL_DATE)));
				}
				
				return lv.getVersion();
			});
			
			// Force re-create latest version if the specified ID is equal to the ID of the version instance 
			final boolean force = latestVersionId.map(versionId::equals)
				.orElse(Boolean.FALSE);

			if (!force) {
				final boolean versionAlreadyExists = ResourceRequests.prepareSearchVersion()
					.setLimit(0)
					.filterByResource(conceptMapUri)
					.filterByVersionId(versionId)
					.buildAsync()
					.execute(context)
					.getTotal() > 0;
					
				if (versionAlreadyExists) {
					throw new BadRequestException(String.format("A concept map version with identifier '%s' already exists.", versionId));
				}
			}
			
			return force;
		}
	
}
