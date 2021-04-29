/*
 * Copyright 2017-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.validation.issue;

import static com.google.common.collect.Maps.newHashMap;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.b2international.commons.collections.Collections3;
import com.b2international.index.Analyzers;
import com.b2international.index.Doc;
import com.b2international.index.ID;
import com.b2international.index.Script;
import com.b2international.index.mapping.Field;
import com.b2international.index.mapping.FieldAlias;
import com.b2international.index.mapping.FieldAlias.FieldAliasType;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.ResourceURI;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.fasterxml.jackson.annotation.*;
import com.google.common.base.MoreObjects;

/**
 * @since 6.0
 */
@Doc
@Script(name = ValidationIssue.Scripts.WHITELIST, script="ctx._source.whitelisted = params.whitelisted")
public final class ValidationIssue implements Serializable {

	private static final long serialVersionUID = 2L;

	/**
	 * @since 6.0
	 */
	public static class Fields {
		public static final String ID = "id";
		public static final String RULE_ID = "ruleId";
		public static final String RESOURCE_URI = "resourceURI";
		public static final String AFFECTED_COMPONENT_ID = "affectedComponentId";
		public static final String AFFECTED_COMPONENT_URI = "affectedComponentURI";
		public static final String AFFECTED_COMPONENT_LABELS = "affectedComponentLabels";
		public static final String AFFECTED_COMPONENT_LABELS_TEXT = AFFECTED_COMPONENT_LABELS + ".text";
		public static final String AFFECTED_COMPONENT_LABELS_PREFIX = AFFECTED_COMPONENT_LABELS + ".prefix";
		public static final String WHITELISTED = "whitelisted";
		public static final String DETAILS = "details";
		
		/**
		 * @deprecated - kept only to support clear migration path for older indices, will be removed in 8.0
		 */
		public static final String BRANCH_PATH = "branchPath";
	}

	/**
	 * @since 6.0
	 */
	public static class Scripts {
		public static final String WHITELIST = "whitelist";
	}
	
	@ID
	private final String id;
	private final String ruleId;
	private final ComponentURI affectedComponentURI;
	private final String affectedComponentId;
	private final ResourceURI resourceURI;
	private final boolean whitelisted;

	@Field(aliases = {
		@FieldAlias(name = "text", type = FieldAliasType.TEXT, analyzer = Analyzers.TOKENIZED),
		@FieldAlias(name = "prefix", type = FieldAliasType.TEXT, analyzer = Analyzers.PREFIX, searchAnalyzer = Analyzers.TOKENIZED)
	})
	private List<String> affectedComponentLabels = Collections.emptyList();
	
	private Map<String, Object> details = null;
	
	@JsonCreator
	/*package*/ ValidationIssue(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId, 
			@JsonProperty("affectedComponentURI") final ComponentURI affectedComponentURI,
			@JsonProperty("resourceURI") final ResourceURI resourceURI,
			@JsonProperty("affectedComponentId") final String affectedComponentId,
			@JsonProperty("whitelisted") final boolean whitelisted) {
		this.id = id;
		this.ruleId = ruleId;
		this.affectedComponentId = affectedComponentId;
		this.affectedComponentURI = affectedComponentURI;
		this.resourceURI = resourceURI;
		this.whitelisted = whitelisted;
	}
	
	public ValidationIssue(
			final String id,
			final String ruleId, 
			final ComponentURI componentURI, 
			final boolean whitelisted) {
		this(id, ruleId, componentURI, componentURI.codeSystemUri(), componentURI.identifier(), whitelisted);
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * @return the component identifier that has been flagged by this issue, never <code>null</code>.
	 */
	@JsonProperty
	/*package*/ String getAffectedComponentId() {
		return affectedComponentId;
	}
	
	/**
	 * @return the {@link ComponentIdentifier} part from the {@link ComponentURI}, never <code>null</code>.
	 */
	@JsonIgnore
	public ComponentIdentifier getAffectedComponent() {
		return getAffectedComponentURI().toComponentIdentifier();
	}
	
	/**
	 * @return the full URI of the component that has been flagged by this issue, never <code>null</code>.
	 */
	public ComponentURI getAffectedComponentURI() {
		return affectedComponentURI;
	}
	
	/**
	 * @return the resourceURI that marks the location of this issue, never <code>null</code>.
	 */
	public ResourceURI getResourceURI() {
		return resourceURI;
	}
	
	/**
	 * @return the rule that has created this issue 
	 */
	public String getRuleId() {
		return ruleId;
	}
	
	/**
	 * @return <code>true</code> if this issue has been marked as whitelisted and has a corresponding {@link ValidationWhiteList} entry, <code>false</code> if there is no such entry. 
	 */
	public boolean isWhitelisted() {
		return whitelisted;
	}
	
	/**
	 * @return all the labels that are associated with this validation issue to support looking up the issue by searching via relevant terms
	 */
	public List<String> getAffectedComponentLabels() {
		return affectedComponentLabels;
	}
	
	public void setAffectedComponentLabels(List<String> affectedComponentLabels) {
		this.affectedComponentLabels = Collections3.toImmutableList(affectedComponentLabels);
	}
	
	/**
	 * @return any additional details that help the resolution of the issue
	 */
	@JsonAnyGetter
	public Map<String, Object> getDetails() {
		return details;
	}
	
	@JsonAnySetter
	public void setDetails(String key, Object value) {
		if (details == null) {
			this.details = newHashMap();
		}
		this.details.put(key, value);
	}
	
	public void setDetails(Map<String, Object> details) {
		this.details = details;
	}
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(getClass())
				.add("id", id)
				.add("ruleId", ruleId)
				.add("resourceURI", resourceURI)
				.add("affectedComponentURI", affectedComponentURI)
				.add("details", getDetails())
				.toString();
	}
	
}
