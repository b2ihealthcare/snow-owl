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
package com.b2international.snowowl.snomed.api.rest.domain;

import com.b2international.snowowl.snomed.core.domain.ConstantIdStrategy;
import com.b2international.snowowl.snomed.core.domain.IdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.NamespaceIdStrategy;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentCreateRequestBuilder;

/**
 * @since 4.0
 */
public abstract class AbstractSnomedComponentRestInput<I extends SnomedComponentCreateRequestBuilder<I>> {

	private String id;
	private Boolean active = Boolean.TRUE;
	private String moduleId;
	private String namespaceId;

	public Boolean isActive() {
		return active;
	}
	
	public void setActive(Boolean active) {
		this.active = active;
	}
	
	public String getId() {
		return id;
	}

	public String getModuleId() {
		return moduleId;
	}
	
	public String getNamespaceId() {
		return namespaceId;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}
	
	public void setNamespaceId(final String namespaceId) {
		this.namespaceId = namespaceId;
	}

	protected abstract I createRequestBuilder();
	
	protected I toRequestBuilder() {
		final I req = createRequestBuilder();
		req.setId(createIdGenerationStrategy(getId()));
		req.setModuleId(getModuleId());
		req.setActive(isActive());
		return req;
	}

	protected IdGenerationStrategy createIdGenerationStrategy(String idOrNull) {
		if (idOrNull == null) {
			return new NamespaceIdStrategy(namespaceId);
		} else {
			return new ConstantIdStrategy(idOrNull);
		}
	}
}
