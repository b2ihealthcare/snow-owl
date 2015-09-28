package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.core.exceptions.BadRequestException;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.SnomedConstants;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserComponent;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationshipType;

public abstract class AbstractInputCreator {

	public static final String SNOMEDCT = "SNOMEDCT";

	void setCommonComponentProperties(String branchPath, ISnomedBrowserComponent component, AbstractSnomedComponentInput componentInput, ComponentCategory componentCategory) {
		componentInput.setBranchPath(branchPath);
		componentInput.setCodeSystemShortName(SNOMEDCT);
		final String moduleId = component.getModuleId();
		componentInput.setModuleId(moduleId != null ? moduleId : SnomedConstants.Concepts.MODULE_SCT_CORE);
		// Use default namespace
		final NamespaceIdGenerationStrategy idGenerationStrategy = new NamespaceIdGenerationStrategy(componentCategory, null);
		componentInput.setIdGenerationStrategy(idGenerationStrategy);
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
