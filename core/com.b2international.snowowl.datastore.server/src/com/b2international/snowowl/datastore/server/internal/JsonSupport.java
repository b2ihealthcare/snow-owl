/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.server.internal;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataHolder;
import com.b2international.snowowl.core.MetadataHolderMixin;
import com.b2international.snowowl.core.MetadataMixin;
import com.b2international.snowowl.datastore.internal.branch.InternalBranch;
import com.b2international.snowowl.datastore.review.Review;
import com.b2international.snowowl.datastore.review.ReviewMixin;
import com.b2international.snowowl.datastore.server.internal.branch.BranchImpl;
import com.b2international.snowowl.datastore.server.internal.branch.BranchImplMixin;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchImpl;
import com.b2international.snowowl.datastore.server.internal.branch.CDOBranchImplMixin;
import com.b2international.snowowl.datastore.server.internal.branch.CDOMainBranchImpl;
import com.b2international.snowowl.datastore.server.internal.branch.CDOMainBranchImplMixin;
import com.b2international.snowowl.datastore.server.internal.branch.InternalBranchMixin;
import com.b2international.snowowl.datastore.server.internal.branch.MainBranchImpl;
import com.b2international.snowowl.datastore.server.internal.branch.MainBranchImplMixin;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.7
 */
public class JsonSupport {

	public static ObjectMapper getDefaultObjectMapper() {
		final ObjectMapper mapper = new ObjectMapper();
		
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.addMixIn(Metadata.class, MetadataMixin.class);
		mapper.addMixIn(MetadataHolder.class, MetadataHolderMixin.class);
		
		mapper.addMixIn(BranchImpl.class, BranchImplMixin.class);
		mapper.addMixIn(MainBranchImpl.class, MainBranchImplMixin.class);
		mapper.addMixIn(InternalBranch.class, InternalBranchMixin.class);
		
		mapper.addMixIn(CDOBranchImpl.class, CDOBranchImplMixin.class);
		mapper.addMixIn(CDOMainBranchImpl.class, CDOMainBranchImplMixin.class);
		
		mapper.addMixIn(Review.class, ReviewMixin.class);
		return mapper;
	}
	
}
