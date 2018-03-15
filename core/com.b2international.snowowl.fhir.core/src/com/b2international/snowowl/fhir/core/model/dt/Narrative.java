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
package com.b2international.snowowl.fhir.core.model.dt;

import com.b2international.snowowl.fhir.core.codesystems.NarrativeStatus;

/**
 * FHIR Narrative datatype
 * 
 * Narrative represents the content of the resource to a human.
 * @since 6.3
 */
public class Narrative {
	
	//"status" : "<cod >", // R!  generated | extensions | additional | empty
	private Code status;
	
	//"div" : "(Escaped XHTML)" // R!  Limited xhtml content
	private String div;
	
	public Narrative(NarrativeStatus status, String div) {
		this.status = status.getCode();
		this.div = div;
	}
	
	public Code getStatus() {
		return status;
	}
	
	public String getDiv() {
		return div;
	}

	@Override
	public String toString() {
		return "Narrative [status=" + status + ", div=" + div + "]";
	}
	
}
