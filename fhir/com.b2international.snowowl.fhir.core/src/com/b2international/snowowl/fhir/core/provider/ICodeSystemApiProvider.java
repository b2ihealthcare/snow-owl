/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;

/**
 * Extension point interface for code system specific FHIR API support. 
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/terminologies.html#system">FHIR:Terminologies:System</a> to determine whether a code system is supported
 * @since 7.0
 */
public interface ICodeSystemApiProvider extends IFhirApiProvider {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.4
	 */
	enum Registry {
		
		INSTANCE;
		
		private final Collection<ICodeSystemApiProvider.Factory> providers;
		
		private Registry() {
			this.providers = ClassPathScanner.INSTANCE.getComponentsByInterface(ICodeSystemApiProvider.Factory.class);
		}
		
		public static Collection<ICodeSystemApiProvider> getProviders(IEventBus bus, List<ExtendedLocale> locales) {
			return INSTANCE.providers.stream().map(factory -> factory.create(bus, locales)).collect(Collectors.toUnmodifiableList());
		}

		public static ICodeSystemApiProvider getProvider(IEventBus bus, List<ExtendedLocale> locales, String toolingId) {
			return getProviders(bus, locales).stream()
					.filter(provider -> provider.getToolingId().equals(toolingId))
					.findFirst()
					.orElseThrow(() -> new BadRequestException("Did not find FHIR support for tooling: " + toolingId, OperationOutcomeCode.MSG_NO_MODULE, "system=" + toolingId));
		}
		
		/**
		 * Returns the matching {@link ICodeSystemApiProvider} for the given logical id (repository:branchPath).
		 * @param bus
		 * @param locales
		 * @param logicalId - the logical id (e.g. icd10Store:20140101)
		 * @return FHIR code system provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
//		public static ICodeSystemApiProvider getCodeSystemProvider(IEventBus bus, List<ExtendedLocale> locales, LogicalId logicalId) {
//			return getProviders(bus, locales).stream()
//				.filter(provider -> provider.isSupported(logicalId))
//				.findFirst()
//				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for code system: " + logicalId, OperationOutcomeCode.MSG_NO_MODULE, "system=" + logicalId));
//		}
		
//		/**
//		 * Returns the matching {@link ICodeSystemApiProvider} for the given URI.
//		 * @param bus
//		 * @param locales
//		 * @param uriValue
//		 * @return FHIR code system provider
//		 */
//		public static ICodeSystemApiProvider getCodeSystemProvider(IEventBus bus, List<ExtendedLocale> locales, String uriValue) {
//			return getProviders(bus, locales).stream()
//				.filter(provider -> provider.isSupported(uriValue))
//				.findFirst()
//				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for code system: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
//		}
		
	}
	
	/**
	 * @since 7.2
	 */
	interface Factory {
		ICodeSystemApiProvider create(IEventBus bus, List<ExtendedLocale> locales);
	}
	
	/**
	 * Performs the lookup operation based on the parameter-based lookup request.
	 * 
	 * <p>
	 * From the spec:
	 * If no properties are specified, the server chooses what to return. The following properties are defined for all code systems: url, name, version (code system info) 
	 * and code information: display, definition, designation, parent and child, and for designations, lang.X where X is a designation language code. 
	 * Some of the properties are returned explicit in named parameters (when the names match), and the rest (except for lang.X) in the property parameter group
	 * </p>
	 * @param lookupRequest
	 * @return result of the lookup
	 */
	LookupResult lookup(LookupRequest lookupRequest);
	
	/**
	 * @return the terminology toolingId associated with this provider
	 */
	String getToolingId();

	/**
	 * Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).
	 * See <a href="http://hl7.org/fhir/codesystem-operations.html#subsumes">docs</a> for more details.  
	 *  
	 * @param subsumption - in parameters
	 * @return
	 */
	SubsumptionResult subsumes(SubsumptionRequest subsumption);

	/**
	 * Creates a FHIR CodeSystem from an internal codesystem and version.
	 * @param codeSystem
	 * @param version
	 * @return
	 */
	CodeSystem createFhirCodeSystem(com.b2international.snowowl.datastore.CodeSystem codeSystem, CodeSystemVersionEntry version);

}
