/*
 * Copyright 2021-2024 B2i Healthcare, https://b2ihealthcare.com
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
package com.b2international.snowowl.fhir.core.request.bundle;

import java.io.IOException;
import java.util.Date;

import org.hl7.fhir.r5.model.Bundle;
import org.hl7.fhir.r5.model.Bundle.BundleType;
import org.hl7.fhir.r5.model.Meta;

import com.b2international.commons.CompareUtils;
import com.b2international.snowowl.core.domain.RepositoryContext;
import com.b2international.snowowl.core.id.IDs;
import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.fhir.core.request.FhirResourceSearchRequest;

/**
 * @since 8.0.0
 */
public class FhirBundleSearchRequest extends SearchResourceRequest<RepositoryContext, Bundle> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Bundle createEmptyResult(int limit) {
		return prepareBundle().setTotal(0);
	}

	@Override
	protected Bundle doExecute(RepositoryContext context) throws IOException {
		throw new UnsupportedOperationException();
	}
	
	private Bundle prepareBundle() {
		return (Bundle) new Bundle(BundleType.SEARCHSET)
				.setId(IDs.base62UUID())
				.setMeta(new Meta()
						.addTag(CompareUtils.isEmpty(fields()) ? null : FhirResourceSearchRequest.CODING_SUBSETTED)
						.setLastUpdated(new Date())
				);
	}

}
