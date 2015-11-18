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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.exporter.server.SnomedRfFileNameBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSet;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * SNOMED CT simple map type reference set exporter (optionally with map target description).
 */
public class SnomedSimpleMapRefSetExporter extends SnomedRefSetExporter {

	private static final Set<String> FIELD_TO_LOAD = SnomedMappings.fieldsToLoad()
			.fields(COMMON_FIELDS_TO_LOAD)
			.memberMapTargetComponentId()
			.memberMapTargetComponentDescription()
			.build();
	
	private final boolean includeMapTargetDescription;
	
	public SnomedSimpleMapRefSetExporter(final SnomedExportConfiguration configuration, final String refSetId, 
			final SnomedRefSetType type, final boolean includeMapTargetDescription) {
		
		super(checkNotNull(configuration, "configuration"), checkNotNull(refSetId, "refSetId"), checkNotNull(type, "type"));
		this.includeMapTargetDescription = includeMapTargetDescription;
	}
	
	@Override
	public Set<String> getFieldsToLoad() {
		return FIELD_TO_LOAD;
	}
	
	@Override
	protected String buildRefSetFileName(final String refSetName, final SnomedRefSet refSet) {
		return SnomedRfFileNameBuilder.buildRefSetFileName(getConfiguration(), refSetName, refSet, includeMapTargetDescription);
	}

	@Override
	public String transform(Document doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.transform(doc));
		sb.append(HT);
		sb.append(SnomedMappings.memberMapTargetComponentId().getValue(doc));
		if (includeMapTargetDescription) {
			sb.append(HT);
			sb.append(nullToEmpty(SnomedMappings.memberMapTargetComponentDescription().getValue(doc)));
		}
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return includeMapTargetDescription 
			? SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER_WITH_DESCRIPTION
			: SnomedRf2Headers.SIMPLE_MAP_TYPE_HEADER;
	}
}
