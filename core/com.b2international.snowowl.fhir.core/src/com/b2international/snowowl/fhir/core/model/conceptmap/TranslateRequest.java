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
package com.b2international.snowowl.fhir.core.model.conceptmap;

import java.util.Collection;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.collect.ImmutableList;

/**
 * This class represents a FHIR Concept Map translate operation request.
 * 
 * @see <a href="https://www.hl7.org/fhir/conceptmap-operations.html#translate">
 * FHIR:ConceptMap:Operations:translate</a>
 * 
 * @since 7.1
 */
@JsonDeserialize(builder = TranslateRequest.Builder.class)
@JsonPropertyOrder({"code", "system", "version", "source", "coding", "codeableConcept",
	"target", "targetsystem", "dependency", "reverse"})
public class TranslateRequest {
	
	// The code that is to be translated. If a code is provided, a system must be provided (0..1)
	private final String code;

	// The system for the code that is to be located (0..1)
	private final String system;
	
	// The version that these details are based on (0..1)
	private final String version;
	
	private final String source;

	// The coding to look up (0..1)
	private final Coding coding;
	
	private final CodeableConcept codeableConcept;
	
	private final String target;
	
	private final String targetsystem;
	
	@FhirType(FhirDataType.PART)
	private final Collection<Dependency> dependency;
	
	private final Boolean reverse;
	
	TranslateRequest(
			String code, 
			String system, 
			String version, 
			String source, 
			Coding coding, 
			CodeableConcept codeableConcept, 
			String target,
			String targetsystem,
			Collection<Dependency> dependencies,
			Boolean isReverse) {
		this.code = code;
		this.system = system;
		this.version = version;
		this.source = source;
		this.coding = coding;
		this.codeableConcept = codeableConcept;
		this.target = target;
		this.targetsystem = targetsystem;
		this.dependency = dependencies;
		this.reverse = isReverse;
	}
	
	public String getCode() {
		if (code != null) {
			return code;
		} else if (coding != null) {
			return coding.getCode().getCodeValue();
		}
		return null;
	}

	public String getSystem() {
		if (system != null) {
			return system;
		} else if (coding != null && coding.getSystem() != null) {
			return coding.getSystem().getUriValue();
		}
		return null;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getSource() {
		return source;
	}

	public Coding getCoding() {
		return coding;
	}
	
	public CodeableConcept getCodeableConcept() {
		return codeableConcept;
	}
	
	public String getTarget() {
		return target;
	}
	
	public String getTargetsystem() {
		return targetsystem;
	}
	
	public Collection<Dependency> getDependency() {
		return dependency;
	}
	
	public Boolean getReverse() {
		return reverse;
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<TranslateRequest> {

		private String code;
		private String system;
		private String version;
		private String source;
		private Coding coding;
		private CodeableConcept codeableConcept;
		private String target;
		private String targetsystem;
		private ImmutableList.Builder<Dependency> dependencies = ImmutableList.builder();
		private Boolean isReverse;
		
		Builder() {}
		
		public Builder code(final String code) {
			this.code = code;
			return this;
		}

		public Builder system(final String system) {
			this.system = system;
			return this;
		}
		
		public Builder version(final String version) {
			this.version = version;
			return this;
		}

		public Builder coding(Coding coding) {
			this.coding = coding;
			return this;
		}
		
		/**
		 * Alternative property deserializer to be used when converting FHIR Parameters representation to TranslateRequest. 
		 * Multi-valued property expand.
		 */
		public Builder addDesignation(Dependency dependency) {
			dependencies.add(dependency);
			return this;
		}
		
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder designation(Collection<Dependency> deps) {
			dependencies = ImmutableList.builder();
			dependencies.addAll(deps);
			return this;
		}

		@Override
		protected TranslateRequest doBuild() {
			return new TranslateRequest(code, system, version, source, coding, codeableConcept, 
					target, targetsystem, dependencies.build(), isReverse);
		}

	}
	
}
