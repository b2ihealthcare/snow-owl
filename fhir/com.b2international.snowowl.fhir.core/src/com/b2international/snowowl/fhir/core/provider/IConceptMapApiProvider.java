/*
 * Copyright 2018-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
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
public interface IConceptMapApiProvider extends IFhirApiProvider {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.9
	 */
	final class Registry {
		
		private final Collection<IConceptMapApiProvider.Factory> providers;
		
		public Registry(ClassPathScanner scanner) {
			this.providers = ImmutableList.copyOf(scanner.getComponentsByInterface(IConceptMapApiProvider.Factory.class));
		}
		
		public Collection<IConceptMapApiProvider> getProviders(IEventBus bus, List<ExtendedLocale> locales) {
			return providers.stream().map(factory -> factory.create(bus, locales)).collect(Collectors.toUnmodifiableList());
		}
		
		/**
		 * Returns the matching {@link IConceptMapApiProvider} for the given path (repository:branchPath).
		 * @param bus
		 * @param locales
		 * @param componentUri code system path (e.g.icd10Store:20140101)
		 * @return FHIR concept map provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
		public IConceptMapApiProvider getConceptMapProvider(IEventBus bus, List<ExtendedLocale> locales, ComponentURI componentUri) {
			return getProviders(bus, locales).stream()
				.filter(provider -> provider.isSupported(componentUri))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing concept map: " + componentUri, OperationOutcomeCode.MSG_NO_MODULE, "system=" + componentUri));
		}
		
		/**
		 * Returns the matching {@link IConceptMapApiProvider} for the given URI.
		 * @param bus
		 * @param locales
		 * @param uriValue
		 * @return FHIR value set provider
		 */
		public IConceptMapApiProvider getConceptMapProvider(IEventBus bus, List<ExtendedLocale> locales, String uriValue) {
			return getProviders(bus, locales).stream()
				.filter(provider -> provider.isSupported(uriValue))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing concept map: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
		}
	}
	
	/**
	 * @since 7.2
	 */
	interface Factory {
		IConceptMapApiProvider create(IEventBus bus, List<ExtendedLocale> locales);
	}
	
	/**
	 * Returns the concept maps supported by this provider.
	 * @return collection of concept maps supported
	 */
	Collection<ConceptMap> getConceptMaps();

	boolean isSupported(ComponentURI componentUri);

	/**
	 * Returns the concept map for the passed in logical id (codesystemname/branchPath/conceptMapId)
	 * @param componentUri
	 * @return {@link ConceptMap}
	 * @throws BadRequestException if the concept map is not supported by this provider
	 */
	ConceptMap getConceptMap(ComponentURI componentUri);

	/**
	 * Returns a the collection of mapping matches as a translate result from a given Concept Map
	 * @param componentUri - logical if of the {@link ConceptMap}
	 * @param translateRequest - {@link TranslateRequest}
	 * @return a {@link TranslateResult} instance
	 */
	TranslateResult translate(ComponentURI componentUri, TranslateRequest translateRequest);

	/**
	 * Returns a collection of mapping matches for the given translate request
	 * These mappings can be fetched from any concept map in the system.
	 * 
	 * @param translateRequest {@link TranslateRequest}
	 * @return collection of translate {@link Match} objects
	 */
	Collection<Match> translate(TranslateRequest translateRequest);
	
}
