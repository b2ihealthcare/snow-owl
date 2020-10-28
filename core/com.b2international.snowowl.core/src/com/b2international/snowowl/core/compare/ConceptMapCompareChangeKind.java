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
 * @since 7.11
 */
public enum ConceptMapCompareChangeKind {
	PRESENT("Present", "2"),
	MISSING("Missing", "1"),

	DIFFERENT_TARGET("Different Target", "3"),

	DIFFERENT_STATUS("Different Status", "4"),

	DIFFERENT_MAP_ADVICE("Different Map Advice", "5"),
	DIFFERENT_MAP_GROUP("Different Map Group", "6"),
	DIFFERENT_MAPPING_CORRELATION("Different Mapping Correlation", "7"),
	DIFFERENT_MAP_PRIORITY("Different Map Priority", "8"),
	DIFFERENT_MAP_RULE("Different Map Rule", "9"),

	UNCHANGED("Same", "10");

	private final String label;
	private final String priority;

	private ConceptMapCompareChangeKind(final String label, final String priority) {
		this.label = checkNotNull(label, "label");
		this.priority = priority;
	}

	@Override
	public String toString() {
		return label;
	}

	public String getPriority() {
		return priority;
	}
}
