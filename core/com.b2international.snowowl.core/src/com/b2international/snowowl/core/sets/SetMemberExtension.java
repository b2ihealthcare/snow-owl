/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.sets;

import java.io.Serializable;

import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.domain.PageableCollectionResource;
import com.b2international.snowowl.core.request.SearchResourceRequestBuilder;
import com.b2international.snowowl.core.request.SearchResourceRequestIterator;

/**
 * @since 7.7
 */
public interface SetMemberExtension <B extends SearchResourceRequestBuilder<B, ?, C>, T, C extends PageableCollectionResource<T>>
	extends Serializable {
		
	short terminologyComponentId();
	
	String sourceCode(T member);
	
	String sourceCodeSystem(String codeSystem, T member);
	
	String sourceTerm(T member);
	
	SearchResourceRequestIterator<B, C> memberIterator(BranchContext context, String branch, String codeSystem, String componentId);
	
}
