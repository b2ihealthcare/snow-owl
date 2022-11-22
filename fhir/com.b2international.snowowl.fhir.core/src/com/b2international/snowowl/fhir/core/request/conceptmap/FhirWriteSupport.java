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

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.request.ResourceRequests;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;

/**
 * @since 8.7.1
 */
public interface FhirWriteSupport {

	/**
	 * Checks whether a business version can be created based on an incoming resource snapshot state (FHIR Resource representation).
	 * 
	 * @param context
	 * @param resourceUri
	 * @param newVersionToCreate
	 * @param newVersionEffectiveDate
	 * @return a boolean value indicating whether the version can be created either forcefully or via the standard way.
	 * @throws BadRequestException - if the version creation cannot be completed for any reason
	 */
	default boolean checkBusinessVersion(
			final ServiceProvider context, 
			final ResourceURI resourceUri, 
			final String newVersionToCreate, 
			final LocalDate newVersionEffectiveDate) {
			
		return ResourceRequests.prepareSearchVersion()
			.setLimit(1)
			.filterByResource(resourceUri)
			.sortBy("effectiveTime:desc")
			.buildAsync()
			.execute(context)
			.first()
			.map(latestVersion -> {
				final int newVersionEffectiveDateCompareValue = newVersionEffectiveDate.compareTo(latestVersion.getEffectiveTime());
				
				// disallow importing versions with earlier effective date
				if (newVersionEffectiveDateCompareValue < 0) {
					throw new BadRequestException(String.format("A version for effective time '%s' already exists, can't add content with effective time '%s'.",
						EffectiveTimes.format(latestVersion.getEffectiveTime()),
						EffectiveTimes.format(newVersionEffectiveDate))
					);
				}
				
				// the incoming version uses the same effective date value, allow patching only when the version value matches
				if (newVersionEffectiveDateCompareValue == 0) {
					if (!newVersionToCreate.equals(latestVersion.getVersion())) {
						throw new BadRequestException(String.format("A different version ('%s') is already using the given effective time '%s' value.",
							latestVersion.getVersion(),
							EffectiveTimes.format(newVersionEffectiveDate))
						);
					}
					
					// if the same effective date and version is given, then allow recreating the version forcefully
					return true;
				}
				
				// in all other cases check whether the version id is already taken
				final boolean versionAlreadyExists = ResourceRequests.prepareSearchVersion()
						.setLimit(0)
						.filterByResource(resourceUri)
						.filterByVersionId(newVersionToCreate)
						.buildAsync()
						.execute(context)
						.getTotal() > 0;

				if (versionAlreadyExists) {
					throw new BadRequestException(String.format("A version with identifier '%s' already exists.", newVersionToCreate));
				}
				
				// if not, allow version creation
				return false;
			})
			// no version present for this resource, let version create proceed without the need for force creation
			.orElse(false);
	}
	
}
