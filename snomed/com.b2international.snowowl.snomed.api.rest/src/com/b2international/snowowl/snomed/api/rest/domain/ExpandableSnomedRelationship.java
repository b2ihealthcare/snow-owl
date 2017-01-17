package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Date;

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ExpandableSnomedRelationship {
	
	private SnomedRelationship wrappedRelationship;
	private SnomedConceptMini source;
	private SnomedConceptMini type;
	
	public ExpandableSnomedRelationship(SnomedRelationship wrappedRelationship, String[] expand) {
		this.wrappedRelationship = wrappedRelationship;
		final ISnomedConcept source = wrappedRelationship.getSourceConcept();
		for (String expandParam : expand) {
			if ("source.fsn".equals(expandParam)) {
				if (source.getFsn() != null) {
					setSource(new SnomedConceptMini(source.getId(), source.getFsn().getTerm()));
				} else {
					setSource(new SnomedConceptMini(source.getId()));
				}
			} else if ("type.fsn".equals(expandParam)) {
				final ISnomedConcept type = wrappedRelationship.getTypeConcept();
				if (type.getFsn() != null) {
					setType(new SnomedConceptMini(type.getId(), type.getFsn().getTerm()));
				} else {
					setType(new SnomedConceptMini(type.getId()));
				}
			}
		}
	}
	
	@JsonIgnore
	public long getStorageKey() {
		return -1;
	}
	
	public SnomedReferenceSetMembers getMembers() {
		return null;
	}
	
	public ISnomedConcept getDestinationConcept() {
		return null;
	}
	
	public ISnomedConcept getSourceConcept() {
		return null;
	}
	
	public ISnomedConcept getTypeConcept() {
		return null;
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

	public Boolean isActive() {
		return wrappedRelationship.isActive();
	}

	public Date getEffectiveTime() {
		return wrappedRelationship.getEffectiveTime();
	}

	public String getModuleId() {
		return wrappedRelationship.getModuleId();
	}

	public String getId() {
		return wrappedRelationship.getId();
	}

	public Boolean isReleased() {
		return wrappedRelationship.isReleased();
	}

	public String getSourceId() {
		return wrappedRelationship.getSourceId();
	}

	public String getDestinationId() {
		return wrappedRelationship.getDestinationId();
	}

	public boolean isDestinationNegated() {
		return wrappedRelationship.isDestinationNegated();
	}

	public String getTypeId() {
		return wrappedRelationship.getTypeId();
	}

	public Integer getGroup() {
		return wrappedRelationship.getGroup();
	}

	public Integer getUnionGroup() {
		return wrappedRelationship.getUnionGroup();
	}

	public CharacteristicType getCharacteristicType() {
		return wrappedRelationship.getCharacteristicType();
	}

	public RelationshipModifier getModifier() {
		return wrappedRelationship.getModifier();
	}
	
	public String getIconId() {
		return wrappedRelationship.getIconId();
	}
	
	public Float getScore() {
		return wrappedRelationship.getScore();
	}
	
}
