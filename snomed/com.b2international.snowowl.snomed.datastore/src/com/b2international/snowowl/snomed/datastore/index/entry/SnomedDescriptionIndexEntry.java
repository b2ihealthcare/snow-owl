/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.datastore.index;

import java.io.Serializable;
import java.util.Map;

import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

/**
 * A transfer object representing a SNOMED CT description.
 */
public class SnomedDescriptionIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable {

	private static final long serialVersionUID = 301681633674309020L;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		private String id;
		private String term;
		private String moduleId;
		private long storageKey;
		private float score;
		private boolean active;
		private boolean released;
		private long effectiveTimeLong;
		private String conceptId;
		private String languageCode = "en"; // FIXME: Should not be optional once it is indexed
		private String typeId;
		private String caseSignificanceId;
		private ImmutableMap.Builder<String, Acceptability> acceptabilityMapBuilder;

		private Builder() {
			// Disallow instantiation outside static method
		}

		public Builder id(final String id) {
			this.id = id;
			return this;
		}

		public Builder term(final String term) {
			this.term = term;
			return this;
		}

		public Builder moduleId(final String moduleId) {
			this.moduleId = moduleId;
			return this;
		}

		public Builder storageKey(final long storageKey) {
			this.storageKey = storageKey;
			return this;
		}

		public Builder score(final float score) {
			this.score = score;
			return this;
		}

		public Builder active(final boolean active) {
			this.active = active;
			return this;
		}

		public Builder released(final boolean released) {
			this.released = released;
			return this;
		}

		public Builder effectiveTimeLong(final long effectiveTimeLong) {
			this.effectiveTimeLong = effectiveTimeLong;
			return this;
		}

		public Builder conceptId(final String conceptId) {
			this.conceptId = conceptId;
			return this;
		}

		public Builder languageCode(final String languageCode) {
			this.languageCode = languageCode;
			return this;
		}

		public Builder typeId(final String typeId) {
			this.typeId = typeId;
			return this;
		}

		public Builder caseSignificanceId(final String caseSignificanceId) {
			this.caseSignificanceId = caseSignificanceId;
			return this;
		}
		
		public Builder acceptability(final String languageRefSetId, final Acceptability acceptability) {
			this.acceptabilityMapBuilder.put(languageRefSetId, acceptability);
			return this;
		}
		
		public Builder acceptability(final Map<String, Acceptability> acceptabilityMap) {
			this.acceptabilityMapBuilder.putAll(acceptabilityMap);
			return this;
		}

		public SnomedDescriptionIndexEntry build() {
			return new SnomedDescriptionIndexEntry(id,
					term,
					moduleId, 
					score,
					storageKey, 
					released, 
					active, 
					effectiveTimeLong, 
					conceptId,
					languageCode,
					typeId,
					caseSignificanceId,
					acceptabilityMapBuilder.build());
		}
	}

	private final String conceptId;
	private final String languageCode;
	private final String typeId;
	private final String caseSignificanceId;
	private final ImmutableMap<String, Acceptability> acceptabilityMap;

	private SnomedDescriptionIndexEntry (final String id, 
			final String term, 
			final String moduleId, 
			final float score, 
			final long storageKey, 
			final boolean released, 
			final boolean active, 
			final long effectiveTimeLong,
			final String conceptId,
			final String languageCode,
			final String typeId, 
			final String caseSignificanceId,
			final ImmutableMap<String, Acceptability> acceptabilityMap) {

		super(id, 
				term, 
				typeId, // XXX: iconId is the same as typeId
				moduleId, 
				score, 
				storageKey, 
				released, 
				active, 
				effectiveTimeLong);

		this.conceptId = conceptId;
		this.languageCode = languageCode;
		this.typeId = typeId;
		this.caseSignificanceId = caseSignificanceId;
		this.acceptabilityMap = acceptabilityMap; 
	}

	/**
	 * @return the parent concept identifier
	 */
	public String getConceptId() {
		return conceptId;
	}

	/**
	 * @return the description Locale (of which only the ISO-639 language code should be populated) 
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * @return the description type concept identifier
	 */
	public String getTypeId() {
		return typeId;
	}

	/**
	 * @return the case significance concept identifier
	 */
	public String getCaseSignificance() {
		return caseSignificanceId;
	}
	
	/**
	 * @return the map of active acceptability values for the description, keyed by language reference set identifier
	 */
	public ImmutableMap<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("iconId", iconId)
				.add("moduleId", getModuleId())
				.add("score", score)
				.add("storageKey", storageKey)
				.add("released", isReleased())
				.add("active", isActive())
				.add("effectiveTime", getEffectiveTimeAsLong())
				.add("conceptId", conceptId)
				.add("locale", languageCode)
				.add("typeId", typeId)
				.add("caseSignificanceId", caseSignificanceId)
				.add("acceptabilityMap", acceptabilityMap)
				.toString();
	}
}
