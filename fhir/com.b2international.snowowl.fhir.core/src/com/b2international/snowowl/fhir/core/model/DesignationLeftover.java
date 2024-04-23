package com.b2international.snowowl.fhir.core.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.Description;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class DesignationLeftover {

	/**
	 * Maps the given collection of {@link Description} objects into FHIR {@link Designation} objects by mapping the term and language fields to the matching properties.
	 * @param descriptions
	 * @return a {@link List} of {@link Designation} objects, never <code>null</code>.
	 */
	@JsonIgnore
	public static List<Designation> fromDescriptions(Collection<Description> descriptions) {
		return descriptions
				.stream()
				.map(description -> Designation.builder().value(description.getTerm()).language(description.getLanguage()).build())
				.collect(Collectors.toList());
	}
	
}
