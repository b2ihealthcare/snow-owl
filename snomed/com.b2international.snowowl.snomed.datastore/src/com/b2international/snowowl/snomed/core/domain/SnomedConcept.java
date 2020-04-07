/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.collect.Sets.newHashSet;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.b2international.snowowl.core.domain.TransactionContext;
import com.b2international.snowowl.core.events.Request;
import com.b2international.snowowl.core.request.IndexResourceRequestBuilder;
import com.b2international.snowowl.core.terminology.ComponentCategory;
import com.b2international.snowowl.core.terminology.MapTargetTypes;
import com.b2international.snowowl.core.terminology.TerminologyComponent;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedRf2Headers;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSet;
import com.b2international.snowowl.snomed.core.domain.refset.SnomedReferenceSetMember;
import com.b2international.snowowl.snomed.datastore.index.entry.SnomedConceptDocument;
import com.b2international.snowowl.snomed.datastore.request.SnomedRequests;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;

/**
 * Represents a SNOMED&nbsp;CT concept.
 * <br>
 * Concepts returned by search requests are populated based on the expand parameters passed into the {@link IndexResourceRequestBuilder#setExpand(String)}
 * methods. The expand parameters can be nested allowing a fine control for the details returned in the resultset.  
 * 
 * The supported expand parameters are:
 * <p>
 * <ul>
 * <li>{@code pt()} - returns the <i>Preferred Term</i> for the </li> locale set by {@link IndexResourceRequestBuilder#setLocales(java.util.List)} method.
 * <li>{@code fsn()} - returns the <i>Fully Specified Name (fsn)</i> for the </li> locale set by {@link IndexResourceRequestBuilder#setLocales(java.util.List)} method.</li>
 * <li>{@code descriptions()} - returns the descriptions of the concept</li>
 * <li>{@code relationships()} - returns the relationships of the concept</li>
 * <li>{@code descendants(direct:true|false)} - returns the all or the only the direct descendants of the concept based on the inferred tree.</li> 
 * <li>{@code ancestors(direct:true|false)} - returns the all or the only the direct ancestors of the concept based on the inferred tree.</li>
 * <li>{@code statedDescendants(direct:true|false)} - returns the all or the only the direct descendants of the concept based on the stated tree.</li> 
 * <li>{@code statedAncestors(direct:true|false)} - returns the all or the only the direct ancestors of the concept based on the stated tree.</li>
 * <li>{@code members()} - returns the reference set members referencing this component</li>
 * <li>{@code preferredDescriptions()} - expands the preferred descriptions for each matching concept</li>
 * <li>{@code module()} - expands the module concept of the concept</li>
 * <li>{@code definitionStatus()} - expands the definition status concept of the concept</li>
 * </ul>
 * 
 * The number of expanded fields can be controlled with the {@code limit:} directive.
 * <br>
 * For example: {@code ancestors(direct:false, limit:1000)}
 * 
 * <p>
 * Expand parameters can be nested to further expand or filter the details returned. 
 * For example the expand string:
 * <p>{@code descriptions(expand(type:"typeId"))}<p>
 * returns only the descriptions with the specified <i>typeId</i>
 * 
 * <p>
 * @see SnomedDescription
 * @see SnomedRelationship
 * @see SnomedReferenceSet
 * @see SnomedReferenceSetMember
 */
@TerminologyComponent(
	id = SnomedTerminologyComponentConstants.CONCEPT, 
	shortId = SnomedTerminologyComponentConstants.CONCEPT_NUMBER,
	name = "SNOMED CT Concept", 
	componentCategory = ComponentCategory.CONCEPT,
	docType = SnomedConceptDocument.class,
	supportedRefSetTypes = {
		MapTargetTypes.SIMPLE,
		MapTargetTypes.SIMPLE_MAP,
		MapTargetTypes.SIMPLE_MAP_WITH_DESCRIPTION,
		MapTargetTypes.ATTRIBUTE_VALUE,
		MapTargetTypes.COMPLEX_MAP,
		MapTargetTypes.EXTENDED_MAP,
		// XXX: Not really SNOMED CT reference set types
		MapTargetTypes.VALUE_SET,
		MapTargetTypes.MAPPING_SET
	},
	supportedMapTargetTypes = {
		MapTargetTypes.SIMPLE_MAP,
		MapTargetTypes.SIMPLE_MAP_WITH_DESCRIPTION
	}
)
public final class SnomedConcept extends SnomedCoreComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * Enumerates expandable property keys.
	 * 
	 * @since 5.10
	 */
	public static final class Expand extends SnomedCoreComponent.Expand {

		public static final String REFERENCE_SET = "referenceSet";
		public static final String STATED_ANCESTORS = "statedAncestors";
		public static final String ANCESTORS = "ancestors";
		public static final String STATED_DESCENDANTS = "statedDescendants";
		public static final String DESCENDANTS = "descendants";
		public static final String RELATIONSHIPS = "relationships";
		public static final String INBOUND_RELATIONSHIPS = "inboundRelationships";
		public static final String DESCRIPTIONS = "descriptions";
		public static final String FULLY_SPECIFIED_NAME = "fsn";
		public static final String PREFERRED_TERM = "pt";
		public static final String PREFERRED_DESCRIPTIONS = "preferredDescriptions";
		public static final String DEFINITION_STATUS = "definitionStatus";

	}
	
	/**
	 * @since 6.16
	 */
	public static final class Fields extends SnomedCoreComponent.Fields {

		public static final String DEFINITION_STATUS_ID = SnomedRf2Headers.FIELD_DEFINITION_STATUS_ID;
		
		public static final Set<String> ALL = ImmutableSet.of(
				// RF2 properties
				ID,
				EFFECTIVE_TIME,
				ACTIVE,
				MODULE_ID,
				DEFINITION_STATUS_ID,
				// additional fields
				RELEASED);
		
	}
	
	/**
	 * Helper function to get ancestors of a given {@link SnomedConcept}, both stated and inferred.
	 */
	public static final Function<SnomedConcept, Set<String>> GET_ANCESTORS = (concept) -> {
		final Set<String> ancestors = newHashSet();
		for (long parent : concept.getParentIds()) {
			ancestors.add(Long.toString(parent));
		}
		for (long ancestor : concept.getAncestorIds()) {
			ancestors.add(Long.toString(ancestor));
		}
		for (long parent : concept.getStatedParentIds()) {
			ancestors.add(Long.toString(parent));
		}
		for (long ancestor : concept.getStatedAncestorIds()) {
			ancestors.add(Long.toString(ancestor));
		}
		return ancestors;
	};

	/**
	 * Helper function to get only direct parents of a given {@link SnomedConcept}, both stated and inferred.
	 */
	public static final Function<SnomedConcept, Set<String>> GET_PARENTS = (concept) -> {
		final Set<String> ancestors = newHashSet();
		for (long parent : concept.getParentIds()) {
			ancestors.add(Long.toString(parent));
		}
		for (long parent : concept.getStatedParentIds()) {
			ancestors.add(Long.toString(parent));
		}
		return ancestors;
	};

	private SnomedConcept definitionStatus;
	private SubclassDefinitionStatus subclassDefinitionStatus;
	private SnomedDescription fsn;
	private SnomedDescription pt;
	private SnomedDescriptions descriptions;
	private SnomedDescriptions preferredDescriptions;
	private SnomedRelationships relationships;
	private SnomedRelationships inboundRelationships;
	private SnomedConcepts ancestors;
	private SnomedConcepts descendants;
	private SnomedConcepts statedAncestors;
	private SnomedConcepts statedDescendants;
	private long[] ancestorIds;
	private long[] parentIds;
	private long[] statedAncestorIds;
	private long[] statedParentIds;
	private SnomedReferenceSet referenceSet;

	public SnomedConcept() {
	}
	
	public SnomedConcept(String id) {
		setId(id);
	}
	
	@Override
	public short getTerminologyComponentId() {
		return SnomedTerminologyComponentConstants.CONCEPT_NUMBER;
	}
	
	public SnomedConcept getDefinitionStatus() {
		return definitionStatus;
	}
	
	public String getDefinitionStatusId() {
		return getDefinitionStatus() == null ? null : getDefinitionStatus().getId();
	}

	/**
	 * Returns the subclass definition status of the concept.
	 * 
	 * @return {@link SubclassDefinitionStatus#DISJOINT_SUBCLASSES} if the subclasses form a disjoint union,
	 *         {@link SubclassDefinitionStatus#NON_DISJOINT_SUBCLASSES} otherwise
	 */
	public SubclassDefinitionStatus getSubclassDefinitionStatus() {
		return subclassDefinitionStatus;
	}

	/**
	 * Returns the descriptions of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	public SnomedDescriptions getDescriptions() {
		return descriptions;
	}
	
	/**
	 * Returns the preferred descriptions (FSN and Synonyms) of the SNOMED CT Concept in creation order.
	 * 
	 * @return
	 */
	public SnomedDescriptions getPreferredDescriptions() {
		return preferredDescriptions;
	}

	/**
	 * Returns the relationships of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	public SnomedRelationships getRelationships() {
		return relationships;
	}
	
	/**
	 * @return the inbound relationships of the SNOMED CT Concept.
	 */
	public SnomedRelationships getInboundRelationships() {
		return inboundRelationships;
	}

	/**
	 * Returns the fully specified name of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	public SnomedDescription getFsn() {
		return fsn;
	}

	/**
	 * Returns the preferred term of the SNOMED CT Concept.
	 * 
	 * @return
	 */
	public SnomedDescription getPt() {
		return pt;
	}

	/**
	 * @return the inferred ancestors of the SNOMED CT concept
	 */
	public SnomedConcepts getAncestors() {
		return ancestors;
	}
	
	/**
	 * @return the stated ancestors of the SNOMED CT concept
	 */
	public SnomedConcepts getStatedAncestors() {
		return statedAncestors;
	}

	/**
	 * @return the inferred descendants of the SNOMED CT concept
	 */
	public SnomedConcepts getDescendants() {
		return descendants;
	}
	
	/**
	 * @return the stated descendants of the SNOMED CT concept
	 */
	public SnomedConcepts getStatedDescendants() {
		return statedDescendants;
	}

	/**
	 * @return the concept IDs of the ancestors
	 */
	@JsonIgnore
	public long[] getAncestorIds() {
		return ancestorIds;
	}
	
	/**
	 * @return the concept IDs of the ancestors as String values
	 */
	@JsonProperty("ancestorIds")
	public List<String> getAncestorIdsAsString() {
		return ancestorIds == null ? null : Arrays.stream(ancestorIds).mapToObj(Long::toString).collect(Collectors.toList());
	}

	/**
	 * @return the concept IDs of the parents
	 */
	@JsonIgnore
	public long[] getParentIds() {
		return parentIds;
	}
	
	/**
	 * @return the concept IDs of the parents as String values
	 */
	@JsonProperty("parentIds")
	public List<String> getParentIdsAsString() {
		return parentIds == null ? null : Arrays.stream(parentIds).mapToObj(Long::toString).collect(Collectors.toList());
	}

	/**
	 * @return the concept IDs of the stated ancestors
	 */
	@JsonIgnore
	public long[] getStatedAncestorIds() {
		return statedAncestorIds;
	}

	/**
	 * @return the concept IDs of the stated ancestors as String values
	 */
	@JsonProperty("statedAncestorIds")
	public List<String> getStatedAncestorIdsAsString() {
		return statedAncestorIds == null ? null : Arrays.stream(statedAncestorIds).mapToObj(Long::toString).collect(Collectors.toList());
	}
	
	/**
	 * @return the concept IDs of the stated parents
	 */
	@JsonIgnore
	public long[] getStatedParentIds() {
		return statedParentIds;
	}
	
	/**
	 * @return the concept IDs of the stated parents as String values
	 */
	@JsonProperty("statedParentIds")
	public List<String> getStatedParentIdsAsString() {
		return statedParentIds == null ? null : Arrays.stream(statedParentIds).mapToObj(Long::toString).collect(Collectors.toList());
	}
	
	public void setDefinitionStatus(SnomedConcept definitionStatus) {
		this.definitionStatus = definitionStatus;
	}
	
	public void setDefinitionStatusId(String definitionStatusId) {
		setDefinitionStatus(new SnomedConcept(definitionStatusId));
	}

	public void setSubclassDefinitionStatus(SubclassDefinitionStatus subclassDefinitionStatus) {
		this.subclassDefinitionStatus = subclassDefinitionStatus;
	}

	public void setDescriptions(SnomedDescriptions descriptions) {
		this.descriptions = descriptions;
	}
	
	public void setPreferredDescriptions(SnomedDescriptions preferredDescriptions) {
		this.preferredDescriptions = preferredDescriptions;
	}
	
	public void setRelationships(SnomedRelationships relationships) {
		this.relationships = relationships;
	}
	
	public void setInboundRelationships(SnomedRelationships inboundRelationships) {
		this.inboundRelationships = inboundRelationships;
	}
	
	public void setFsn(SnomedDescription fsn) {
		this.fsn = fsn;
	}
	
	public void setPt(SnomedDescription pt) {
		this.pt = pt;
	}
	
	public void setAncestors(SnomedConcepts ancestors) {
		this.ancestors = ancestors;
	}
	
	public void setDescendants(SnomedConcepts descendants) {
		this.descendants = descendants;
	}
	
	public void setStatedAncestors(SnomedConcepts statedAncestors) {
		this.statedAncestors = statedAncestors;
	}
	
	public void setStatedDescendants(SnomedConcepts statedDescendants) {
		this.statedDescendants = statedDescendants;
	}
	
	@JsonIgnore
	public void setAncestorIds(final long[] ancestorIds) {
		this.ancestorIds = ancestorIds;
	}
	
	@JsonProperty
	public void setAncestorIds(final List<String> ancestorIds) {
		this.ancestorIds = ancestorIds == null ? null : ancestorIds.stream().mapToLong(Long::parseLong).toArray();
	}
	
	@JsonIgnore
	public void setParentIds(final long[] parentIds) {
		this.parentIds = parentIds;
	}
	
	@JsonProperty
	public void setParentIds(final List<String> parentIds) {
		this.parentIds = parentIds == null ? null : parentIds.stream().mapToLong(Long::parseLong).toArray();
	}
	
	@JsonIgnore
	public void setStatedAncestorIds(final long[] statedAncestorIds) {
		this.statedAncestorIds = statedAncestorIds;
	}
	
	@JsonProperty
	public void setStatedAncestorIds(final List<String> statedAncestorIds) {
		this.statedAncestorIds = statedAncestorIds == null ? null : statedAncestorIds.stream().mapToLong(Long::parseLong).toArray();
	}
	
	@JsonIgnore
	public void setStatedParentIds(final long[] statedParentIds) {
		this.statedParentIds = statedParentIds;
	}
	
	@JsonProperty
	public void setStatedParentIds(final List<String> statedParentIds) {
		this.statedParentIds = statedParentIds == null ? null : statedParentIds.stream().mapToLong(Long::parseLong).toArray();
	}
	
	public void setReferenceSet(SnomedReferenceSet referenceSet) {
		this.referenceSet = referenceSet;
	}
	
	public SnomedReferenceSet getReferenceSet() {
		return referenceSet;
	}

	@JsonIgnore
	public boolean isPrimitive() {
		return Concepts.PRIMITIVE.equals(getDefinitionStatusId());
	}
	
	@Override
	public Request<TransactionContext, String> toCreateRequest(String containerId) {
		return SnomedRequests.prepareNewConcept()
				.setActive(isActive())
				.addMembers(getMembers())
				.addRelationships(getRelationships())
				.addDescriptions(getDescriptions())
				.setDefinitionStatusId(getDefinitionStatusId())
				.setId(getId())
				.setModuleId(getModuleId())
				.setSubclassDefinitionStatus(getSubclassDefinitionStatus())
				.build();
	}
	
	@Override
	public Request<TransactionContext, Boolean> toUpdateRequest() {
		return SnomedRequests.prepareUpdateConcept(getId())
				.setActive(isActive())
				.setInactivationProperties(getInactivationProperties())
				.setDefinitionStatusId(getDefinitionStatusId())
				.setModuleId(getModuleId())
				.setSubclassDefinitionStatus(getSubclassDefinitionStatus())
				.setDescriptions(getDescriptions())
				.setRelationships(getRelationships())
				.setMembers(getMembers())
				.build();
	}
	
	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedConcept [isActive()=");
		builder.append(isActive());
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getId()=");
		builder.append(getId());
		builder.append(", isReleased()=");
		builder.append(isReleased());
		builder.append(", getDefinitionStatusId()=");
		builder.append(getDefinitionStatusId());
		builder.append(", getSubclassDefinitionStatus()=");
		builder.append(getSubclassDefinitionStatus());
		builder.append(", getInactivationProperties()=");
		builder.append(getInactivationProperties());
		builder.append("]");
		return builder.toString();
	}

}
