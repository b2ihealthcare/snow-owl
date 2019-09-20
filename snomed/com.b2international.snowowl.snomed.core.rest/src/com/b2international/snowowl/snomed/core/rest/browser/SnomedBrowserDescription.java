/*
 * Copyright 2011-2016 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core.rest.browser;

import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;

public class SnomedBrowserDescription extends SnomedBrowserComponent implements ISnomedBrowserDescription {

	private String descriptionId;
	private String conceptId;
	private SnomedBrowserDescriptionType type;
	private String lang;
	private String term;
	private CaseSignificance caseSignificance;
	private Map<String, Acceptability> acceptabilityMap;

	@Override
	public String getId() {
		return descriptionId;
	}

	@Override
	public String getDescriptionId() {
		return descriptionId;
	}

	@Override
	public String getConceptId() {
		return conceptId;
	}

	@Override
	public SnomedBrowserDescriptionType getType() {
		return type;
	}

	@Override
	public String getLang() {
		return lang;
	}

	@Override
	public String getTerm() {
		return term;
	}

	@Override
	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	@Override
	public Map<String, Acceptability> getAcceptabilityMap() {
		return acceptabilityMap;
	}

	public void setDescriptionId(final String descriptionId) {
		this.descriptionId = descriptionId;
	}

	public void setConceptId(final String conceptId) {
		this.conceptId = conceptId;
	}

	public void setType(final SnomedBrowserDescriptionType type) {
		this.type = type;
	}

	public void setLang(final String lang) {
		this.lang = lang;
	}

	public void setTerm(final String term) {
		this.term = term;
	}

	public void setCaseSignificance(final CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public void setAcceptabilityMap(Map<String, Acceptability> acceptabilityMap) {
		this.acceptabilityMap = acceptabilityMap;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedBrowserDescription [descriptionId=");
		builder.append(descriptionId);
		builder.append(", conceptId=");
		builder.append(conceptId);
		builder.append(", type=");
		builder.append(type);
		builder.append(", lang=");
		builder.append(lang);
		builder.append(", term=");
		builder.append(term);
		builder.append(", caseSignificance=");
		builder.append(caseSignificance);
		builder.append(", acceptabilityMap=");
		builder.append(acceptabilityMap);
		builder.append(", getEffectiveTime()=");
		builder.append(getEffectiveTime());
		builder.append(", getModuleId()=");
		builder.append(getModuleId());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((descriptionId == null) ? 0 : descriptionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SnomedBrowserDescription other = (SnomedBrowserDescription) obj;
		if (descriptionId == null) {
			if (other.descriptionId != null)
				return false;
		} else if (!descriptionId.equals(other.descriptionId))
			return false;
		return true;
	}
	
}
