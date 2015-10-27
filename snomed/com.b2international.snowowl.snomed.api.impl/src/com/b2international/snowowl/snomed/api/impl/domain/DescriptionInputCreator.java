package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.Map;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedDescriptionCreateRequest;

public class DescriptionInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedDescriptionCreateRequest, SnomedDescriptionUpdate, SnomedBrowserDescription> {

	@Override
	public SnomedDescriptionCreateRequest createInput(String branchPath, SnomedBrowserDescription description, InputFactory inputFactory) {
		final SnomedDescriptionCreateRequest descriptionInput = new SnomedDescriptionCreateRequest();
		setCommonComponentProperties(branchPath, description, descriptionInput, ComponentCategory.DESCRIPTION);
		descriptionInput.setLanguageCode(description.getLang());
		descriptionInput.setTypeId(description.getType().getConceptId());
		descriptionInput.setTerm(description.getTerm());
		descriptionInput.setAcceptability(description.getAcceptabilityMap());
		return descriptionInput;
	}

	@Override
	public SnomedDescriptionUpdate createUpdate(SnomedBrowserDescription existingDesc, SnomedBrowserDescription newVersionDesc) {
		final SnomedDescriptionUpdate update = new SnomedDescriptionUpdate();
		boolean change = false;
		if (existingDesc.isActive() != newVersionDesc.isActive()) {
			change = true;
			update.setActive(newVersionDesc.isActive());
		}
		if (!existingDesc.getModuleId().equals(newVersionDesc.getModuleId())) {
			change = true;
			update.setModuleId(newVersionDesc.getModuleId());
		}
		final Map<String, Acceptability> newAcceptabilityMap = newVersionDesc.getAcceptabilityMap();
		if (!existingDesc.getAcceptabilityMap().equals(newAcceptabilityMap)) {
			change = true;
			update.setAcceptability(newAcceptabilityMap);
		}
		if (existingDesc.getCaseSignificance() != newVersionDesc.getCaseSignificance()) {
			change = true;
			update.setCaseSignificance(newVersionDesc.getCaseSignificance());
		}
		return change ? update : null;
	}

	@Override
	public boolean canCreateInput(Class<? extends SnomedComponentCreateRequest> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionCreateRequest.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionUpdate.class, updateType.getName());
	}
}
