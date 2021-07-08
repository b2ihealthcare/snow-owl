/*
 * Copyright 2019-2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.domain;

import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.core.rest.domain.ObjectRestSearch;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * @since 6.16
 */
public final class SnomedRelationshipRestSearch extends ObjectRestSearch {

	@Schema(description = "The effective time to match (yyyyMMdd, exact matches only)")
	private String effectiveTime;

	@Schema(description = "The status to match")
	private Boolean active;

	@Schema(description = "The module identifier to match")
	private String module;

	@Schema(description = "The namespace to match")
	private String namespace;

	@Schema(description = "The source concept to match")
	private String source;

	@Schema(description = "The type concept to match")
	private String type;

	@Schema(description = "The destination concept to match")
	private String destination;
	
	@Schema(description = "The value type to match")
	private RelationshipValueType valueType;

	@Schema(description = "The value comparison operator")
	private SearchResourceRequest.Operator operator;

	@Schema(description = "The value to match (in literal form)")
	private String value;

	@Schema(description = "The characteristic type to match")
	private String characteristicType;

	@Schema(description = "The group to match")
	private Integer group;

	@Schema(description = "The union group to match")
	private Integer unionGroup;

	public String getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(String effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public RelationshipValueType getValueType() {
		return valueType;
	}

	public void setValueType(RelationshipValueType valueType) {
		this.valueType = valueType;
	}

	public SearchResourceRequest.Operator getOperator() {
		return operator;
	}

	public void setOperator(SearchResourceRequest.Operator operator) {
		this.operator = operator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCharacteristicType() {
		return characteristicType;
	}

	public void setCharacteristicType(String characteristicType) {
		this.characteristicType = characteristicType;
	}

	public Integer getGroup() {
		return group;
	}

	public void setGroup(Integer group) {
		this.group = group;
	}

	public Integer getUnionGroup() {
		return unionGroup;
	}

	public void setUnionGroup(Integer unionGroup) {
		this.unionGroup = unionGroup;
	}
}
