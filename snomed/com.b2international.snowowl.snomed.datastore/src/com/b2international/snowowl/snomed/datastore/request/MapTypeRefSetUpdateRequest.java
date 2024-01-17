/*
 * Copyright 2020-2022 B2i Healthcare, https://b2ihealthcare.com
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
import java.util.Objects;
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
	
	@NotEmpty
	private final String mapSourceComponent;
	
	MapTypeRefSetUpdateRequest(String referenceSetId, String mapTargetComponent, String mapSourceComponent) {
		this.referenceSetId = referenceSetId;
		this.mapTargetComponent = mapTargetComponent;
		this.mapSourceComponent = mapSourceComponent;
	}
	
	@Override
	public Boolean execute(TransactionContext context) {
		// fail fast if refset identifier concept does not exist
		SnomedConceptDocument conceptDocument = context.lookup(referenceSetId, SnomedConceptDocument.class);
		
		SnomedRefSetType refSetType = conceptDocument.getRefSetType();
		if (!SnomedRefSetUtil.isMapping(refSetType)) {
			throw new BadRequestException("Map source or target component type can only be set for map-type reference sets, reference set: '%s' reference set type: '%s'." , referenceSetId, refSetType);
		}

		if (!TerminologyRegistry.UNKNOWN_COMPONENT_TYPE.equals(mapTargetComponent) && SnomedRefSetType.SIMPLE_MAP_TO.equals(refSetType)) {
			throw new BadRequestException("Map target component type can not be set for 'Simple map to SNOMED CT' reference sets, reference set: '%s' reference set type: '%s'." , referenceSetId, refSetType);
		}
		
		if (!TerminologyRegistry.UNKNOWN_COMPONENT_TYPE.equals(mapSourceComponent) && !SnomedRefSetType.SIMPLE_MAP_TO.equals(refSetType)) {
			throw new BadRequestException("Map source component type can only be set for 'Simple map to SNOMED CT' reference sets, reference set: '%s' reference set type: '%s'." , referenceSetId, refSetType);
		}
		
		Builder conceptBuilder = SnomedConceptDocument.builder(conceptDocument);
		boolean changed = false;
		
		String currentMapTargetComponent = conceptDocument.getMapTargetComponentType();
		if (!Objects.equals(currentMapTargetComponent, mapTargetComponent)) {
			conceptBuilder.mapTargetComponentType(mapTargetComponent);
			changed = true;
		}
		
		String currentMapSourceComponent = conceptDocument.getMapSourceComponentType();
		if (!Objects.equals(currentMapSourceComponent, mapSourceComponent)) {
			conceptBuilder.mapSourceComponentType(mapSourceComponent);
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
