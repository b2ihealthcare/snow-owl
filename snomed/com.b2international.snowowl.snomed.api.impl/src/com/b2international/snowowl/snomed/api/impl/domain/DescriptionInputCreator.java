package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;

import java.util.Map;

public class DescriptionInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedDescriptionInput, SnomedDescriptionUpdate, SnomedBrowserDescription> {

	@Override
	public SnomedDescriptionInput createInput(String branchPath, SnomedBrowserDescription description, InputFactory inputFactory) {
		final SnomedDescriptionInput descriptionInput = new SnomedDescriptionInput();
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
		update.setActive(newVersionDesc.isActive());
		update.setModuleId(newVersionDesc.getModuleId());
		final Map<String, Acceptability> newAcceptabilityMap = newVersionDesc.getAcceptabilityMap();
		if (!existingDesc.getAcceptabilityMap().equals(newAcceptabilityMap)) {
			update.setAcceptability(newAcceptabilityMap);
		}
		update.setCaseSignificance(newVersionDesc.getCaseSignificance());
		return update;
	}

	@Override
	public boolean canCreateInput(Class<? extends ISnomedComponentInput> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionInput.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionUpdate.class, updateType.getName());
	}
}
