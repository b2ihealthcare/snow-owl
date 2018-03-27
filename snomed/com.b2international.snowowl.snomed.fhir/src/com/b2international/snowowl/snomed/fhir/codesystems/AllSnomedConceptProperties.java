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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.api.IBranchPath;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.fhir.core.codesystems.CommonConceptProperties;
import com.b2international.snowowl.fhir.core.codesystems.ConceptPropertyType;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.google.common.collect.Sets;

/**
 * Experimental.
 * If a keeper.
 * @since 6.3
 */
public class AllSnomedConceptProperties {
	
	private static Collection<Property> properties = Sets.newHashSet();

	static {

		//Common properties
		Arrays.stream(CommonConceptProperties.values()).map(p -> {
			return new AllSnomedConceptProperties.Property(p.getCode(), p.getUri(), p.getDisplayName(), p.getConceptPropertyType());
		}).forEach(properties::add);
		
		//Core SNOMED CT properties
		Arrays.stream(CoreSnomedConceptProperties.values()).map(p -> {
			return new AllSnomedConceptProperties.Property(p.getCode(), p.getUri(), p.getCodeValue(), p.getConceptPropertyType());
		}).forEach(properties::add);

		//what should be the locale here? Likely we need to add the config locale as well
		final List<ExtendedLocale> locales = ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
		locales.add(ExtendedLocale.valueOf("en-x-" + Concepts.REFSET_LANGUAGE_TYPE_US));

		//add the dynamic relationship types as properties
		Set<Property> dynamicProperties = SnomedRequests.prepareSearchConcept()
			.all()
			.filterByActive(true)
			.filterByAncestor("246061005") //Attribute
			.setExpand("pt()")
			.setLocales(locales)
			.build(SnomedDatastoreActivator.REPOSITORY_UUID, IBranchPath.MAIN_BRANCH)
			.execute(ApplicationContext.getServiceForClass(IEventBus.class))
			.then(sc -> sc.stream()
					.map(c -> {
							return new AllSnomedConceptProperties.Property(new Code(c.getPt().getTerm()), getUri(c.getId()), c.getId(), ConceptPropertyType.CODE);
						})
					.collect(Collectors.toSet())
				)
			.getSync();
		
		properties.addAll(dynamicProperties);
		
	}
	
	public static Uri getUri(String id) {
		return new Uri(CoreSnomedConceptProperties.CODE_SYSTEM_URI+"/id" + id);
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
