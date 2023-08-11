/*
 * Copyright 2011-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.snowowl.core.ecl.EclLabelerRequestBuilder;
import com.b2international.snowowl.core.request.ConceptSearchRequestBuilder;
import com.b2international.snowowl.core.request.QueryOptimizeRequestBuilder;
import com.b2international.snowowl.core.request.ValueSetMemberSearchRequestBuilder;
import com.b2international.snowowl.core.request.suggest.ConceptSuggestionBulkRequestBuilder;
import com.b2international.snowowl.core.request.suggest.ConceptSuggestionRequestBuilder;

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

	public static CodeSystemGetRequestBuilder prepareGetCodeSystem(final String codeSystemUri) {
		return new CodeSystemGetRequestBuilder(CodeSystem.uri(codeSystemUri));
	}
	
	public static CodeSystemGetRequestBuilder prepareGetCodeSystem(final ResourceURI codeSystemUri) {
		return new CodeSystemGetRequestBuilder(codeSystemUri);
	}

	public static CodeSystemSearchRequestBuilder prepareSearchCodeSystem() {
		return new CodeSystemSearchRequestBuilder();
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
	
	public static ConceptSuggestionBulkRequestBuilder prepareBulkSuggestConcepts() {
		return new ConceptSuggestionBulkRequestBuilder();
	}

	public static QueryOptimizeRequestBuilder prepareOptimizeQueries() {
		return new QueryOptimizeRequestBuilder();
	}
	
	/**
	 * Returns a request builder to prepare the labeling of a ECL expressions.
	 * 
	 * @param codeSystemUri - the code system from where the labels should be computed
	 * @param expression - the ECL expression to extend with labels
	 * @return ECL labeler request builder
	 */
	public static EclLabelerRequestBuilder prepareEclLabeler(String codeSystemUri, String expression) {
		return new EclLabelerRequestBuilder(codeSystemUri, expression);
	}
	
	/**
	 * Returns a request builder to prepare the labeling of a list of Expression Constraint Language (ECL) expressions.
	 * 
	 * @param codeSystemUri - the code system from where the labels should be computed
	 * @param expressions - a list of ECL expressions to extend with labels
	 * @return ECL labeler request builder
	 */
	public static EclLabelerRequestBuilder prepareEclLabeler(String codeSystemUri, List<String> expressions) {
		return new EclLabelerRequestBuilder(codeSystemUri, expressions);
	}

}
