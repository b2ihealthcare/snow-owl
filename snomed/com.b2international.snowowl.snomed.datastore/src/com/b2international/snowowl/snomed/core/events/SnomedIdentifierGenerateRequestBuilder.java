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
package com.b2international.snowowl.snomed.core.events;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.datastore.request.BaseBranchRequestBuilder;
import com.b2international.snowowl.snomed.datastore.SnomedDatastoreActivator;

/**
 * @since 4.5
 */
public final class SnomedIdentifierGenerateRequestBuilder extends BaseBranchRequestBuilder<SnomedIdentifierGenerateRequestBuilder, String> {

	private ComponentCategory category;
	private String namespace;
	
	// TODO remove hard coded repository Id
	public SnomedIdentifierGenerateRequestBuilder() {
		super(SnomedDatastoreActivator.REPOSITORY_UUID);
	}
	
	public SnomedIdentifierGenerateRequestBuilder setCategory(ComponentCategory category) {
		this.category = category;
		return getSelf();
	}
	
	public SnomedIdentifierGenerateRequestBuilder setNamespace(String namespace) {
		this.namespace = namespace;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, String> doBuild() {
		return new SnomedIdentifierGenerateRequest(category, namespace);
	}

}
