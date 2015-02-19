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
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ACTIVE;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.COMPONENT_LABEL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CASE_SIGNIFICANCE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_CONCEPT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_EFFECTIVE_TIME;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_MODULE_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.DESCRIPTION_TYPE_ID;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;

/**
 * RF2 export implementation for SNOMED&nbsp;CT description.
 *
 */
public class SnomedDescriptionExporter extends SnomedCoreExporter {

	//TODO store this as well in index
	private static final String LANGUAGE_CODE = "en";
	
	private static final Set<String> FIELDS_TO_LOAD = unmodifiableSet(newHashSet(
			COMPONENT_ID,
			DESCRIPTION_EFFECTIVE_TIME,
			COMPONENT_ACTIVE,
			DESCRIPTION_MODULE_ID,
			DESCRIPTION_CONCEPT_ID,
			DESCRIPTION_TYPE_ID,
			COMPONENT_LABEL,
			DESCRIPTION_CASE_SIGNIFICANCE_ID
		));

	public SnomedDescriptionExporter(final SnomedExportConfiguration configuration) {
		super(checkNotNull(configuration, "configuration"));
	}

	@Override
	public Set<String> getFieldsToLoad() {
		return FIELDS_TO_LOAD;
	}

	@Override
	public String transform(final Document doc) {
		final StringBuilder sb = new StringBuilder();
		sb.append(doc.get(COMPONENT_ID));
		sb.append(HT);
		sb.append(formatEffectiveTime(doc.getField(getEffectiveTimeField())));
		sb.append(HT);
		sb.append(getIntValue(doc.getField(COMPONENT_ACTIVE)));
		sb.append(HT);
		sb.append(doc.get(DESCRIPTION_MODULE_ID));
		sb.append(HT);
		sb.append(doc.get(DESCRIPTION_CONCEPT_ID));
		sb.append(HT);
		sb.append(LANGUAGE_CODE);
		sb.append(HT);
		sb.append(doc.get(DESCRIPTION_TYPE_ID));
		sb.append(HT);
		sb.append(doc.get(COMPONENT_LABEL));
		sb.append(HT);
		sb.append(doc.get(DESCRIPTION_CASE_SIGNIFICANCE_ID));
		return sb.toString();
	}

	@Override
	public ComponentExportType getType() {
		return ComponentExportType.DESCRIPTION;
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.DESCRIPTION_HEADER;
	}
	
	@Override
	protected int getTerminologyComponentType() {
		return DESCRIPTION_NUMBER;
	}

	@Override
	protected String getEffectiveTimeField() {
		return DESCRIPTION_EFFECTIVE_TIME;
	}

}