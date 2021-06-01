/*
 * Copyright 2018-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.model.ValidateCodeResult;
import com.b2international.snowowl.fhir.core.model.valueset.ExpandValueSetRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValidateCodeRequest;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.b2international.snowowl.fhir.core.search.FhirSearchParameter;

/**
 * Interface for value set specific FHIR API support. 
 * 
 * @since 7.0
 */
public interface IValueSetApiProvider extends IFhirApiProvider {
	
	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.4
	 */
	final class Registry {
		
		private final Collection<IValueSetApiProvider.Factory> providers;
		
		public Registry(ClassPathScanner scanner) {
			this.providers = scanner.getComponentsByInterface(IValueSetApiProvider.Factory.class);
		}
		
		public Collection<IValueSetApiProvider> getProviders(IEventBus bus, List<ExtendedLocale> locales) {
			return providers.stream().map(factory -> factory.create(bus, locales)).collect(Collectors.toUnmodifiableList());
		}
		
		/**
		 * Returns the matching {@link IValueSetApiProvider} for the given path (repository:branchPath).
		 * @param bus
		 * @param locales
		 * @param componentURI - code system URI (e.g.icd10/20140101)
		 * @return FHIR value set provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
		public IValueSetApiProvider getValueSetProvider(IEventBus bus, List<ExtendedLocale> locales, ComponentURI componentURI) {
			return getProviders(bus, locales).stream()
				.filter(provider -> provider.isSupported(componentURI))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing value set: " + componentURI, OperationOutcomeCode.MSG_NO_MODULE, "system=" + componentURI));
		}
		
		/**
		 * Returns the matching {@link IValueSetApiProvider} for the given URI.
		 * @param bus
		 * @param locales
		 * @param uriValue
		 * @return FHIR value setprovider
		 */
		public IValueSetApiProvider getValueSetProvider(IEventBus bus, List<ExtendedLocale> locales, String uriValue) {
			return getProviders(bus, locales).stream()
				.filter(provider -> provider.isSupported(uriValue))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing value set: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
		}
	}
	
	/**
	 * @since 7.2
	 */
	interface Factory {
		IValueSetApiProvider create(IEventBus bus, List<ExtendedLocale> locales);
	}
	
	/**
	 * Returns the value sets based on the search parameters provided.
	 * Passing in an empty collection as parameters returns all the available value sets.
	 * @param searchParameters
	 * @return collection of value sets found based on the parameters
	 */
	Collection<ValueSet> getValueSets(final Set<FhirSearchParameter> searchParameters);

	/**
	 * @param componentURI - logical code system path (codeSystemShortName/version/typeId/componentId)
	 * @return true if this provider supports the code system represented by the logical id
	 */
	boolean isSupported(ComponentURI componentURI);

	/**
	 * Returns the value set for the passed in logical id (codeSystemShortName/version/typeId/componentId)
	 * @param componentURI
	 * @return {@link ValueSet}
	 * @throws BadRequestException if the value set is not supported by this provider
	 */
	ValueSet getValueSet(ComponentURI componentURI);
	
	/**
	 * Returns the expanded form of the value set specified by its logical id
	 * @param componentURI - logical ID of the valueset
	 * @return {@link ValueSet}
	 * @throws BadRequestException if the value set is not supported by this provider
	 */
	ValueSet expandValueSet(ComponentURI componentURI);

	/**
	 * Returns the expanded value set for the passed in value set URI
	 * @param url
	 * @return {@link ValueSet}
	 * @throws BadRequestException if the value set is not supported by this provider
	 */
	ValueSet expandValueSet(String url);
	
	/**
	 * Returns the expanded value set for the passed in request
	 * @param request - the expand request
	 * @return expanded {@link ValueSet}
	 * @throws BadRequestException if the value set is not supported by this provider
	 */
	ValueSet expandValueSet(ExpandValueSetRequest request);
	
	/**
	 * Validates a code against a provided value set
	 * @param validateCodeRequest - code to validate
	 * @param componentURI - logical id of the value set to validate the code against
	 * @return validation result
	 */
	ValidateCodeResult validateCode(ValidateCodeRequest validateCodeRequest, ComponentURI componentURI);

	/**
	 * Validates a code against a provided value set defined by its canonical URL
	 * @param validateCodeRequest
	 * @return validation result
	 */
	ValidateCodeResult validateCode(ValidateCodeRequest validateCodeRequest);

}
