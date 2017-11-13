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
package com.b2international.snowowl.snomed.core.mrcm;

import java.util.Collection;

import com.b2international.snowowl.core.markers.IDiagnostic;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class MrcmPredicateDiagnostic implements IDiagnostic {

	private Collection<IDiagnostic> children = Lists.newArrayList();
	private final String predicateUuid;
	private final DiagnosticSeverity severity;
	private final String message;
	
	public MrcmPredicateDiagnostic(String predicateUuid, String message, DiagnosticSeverity severity) {
		this.predicateUuid = predicateUuid;
		this.message = message;
		this.severity = severity;
	}
	
	@Override
	public boolean isOk() {
		return DiagnosticSeverity.OK.equals(getProblemMarkerSeverity());
	}

	@Override
	public DiagnosticSeverity getProblemMarkerSeverity() {
		int aggregateSeverity = severity.getErrorCode();
		for (IDiagnostic childDiagnostic : getChildren()) {
			aggregateSeverity |= childDiagnostic.getProblemMarkerSeverity().getErrorCode();
		}
		if ((aggregateSeverity & DiagnosticSeverity.ERROR.getErrorCode()) != 0) {
			return DiagnosticSeverity.ERROR;
		}
		if ((aggregateSeverity & DiagnosticSeverity.WARNING.getErrorCode()) != 0) {
			return DiagnosticSeverity.WARNING;
		}
		if ((aggregateSeverity & DiagnosticSeverity.INFO.getErrorCode()) != 0) {
			return DiagnosticSeverity.INFO;
		}
		if (aggregateSeverity == 0) {
			return DiagnosticSeverity.OK;
		}
		throw new IllegalArgumentException("Unexpected child diagnostic aggregate severity: " + aggregateSeverity);
	}

	@Override
	public Collection<IDiagnostic> getChildren() {
		return children;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getSource() {
		return predicateUuid;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("severity", severity).add("predicateUuid", predicateUuid).toString();
	}

}