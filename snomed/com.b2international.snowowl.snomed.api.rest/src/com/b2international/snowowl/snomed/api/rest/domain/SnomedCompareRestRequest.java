/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.api.rest.domain;

import org.hibernate.validator.constraints.NotEmpty;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
/**
 * @since 7.0
 */
@ApiModel
public final class SnomedCompareRestRequest {
	
	@ApiModelProperty(required = true)
	@NotEmpty
	private String baseBranch;
	
	@ApiModelProperty(required = true)
	@NotEmpty
	private String compareBranch;
	
	@ApiModelProperty(required = false)
	private int limit = 50;
	
	public String getBaseBranch() {
		return baseBranch;
	}
	
	public void setBaseBranch(String baseBranch) {
		this.baseBranch = baseBranch;
	}
	
	public String getCompareBranch() {
		return compareBranch;
	}
	
	public void setCompareBranch(String compareBranch) {
		this.compareBranch = compareBranch;
	}
	
	public int getLimit() {
		return limit;
	}
	
	public void setLimit(int limit) {
		this.limit = limit;
	}

}
