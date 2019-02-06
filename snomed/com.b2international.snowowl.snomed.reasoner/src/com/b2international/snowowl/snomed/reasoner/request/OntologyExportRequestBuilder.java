/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.reasoner.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;

/**
 * @since 7.0
 */
public final class OntologyExportRequestBuilder 
		extends BaseRequestBuilder<OntologyExportRequestBuilder, BranchContext, String> 
		implements RevisionIndexRequestBuilder<String> {

	private OntologyExportType exportType;

	OntologyExportRequestBuilder() {}

	public OntologyExportRequestBuilder setExportType(final OntologyExportType exportType) {
		this.exportType = exportType;
		return this;
	}

	@Override
	protected Request<BranchContext, String> doBuild() {
		final OntologyExportRequest exportRequest = new OntologyExportRequest();
		exportRequest.setExportType(exportType);
		return exportRequest;
	}
}
