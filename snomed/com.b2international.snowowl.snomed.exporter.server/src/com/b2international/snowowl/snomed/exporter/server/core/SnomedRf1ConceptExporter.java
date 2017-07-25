/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.exporter.server.core;

import static com.b2international.commons.StringUtils.valueOfOrEmptyString;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.6.12
 */
public class SnomedRf1ConceptExporter extends SnomedPageableRf1Exporter<SnomedConcepts, ISnomedConcept> {

	private Map<String, String> conceptToInactivationIndicatorMap;
	private Map<String, String> conceptIdToCtv3Map;
	private Map<String, String> conceptIdToSnomedRTMap;
	
	public SnomedRf1ConceptExporter(SnomedExportConfiguration configuration, Id2Rf1PropertyMapper mapper) {
		super(configuration, mapper);
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.CONCEPT;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_CONCEPT_HEADER;
	}

	@Override
	protected SnomedConcepts executeQuery(int offset) {
		
		SnomedConcepts concepts = SnomedRequests.prepareSearchConcept()
			.setOffset(offset)
			.setLimit(DEFAULT_LIMIT)
			.setExpand("fsn()")
			.setLocales(getLocales())
			.build(getBranch())
			.executeSync(getBus());
		
		Set<String> conceptIds = FluentIterable.from(concepts.getItems()).transform(IComponent.ID_FUNCTION).toSet();
		
		conceptToInactivationIndicatorMap = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSet(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR)
			.filterByReferencedComponent(conceptIds)
			.build(getBranch())
			.execute(getBus())
			.then(new Function<SnomedReferenceSetMembers, Map<String, String>>() {
				@Override
				public Map<String, String> apply(SnomedReferenceSetMembers input) {
					Map<String, String> conceptIdToValueIdMap = newHashMap();
					for (SnomedReferenceSetMember member : input) {
						conceptIdToValueIdMap.put(member.getReferencedComponent().getId(),
								(String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
					}
					return conceptIdToValueIdMap;
				}
			}).getSync();
			
		conceptIdToCtv3Map = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSet(Concepts.CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID)
			.filterByReferencedComponent(conceptIds)
			.build(getBranch())
			.execute(getBus())
			.then(new Function<SnomedReferenceSetMembers, Map<String, String>>() {
				@Override
				public Map<String, String> apply(SnomedReferenceSetMembers input) {
					Map<String, String> conceptIdToMapTargetMap = newHashMap();
					for (SnomedReferenceSetMember member : input) {
						conceptIdToMapTargetMap.put(member.getReferencedComponent().getId(),
								(String) member.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET));
					}
					return conceptIdToMapTargetMap;
				}
			}).getSync();
			
		conceptIdToSnomedRTMap = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSet(Concepts.SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID)
			.filterByReferencedComponent(conceptIds)
			.build(getBranch())
			.execute(getBus())
			.then(new Function<SnomedReferenceSetMembers, Map<String, String>>() {
				@Override
				public Map<String, String> apply(SnomedReferenceSetMembers input) {
					Map<String, String> conceptIdToMapTargetMap = newHashMap();
					for (SnomedReferenceSetMember member : input) {
						conceptIdToMapTargetMap.put(member.getReferencedComponent().getId(),
								(String) member.getProperties().get(SnomedRf2Headers.FIELD_MAP_TARGET));
					}
					return conceptIdToMapTargetMap;
				}
			}).getSync();
		
		return concepts;
	}

	@Override
	protected String convertToString(ISnomedConcept concept) {
		
		Object[] _values = new Object[6];
		
		_values[0] = concept.getId();
		_values[1] = BooleanUtils.toString(!concept.isActive()); // inverse RF1 status conversion
		
		if (!concept.isActive() && conceptToInactivationIndicatorMap.containsKey(concept.getId())) {
			_values[1] = conceptToInactivationIndicatorMap.get(concept.getId());
		}
		
		_values[2] = concept.getFsn().getTerm();
		
		if (conceptIdToCtv3Map.containsKey(concept.getId())) {
			_values[3] = conceptIdToCtv3Map.get(concept.getId());
		}
		
		if (conceptIdToSnomedRTMap.containsKey(concept.getId())) {
			_values[4] = conceptIdToSnomedRTMap.get(concept.getId());
		}
		
		_values[5] = BooleanUtils.toString(concept.getDefinitionStatus().isPrimitive());
		
		return new StringBuilder(valueOfOrEmptyString(_values[0])) //ID
			.append(HT)
			.append(getConceptStatus(valueOfOrEmptyString(_values[1]))) //status
			.append(HT)
			.append(valueOfOrEmptyString(_values[2])) //FSN
			.append(HT)
			.append(valueOfOrEmptyString(_values[3])) //CTV3
			.append(HT)
			.append(valueOfOrEmptyString(_values[4])) //SNOMEDRT
			.append(HT)
			.append(valueOfOrEmptyString(_values[5])) //definition status
			.toString();
	}
	
	/*returns with a number indicating the status of a concept for RF1 publication.*/
	private String getConceptStatus(final String stringValue) {
		return Preconditions.checkNotNull(getMapper().getConceptStatusProperty(stringValue));
	}

	@Override
	protected SnomedConcepts initItems() {
		return new SnomedConcepts(0, 0, -1);
	}
	
}
