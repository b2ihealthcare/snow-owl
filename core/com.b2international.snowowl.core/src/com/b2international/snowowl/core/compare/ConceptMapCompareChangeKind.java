/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @since 7.10
 */
public enum ConceptMapCompareChangeKind {
	PRESENT("Present"),
	MISSING("Missing"),

	DIFFERENT_TARGET("Different Target"),

	DIFFERENT_STATUS("Different Status"),

	DIFFERENT_MAP_ADVICE("Different Map Advice"),
	DIFFERENT_MAP_GROUP("Different Map Group"),
	DIFFERENT_MAPPING_CORRELATION("Different Mapping Correlation"),
	DIFFERENT_MAP_PRIORITY("Different Map Priority"),
	DIFFERENT_MAP_RULE("Different Map Rule"),

	UNCHANGED("Unchanged");

	private final String label;

	private ConceptMapCompareChangeKind(final String label) {
		this.label = checkNotNull(label, "label");
	}

	@Override
	public String toString() {
		return label;
	}
}
