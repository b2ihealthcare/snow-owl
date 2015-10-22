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
package com.b2international.snowowl.datastore.server.internal.review;

import com.b2international.snowowl.datastore.review.ConceptChangesMixin;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.1
 */
public class ReviewSerializer extends ObjectMapper {

	private static final long serialVersionUID = -217524850635164394L;

	public ReviewSerializer() {	
		initMixinAnnotations();
	}

	private void initMixinAnnotations() {
		addMixInAnnotations(ReviewImpl.class, ReviewImplMixin.class);
		addMixInAnnotations(BranchStateImpl.class, BranchStateImplMixin.class);
		addMixInAnnotations(ConceptChangesImpl.class, ConceptChangesMixin.class);
	}
}
