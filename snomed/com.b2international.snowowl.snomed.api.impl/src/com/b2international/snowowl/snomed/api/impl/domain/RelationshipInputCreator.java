package com.b2international.snowowl.snomed.api.impl.domain;

import com.b2international.commons.ClassUtils;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentInput;
import com.b2international.snowowl.snomed.api.domain.ISnomedComponentUpdate;
import com.b2international.snowowl.snomed.api.impl.domain.browser.SnomedBrowserRelationship;

public class RelationshipInputCreator extends AbstractInputCreator implements ComponentInputCreator<SnomedRelationshipInput, SnomedRelationshipUpdate, SnomedBrowserRelationship> {
	@Override
	public SnomedRelationshipInput createInput(String branchPath, SnomedBrowserRelationship relationship, InputFactory inputFactory) {
		final SnomedRelationshipInput relationshipInput = new SnomedRelationshipInput();
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
		return change ? update : null;
	}

	@Override
	public boolean canCreateInput(Class<? extends ISnomedComponentInput> inputType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipInput.class, inputType.getName());
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return ClassUtils.isClassAssignableFrom(SnomedRelationshipUpdate.class, updateType.getName());
	}
}
