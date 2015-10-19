package com.b2international.snowowl.snomed.api.impl.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConceptUpdate;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponentInput;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.core.domain.ISnomedConceptInput;
import com.b2international.snowowl.snomed.core.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescriptionInput;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.UserIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.server.domain.SnomedConceptInput;

public class ConceptInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedConceptInput, SnomedConceptUpdate, SnomedBrowserConcept> {
	@Override
	public SnomedConceptInput createInput(final String branchPath, SnomedBrowserConcept concept, InputFactory inputFactory) {
		final SnomedConceptInput conceptInput = new SnomedConceptInput();
		setCommonComponentProperties(branchPath, concept, conceptInput, ComponentCategory.CONCEPT);
		
		// Find a parent relationship
		final String parentRelationshipId = getParentId(concept);
		conceptInput.setParentId(parentRelationshipId);
		conceptInput.setIsAIdGenerationStrategy(new UserIdGenerationStrategy(parentRelationshipId));

		final List<ISnomedDescriptionInput> descriptionInputs = newArrayList();
		for (ISnomedBrowserDescription description : concept.getDescriptions()) {
			descriptionInputs.add(inputFactory.createComponentInput(branchPath, description, ISnomedDescriptionInput.class));
		}

		conceptInput.setDescriptions(descriptionInputs);

		return conceptInput;
	}

	@Override
	public SnomedConceptUpdate createUpdate(SnomedBrowserConcept existingVersion, SnomedBrowserConcept newVersion) {
		final SnomedConceptUpdate snomedConceptUpdate = new SnomedConceptUpdate();
		boolean anyDifference = false;

		if (existingVersion.isActive() != newVersion.isActive()) {
			anyDifference = true;
			snomedConceptUpdate.setActive(newVersion.isActive());
		}
		if (!existingVersion.getModuleId().equals(newVersion.getModuleId())) {
			anyDifference = true;
			snomedConceptUpdate.setModuleId(newVersion.getModuleId());
		}
		if (!existingVersion.getDefinitionStatus().equals(newVersion.getDefinitionStatus())) {
			anyDifference = true;
			snomedConceptUpdate.setDefinitionStatus(newVersion.getDefinitionStatus());
		}
		
		if (newVersion instanceof SnomedBrowserConceptUpdate) {
			SnomedBrowserConceptUpdate update = (SnomedBrowserConceptUpdate) newVersion;
			if (!newVersion.isActive()) {
				InactivationIndicator inactivationIndicator = update.getInactivationIndicator();
				if (inactivationIndicator != null) {
					snomedConceptUpdate.setInactivationIndicator(inactivationIndicator);
					anyDifference = true;
				}
			}
		}

		if (anyDifference) {
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
