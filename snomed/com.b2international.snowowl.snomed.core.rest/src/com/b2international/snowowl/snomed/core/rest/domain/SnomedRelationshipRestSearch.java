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

import java.util.List;

import com.b2international.snowowl.core.request.SearchResourceRequest;
import com.b2international.snowowl.snomed.core.domain.RelationshipValueType;

import io.swagger.v3.oas.annotations.Parameter;

/**
 * @since 6.16
 */
public final class SnomedRelationshipRestSearch extends SnomedComponentRestSearch {

	@Parameter(description = "The source concept(s) or ECL expression to match")
	private List<String> source;

	@Parameter(description = "The type concept(s) or ECL expression to match")
	private List<String> type;

	@Parameter(description = "The destination concept(s) or ECL expression to match")
	private List<String> destination;
	
	@Parameter(description = "The value type to match")
	private RelationshipValueType valueType;

	@Parameter(description = "The value comparison operator")
	private SearchResourceRequest.Operator operator;

	@Parameter(description = "The value to match (in literal form)")
	private String value;

	@Parameter(description = "The characteristic type to match")
	private String characteristicType;

	@Parameter(description = "The group to match")
	private Integer group;

	@Parameter(description = "The union group to match")
	private Integer unionGroup;

	public List<String> getSource() {
		return source;
	}

	public void setSource(List<String> source) {
		this.source = source;
	}

	public List<String> getType() {
		return type;
	}

	public void setType(List<String> type) {
		this.type = type;
	}

	public List<String> getDestination() {
		return destination;
	}

	public void setDestination(List<String> destination) {
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
