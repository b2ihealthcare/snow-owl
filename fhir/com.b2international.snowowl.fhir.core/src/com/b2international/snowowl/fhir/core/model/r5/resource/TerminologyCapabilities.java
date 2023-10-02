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
package com.b2international.snowowl.fhir.core.model.r5.resource;

import java.util.List;

import com.b2international.snowowl.fhir.core.model.r5.Summary;
import com.b2international.snowowl.fhir.core.model.r5.datatype.primitive.CodeType;
import com.b2international.snowowl.fhir.core.model.r5.element.terminologycapabilities.*;

/**
 * A TerminologyCapabilities resource documents a set of capabilities
 * (behaviors) of a FHIR Terminology Server that may be used as a statement of
 * actual server functionality or a statement of required or desired server
 * implementation.
 *
 * @see <a href="https://hl7.org/fhir/R5/terminologycapabilities.html#resource">4.12.4 Resource Content</a>
 * @since 9.0
 */
public class TerminologyCapabilities extends DomainResource {

	/** The way that this statement is intended to be used */
	@Summary
	private CodeType kind;
	
	/** Software that is covered by this terminology capability statement */
	@Summary
	private Software software;
	
	/** A particular instance or installation that is described by this capability statement */
	@Summary
	private Implementation implementation;

	/** Whether "lockedDate" is supported */
	@Summary
	private Boolean lockedDate;
	
	/** A code system supported by the server */
	private List<CodeSystem> codeSystem;
	
	/** Information about ValueSet $expand operations */
	private Expansion expansion;

	/** The degree to which the server supports the code search parameter on ValueSet (compose, expansion or both) */
	private CodeType codeSearch;

	/** Information about ValueSet $validate-code operations */
	private ValidateCode validateCode;
	
	/** Information about ConceptMap $translate operations */
	private Translation translation;

	/** Information about ConceptMap $closure operations */
	private Closure closure;

	public CodeType getKind() {
		return kind;
	}

	public void setKind(CodeType kind) {
		this.kind = kind;
	}

	public Software getSoftware() {
		return software;
	}

	public void setSoftware(Software software) {
		this.software = software;
	}

	public Implementation getImplementation() {
		return implementation;
	}

	public void setImplementation(Implementation implementation) {
		this.implementation = implementation;
	}

	public Boolean getLockedDate() {
		return lockedDate;
	}

	public void setLockedDate(Boolean lockedDate) {
		this.lockedDate = lockedDate;
	}

	public List<CodeSystem> getCodeSystem() {
		return codeSystem;
	}

	public void setCodeSystem(List<CodeSystem> codeSystem) {
		this.codeSystem = codeSystem;
	}

	public Expansion getExpansion() {
		return expansion;
	}

	public void setExpansion(Expansion expansion) {
		this.expansion = expansion;
	}

	public CodeType getCodeSearch() {
		return codeSearch;
	}

	public void setCodeSearch(CodeType codeSearch) {
		this.codeSearch = codeSearch;
	}

	public ValidateCode getValidateCode() {
		return validateCode;
	}

	public void setValidateCode(ValidateCode validateCode) {
		this.validateCode = validateCode;
	}

	public Translation getTranslation() {
		return translation;
	}

	public void setTranslation(Translation translation) {
		this.translation = translation;
	}

	public Closure getClosure() {
		return closure;
	}

	public void setClosure(Closure closure) {
		this.closure = closure;
	}
}
