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
package com.b2international.snowowl.snomed.importer.rf2.refset;

/**
 * This is a collection of the erroneous filenames appearing in NEHTA_0761_2011_SNOMED_CT-AU_TerminologyReleaseFileBundle_20110531.
 * These files have to be handled differently mainly because of the importer (the importer preprocess them, to rename the erroneous column).
 * 
 * @see SnomedRefSetImporterFactory
 * 
 */
public abstract class ErroneousAustralianReleaseFileNames {

	// hardcoded filenames, these files last column name is wrong so preprocess them before passing to the importer
	public static final String ERRONEOUS_AU_20110531_CTV3_ID_REFSET_NAME = "der2_sRefset_CTV3IdMapSnapshot_AU1000036_20110531.txt";
	public static final String ERRONEOUS_AU_20110531_SNOMED_RT_REFSET_NAME= "der2_sRefset_SnomedRtIdMapSnapshot_AU1000036_20110531.txt";
	
	// same with the language reference set
	public static final String ERRONEOUS_AU_20110531_SNOMED_LANGUAGE_REFSET_NAME = "der2_cRefset_LanguageSnapshot-en-AU_AU1000036_20110531.txt";
	public static final String ERRONEOUS_AU_20120229_SNOMED_LANGUAGE_REFSET_NAME = "xder2_cRefset_LanguageFull-en-AU_AU1000036_20120229.txt";
	
	// same with all concrete domain reference sets  
	public static final String ERRONEOUS_AU_20120229_STRENGTH_REFSET_NAME = "xder2_ccsRefset_StrengthFull_AU1000036_20120229.txt";
	public static final String ERRONEOUS_AU_20120229_SUBPACK_QUANTITY_FULL_REFSET_NAME = "xder2_ccsRefset_SubpackQuantityFull_AU1000036_20120229.txt";
	public static final String ERRONEOUS_AU_20120229_UNIT_OF_USE_QUANTITY_REFSET_NAME = "xder2_ccsRefset_UnitOfUseQuantityFull_AU1000036_20120229.txt";
	public static final String ERRONEOUS_AU_20120229_UNIT_OF_USE_SIZE_REFSET_NAME = "xder2_ccsRefset_UnitOfUseSizeFull_AU1000036_20120229.txt";
	
	private ErroneousAustralianReleaseFileNames() { 
		// Prevent instantiation
	}
}