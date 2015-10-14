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
package com.b2international.snowowl.snomed.api.impl.domain;

import java.util.Map;

import com.b2international.snowowl.snomed.core.domain.AbstractSnomedComponentUpdate;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.CaseSignificance;
import com.b2international.snowowl.snomed.core.domain.ISnomedDescriptionUpdate;

/**
 */
public class SnomedDescriptionUpdate extends AbstractSnomedComponentUpdate implements ISnomedDescriptionUpdate {

	private CaseSignificance caseSignificance;
	private Map<String, Acceptability> acceptability;

	@Override
	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

	@Override
	public Map<String, Acceptability> getAcceptability() {
		return acceptability;
	}

	public void setCaseSignificance(final CaseSignificance caseSignificance) {
		this.caseSignificance = caseSignificance;
	}

	public void setAcceptability(final Map<String, Acceptability> acceptability) {
		this.acceptability = acceptability;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedDescriptionUpdate [getModuleId()=");
		builder.append(getModuleId());
		builder.append(", getCaseSignificance()=");
		builder.append(getCaseSignificance());
		builder.append(", getAcceptability()=");
		builder.append(getAcceptability());
		builder.append(", isActive()=");
		builder.append(isActive());
		builder.append("]");
		return builder.toString();
	}
}