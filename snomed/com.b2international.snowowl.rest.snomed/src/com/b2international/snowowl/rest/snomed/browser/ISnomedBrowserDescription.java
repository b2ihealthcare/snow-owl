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
package com.b2international.snowowl.rest.snomed.browser;

import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;

/**
 * Represents a specific SNOMED CT description, carrying information for use in the IHTSDO SNOMED CT Browser.
 */
public interface ISnomedBrowserDescription extends ISnomedBrowserComponentWithId {

	/** @return the descriptions's unique component identifier */
	String getDescriptionId();
	
	/** @return the described concept's component identifier */
	String getConceptId();
	
	/** @return the description type */
	SnomedBrowserDescriptionType getType();
	
	/** @return the two-letter language code of the description */
	String getLang();
	
	/** @return the description term */
	String getTerm();
	
	/** @return the case sensitivity of the description term */
	CaseSignificance getCaseSignificance();

	/** @return language reference set member acceptability values for this description, keyed by language reference set identifier. */
	Map<String, Acceptability> getAcceptabilityMap();
}
