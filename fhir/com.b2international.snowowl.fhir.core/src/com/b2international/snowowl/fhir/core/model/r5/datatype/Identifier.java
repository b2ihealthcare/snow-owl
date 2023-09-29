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

import com.b2international.snowowl.fhir.core.model.r5.ModifierElement;
import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.base.DataType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.UriType;

/**
 * @since 9.0
 */
public class Identifier extends DataType {

	@ModifierElement
	@Summary
	private CodeType use;
	
	@Summary
	private CodeableConcept type;
	
	@Summary
	private UriType system;
	
	@Summary
	private String value;
	
	@Summary
	private Period period;
	
	@Summary
	private Reference assigner;

	public CodeType getUse() {
		return use;
	}

	public void setUse(CodeType use) {
		this.use = use;
	}

	public CodeableConcept getType() {
		return type;
	}

	public void setType(CodeableConcept type) {
		this.type = type;
	}

	public UriType getSystem() {
		return system;
	}

	public void setSystem(UriType system) {
		this.system = system;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public Reference getAssigner() {
		return assigner;
	}

	public void setAssigner(Reference assigner) {
		this.assigner = assigner;
	}
}
