/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.commons.options.Options;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.exceptions.ComponentNotFoundException;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.RevisionDocument;
import com.b2international.snowowl.datastore.request.RevisionGetRequest;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.datastore.converter.SnomedConverters;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;

/**
 * @since 4.5
 */
final class SnomedRefSetGetRequest extends RevisionGetRequest<SnomedReferenceSet> {

	private static final String REFERENCE_SET = "Reference Set";

	protected SnomedRefSetGetRequest() {
		super(REFERENCE_SET);
	}

	@Override
	protected SnomedReferenceSet process(BranchContext context, IComponent<String> component, Options expand) {
		final SnomedReferenceSet refSet = SnomedConverters.newRefSetConverter(context, expand, locales())
				.convert((SnomedConceptDocument) component);
		
		if (refSet.getStorageKey() != CDOUtils.NO_STORAGE_KEY) {
			return refSet;
		} else {
			throw new ComponentNotFoundException(REFERENCE_SET, component.getId());
		}
	}
	
	@Override
	protected Class<? extends RevisionDocument> getRevisionType() {
		return SnomedConceptDocument.class;
	}
	
}
