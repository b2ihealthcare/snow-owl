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
package com.b2international.snowowl.snomed.core.domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.IndexResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.MapTargetTypes;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a SNOMED&nbsp;CT description.
 * <br>
 * Descriptions returned by search requests are populated based on the expand parameters passed into the {@link IndexResourceRequestBuilder#setExpand(String)}
 * methods.   
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code type()} - returns the concept representing the type of the description</li>
 * <li>{@code members()} - returns the reference set members referencing this component</li>
 * <li>{@code acceptabilities()} - returns the acceptability membership for this description</li>
 * </ul>
 * 
 * Expand parameters can be nested to further expand or filter the details returned. 
 * <p>
 * @see SnomedConcept
 * @see SnomedRelationship
 * @see SnomedReferenceSet
 * @see SnomedReferenceSetMember
 */
@TerminologyComponent(
	name = "SNOMED CT Description",
	componentCategory = ComponentCategory.DESCRIPTION,
	docType = SnomedDescriptionIndexEntry.class,
	supportedRefSetTypes = {
		MapTargetTypes.SIMPLE,
		MapTargetTypes.ATTRIBUTE_VALUE,
		MapTargetTypes.SIMPLE_MAP,
		MapTargetTypes.SIMPLE_MAP_WITH_DESCRIPTION,
		MapTargetTypes.LANGUAGE
	}
)
public final class SnomedDescription extends SnomedCoreComponent {

	private static final long serialVersionUID = 1L;

	public static final String TYPE = "description";
	
	/**
	 * @since 6.16
	 */
	public static final class Fields extends SnomedCoreComponent.Fields {
		
		public static final String CONCEPT_ID = SnomedRf2Headers.FIELD_CONCEPT_ID;
		public static final String LANGUAGE_CODE = SnomedRf2Headers.FIELD_LANGUAGE_CODE;
		public static final String TYPE_ID = SnomedRf2Headers.FIELD_TYPE_ID;
		public static final String TERM = SnomedRf2Headers.FIELD_TERM;
		public static final String CASE_SIGNIFICANCE_ID = SnomedRf2Headers.FIELD_CASE_SIGNIFICANCE_ID;
		public static final String SEMANTIC_TAG = "semanticTag";
		
		public static final Set<String> ALL = ImmutableSet.<String>builder()
				// RF2 properties
				.add(ID)
				.add(EFFECTIVE_TIME)
				.add(ACTIVE)
				.add(MODULE_ID)
				.add(CONCEPT_ID)
				.add(LANGUAGE_CODE)
				.add(TYPE_ID)
				.add(TERM)
				.add(CASE_SIGNIFICANCE_ID)
				// additional fields
				.add(RELEASED)
				.add(SEMANTIC_TAG)
				.build();
		
	}
	
	private String term;
	private String semanticTag;
	private String languageCode;
	private SnomedConcept caseSignificance;
	private Map<String, Acceptability> acceptabilityMap;
	private List<AcceptabilityMembership> acceptabilities;
	private SnomedConcept concept;
	private SnomedConcept type;

	public SnomedDescription() {
	}
	
	public SnomedDescription(String id) {
		setId(id);
	}
	
	@Override
	public String getComponentType() {
		return SnomedDescription.TYPE;
	}

	/**
	 * Returns the associated concept's identifier, eg. "{@code 363698007}".
	 * 
	 * @return the concept identifier or <code>null</code> if the concept is currently not set
	 */
	@JsonProperty
	public String getConceptId() {
		return getConcept() == null ? null : getConcept().getId();
	}

	/**
	 * Returns the description type identifier, eg. "{@code 900000000000013009}".
	 * 
	 * @return the type identifier or <code>null</code> if the type is currently not set
	 */
	@JsonProperty
	public String getTypeId() {
		return getType() == null ? null : getType().getId();
	}

	/**
	 * Returns the container concept of the description.
	 *  
	 * @return the container concept
	 */
	public SnomedConcept getConcept() {
		return concept;
	}

	/**
	 * Returns the type concept of the description.
	 *  
	 * @return the type concept
	 */
	public SnomedConcept getType() {
		return type;
	}

	/**
	 * Returns the description term, eg. "{@code Finding site}".
	 * 
	 * @return the description term
	 */
	public String getTerm() {
		return term;
	}
	
	/**
	 * Returns the semantic tag value from the term without the brackets, eg. "{@code finding}", or an empty {@link String} value if no semantic tag is specified in the term.  
	 * @return
	 */
	public String getSemanticTag() {
		return semanticTag;
	}

	/**
	 * Returns the description's language code, not including any dialects or variations, eg. "{@code en}".
	 * 
	 * @return the language code of this description
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * Returns the description's case significance concept, indicating whether character case within the term should
	 * be preserved or is interchangeable.
	 * 
	 * @return the case significance concept of this description
	 */
	public SnomedConcept getCaseSignificance() {
		return caseSignificance;
	}
	
	/**
	 * Returns the identifier of the description's case significance concept.
	 * 
	 * @return the identifier of the case significance concept of this description
	 */
	public String getCaseSignificanceId() {
		return getCaseSignificance() == null ? null : getCaseSignificance().getId();
	}

	/**
	 * Returns language reference set member acceptability values for this description, keyed by language reference set identifier.
	 * 
	 * @return all acceptability values from each available language refset for this description
	 * @deprecated - expand {@link #getAcceptabilities()} instead of relying on {@link #getAcceptabilityMap()}
	 */
	public Map<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}
	
	/**
	 * Returns language reference set member acceptability values for this description, keyed by language reference set identifier.
	 * 
	 * @return all acceptability values from each available language refset for this description
	 */
	public List<AcceptabilityMembership> getAcceptabilities() {
		return acceptabilities;
	}
	
	@JsonIgnore
	public boolean isPreferredInLocales(final List<ExtendedLocale> locales) {
		if (acceptabilities != null) {
			for (ExtendedLocale locale: locales) {
				for (AcceptabilityMembership membership: acceptabilities) {
					if (membership.getLanguageRefSetId().equals(locale.getLanguageRefSetId()) &&
							Concepts.REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED.equals(membership.getAcceptabilityId())) {
						return true;
					}
				}
			}
			
			return false;
		} else {
			return locales.stream().anyMatch(locale -> Acceptability.PREFERRED == acceptabilityMap.get(locale.getLanguageRefSetId()));
		}
	}
	
	@JsonIgnore
	public void setConceptId(final String conceptId) {
		setConcept(new SnomedConcept(conceptId));
	}

	@JsonIgnore
	public void setTypeId(final String typeId) {
		setType(new SnomedConcept(typeId));
	}
	
	public void setConcept(SnomedConcept concept) {
		this.concept = concept;
	}
	
	public void setType(SnomedConcept type) {
		this.type = type;
	}

	public void setTerm(final String term) {
		this.term = term;
	}
	
	public void setSemanticTag(String semanticTag) {
		this.semanticTag = semanticTag;
	}

	public void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}

	public void setCaseSignificanceId(final String caseSignificanceId) {
		setCaseSignificance(new SnomedConcept(caseSignificanceId));
	}
	
	public void setCaseSignificance(final SnomedConcept caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	@Deprecated
	@JsonProperty("acceptability")
	public void setAcceptabilityMap(final Map<String, Acceptability> acceptabilityMap) {
		this.acceptabilityMap = acceptabilityMap;
	}
	
	public void setAcceptabilities(List<AcceptabilityMembership> acceptabilities) {
		this.acceptabilities = acceptabilities;
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		return SnomedRequests.prepareUpdateDescription(getId())
			.setAcceptability(getAcceptabilityMap())
			.setActive(isActive())
			.setInactivationProperties(getInactivationProperties())
			.setCaseSignificanceId(getCaseSignificanceId())
			.setModuleId(getModuleId())
			.setTypeId(getTypeId())
			.setTerm(getTerm())
			.setLanguageCode(getLanguageCode())
			.build();
	}

	@Override
	public Request<TransactionContext, String> toCreateRequest(final String conceptId) {
		return SnomedRequests.prepareNewDescription()
			.setActive(isActive())
			.setAcceptability(getAcceptabilityMap())
			.setCaseSignificanceId(getCaseSignificanceId())
			// ensure that the description's conceptId property is the right one
			.setConceptId(conceptId)
			// XXX assuming that the ID is always set in this case
			.setId(getId())
			.setLanguageCode(getLanguageCode())
			.setModuleId(getModuleId())
			.setTerm(getTerm())
			.setTypeId(getTypeId())
			.build();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedDescription [getId()=");
		builder.append(getId());
		builder.append(", isReleased()=");
		builder.append(isReleased());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getTypeId()=");
		builder.append(getTypeId());
		builder.append(", getTerm()=");
		builder.append(getTerm());
		builder.append(", getLanguageCode()=");
		builder.append(getLanguageCode());
		builder.append(", getCaseSignificance()=");
		builder.append(getCaseSignificance());
		builder.append(", getAcceptabilityMap()=");
		builder.append(getAcceptabilityMap());
		if (null != getInactivationProperties()) {
			builder.append(", getInactivationProperties()=")
				.append(getInactivationProperties());
			
		}
		builder.append("]");
		return builder.toString();
	}
}