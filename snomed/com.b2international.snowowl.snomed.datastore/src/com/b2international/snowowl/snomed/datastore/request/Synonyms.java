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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * Works as a cache on top of multiple SNOMED CT specific requests. Requests can access this cache via {@link ServiceProvider#service(Class)} method.
 * 
 * @since 6.5
 */
public final class Synonyms {

	private final BranchContext context;
	private Set<String> synonyms;

	public Synonyms(BranchContext context) {
		this.context = context;
	}
	
	public Set<String> get() {
		if (synonyms == null) {
			synonyms = SnomedRequests.prepareGetSynonyms()
					.setFields(SnomedConceptDocument.Fields.ID)
					.build()
					.execute(context)
					.stream()
					.map(IComponent::getId)
					.collect(Collectors.toSet());
		}
		return synonyms;
	}
	
}
