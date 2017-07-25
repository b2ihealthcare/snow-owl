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

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.ISnomedRelationship;
import com.b2international.snowowl.snomed.core.domain.SnomedRelationships;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.b2international.snowowl.snomed.exporter.server.Id2Rf1PropertyMapper;
import com.b2international.snowowl.snomed.exporter.server.SnomedReleaseFileHeaders;
import com.b2international.snowowl.snomed.exporter.server.sandbox.SnomedExportConfiguration;

/**
 * @since 4.6.12
 */
public class SnomedRf1RelationshipExporter extends SnomedPageableRf1Exporter<SnomedRelationships, ISnomedRelationship> {

	public SnomedRf1RelationshipExporter(SnomedExportConfiguration configuration, Id2Rf1PropertyMapper mapper) {
		super(configuration, mapper);
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.RELATIONSHIP;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedReleaseFileHeaders.RF1_RELATIONSHIP_HEADER;
	}

	@Override
	protected SnomedRelationships executeQuery(int offset) {
		return SnomedRequests.prepareSearchRelationship()
			.setOffset(offset)
			.setLimit(DEFAULT_LIMIT)
			.build(getBranch())
			.executeSync(getBus());
	}

	@Override
	protected String convertToString(ISnomedRelationship relationship) {
		
		Object[] _values = new Object[7];
		
		_values[0] = relationship.getId();
		_values[1] = relationship.getSourceId();
		_values[2] = relationship.getTypeId();
		_values[3] = relationship.getDestinationId();
		_values[4] = relationship.getCharacteristicType().getConceptId();
		_values[5] = Concepts.OPTIONAL_REFINABLE;
		_values[6] = relationship.getGroup();
		
		return new StringBuilder(valueOfOrEmptyString(_values[0])) // ID
			.append(HT)
			.append(valueOfOrEmptyString(_values[1])) // source
			.append(HT)
			.append(valueOfOrEmptyString(_values[2])) // type
			.append(HT)
			.append(valueOfOrEmptyString(_values[3])) // destination
			.append(HT)
			.append(checkNotNull(getMapper().getRelationshipType(valueOfOrEmptyString(_values[4])))) // characteristic type
			.append(HT)
			.append(checkNotNull(getMapper().getRefinabilityType(valueOfOrEmptyString(_values[5])))) // refinability
			.append(HT)
			.append(valueOfOrEmptyString(_values[6])) //group
			.toString();
	}

	@Override
	protected SnomedRelationships initItems() {
		return new SnomedRelationships(0, 0, -1);
	}

}
