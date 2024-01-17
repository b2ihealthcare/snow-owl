/*
 * Copyright 2020-2022 B2i Healthcare, https://b2ihealthcare.com
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

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptRequestCache;

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

			final Iterable<String> moduleIds = results.stream().map(SnomedComponent::getModuleId)::iterator;
			
			context.service(SnomedConceptRequestCache.class)
				.request(context, moduleIds, moduleOptions.getOptions("expand"), locales, modulesById -> {
					for (SnomedComponent component : results) {
						component.setModule(modulesById.get(component.getModuleId()));
					}
				});
			
		}
	}
	
}
