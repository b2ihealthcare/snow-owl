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

import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.snomed.api.exception.SnomedExportException;
import com.b2international.snowowl.snomed.core.domain.ISnomedExportConfiguration;

/**
 * Implementations of this interface are responsible for generating export archive in RF2 release format from the state
 * of the underlying SNOMED CT ontology.
 */
public interface ISnomedExportService {

	/**
	 * Generates an export archive file in RF2 release format using the specified configuration object.
	 * <p>
	 * Exporting is a long-running process; this method call will not return until the export finishes.
	 * 
	 * @param configuration the configuration for the RF2 export run
	 * 
	 * @return the generated export file
	 * 
	 * @throws SnomedExportException if the configuration is invalid, or the export fails for some reason
	 */
	File export(ISnomedExportConfiguration configuration);

	/**
	 * Resolves the namespace to be used for the export by extracting branch metadata information.
	 * @param branch the branch used for extracting the metadata information
	 * @return the namespace extracted from the branch metadata information or INT by default.
	 */
	String resolveNamespaceId(Branch branch);
	
}
