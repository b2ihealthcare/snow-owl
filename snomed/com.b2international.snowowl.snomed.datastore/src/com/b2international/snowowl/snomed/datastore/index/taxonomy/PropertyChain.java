/*
 * Copyright 2017 International Health Terminology Standards Development Organisation
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
package com.b2international.snowowl.snomed.datastore.index.taxonomy;

import java.util.Objects;

/**
 * Representation for an OWL property chain.
 *
 * In example property chain: "has_active_ingredient o is_modification_of -> has_active_ingredient"
 * The naming within this class is: "sourceType o destinationType -> inferredType"
 */
public class PropertyChain {

	private long sourceType;
	private long destinationType;
	private long inferredType;

	public PropertyChain(long sourceType, long destinationType, long inferredType) {
		this.sourceType = sourceType;
		this.destinationType = destinationType;
		this.inferredType = inferredType;
	}

	public long getSourceType() {
		return sourceType;
	}

	public long getDestinationType() {
		return destinationType;
	}

	public long getInferredType() {
		return inferredType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sourceType, destinationType, inferredType);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		
		PropertyChain other = (PropertyChain) obj;
		if (destinationType != other.destinationType) { return false; }
		if (inferredType != other.inferredType) { return false; }
		if (sourceType != other.sourceType) { return false; }
		
		return true;
	}
}
