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
package com.b2international.snowowl.core.request;

/**
 * @since 7.2
 */
public enum MappingCorrelation {

	NOT_SPECIFIED("Not specified"),
	EXACT_MATCH("Exact match"),
	BROAD_TO_NARROW("Broad to narrow"),
	NARROW_TO_BROAD("Narrow to broad"),
	PARTIAL_OVERLAP("Partial overlap"),
	NOT_MAPPABLE("Not mappable");

	public static MappingCorrelation getByDisplayName(String displayName) {
		final MappingCorrelation[] correlations = values();
		for (MappingCorrelation possibleCorrelation : correlations) {
			if (possibleCorrelation.getDisplayName().equalsIgnoreCase(displayName)) {
				return possibleCorrelation;
			}
		}
		
		return MappingCorrelation.NOT_SPECIFIED;
	}

	private final String displayName;

	private MappingCorrelation(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
	  return displayName;
	}

}
