package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipCreateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipUpdateRequest;
import com.b2international.snowowl.snomed.datastore.request.SnomedRelationshipUpdateRequestBuilder;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

public class RelationshipInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedRelationshipCreateRequest, SnomedRelationshipUpdateRequest, SnomedBrowserRelationship> {
	@Override
	public SnomedRelationshipCreateRequest createInput(String branchPath, SnomedBrowserRelationship relationship, InputFactory inputFactory) {
		final SnomedRelationshipCreateRequestBuilder builder = SnomedRequests
				.prepareNewRelationship()
				.setModuleId(getModuleOrDefault(relationship))
				.setTypeId(relationship.getType().getConceptId())
				.setCharacteristicType(relationship.getCharacteristicType())
				.setSourceId(relationship.getSourceId())
				.setDestinationId(relationship.getTarget().getConceptId())
				.setGroup(relationship.getGroupId())
				.setModifier(relationship.getModifier());
		
		if (relationship.getRelationshipId() != null) {
			builder.setId(relationship.getRelationshipId());
		} else {
			builder.setIdFromNamespace(getDefaultNamespace());
		}
		
		return (SnomedRelationshipCreateRequest) builder.build();
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
	public boolean canCreateUpdate(Class<? extends SnomedComponentUpdateRequest> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipUpdateRequest.class, updateType.getName());
	}
}
