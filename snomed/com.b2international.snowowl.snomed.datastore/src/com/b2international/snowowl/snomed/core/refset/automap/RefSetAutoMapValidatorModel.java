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
package com.b2international.snowowl.snomed.core.refset.automap;

/**
 * Model for collecting validation errors after creating simple map type reference set from automapped values.
 * See {@link RefSetAutoMapValidationErrorReason} for the avail validation errors.
 * 
 *
 */
public class RefSetAutoMapValidatorModel {

	private final String mappedIdentifier;
	private final String mappedLabel;
	private final RefSetAutoMapValidationErrorReason reason;
	private final String sourceLabel;

	public RefSetAutoMapValidatorModel(final RefSetAutoMapValidationErrorReason reason, final String sourceLabel) {
		this (null, null, reason, sourceLabel);
	}
	
	public RefSetAutoMapValidatorModel(final String mappedIdentifier, final RefSetAutoMapValidationErrorReason reason, final String sourceLabel) {
		this (mappedIdentifier, null, reason, sourceLabel);
	}
	
	public RefSetAutoMapValidatorModel(final String mappedIdentifier, final String mappedLabel, final RefSetAutoMapValidationErrorReason reason, final String sourceLabel) {
		this.mappedIdentifier = mappedIdentifier;
		this.mappedLabel = mappedLabel;
		this.reason = reason;
		this.sourceLabel = sourceLabel;
	}

	public String getMappedIdentifier() {
		return mappedIdentifier;
	}

	public String getMappedLabel() {
		return mappedLabel;
	}

	public RefSetAutoMapValidationErrorReason getReason() {
		return reason;
	}

	public String getSourceLabel() {
		return sourceLabel;
	}
	
	
}