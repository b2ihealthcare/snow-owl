/*
 * Copyright 2023 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.fhir.core.model.r5.datatype;

import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.base.DataType;

/**
 * A CodeableConcept represents a value that is usually supplied by providing a
 * reference to one or more terminologies or ontologies but may also be defined
 * by the provision of text.
 *
 * @see <a href="https://hl7.org/fhir/R5/datatypes.html#codeableconcept">2.1.28.0.5 CodeableConcept</a>
 * @since 9.0
 */
public class CodeableConcept extends DataType {

	/** Code defined by a terminology system */
	@Summary
	private List<Coding> coding;
	
	/** Plain text representation of the concept */
	@Summary
	private String text;

	public List<Coding> getCoding() {
		return coding;
	}

	public void setCoding(List<Coding> coding) {
		this.coding = coding;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
