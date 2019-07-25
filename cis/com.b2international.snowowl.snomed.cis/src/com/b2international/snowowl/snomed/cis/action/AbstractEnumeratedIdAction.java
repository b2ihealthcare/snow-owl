/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.cis.action;

import java.util.Set;

import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.snomed.cis.domain.SctIds;
import com.b2international.snowowl.snomed.cis.request.AbstractSnomedIdentifierEnumeratedRequestBuilder;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * @since 4.5
 */
abstract class AbstractEnumeratedIdAction extends AbstractIdAction<SctIds> {

	private final Set<String> componentIds;

	public AbstractEnumeratedIdAction(final Set<String> componentIds) {
		this.componentIds = ImmutableSet.copyOf(componentIds);
	}

	@Override
	protected final SctIds doExecute(RepositoryContext context) {
		return createRequestBuilder()
				.setComponentIds(componentIds)
				.build()
				.execute(context);
	}

	protected abstract AbstractSnomedIdentifierEnumeratedRequestBuilder<?> createRequestBuilder();
	
	@Override
	public final String toString() {
		return Objects.toStringHelper(this).add("componentIds", componentIds).toString();
	}

}
