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
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Iterator;
import java.util.Map;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.ISnomedConcept;
import com.b2international.snowowl.snomed.core.domain.SnomedConcepts;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMembers;
import com.b2international.snowowl.snomed.core.lang.LanguageSetting;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService;
import com.b2international.snowowl.snomed.datastore.services.ISnomedComponentService.IdStorageKeyPair;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders;
import com.b2international.snowowl.snomed.exporter.server.SnomedRf1Exporter;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Maps;

/**
 * RF1 exporter for SNOMED CT concepts.
 */
public class SnomedRf1ConceptExporter implements SnomedRf1Exporter {

	private final Id2Rf1PropertyMapper mapper;
	private final SnomedExportConfiguration configuration;
	private final Supplier<Iterator<String>> itrSupplier;

	private Map<String, ISnomedConcept> concepts;
	private Map<String, String> conceptToInactivationIndicatorMap;
	private Map<String, String> conceptIdToCtv3Map;
	private Map<String, String> conceptIdToSnomedRTMap;

	public SnomedRf1ConceptExporter(final SnomedExportConfiguration configuration, final Id2Rf1PropertyMapper mapper) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.mapper = checkNotNull(mapper, "mapper");
		itrSupplier = createSupplier();
		initMaps(configuration.getCurrentBranchPath().getPath());
	}

	private void initMaps(String branchPath) {
		
		IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
		
		concepts = SnomedRequests.prepareSearchConcept()
			.all()
			.setExpand("fsn()")
			.setLocales(ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference())
			.build(branchPath)
			.execute(bus)
			.then(new Function<SnomedConcepts, Map<String, ISnomedConcept>>() {
				@Override
				public Map<String, ISnomedConcept> apply(SnomedConcepts input) {
					return Maps.uniqueIndex(input, IComponent.ID_FUNCTION);
				}
			}).getSync();
		
		conceptToInactivationIndicatorMap = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSet(Concepts.REFSET_CONCEPT_INACTIVITY_INDICATOR)
			.build(branchPath)
			.execute(bus)
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
			.build(branchPath)
			.execute(bus)
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
			.build(branchPath)
			.execute(bus)
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
	}
	
	private Supplier<Iterator<String>> createSupplier() {
		
		return memoize(new Supplier<Iterator<String>>() {
			@Override
			public Iterator<String> get() {
				return new AbstractIterator<String>() {
					
					private final Iterator<IdStorageKeyPair> idIterator = ApplicationContext.getServiceForClass(ISnomedComponentService.class)
							.getAllComponentIdStorageKeys(configuration.getCurrentBranchPath(), CONCEPT_NUMBER).iterator();
					
					private Object[] _values;
					
					@Override
					protected String computeNext() {
						
						while (idIterator.hasNext()) {
							
							final String conceptId = idIterator.next().getId();
							_values = new Object[6];
							
							ISnomedConcept concept = concepts.get(conceptId);
							
							_values[0] = conceptId;
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
						
						return endOfData();
					}
					
				};
			}
		});
	}

	@Override
	public String getRelativeDirectory() {
		return RF1_CORE_RELATIVE_DIRECTORY;
	}

	@Override
	public String getFileName() {
		return SnomedRfFileNameBuilder.buildCoreRf1FileName(getType(), configuration);
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
	public boolean hasNext() {
		return itrSupplier.get().hasNext();
	}

	@Override
	public String next() {
		return itrSupplier.get().next();
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<String> iterator() {
		return itrSupplier.get();
	}

	@Override
	public void close() throws Exception {
		//intentionally ignored
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	/*returns with a number indicating the status of a concept for RF1 publication.*/
	private String getConceptStatus(final String stringValue) {
		return Preconditions.checkNotNull(mapper.getConceptStatusProperty(stringValue));
	}

}