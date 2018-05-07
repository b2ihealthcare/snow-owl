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
package com.b2international.snowowl.snomed.datastore.derivation;

import java.io.Serializable;

/**
 * Model class for the simple type and simple map type reference set derivation.
 * 
 * @since Snow&nbsp;Owl 3.0.1
 */
public class SnomedRefSetDerivationModel implements Serializable {

	private static final long serialVersionUID = 1006183800987095659L;

	/**
	 * Enum for simple type refset derivation.
	 * Description: the descriptions of the refset members.
	 * Relationship: the relationships between the refset members.
	 * DUO: both description and relationship derivation type.
	 */
	public enum SimpleTypeDerivation {
		DESCRIPTION, RELATIONSHIP, DUO
	}

	/**
	 * Enum for simple map type refset derivation.
	 * Concept: the concepts of the refset members.
	 * Concept with subtypes: the concepts and all of their subtypes.
	 * Trio: concepts, descriptions, relationships.
	 */
	public enum SimpleMapTypeDerivation {
		CONCEPT, CONCEPT_WITH_SUBTYPES, TRIO
	}

	private final String refSetId;
	private final String refSetName;
	private final String commitMessage;
	private final String userId;
	private final boolean mapTarget;
	private final SimpleTypeDerivation simpleTypeDerivation;
	private final SimpleMapTypeDerivation simpleMapTypeDerivation;
	private final String languageReferenceSetId;

	/**
	 * 
	 * @param refSetId the ID of the reference set from which the members are derived.
	 * @param refSetName the name of the new reference set.
	 * @param commitMessage the message of the commit.
	 * @param userId the ID of the user.
	 * @param simpleTypeDerivation the derivation type for simple type derivation.
	 */
	public SnomedRefSetDerivationModel(final String refSetId, 
			final String refSetName, 
			final String commitMessage, 
			final String userId, 
			final SimpleTypeDerivation simpleTypeDerivation,
			final String languageReferenceSetId) {
		this.refSetId = refSetId;
		this.refSetName = refSetName;
		this.commitMessage = commitMessage;
		this.userId = userId;
		this.simpleTypeDerivation = simpleTypeDerivation;
		this.simpleMapTypeDerivation = null;
		this.languageReferenceSetId = languageReferenceSetId;
		this.mapTarget = false;
	}

	/**
	 * 
	 * @param refSetId the ID of the reference set from which the members are derived.
	 * @param refSetName the name of the new reference set.
	 * @param commitMessage the message of the commit.
	 * @param userId the ID of the user.
	 * @param mapTarget <code>true</code> if the target of the map should be derived.
	 * @param simpleMapTypeDerivation the derivation type for simple map derivation.
	 */
	public SnomedRefSetDerivationModel(final String refSetId, 
			final String refSetName, 
			final String commitMessage, 
			final String userId, 
			final SimpleMapTypeDerivation simpleMapTypeDerivation,
			final String languageReferenceSetId,
			final boolean mapTarget) {
		this.refSetId = refSetId;
		this.refSetName = refSetName;
		this.commitMessage = commitMessage;
		this.userId = userId;
		this.mapTarget = mapTarget;
		this.simpleMapTypeDerivation = simpleMapTypeDerivation;
		this.simpleTypeDerivation = null;
		this.languageReferenceSetId = languageReferenceSetId;
	}
	
	public String getLanguageReferenceSetId() {
		return languageReferenceSetId;
	}

	public String getRefSetId() {
		return refSetId;
	}

	public String getRefSetName() {
		return refSetName;
	}

	public String getCommitMessage() {
		return commitMessage;
	}

	public String getUserId() {
		return userId;
	}

	public boolean isMapTarget() {
		return mapTarget;
	}

	public SimpleTypeDerivation getSimpleTypeDerivation() {
		return simpleTypeDerivation;
	}

	public SimpleMapTypeDerivation getSimpleMapTypeDerivation() {
		return simpleMapTypeDerivation;
	}
}