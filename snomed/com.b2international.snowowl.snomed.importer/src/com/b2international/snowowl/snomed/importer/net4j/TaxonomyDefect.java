/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.net4j;

import java.io.Serializable;

/**
 * @since 5.4
 */
public final class TaxonomyDefect implements Serializable {

	public enum Type {
		MISSING_SOURCE,
		MISSING_DESTINATION
	} 
	
	private final long relationshipId;
	private final String effectiveTime;
	private final Type type;
	private final long missingConceptId;

	public TaxonomyDefect(long relationshipId, String effectiveTime, Type type, final long missingConceptId) {
		this.relationshipId = relationshipId;
		this.effectiveTime = effectiveTime;
		this.type = type;
		this.missingConceptId = missingConceptId;
	}
	
	public String getEffectiveTime() {
		return effectiveTime;
	}
	
	public long getRelationshipId() {
		return relationshipId;
	}
	
	public Type getType() {
		return type;
	}
	
	public long getMissingConceptId() {
		return missingConceptId;
	}
	
}
