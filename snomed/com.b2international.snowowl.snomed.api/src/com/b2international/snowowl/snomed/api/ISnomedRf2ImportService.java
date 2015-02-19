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
package com.b2international.snowowl.snomed.api;

import java.io.InputStream;
import java.util.UUID;

import com.b2international.snowowl.snomed.api.domain.ISnomedImportConfiguration;

/**
 * Representation of a SNOMED&nbsp;CT import service for RF2 release archives.
 */
public interface ISnomedRf2ImportService {

	/**
	 * Returns with the previously configured SNOMED&nbsp;CT RF2 import configuration.
	 * @param importId the import configuration UUID.
	 * @return the configuration.
	 */
	ISnomedImportConfiguration getImportDetails(final UUID importId);

	/**
	 * Deletes a previously configured SNOMED&nbsp;CT RF2 import configuration.
	 * @param importId the import configuration unique identifier.
	 */
	void deleteImportDetails(final UUID importId);

	/**
	 * Performs the SNOMED&nbsp;CT RF2 import.
	 * @param importId the import configuration unique ID.
	 * @param inputStream the input stream to the RF2 release archive.
	 * @param originalFilename the file name of the release archive.
	 */
	void startImport(final UUID importId, final InputStream inputStream);

	/**
	 * Creates and registers a new SNOMED&nbsp;CT RF2 import configuration. After the successful registration
	 * it returns with the UUID of configuration.
	 * @param configuration the configuration to register.
	 * @return the UUID of the associated configuration.
	 */
	UUID create(final ISnomedImportConfiguration configuration);
}
