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
package com.b2international.snowowl.snomed.core.rest.browser;

import com.b2international.snowowl.snomed.core.domain.CharacteristicType;
import com.b2international.snowowl.snomed.core.domain.RelationshipModifier;

/**
 * Represents a specific SNOMED CT relationship, carrying information for use in the IHTSDO SNOMED CT Browser.
 */
public interface ISnomedBrowserRelationship extends ISnomedBrowserComponentWithId {

	/** @return the relationship's unique component identifier */
	String getRelationshipId();

	/** @return details about the relationship's type concept */
	ISnomedBrowserRelationshipType getType();
	
	/** @return details about the relationship's target concept */
	ISnomedBrowserRelationshipTarget getTarget();

	/** @return the component identifier of the relationship's source concept */
	String getSourceId();
	
	/** @return the relationship group number, or 0 if this relationship is not part of a numbered group */
	int getGroupId();
	
	/** @return the relationship's characteristic type, eg. {@link CharacteristicType#STATED_RELATIONSHIP STATED_RELATIONSHIP} */
	CharacteristicType getCharacteristicType();
	
	/** @return the relationship modifier ({@link RelationshipModifier#EXISTENTIAL EXISTENTIAL} or {@link RelationshipModifier#UNIVERSAL UNIVERSAL}) */
	RelationshipModifier getModifier();
}
