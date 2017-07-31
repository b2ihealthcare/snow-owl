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
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Map;
import java.util.Set;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.domain.IComponent;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.SnomedConstants.LanguageCodeReferenceSetIdentifierMapping;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.core.domain.SnomedDescriptions;
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
public class SnomedRf1DescriptionExporter extends SnomedPageableRf1Exporter<SnomedDescriptions, ISnomedDescription>{

	private Map<String, String> descriptionToInactivationIndicatorMap;
	private String preferredLanguageRefsetId;
	private boolean includeExtendedDescriptionTypes;
	
	public SnomedRf1DescriptionExporter(SnomedExportConfiguration configuration, Id2Rf1PropertyMapper mapper, final boolean includeExtendedDescriptionTypes) {
		super(configuration, mapper);
		this.includeExtendedDescriptionTypes = includeExtendedDescriptionTypes;
		preferredLanguageRefsetId = LanguageCodeReferenceSetIdentifierMapping.getReferenceSetIdentifier(getLocales().get(0).getLanguageTag());
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
	protected SnomedDescriptions executeQuery(int offset) {
		
		 SnomedDescriptions descriptions = SnomedRequests.prepareSearchDescription()
				.setOffset(offset)
				.setLimit(DEFAULT_LIMIT)
				.build(getBranch())
				.executeSync(getBus());
			
		 Set<String> descriptionIds = FluentIterable.from(descriptions).transform(IComponent.ID_FUNCTION).toSet();
		 
		descriptionToInactivationIndicatorMap = SnomedRequests.prepareSearchMember()
			.all()
			.filterByActive(true)
			.filterByRefSet(Concepts.REFSET_DESCRIPTION_INACTIVITY_INDICATOR)
			.filterByReferencedComponent(descriptionIds)
			.build(getBranch())
			.execute(getBus())
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
		
		return descriptions;
	}

	@Override
	protected String convertToString(ISnomedDescription description) {	
		Object[] _values = new Object[7];
		
		_values[0] = description.getId();
		_values[1] = BooleanUtils.toString(!description.isActive()); // inverse RF1 status conversion
		
		if (descriptionToInactivationIndicatorMap.containsKey(description.getId())) {
			_values[1] = descriptionToInactivationIndicatorMap.get(description.getId());
		}
		
		_values[2] = description.getConceptId();
		_values[3] = description.getTerm();
		_values[4] = checkNotNull(getMapper().getInitialCapitalStatus(description.getCaseSignificance().getConceptId()));
		
		boolean preferred = false;
		
		if (description.getAcceptabilityMap().containsKey(preferredLanguageRefsetId)) {
			preferred = description.getAcceptabilityMap().get(preferredLanguageRefsetId) == Acceptability.PREFERRED;
		}
		
		String type;
		
		if (includeExtendedDescriptionTypes) {
			
			String extendedType = getMapper().getExtendedDescriptionType(description.getTypeId());
			
			if (!description.getTypeId().equals(Concepts.FULLY_SPECIFIED_NAME) && preferred) {
				type = "1"; // preferred term
			} else if (extendedType != null) {
				type = extendedType;
			} else {
				type = "0";
			}
			
		} else {
			
			String simpleType = getMapper().getDescriptionType(description.getTypeId());
			
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

	@Override
	protected SnomedDescriptions initItems() {
		return new SnomedDescriptions(0, 0, -1);
	}

	/* returns with a number indicating the status of a description for RF1 publication. */
	private String getDescriptionStatus(final String stringValue) {
		return Preconditions.checkNotNull(getMapper().getDescriptionStatusProperty(stringValue));
	}
	
}
