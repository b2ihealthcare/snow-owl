/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.mrcm.core.widget;

import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * Internal representation of a relationship for use with {@link WidgetBeanProvider}.
 * 
 */
public final class SnomedRelationship {
	
	public enum ActivePredicate implements Predicate<SnomedRelationship> {
		INSTANCE;

		@Override public boolean apply(SnomedRelationship relationship) {
			return relationship.isActive();
		}
	}

	public enum CDOObjectConverterFunction implements Function<Relationship, SnomedRelationship> { 
		INSTANCE;	
		
		@Override public SnomedRelationship apply(Relationship relationship) {
			return new SnomedRelationship(relationship);
		}
	}

	public enum IndexObjectConverterFunction implements Function<SnomedRelationshipIndexEntry, SnomedRelationship> {
		INSTANCE;
		
		@Override public SnomedRelationship apply(SnomedRelationshipIndexEntry relationship) {
			return new SnomedRelationship(relationship);
		}
	}

	private final String id;
	private final String typeId;
	private final String characteristicTypeId;
	private final String destinationId;
	private final int group;
	private final boolean active;
	private final boolean released;
	private final boolean universalRestriction;
	
	public SnomedRelationship(final Relationship relationship) {
		id = relationship.getId();
		typeId = relationship.getType().getId();
		characteristicTypeId = relationship.getCharacteristicType().getId();
		destinationId = relationship.getDestination().getId();
		group = relationship.getGroup();
		active = relationship.isActive();
		released = relationship.isReleased();
		universalRestriction = Concepts.UNIVERSAL_RESTRICTION_MODIFIER.equals(relationship.getModifier().getId());
	}
	
	public SnomedRelationship(final SnomedRelationshipIndexEntry relationship) {
		id = relationship.getId();
		typeId = relationship.getAttributeId();
		characteristicTypeId = relationship.getCharacteristicTypeId();
		destinationId = relationship.getValueId();
		group = relationship.getGroup();
		active = relationship.isActive();
		released = relationship.isReleased();
		universalRestriction = relationship.isUniversal();
	}

	public String getId() {
		return id;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getCharacteristicTypeId() {
		return characteristicTypeId;
	}

	public String getDestinationId() {
		return destinationId;
	}

	public int getGroup() {
		return group;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isReleased() {
		return released;
	}
	
	public boolean isUniversalRestriction() {
		return universalRestriction;
	} 
	
}