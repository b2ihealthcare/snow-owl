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
package com.b2international.snowowl.snomed.datastore.index.update;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.snowowl.datastore.index.DocumentUpdaterBase;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedDocumentBuilder;
import com.b2international.snowowl.snomed.datastore.taxonomy.ISnomedTaxonomyBuilder;

/**
 * @since 4.3
 */
public abstract class SnomedDocumentUpdaterBase extends DocumentUpdaterBase<SnomedDocumentBuilder> {

	private ISnomedTaxonomyBuilder taxonomyBuilder;

	public SnomedDocumentUpdaterBase(ISnomedTaxonomyBuilder taxonomyBuilder, final String componentId) {
		super(componentId);
		this.taxonomyBuilder = checkNotNull(taxonomyBuilder, "taxonomyBuilder");
	}
	
	protected ISnomedTaxonomyBuilder getTaxonomyBuilder() {
		return taxonomyBuilder;
	}
}
