/*
 * Copyright 2018 B2i Healthcare Pte Ltd, http://b2i.sg
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

import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.Uri;

/**
 * Internal FHIR code system
 * 
 * @see <a href="http://hl7.org/fhir/terminologies-systems.html">FHIR:Terminology:Code Systems</a>
 * @since 6.4
 */
public interface FhirCodeSystem {
	
	/**
	 * Returns the code system URI as a string of this code system
	 * @return
	 */
	String getCodeSystemUri();
	
	/**
	 * Returns the code from this code system.
	 * @return
	 */
	default Code getCode() {
		return new Code(getCodeValue());
	}
	
	/**
	 * Returns the code value of the code
	 * @return
	 */
	String getCodeValue();
	
	/**
	 * Returns the human-readable display name for the code
	 * @return
	 */
	String getDisplayName();
	
	/**
	 * Returns the full URI of the code. By default this is in the format of `{@link #getCodeSystemUri() uri}/{@link #getCodeValue() value}`.
	 * @return
	 */
	default Uri getUri() {
		return new Uri(String.format("%s/%s", getCodeSystemUri(), getCodeValue()));
	}

	/**
	 * @return the version tag of the code system
	 */
	default String getVersion() {
		return "3.0.1";
	}

}
