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
package com.b2international.snowowl.snomed.datastore.id.action;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.datastore.id.ISnomedIdentifierService;

/**
 * @since 4.5
 */
public class ReserveAction extends IdAction<String> {

	private final String namespace;
	private final ComponentCategory category;

	private String componentId;

	public ReserveAction(final String namespace, final ComponentCategory category, final ISnomedIdentifierService identifierService) {
		super(identifierService);
		this.namespace = namespace;
		this.category = category;
	}

	@Override
	public void rollback() {
		if (!isFailed())
			identifierService.release(componentId);
	}

	@Override
	public void execute() {
		identifierService.reserve(namespace, category);
	}

	@Override
	public void commit() {
		if (!isFailed())
			identifierService.register(componentId);
	}

	@Override
	public String get() {
		return componentId;
	}

}
