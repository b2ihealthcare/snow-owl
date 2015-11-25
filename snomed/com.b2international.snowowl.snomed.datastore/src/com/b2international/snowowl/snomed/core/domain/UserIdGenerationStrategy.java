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

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.snomed.datastore.id.SnomedIdentifiers;

/**
 */
public class UserIdGenerationStrategy implements IdGenerationStrategy {

	private final String id;

	/**
	 * @param id
	 */
	public UserIdGenerationStrategy(final String id) {
		this.id = id;
	}

	@Override
	public String generate(final BranchContext context) {
		final SnomedIdentifiers snomedIdentifiers = context.service(SnomedIdentifiers.class);
		// XXX is the ID already registered at this point or not?
		snomedIdentifiers.register(id);
		return id;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("UserIdGenerationStrategy [id=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}
}