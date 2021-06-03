/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.events.bulk;

import java.util.List;

import com.b2international.snowowl.core.domain.ListCollectionResource;
import com.google.common.collect.FluentIterable;

/**
 * @since 4.5
 * @see BulkRequestBuilder
 */
public final class BulkResponse extends ListCollectionResource<Object> {

	BulkResponse(List<Object> responses) {
		super(responses);
	}
	
	public <T> FluentIterable<T> getResponses(final Class<T> type) {
		return FluentIterable.from(getItems()).filter(type);
	}
}
