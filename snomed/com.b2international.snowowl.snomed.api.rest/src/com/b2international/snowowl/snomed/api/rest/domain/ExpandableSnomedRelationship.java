package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Date;

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.RelationshipRefinability;

public class ExpandableSnomedRelationship implements ISnomedRelationship {
	
	private ISnomedRelationship wrappedRelationship;
	private SnomedConceptMini source;
	private SnomedConceptMini type;
	
	public ExpandableSnomedRelationship(ISnomedRelationship wrappedRelationship) {
		this.wrappedRelationship = wrappedRelationship;
	}
	
	public void setSource(SnomedConceptMini source) {
		this.source = source;
	}

	public SnomedConceptMini getSource() {
		return source;
	}

	public void setType(SnomedConceptMini type) {
		this.type = type;
	}

	public SnomedConceptMini getType() {
		return type;
	}

	@Override
	public Boolean isActive() {
		return wrappedRelationship.isActive();
	}

	@Override
	public Date getEffectiveTime() {
		return wrappedRelationship.getEffectiveTime();
	}

	@Override
	public String getModuleId() {
		return wrappedRelationship.getModuleId();
	}

	@Override
	public String getId() {
		return wrappedRelationship.getId();
	}

	@Override
	public Boolean isReleased() {
		return wrappedRelationship.isReleased();
	}

	@Override
	public String getSourceId() {
		return wrappedRelationship.getSourceId();
	}

	@Override
	public String getDestinationId() {
		return wrappedRelationship.getDestinationId();
	}

	@Override
	public boolean isDestinationNegated() {
		return wrappedRelationship.isDestinationNegated();
	}

	@Override
	public String getTypeId() {
		return wrappedRelationship.getTypeId();
	}

	@Override
	public int getGroup() {
		return wrappedRelationship.getGroup();
	}

	@Override
	public int getUnionGroup() {
		return wrappedRelationship.getUnionGroup();
	}

	@Override
	public CharacteristicType getCharacteristicType() {
		return wrappedRelationship.getCharacteristicType();
	}

	@Override
	public RelationshipRefinability getRefinability() {
		return wrappedRelationship.getRefinability();
	}

	@Override
	public RelationshipModifier getModifier() {
		return wrappedRelationship.getModifier();
	}

}
