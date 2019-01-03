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
package com.b2international.snowowl.fhir.core.model.structuredefinition;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.b2international.snowowl.fhir.core.codesystems.AggregationMode;
import com.b2international.snowowl.fhir.core.codesystems.ReferenceVersionRules;
import com.b2international.snowowl.fhir.core.model.Element;
import com.b2international.snowowl.fhir.core.model.Extension;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.b2international.snowowl.fhir.core.search.Mandatory;
import com.b2international.snowowl.fhir.core.search.Summary;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;

/**
 * FHIR {@link ElementDefinition} type and Profile
 * @since 7.1
 */
public class Type extends Element {
	
	@NotNull
	@Valid
	@Mandatory
	@JsonProperty
	private final Uri code;
	
	@Valid
	@Summary
	@JsonProperty
	private final Uri profile;

	@Valid
	@Summary
	@JsonProperty
	private final Uri targetProfile;

	@Summary
	@JsonProperty
	private final Collection<Code> aggregation;

	@Valid
	@Summary
	@JsonProperty
	private final Code versioning;
	
	protected Type(final String id, 
			@SuppressWarnings("rawtypes") final Collection<Extension> extensions,
			final Uri code, 
			final Uri profile, 
			final Uri targetProfile, 
			final Collection<Code> aggregation, 
			final Code versioning) {
		
		super(id, extensions);
		
		this.code = code;
		this.profile = profile;
		this.targetProfile = targetProfile;
		this.aggregation = aggregation;
		this.versioning = versioning;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends Element.Builder<Builder, Type> {
		
		private Uri code;
		private Uri profile;
		private Uri targetProfile;
		private Collection<AggregationMode> aggregationModes = Lists.newArrayList();
		private ReferenceVersionRules referenceVersionRule;
		
		
		@Override
		protected Builder getSelf() {
			return this;
		}
		
		public Builder code(String code) {
			this.code = new Uri(code);
			return getSelf();
		}
		
		public Builder profile(String profile) {
			this.profile = new Uri(profile);
			return getSelf();
		}
		
		public Builder targetProfile(String targetProfile) {
			this.targetProfile = new Uri(targetProfile);
			return getSelf();
		}
		
		public Builder aggregations(Collection<AggregationMode> aggregationModes) {
			this.aggregationModes = aggregationModes;
			return getSelf();
		}
		
		public Builder addAggregation(AggregationMode aggregationMode) {
			this.aggregationModes.add(aggregationMode);
			return getSelf();
		}
		
		public Builder versioning(ReferenceVersionRules referenceVersionRule) {
			this.referenceVersionRule = referenceVersionRule;
			return getSelf();
		}
		
		@Override
		protected Type doBuild() {
			Set<Code> aggregationCodes = aggregationModes.stream().map(a -> a.getCode()).collect(Collectors.toSet());
			return new Type(id, extensions, code, profile, targetProfile, aggregationCodes, referenceVersionRule.getCode());
		}
	}

}
