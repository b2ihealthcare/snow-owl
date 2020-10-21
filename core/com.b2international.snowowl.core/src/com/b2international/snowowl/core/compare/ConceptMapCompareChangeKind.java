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
	PRESENT("Present in Base"),
	NOT_PRESENT("Not Present in Base"),
	
	DIFFERENT_TARGET_PRESENT("Different Target - Present in Base"),
	DIFFERENT_TARGET_NOT_PRESENT("Different Target - Not Present in Base"),
	
	DIFFERENT_STATUS_PRESENT("Different Status - Present in Base"),
	DIFFERENT_STATUS_NOT_PRESENT("Different Status - Not Present in Base"),
	
	DIFFERENT_MAP_ADVICE_PRESENT("Different Map Advice - Present in Base"),
	DIFFERENT_MAP_ADVICE_NOT_PRESENT("Different Map Advice - Not Present in Base"),
	
	DIFFERENT_MAP_GROUP_PRESENT("Different Map Group - Present in Base"),
	DIFFERENT_MAP_GROUP_NOT_PRESENT("Different Map Group - Not Present in Base"),
	
	DIFFERENT_MAPPING_CORRELATION_PRESENT("Different Mapping Correlation - Present in Base"),
	DIFFERENT_MAPPING_CORRELATION_NOT_PRESENT("Different Mapping Correlation - Not Present in Base"),
	
	DIFFERENT_MAP_PRIORITY_PRESENT("Different Map Priority - Present in Base"),
	DIFFERENT_MAP_PRIORITY_NOT_PRESENT("Different Map Priority - Not Present in Base"),
	
	DIFFERENT_MAP_RULE_PRESENT("Different Map Rule - Present in Base"),
	DIFFERENT_MAP_RULE_NOT_PRESENT("Different Map Rule - Not Present in Base"),
	
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
