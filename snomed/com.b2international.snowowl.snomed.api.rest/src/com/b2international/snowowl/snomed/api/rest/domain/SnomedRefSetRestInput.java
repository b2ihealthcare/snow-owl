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
package com.b2international.snowowl.snomed.api.rest.domain;

import com.b2international.snowowl.snomed.datastore.SnomedRefSetUtil;
import com.b2international.snowowl.snomed.datastore.request.SnomedConceptCreateRequestBuilder;
import com.b2international.snowowl.snomed.snomedrefset.SnomedRefSetType;
import com.google.common.base.Strings;

/**
 * @since 4.5
 */
public class SnomedRefSetRestInput extends SnomedConceptRestInput {

	private SnomedRefSetType type;
	private String referencedComponentType;
	
	public SnomedRefSetType getType() {
		return type;
	}
	
	public String getReferencedComponentType() {
		return referencedComponentType;
	}
	
	public void setType(SnomedRefSetType type) {
		this.type = type;
	}
	
	public void setReferencedComponentType(String referencedComponentType) {
		this.referencedComponentType = referencedComponentType;
	}
	
	@Override
	public SnomedConceptCreateRequestBuilder toRequestBuilder() {
		final SnomedConceptCreateRequestBuilder req = super.toRequestBuilder();
		if (Strings.isNullOrEmpty(getParentId())) {
			req.setParent(SnomedRefSetUtil.getConceptId(getType()));
		}
		return req;
	}
	
}
