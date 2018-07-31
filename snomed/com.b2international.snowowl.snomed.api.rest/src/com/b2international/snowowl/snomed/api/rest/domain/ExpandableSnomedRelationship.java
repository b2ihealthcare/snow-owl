/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Date;

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.fasterxml.jackson.annotation.JsonIgnore;
/**
 * @since 7.0
 */
public class ExpandableSnomedRelationship {
	
	private SnomedRelationship wrappedRelationship;
	private SnomedConceptMini source;
	private SnomedConceptMini type;
	
	public ExpandableSnomedRelationship(SnomedRelationship wrappedRelationship, String[] expand) {
		this.wrappedRelationship = wrappedRelationship;
		final SnomedConcept source = wrappedRelationship.getSource();
		for (String expandParam : expand) {
			if ("source.fsn".equals(expandParam)) {
				if (source.getFsn() != null) {
					setSource(new SnomedConceptMini(source.getId(), source.getFsn().getTerm()));
				} else {
					setSource(new SnomedConceptMini(source.getId()));
				}
			} else if ("type.fsn".equals(expandParam)) {
				final SnomedConcept type = wrappedRelationship.getType();
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
	
	public SnomedConcept getDestinationConcept() {
		return null;
	}
	
	public SnomedConcept getSourceConcept() {
		return null;
	}
	
	public SnomedConcept getTypeConcept() {
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
