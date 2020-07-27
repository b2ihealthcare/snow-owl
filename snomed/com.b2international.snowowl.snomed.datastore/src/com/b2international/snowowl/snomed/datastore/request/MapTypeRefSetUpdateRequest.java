/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import java.util.Collections;
import java.util.Set;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.commons.exceptions.BadRequestException;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedRefSetType;
import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument.Builder;

/**
 * @since 7.8
 */
public final class MapTypeRefSetUpdateRequest implements SnomedComponentRequest<Boolean> {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private final String referenceSetId;
	
	@NotEmpty
	private final String mapTargetComponent;
	
	MapTypeRefSetUpdateRequest(String referenceSetId, String mapTargetComponent) {
		this.referenceSetId = referenceSetId;
		this.mapTargetComponent = mapTargetComponent;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		
		// fail fast if map target component does not exist
		short terminologyComponentShortId = context.service(TerminologyRegistry.class).getTerminologyComponentById(mapTargetComponent).shortId();
		
		// fail fast if refset identifier concept does not exist
		SnomedConceptDocument conceptDocument = context.lookup(referenceSetId, SnomedConceptDocument.class);
		
		SnomedRefSetType refSetType = conceptDocument.getRefSetType();
		if (!SnomedRefSetUtil.isMapping(refSetType)) {
			throw new BadRequestException("Map target codesystem can be set only for map-type reference sets, reference set: '%s' reference set type: '%s'." , referenceSetId, refSetType);
		}
		
		Builder conceptBuilder = SnomedConceptDocument.builder(conceptDocument);
		
		boolean changed = false;
		
		if (conceptDocument.getMapTargetComponentType() != null) {
			
			String currentMapTargetComponent = context.service(TerminologyRegistry.class).getTerminologyComponentByShortId(conceptDocument.getMapTargetComponentType()).id();
			
			if (!mapTargetComponent.equals(currentMapTargetComponent)) {
				conceptBuilder.mapTargetComponentType(terminologyComponentShortId).build();
				changed = true;
			}
			
		} else {
			
			conceptBuilder.mapTargetComponentType(terminologyComponentShortId).build();
			changed = true;
			
		}
		
		
		if (changed) {
			context.update(conceptDocument, conceptBuilder.build());
		}
		
		return changed;
		
	}
	
	@Override
	public Set<String> getRequiredComponentIds(TransactionContext context) {
		return Collections.emptySet();
	}

}
