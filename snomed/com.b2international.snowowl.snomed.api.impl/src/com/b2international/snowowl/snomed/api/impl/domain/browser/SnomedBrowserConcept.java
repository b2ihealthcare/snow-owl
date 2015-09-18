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
package com.b2international.snowowl.snomed.api.impl.domain.browser;

import com.b2international.snowowl.snomed.api.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserConcept;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserDescription;
import com.b2international.snowowl.snomed.api.domain.browser.ISnomedBrowserRelationship;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class SnomedBrowserConcept extends SnomedBrowserComponent implements ISnomedBrowserConcept {

	private String conceptId;
	private String fsn;
	private DefinitionStatus definitionStatus;
	private String preferredSynonym;
	private boolean leafInferred;
	
	@JsonDeserialize(contentAs=SnomedBrowserDescription.class)
	private List<ISnomedBrowserDescription> descriptions = ImmutableList.of();

	@JsonDeserialize(contentAs=SnomedBrowserRelationship.class)
	private List<ISnomedBrowserRelationship> relationships = ImmutableList.of();

	@Override
	public String getId() {
		return conceptId;
	}

	@Override
	public String getConceptId() {
		return conceptId;
	}

	@Override
	public String getFsn() {
		return fsn;
	}

	@Override
	public DefinitionStatus getDefinitionStatus() {
		return definitionStatus;
	}

	@Override
	public String getPreferredSynonym() {
		return preferredSynonym;
	}

	@Override
	public boolean getIsLeafInferred() {
		return leafInferred;
	}

	@Override
	public List<ISnomedBrowserDescription> getDescriptions() {
		return descriptions;
	}

	@Override
	public List<ISnomedBrowserRelationship> getRelationships() {
		return relationships;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	public void setFsn(final String fsn) {
		this.fsn = fsn;
	}

	public void setDefinitionStatus(final DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	public void setPreferredSynonym(final String preferredSynonym) {
		this.preferredSynonym = preferredSynonym;
	}

	public void setIsLeafInferred(final boolean leafInferred) {
		this.leafInferred = leafInferred;
	}

	public void setDescriptions(final List<ISnomedBrowserDescription> descriptions) {
		this.descriptions = descriptions;
	}

	public void setRelationships(final List<ISnomedBrowserRelationship> relationships) {
		this.relationships = relationships;
	}

//	@Override
//	public SnomedConceptInput toComponentInput(final String branchPath) {
//		final SnomedConceptInput result = super.toComponentInput(branchPath);
//
//		result.setIsAIdGenerationStrategy(createIdGenerationStrategy(getIsAId()));
//
//		final List<SnomedDescriptionInput> descriptionInputs = newArrayList();
//		for (SnomedDescriptionRestInput restDescription : getDescriptions()) {
//			// Propagate namespace from concept if present, and the description does not already have one
//			if (null == restDescription.getNamespaceId()) {
//				restDescription.setNamespaceId(getNamespaceId());
//			}
//			
//			descriptionInputs.add(restDescription.toComponentInput(branchPath));
//		}
//
//		result.setDescriptions(descriptionInputs);
//		result.setParentId(getParentId());
//
//		return result;
//	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedBrowserConcept [conceptId=");
		builder.append(conceptId);
		builder.append(", fsn=");
		builder.append(fsn);
		builder.append(", definitionStatus=");
		builder.append(definitionStatus);
		builder.append(", preferredSynonym=");
		builder.append(preferredSynonym);
		builder.append(", leafInferred=");
		builder.append(leafInferred);
		builder.append(", descriptions=");
		builder.append(descriptions);
		builder.append(", relationships=");
		builder.append(relationships);
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append("]");
		return builder.toString();
	}
}
