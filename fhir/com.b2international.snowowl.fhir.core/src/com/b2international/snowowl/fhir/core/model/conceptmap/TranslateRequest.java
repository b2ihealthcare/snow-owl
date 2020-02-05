/*
 * Copyright 2018-2019 B2i Healthcare Pte Ltd, http://b2i.sg
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

import javax.validation.constraints.AssertTrue;

import com.b2international.snowowl.fhir.core.model.ValidatingBuilder;
import com.b2international.snowowl.fhir.core.model.dt.Code;
import com.b2international.snowowl.fhir.core.model.dt.CodeableConcept;
import com.b2international.snowowl.fhir.core.model.dt.Coding;
import com.b2international.snowowl.fhir.core.model.dt.FhirDataType;
import com.b2international.snowowl.fhir.core.model.dt.FhirType;
import com.b2international.snowowl.fhir.core.model.dt.Uri;
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
@JsonPropertyOrder({"url", "conceptMap", "conceptMapVersion", "code", "system",	"version", 
	"source", "coding", "codeableConcept", "target", "targetsystem", "dependency", "reverse"})
public class TranslateRequest {
	
	//A canonical URL for the concept map. (0..1)
	private final Uri url;
	
	//The concept map is provided directly as part of the request. Servers may choose not to accept concept maps in this fashion. (0..1)
	private final ConceptMap conceptMap;
	
	//The identifier that is used to identify a specific version of the concept map to be used for the translation. (0..1)
	private final String conceptMapVersion;
	
	// The code that is to be translated. If a code is provided, a system must be provided (0..1)
	private final Code code;

	// The system for the code that is to be located (0..1)
	private final Uri system;
	
	// The version that these details are based on (0..1)
	private final String version;
	
	private final Uri source;

	// The coding to translate (0..1)
	private final Coding coding;
	
	//A full codeableConcept to translate. The server can translate any of the coding values
	//(e.g. existing translations) as it chooses
	private final CodeableConcept codeableConcept;
	
	//Identifies the value set in which a translation is sought (either this is set or target code system).
	private final Uri target;
	
	//Identifies a target code system in which a mapping is sought (either this is set or target code system).
	private final Uri targetsystem;
	
	@FhirType(FhirDataType.PART)
	private final Collection<Dependency> dependency;
	
	private final Boolean reverse;
	
	TranslateRequest(
			Uri url,
			ConceptMap conceptMap,
			String conceptMapVersion,
			Code code, 
			Uri system, 
			String version, 
			Uri source, 
			Coding coding, 
			CodeableConcept codeableConcept, 
			Uri target,
			Uri targetsystem,
			Collection<Dependency> dependencies,
			Boolean isReverse) {
		
		this.url = url;
		this.conceptMap = conceptMap;
		this.conceptMapVersion = conceptMapVersion;
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
	
	public String getUrlValue() {
		if (url != null) {
			return url.getUriValue();
		}
		return null;
	}
	
	public ConceptMap getConceptMap() {
		return conceptMap;
	}
	
	public String getConceptMapVersion() {
		return conceptMapVersion;
	}
	
	public String getCodeValue() {
		if (code != null) {
			return code.getCodeValue();
		} else if (coding != null) {
			return coding.getCode().getCodeValue();
		}
		return null;
	}

	public String getSystemValue() {
		if (system != null) {
			return system.getUriValue();
		} else if (coding != null && coding.getSystem() != null) {
			return coding.getSystem().getUriValue();
		}
		return null;
	}
	
	public Uri getSystem() {
		if (system != null) {
			return system;
		} else if (coding != null && coding.getSystem() != null) {
			return coding.getSystem();
		}
		return null;
	}
	
	public String getVersion() {
		return version;
	}
	
	public Uri getSource() {
		return source;
	}

	public Coding getCoding() {
		return coding;
	}
	
	public CodeableConcept getCodeableConcept() {
		return codeableConcept;
	}
	
	public Uri getTarget() {
		return target;
	}
	
	public Uri getTargetsystem() {
		return targetsystem;
	}
	
	public Collection<Dependency> getDependency() {
		return dependency;
	}
	
	public Boolean getReverse() {
		return reverse;
	}
	
	@AssertTrue(message = "Both code and system needs to be provided")
	private boolean isCodeAndSystemValid() {

		if (code != null && system == null) {
			return false;
		}

		if (code == null && system != null) {
			return false;
		}
		return true;
	}
	
	@AssertTrue(message = "Source needs to be set either via code/system or code or codeable concept")
	private boolean isSourceValid() {

		int numberOfSourceParams = 0;
		
		if (code != null) {
			numberOfSourceParams++;
		}
		
		if (coding != null) {
			numberOfSourceParams++;
		}
		
		if (codeableConcept != null) {
			numberOfSourceParams++;
		}
		if (numberOfSourceParams != 1) {
			return false;
		}
		return true;
	}
	
	@AssertTrue(message = "Target or target system needs to be provided")
	private boolean isTargetValid() {

		if (target == null && targetsystem == null) {
			return false;
		}

		if (target != null && targetsystem != null) {
			return false;
		}
		return true;
	}
	
	@AssertTrue(message = "SNOMED CT version is defined as part of the system URI")
	private boolean isVersionValid() {
		
		if (version != null && system != null && system.isSnomedUri()) {
			return false;
		}
		return true;
	}

	public static Builder builder() {
		return new Builder();
	}

	@JsonPOJOBuilder(withPrefix="")
	public static final class Builder extends ValidatingBuilder<TranslateRequest> {

		private Uri url;
		private ConceptMap conceptMap;
		private String conceptMapVersion;
		private Code code;
		private Uri system;
		private String version;
		private Uri source;
		private Coding coding;
		private CodeableConcept codeableConcept;
		private Uri target;
		private Uri targetsystem;
		private ImmutableList.Builder<Dependency> dependencies = ImmutableList.builder();
		private Boolean isReverse;
		
		Builder() {}
		
		public Builder url(final String url) {
			this.url = new Uri(url);
			return this;
		}
		
		public Builder conceptMap(final ConceptMap conceptMap) {
			this.conceptMap =conceptMap;
			return this;
		}
		
		public Builder conceptMapVersion(final String conceptMapVersion) {
			this.conceptMapVersion = conceptMapVersion;
			return this;
		}
		
		public Builder code(final String code) {
			this.code = new Code(code);
			return this;
		}

		public Builder system(final String system) {
			this.system = new Uri(system);
			return this;
		}
		
		public Builder version(final String version) {
			this.version = version;
			return this;
		}
		
		public Builder source(final String source) {
			this.source = new Uri(source);
			return this;
		}

		public Builder coding(Coding coding) {
			this.coding = coding;
			return this;
		}
		
		public Builder codeableConcept(CodeableConcept codeableConcept) {
			this.codeableConcept = codeableConcept;
			return this;
		}
		
		public Builder target(final String target) {
			this.target = new Uri(target);
			return this;
		}
		
		public Builder targetSystem(final String targetSystem) {
			this.targetsystem = new Uri(targetSystem);
			return this;
		}
		
		public Builder targetSystem(final Uri targetSystemUri) {
			this.targetsystem = targetSystemUri;
			return this;
		}
		
		/**
		 * Alternative property deserializer to be used when converting FHIR Parameters representation to TranslateRequest. 
		 * Multi-valued property expand.
		 */
		public Builder addDependency(Dependency dependency) {
			dependencies.add(dependency);
			return this;
		}
		
		@JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
		public Builder dependencies(Collection<Dependency> deps) {
			dependencies = ImmutableList.builder();
			dependencies.addAll(deps);
			return this;
		}
		
		public Builder isReverse(final Boolean isReverse) {
			this.isReverse = isReverse;
			return this;
		}

		@Override
		protected TranslateRequest doBuild() {
			return new TranslateRequest(url, conceptMap, conceptMapVersion, code, system, version, 
					source, coding, codeableConcept, target, targetsystem, dependencies.build(), isReverse);
		}

	}
	
}
