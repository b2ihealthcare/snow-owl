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

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;

import com.b2international.commons.platform.Extensions;
import com.b2international.snowowl.fhir.core.codesystems.OperationOutcomeCode;
import com.b2international.snowowl.fhir.core.exceptions.BadRequestException;

/**
 * FHIR utility methods
 * 
 * @since 6.3
 */
public abstract class FhirUtils {

	public final static String FHIR_EXTENSION_POINT = "com.b2international.snowowl.fhir.core.provider"; //$NON-NLS-N$

	private FhirUtils() {
	}
	
	/**
	 * Returns the matching {@link IFhirProvider} for the given URI.
	 * @param uriValue
	 * @return FHIR provider
	 */
	public static IFhirProvider getFhirProvider(String uriValue) {
		
		Collection<IFhirProvider> fhirProviders = Extensions.getExtensions(FHIR_EXTENSION_POINT, IFhirProvider.class);
		
		Optional<IFhirProvider> fhirProviderOptional = fhirProviders.stream()
				.filter(provider -> provider.isSupported(uriValue))
				.findFirst();
		
		fhirProviderOptional.orElseThrow(() -> {
			return new BadRequestException("Did not find FHIR module for code system: " + uriValue, OperationOutcomeCode.MSG_NO_MODULE, "system=" + uriValue);
		});
		
		IFhirProvider iFhirProvider = fhirProviderOptional.get();
		return iFhirProvider;
	}
	
	/**
	 * Returns (attempts) the SO 639 two letter code based on the language name.
	 * @return two letter language code
	 */
	public static String getLanguageCode(String language) {
		
		if (language == null) return null;
		
	    Locale loc = new Locale("en");
	    String[] languages = Locale.getISOLanguages(); // list of language codes

	    return Arrays.stream(languages)
	    		.filter(l -> {
	    			Locale locale = new Locale(l,"US");
	    			return locale.getDisplayLanguage(loc).equalsIgnoreCase(language) 
	    					|| locale.getISO3Language().equalsIgnoreCase(language);
	    		}).findFirst().orElseGet(() -> null);
	}
}
