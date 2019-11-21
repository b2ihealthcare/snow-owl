/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.provider;

import java.util.Collection;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.b2international.snowowl.fhir.core.model.conceptmap.Match;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateRequest;
import com.b2international.snowowl.fhir.core.model.conceptmap.TranslateResult;
import com.google.common.collect.ImmutableList;

/**
 * Extension point interface for concept map specific FHIR API support. 
 * 
 * @since 6.9
 */
public interface IConceptMapApiProvider {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.9
	 */
	enum Registry {
		
		INSTANCE;
		
		private final Collection<IConceptMapApiProvider> providers;
		
		private Registry() {
			this.providers = ImmutableList.copyOf(ClassPathScanner.INSTANCE.getComponentsByInterface(IConceptMapApiProvider.class));
		}
		
		public static Collection<IConceptMapApiProvider> getProviders() {
			return INSTANCE.providers;
		}
		
	}
	
	/**
	 * Returns the concept maps supported by this provider.
	 * @return collection of concept maps supported
	 */
	Collection<ConceptMap> getConceptMaps();

	/**
	 * Returns the concept map for the passed in logical id (repositoryId:branchPath/conceptMapId)
	 * @param logicalId
	 * @return {@link ConceptMap}
	 * @throws BadRequestException if the concept map is not supported by this provider
	 */
	ConceptMap getConceptMap(LogicalId logicalId);

	/**
	 * Returns a the collection of mapping matches as a translate result from a given Concept Map
	 * @param logicalId - logical if of the {@link ConceptMap}
	 * @param translateRequest - {@link TranslateRequest}
	 * @return a {@link TranslateResult} instance
	 */
	TranslateResult translate(LogicalId logicalId, TranslateRequest translateRequest);

	/**
	 * Returns a collection of mapping matches for the given translate request
	 * These mappings can be fetched from any concept map in the system.
	 * 
	 * @param translateRequest {@link TranslateRequest}
	 * @return collection of translate {@link Match} objects
	 */
	Collection<Match> translate(TranslateRequest translateRequest);
	
}
