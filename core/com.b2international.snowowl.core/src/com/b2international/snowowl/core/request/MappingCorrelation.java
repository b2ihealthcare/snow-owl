/*******************************************************************************
 * Copyright (c) 2019 B2i Healthcare. All rights reserved.
 *******************************************************************************/
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
