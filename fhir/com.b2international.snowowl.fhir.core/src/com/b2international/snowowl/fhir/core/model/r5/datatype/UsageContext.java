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

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.base.DataType;

/**
 * @since 9.0
 */
public class UsageContext extends DataType {

	@Summary
	private Coding code;
	
	@Summary
	private CodeableConcept valueCodeableConcept;
	
	@Summary
	private Quantity valueQuantity;
	
	@Summary
	private Range valueRange;
	
	@Summary
	private Reference valueReference;

	public Coding getCode() {
		return code;
	}

	public void setCode(Coding code) {
		this.code = code;
	}

	public CodeableConcept getValueCodeableConcept() {
		return valueCodeableConcept;
	}

	public void setValueCodeableConcept(CodeableConcept valueCodeableConcept) {
		this.valueCodeableConcept = valueCodeableConcept;
	}

	public Quantity getValueQuantity() {
		return valueQuantity;
	}

	public void setValueQuantity(Quantity valueQuantity) {
		this.valueQuantity = valueQuantity;
	}

	public Range getValueRange() {
		return valueRange;
	}

	public void setValueRange(Range valueRange) {
		this.valueRange = valueRange;
	}

	public Reference getValueReference() {
		return valueReference;
	}

	public void setValueReference(Reference valueReference) {
		this.valueReference = valueReference;
	}
}
