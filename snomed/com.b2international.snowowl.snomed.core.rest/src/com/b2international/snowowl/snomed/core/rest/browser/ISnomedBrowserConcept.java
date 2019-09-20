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
package com.b2international.snowowl.snomed.core.rest.browser;

import java.util.List;

import com.b2international.snowowl.snomed.core.domain.DefinitionStatusProvider;

/**
 * Represents a specific SNOMED CT concept, carrying information for use in the IHTSDO SNOMED CT Browser.
 */
public interface ISnomedBrowserConcept extends ISnomedBrowserComponentWithId, IConceptIdWithFsnProvider, DefinitionStatusProvider, TaxonomyNode {

	/** @return the Preferred Term of this concept for the requested language code and dialect */
	String getPreferredSynonym();
	
	/** @return the list of descriptions associated with this concept */
	List<ISnomedBrowserDescription> getDescriptions();
	
	/** @return the list of relationships associated with this concept */
	List<ISnomedBrowserRelationship> getRelationships();
}
