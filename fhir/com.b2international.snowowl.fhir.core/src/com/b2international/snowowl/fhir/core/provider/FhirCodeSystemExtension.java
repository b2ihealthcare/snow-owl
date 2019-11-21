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

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import com.b2international.commons.extension.ClassPathScanner;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.datastore.CodeSystemVersionEntry;
import com.b2international.snowowl.fhir.core.model.codesystem.CodeSystem;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.LookupResult;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionRequest;
import com.b2international.snowowl.fhir.core.model.codesystem.SubsumptionResult;
import com.google.common.collect.ImmutableList;

/**
 * Interface for code system specific FHIR API support. 
 * 
 * @see <a href="https://www.hl7.org/fhir/2016May/terminologies.html#system">FHIR:Terminologies:System</a> to determine whether a code system is supported
 * @since 7.0
 */
public interface FhirCodeSystemExtension {

	/**
	 * Registry that reads and instantiates FHIR API supporting services registered by the actual terminology plug-ins.
	 * 
	 * @since 6.4
	 */
	enum Registry {
		
		INSTANCE;
		
		private final Collection<FhirCodeSystemExtension> providers;
		
		private Registry() {
			this.providers = ImmutableList.copyOf(ClassPathScanner.INSTANCE.getComponentsByInterface(FhirCodeSystemExtension.class));
		}
		
		public static Collection<FhirCodeSystemExtension> getCodeSystemExtensions() {
			return INSTANCE.providers;
		}
		
		public static FhirCodeSystemExtension getCodeSystemExtension(String toolingId) {
			return INSTANCE.providers.stream().filter(ext -> ext.getToolingId().equals(toolingId)).findFirst().orElseThrow(() -> new IllegalArgumentException("Missing extension for: " + toolingId));
		}

	}
	
	/**
	 * @return the terminology toolingId associated with this provider
	 */
	String getToolingId();
	
	/**
	 * Performs the lookup operation based on the parameter-based lookup request.
	 * 
	 * <p>
	 * From the spec:
	 * If no properties are specified, the server chooses what to return. The following properties are defined for all code systems: url, name, version (code system info) 
	 * and code information: display, definition, designation, parent and child, and for designations, lang.X where X is a designation language code. 
	 * Some of the properties are returned explicit in named parameters (when the names match), and the rest (except for lang.X) in the property parameter group
	 * </p>
	 * @param context - where this lookup should be executed
	 * @param lookupRequest
	 * @return result of the lookup
	 */
	LookupResult lookup(BranchContext context, LookupRequest lookup);
	
	/**
	 * Test the subsumption relationship between code/Coding A and code/Coding B given the semantics of subsumption in the underlying code system (see hierarchyMeaning).
	 * See <a href="http://hl7.org/fhir/codesystem-operations.html#subsumes">docs</a> for more details.  
	 *  
	 * @param context - where this subsumption should be executed
	 * @param subsumption - in parameters
	 * @return
	 */
	SubsumptionResult subsumes(BranchContext context, SubsumptionRequest subsumption);

	/**
	 * Creates a FHIR CodeSystem from an internal codesystem and version.
	 * @param codeSystem
	 * @param version
	 * @return
	 */
	CodeSystem createFhirCodeSystem(com.b2international.snowowl.datastore.CodeSystem codeSystem, CodeSystemVersionEntry version);

	/**
	 * Returns (attempts) the ISO 639 two letter code based on the language name.
	 * @return two letter language code
	 */
	static String getLanguageCode(String language) {
		if (language == null) return null;
		
	    Locale loc = new Locale("en");
	    String[] languages = Locale.getISOLanguages(); // list of language codes

	    return Arrays.stream(languages)
	    		.filter(l -> {
	    			Locale locale = new Locale(l,"US");
	    			return locale.getDisplayLanguage(loc).equalsIgnoreCase(language) 
	    					|| locale.getISO3Language().equalsIgnoreCase(language);
	    		})
	    		.findFirst()
	    		.orElse(null);
	}
	
}
