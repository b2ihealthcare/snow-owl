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
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.EXISTENTIAL_RESTRICTION_MODIFIER;
import static com.b2international.snowowl.snomed.SnomedConstants.Concepts.UNIVERSAL_RESTRICTION_MODIFIER;
import static com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants.RELATIONSHIP_NUMBER;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_GROUP;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_OBJECT_ID;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_UNIVERSAL;
import static com.b2international.snowowl.snomed.datastore.browser.SnomedIndexBrowserConstants.RELATIONSHIP_VALUE_ID;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;

/**
 * RF2 exporter for SNOMED CT relationships.
 */
public abstract class AbstractSnomedRelationshipExporter extends SnomedCoreExporter {

	private static final Set<String> FIELDS_TO_LOAD = SnomedMappings.fieldsToLoad()
			.id()
			.effectiveTime()
			.active()
			.module()
			.relationshipType()
			.relationshipCharacteristicType()
			.field(RELATIONSHIP_OBJECT_ID)
			.field(RELATIONSHIP_VALUE_ID)
			.field(RELATIONSHIP_GROUP)
			.field(RELATIONSHIP_UNIVERSAL).build();
	
	private Occur statedOccur;
	
	protected AbstractSnomedRelationshipExporter(final SnomedExportConfiguration configuration, final Occur statedOccur) {
		super(checkNotNull(configuration, "configuration"));
		this.statedOccur = checkNotNull(statedOccur, "statedOccur");
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
		sb.append(SnomedMappings.active().getValueAsString(doc));
		sb.append(HT);
		sb.append(SnomedMappings.module().getValueAsString(doc));
		sb.append(HT);
		sb.append(doc.get(RELATIONSHIP_OBJECT_ID));
		sb.append(HT);
		sb.append(doc.get(RELATIONSHIP_VALUE_ID));
		sb.append(HT);
		sb.append(doc.get(RELATIONSHIP_GROUP));
		sb.append(HT);
		sb.append(SnomedMappings.relationshipType().getValueAsString(doc));
		sb.append(HT);
		sb.append(SnomedMappings.relationshipCharacteristicType().getValueAsString(doc));
		sb.append(HT);
		sb.append(getModifierValue(doc));
		return sb.toString();
	}

	@Override
	public String[] getColumnHeaders() {
		return SnomedRf2Headers.RELATIONSHIP_HEADER;
	}
	
	@Override
	protected int getTerminologyComponentType() {
		return RELATIONSHIP_NUMBER;
	}
	
	@Override
	protected Query getSnapshotQuery() {
		final BooleanQuery snapshotQuery = new BooleanQuery(true);
		snapshotQuery.add(super.getSnapshotQuery(), Occur.MUST);
		snapshotQuery.add(SnomedMappings.newQuery().relationship().relationshipCharacteristicType(Concepts.STATED_RELATIONSHIP).matchAll(), statedOccur);
		return snapshotQuery;
	}
	
	private String getModifierValue(final Document doc) {
		return 1 == getIntValue(doc.getField(RELATIONSHIP_UNIVERSAL)) 
				? UNIVERSAL_RESTRICTION_MODIFIER 
				: EXISTENTIAL_RESTRICTION_MODIFIER;
	}
}
