/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.model.conceptmap.ConceptMap;
import com.google.common.collect.ImmutableList;

/**
 * Extension point interface for concept map specific FHIR API support. 
 * 
 * @see 'com.b2international.snowowl.fhir.core.conceptMapProvider' for the extension point definition
 * @since 6.9
 */
public interface IConceptMapApiProvider extends IFhirApiProvider {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.9
	 */
	enum Registry {
		
		INSTANCE;
		
		private final static String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.core.conceptMapProvider"; //$NON-NLS-N$
		private final Collection<IConceptMapApiProvider> providers;
		
		private Registry() {
			Collection<IConceptMapApiProvider> extensions = Extensions.getExtensions(FHIR_EXTENSION_POINT, IConceptMapApiProvider.class);
			this.providers = ImmutableList.copyOf(extensions);
		}
		
		public static Collection<IConceptMapApiProvider> getProviders() {
			return INSTANCE.providers;
		}
		
		/**
		 * Returns the matching {@link IConceptMapApiProvider} for the given path (repository:branchPath).
		 * @param logical code system path (e.g.icd10Store:20140101)
		 * @return FHIR concept map provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
		public static IConceptMapApiProvider getConceptMapProvider(LogicalId logicalId) {
			return getProviders().stream()
				.filter(provider -> provider.isSupported(logicalId))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing concept map: " + logicalId, OperationOutcomeCode.MSG_NO_MODULE, "system=" + logicalId));
		}
		
		/**
		 * Returns the matching {@link IConceptMapApiProvider} for the given URI.
		 * @param uriValue
		 * @return FHIR value setprovider
		 */
		public static IConceptMapApiProvider getConceptMapProvider(String uriValue) {
			return getProviders().stream()
				.filter(provider -> provider.isSupported(uriValue))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing concept map: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
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
	 * @param logicalId
	 * @return
	 */
	String translate(LogicalId logicalId);
	
}
