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
package com.b2international.snowowl.terminologyregistry.core.request;

import com.b2international.snowowl.datastore.request.version.CodeSystemVersionCreateRequestBuilder;

/**
 * @since 4.7
 */
public class CodeSystemRequests {

	private CodeSystemRequests() {}
	
	public static CodeSystemCreateRequestBuilder prepareNewCodeSystem() {
		return new CodeSystemCreateRequestBuilder();
	}

	public static CodeSystemUpdateRequestBuilder prepareUpdateCodeSystem(final String uniqueId) {
		return new CodeSystemUpdateRequestBuilder(uniqueId);
	}

	public static CodeSystemGetRequestBuilder prepareGetCodeSystem(final String uniqeId) {
		return new CodeSystemGetRequestBuilder(uniqeId);
	}

	public static CodeSystemSearchRequestBuilder prepareSearchCodeSystem() {
		return new CodeSystemSearchRequestBuilder();
	}

	public static CodeSystemVersionSearchRequestBuilder prepareSearchCodeSystemVersion() {
		return new CodeSystemVersionSearchRequestBuilder();
	}
	
	public static CodeSystemVersionCreateRequestBuilder prepareNewCodeSystemVersion() {
		return new CodeSystemVersionCreateRequestBuilder();
	}
	
}
