/*
 * Copyright 2017-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.request;

import javax.annotation.Nonnegative;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.domain.SctIds;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @since 5.5
 */
abstract class AbstractSnomedIdentifierCountedRequest implements Request<RepositoryContext, SctIds> {

	@JsonProperty
	@NotNull
	private final ComponentCategory category;
	
	@JsonProperty
	private final String namespace;

	@JsonProperty
	@Nonnegative
	private final int quantity;

	AbstractSnomedIdentifierCountedRequest(ComponentCategory category, String namespace, int quantity) {
		this.category = category;
		this.namespace = namespace;
		this.quantity = quantity;
	}

	protected final ComponentCategory category() {
		return category;
	}
	
	protected final String namespace() {
		return namespace;
	}
	
	protected final int quantity() {
		return quantity;
	}

}
