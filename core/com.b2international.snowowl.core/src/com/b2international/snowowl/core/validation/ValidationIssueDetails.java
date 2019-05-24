package com.b2international.snowowl.core.validation;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import com.b2international.snowowl.core.ComponentIdentifier;

public class ValidationIssueDetails {
	
	public static final String HIGHLIGHT_DETAILS = "highlightDetails";
	public final List<Entry<Integer, Integer>> detailEntries;
	public final ComponentIdentifier affectedComponentId;

	public ValidationIssueDetails(ComponentIdentifier affectedComponentId) {
		this.detailEntries = Collections.emptyList();
		this.affectedComponentId = affectedComponentId;
	}
	
	public ValidationIssueDetails(List<Entry<Integer, Integer>> detailEntries, ComponentIdentifier affectedComponentId) {
		this.detailEntries = detailEntries;
		this.affectedComponentId = affectedComponentId;
	}
	
}
