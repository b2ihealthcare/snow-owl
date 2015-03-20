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
package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.SnowOwlApplication;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.config.SnomedCoreConfiguration;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;

public class NamespaceIdGenerationStrategy implements IdGenerationStrategy {

	private final ComponentCategory category;
	private final String namespaceId;

	/**
	 * @param category
	 * @param namespaceId
	 */
	public NamespaceIdGenerationStrategy(final ComponentCategory category, final String namespaceId) {
		this.category = category;
		this.namespaceId = namespaceId;
	}

	@Override
	public String getId() {
		return ApplicationContext.getServiceForClass(ISnomedIdentifierService.class).generateId(category, getNamespaceIdOrDefault());
	}

	private String getNamespaceIdOrDefault() {
		// XXX: an empty namespace string for INT component IDs must be supported here
		return (null == namespaceId) 
				? SnowOwlApplication.INSTANCE.getConfiguration().getModuleConfig(SnomedCoreConfiguration.class).getDefaultNamespace()
				: namespaceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NamespaceIdGenerationStrategy [category=");
		builder.append(category);
		builder.append(", namespaceId=");
		builder.append(namespaceId);
		builder.append("]");
		return builder.toString();
	}
}
