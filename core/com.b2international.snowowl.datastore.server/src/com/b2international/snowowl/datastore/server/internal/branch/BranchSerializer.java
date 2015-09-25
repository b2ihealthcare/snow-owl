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
package com.b2international.snowowl.datastore.server.internal.branch;

import com.b2international.snowowl.core.Metadata;
import com.b2international.snowowl.core.MetadataHolder;
import com.b2international.snowowl.core.MetadataHolderMixin;
import com.b2international.snowowl.core.MetadataMixin;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 4.1
 */
public class BranchSerializer extends ObjectMapper {

	private static final long serialVersionUID = 4857662751065469215L;

	public BranchSerializer() {	
		super();
		initMixinAnnotations();
	}

	private void initMixinAnnotations() {
		addMixInAnnotations(Metadata.class, MetadataMixin.class);
		addMixInAnnotations(MetadataHolder.class, MetadataHolderMixin.class);
		addMixInAnnotations(BranchImpl.class, BranchImplMixin.class);
		addMixInAnnotations(MainBranchImpl.class, MainBranchImplMixin.class);
		addMixInAnnotations(CDOBranchImpl.class, CDOBranchImplMixin.class);
		addMixInAnnotations(CDOMainBranchImpl.class, CDOMainBranchImplMixin.class);
		addMixInAnnotations(InternalBranch.class, InternalBranchMixin.class);
	}
	
}
