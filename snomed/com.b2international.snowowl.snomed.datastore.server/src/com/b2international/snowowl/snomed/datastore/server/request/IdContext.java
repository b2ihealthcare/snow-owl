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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.DelegatingBranchContext;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

/**
 * @since 4.5
 */
public class IdContext extends DelegatingBranchContext {

	private final SnomedIdentifiers snomedIdentifiers;

	public IdContext(final BranchContext context, final SnomedIdentifiers snomedIdentifiers) {
		super(context);
		this.snomedIdentifiers = snomedIdentifiers;
	}

	@Override
	public <T> T service(Class<T> type) {
		if (SnomedIdentifiers.class.isAssignableFrom(type)) {
			return type.cast(snomedIdentifiers);
		} else {
			return super.service(type);
		}
	}

}
