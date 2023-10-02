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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.ConceptMapEquivalence;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

/**
 * This class represents a FHIR Concept Map translate operation result.
 * 
 * @see <a href="https://www.hl7.org/fhir/conceptmap-operations.html#translate">
 * FHIR:ConceptMap:Operations:translate</a>
 * 
 * @since 7.1
 */
@JsonDeserialize(builder = TranslateResult.Builder.class)
@JsonPropertyOrder({"result", "message", "match"})
public class TranslateResult {
	
	// True if the concept could be translated successfully (1..1)
	@NotNull
	private final Boolean result;

	// Human-readable error details (0..1)
	private final String message;
	
	/* A concept in the target value set with an equivalence. 
	 * Note that there may be multiple matches of equal or differing equivalence, 
	 * and the matches may include equivalence values that mean that there is no match (0..*)
	 * */
	@FhirType(FhirDataType.PART)
	private final Collection<Match> match;
	
	TranslateResult(Boolean result,	String message,	Collection<Match> matches) {
		this.result = result;
		this.message = message;
		this.match = matches;
	}
	
	public Boolean getResult() {
		
		if (match == null || match.isEmpty()) return false;
		
		return match.stream().filter(m -> m.getEquivalence() != null 
			&& !m.getEquivalence().equals(ConceptMapEquivalence.DISJOINT.getCode())
			&& !m.getEquivalence().equals(ConceptMapEquivalence.UNMATCHED.getCode())
		)
		.findAny().isPresent();
	}
	
	public String getMessage() {
		return message;
	}

	public Collection<Match> getMatches() {
		return match;
	}
	
	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<TranslateResult> {

		private String message;
		private ImmutableList.Builder<Match> matches = ImmutableList.builder();
		
		Builder() {}
		
		//only for Deserialization
		@SuppressWarnings("unused")
		private Builder result(final Boolean result) {
			//
			return this;
		}
		
		public Builder message(final String message) {
			this.message = message;
			return this;
		}
		
		/**
		 * Alternative property deserializer to be used when converting FHIR Parameters representation to TranslateRequest. 
		 * Multi-valued property expand.
		 */
		public Builder addMatch(Match match) {
			matches.add(match);
			return this;
		}
		
		public void addMatches(Collection<Match> additionalMatches) {
			matches.addAll(additionalMatches);
		}
		
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder match(Collection<Match> matchCollection) {
			matches = ImmutableList.builder();
			matches.addAll(matchCollection);
			return this;
		}
		
		@Override
		protected TranslateResult doBuild() {
			
			List<Match> matchesList = matches.build();
			
			boolean result = matchesList.stream().filter(m -> m.getEquivalence() != null 
				&& !m.getEquivalence().equals(ConceptMapEquivalence.DISJOINT.getCode())
				&& !m.getEquivalence().equals(ConceptMapEquivalence.UNMATCHED.getCode())
			)
			.findAny().isPresent();
			
			return new TranslateResult(result, message, matchesList);
		}
	}
	
}
