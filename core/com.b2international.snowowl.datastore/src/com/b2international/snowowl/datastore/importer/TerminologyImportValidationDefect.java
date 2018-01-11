/*
 * Copyright 2011-2018 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.datastore.importer;

import java.io.Serializable;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * Model for the terminology import validation defects.
 * 
 * @since SNow&nbsp;Owl 3.0.1
 */
public class TerminologyImportValidationDefect implements Serializable {
	
	private final String sheetName;
	private final Set<Defect> defects;

	public TerminologyImportValidationDefect(final String sheetName) {
		this.sheetName = sheetName;
		this.defects = Sets.newHashSet();
	}
	
	public void addDefect(final DefectType defectType, final String errorMessage) {
		defects.add(new Defect(defectType, errorMessage));
	}

	public String getSheetName() {
		return sheetName;
	}

	public Set<Defect> getDefects() {
		return defects;
	}
	
	public enum DefectType {
		EMPTINESS,
		DIFFERENCES,
		EFFECTIVE_TIME,
		INCORRECT_FORMAT,
		GROUP,
		CYCLE, 
		DUPLICATE
	}
	
	/**
	 * Represents a terminology import defect with a defect type and an error message. 
	 */
	public class Defect implements Serializable {
		
		private final DefectType defectType;
		private final String errorMessage;
		
		public Defect(final DefectType defectType, final String errorMessage) {
			this.defectType = defectType;
			this.errorMessage = errorMessage;
		}

		public DefectType getDefectType() {
			return defectType;
		}

		public String getErrorMessage() {
			return errorMessage;
		}
	}

}