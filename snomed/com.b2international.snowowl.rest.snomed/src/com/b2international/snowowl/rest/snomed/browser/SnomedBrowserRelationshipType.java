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
package com.b2international.snowowl.rest.snomed.browser;

public class SnomedBrowserRelationshipType implements ISnomedBrowserRelationshipType {

	private String conceptId;
	private String fsn;

	public SnomedBrowserRelationshipType() {
	}

	public SnomedBrowserRelationshipType(String conceptId) {
		this.conceptId = conceptId;
	}

	@Override
	public String getConceptId() {
		return conceptId;
	}

	@Override
	public String getFsn() {
		return fsn;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	public void setFsn(final String fsn) {
		this.fsn = fsn;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedBrowserRelationshipType [conceptId=");
		builder.append(conceptId);
		builder.append(", fsn=");
		builder.append(fsn);
		builder.append("]");
		return builder.toString();
	}
}
