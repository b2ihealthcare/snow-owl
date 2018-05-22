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
package com.b2international.snowowl.snomed.datastore.index;

import java.util.Collection;

import com.b2international.snowowl.datastore.index.MappingProvider;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.id.domain.SctId;
import com.b2international.snowowl.snomed.datastore.index.constraint.SnomedConstraintDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRefSetMemberIndexEntry;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedRelationshipIndexEntry;
import com.google.common.collect.ImmutableSet;

/**
 * @since 5.0
 */
public class SnomedMappingProvider implements MappingProvider {

	@Override
	public Collection<Class<?>> getMappings() {
		return ImmutableSet.<Class<?>>builder()
				.add(SnomedConceptDocument.class)
				.add(SnomedDescriptionIndexEntry.class)
				.add(SnomedRelationshipIndexEntry.class)
				.add(SnomedRefSetMemberIndexEntry.class)
				.add(SnomedConstraintDocument.class)
				.build();
	}

	@Override
	public String getToolingId() {
		return SnomedTerminologyComponentConstants.TERMINOLOGY_ID;
	}

}
