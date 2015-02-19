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
package com.b2international.snowowl.core.validation;

import com.b2international.snowowl.core.markers.IDiagnostic.DiagnosticSeverity;

/**
 * Descriptor for global validation constraints.
 * 
 */
public class GlobalConstraintDescriptor {
	private final String id;
	private final String name;
	private final DiagnosticSeverity severity;
	private final String description;
	private final String message;
	private final IGlobalConstraint constraint;
	
	/**
	 * Class constructor.
	 * @param id
	 * @param name
	 * @param severity
	 * @param description
	 * @param message
	 * @param constraint
	 */
	public GlobalConstraintDescriptor(String id, String name, DiagnosticSeverity severity, String description, String message, IGlobalConstraint constraint) {
		this.id = id;
		this.name = name;
		this.severity = severity;
		this.description = description;
		this.message = message;
		this.constraint = constraint;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public DiagnosticSeverity getSeverity() {
		return severity;
	}

	public String getDescription() {
		return description;
	}
	
	public IGlobalConstraint getConstraint() {
		return constraint;
	}
	
	public String getMessage() {
		return message;
	}
}