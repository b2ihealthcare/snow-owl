/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.id.action;

import java.util.Set;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.snomed.datastore.id.request.AbstractSnomedIdentifierEnumeratedRequestBuilder;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
abstract class AbstractEnumeratedIdAction extends AbstractIdAction<Boolean> {

	private final Set<String> componentIds;

	public AbstractEnumeratedIdAction(final Set<String> componentIds) {
		this.componentIds = ImmutableSet.copyOf(componentIds);
	}

	@Override
	protected final Boolean doExecute(RepositoryContext context) {
		return createRequestBuilder()
				.setComponentIds(componentIds)
				.build()
				.execute(context);
	}

	protected abstract AbstractSnomedIdentifierEnumeratedRequestBuilder<?, Boolean> createRequestBuilder();
	
	@Override
	public final String toString() {
		return MoreObjects.toStringHelper(this).add("componentIds", componentIds).toString();
	}

}
