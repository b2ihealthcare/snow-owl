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

import java.util.Date;

import com.b2international.snowowl.fhir.core.codesystems.IdentifierUse;
import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * FHIR Identifier datatype
 * 
 * @see <a href="https://www.hl7.org/fhir/datatypes.html#identifier">FHIR:Data Types:Identifier</a>
 * @since 6.3
 */
public class Identifier {

	@JsonProperty
	private Code use; //usual | official | temp | secondary (If known)
	
	//identifier type codes
	@JsonProperty
	private CodeableConcept type;
	
	@JsonProperty
	private Uri system;
	
	@JsonProperty
	private String value;

	@JsonProperty
	private Period period;
	
	@JsonProperty
	private Reference assigner;
	
	Identifier(Code identifierUseCode, CodeableConcept type, Uri system, String value, final Period period, final Reference assigner) {
		this.use = identifierUseCode;
		this.type = type;
		this.system = system;
		this.value = value;
		this.period = period;
		this.assigner = assigner;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	public static class Builder extends ValidatingBuilder<Identifier> {
		
		private Code use; //usual | official | temp | secondary (If known)
		private CodeableConcept type;
		private Uri system;
		private String value;
		private Period period;
		private Reference assigner;

		/**
		 * usual | official | temp | secondary (If known)
		 * @param identifierUse
		 * @return
		 */
		public Builder use(final IdentifierUse identifierUse) {
			this.use = identifierUse.getCode();
			return this;
		}
		
		public Builder type(final CodeableConcept type) {
			this.type = type;
			return this;
		}

		public Builder system(final String system) {
			this.system = new Uri(system);
			return this;
		}
		
		public Builder system(final Uri systemUri) {
			this.system = systemUri;
			return this;
		}

		
		public Builder value(final String value) {
			this.value = value;
			return this;
		}

		public Builder period(final Period period) {
			this.period = period;
			return this;
		}
		
		public Builder period(final Date startDate, final Date endDate) {
			this.period = new Period(startDate, endDate);
			return this;
		}
		
		public Builder assigner(final Reference assigner) {
			this.assigner = assigner;
			return this;
		}
		@Override
		protected Identifier doBuild() {
			return new Identifier(use, type, system, value, period, assigner);
		}
		
	}
	
}
