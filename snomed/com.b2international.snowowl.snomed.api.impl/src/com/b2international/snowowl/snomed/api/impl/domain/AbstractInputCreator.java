package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponent;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationshipType;

public abstract class AbstractInputCreator {

	protected String getModuleOrDefault(ISnomedBrowserComponent component) {
		final String moduleId = component.getModuleId();
		return moduleId != null ? moduleId : SnomedConstants.Concepts.MODULE_SCT_CORE;
	}
	
	String getParentId(ISnomedBrowserConcept concept) {
		ISnomedBrowserRelationship parentRelationship = null;
		for (ISnomedBrowserRelationship relationship : concept.getRelationships()) {
			final ISnomedBrowserRelationshipType type = relationship.getType();
			final String conceptId = type.getConceptId();
			if (SnomedConstants.Concepts.IS_A.equals(conceptId)) {
				parentRelationship = relationship;
			}
		}
		if (parentRelationship != null) {
			return parentRelationship.getTarget().getConceptId();
		} else {
			throw new BadRequestException("At least one isA relationship is required.");
		}
	}

}
