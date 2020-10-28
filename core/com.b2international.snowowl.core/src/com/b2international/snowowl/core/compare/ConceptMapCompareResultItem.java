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

import java.io.Serializable;
import java.util.Objects;

import com.b2international.snowowl.core.domain.ConceptMapMapping;
import com.google.common.collect.ComparisonChain;

/**
 * @since 7.11
 */
public final class ConceptMapCompareResultItem implements Serializable, Comparable<ConceptMapCompareResultItem> {

	private static final long serialVersionUID = 1L;
	
	private final ConceptMapCompareChangeKind changeKind;
	private final ConceptMapMapping mapping;

	public ConceptMapCompareResultItem(ConceptMapCompareChangeKind changeKind, ConceptMapMapping mapping) {
		this.changeKind = changeKind;
		this.mapping = mapping;
	}
	
	public ConceptMapMapping getMapping() {
		return mapping;
	}
	
	public ConceptMapCompareChangeKind getChangeKind() {
		return changeKind;
	}

	@Override
	public int hashCode() {
		return Objects.hash(changeKind, mapping);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ConceptMapCompareResultItem other = (ConceptMapCompareResultItem) obj;
		return Objects.equals(changeKind, other.changeKind)
				&& Objects.equals(mapping, other.mapping);
	}
	
	@Override
	public int compareTo(ConceptMapCompareResultItem o) {
		return ComparisonChain.start()
			.compare(changeKind, o.getChangeKind())
			.compare(mapping.getSourceTerm(), o.getMapping().getSourceTerm())
			.result();
	}
	
}
