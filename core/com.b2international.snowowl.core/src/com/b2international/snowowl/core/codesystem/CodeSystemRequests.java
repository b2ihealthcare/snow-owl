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

import java.util.List;

import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.codesystem.version.CodeSystemVersionCreateRequestBuilder;
import com.b2international.snowowl.core.codesystem.version.CodeSystemVersionSearchRequestBuilder;
import com.b2international.snowowl.core.compare.ConceptMapCompareResultItem;
import com.b2international.snowowl.core.jobs.RemoteJobEntry;
import com.b2international.snowowl.core.request.*;
import com.b2international.snowowl.core.uri.ComponentURI;

/**
 * @since 4.7
 */
public class CodeSystemRequests {

	public static final String VERSION_JOB_KEY_PREFIX = "version-";
	
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
	
	// Upgrade API
	
	public static CodeSystemUpgradeRequestBuilder prepareUpgrade(ResourceURI codeSystem, ResourceURI extensionOf) {
		return new CodeSystemUpgradeRequestBuilder(codeSystem, extensionOf);
	}
	
	public static CodeSystemCompleteUpgradeRequestBuilder prepareComplete(String codeSystemId) {
		return new CodeSystemCompleteUpgradeRequestBuilder(codeSystemId);
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
	public static MemberSearchRequestBuilder prepareSearchMembers() {
		return new MemberSearchRequestBuilder();
	}
	
	/**
	 * Creates a new generic set mapping search request builder.
	 * 
	 * @return the builder to configure for generic mappings search
	 */
	public static ConceptMapMappingSearchRequestBuilder prepareSearchConceptMapMappings() {
		return new ConceptMapMappingSearchRequestBuilder();
	}
	
	public static ConceptSuggestionRequestBuilder prepareSuggestConcepts() {
		return new ConceptSuggestionRequestBuilder();
	}

	public static QueryOptimizeRequestBuilder prepareOptimizeQueries() {
		return new QueryOptimizeRequestBuilder();
	}
	
	public static ConceptMapCompareRequestBuilder prepareConceptMapCompare(ComponentURI baseConceptMapURI, ComponentURI compareConceptMapURI){
		return new ConceptMapCompareRequestBuilder(baseConceptMapURI, compareConceptMapURI);
	}

	public static ConceptMapCompareDsvExportRequestBuilder prepareConceptMapCompareDsvExport(final List<ConceptMapCompareResultItem> items, final String filePath){
		return new ConceptMapCompareDsvExportRequestBuilder(items, filePath);
	}

	public static String versionJobKey(String codeSystemShortName) {
		return VERSION_JOB_KEY_PREFIX.concat(codeSystemShortName);
	}
	
	public static boolean isVersionJob(RemoteJobEntry job) {
		return job != null && job.getKey().startsWith(VERSION_JOB_KEY_PREFIX);
	}
	
}
