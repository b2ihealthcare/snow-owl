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
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * SNOMED CT complex and extended map type reference set exporter.
 */
public class SnomedComplexMapRefSetExporter extends SnomedRefSetExporter {

	private static final Set<String> FIELD_TO_LOAD = SnomedMappings.fieldsToLoad()
			.fields(COMMON_FIELDS_TO_LOAD)
			.memberMapGroup()
			.memberMapPriority()
			.memberMapRule()
			.memberMapAdvice()
			.memberMapTargetComponentId()
			.memberCorrelationId()
			.memberMapCategoryId()
			.build();
	
	private final boolean extended;
	
	public SnomedComplexMapRefSetExporter(final SnomedExportConfiguration configuration, final String refSetId, 
			final SnomedRefSetType type, final boolean extended) {
		
		super(checkNotNull(configuration, "configuration"), checkNotNull(refSetId, "refSetId"), checkNotNull(type, "type"));
		this.extended = extended;
	}
	
	@Override
	public Set<String> getFieldsToLoad() {
		return FIELD_TO_LOAD;
	}

	@Override
	public String transform(Document doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(super.transform(doc));
		sb.append(HT);
		sb.append(SnomedMappings.memberMapGroup().getValueAsString(doc));
		sb.append(HT);
		sb.append(SnomedMappings.memberMapPriority().getValueAsString(doc));
		sb.append(HT);
		sb.append(nullToEmpty(SnomedMappings.memberMapRule().getValue(doc)));
		sb.append(HT);
		sb.append(nullToEmpty(SnomedMappings.memberMapAdvice().getValue(doc)));
		sb.append(HT);
		sb.append(nullToEmpty(SnomedMappings.memberMapTargetComponentId().getValue(doc)));
		sb.append(HT);
		sb.append(SnomedMappings.memberCorrelationId().getValueAsString(doc));
		if (extended) {
			sb.append(HT);
			sb.append(nullToEmpty(SnomedMappings.memberMapCategoryId().getValueAsString(doc)));
		}
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return extended ? SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER : SnomedRf2Headers.COMPLEX_MAP_TYPE_HEADER;
	}
}
