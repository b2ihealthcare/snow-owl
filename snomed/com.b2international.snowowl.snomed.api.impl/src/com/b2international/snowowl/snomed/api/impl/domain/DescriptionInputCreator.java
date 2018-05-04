package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.Collections;
import java.util.Map;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserDescription;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedCoreComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedDescriptionUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

public class DescriptionInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedDescriptionCreateRequest, SnomedDescriptionUpdateRequest, SnomedBrowserDescription> {

	@Override
	public SnomedDescriptionCreateRequest createInput(String branchPath, SnomedBrowserDescription description, InputFactory inputFactory) {
		final SnomedDescriptionCreateRequestBuilder builder = SnomedRequests.prepareNewDescription()
				.setModuleId(getModuleOrDefault(description))
				.setLanguageCode(description.getLang())
				.setTypeId(description.getType().getConceptId())
				.setTerm(description.getTerm())
				.setAcceptability(description.getAcceptabilityMap())
				.setCaseSignificance(description.getCaseSignificance());
		
		if (description.getDescriptionId() != null) {
			builder.setId(description.getDescriptionId());
		} else {
			builder.setIdFromNamespace(getDefaultNamespace());
		}
		
		return (SnomedDescriptionCreateRequest) builder.build();
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
		Map<String, Acceptability> newAcceptabilityMap = newVersionDesc.getAcceptabilityMap();
		if (newAcceptabilityMap == null) {
			newAcceptabilityMap = Collections.emptyMap();
		}
		// If the description is inactive make sure the acceptability map is empty to make the language reference set entries inactive
		if (!newVersionDesc.isActive()) {
			newAcceptabilityMap.clear();
		}
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
	public boolean canCreateInput(Class<? extends SnomedCoreComponentCreateRequest> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionCreateRequest.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends SnomedComponentUpdateRequest> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedDescriptionUpdateRequest.class, updateType.getName());
	}
}
