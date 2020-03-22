/*
 * Copyright 2011-2020 B2i Healthcare Pte Ltd, http://b2i.sg
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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

/**
 * Represents the configuration object used by a SNOMED CT RF2 Importer implementation.
 * @deprecated - refactored and redesigned - see {@link SnomedRf2ImportConfiguration} - to eliminate the necessity of a memory stored object {@link ISnomedImportConfiguration}
 */
public interface ISnomedImportConfiguration extends ISnomedRF2Configuration {

	/**
	 * Determines whether the importer should create versions after processing each effective time "layer" in an RF2
	 * import file. Only applicable for {@link Rf2ReleaseType#FULL FULL} imports.
	 * 
	 * @return {@code true} if a version should be created for each individual effective time value in RF2 import files,
	 * {@code false} otherwise
	 */
	boolean shouldCreateVersion();

	/**
	 * Returns the current status of the import process.
	 * 
	 * @return the import status
	 */
	ImportStatus getStatus();

	/**
	 * Returns the start date of the import, or <code>null</code> if it hasn't started yet.
	 * 
	 * @return the import's starting date
	 */
	Date getStartDate();

	/**
	 * Returns the completion date of the import, or <code>null</code> if it hasn't completed yet.
	 * 
	 * @return the import's completion date
	 */
	Date getCompletionDate();
	
	/**
	 * Returns the short name of the Code System to use for the import.
	 * 
	 * @return the short name of the Code System to use.
	 */
	String getCodeSystemShortName();

	/**
	 * Enumerates possible values for the state of an RF2 import process.
	 */
	static enum ImportStatus {

		WAITING_FOR_FILE("Waiting for file"),
		RUNNING("Running"),
		COMPLETED("Completed"),
		FAILED("Failed");

		private String label;

		private ImportStatus(final String label) {
			this.label = checkNotNull(label, "label");
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
