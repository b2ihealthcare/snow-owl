/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.request.xls;

import java.util.UUID;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.events.BaseRequestBuilder;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.identity.domain.User;

/**
 * @since 7.0
 */
public final class SnomedRefSetExcelExportRequestBuilder extends BaseRequestBuilder<SnomedRefSetExcelExportRequestBuilder, BranchContext, UUID>
		implements RevisionIndexRequestBuilder<UUID> {

	private String refSetId;
	private String usedId = User.SYSTEM.getUsername();
	
	SnomedRefSetExcelExportRequestBuilder(String refSetId) {
		this.refSetId = refSetId;
	}
	
	public SnomedRefSetExcelExportRequestBuilder setUsedId(String usedId) {
		this.usedId = usedId;
		return getSelf();
	}

	@Override
	protected Request<BranchContext, UUID> doBuild() {
		final SnomedRefSetExcelExportRequest req = new SnomedRefSetExcelExportRequest();
		req.refSetId = refSetId;
		req.userId = usedId;
		return req;
	}

}
