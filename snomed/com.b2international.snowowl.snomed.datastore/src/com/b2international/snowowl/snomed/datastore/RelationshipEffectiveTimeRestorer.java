/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore;

import com.b2international.snowowl.snomed.Component;
import com.b2international.snowowl.snomed.Relationship;
import com.b2international.snowowl.snomed.core.domain.SnomedComponent;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationship;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;

/**
 * @since 6.14
 */
public final class RelationshipEffectiveTimeRestorer extends ComponentEffectiveTimeRestorer {

	@Override
	protected boolean canRestoreComponentEffectiveTime(Component componentToRestore, SnomedComponent previousVersion) {
		final Relationship relationshipToRestore = (Relationship) componentToRestore;
		final SnomedRelationship previousRelationshipVersion = (SnomedRelationship) previousVersion;
		
		if (!relationshipToRestore.getSource().getId().equals(previousRelationshipVersion.getSourceId())) return false;
		if (!relationshipToRestore.getType().getId().equals(previousRelationshipVersion.getTypeId())) return false;
		if (!relationshipToRestore.getDestination().getId().equals(previousRelationshipVersion.getDestinationId())) return false;
		if (relationshipToRestore.getGroup() != previousRelationshipVersion.getGroup().intValue()) return false;
		if (relationshipToRestore.getUnionGroup() != previousRelationshipVersion.getUnionGroup().intValue()) return false;
		if (!relationshipToRestore.getCharacteristicType().getId().equals(previousRelationshipVersion.getCharacteristicType().getConceptId())) return false;
		if (!relationshipToRestore.getModifier().getId().equals(previousRelationshipVersion.getModifier().getConceptId())) return false;
		
		return true;
	}

	@Override
	protected SnomedComponent getVersionedComponent(String branch, String componentId) {
		return SnomedRequests.prepareGetRelationship(componentId)
				.build(SnomedDatastoreActivator.REPOSITORY_UUID, branch)
				.get();
	}

}
