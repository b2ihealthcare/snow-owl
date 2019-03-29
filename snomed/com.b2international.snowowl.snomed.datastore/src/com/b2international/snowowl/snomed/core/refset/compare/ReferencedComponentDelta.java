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
package com.b2international.snowowl.snomed.core.refset.compare;

import com.google.common.base.MoreObjects;

/**
 * Represents a reference set member delta.
 * 
 */
public final class ReferencedComponentDelta {
	private final String referencedComponentId;
	private final String referencedComponentLabel;
	private final String referencedComponentIconId;
	private final DeltaKind deltaKind;
	private String relatedToId;
	private String relatedToLabel;
	private String relatedToIconId;
	
	/**
	 * Class constructor.
	 * 
	 * @param referencedComponentId the referenced component ID
	 * @param referencedComponentLabel the referenced component label
	 * @param referencedComponentIconId the referenced component icon ID
	 * @param deltaKind the delta kind
	 */
	public ReferencedComponentDelta(String referencedComponentId, String referencedComponentLabel, String referencedComponentIconId, DeltaKind deltaKind) {
		this.referencedComponentId = referencedComponentId;
		this.referencedComponentLabel = referencedComponentLabel;
		this.referencedComponentIconId = referencedComponentIconId;
		this.deltaKind = deltaKind;
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param referencedComponentId the referenced component ID
	 * @param referencedComponentLabel the referenced component label
	 * @param referencedComponentIconId the referenced component icon ID
	 * @param deltaKind the delta kind
	 * @param relatedToId the related component ID
	 * @param relatedToLabel the related component label
	 * @param relatedToIconId the related component icon ID
	 */
	public ReferencedComponentDelta(String referencedComponentId, String referencedComponentLabel, String referencedComponentIconId, 
			DeltaKind deltaKind, String relatedToId, String relatedToLabel, String relatedToIconId) {
		this(referencedComponentId, referencedComponentLabel, referencedComponentIconId, deltaKind);
		this.relatedToId = relatedToId;
		this.relatedToLabel = relatedToLabel;
		this.relatedToIconId = relatedToIconId;
	}

	public DeltaKind getDeltaKind() {
		return deltaKind;
	}
	
	public String getReferencedComponent() {
		return referencedComponentId;
	}
	
	public String getReferencedComponentLabel() {
		return referencedComponentLabel;
	}
	
	public String getReferencedComponentIconId() {
		return referencedComponentIconId;
	}
	
	public String getRelatedTo() {
		return relatedToId;
	}
	
	public String getRelatedToLabel() {
		return relatedToLabel;
	}
	
	public String getRelatedToIconId() {
		return relatedToIconId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((deltaKind == null) ? 0 : deltaKind.hashCode());
		result = prime
				* result
				+ ((referencedComponentId == null) ? 0 : referencedComponentId
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReferencedComponentDelta other = (ReferencedComponentDelta) obj;
		if (deltaKind != other.deltaKind)
			return false;
		if (referencedComponentId == null) {
			if (other.referencedComponentId != null)
				return false;
		} else if (!referencedComponentId.equals(other.referencedComponentId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("refComponent", referencedComponentId)
				.add("deltaKind", deltaKind).toString();
	}
	
	/**
	 * Enumeration of referenced component delta kinds.
	 * 
	 */
	public static enum DeltaKind {
		SUBSUMED,
		ADDED,
		RELATED
	}
}