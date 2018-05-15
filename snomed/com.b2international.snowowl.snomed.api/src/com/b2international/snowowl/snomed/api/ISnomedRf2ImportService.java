/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api;

import java.io.InputStream;
import java.util.UUID;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemNotFoundException;
import com.b2international.snowowl.core.domain.exceptions.CodeSystemVersionNotFoundException;
import com.b2international.snowowl.snomed.api.domain.exception.SnomedImportConfigurationNotFoundException;
import com.b2international.snowowl.snomed.core.domain.ISnomedImportConfiguration;

/**
 * Implementations allow importing SNOMED CT content from RF2 release archives.
 */
public interface ISnomedRf2ImportService {

	/**
	 * Retrieves the configuration object for the specified import run identifier.
	 * 
	 * @param importId the identifier of the import run to look up
	 * 
	 * @return the configuration object describing import details
	 * 
	 * @throws SnomedImportConfigurationNotFoundException if the specified import run does not exist
	 */
	ISnomedImportConfiguration getImportDetails(UUID importId);

	/**
	 * Deletes a previously configured SNOMED CT RF2 import run.
	 * 
	 * @param importId the identifier of the import run to remove
	 */
	void deleteImportDetails(UUID importId);

	/**
	 * Starts the import using the specified input stream.
	 * <p>
	 * This method returns immediately. 
	 * Import status can be followed by polling {@link #getImportDetails(UUID)}.
	 *  
	 * @param importId    the identifier of the import run to begin
	 * @param inputStream the input stream opened on a valid RF2 release archive
	 * 
	 * @throws SnomedImportException if the import can not start for some reason (import errors are only 
	 *                               reflected in the import run's status flag)
	 */
	void startImport(UUID importId, InputStream inputStream);

	/**
	 * Prepares a new SNOMED CT RF2 import run.
	 * 
	 * @param configuration the configuration object for the import run to register
	 * 
	 * @return the UUID of the associated configuration
	 * 
	 * @throws CodeSystemNotFoundException        if SNOMED CT as a code system is not registered
	 * @throws CodeSystemVersionNotFoundException if a code system version for SNOMED CT with the given identifier
	 *                                            is not registered
	 * @throws BadRequestException                if required properties on the configuration object are not populated                                             
	 */
	UUID create(ISnomedImportConfiguration configuration);
}
