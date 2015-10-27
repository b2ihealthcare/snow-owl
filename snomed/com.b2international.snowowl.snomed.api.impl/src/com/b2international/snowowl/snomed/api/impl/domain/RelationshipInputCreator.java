package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;
import com.b2international.snowowl.snomed.core.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedComponentCreateRequest;
import com.b2international.snowowl.snomed.datastore.server.events.SnomedRelationshipCreateRequest;

public class RelationshipInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedRelationshipCreateRequest, SnomedRelationshipUpdate, SnomedBrowserRelationship> {
	@Override
	public SnomedRelationshipCreateRequest createInput(String branchPath, SnomedBrowserRelationship relationship, InputFactory inputFactory) {
		final SnomedRelationshipCreateRequest relationshipInput = new SnomedRelationshipCreateRequest();
		setCommonComponentProperties(branchPath, relationship, relationshipInput, ComponentCategory.RELATIONSHIP);
		relationshipInput.setTypeId(relationship.getType().getConceptId());
		relationshipInput.setCharacteristicType(relationship.getCharacteristicType());
		relationshipInput.setSourceId(relationship.getSourceId());
		relationshipInput.setDestinationId(relationship.getTarget().getConceptId());
		relationshipInput.setGroup(relationship.getGroupId());
		relationshipInput.setModifier(relationship.getModifier());
		return relationshipInput;
	}

	@Override
	public SnomedRelationshipUpdate createUpdate(SnomedBrowserRelationship existingVersion, SnomedBrowserRelationship newVersion) {
		final SnomedRelationshipUpdate update = new SnomedRelationshipUpdate();
		boolean change = false;
		if (!existingVersion.getModuleId().equals(newVersion.getModuleId())) {
			change = true;
			update.setModuleId(newVersion.getModuleId());
		}
		if (existingVersion.isActive() != newVersion.isActive()) {
			change = true;
			update.setActive(newVersion.isActive());
		}
		return change ? update : null;
	}

	@Override
	public boolean canCreateInput(Class<? extends SnomedComponentCreateRequest> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipCreateRequest.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipUpdate.class, updateType.getName());
	}
}
