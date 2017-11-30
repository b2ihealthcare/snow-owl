/*******************************************************************************
 * Copyright (c) 2017 Integrated Health Information Systems (IHIS) Pte Ltd.
 * All rights reserved.
 *
 * Contributors: B2i Healthcare - initial API and implementation
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.GetResourceRequest;
import com.b2international.snowowl.snomed.core.domain.SnomedConcept;

/**
 * @since 5.7
 */
final class SnomedConceptGetRequest extends GetResourceRequest<SnomedConceptSearchRequestBuilder, BranchContext, SnomedConcept> {

	private static final long serialVersionUID = 1L;
	
	SnomedConceptGetRequest(String conceptId) {
		super(conceptId);
	}

	@Override
	protected SnomedConceptSearchRequestBuilder createSearchRequestBuilder() {
		return new SnomedConceptSearchRequestBuilder();
	}

}
