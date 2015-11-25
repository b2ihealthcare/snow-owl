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
package com.b2international.snowowl.snomed.datastore.server.request;

import com.b2international.snowowl.snomed.core.domain.AssociationType;
import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;
import com.b2international.snowowl.snomed.core.domain.InactivationIndicator;
import com.b2international.snowowl.snomed.core.domain.SubclassDefinitionStatus;
import com.google.common.collect.Multimap;

/**
 * @since 4.5
 */
public class SnomedConceptUpdateRequestBuilder extends BaseSnomedComponentUpdateRequestBuilder<SnomedConceptUpdateRequestBuilder, SnomedConceptUpdateRequest> {

	private Multimap<AssociationType, String> associationTargets;
	private DefinitionStatus definitionStatus;
	private InactivationIndicator inactivationIndicator;
	private SubclassDefinitionStatus subclassDefinitionStatus;

	SnomedConceptUpdateRequestBuilder(String repositoryId, String componentId) {
		super(repositoryId, componentId);
	}
	
	@Override
	protected SnomedConceptUpdateRequest create(String componentId) {
		return new SnomedConceptUpdateRequest(componentId);
	}
	
	public SnomedConceptUpdateRequestBuilder setAssociationTargets(Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
		return this;
	}
	
	public SnomedConceptUpdateRequestBuilder setDefinitionStatus(DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
		return this;
	}
	
	public SnomedConceptUpdateRequestBuilder setInactivationIndicator(InactivationIndicator inactivationIndicator) {
		this.inactivationIndicator = inactivationIndicator;
		return this;
	}
	
	public SnomedConceptUpdateRequestBuilder setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
		return this;
	}
	
	@Override
	protected void init(SnomedConceptUpdateRequest req) {
		super.init(req);
		req.setAssociationTargets(associationTargets);
		req.setDefinitionStatus(definitionStatus);
		req.setInactivationIndicator(inactivationIndicator);
		req.setSubclassDefinitionStatus(subclassDefinitionStatus);
	}
	
	@Override
	protected SnomedConceptUpdateRequestBuilder getSelf() {
		return this;
	}

}
