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
package com.b2international.snowowl.snomed.importer.rf2;

import java.nio.charset.Charset;

import org.supercsv.prefs.CsvPreference;

import com.google.common.base.Charsets;

/**
 * Stores CSV settings for IHTSDO release files.
 *
 */
public abstract class CsvConstants {
	
	private CsvConstants() {
		// Suppress instantiation
	}
	
	public static final Charset IHTSDO_CHARSET = Charsets.UTF_8;
	public static final CsvPreference IHTSDO_CSV_PREFERENCE = new CsvPreference('\0', '\t', "\n");
}