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
package com.b2international.snowowl.core.validation.whitelist;

import java.util.Collections;
import java.util.Set;

/**
 * @since 6.1
 */
public enum ValidationWhiteListRequests {

	INSTANCE;
	
	public ValidationWhiteListCreateRequestBuilder prepareCreate() {
		return new ValidationWhiteListCreateRequestBuilder();
	}
	
	public ValidationWhiteListDeleteRequestBuilder prepareDelete(String id) {
		return prepareDelete(Collections.singleton(id));
	}

	public ValidationWhiteListDeleteRequestBuilder prepareDelete(Set<String> ids) {
		return new ValidationWhiteListDeleteRequestBuilder(ids);
	}

	public ValidationWhiteListGetRequestBuilder prepareGet(String id) {
		return new ValidationWhiteListGetRequestBuilder(id);
	}
	
	public ValidationWhiteListSearchRequestBuilder prepareSearch() {
		return new ValidationWhiteListSearchRequestBuilder();
	}
	
}
