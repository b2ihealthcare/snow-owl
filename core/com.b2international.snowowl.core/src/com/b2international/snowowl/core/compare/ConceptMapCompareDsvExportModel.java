/*
 * Copyright 2020 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.core.compare;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @since 7.13
 */
@JsonPropertyOrder(value = { "changeKind", "conceptMap", "sourceCodeSystem", "sourceCode", "sourceTerm", "targetCodeSystem", "targetCode", "targetTerm" })
public final class ConceptMapCompareDsvExportModel {
	
	private String changeKind;
	private String conceptMap;
	private String sourceCodeSystem;
	private String sourceCode;
	private String sourceTerm;
	private String targetCodeSystem;
	private String targetCode;
	private String targetTerm;
	
	ConceptMapCompareDsvExportModel() { }

	public ConceptMapCompareDsvExportModel(
			String changeKind,
			String conceptMap,
			String sourceCodeSystem,
			String sourceCode,
			String sourceTerm,
			String targetCodeSystem,
			String targetCode,
			String targetTerm) {
		this.changeKind = changeKind;
		this.conceptMap = conceptMap;
		this.sourceCodeSystem = sourceCodeSystem;
		this.sourceCode = sourceCode;
		this.sourceTerm = sourceTerm;
		this.targetCodeSystem = targetCodeSystem;
		this.targetCode = targetCode;
		this.targetTerm = targetTerm;

	}
	
	public String getChangeKind() {
		return changeKind;
	}
	
	public String getConceptMap() {
		return conceptMap;
	}

	public String getSourceCode() {
		return sourceCode;
	}
	
	public String getSourceCodeSystem() {
		return sourceCodeSystem;
	}
	
	public String getSourceTerm() {
		return sourceTerm;
	}
	
	public String getTargetCode() {
		return targetCode;
	}
	
	public String getTargetCodeSystem() {
		return targetCodeSystem;
	}
	
	public String getTargetTerm() {
		return targetTerm;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { return true; }
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		final ConceptMapCompareDsvExportModel other = (ConceptMapCompareDsvExportModel) obj;

		return Objects.equals(changeKind, other.changeKind) &&
				Objects.equals(conceptMap, other.conceptMap) &&
				Objects.equals(sourceCodeSystem, other.sourceCodeSystem) &&
				Objects.equals(sourceCode, other.sourceCode) &&
				Objects.equals(sourceTerm, other.sourceTerm) &&
				Objects.equals(targetCodeSystem, other.targetCodeSystem) &&
				Objects.equals(targetCode, other.targetCode) &&
				Objects.equals(targetTerm, other.targetTerm);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(changeKind, conceptMap, sourceCodeSystem, sourceCode, sourceTerm, targetCodeSystem, targetCode, targetTerm);
	}
}
