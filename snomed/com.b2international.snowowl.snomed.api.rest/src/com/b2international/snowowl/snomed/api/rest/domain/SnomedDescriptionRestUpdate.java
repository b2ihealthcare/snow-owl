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
package com.b2international.snowowl.snomed.api.rest.domain;

import java.util.Map;

import com.b2international.snowowl.snomed.api.domain.Acceptability;
import com.b2international.snowowl.snomed.api.domain.CaseSignificance;
import com.b2international.snowowl.snomed.api.impl.domain.SnomedDescriptionUpdate;

/**
 * @since 1.0
 */
public class SnomedDescriptionRestUpdate extends AbstractSnomedComponentRestUpdate<SnomedDescriptionUpdate> {

	private CaseSignificance caseSignificance;
	private Map<String, Acceptability> acceptability;

	/**
	 * @return
	 */
	public CaseSignificance getCaseSignificance() {
		return caseSignificance;
	}

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
	protected SnomedDescriptionUpdate createComponentUpdate() {
		return new SnomedDescriptionUpdate();
	}

	/**
	 * @return
	 */
	@Override
	public SnomedDescriptionUpdate toComponentUpdate() {
		final SnomedDescriptionUpdate result = super.toComponentUpdate();
		result.setCaseSignificance(getCaseSignificance());
		result.setAcceptability(getAcceptability());
		return result;
	}
}