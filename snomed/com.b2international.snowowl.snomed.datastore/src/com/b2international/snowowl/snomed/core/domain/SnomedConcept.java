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
package com.b2international.snowowl.snomed.core.domain;

import com.google.common.collect.Multimap;

/**
 * Represents a SNOMED&nbsp;CT concept.
 * 
 */
public class SnomedConcept extends BaseSnomedComponent implements ISnomedConcept {

	private DefinitionStatus definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private InactivationIndicator inactivationIndicator;
	private Multimap<AssociationType, String> associationTargets;
	private ISnomedDescription fsn;
	private ISnomedDescription pt;
	private SnomedDescriptions descriptions;
	private SnomedConcepts ancestors;
	private SnomedConcepts descendants;

	@Override
	public DefinitionStatus getDefinitionStatus() {
		return definitionStatus;
	}

	@Override
	public SubclassDefinitionStatus getSubclassDefinitionStatus() {
		return subclassDefinitionStatus;
	}

	@Override
	public InactivationIndicator getInactivationIndicator() {
		return inactivationIndicator;
	}

	@Override
	public Multimap<AssociationType, String> getAssociationTargets() {
		return associationTargets;
	}
	
	@Override
	public SnomedDescriptions getDescriptions() {
		return descriptions;
	}

	@Override
	public ISnomedDescription getFsn() {
		return fsn;
	}
	
	@Override
	public ISnomedDescription getPt() {
		return pt;
	}
	
	@Override
	public SnomedConcepts getAncestors() {
		return ancestors;
	}
	
	@Override
	public SnomedConcepts getDescendants() {
		return descendants;
	}

	public void setDefinitionStatus(final DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public void setSubclassDefinitionStatus(final SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}

	public void setInactivationIndicator(final InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
	}

	public void setAssociationTargets(final Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
	}
	
	public void setDescriptions(SnomedDescriptions descriptions) {
		this.descriptions = descriptions;
	}
	
	public void setFsn(ISnomedDescription fsn) {
		this.fsn = fsn;
	}
	
	public void setPt(ISnomedDescription pt) {
		this.pt = pt;
	}
	
	public void setAncestors(SnomedConcepts ancestors) {
		this.ancestors = ancestors;
	}
	
	public void setDescendants(SnomedConcepts descendants) {
		this.descendants = descendants;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedConcept [isActive()=");
		builder.append(isActive());
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", isReleased()=");
		builder.append(isReleased());
		builder.append(", getDefinitionStatus()=");
		builder.append(getDefinitionStatus());
		builder.append(", getSubclassDefinitionStatus()=");
		builder.append(getSubclassDefinitionStatus());
		builder.append(", getInactivationIndicator()=");
		builder.append(getInactivationIndicator());
		builder.append(", getAssociationTargets()=");
		builder.append(getAssociationTargets());
		builder.append("]");
		return builder.toString();
	}
}