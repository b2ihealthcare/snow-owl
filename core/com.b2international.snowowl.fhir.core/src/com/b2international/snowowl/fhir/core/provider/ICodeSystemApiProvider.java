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

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.fhir.core.LogicalId;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.google.common.collect.ImmutableList;

/**
 * Extension point interface for code system specific FHIR API support. 
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/terminologies.html#system">FHIR:Terminologies:System</a> to determine whether a code system is supported
 * @see 'com.b2international.snowowl.fhir.core.codeSystemProvider' for the extension point definition
 * @since 6.4
 */
public interface ICodeSystemApiProvider extends IFhirApiProvider {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.4
	 */
	enum Registry {
		
		INSTANCE;
		
		private final static String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.core.codeSystemProvider"; //$NON-NLS-N$
		private final Collection<ICodeSystemApiProvider> providers;
		
		private Registry() {
			this.providers = ImmutableList.copyOf(Extensions.getExtensions(FHIR_EXTENSION_POINT, ICodeSystemApiProvider.class));
		}
		
		public static Collection<ICodeSystemApiProvider> getProviders() {
			return INSTANCE.providers;
		}
		
		/**
		 * Returns the matching {@link ICodeSystemApiProvider} for the given logical id (repository:branchPath).
		 * @param logical id (e.g. icd10Store:20140101)
		 * @return FHIR code system provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
		public static ICodeSystemApiProvider getCodeSystemProvider(LogicalId logicalId) {
			return getProviders().stream()
				.filter(provider -> provider.isSupported(logicalId))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for code system: " + logicalId, OperationOutcomeCode.MSG_NO_MODULE, "system=" + logicalId));
		}
		
		/**
		 * Returns the matching {@link ICodeSystemApiProvider} for the given URI.
		 * @param uriValue
		 * @return FHIR code system provider
		 */
		public static ICodeSystemApiProvider getCodeSystemProvider(String uriValue) {
			return getProviders().stream()
				.filter(provider -> provider.isSupported(uriValue))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for code system: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
		}
		
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
	 * Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).
	 * See <a href="http://hl7.org/fhir/codesystem-operations.html#subsumes">docs</a> for more details.  
	 *  
	 * @param subsumption - in parameters
	 * @return
	 */
	SubsumptionResult subsumes(SubsumptionRequest subsumption);

	/**
	 * Returns the code systems supported by this provider
	 * @return collection of code systems supported
	 */
	Collection<CodeSystem> getCodeSystems();

	/**
	 * Returns the code system for the passed in code system URI
	 * @param codeSystemUri
	 * @return {@link CodeSystem}
	 * @throws BadRequestException if the code system is not supported by this provider
	 * 
	 * TODO: Is this used anywhere?  Should be probably as a filter.
	 */
	CodeSystem getCodeSystem(String codeSystemUri);

	/**
	 * Returns the code system for the passed in logical id @see {@link LogicalId}
	 * @param codeSystemId
	 * @return {@link CodeSystem}
	 * @throws BadRequestException if the code system is not supported by this provider
	 */
	CodeSystem getCodeSystem(LogicalId codeSystemId);

}
