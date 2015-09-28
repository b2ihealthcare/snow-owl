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
package com.b2international.snowowl.snomed.datastore.index;

import static com.b2international.snowowl.datastore.cdo.CDOIDUtils.asLong;

import org.apache.lucene.document.Document;

import com.b2international.snowowl.datastore.index.AbstractIndexMappingStrategy;
import com.b2international.snowowl.datastore.index.ComponentBaseUpdater;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.b2international.snowowl.snomed.datastore.index.update.ComponentModuleUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.DescriptionImmutablePropertyUpdater;
import com.b2international.snowowl.snomed.datastore.index.update.DescriptionMutablePropertyUpdater;

/**
 * Mapping strategy for SNOMED CT descriptions.
 */
public class SnomedDescriptionIndexMappingStrategy extends AbstractIndexMappingStrategy {
	
	private final Description description;

	public SnomedDescriptionIndexMappingStrategy(final Description description) {
		this.description = description;
	}

	@Override
	public Document createDocument() {
		return SnomedMappings.doc()
				.with(new ComponentBaseUpdater<SnomedDocumentBuilder>(description.getId(), SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER, description.cdoID()))
				.with(new ComponentModuleUpdater(description))
				.with(new DescriptionMutablePropertyUpdater(description))
				.with(new DescriptionImmutablePropertyUpdater(description))
				.build();
	}
	
	@Override
	protected long getStorageKey() {
		return asLong(description.cdoID());
	}
}
