package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.datastore.server.request.BaseSnomedComponentUpdateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRelationshipCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRelationshipUpdateRequest;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRelationshipUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.server.request.SnomedRequests;

public class RelationshipInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedRelationshipCreateRequest, SnomedRelationshipUpdateRequest, SnomedBrowserRelationship> {
	@Override
	public SnomedRelationshipCreateRequest createInput(String branchPath, SnomedBrowserRelationship relationship, InputFactory inputFactory) {
		return (SnomedRelationshipCreateRequest) SnomedRequests
				.prepareNewRelationship()
				.setModuleId(getModuleOrDefault(relationship))
				.setTypeId(relationship.getType().getConceptId())
				.setCharacteristicType(relationship.getCharacteristicType())
				.setSourceId(relationship.getSourceId())
				.setDestinationId(relationship.getTarget().getConceptId())
				.setGroup(relationship.getGroupId())
				.setModifier(relationship.getModifier())
				.build();
	}

	@Override
	public SnomedRelationshipUpdateRequest createUpdate(SnomedBrowserRelationship existingVersion, SnomedBrowserRelationship newVersion) {
		final SnomedRelationshipUpdateRequestBuilder update = SnomedRequests.prepareUpdateRelationship(existingVersion.getRelationshipId());
		boolean change = false;
		if (!existingVersion.getModuleId().equals(newVersion.getModuleId())) {
			change = true;
			update.setModuleId(newVersion.getModuleId());
		}
		if (existingVersion.isActive() != newVersion.isActive()) {
			change = true;
			update.setActive(newVersion.isActive());
		}
		if (existingVersion.getGroupId() != newVersion.getGroupId()) {
			change = true;
			update.setGroup(newVersion.getGroupId());
		}
		if (existingVersion.getCharacteristicType() != newVersion.getCharacteristicType()) {
			change = true;
			update.setCharacteristicType(newVersion.getCharacteristicType());
		}
		if (existingVersion.getModifier() != newVersion.getModifier()) {
			change = true;
			update.setModifier(newVersion.getModifier());
		}
		return change ? (SnomedRelationshipUpdateRequest) update.build() : null;
	}

	@Override
	public boolean canCreateInput(Class<? extends SnomedComponentCreateRequest> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipCreateRequest.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends BaseSnomedComponentUpdateRequest> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipUpdateRequest.class, updateType.getName());
	}
}
