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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_OPERATOR_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_SERIALIZED_VALUE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_UOM_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * SNOMED&nbsp;CT concrete domain reference set exporter.
 *
 */
public class SnomedConcreteDomainRefSetExporter extends SnomedRefSetExporter {

	private static final Set<String> FIELD_TO_LOAD;
	
	static {
		final Set<String> fieldsToLoad = newHashSet(COMMON_FIELDS_TO_LOAD);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_UOM_ID);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_OPERATOR_ID);
		fieldsToLoad.add(COMPONENT_LABEL);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_SERIALIZED_VALUE);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_CHARACTERISTIC_TYPE_ID);
		FIELD_TO_LOAD = unmodifiableSet(fieldsToLoad);
	}
	
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
		sb.append(nullToEmpty(doc.get(COMPONENT_LABEL)));
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