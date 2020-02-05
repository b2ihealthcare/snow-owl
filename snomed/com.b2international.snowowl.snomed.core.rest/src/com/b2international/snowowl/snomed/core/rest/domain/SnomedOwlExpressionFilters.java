/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import io.swagger.annotations.ApiParam;

/**
 * @since 6.16
 */
public final class SnomedOwlExpressionFilters {

	@ApiParam(value = "Special filter to match concept IDs in an owlExpression")
	private List<String> conceptId;
	@ApiParam(value = "Special filter to match destination IDs in an owlExpression")
	private List<String> destinationId;
	@ApiParam(value = "Special filter to match type IDs in an owlExpression")
	private List<String> typeId;
	@ApiParam(value = "Special filter to match GCI/non-GCI axioms")
	private Boolean gci;
	
	public List<String> getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(List<String> conceptId) {
		this.conceptId = conceptId;
	}
	
	public List<String> getTypeId() {
		return typeId;
	}
	
	public void setTypeId(List<String> typeId) {
		this.typeId = typeId;
	}
	
	public List<String> getDestinationId() {
		return destinationId;
	}
	
	public void setDestinationId(List<String> destinationId) {
		this.destinationId = destinationId;
	}
	
	public Boolean getGci() {
		return gci;
	}
	
	public void setGci(Boolean gci) {
		this.gci = gci;
	}
	
}
