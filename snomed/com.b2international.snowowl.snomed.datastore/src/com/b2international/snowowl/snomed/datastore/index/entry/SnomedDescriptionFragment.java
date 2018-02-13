/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index.entry;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import com.b2international.commons.collections.Collections3;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

/**
 * A minimum representation of a SNOMED CT Description indexed and added to
 * {@link SnomedConceptDocument concept documents} to allow efficient term based
 * sorting and expand of PTs/FSNs for a given language reference set and acceptability. 
 */
public final class SnomedDescriptionFragment implements Serializable {

	private static final long serialVersionUID = 25732180785342410L;
	
	private final String id;
	private final long storageKey;
	private final String typeId;
	private final String term;
	private final List<String> languageRefSetIds;
	
	public SnomedDescriptionFragment(
			final String id,
			final long storageKey,
			final String typeId, 
			final String term, 
			final String languageRefSetId) {
		this(id, storageKey, typeId, term, ImmutableList.of(languageRefSetId));
	} 
	
	@JsonCreator
	public SnomedDescriptionFragment(
			@JsonProperty("id") final String id,
			@JsonProperty("storageKey") final long storageKey,
			@JsonProperty("typeId") final String typeId, 
			@JsonProperty("term") final String term, 
			@JsonProperty("languageRefSetId") final List<String> languageRefSetIds) {
		this.id = id;
		this.storageKey = storageKey;
		this.typeId = typeId;
		this.languageRefSetIds = Collections3.toImmutableList(languageRefSetIds);
		this.term = term;
	}

	public String getId() {
		return id;
	}
	
	public long getStorageKey() {
		return storageKey;
	}

	public String getTypeId() {
		return typeId;
	}

	public String getTerm() {
		return term;
	}

	public List<String> getLanguageRefSetIds() {
		return languageRefSetIds;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, storageKey, typeId, term, languageRefSetIds);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SnomedDescriptionFragment other = (SnomedDescriptionFragment) obj;
		return Objects.equals(id, other.id)
				&& Objects.equals(storageKey, other.storageKey)
				&& Objects.equals(typeId, other.typeId)
				&& Objects.equals(term, other.term)
				&& languageRefSetIds.containsAll(other.languageRefSetIds) && other.languageRefSetIds.containsAll(languageRefSetIds);
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("id", id)
				.add("storageKey", storageKey)
				.add("typeId", typeId)
				.add("term", term)
				.add("languageRefSetIds", languageRefSetIds)
				.toString();
	}
	
}