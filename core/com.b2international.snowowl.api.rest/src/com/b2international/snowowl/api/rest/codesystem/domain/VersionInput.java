/*
 * Copyright 2011-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.api.rest.codesystem.domain;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.b2international.snowowl.datastore.CodeSystemVersionProperties;

/**
 * @since 1.0
 */
public class VersionInput implements CodeSystemVersionProperties {

	@NotEmpty
	private String version;
	private String description = "";
	
	@NotNull
	private Date effectiveDate;
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	
	@Override
	public String getVersion() {
		return version;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
}
