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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.SnomedFactory;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.terminologymetadata.CodeSystem;
import com.b2international.snowowl.terminologyregistry.core.CodeSystemFactory;

/**
 * Code system factory for SNOMED&nbsp;CT ontology.
 *
 */
public class SnomedCodeSystemFactory extends CodeSystemFactory {

	public static final String SHORT_NAME = "SNOMEDCT";

	private static final String LANGUAGE = "ENG";
	private static final String LINK = "http://www.ihtsdo.org";
	private static final String ICON_PATH = "icons/snomed.png";
	private static final String NAME = "Systematized Nomenclature of Medicine Clinical Terms International Version";
	private static final String OID = "2.16.840.1.113883.6.96";
	private static final String CITATION = "SNOMED CT contributes to the improvement of patient care by underpinning the " +
			"development of Electronic Health Records that record clinical information in ways that enable meaning-based retrieval. " +
			"This provides effective access to information required for decision support and consistent reporting and analysis. " +
			"Patients benefit from the use of SNOMED CT because it improves the recording of EHR information and facilitates better communication, " +
			"leading to improvements in the quality of care.";

	@Override
	protected String getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT;
	}
	
	@Override
	protected CodeSystem createCodeSystem() {
		return SnomedFactory.eINSTANCE.createCodeSystem();
	}

	@Override
	protected String getShortName() {
		return SHORT_NAME;
	}

	@Override
	protected String getName() {
		return NAME;
	}

	@Override
	protected String getMaintainingOrganizationLink() {
		return LINK;
	}

	@Override
	protected String getLanguage() {
		return LANGUAGE;
	}

	@Override
	protected String getIconPath() {
		return ICON_PATH;
	}

	@Override
	protected String getCodeSystemOid() {
		return OID;
	}

	@Override
	protected String getCitation() {
		return CITATION;
	}

}