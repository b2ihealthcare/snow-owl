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

import static com.b2international.snowowl.datastore.index.IndexUtils.getIntValue;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.FULLY_DEFINED;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.PRIMITIVE;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.CONCEPT_PRIMITIVE;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;

/**
 * RF2 exporter for SNOMED&nbsp;CT concepts.
 *
 */
public class SnomedConceptExporter extends SnomedCoreExporter {

	private static final Set<String> FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.effectiveTime()
			.active()
			.module()
			.field(CONCEPT_PRIMITIVE)
			.build();
	
	public SnomedConceptExporter(final SnomedExportConfiguration configuration) {
		super(checkNotNull(configuration, "configuration"));
	}

	@Override
	public Set<String> getFieldsToLoad() {
		return FIELDS_TO_LOAD;
	}
	
	@Override
	public String transform(final Document doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(SnomedMappings.id().getValueAsString(doc));
		sb.append(HT);
		sb.append(formatEffectiveTime(SnomedMappings.effectiveTime().getValue(doc)));
		sb.append(HT);
		sb.append(SnomedMappings.active().getValue(doc));
		sb.append(HT);
		sb.append(SnomedMappings.module().getValueAsString(doc));
		sb.append(HT);
		sb.append(getDefinitionStatusValue(doc));
		return sb.toString();
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.CONCEPT;
	}
	
	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.CONCEPT_HEADER;
	}
	
	@Override
	protected int getTerminologyComponentType() {
		return CONCEPT_NUMBER;
	}

	private String getDefinitionStatusValue(final Document doc) {
		return 1 == getIntValue(doc.getField(CONCEPT_PRIMITIVE)) 
				? PRIMITIVE 
				: FULLY_DEFINED;
	}
}
