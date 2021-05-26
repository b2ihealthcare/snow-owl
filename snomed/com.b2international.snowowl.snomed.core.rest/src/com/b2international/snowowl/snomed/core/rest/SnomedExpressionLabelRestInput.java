/*
 * Copyright 2021 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest;

import java.util.List;

import io.swagger.annotations.ApiParam;

/**
 * @since 7.17.0
 */
public class SnomedExpressionLabelRestInput {
	
	@ApiParam
	private List<String> expressions;
	@ApiParam(defaultValue = "fsn", example = "{fsn|pt}")
	private String descriptionType;
	
	public List<String> getExpressions() {
		return expressions;
	}
	
	public void setExpressions(List<String> expressions) {
		this.expressions = expressions;
	}
	
	public String getDescriptionType() {
		return descriptionType;
	}
	
	public void setDescriptionType(String descriptionType) {
		this.descriptionType = descriptionType;
	}

}