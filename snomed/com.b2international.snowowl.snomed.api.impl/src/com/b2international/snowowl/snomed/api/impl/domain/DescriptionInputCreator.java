package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.Map;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.server.request.BaseSnomedComponentUpdateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionUpdateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedDescriptionUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;

public class DescriptionInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedDescriptionCreateRequest, SnomedDescriptionUpdateRequest, SnomedBrowserDescription> {

	@Override
	public SnomedDescriptionCreateRequest createInput(String branchPath, SnomedBrowserDescription description, InputFactory inputFactory) {
		return (SnomedDescriptionCreateRequest) SnomedRequests
				.prepareNewDescription()
				.setModuleId(getModuleOrDefault(description))
				.setLanguageCode(description.getLang())
				.setTypeId(description.getType().getConceptId())
				.setTerm(description.getTerm())
				.setAcceptability(description.getAcceptabilityMap())
				.setCaseSignificance(description.getCaseSignificance())
				.build();
	}

	@Override
	public SnomedDescriptionUpdateRequest createUpdate(SnomedBrowserDescription existingDesc, SnomedBrowserDescription newVersionDesc) {
		final SnomedDescriptionUpdateRequestBuilder update = SnomedRequests.prepareUpdateDescription(existingDesc.getDescriptionId());
		
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
		return change ? (SnomedDescriptionUpdateRequest) update.build() : null;
	}

	@Override
	public boolean canCreateInput(Class<? extends SnomedComponentCreateRequest> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionCreateRequest.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends BaseSnomedComponentUpdateRequest> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionUpdateRequest.class, updateType.getName());
	}
}
