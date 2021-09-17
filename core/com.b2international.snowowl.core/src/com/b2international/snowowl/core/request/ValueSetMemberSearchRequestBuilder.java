/*
 * Copyright 2020-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.request;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.ServiceProvider;
import com.b2international.snowowl.core.domain.ValueSetMembers;
import com.b2international.snowowl.core.request.MemberSearchRequestEvaluator.OptionKey;
import com.google.common.collect.FluentIterable;

/**
* @since 7.7
*/
public final class ValueSetMemberSearchRequestBuilder 
		extends SearchPageableCollectionResourceRequestBuilder<ValueSetMemberSearchRequestBuilder, ServiceProvider, ValueSetMembers> 
		implements SystemRequestBuilder<ValueSetMembers> {
	
	public ValueSetMemberSearchRequestBuilder filterByValueSet(String valueSetId) {
		return addOption(OptionKey.URI, valueSetId);
	}
	
	public ValueSetMemberSearchRequestBuilder filterByValueSets(Iterable<String> valueSetIds) {
		return addOption(OptionKey.URI, valueSetIds);
	}
	
	public ValueSetMemberSearchRequestBuilder filterByValueSetUri(ResourceURI valueSetId) {
		return addOption(OptionKey.URI, valueSetId == null ? null : valueSetId.toString());
	}
	
	public ValueSetMemberSearchRequestBuilder filterByValueSetUris(Iterable<ResourceURI> valueSetIds) {
		return addOption(OptionKey.URI, valueSetIds == null ? null : FluentIterable.from(valueSetIds).transform(ResourceURI::toString).toSet());
	}
	
	@Override
	protected SearchResourceRequest<ServiceProvider, ValueSetMembers> createSearch() {
		return new ValueSetMemberSearchRequest();
	}

}
