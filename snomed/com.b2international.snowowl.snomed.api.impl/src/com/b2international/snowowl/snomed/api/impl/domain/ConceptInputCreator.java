package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.*;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class ConceptInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedConceptInput, SnomedConceptUpdate, SnomedBrowserConcept> {
	@Override
	public SnomedConceptInput createInput(final String branchPath, SnomedBrowserConcept concept, InputFactory inputFactory) {
		final SnomedConceptInput conceptInput = new SnomedConceptInput();
		setCommonComponentProperties(branchPath, concept, conceptInput, ComponentCategory.CONCEPT);
		conceptInput.setIsAIdGenerationStrategy(conceptInput.getIdGenerationStrategy());

		// Find a parent relationship
		final String parentRelationshipId = getParentId(concept);
		conceptInput.setParentId(parentRelationshipId);

		final List<ISnomedDescriptionInput> descriptionInputs = newArrayList();
		for (ISnomedBrowserDescription description : concept.getDescriptions()) {
			descriptionInputs.add(inputFactory.createComponentInput(branchPath, description, ISnomedDescriptionInput.class));
		}

		conceptInput.setDescriptions(descriptionInputs);

		return conceptInput;
	}

	@Override
	public SnomedConceptUpdate createUpdate(SnomedBrowserConcept existingVersion, SnomedBrowserConcept newVersion) {
		boolean anyDifference = existingVersion.isActive() != newVersion.isActive()
				|| !existingVersion.getModuleId().equals(newVersion.getModuleId())
				|| !existingVersion.getDefinitionStatus().equals(newVersion.getDefinitionStatus());

		if (anyDifference) {
			final SnomedConceptUpdate snomedConceptUpdate = new SnomedConceptUpdate();
			snomedConceptUpdate.setModuleId(newVersion.getModuleId());
			snomedConceptUpdate.setDefinitionStatus(newVersion.getDefinitionStatus());
			snomedConceptUpdate.setActive(newVersion.isActive());
			return snomedConceptUpdate;
		} else {
			return null;
		}
	}

	@Override
	public boolean canCreateInput(Class<? extends ISnomedComponentInput> inputType) {
		return ISnomedConceptInput.class.isAssignableFrom(inputType);
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return ISnomedConceptUpdate.class.isAssignableFrom(updateType);
	}

}
