/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.converter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 7.4
 */
final class ModuleExpander {

	private final BranchContext context;
	private final Options expand;
	private final List<ExtendedLocale> locales;

	public ModuleExpander(BranchContext context, Options expand, List<ExtendedLocale> locales) {
		this.context = context;
		this.expand = expand;
		this.locales = locales;
	}
	
	void expand(List<? extends SnomedComponent> results) {
		if (expand.containsKey(SnomedComponent.Expand.MODULE)) {
			final Options moduleOptions = expand.get(SnomedComponent.Expand.MODULE, Options.class);

			final Set<String> moduleIds = results.stream().map(SnomedComponent::getModuleId).collect(Collectors.toSet());
			
			final Map<String, SnomedConcept> modulesById = SnomedRequests.prepareSearchConcept()
				.all()
				.filterByIds(moduleIds)
				.setExpand(moduleOptions.getOptions("expand"))
				.setLocales(locales)
				.build()
				.execute(context)
				.stream()
				.collect(Collectors.toMap(SnomedConcept::getId, c -> c));
			
			for (SnomedComponent component : results) {
				component.setModule(modulesById.get(component.getModuleId()));
			}
		}
	}
	
}
