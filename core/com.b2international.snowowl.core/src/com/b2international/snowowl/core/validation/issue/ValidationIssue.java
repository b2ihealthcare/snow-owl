/*
 * Copyright 2017-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
import com.b2international.index.Keyword;
import com.b2international.index.Script;
import com.b2international.index.Text;
import com.b2international.snowowl.core.ComponentIdentifier;
import com.b2international.snowowl.core.uri.CodeSystemURI;
import com.b2international.snowowl.core.uri.ComponentURI;
import com.b2international.snowowl.core.validation.whitelist.ValidationWhiteList;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

/**
 * @since 6.0
 */
@Doc
@Script(name = ValidationIssue.Scripts.WHITELIST, script="ctx._source.whitelisted = params.whitelisted")
@Script(name="normalizeWithOffset", script="(_score / (_score + 1.0f)) + params.offset")
public final class ValidationIssue implements Serializable {

	private static final long serialVersionUID = 5674287017882543560L;

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
		public static final String AFFECTED_COMPONENT_LABELS_PREFIX = AFFECTED_COMPONENT_LABELS + ".prefix";
		public static final String AFFECTED_COMPONENT_LABELS_ORIGINAL= AFFECTED_COMPONENT_LABELS + ".original";
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
	private final CodeSystemURI resourceURI;
	private final boolean whitelisted;
	
	
	private final String affectedComponentId;

	/**
	 * @deprecated - kept only to support clear migration path for older indices, will be removed in 8.0
	 */
	private final String branchPath;
	
	/**
	 * @deprecated - kept only to support clear migration path for older indices, will be removed in 8.0
	 */
	private final short affectedComponentType;
	
	
	@Text(analyzer = Analyzers.TOKENIZED)
	@Text(alias="prefix", analyzer = Analyzers.PREFIX, searchAnalyzer = Analyzers.TOKENIZED)
	@Keyword(alias="original")
	private List<String> affectedComponentLabels = Collections.emptyList();
	
	private Map<String, Object> details = null;
	
	@JsonCreator
	/*package*/ ValidationIssue(
			@JsonProperty("id") final String id,
			@JsonProperty("ruleId") final String ruleId, 
			@JsonProperty("branchPath") final String branchPath, 
			@JsonProperty("affectedComponentURI") final ComponentURI affectedComponentURI,
			@JsonProperty("resourceURI") final CodeSystemURI resourceURI,
			@JsonProperty("affectedComponentType") final short affectedComponentType,
			@JsonProperty("affectedComponentId") final String affectedComponentId,
			@JsonProperty("whitelisted") final boolean whitelisted) {
		this.id = id;
		this.ruleId = ruleId;
		this.affectedComponentId = affectedComponentId;
		this.affectedComponentURI = affectedComponentURI;
		this.resourceURI = resourceURI;
		this.whitelisted = whitelisted;
		this.branchPath = branchPath;
		this.affectedComponentType = affectedComponentType;
	}
	
	public ValidationIssue(
			final String id,
			final String ruleId, 
			final ComponentURI componentURI, 
			final boolean whitelisted) {
		this(id, ruleId, null, componentURI, componentURI.codeSystemUri(), componentURI.terminologyComponentId(), componentURI.identifier(), whitelisted);
	}
	
	public String getId() {
		return id;
	}
	
	/**
	 * @deprecated - kept only to support clear migration path for older indices, will be removed in 8.0
	 */
	@JsonProperty
	public String getBranchPath() {
		return branchPath;
	}
	
	/**
	 * @return the component identifier that has been flagged by this issue, never <code>null</code>.
	 */
	@JsonProperty
	/*package*/ String getAffectedComponentId() {
		return affectedComponentId;
	}
	
	/**
	 * @deprecated - kept only to support clear migration path for older indices, will be removed in 8.0
	 */
	@JsonProperty
	short getAffectedComponentType() {
		return affectedComponentType;
	}
	
	/**
	 * @return the {@link ComponentIdentifier} part from the {@link ComponentURI}, never <code>null</code>.
	 */
	@JsonIgnore
	public ComponentIdentifier getAffectedComponent() {
		if (getAffectedComponentURI() == null) {
			return ComponentIdentifier.of(affectedComponentType, affectedComponentId);
		} else {
			return getAffectedComponentURI().toComponentIdentifier();
		}
	}
	
	/**
	 * @return the full URI of the component that has been flagged by this issue, never <code>null</code>.
	 */
	public ComponentURI getAffectedComponentURI() {
		return affectedComponentURI;
	}
	
	/**
	 * @return the resourceURI (currently only {@link CodeSystemURI} is supported) that marks the location of this issue, never <code>null</code>.
	 */
	public CodeSystemURI getResourceURI() {
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
