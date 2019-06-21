/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.ResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedDescriptionIndexEntry;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Represents a SNOMED&nbsp;CT description.
 * <br>
 * Descriptions returned by search requests are populated based on the expand parameters passed into the {@link ResourceRequestBuilder#setExpand(String)}
 * methods.   
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code type()} - returns the concept representing the type of the description</li>
 * <li>{@code members()} - returns the reference set members referencing this component</li>
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
	id = SnomedTerminologyComponentConstants.DESCRIPTION, 
	shortId = SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER,
	name = "SNOMED CT Description",
	componentCategory = ComponentCategory.DESCRIPTION,
	docType = SnomedDescriptionIndexEntry.class,
	supportedRefSetTypes = {
		"SIMPLE",
		"ATTRIBUTE_VALUE",
		"SIMPLE_MAP",
		"SIMPLE_MAP_WITH_DESCRIPTION"
	}
)
public final class SnomedDescription extends SnomedCoreComponent {

	private static final long serialVersionUID = 1L;

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
	private CaseSignificance caseSignificance;
	private DescriptionInactivationIndicator inactivationIndicator;
	private Map<String, Acceptability> acceptabilityMap;
	private Multimap<AssociationType, String> associationTargets;
	private SnomedConcept concept;
	private SnomedConcept type;

	public SnomedDescription() {
	}
	
	public SnomedDescription(String id) {
		setId(id);
	}
	
	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.DESCRIPTION_NUMBER;
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
	 * Returns the description's case significance attribute, indicating whether character case within the term should
	 * be preserved or is interchangeable.
	 * 
	 * @return the case significance of this description
	 */
	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	/**
	 * Returns language reference set member acceptability values for this description, keyed by language reference set identifier.
	 * 
	 * @return the acceptability map for this description
	 */
	public Map<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}

	/**
	 * Returns the inactivation indicator (if any) of the description that can be used to identify the reason why the
	 * current description has been deactivated.
	 * 
	 * @return the inactivation reason for this description, or {@code null} if the description does not have an inactivation indicator set
	 */
	public DescriptionInactivationIndicator getInactivationIndicator() {
		return inactivationIndicator;
	}

	/**
	 * Returns association reference set member targets keyed by the association type.
	 * 
	 * @return related association targets, or {@code null} if the description does not have any association targets
	 */
	public Multimap<AssociationType, String> getAssociationTargets() {
		return associationTargets;
	}
	
	/**
	 * Returns association reference set member targets keyed by the association type as a {@link java.util.Map}.
	 * 
	 * @return related association targets, or {@code null} if the description does not have any association targets
	 */
	@JsonProperty("associationTargets")
	public Map<AssociationType, List<String>> getAssociationTargetsAsMap() {
		if (associationTargets == null) {
			return null;
		} else {
			final Map<AssociationType, List<String>> targets = Maps.newHashMapWithExpectedSize(associationTargets.size());
			associationTargets.forEach((key, value) -> {
				if (!targets.containsKey(key)) {
					targets.put(key, new ArrayList<>(associationTargets.get(key).size()));
				}
				targets.get(key).add(value);
			});
			return targets;
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

	public void setCaseSignificance(final CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	@JsonProperty("acceptability")
	public void setAcceptabilityMap(final Map<String, Acceptability> acceptabilityMap) {
		this.acceptabilityMap = acceptabilityMap;
	}
	
	public void setInactivationIndicator(final DescriptionInactivationIndicator descriptionInactivationIndicator) {
		this.inactivationIndicator = descriptionInactivationIndicator;
	}
	
	@JsonIgnore
	public void setAssociationTargets(final Multimap<AssociationType, String> associationTargets) {
		this.associationTargets = associationTargets;
	}
	
	@JsonProperty("associationTargets")
	public void setAssociationTargets(final Map<AssociationType, List<String>> associationTargets) {
		if (associationTargets == null) {
			this.associationTargets = null;
		} else {
			final ImmutableListMultimap.Builder<AssociationType, String> targets = ImmutableListMultimap.<AssociationType, String>builder();
			associationTargets.forEach(targets::putAll);
			this.associationTargets = targets.build();
		}
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		return SnomedRequests.prepareUpdateDescription(getId())
			.setAcceptability(getAcceptabilityMap())
			.setActive(isActive())
			.setAssociationTargets(getAssociationTargets())
			.setCaseSignificance(getCaseSignificance())
			.setInactivationIndicator(getInactivationIndicator())
			.setModuleId(getModuleId())
			.setTypeId(getTypeId())
			.setTerm(getTerm())
			.setLanguageCode(getLanguageCode())
			.build();
	}

	@Override
	public Request<TransactionContext, String> toCreateRequest(final String conceptId) {
		return SnomedRequests.prepareNewDescription()
			.setAcceptability(getAcceptabilityMap())
			.setCaseSignificance(getCaseSignificance())
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
		if (null != inactivationIndicator) {
			builder.append(", getDescriptionInactivationIndicator()=")
				.append(inactivationIndicator);
			
		}
		builder.append(", getAssociationTargets()=");
		builder.append(getAssociationTargets());
		builder.append("]");
		return builder.toString();
	}
}