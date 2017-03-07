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
package com.b2international.snowowl.snomed.datastore.internal.rf2;

/**
 * Model class to collect common properties of RF2 and DSV export wizards.
 *
 */
public abstract class SnomedExportModel {

	private String exportPath;
	private final SnomedExportResult exportResult;

	public SnomedExportModel() {
		exportResult = new SnomedExportResult();
	}

	public String getExportPath() {
		return exportPath;
	}

	public void setExportPath(final String exportPath) {
		this.exportPath = exportPath;
	}

	public SnomedExportResult getExportResult() {
		return exportResult;
	}

}