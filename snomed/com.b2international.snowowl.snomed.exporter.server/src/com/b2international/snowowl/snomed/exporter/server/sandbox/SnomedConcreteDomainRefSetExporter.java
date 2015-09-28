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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;

import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * SNOMED&nbsp;CT concrete domain reference set exporter.
 *
 */
public class SnomedConcreteDomainRefSetExporter extends SnomedRefSetExporter {

	private static final Set<String> FIELD_TO_LOAD = SnomedMappings.fieldsToLoad()
			.fields(COMMON_FIELDS_TO_LOAD)
			.label()
			.field(REFERENCE_SET_MEMBER_UOM_ID)
			.field(REFERENCE_SET_MEMBER_OPERATOR_ID)
			.field(REFERENCE_SET_MEMBER_SERIALIZED_VALUE)
			.field(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID).build();
	
	public SnomedConcreteDomainRefSetExporter(final SnomedExportConfiguration configuration, final String refSetId, final SnomedRefSetType type) {
		super(checkNotNull(configuration, "configuration"), checkNotNull(refSetId, "refSetId"), checkNotNull(type, "type"));
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
		sb.append(nullToEmpty(doc.get(REFERENCE_SET_MEMBER_UOM_ID)));
		sb.append(HT);
		sb.append(doc.get(REFERENCE_SET_MEMBER_OPERATOR_ID));
		sb.append(HT);
		sb.append(nullToEmpty(Mappings.label().getValue(doc)));
		sb.append(HT);
		sb.append(doc.get(REFERENCE_SET_MEMBER_SERIALIZED_VALUE));
		sb.append(HT);
		sb.append(nullToEmpty(doc.get(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID)));
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER_WITH_LABEL;
	}
	
}