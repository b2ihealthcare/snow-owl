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
package com.b2international.snowowl.core.codesystem;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.request.*;

/**
 * @since 4.7
 */
public class CodeSystemRequests {

	private CodeSystemRequests() {}
	
	public static CodeSystemCreateRequestBuilder prepareNewCodeSystem() {
		return new CodeSystemCreateRequestBuilder();
	}

	public static CodeSystemUpdateRequestBuilder prepareUpdateCodeSystem(final String codeSystemId) {
		return new CodeSystemUpdateRequestBuilder(codeSystemId);
	}

	public static CodeSystemGetRequestBuilder prepareGetCodeSystem(final String codeSystemId) {
		return new CodeSystemGetRequestBuilder(codeSystemId);
	}

	public static CodeSystemSearchRequestBuilder prepareSearchCodeSystem() {
		return new CodeSystemSearchRequestBuilder();
	}

	// Upgrade API
	
	public static CodeSystemUpgradeRequestBuilder prepareUpgrade(ResourceURI codeSystem, ResourceURI extensionOf) {
		return new CodeSystemUpgradeRequestBuilder(codeSystem, extensionOf);
	}
	
	public static CodeSystemCompleteUpgradeRequestBuilder prepareComplete(String codeSystemId) {
		return new CodeSystemCompleteUpgradeRequestBuilder(codeSystemId);
	}
	
	public static CodeSystemUpgradeSynchronizationRequestBuilder prepareUpgradeSynchronization(ResourceURI codeSystemId, ResourceURI source) {
		return new CodeSystemUpgradeSynchronizationRequestBuilder(codeSystemId, source);
	}
	
	// Generic Content Search APIs
	
	/**
	 * Creates a new generic concept search request builder.
	 * 
	 * @return the builder to configure for generic concept search
	 */
	public static ConceptSearchRequestBuilder prepareSearchConcepts() {
		return new ConceptSearchRequestBuilder();
	}
	
	/**
	 * Creates a new generic set member search request builder.
	 * 
	 * @return the builder to configure for generic set member search
	 */
	public static ValueSetMemberSearchRequestBuilder prepareSearchMembers() {
		return new ValueSetMemberSearchRequestBuilder();
	}
	
	public static ConceptSuggestionRequestBuilder prepareSuggestConcepts() {
		return new ConceptSuggestionRequestBuilder();
	}

	public static QueryOptimizeRequestBuilder prepareOptimizeQueries() {
		return new QueryOptimizeRequestBuilder();
	}
	
}
