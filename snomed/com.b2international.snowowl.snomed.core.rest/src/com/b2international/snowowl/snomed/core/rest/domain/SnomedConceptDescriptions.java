/*
 * Copyright 2011-2018 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.snomed.core.rest.domain;

import java.util.List;

import com.b2international.snowowl.snomed.core.domain.SnomedDescription;

/**
 * @since 1.0
 */
public class SnomedConceptDescriptions {

	private List<SnomedDescription> conceptDescriptions;

	public List<SnomedDescription> getConceptDescriptions() {
		return conceptDescriptions;
	}

	public void setConceptDescriptions(final List<SnomedDescription> conceptDescriptions) {
		this.conceptDescriptions = conceptDescriptions;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedConceptDescriptions [conceptDescriptions=");
		builder.append(conceptDescriptions);
		builder.append("]");
		return builder.toString();
	}
}