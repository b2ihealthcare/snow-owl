package com.b2international.snowowl.snomed.api.impl.domain;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserConceptUpdate;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.core.domain.ISnomedConceptUpdate;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.NamespaceIdGenerationStrategy;
import com.b2international.snowowl.snomed.core.domain.UserIdGenerationStrategy;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedConceptCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedDescriptionCreateRequest;

public class ConceptInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedConceptCreateRequest, SnomedConceptUpdate, SnomedBrowserConcept> {
	@Override
	public SnomedConceptCreateRequest createInput(final String branchPath, SnomedBrowserConcept concept, InputFactory inputFactory) {
		final SnomedConceptCreateRequest conceptInput = new SnomedConceptCreateRequest();
		setCommonComponentProperties(branchPath, concept, conceptInput, ComponentCategory.CONCEPT);
		
		String conceptId = concept.getConceptId();
		if (conceptId != null) {
			conceptInput.setIdGenerationStrategy(new UserIdGenerationStrategy(conceptId));
		}
		
		conceptInput.setDefinitionStatus(concept.getDefinitionStatus());

		// Find a parent relationship
		final String parentId = getParentId(concept);
		conceptInput.setParentId(parentId);
		conceptInput.setIsAIdGenerationStrategy(new NamespaceIdGenerationStrategy(ComponentCategory.RELATIONSHIP, null));

		final List<SnomedDescriptionCreateRequest> descriptionInputs = newArrayList();
		for (ISnomedBrowserDescription description : concept.getDescriptions()) {
			descriptionInputs.add(inputFactory.createComponentInput(branchPath, description, SnomedDescriptionCreateRequest.class));
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
	public boolean canCreateInput(Class<? extends SnomedComponentCreateRequest> inputType) {
		return SnomedConceptCreateRequest.class.isAssignableFrom(inputType);
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return ISnomedConceptUpdate.class.isAssignableFrom(updateType);
	}

}
