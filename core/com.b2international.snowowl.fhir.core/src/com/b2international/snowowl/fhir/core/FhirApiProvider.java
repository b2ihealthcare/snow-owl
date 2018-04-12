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
import java.util.Locale;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.eventbus.IEventBus;

/**
 * 
 * @since 6.4
 */
public class FhirApiProvider {
	
	/**
	 * @return the {@link IEventBus} service to access terminology resources.
	 */
	protected final IEventBus getBus() {
		return ApplicationContext.getServiceForClass(IEventBus.class);
	}
	
	/**
	 * @param version - the version to target 
	 * @return an absolute branch path to use in terminology API requests
	 */
	protected final String getBranchPath(String version) {
		return CompareUtils.isEmpty(version) ? Branch.MAIN_PATH : Branch.get(Branch.MAIN_PATH, version); 
	}
	
	/**
	 * Returns (attempts) the ISO 639 two letter code based on the language name.
	 * @return two letter language code
	 */
	protected static String getLanguageCode(String language) {
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
