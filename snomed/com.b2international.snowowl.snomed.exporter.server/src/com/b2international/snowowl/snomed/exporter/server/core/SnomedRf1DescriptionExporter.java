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
import static com.b2international.snowowl.core.ApplicationContext.getServiceForClass;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Suppliers.memoize;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.b2international.commons.BooleanUtils;
import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.ApplicationContext;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.eventbus.IEventBus;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
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
 * RF1 exporter for SNOMED CT descriptions.
 */
public class SnomedRf1DescriptionExporter implements SnomedRf1Exporter {

	private final Id2Rf1PropertyMapper mapper;
	private final SnomedExportConfiguration configuration;
	private final boolean includeExtendedDescriptionTypes;
	private final Supplier<Iterator<String>> itrSupplier;
	private String preferredLanguageRefsetId;

	private Map<String, ISnomedDescription> descriptions;
	private Map<String, String> descriptionToInactivationIndicatorMap;

	public SnomedRf1DescriptionExporter(final SnomedExportConfiguration configuration, final Id2Rf1PropertyMapper mapper, final boolean includeExtendedDescriptionTypes) {
		this.configuration = checkNotNull(configuration, "configuration");
		this.mapper = checkNotNull(mapper, "mapper");
		this.includeExtendedDescriptionTypes = includeExtendedDescriptionTypes;
		List<ExtendedLocale> extendedLocales = ApplicationContext.getServiceForClass(LanguageSetting.class).getLanguagePreference();
		preferredLanguageRefsetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(extendedLocales.get(0).getLanguageTag());
		itrSupplier = createSupplier();
		initMaps(configuration.getCurrentBranchPath().getPath());
	}
	
	private void initMaps(String branchPath) {
		
		IEventBus bus = ApplicationContext.getServiceForClass(IEventBus.class);
		
		descriptions = SnomedRequests.prepareSearchDescription()
			.all()
			.build(branchPath)
			.execute(bus)
			.then(new Function<SnomedDescriptions, Map<String, ISnomedDescription>>() {
				@Override
				public Map<String, ISnomedDescription> apply(SnomedDescriptions input) {
					return Maps.uniqueIndex(input, IComponent.ID_FUNCTION);
				}
			}).getSync();
		
		descriptionToInactivationIndicatorMap = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSet(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
			.build(branchPath)
			.execute(bus)
			.then(new Function<SnomedReferenceSetMembers, Map<String, String>>() {
				@Override
				public Map<String, String> apply(SnomedReferenceSetMembers input) {
					Map<String, String> descriptionIdToValueIdMap = newHashMap();
					for (SnomedReferenceSetMember member : input) {
						descriptionIdToValueIdMap.put(member.getReferencedComponent().getId(),
								(String) member.getProperties().get(SnomedRf2Headers.FIELD_VALUE_ID));
					}
					return descriptionIdToValueIdMap;
				}
			}).getSync();
		
	}

	private Supplier<Iterator<String>> createSupplier() {
		return memoize(new Supplier<Iterator<String>>() {
			@Override
			public Iterator<String> get() {
				return new AbstractIterator<String>() {
					
					private final Iterator<IdStorageKeyPair> idIterator = getServiceForClass(ISnomedComponentService.class)
							.getAllComponentIdStorageKeys(configuration.getCurrentBranchPath(), DESCRIPTION_NUMBER).iterator();
					
					private Object[] _values;
					
					@Override
					protected String computeNext() {
						
						while (idIterator.hasNext()) {
							
							final String descriptionId = idIterator.next().getId();
							
							_values = new Object[7];
							
							ISnomedDescription description = descriptions.get(descriptionId);
							
							_values[0] = descriptionId;
							_values[1] = BooleanUtils.toString(!description.isActive()); // inverse RF1 status conversion
							
							if (!description.isActive() && descriptionToInactivationIndicatorMap.containsKey(description.getId())) {
								_values[1] = descriptionToInactivationIndicatorMap.get(description.getId());
							}
							
							_values[2] = description.getConceptId();
							_values[3] = description.getTerm();
							_values[4] = checkNotNull(mapper.getInitialCapitalStatus(description.getCaseSignificance().getConceptId()));
							
							boolean preferred = false;
							
							if (description.getAcceptabilityMap().containsKey(preferredLanguageRefsetId)) {
								preferred = description.getAcceptabilityMap().get(preferredLanguageRefsetId) == Acceptability.PREFERRED;
							}
							
							String type;
							
							if (includeExtendedDescriptionTypes) {
								
								String extendedType = mapper.getExtendedDescriptionType(description.getTypeId());
								
								if (!description.getTypeId().equals(Concepts.FULLY_SPECIFIED_NAME) && preferred) {
									type = "1"; // preferred term
								} else if (extendedType != null) {
									type = extendedType;
								} else {
									type = "0";
								}
								
							} else {
								
								String simpleType = mapper.getDescriptionType(description.getTypeId());
								
								if (!description.getTypeId().equals(Concepts.FULLY_SPECIFIED_NAME) && preferred) {
									type = "1"; // preferred term
								} else if (simpleType != null) {
									type = simpleType; // either 2 -> SYN or 3 -> FSN
								} else {
									type = "0";
								}
								
							}
							
							_values[5] = type;
							_values[6] = description.getLanguageCode();
							
							return new StringBuilder(valueOfOrEmptyString(_values[0])) // ID
								.append(HT)
								.append(getDescriptionStatus(valueOfOrEmptyString(_values[1]))) // status
								.append(HT)
								.append(valueOfOrEmptyString(_values[2])) // concept id
								.append(HT)
								.append(valueOfOrEmptyString(_values[3])) // term
								.append(HT)
								.append(valueOfOrEmptyString(_values[4])) // initial capital status
								.append(HT)
								.append(valueOfOrEmptyString(_values[5])) // type
								.append(HT)
								.append(valueOfOrEmptyString(_values[6])) // language code
								.append(HT)
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
		return ComponentExportType.DESCRIPTION;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_DESCRIPTION_HEADER;
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
	public void close() throws IOException {
		//intentionally ignored
	}
	
	@Override
	public SnomedExportConfiguration getConfiguration() {
		return configuration;
	}
	
	/* returns with a number indicating the status of a description for RF1 publication. */
	private String getDescriptionStatus(final String stringValue) {
		return Preconditions.checkNotNull(mapper.getDescriptionStatusProperty(stringValue));
	}
	
}