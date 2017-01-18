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
package com.b2international.snowowl.snomed.datastore.id.request;

import javax.annotation.Nonnegative;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequest;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;
import com.b2international.snowowl.snomed.datastore.id.domain.SnomedComponentIds;

/**
 * @since 4.5
 */
final class SnomedIdentifierReserveRequest extends BaseRequest<BranchContext, SnomedComponentIds> {

	@NotNull
	private final ComponentCategory category;
	
	private final String namespace;

	@Nonnegative
	private final int quantity;

	SnomedIdentifierReserveRequest(ComponentCategory category, String namespace, int quantity) {
		this.category = category;
		this.namespace = namespace;
		this.quantity = quantity;
	}

	@Override
	public SnomedComponentIds execute(BranchContext context) {
		return new SnomedComponentIds(context.service(ISnomedIdentifierService.class).reserve(namespace, category, quantity));
	}

	@Override
	protected Class<SnomedComponentIds> getReturnType() {
		return SnomedComponentIds.class;
	}

}
