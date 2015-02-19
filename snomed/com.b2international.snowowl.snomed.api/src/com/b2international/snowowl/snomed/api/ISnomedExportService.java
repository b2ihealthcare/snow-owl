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

import java.io.File;

import com.b2international.snowowl.snomed.api.domain.ISnomedExportConfiguration;

/**
 * Representation of an export service for the SNOMEd&nbsp;CT ontology.
 * This service is responsible for generating RF2 release format from the
 * state of the underlying SNOMED&nbsp;CT ontology.
 *
 */
public interface ISnomedExportService {

	/**
	 * Generates an RF2 release format by exporting the state of the SNOMED&nbsp;CT ontology
	 * based on the export configuration argument.
	 * @param configuration the configuration for the RF2 export.
	 * @return the RF2 release format export file.
	 */
	File export(final ISnomedExportConfiguration configuration);
	
	
}