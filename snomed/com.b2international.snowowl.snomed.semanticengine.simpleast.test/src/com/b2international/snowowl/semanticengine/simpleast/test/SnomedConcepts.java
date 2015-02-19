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
package com.b2international.snowowl.semanticengine.simpleast.test;

public abstract class SnomedConcepts {
	// attribute name concepts
	public static final String ATTRIBUTE = "246061005";
	public static final String FINDING_SITE = "363698007";
	public static final String PROCEDURE_SITE = "363704007";
	public static final String CAUSATIVE_AGENT = "246075003";
	public static final String ASSOCIATED_WITH = "47429007";
	public static final String ASSOCIATED_FINDING = "246090004";
	public static final String SEVERITY = "246112005";
	public static final String METHOD = "260686004";
	public static final String PROCEDURE_SITE_DIRECT = "405813007";
	public static final String ASSOCIATED_MORPHOLOGY = "116676008";
	// attribute value concepts
	public static final String HEAD_STRUCTURE = "69536005";
	public static final String LUNG_STRUCTURE = "39607008";
	public static final String SEVERE = "24484000";
	public static final String DUST_ALLERGEN = "410980008";
	public static final String CONTACT_ALLERGEN = "406473004";
	public static final String SUBSTANCE = "105590001";
	// low-level primitive concepts
	public static final String FAMILY_FELIDAE = "388618001";	// organism
	public static final String SUBFAMILY_FELINAE = "388623001";	// organism
	public static final String TURKISH_ANGORA_CAT = "61686008";	// organism
	public static final String SUBFAMILY_PANTHERINAE = "388748007";	// organism
	public static final String PANTHERA_TIGRIS = "79047009";	// organism
	public static final String FOOT_STRUCTURE = "56459004";	// body structure
	public static final String PAIN = "22253000";	// clinical finding
	public static final String EXPIRATORY_CRACKLES = "12529006";	// clinical finding
	public static final String EXCISION_ACTION = "129304002";	// qualifier value
	public static final String FALLOPIAN_TUBE_STRUCTURE = "31435000";	// body structure
	public static final String OVARIAN_STRUCTURE = "15497006";	// body structure
	public static final String FRACTURE = "72704001";	// body structure
	public static final String FRACTURE_OF_BONE = "125605004";	// clinical finding
	public static final String BONE_STRUCTURE_OF_FEMUR = "71341001";	// body structure
	public static final String EXCISION_OF_PELVIS = "123014005";	// procedure
	public static final String AUSCULTATION = "37931006";	// procedure
	public static final String AUSCULTATION_ACTION = "129436005";	// qualifier
	
	
	// high-level primitive concepts
	public static final String PROCEDURE = "71388002";	// procedure
	public static final String DISEASE = "64572001";	// clinical finding
	// fully defined concepts
	public static final String DISORDER_OF_HEAD = "118934005";	// clinical finding
	public static final String DISORDER_OF_EAR = "25906001";	// clinical finding
	public static final String FOOT_PAIN = "47933007";	// clinical finding
	public static final String FRACTURE_OF_FEMUR = "71620000";	// clinical finding
	public static final String SALPINGO_OOPHORECTOMY = "116028008";	// procedure

	private SnomedConcepts() {}	// suppressing public constructor to avoid subclassing
}
