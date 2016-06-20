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
package com.b2international.snowowl.datastore.server.snomed.merge;

/**
 * @since 4.7
 */
public class SnomedRelationshipMergeConflict extends SnomedMergeConflict {

	private static final long serialVersionUID = 2809107730087092007L;
	private final String typeId;
	private final String sourceId;
	private final String destinationId;

	public SnomedRelationshipMergeConflict(final String relationshipId, final String sourceId, final String typeId, final String destinationId, final String message) {
		super(relationshipId, message);
		this.typeId = typeId;
		this.sourceId = sourceId;
		this.destinationId = destinationId;
	}
	
	/**
	 * @return the typeId
	 */
	public String getTypeId() {
		return typeId;
	}
	
	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}
	
	/**
	 * @return the destinationId
	 */
	public String getDestinationId() {
		return destinationId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((destinationId == null) ? 0 : destinationId.hashCode());
		result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
		result = prime * result + ((typeId == null) ? 0 : typeId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SnomedRelationshipMergeConflict other = (SnomedRelationshipMergeConflict) obj;
		if (destinationId == null) {
			if (other.destinationId != null) {
				return false;
			}
		} else if (!destinationId.equals(other.destinationId)) {
			return false;
		}
		if (sourceId == null) {
			if (other.sourceId != null) {
				return false;
			}
		} else if (!sourceId.equals(other.sourceId)) {
			return false;
		}
		if (typeId == null) {
			if (other.typeId != null) {
				return false;
			}
		} else if (!typeId.equals(other.typeId)) {
			return false;
		}
		return true;
	}
}
