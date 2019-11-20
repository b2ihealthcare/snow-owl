/*
 * Copyright 2019 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.codesystems;

import static com.google.common.base.Preconditions.checkNotNull;

import com.b2international.commons.StringUtils;
import com.b2international.snowowl.fhir.core.model.codesystem.Concept;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @since 7.2
 */
public interface FhirInternalCode {

	/**
	 * @return the {@link Code} with value of {@link #getCodeValue()}.
	 */
	default Code getCode() {
		return new Code(getCodeValue());
	}
	
	/**
	 * @return the code value of the code
	 */
	default String getCodeValue() {
		return name().toLowerCase();
	}
	
	/**
	 * @return the name of the internal code. NOTE: should be an enum constant.
	 */
	String name(); 
	
	/**
	 * @return the human-readable display name for the code
	 */
	default String getDisplayName() {
		return StringUtils.capitalizeFirstLetter(getCodeValue());
	}
	
	/**
	 * Returns the full URI of the code. By default this is in the format of `{@link #getCodeSystemUri() uri}/{@link #getCodeValue() value}`.
	 * @return
	 */
	default Uri getUri() {
		return new Uri(String.format("%s/%s", getCodeSystem().uri(), getCodeValue()));
	}
	
	/**
	 * @return the codesystem for this internal FHIR code.
	 */
	@JsonIgnore
	default FhirInternalCodeSystem getCodeSystem() {
		return checkNotNull(getClass().getAnnotation(FhirInternalCodeSystem.class), "Type "+ getClass().getSimpleName() +" must be annotated with " + FhirInternalCodeSystem.class.getSimpleName() + " in order to be designated as internal code system.");
	}

	@JsonIgnore
	default Concept toConcept() {
		return Concept.builder()
				.code(getCodeValue())
				.display(getDisplayName())
				.build();
	}
	
}
