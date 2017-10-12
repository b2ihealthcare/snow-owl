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
package com.b2international.snowowl.snomed.datastore.index.entry;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;

import com.b2international.commons.BooleanUtils;
import com.b2international.snowowl.core.api.IComponent;
import com.b2international.snowowl.core.api.index.IIndexEntry;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.datastore.cdo.CDOUtils;
import com.b2international.snowowl.datastore.index.mapping.Mappings;
import com.b2international.snowowl.snomed.Description;
import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescription;
import com.b2international.snowowl.snomed.datastore.index.mapping.SnomedMappings;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.FluentIterable;

/**
 * A transfer object representing a SNOMED CT description.
 */
public class SnomedDescriptionIndexEntry extends SnomedIndexEntry implements IComponent<String>, IIndexEntry, Serializable {

	private static final long serialVersionUID = 301681633674309020L;

	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final Document doc) {
		final Builder builder = builder()
				.id(SnomedMappings.id().getValueAsString(doc)) 
				.term(SnomedMappings.descriptionTerm().getValue(doc)) 
				.moduleId(SnomedMappings.module().getValueAsString(doc))
				.languageCode(SnomedMappings.descriptionLanguageCode().getValue(doc))
				.storageKey(Mappings.storageKey().getValue(doc))
				.released(BooleanUtils.valueOf(SnomedMappings.released().getValue(doc)))
				.active(BooleanUtils.valueOf(SnomedMappings.active().getValue(doc)))
				.typeId(SnomedMappings.descriptionType().getValueAsString(doc))
				.conceptId(SnomedMappings.descriptionConcept().getValueAsString(doc))
				.caseSignificanceId(SnomedMappings.descriptionCaseSignificance().getValueAsString(doc))
				.effectiveTimeLong(SnomedMappings.effectiveTime().getValue(doc));
		
		final Iterable<String> preferredRefSetIds = SnomedMappings.descriptionPreferredReferenceSetId().getValuesAsStringList(doc);
		for (final String preferredRefSetId : preferredRefSetIds) {
			builder.acceptability(preferredRefSetId, Acceptability.PREFERRED);
		}
		
		final Iterable<String> acceptableRefSetIds = SnomedMappings.descriptionAcceptableReferenceSetId().getValuesAsStringList(doc);
		for (final String acceptableRefSetId : acceptableRefSetIds) {
			builder.acceptability(acceptableRefSetId, Acceptability.ACCEPTABLE);
		}
		
		return builder;
	}
	
	public static Builder builder(final ISnomedDescription input) {
		final Builder builder = builder()
				.id(input.getId())
				.term(input.getTerm()) 
				.moduleId(input.getModuleId())
				.languageCode(input.getLanguageCode())
				.released(input.isReleased())
				.active(input.isActive())
				.typeId(input.getTypeId())
				.conceptId(input.getConceptId())
				.caseSignificanceId(input.getCaseSignificance().getConceptId())
				.effectiveTimeLong(EffectiveTimes.getEffectiveTime(input.getEffectiveTime()));
		
		if (input.getScore() != null) {
			builder.score(input.getScore());
		}
		
		if (input.getType() != null && input.getType().getPt() != null) {
			builder.typeLabel(input.getType().getPt().getTerm());
		}
		
		for (final String refSetId : input.getAcceptabilityMap().keySet()) {
			builder.acceptability(refSetId, input.getAcceptabilityMap().get(refSetId));
		}
	
		return builder;
	}
	
	public static Builder builder(Description description) {
		return builder()
				.id(description.getId()) 
				.term(description.getTerm())
				.moduleId(description.getModule().getId())
				.storageKey(CDOUtils.getStorageKey(description))
				.released(description.isReleased()) 
				.active(description.isActive()) 
				.typeId(description.getType().getId()) 
				.caseSignificanceId(description.getCaseSignificance().getId()) 
				.conceptId(description.getConcept().getId())
				.languageCode(description.getLanguageCode())
				.effectiveTimeLong(description.isSetEffectiveTime() ? description.getEffectiveTime().getTime() : EffectiveTimes.UNSET_EFFECTIVE_TIME);
	}
	
	public static List<SnomedDescriptionIndexEntry> fromDescriptions(Iterable<ISnomedDescription> descriptions) {
		return FluentIterable.from(descriptions).transform(new Function<ISnomedDescription, SnomedDescriptionIndexEntry>() {
			@Override
			public SnomedDescriptionIndexEntry apply(ISnomedDescription input) {
				return builder(input).build();
			}
		}).toList();
	}

	public static class Builder extends AbstractBuilder<Builder> {

		private String term;
		private String conceptId;
		private String languageCode;
		private String typeId;
		private String typeLabel;
		private String caseSignificanceId;
		private final Map<String, Acceptability> acceptabilityMapBuilder = new HashMap<>();

		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder term(final String term) {
			this.term = term;
			label(term);
			return getSelf();
		}

		public Builder conceptId(final String conceptId) {
			this.conceptId = conceptId;
			return getSelf();
		}

		public Builder languageCode(final String languageCode) {
			this.languageCode = languageCode;
			return getSelf();
		}

		public Builder typeId(final String typeId) {
			this.typeId = typeId;
			return getSelf();
		}
		
		public Builder typeLabel(final String typeLabel) {
			this.typeLabel = typeLabel;
			return getSelf();
		}

		public Builder caseSignificanceId(final String caseSignificanceId) {
			this.caseSignificanceId = caseSignificanceId;
			return getSelf();
		}
		
		public Builder acceptability(final String languageRefSetId, final Acceptability acceptability) {
			this.acceptabilityMapBuilder.put(languageRefSetId, acceptability);
			return getSelf();
		}
		
		public Builder acceptabilityMap(final Map<String, Acceptability> acceptabilityMap) {
			this.acceptabilityMapBuilder.putAll(acceptabilityMap);
			return getSelf();
		}

		public SnomedDescriptionIndexEntry build() {
			return new SnomedDescriptionIndexEntry(id,
					label,
					score,
					storageKey, 
					moduleId,
					released, 
					active, 
					effectiveTimeLong, 
					conceptId, 
					languageCode,
					term,
					typeId,
					typeLabel == null ? typeId : typeLabel,
					caseSignificanceId,
					acceptabilityMapBuilder);
		}
	}

	private final String conceptId;
	private final String languageCode;
	private final String term;
	private final String typeId;
	private final String caseSignificanceId;
	private final Map<String, Acceptability> acceptabilityMap;
	private final String typeLabel;

	private SnomedDescriptionIndexEntry(final String id,
			final String label,
			final float score, 
			final long storageKey, 
			final String moduleId, 
			final boolean released, 
			final boolean active, 
			final long effectiveTimeLong, 
			final String conceptId,
			final String languageCode,
			final String term,
			final String typeId,
			final String typeLabel,
			final String caseSignificanceId,
			final Map<String, Acceptability> acceptabilityMap) {

		super(id,
				label,
				typeId, // XXX: iconId is the same as typeId 
				score, 
				storageKey, 
				moduleId, 
				released, 
				active, 
				effectiveTimeLong);

		this.conceptId = checkNotNull(conceptId, "Description concept identifier may not be null.");
		this.languageCode = checkNotNull(languageCode, "Description language code may not be null.");
		this.term = checkNotNull(term, "Description term may not be null.");
		this.typeId = checkNotNull(typeId, "Description type identifier may not be null.");
		this.typeLabel = typeLabel;
		this.caseSignificanceId = checkNotNull(caseSignificanceId, "Description case significance identifier may not be null.");
		this.acceptabilityMap = checkNotNull(acceptabilityMap, "Description acceptability map may not be null."); 
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
	 * @return the description term
	 */
	public String getTerm() {
		return term;
	}

	/**
	 * @return the description type concept identifier
	 */
	public String getTypeId() {
		return typeId;
	}
	
	/**
	 * @return the label of the description type concept
	 */
	public String getTypeLabel() {
		return typeLabel;
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
	public Map<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}
	
	/**
	 * @return <code>true</code> if this description is a fully specified name, <code>false</code> otherwise.
	 */
	public boolean isFsn() {
		return Concepts.FULLY_SPECIFIED_NAME.equals(getTypeId());
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("id", id)
				.add("label", label)
				.add("iconId", iconId)
				.add("moduleId", moduleId)
				.add("score", score)
				.add("storageKey", storageKey)
				.add("released", released)
				.add("active", active)
				.add("effectiveTime", effectiveTimeLong)
				.add("conceptId", conceptId)
				.add("languageCode", languageCode)
				.add("term", term)
				.add("typeId", typeId)
				.add("caseSignificanceId", caseSignificanceId)
				.add("acceptabilityMap", acceptabilityMap)
				.toString();
	}

}
