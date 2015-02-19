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
package com.b2international.snowowl.snomed.datastore.quicksearch;

import java.util.Map;

import com.b2international.snowowl.datastore.quicksearch.QuickSearchProviderBase;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;

/**
 * Provider used to access SNOMED CT concepts.
 * 
 */
public class SnomedConceptQuickSearchProvider extends QuickSearchProviderBase {

	public static final String ID = "com.b2international.snowowl.snomed.datastore.quicksearch.SnomedConceptQuickSearchProvider";
	
	private static final String NAME = "SNOMED CT";

	/**
	 * Configuration key to specify the SCT ID {@link String} of the parent concept.
	 */
	public static final String CONFIGURATION_PARENT_ID = "PARENT_ID";
	
	public SnomedConceptQuickSearchProvider() {
		super();
	}
	
	public SnomedConceptQuickSearchProvider(final Map<String, Object> configuration) {
		super(configuration);
	}
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return NAME;
	}
	
	@Override
	public int getPriority() {
		return 30;
	}
	
	@Override
	public String getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT;
	}
	
	@Override
	public int getSuffixMultiplier() {
		return 5;
	}
}