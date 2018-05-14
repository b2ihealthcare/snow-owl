/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.domain.constraint;

import java.util.Set;

import com.google.common.base.Joiner;

/**
 * @since 6.5
 */
public final class SnomedEnumeratedDefinition extends SnomedConceptSetDefinition {

	private Set<String> conceptIds;

	public Set<String> getConceptIds() {
		return conceptIds;
	}
	
	public void setConceptIds(Set<String> conceptIds) {
		this.conceptIds = conceptIds;
	}
	
	@Override
	public String toEcl() {
		return Joiner.on(" OR ").join(conceptIds);
	}
}
