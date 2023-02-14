/*
 * Copyright 2020-2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.core;

import java.util.function.Function;

import com.b2international.snowowl.snomed.core.domain.SnomedConcept;
import com.google.common.base.Strings;

/**
 * @since 7.10.0
 */
public enum SnomedDisplayTermType {
	
	FSN(
		"Fully specified name", 
		SnomedConcept.Expand.FULLY_SPECIFIED_NAME + "()",
		concept -> concept.getFsn() != null ? concept.getFsn().getTerm() : concept.getId()
	),
	
	PT(
		"Preferred term", 
		SnomedConcept.Expand.PREFERRED_TERM + "()",
		concept -> concept.getPt() != null ? concept.getPt().getTerm() : concept.getId()
	),
	
	ID_ONLY(
		"ID", 
		"",
		SnomedConcept::getId
	);
	
	private final String label;
	private final String expand;
	private final Function<SnomedConcept, String> getLabel;
	
	private SnomedDisplayTermType(final String label, final String expand, final Function<SnomedConcept, String> getLabel) {
		this.label = label;
		this.expand = expand;
		this.getLabel = getLabel;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getExpand() {
		return expand;
	}
	
	public String getLabel(SnomedConcept concept) {
		return getLabel.apply(concept);
	}
	
	@Override
	public String toString() {
		return label;
	}

	public static SnomedDisplayTermType getEnum(final String value) {
		try {
			return SnomedDisplayTermType.valueOf(Strings.nullToEmpty(value).toUpperCase());
		} catch (Exception e) {
			return ID_ONLY;
		}
	}
	
}
