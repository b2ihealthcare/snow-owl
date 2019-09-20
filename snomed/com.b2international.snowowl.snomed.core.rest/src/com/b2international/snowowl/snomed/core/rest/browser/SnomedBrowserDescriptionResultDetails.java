/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.b2international.snowowl.snomed.core.rest.browser;

import com.b2international.snowowl.snomed.core.domain.DefinitionStatus;

public class SnomedBrowserDescriptionResultDetails implements ISnomedBrowserDescriptionResultDetails {

	private String conceptId;
	private String fsn;
	private boolean active;
	private String moduleId;
	private DefinitionStatus definitionStatus;

	@Override
	public String getConceptId() {
		return conceptId;
	}

	@Override
	public String getFsn() {
		return fsn;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public String getModuleId() {
		return moduleId;
	}

	@Override
	public DefinitionStatus getDefinitionStatus() {
		return definitionStatus;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	public void setFsn(final String fsn) {
		this.fsn = fsn;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public void setModuleId(final String moduleId) {
		this.moduleId = moduleId;
	}

	public void setDefinitionStatus(final DefinitionStatus definitionStatus) {
		this.definitionStatus = definitionStatus;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedBrowserDescriptionResultDetails [conceptId=");
		builder.append(conceptId);
		builder.append(", fsn=");
		builder.append(fsn);
		builder.append(", active=");
		builder.append(active);
		builder.append(", moduleId=");
		builder.append(moduleId);
		builder.append(", definitionStatus=");
		builder.append(definitionStatus);
		builder.append("]");
		return builder.toString();
	}
}
