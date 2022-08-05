/*
 * Copyright 2022 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request.suggest;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.SnowOwl;
import com.b2international.snowowl.core.events.util.Promise;
import com.b2international.snowowl.core.plugin.ClassPathScanner;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Concept Suggester algorithm interface. Can suggests similar concepts compared to an array of like texts/items based on certain configurable algorithm specific parameters.
 * 
 * @since 8.5
 */
@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME, 
	include = JsonTypeInfo.As.PROPERTY,
	property = "type"
)
public interface ConceptSuggester {

	class Registry {
		
		private static final Logger LOG = LoggerFactory.getLogger("suggest");
		private final Map<String, Class<? extends ConceptSuggester>> suggesters = new LinkedHashMap<>();
		private ObjectMapper mapper;
		
		public Registry(ClassPathScanner scanner, ObjectMapper mapper) {
			this.mapper = mapper;
			scanner.getComponentsClassesByInterface(ConceptSuggester.class).forEach(suggesterClass -> {
				JsonTypeName typeName = suggesterClass.getAnnotation(JsonTypeName.class);
				if (typeName == null) {
					throw new SnowOwl.InitializationException("Missing JsonTypeName annotation from suggester implementation class: " + suggesterClass.getName());
				}
				
				final String type = typeName.value();
				if (suggesters.containsKey(type)) {
					LOG.warn("Duplicate concept suggester implementation found for key '{}'. Disabling {} in favor of {}.", type, suggesterClass.getName(), suggesters.get(type).getName());
				} else {
					suggesters.put(type, (Class<? extends ConceptSuggester>) suggesterClass);
				}
			});
		}
		
		public Set<String> getSuggesterTypes() {
			return suggesters.keySet();
		}
		
		public ConceptSuggester create(Suggester suggester) {
			if (!suggesters.containsKey(suggester.getType())) {
				throw new BadRequestException("'%s' suggester is not found. Available suggesters are: %s", suggester.getType(), getSuggesterTypes());
			}
			return mapper.convertValue(suggester, suggesters.get(suggester.getType()));
		}

	}
	
	/**
	 * Perform suggestions using the context to access like texts/items, perform search requests, etc. 
	 * 
	 * @param context - the context to use to compute the evaluations
	 * @param limit - the number of suggestions to return
	 * @param display - the requested display for the suggested concepts
	 * @param locales - a set of locales to compute the requested display
	 * @return
	 */
	Promise<Suggestions> suggest(ConceptSuggestionContext context, int limit, String display, List<ExtendedLocale> locales);
	
	
}
