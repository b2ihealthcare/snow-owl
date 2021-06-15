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

import java.util.List;
import java.util.Set;

import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * @since 7.11
 */
public final class MapCompareSourceAndTargetEquivalence extends Equivalence<ConceptMapMapping> {
	
	private final Set<ConceptMapCompareConfigurationProperties> configProps;
	
	public MapCompareSourceAndTargetEquivalence(Set<ConceptMapCompareConfigurationProperties> configProps) {
		this.configProps = configProps;
	}

	@Override
	protected boolean doEquivalent(ConceptMapMapping a, ConceptMapMapping b) {
		if (!configProps.isEmpty()) {
			boolean isSourceDifferent = configProps.stream().anyMatch(config -> !config.isSourceEqual(a, b));
			boolean isTargetDifferent = configProps.stream().anyMatch(config -> !config.isTargetEqual(a, b));
			return !isSourceDifferent && !isTargetDifferent;
		}
		return false;
	}

	@Override
	protected int doHash(ConceptMapMapping t) {
		List<String> objectsToHash = Lists.newArrayList();
		if (configProps.contains(ConceptMapCompareConfigurationProperties.CODE_SYSTEM)) {
			objectsToHash.add(t.getSourceComponentURI().resourceUri().getResourceId());
			objectsToHash.add(t.getTargetComponentURI().resourceUri().getResourceId());
		}
		
		if (configProps.contains(ConceptMapCompareConfigurationProperties.CODE)) {
			objectsToHash.add(t.getSourceComponentURI().identifier());
			objectsToHash.add(t.getTargetComponentURI().identifier());
		}
		
		if (configProps.contains(ConceptMapCompareConfigurationProperties.TERM)) {
			objectsToHash.add(t.getSourceTerm());
			objectsToHash.add(t.getTargetTerm());
		}
		
		if (!objectsToHash.isEmpty()) {
			return Objects.hashCode(objectsToHash);
		} else {
			return t.hashCode();
		}
	}

}
