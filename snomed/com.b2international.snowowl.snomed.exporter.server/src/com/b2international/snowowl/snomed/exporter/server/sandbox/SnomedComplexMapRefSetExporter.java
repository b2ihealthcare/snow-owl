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

import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_CORRELATION_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_ADVICE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_CATEGORY_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_PRIORITY;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_RULE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * SNOMED&nbsp;CT complex and extended map type reference set exporter.
 *
 */
public class SnomedComplexMapRefSetExporter extends SnomedRefSetExporter {

	private static final Set<String> FIELD_TO_LOAD;
	
	static {
		final Set<String> fieldsToLoad = newHashSet(COMMON_FIELDS_TO_LOAD);
		
		fieldsToLoad.add(REFERENCE_SET_MEMBER_MAP_GROUP);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_MAP_PRIORITY);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_MAP_RULE);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_MAP_ADVICE);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_CORRELATION_ID);
		fieldsToLoad.add(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID);
		FIELD_TO_LOAD = unmodifiableSet(fieldsToLoad);
	}
	
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
		sb.append(doc.get(REFERENCE_SET_MEMBER_MAP_GROUP));
		sb.append(HT);
		sb.append(doc.get(REFERENCE_SET_MEMBER_MAP_PRIORITY));
		sb.append(HT);
		sb.append(nullToEmpty(doc.get(REFERENCE_SET_MEMBER_MAP_RULE)));
		sb.append(HT);
		sb.append(nullToEmpty(doc.get(REFERENCE_SET_MEMBER_MAP_ADVICE)));
		sb.append(HT);
		sb.append(nullToEmpty(doc.get(REFERENCE_SET_MEMBER_MAP_TARGET_COMPONENT_ID)));
		sb.append(HT);
		sb.append(doc.get(REFERENCE_SET_MEMBER_CORRELATION_ID));
		if (extended) {
			sb.append(HT);
			sb.append(nullToEmpty(doc.get(REFERENCE_SET_MEMBER_MAP_CATEGORY_ID)));
		}
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return extended ? SnomedRf2Headers.EXTENDED_MAP_TYPE_HEADER : SnomedRf2Headers.COMPLEX_MAP_TYPE_HEADER;
	}
	
}