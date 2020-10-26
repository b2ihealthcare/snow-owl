/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Set;

import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;

/**
 * @since 7.11
 */
public class MapCompareEquivalence  extends Equivalence<ConceptMapMapping> {
	
	Set<ConceptMapCompareConfigurationProperties> configProps;
	
	public MapCompareEquivalence(Set<ConceptMapCompareConfigurationProperties> configProps) {
		this.configProps = configProps;
	}

	@Override
	protected boolean doEquivalent(ConceptMapMapping a, ConceptMapMapping b) {
		if (!configProps.isEmpty()) {
			boolean isSourceDifferent = configProps.stream().anyMatch(config -> !config.isSourceEqual(a, b));
			return !isSourceDifferent;
		}
		return false;
	}

	@Override
	protected int doHash(ConceptMapMapping t) {
		if (configProps.containsAll(ImmutableSet.of(ConceptMapCompareConfigurationProperties.CODE_SYSTEM, ConceptMapCompareConfigurationProperties.CODE, ConceptMapCompareConfigurationProperties.TERM))) {
			return Objects.hashCode(t.getSourceComponentURI().codeSystem(), t.getSourceComponentURI().identifier(), t.getSourceTerm());
		} else if (configProps.containsAll(ImmutableSet.of(ConceptMapCompareConfigurationProperties.CODE_SYSTEM, ConceptMapCompareConfigurationProperties.CODE))) {
			return Objects.hashCode(t.getSourceComponentURI().codeSystem(), t.getSourceComponentURI().identifier());
		} else if (configProps.containsAll(ImmutableSet.of(ConceptMapCompareConfigurationProperties.CODE_SYSTEM, ConceptMapCompareConfigurationProperties.TERM))) {
			return Objects.hashCode(t.getSourceComponentURI().codeSystem(), t.getSourceTerm());
		} else if (configProps.containsAll(ImmutableSet.of(ConceptMapCompareConfigurationProperties.CODE, ConceptMapCompareConfigurationProperties.TERM))) {
			return Objects.hashCode(t.getSourceComponentURI().identifier(), t.getSourceTerm());
		} else if (configProps.contains(ConceptMapCompareConfigurationProperties.CODE_SYSTEM)) {
			return Objects.hashCode(t.getSourceComponentURI().codeSystem());
		} else if (configProps.contains(ConceptMapCompareConfigurationProperties.CODE)) {
			return Objects.hashCode(t.getSourceComponentURI().identifier());
		} else if (configProps.contains(ConceptMapCompareConfigurationProperties.TERM)) {
			return Objects.hashCode(t.getSourceTerm());
		} else {
			return t.hashCode();
		}
	}

}
