/*
 * Copyright 2011-2015 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed.validation.diagnostic;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b2international.snowowl.core.markers.IDiagnostic;

/**
 * Diagnostic for reference set member validation.
 */
public class SnomedRefSetDiagnostic implements IDiagnostic {
	
	public static final String SOURCE_ID = "com.b2international.snowowl.snomed.validation.diagnostic.SnomedRefSetDiagnostic";
	
	private final DiagnosticSeverity diagnosticSeverity;
	
	private final List<IDiagnostic> children;
	private final String errorMessage;

	public SnomedRefSetDiagnostic(final DiagnosticSeverity diagnosticSeverity, final String errorMessage) {
		this(diagnosticSeverity, errorMessage, Collections.<IDiagnostic>emptyList());
	}
	
	public SnomedRefSetDiagnostic(final DiagnosticSeverity diagnosticSeverity, final String errorMessage, List<IDiagnostic> children) {
		this.diagnosticSeverity = diagnosticSeverity;
		this.errorMessage = errorMessage;
		this.children = children;
	}

	@Override
	public boolean isOk() {
		return DiagnosticSeverity.OK.equals(getProblemMarkerSeverity());
	}

	@Override
	public DiagnosticSeverity getProblemMarkerSeverity() {
		return diagnosticSeverity;
	}

	@Override
	public Collection<IDiagnostic> getChildren() {
		return children;
	}

	@Override
	public String getMessage() {
		return errorMessage;
	}

	@Override
	public String getSource() {
		return SOURCE_ID;
	}
}