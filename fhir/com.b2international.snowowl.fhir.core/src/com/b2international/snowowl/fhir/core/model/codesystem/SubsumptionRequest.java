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
package com.b2international.snowowl.fhir.core.model.codesystem;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * @since 6.4
 */
@JsonDeserialize(builder = SubsumptionRequest.Builder.class)
@JsonPropertyOrder({"codeA", "codeB", "system", "version", "codingA", "codingB"})
public class SubsumptionRequest {

	/**
	 * The "A" code that is to be tested. If a code is provided, a system must be provided
	 */
	private final String codeA;
	
	/**
	 * The "B" code that is to be tested. If a code is provided, a system must be provided
	 */
	private final String codeB;
	
	/**
	 * The code system in which subsumption testing is to be performed. This must be provided unless the operation is invoked on a code system instance
	 */
	private final String system;
	
	private final String version;
	
	/**
	 * The "A" Coding that is to be tested. 
	 * The code system does not have to match the specified subsumption code system, but the relationships between the code systems must be well established
	 */
	private final Coding codingA;
	
	/**
	 * The "B" Coding that is to be tested. The code system does not have to match the specified subsumption code system, but the relationships between the code systems must be well established
	 */
	private final Coding codingB;
	
	private SubsumptionRequest(String system, String version, String codeA, String codeB, Coding codingA, Coding codingB) {
		this.system = system;
		this.version = version;
		this.codeA = codeA;
		this.codeB = codeB;
		this.codingA = codingA;
		this.codingB = codingB;
	}

	public String getSystem() {
		return system;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getCodeA() {
		return codeA;
	}
	
	public String getCodeB() {
		return codeB;
	}
	
	public Coding getCodingA() {
		return codingA;
	}
	
	public Coding getCodingB() {
		return codingB;
	}
	
	public static Builder builder() {
		return new Builder();
	}
	
	@JsonPOJOBuilder(withPrefix = "")
	public static final class Builder extends ValidatingBuilder<SubsumptionRequest> {
		
		private String system;
		private String version;
		private String codeA;
		private String codeB;
		private Coding codingA;
		private Coding codingB;
		
		public Builder system(String system) {
			this.system = system;
			return this;
		}
		
		public Builder version(String version) {
			this.version = version;
			return this;
		}
		
		public Builder codeA(String codeA) {
			this.codeA = codeA;
			return this;
		}
		
		public Builder codeB(String codeB) {
			this.codeB = codeB;
			return this;
		}
		
		public Builder codingA(Coding codingA) {
			this.codingA = codingA;
			return this;
		}
		
		public Builder codingB(Coding codingB) {
			this.codingB = codingB;
			return this;
		}
		
		@Override
		protected SubsumptionRequest doBuild() {
			return new SubsumptionRequest(system, version, codeA, codeB, codingA, codingB);
		}
		
	}

}
