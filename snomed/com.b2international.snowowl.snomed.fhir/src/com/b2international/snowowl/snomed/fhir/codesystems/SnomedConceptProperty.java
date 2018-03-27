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
package com.b2international.snowowl.snomed.fhir.codesystems;

import java.util.Arrays;
import java.util.Collection;

import com.b2international.snowowl.fhir.core.codesystems.ConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.ConceptPropertyType;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.google.common.collect.Sets;

/**
 * Experimental.
 * If a keeper, move it to the SNOMED specific bundle.
 * @since 6.3
 */
public class SnomedConceptProperty {
	
	private static Collection<Property> properties = Sets.newHashSet();

	static {
		Arrays.stream(ConceptProperties.values()).map(p -> {
			return new SnomedConceptProperty.Property(p.getCode(), p.getUri(), p.getDisplayName(), p.getConceptPropertyType());
		}).forEach(properties::add);
		
		//add the hard-coded SNOMED properties
		
		//add the dynamic relationship types as properties
	}
	
	static class Property {

		private Code code;
		private Uri uri;
		private String description;
		private ConceptPropertyType type;
		
		public Property(Code code, Uri uri, String description, ConceptPropertyType type) {
			this.code = code;
			this.uri = uri;
			this.description = description;
			this.type = type;
		}
	}
	
	
	
}
