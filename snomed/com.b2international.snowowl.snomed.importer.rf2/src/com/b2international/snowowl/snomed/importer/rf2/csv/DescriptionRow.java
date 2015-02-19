/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.importer.rf2.csv;

/**
 * Represents a description release file row. The class provides storage for the following 
 * CSV fields:
 * <ul>
 * <li>{@code conceptId}
 * <li>{@code languageCode}
 * <li>{@code typeId}
 * <li>{@code term}
 * <li>{@code caseSignificanceId}
 * </ul> 
 *
 */
public class DescriptionRow extends AbstractTerminologyComponentRow {

	public static final String PROP_CONCEPT_ID = "conceptId";
	public static final String PROP_LANGUAGE_CODE = "languageCode";
	public static final String PROP_TYPE_ID = "typeId";
	public static final String PROP_TERM = "term";
	public static final String PROP_CASE_SIGNIFICANCE_ID = "caseSignificanceId";
	
	private String conceptId;
	private String languageCode;
	private String typeId;
	private String term;
	private String caseSignificanceId;
	
	public String getConceptId() {
		return conceptId;
	}
	
	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}
	
	public String getLanguageCode() {
		return languageCode;
	}
	
	public void setLanguageCode(final String languageCode) {
		this.languageCode = languageCode;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	public void setTypeId(final String typeId) {
		this.typeId = typeId;
	}
	
	public String getTerm() {
		return term;
	}
	
	public void setTerm(final String term) {
		this.term = term;
	}
	
	public String getCaseSignificanceId() {
		return caseSignificanceId;
	}
	
	public void setCaseSignificanceId(final String caseSignificanceId) {
		this.caseSignificanceId = caseSignificanceId;
	}

	@Override
	public String toString() {
		return String.format("DescriptionRow [id=%s, effectiveTime=%s, active=%s, moduleId=%s, conceptId=%s, languageCode=%s, typeId=%s, " +
				"term=%s, caseSignificanceId=%s]",
				getId(), getEffectiveTime(), isActive(), getModuleId(), getConceptId(), getLanguageCode(), getTypeId(),
				getTerm(), getCaseSignificanceId());
	}
}