package com.b2international.snowowl.snomed.api.impl.domain;

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
		update.setModuleId(newVersion.getModuleId());
		update.setActive(newVersion.isActive());
		return update;
	}

	@Override
	public boolean canCreateInput(Class<? extends ISnomedComponentInput> inputType) {
		return SnomedRelationshipInput.class.isAssignableFrom(inputType);
	}

	@Override
	public boolean canCreateUpdate(Class<? extends ISnomedComponentUpdate> updateType) {
		return SnomedRelationshipUpdate.class.isAssignableFrom(updateType);
	}
}
