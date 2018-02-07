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
package com.b2international.snowowl.snomed.exporter.server.sandbox;

import java.io.File;
import java.util.Iterator;

import com.b2international.snowowl.snomed.exporter.server.ComponentExportType;
import com.google.common.base.Ascii;
import com.google.common.base.Charsets;

/**
 * Representation of an exporter for SNOMED&nbsp;CT ontology.
 *
 */
public interface SnomedExporter extends Iterator<String>, Iterable<String>, AutoCloseable {
	
	/**Horizontal tab.*/
	String HT = new String(new byte [] { Ascii.HT }, Charsets.US_ASCII);
	String CR_LF = new String(new byte[] {Ascii.CR, Ascii.LF}, Charsets.US_ASCII);
	
	String METADATA = "Metadata";
	String LANGUAGE = "Language";
	String CROSSMAP = "Maps";
	String MAP = "Map";
	String TERMINOLOGY = "Terminology";
	String CONTENT = "Content";
	String SUBSETS = "Subsets";
	String RF1_ROOT_FOLDER = "RF1Release";
	String RF2_ROOT_FOLDER = "RF2Release";
	String RF2_REFERENCE_SET_RELATIVE_ROOT_DIR = RF2_ROOT_FOLDER + File.separatorChar + "Refset";

	String RF1_CORE_RELATIVE_DIRECTORY = RF1_ROOT_FOLDER + File.separatorChar + TERMINOLOGY + File.separatorChar + CONTENT;
	String RF1_SUBSET_RELATIVE_DIRECTORY = RF1_ROOT_FOLDER + File.separatorChar + SUBSETS;
	String RF1_CROSSMAP_RELATIVE_DIRECTORY = RF1_ROOT_FOLDER + File.separatorChar + CROSSMAP;
	String RF2_CORE_RELATIVE_DIRECTORY = RF2_ROOT_FOLDER + File.separatorChar + TERMINOLOGY;
	String RF2_CONTENT_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + CONTENT;
	String RF2_MAP_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + MAP;
	String RF2_LANGUAGE_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + LANGUAGE;
	String RF2_METADATA_REFERENCE_SET_RELATIVE_DIR = RF2_REFERENCE_SET_RELATIVE_ROOT_DIR + File.separatorChar + METADATA;
	
	String getRelativeDirectory();
	
	String getFileName();
	
	ComponentExportType getType();
	
	String[] getColumnHeaders();
	
	SnomedExportConfiguration getConfiguration();
	
}