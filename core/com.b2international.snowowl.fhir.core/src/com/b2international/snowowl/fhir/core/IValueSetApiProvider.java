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
package com.b2international.snowowl.fhir.core;

import java.nio.file.Path;
import java.util.Collection;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.model.valueset.ValueSet;
import com.google.common.collect.ImmutableList;

/**
 * Extension point interface for value set specific FHIR API support. 
 * 
 * @see 'com.b2international.snowowl.fhir.core.valueSetProvider' for the extension point definition
 * @since 6.4
 */
public interface IValueSetApiProvider {
	
	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.4
	 */
	enum Registry {
		
		INSTANCE;
		
		private final static String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.core.valueSetProvider"; //$NON-NLS-N$
		private final Collection<IValueSetApiProvider> providers;
		
		private Registry() {
			this.providers = ImmutableList.copyOf(Extensions.getExtensions(FHIR_EXTENSION_POINT, IValueSetApiProvider.class));
		}
		
		public static Collection<IValueSetApiProvider> getProviders() {
			return INSTANCE.providers;
		}
		
		/**
		 * Returns the matching {@link IValueSetApiProvider} for the given path (repository/shortName).
		 * @param logical code system path (e.g. icd10Store/ICD-10)
		 * @return FHIR value set provider
		 * @throws com.b2international.snowowl.fhir.core.exceptions.BadRequestException - if provider is not found with the given path
		 */
		public static IValueSetApiProvider getValueSetProvider(Path path) {
			return getProviders().stream()
				.filter(provider -> provider.isSupported(path))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing value set: " + path, OperationOutcomeCode.MSG_NO_MODULE, "system=" + path));
		}
		
		/**
		 * Returns the matching {@link IValueSetApiProvider} for the given URI.
		 * @param uriValue
		 * @return FHIR value setprovider
		 */
		public static IValueSetApiProvider getValueSetProvider(String uriValue) {
			return getProviders().stream()
				.filter(provider -> provider.isSupported(uriValue))
				.findFirst()
				.orElseThrow(() -> new BadRequestException("Did not find FHIR module for managing value set: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue));
		}
	}
	
	/**
	 * @param uri
	 * @return true if the code system represented by the URI is supported
	 */
	boolean isSupported(String uri);
	
	/**
	 * @param logical code system path (repositoryId/shortName)
	 * @return true if this provider supports the code system represented by the path
	 */
	boolean isSupported(Path path);
	
	/**
	 * Returns the value sets supported by this provider.
	 * TODO: move this to a different extension. (probably an extension definition per resource)
	 * @return collection of value sets supported
	 */
	Collection<ValueSet> getValueSets();

	/**
	 * Returns the value set for the passed in logical path (repositoryId/valueSetId)
	 * @param valueSetPath
	 * @return {@link ValueSet}
	 * @throws BadRequestException if the value set is not supported by this provider
	 */
	ValueSet getValueSet(Path valueSetPath);

}
