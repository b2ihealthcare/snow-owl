/*
 * Copyright 2011-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.b2international.index.query.Expressions.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.b2international.index.*;
import com.b2international.index.query.Expression;
import com.b2international.index.revision.ObjectId;
import com.b2international.index.revision.Revision;
import com.b2international.snowowl.core.date.EffectiveTimes;
import com.b2international.snowowl.core.request.TermFilter;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Function;
import com.google.common.base.MoreObjects.ToStringHelper;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * A transfer object representing a SNOMED CT description.
 */
@Doc(
	type="description",
	revisionHash = { 
		SnomedDocument.Fields.ACTIVE, 
		SnomedDocument.Fields.EFFECTIVE_TIME, 
		SnomedDocument.Fields.MODULE_ID, 
		SnomedDocument.Fields.RELEASED, // XXX required for SnomedComponentRevisionConflictProcessor CHANGED vs. DELETED detection
		SnomedDescriptionIndexEntry.Fields.TYPE_ID,
		SnomedDescriptionIndexEntry.Fields.TERM,
		SnomedDescriptionIndexEntry.Fields.CASE_SIGNIFICANCE_ID
	}
)
@JsonDeserialize(builder = SnomedDescriptionIndexEntry.Builder.class)
public final class SnomedDescriptionIndexEntry extends SnomedComponentDocument {

	private static final long serialVersionUID = 301681633674309020L;
	private static final Pattern SEM_TAG = Pattern.compile(".*\\((.*)\\)");

	/**
	 * Extracts the semantic tag from a given term. Returns empty {@link String} if there is no semantic tag.
	 * @param term
	 * @return the semantic tag or empty {@link String}, never <code>null</code>
	 */
	public static String extractSemanticTag(String term) {
		final Matcher matcher = SEM_TAG.matcher(term);
		if (matcher.matches()) {
			return matcher.group(1);
		} else {
			return "";
		}
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static Builder builder(final SnomedDescription input) {
		final Builder builder = builder()
				.id(input.getId())
				.active(input.isActive())
				.effectiveTime(EffectiveTimes.getEffectiveTime(input.getEffectiveTime()))
				.released(input.isReleased())
				.moduleId(input.getModuleId())
				.conceptId(input.getConceptId())
				.languageCode(input.getLanguageCode())
				.term(input.getTerm()) 
				.typeId(input.getTypeId())
				.caseSignificanceId(input.getCaseSignificanceId());
		
		for (final String refSetId : input.getAcceptabilityMap().keySet()) {
			builder.acceptability(refSetId, input.getAcceptabilityMap().get(refSetId));
		}
	
		return builder;
	}
	
	/**
	 * Creates a new {@link Builder} from the given {@link SnomedDescriptionIndexEntry}. The acceptability map is not copied over to the
	 * {@link Builder} instance, if you need that, manually modify the returned {@link Builder} to represent the desired acceptability state.
	 * 
	 * @param doc
	 * @return
	 */
	public static Builder builder(SnomedDescriptionIndexEntry doc) {
		return builder()
				.id(doc.getId())
				.active(doc.isActive())
				.effectiveTime(doc.getEffectiveTime())
				.released(doc.isReleased())
				.moduleId(doc.getModuleId())
				.conceptId(doc.getConceptId())
				.languageCode(doc.getLanguageCode())
				.term(doc.getTerm())
				.typeId(doc.getTypeId())
				.caseSignificanceId(doc.getCaseSignificanceId())
				.acceptabilityMap(doc.getAcceptabilityMap());
	}
	
	public static List<SnomedDescriptionIndexEntry> fromDescriptions(Iterable<SnomedDescription> descriptions) {
		return FluentIterable.from(descriptions).transform(new Function<SnomedDescription, SnomedDescriptionIndexEntry>() {
			@Override
			public SnomedDescriptionIndexEntry apply(SnomedDescription input) {
				return builder(input).build();
			}
		}).toList();
	}

	public final static class Fields extends SnomedComponentDocument.Fields {
		public static final String CONCEPT_ID = SnomedRf2Headers.FIELD_CONCEPT_ID;
		public static final String TYPE_ID = SnomedRf2Headers.FIELD_TYPE_ID;
		public static final String CASE_SIGNIFICANCE_ID = SnomedRf2Headers.FIELD_CASE_SIGNIFICANCE_ID;
		public static final String LANGUAGE_CODE = SnomedRf2Headers.FIELD_LANGUAGE_CODE;
		public static final String PREFERRED_IN = "preferredIn";
		public static final String ACCEPTABLE_IN = "acceptableIn";
		public static final String SEMANTIC_TAG = "semanticTag";
		public static final String TERM = SnomedRf2Headers.FIELD_TERM;
		public static final String TERM_PREFIX = Fields.TERM + ".prefix";
		public static final String TERM_ORIGINAL = Fields.TERM + ".original";
		public static final String TERM_EXACT = Fields.TERM + ".exact";
	}
	
	public final static class Expressions extends SnomedComponentDocument.Expressions {
		
		private Expressions() {
		}
		
		public static Expression termDisjunctionQuery(final TermFilter termFilter) {
			return dismaxWithScoreCategories(
				matchEntireTerm(termFilter.getTerm()),
				matchTextAll(Fields.TERM, termFilter.getTerm()).withIgnoreStopwords(termFilter.isIgnoreStopwords()),
				matchBooleanPrefix(Fields.TERM, termFilter.getTerm()).withIgnoreStopwords(termFilter.isIgnoreStopwords()),
				matchTextAll(Fields.TERM_PREFIX, termFilter.getTerm()).withIgnoreStopwords(termFilter.isIgnoreStopwords())
			);
		}
		
		public static Expression minShouldMatchTermDisjunctionQuery(final TermFilter termFilter) {
			return dismaxWithScoreCategories(
				matchTextAny(Fields.TERM, termFilter.getTerm(), termFilter.getMinShouldMatch()).withIgnoreStopwords(termFilter.isIgnoreStopwords()),
				matchTextAny(Fields.TERM_PREFIX, termFilter.getTerm(), termFilter.getMinShouldMatch()).withIgnoreStopwords(termFilter.isIgnoreStopwords())
			);
		}

		public static Expression fuzzy(String term) {
			return matchTextFuzzy(Fields.TERM, term);
		}
		
		public static Expression matchEntireTerm(String term) {
			return matchTextAll(Fields.TERM_EXACT, term);
		}
		
		public static Expression matchTermOriginal(String term) {
			return exactMatch(Fields.TERM_ORIGINAL, term);
		}
		
		public static Expression matchTermOriginal(Iterable<String> terms) {
			return matchAny(Fields.TERM_ORIGINAL, terms);
		}
		
		public static Expression matchTermRegex(String regex) {
			return regexp(Fields.TERM_ORIGINAL, regex);
		}
		
		public static Expression parsedTerm(String term) {
			return matchTextParsed(Fields.TERM, term);
		}
		
		public static Expression concept(String conceptId) {
			return concepts(Collections.singleton(conceptId));
		}
		
		public static Expression concepts(Collection<String> conceptIds) {
			return matchAny(Fields.CONCEPT_ID, conceptIds);
		}
		
		public static Expression type(String typeId) {
			return types(Collections.singleton(typeId));
		}
		
		public static Expression types(Collection<String> typeIds) {
			return matchAny(Fields.TYPE_ID, typeIds);
		}
		
		public static Expression caseSignificance(String caseSignificanceId) {
			return caseSignificances(Collections.singleton(caseSignificanceId));
		}
		
		public static Expression caseSignificances(Collection<String> caseSignificanceIds) {
			return matchAny(Fields.CASE_SIGNIFICANCE_ID, caseSignificanceIds);
		}
		
		public static Expression acceptableIn(String languageReferenceSetId) {
			return acceptableIn(Collections.singleton(languageReferenceSetId));
		}
		
		public static Expression preferredIn(String languageReferenceSetId) {
			return preferredIn(Collections.singleton(languageReferenceSetId));
		}
		
		public static Expression acceptableIn(Collection<String> languageReferenceSetIds) {
			return matchAny(Fields.ACCEPTABLE_IN, languageReferenceSetIds);
		}
		
		public static Expression preferredIn(Collection<String> languageReferenceSetIds) {
			return matchAny(Fields.PREFERRED_IN, languageReferenceSetIds);
		}
		
		public static Expression languageCode(String languageCode) {
			return exactMatch(Fields.LANGUAGE_CODE, languageCode);
		}
		
		public static Expression languageCodes(Collection<String> languageCodes) {
			return matchAny(Fields.LANGUAGE_CODE, languageCodes);
		}

		public static Expression semanticTags(Iterable<String> semanticTags) {
			return matchAny(Fields.SEMANTIC_TAG, semanticTags);
		}
		
		public static Expression semanticTagRegex(String regex) {
			return regexp(Fields.SEMANTIC_TAG, regex);
		}

	}
	
	@JsonPOJOBuilder(withPrefix="")
	public static class Builder extends SnomedComponentDocument.Builder<Builder, SnomedDescriptionIndexEntry> {

		private String term;
		private String conceptId;
		private String languageCode;
		private String typeId;
		private String caseSignificanceId;
		private SortedSet<String> acceptableIn = Sets.newTreeSet();
		private SortedSet<String> preferredIn = Sets.newTreeSet();
		private String semanticTag;

		@JsonCreator
		private Builder() {
			// Disallow instantiation outside static method
		}
		
		@Override
		protected Builder getSelf() {
			return this;
		}

		public Builder term(final String term) {
			this.term = term;
			return getSelf();
		}
		
		Builder semanticTag(final String semanticTag) {
			this.semanticTag = semanticTag;
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
		
		public Builder caseSignificanceId(final String caseSignificanceId) {
			this.caseSignificanceId = caseSignificanceId;
			return getSelf();
		}
		
		public Builder acceptableIn(final Collection<String> acceptableIn) {
			this.acceptableIn.clear();
			this.acceptableIn.addAll(acceptableIn);
			return getSelf();
		}
		
		public Builder preferredIn(final Collection<String> preferredIn) {
			this.preferredIn.clear();
			this.preferredIn.addAll(preferredIn);
			return getSelf();
		}
		
		public Builder acceptability(final String languageRefSetId, final Acceptability acceptability) {
			switch (acceptability) {
			case ACCEPTABLE:
				this.acceptableIn.add(languageRefSetId);
				break;
			case PREFERRED:
				this.preferredIn.add(languageRefSetId);
				break;
			default: throw new UnsupportedOperationException("Not implemented: " + acceptability);
			}
			return getSelf();
		}
		
		public Builder acceptabilityMap(final Map<String, Acceptability> acceptabilityMap) {
			this.acceptableIn = Sets.newTreeSet();
			this.preferredIn = Sets.newTreeSet();
			for (Entry<String, Acceptability> entry : acceptabilityMap.entrySet()) {
				acceptability(entry.getKey(), entry.getValue());
			}
			return getSelf();
		}
		
		@Override
		public Builder label(String label) {
			throw new IllegalStateException("Use term() builder method instead to set the label property");
		}
		
		public SnomedDescriptionIndexEntry build() {
			if (!Strings.isNullOrEmpty(term) && semanticTag == null) {
				semanticTag = extractSemanticTag(term);
			}
			final SnomedDescriptionIndexEntry doc = new SnomedDescriptionIndexEntry(id,
					term,
					moduleId,
					released, 
					active, 
					effectiveTime, 
					conceptId, 
					languageCode,
					term,
					semanticTag,
					typeId,
					caseSignificanceId,
					preferredIn, 
					acceptableIn,
					memberOf,
					activeMemberOf);
			doc.setScore(score);
			return doc;
		}
	}

	private final String conceptId;
	private final String languageCode;
	
	@Text(analyzer=Analyzers.TOKENIZED)
	@Text(alias="prefix", analyzer=Analyzers.PREFIX, searchAnalyzer=Analyzers.TOKENIZED)
	@Keyword(alias="exact", normalizer=Normalizers.LOWER_ASCII)
	@Keyword(alias="original")
	private final String term;
	
	private final String semanticTag;
	private final String typeId;
	private final String caseSignificanceId;
	private final SortedSet<String> acceptableIn;
	private final SortedSet<String> preferredIn;

	private SnomedDescriptionIndexEntry(final String id,
			final String label,
			final String moduleId, 
			final Boolean released, 
			final Boolean active, 
			final Long effectiveTime, 
			final String conceptId,
			final String languageCode,
			final String term,
			final String semanticTag,
			final String typeId,
			final String caseSignificanceId,
			final SortedSet<String> preferredIn, 
			final SortedSet<String> acceptableIn,
			final List<String> referringRefSets,
			final List<String> referringMappingRefSets) {
		
		super(id,
				label,
				typeId /* XXX: iconId is the same as typeId*/,
				moduleId,
				released,
				active,
				effectiveTime,
				referringRefSets,
				referringMappingRefSets);
		
		this.conceptId = conceptId;
		this.languageCode = languageCode;
		this.term = term == null ? term : term.trim();
		this.semanticTag = semanticTag;
		this.typeId = typeId;
		this.caseSignificanceId = caseSignificanceId;
		this.preferredIn = preferredIn == null ? Collections.<String>emptySortedSet() : preferredIn;
		this.acceptableIn = acceptableIn == null ? Collections.<String>emptySortedSet() : acceptableIn;
	}
	
	@Override
	protected Revision.Builder<?, ? extends Revision> toBuilder() {
		return builder(this);
	}
	
	@Override
	protected ObjectId getContainerId() {
		return ObjectId.of(SnomedConceptDocument.class, getConceptId());
	}
	
	@Override
	@JsonIgnore
	public String getIconId() {
		return super.getIconId();
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
	 * The value is extracted from each (not just FSN values) description't term using {@link #extractSemanticTag(String)} function. 
	 * 
	 * @return the semantic tag of the description's term
	 */
	public String getSemanticTag() {
		return semanticTag;
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
	public String getCaseSignificanceId() {
		return caseSignificanceId;
	}
	
	/**
	 * Returns the language reference set identifiers where this description is preferred.
	 * @return
	 */
	public SortedSet<String> getPreferredIn() {
		return preferredIn;
	}
	
	/**
	 * Returns the language reference set identifiers where this description is acceptable.
	 * @return
	 */
	public SortedSet<String> getAcceptableIn() {
		return acceptableIn;
	}
	
	/**
	 * @return the map of active acceptability values for the description, keyed by language reference set identifier
	 */
	@JsonIgnore
	public Map<String, Acceptability> getAcceptabilityMap() {
		// TODO check reindex vm argument
		final Map<String, Acceptability> result = Maps.newHashMap();
		for (String acceptableIn : this.acceptableIn) {
			result.put(acceptableIn, Acceptability.ACCEPTABLE);
		}
		for (String preferredIn : this.preferredIn) {
			result.put(preferredIn, Acceptability.PREFERRED);
		}
		return result;
	}
	
	/**
	 * @return <code>true</code> if this description is a fully specified name, <code>false</code> otherwise.
	 */
	@JsonIgnore
	public boolean isFsn() {
		return Concepts.FULLY_SPECIFIED_NAME.equals(getTypeId());
	}
	
	@Override
	protected ToStringHelper doToString() {
		return super.doToString()
				.add("conceptId", conceptId)
				.add("languageCode", languageCode)
				.add("term", term)
				.add("typeId", typeId)
				.add("caseSignificanceId", caseSignificanceId)
				.add("acceptableIn", acceptableIn)
				.add("preferredIn", preferredIn);
	}

}
