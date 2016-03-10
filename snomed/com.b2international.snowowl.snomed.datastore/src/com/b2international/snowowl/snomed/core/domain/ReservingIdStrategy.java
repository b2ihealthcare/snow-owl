/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain;

import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.google.common.base.Objects;

/**
 * @since 4.5
 */
public class ReservingIdStrategy implements IdGenerationStrategy {

	private final ComponentCategory category;
	private final String namespaceId;

	public ReservingIdStrategy(final ComponentCategory category) {
		this(category, null);
	}
	
	public ReservingIdStrategy(final ComponentCategory category, final String namespaceId) {
		this.category = category;
		this.namespaceId = namespaceId;
	}

	@Override
	public String generate(BranchContext context) {
		// XXX: Does not add an IdAction to SnomedIdentifiers
		final ISnomedIdentifierService identifierService = context.service(ISnomedIdentifierService.class);
		final String componentId = identifierService.reserve(getNamespaceIdOrDefault(), category);
		return componentId;
	}

	private String getNamespaceIdOrDefault() {
		// XXX: an empty namespace string for INT component IDs must be supported here
		return (null == namespaceId) 
				? SnowOwlApplication.INSTANCE.getConfiguration().getModuleConfig(SnomedCoreConfiguration.class).getDefaultNamespace()
				: namespaceId;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("category", category)
				.add("namespaceId", namespaceId)
				.toString();
	}
}
