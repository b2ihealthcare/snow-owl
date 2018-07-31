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

import java.util.List;

import com.b2international.snowowl.core.history.domain.IHistoryInfo;

/**
 * @since 1.0
 */
public class SnomedReferenceSetHistory {

	private List<IHistoryInfo> referenceSetHistory;

	public List<IHistoryInfo> getReferenceSetHistory() {
		return referenceSetHistory;
	}

	public void setReferenceSetHistory(final List<IHistoryInfo> referenceSetHistory) {
		this.referenceSetHistory = referenceSetHistory;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("SnomedReferenceSetHistory [referenceSetHistory=");
		builder.append(referenceSetHistory);
		builder.append("]");
		return builder.toString();
	}
}