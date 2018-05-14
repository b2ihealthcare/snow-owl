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
package com.b2international.snowowl.snomed.datastore.request;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.request.GetResourceRequestBuilder;
import com.b2international.snowowl.datastore.request.RevisionIndexRequestBuilder;
import com.b2international.snowowl.snomed.core.domain.constraint.SnomedConstraint;

/**
 * <i>Builder</i> class to build requests responsible for fetching a single SNOMED CT MRCM attribute constraint.
 * This class should be instantiated from the corresponding static method on the central {@link SnomedRequests} class.
 * 
 * @since 6.5
 */
public final class SnomedConstraintGetRequestBuilder 
		extends GetResourceRequestBuilder<SnomedConstraintGetRequestBuilder, SnomedConstraintSearchRequestBuilder, BranchContext, SnomedConstraint>
		implements RevisionIndexRequestBuilder<SnomedConstraint> {

	SnomedConstraintGetRequestBuilder(String conceptId) {
		super(new SnomedConstraintGetRequest(conceptId));
	}

}
