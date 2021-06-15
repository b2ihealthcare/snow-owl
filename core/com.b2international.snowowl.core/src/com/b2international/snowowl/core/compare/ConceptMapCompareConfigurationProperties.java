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
package com.b2international.snowowl.core.compare;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;

import com.b2international.snowowl.core.domain.ConceptMapMapping;

/**
 * @since 7.11
 */
public enum ConceptMapCompareConfigurationProperties {

	CODE_SYSTEM (
		"Code System", 
		(member1, member2) -> Objects.equals(member1.getSourceComponentURI().resourceUri().getResourceId(), member2.getSourceComponentURI().resourceUri().getResourceId()),
		(member1, member2) -> Objects.equals(member1.getTargetComponentURI().resourceUri().getResourceId(), member2.getTargetComponentURI().resourceUri().getResourceId())
	),

	TERM (
		"Term", 
		(member1, member2) -> Objects.equals(member1.getSourceTerm(), member2.getSourceTerm()),
		(member1, member2) -> Objects.equals(member1.getTargetTerm(), member2.getTargetTerm())
	),

	CODE (
		"Code", 
		(member1, member2) -> Objects.equals(member1.getSourceComponentURI().identifier(), member2.getSourceComponentURI().identifier()),
		(member1, member2) -> Objects.equals(member1.getTargetComponentURI().identifier(), member2.getTargetComponentURI().identifier())
	);

	public static final Set<ConceptMapCompareConfigurationProperties> ALL_PROPERTIES = Set.of(ConceptMapCompareConfigurationProperties.CODE_SYSTEM, ConceptMapCompareConfigurationProperties.CODE, ConceptMapCompareConfigurationProperties.TERM);
	public static final Set<ConceptMapCompareConfigurationProperties> DEFAULT_SELECTED_PROPERTIES = Set.of(ConceptMapCompareConfigurationProperties.CODE_SYSTEM, ConceptMapCompareConfigurationProperties.CODE);
			
	private final String label;
	private final BiFunction<ConceptMapMapping, ConceptMapMapping, Boolean> getSourceEqualFunction;
	private final BiFunction<ConceptMapMapping, ConceptMapMapping, Boolean> getTargetEqualFunction;

	private ConceptMapCompareConfigurationProperties(final String label, 
			final BiFunction<ConceptMapMapping, ConceptMapMapping, Boolean> getSourceEqualFunction, 
			final BiFunction<ConceptMapMapping, ConceptMapMapping, Boolean> getTargetEqualFunction) {
		this.label = label;
		this.getSourceEqualFunction = getSourceEqualFunction;
		this.getTargetEqualFunction = getTargetEqualFunction;
	}

	public String getLabel() {
		return label;
	}

	public boolean isSourceEqual(ConceptMapMapping member1, ConceptMapMapping member2) {
		return getSourceEqualFunction.apply(member1, member2);
	}

	public boolean isTargetEqual(ConceptMapMapping member1, ConceptMapMapping member2) {
		return getTargetEqualFunction.apply(member1, member2);
	}

	@Override
	public String toString() {
		return label;
	}
	
}
