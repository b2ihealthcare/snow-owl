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
package com.b2international.snowowl.snomed.importer.rf2.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;

import org.supercsv.io.CsvListWriter;

import com.b2international.snowowl.snomed.importer.Importer;

/**
 * Stores a {@link CsvListWriter} and a record count for slicing release files
 * based on the entries' effective time.
 */
public class ComponentImportEntry {

	private final File unitFile;
	private final CsvListWriter writer;
	private int recordCount;
	
	public ComponentImportEntry(final File unitFile, final CsvListWriter writer) {
		this.unitFile = checkNotNull(unitFile, "unitFile");
		this.writer = checkNotNull(writer, "writer");
	}
	
	public ComponentImportUnit createUnit(final Importer importer, final String effectiveTimeKey, final ComponentImportType type) {
		return new ComponentImportUnit(importer, effectiveTimeKey, type, unitFile, recordCount);
	}
	
	public CsvListWriter getWriter() {
		return writer;
	}
	
	public void increaseRecordCount() {
		++recordCount;
	}

	@Override
	public String toString() {
		return String.format("ComponentImportEntry [unitFile=%s, recordCount=%s]", unitFile.getAbsolutePath(), recordCount);
	}
}
