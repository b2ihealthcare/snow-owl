/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import java.util.Date;
import java.util.UUID;

/**
 * @since 1.0
 */
public class SnomedImportDetails extends SnomedImportRestConfiguration {

	private UUID id;
	private SnomedImportStatus status;
	private Date startDate;
	private Date completionDate;
	
	public UUID getId() {
		return id;
	}
	
	public SnomedImportStatus getStatus() {
		return status;
	}

	public void setId(final UUID id) {
		this.id = id;
	}
	
	public void setStatus(SnomedImportStatus status) {
		this.status = status;
	}
	
	public Date getCompletionDate() {
		return completionDate;
	}
	
	public void setCompletionDate(Date completionDate) {
		this.completionDate = completionDate;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedImportDetails [id=");
		builder.append(id);
		builder.append(", status=");
		builder.append(id);
		builder.append("]");
		return builder.toString();
	}
}
