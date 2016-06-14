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

import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;

/**
 * SNOMED CT concrete domain reference set exporter.
 */
public class SnomedConcreteDomainRefSetExporter extends SnomedRefSetExporter {

	public SnomedConcreteDomainRefSetExporter(final SnomedExportConfiguration configuration, final String refSetId, final SnomedRefSetType type) {
		super(checkNotNull(configuration, "configuration"), checkNotNull(refSetId, "refSetId"), checkNotNull(type, "type"));
	}
	
	@Override
	public String transform(SnomedRefSetMemberIndexEntry doc) {
		
		final StringBuilder sb = new StringBuilder();
		sb.append(super.transform(doc));
		sb.append(HT);
		sb.append(doc.getUomComponentId());
		sb.append(HT);
		sb.append(doc.getOperatorComponentId());
		sb.append(HT);
		sb.append(nullToEmpty(doc.getAttributeLabel()));
		sb.append(HT);
		sb.append(doc.getStringField(SnomedRefSetMemberIndexEntry.Fields.DATA_VALUE)); //the direct value
		sb.append(HT);
		sb.append(nullToEmpty(doc.getCharacteristicTypeId()));
		return sb.toString();
	}
	
	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.CONCRETE_DATA_TYPE_HEADER_WITH_LABEL;
	}
}
