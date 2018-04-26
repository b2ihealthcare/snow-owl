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
package com.b2international.snowowl.snomed.core.domain;

/**
 * Enumerates available export file layouts for reference sets.
 */
public enum Rf2RefSetExportLayout {

	/**
	 * Reference set content for the same type are concatenated into a single file.
	 */
	COMBINED("One file per reference set type"),

	/**
	 * A separate file is created for each reference set.
	 */
	INDIVIDUAL("One file per reference set");

	private String displayLabel;

	private Rf2RefSetExportLayout(final String displayLabel) {
		this.displayLabel = displayLabel;
	}

	@Override
	public String toString() {
		return displayLabel;
	}
}
