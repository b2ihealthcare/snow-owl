/*
 * Copyright 2011-2017 B2i Healthcare Pte Ltd, http://b2i.sg
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
package com.b2international.snowowl.snomed;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * Constant class for SNOMED CT specific constants.
 * 
 */
public abstract class SnomedConstants {
	// suppress constructor to avoid instantiation 
	private SnomedConstants() { }

	/**
	 * Mapping between language codes (e.g. "en-gb") and preferred term reference set identifiers.
	 * TODO: not a very elegant solution, should eventually be replaced.
	 */
	public static abstract class LanguageCodeReferenceSetIdentifierMapping {
		
		public static final String EN_LANGUAGE_CODE = "en"; 
		
		// suppress constructor to avoid instantiation
		private LanguageCodeReferenceSetIdentifierMapping() { }
		
		private static final BiMap<String, String> CODE_IDENTIFIER_BIMAP = ImmutableBiMap.of(
				"en-us", SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_US,
				"en-gb", SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_UK,
				"en-sg", SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_SG,
				"en-au", SnomedConstants.Concepts.AUSTRALIAN_LANGUAGE_REFERENCE_SET,
				"es", SnomedConstants.Concepts.REFSET_LANGUAGE_TYPE_ES);
		
		/**
		 * @param languageCode
		 * @return the language reference set identifier for the specified language code, or null if
		 * 			the language code is not supported
		 */
		public static String getReferenceSetIdentifier(final String languageCode) {
			final String refSetId = CODE_IDENTIFIER_BIMAP.get(languageCode);
			return refSetId;
		}
		
		/**
		 * @param referenceSetIdentifier
		 * @return the language code for the specified refset identifier, or null if
		 * 			the refset identifier is not supported
		 */
		public static String getLanguageCode(final String referenceSetIdentifier) {
			return CODE_IDENTIFIER_BIMAP.inverse().get(referenceSetIdentifier);
		}
		
		/***
		 * Returns with a collection of SNOMED&nbsp;CT language type reference set identifier concept IDs
		 * supported by the application.
		 * @return a collection of language type reference set identifier concept IDs.
		 */
		public static Collection<String> getSupportedLanguageIds() {
			return Collections.unmodifiableCollection(CODE_IDENTIFIER_BIMAP.values());
		}
	}
	
	/**
	 * Constant class for frequently used SNOMED CT concept IDs.
	 * 
	 */
	public static abstract class Concepts {

		// suppress constructor to avoid instantiation
		private Concepts() { }
		
		public static final Set<String> DEFINING_CHARACTERISTIC_TYPES = ImmutableSet.of(
				Concepts.DEFINING_RELATIONSHIP, 
				Concepts.STATED_RELATIONSHIP, 
				Concepts.INFERRED_RELATIONSHIP);

		public static final String ROOT_CONCEPT = "138875005";
		public static final String IS_A = "116680003";
		public static final String FINDING_SITE = "363698007";
		public static final String METHOD = "260686004";
		public static final String MORPHOLOGY = "116676008";
		public static final String PROCEDURE_SITE_DIRECT = "405813007";
		public static final String INTERPRETS = "363714003";
		public static final String CAUSATIVE_AGENT = "246075003";
		
		public static final String DEFINITION_STATUS_ROOT = "900000000000444006";
		public static final String FULLY_DEFINED = "900000000000073002";
		public static final String PRIMITIVE = "900000000000074008";
		public static final String CHARACTERISTIC_TYPE = "900000000000449001";
		public static final String DEFINING_RELATIONSHIP = "900000000000006009";
		public static final String QUALIFYING_RELATIONSHIP = "900000000000225001";
		public static final String INFERRED_RELATIONSHIP = "900000000000011006";
		public static final String STATED_RELATIONSHIP = "900000000000010007";
		public static final String ADDITIONAL_RELATIONSHIP = "900000000000227009";
		public static final String MODIFIER_ROOT = "900000000000450001";
		public static final String EXISTENTIAL_RESTRICTION_MODIFIER = "900000000000451002";
		public static final String UNIVERSAL_RESTRICTION_MODIFIER = "900000000000452009";
		public static final String FULLY_SPECIFIED_NAME = "900000000000003001";
		public static final String SYNONYM = "900000000000013009";
		public static final String TEXT_DEFINITION = "900000000000550004";
		public static final String ONLY_INITIAL_CHARACTER_CASE_INSENSITIVE = "900000000000020002";
		public static final String ENTIRE_TERM_CASE_INSENSITIVE = "900000000000448009";	//not used in description snapshot
		public static final String ENTIRE_TERM_CASE_SENSITIVE = "900000000000017005";
		public static final String FOUNDATION_METADATA_CONCEPTS = "900000000000454005";
		public static final String CASE_SIGNIFICANCE_ROOT_CONCEPT = "900000000000447004";
		
		//TODO: These are substitutes for concrete domain operational concepts that WILL be 
		//under the metadata hierarchy.  These need to be replaced once available.  bbanfai - 2012.09.11
		public static final String CD_EQUAL = "276136004";  // =
		public static final String CD_LESS = "276139006";  // <
		public static final String CD_GREATER = "276140008";  // >
		public static final String CD_LESS_OR_EQUAL = "276137008";  // <=
		public static final String CD_GREATER_OR_EQUAL = "276138003";  // >=
		public static final String CD_UNEQUAL = "431878004";  // <>
		

		public static final String TOPLEVEL_METADATA = "900000000000441003";
		public static final String LINKAGE = "106237007";
		public static final String PHARMACEUTICAL = "373873005";
		public static final String PHYSICAL_OBJECT = "260787004";
		public static final String QUALIFIER_VALUE_TOPLEVEL_CONCEPT = "362981000";
		
		public static final String NOT_REFINABLE = "900000000000007000";
		public static final String OPTIONAL_REFINABLE = "900000000000216007";
		public static final String MANDATORY_REFINABLE = "900000000000218008";
		
		public static final String DESCRIPTION_TYPE_ROOT_CONCEPT = "900000000000446008";
		public static final String DESCRIPTION_FORMAT_TYPE_ROOT_CONCEPT = "900000000000539002";
		public static final String DESCRIPTION_FORMAT_PLAIN_TEXT = "900000000000540000";
		
		//ref sets
		public static final String REFSET_ROOT_CONCEPT = "900000000000455006";
		public static final String REFSET_ALL = REFSET_ROOT_CONCEPT;
		public static final String REFSET_SIMPLE_TYPE = "446609009";	// manually added by the importer to 0531 NEHTA (AU)
		public static final String REFSET_COMPLEX_MAP_TYPE = "447250001";	// manually added by the importer to 0531 NEHTA (AU)
		public static final String EXTENDED_MAP_TYPE = "609331003";
		public static final String REFSET_ATTRIBUTE_VALUE_TYPE = "900000000000480006";
		public static final String REFSET_ASSOCIATION_TYPE = "900000000000521006";
		public static final String REFSET_LANGUAGE_TYPE = "900000000000506000";
		public static final String REFSET_LANGUAGE_TYPE_UK = "900000000000508004";
		public static final String REFSET_LANGUAGE_TYPE_US = "900000000000509007";		
		public static final String REFSET_LANGUAGE_TYPE_SG = "9011000132109";
		public static final String REFSET_LANGUAGE_TYPE_ES = "450828004";
		public static final String REFSET_QUERY_SPECIFICATION_TYPE = "900000000000512005";
		public static final String REFSET_SIMPLE_MAP_TYPE = "900000000000496009";
		public static final String REFSET_DESCRIPTION_TYPE = "900000000000538005";
		public static final String REFSET_CONCRETE_DOMAIN_TYPE_AU = "50131000036100"; //AU release -> NEHTA_0856_2012_AMTImplentationKit_20120229
		public static final String REFSET_MODULE_DEPENDENCY_TYPE = "900000000000534007";
		public static final String REFSET_OWL_AXIOM = "733073007";
		
		public static final String REFSET_MRCM_ROOT = "723564002";
		public static final String REFSET_MRCM_DOMAIN_INTERNATIONAL = "723560006";
		public static final String REFSET_MRCM_ATTRIBUTE_DOMAIN_INTERNATIONAL = "723561005";
		public static final String REFSET_MRCM_ATTRIBUTE_RANGE_INTERNATIONAL = "723562003";
		public static final String REFSET_MRCM_MODULE_SCOPE = "723563008";
		
		//CMT reference sets
		public static final String REFSET_B2I_EXAMPLE = "780716481000154104"; //for more details see: https://github.com/b2ihealthcare/snowowl/issues/368
		public static final String REFSET_KP_CONVERGENT_MEDICAL_TERMINOLOGY = "494287621000154107"; //for more details see: https://github.com/b2ihealthcare/snowowl/issues/368
		public static final String REFSET_CORE_PROBLEM_LIST_REFERENCE_SETS = "344562521000154101";
		public static final String REFSET_INFOWAY_PRIMARY_HEALTH_CARE_REFERENCE_SETS = "372749141000154103";
		
		public static final String CARDIOLOGY_REFERENCE_SET = "152725851000154106";
		public static final String ENDOCRINOLOGY_UROLOGY_NEPHROLOGY_REFERENCE_SET = "674330851000154100";
		public static final String HEMATOLOGY_ONCOLOGY_REFERENCE_SET = "291212201000154102";
		public static final String MENTAL_HEALTH_REFERENCE_SET = "599282691000154102";
		public static final String MUSCULOSKELETAL_REFERENCE_SET = "99400311000154108";
		public static final String NEUROLOGY_REFERENCE_SET = "501847791000154106";
		public static final String OPHTHALMOLOGY_REFERENCE_SET = "735316271000154103";
		public static final String PRIMARY_CARE_REFERENCE_SET = "723916301000154101";
		public static final String HISTORY_AND_FAMILY_HISTORY_REFERENCE_SET = "470288881000154105";
		public static final String INJURIES_REFERENCE_SET = "111719501000154107";
		public static final String ORTHOPEDICS_REFERENCE_SET = "461251521000154104";
		public static final String OBSTETRICS_AND_GYNECOLOGY_REFERENCE_SET = "358903761000154109";
		public static final String SKIN_RESPIRATORY_REFERENCE_SET = "832566231000154105";
		public static final String ENT_GASTROINTESTINAL_INFECTIOUS_DISEASES_REFERENCE_SET = "149994661000154106";
		public static final String KP_PROBLEM_LIST_REFERENCE_SET = "376537701000154105";

		public static final Map<String, String> CMT_REFSET_NAME_ID_MAP = ImmutableMap.<String, String>builder()
				.put("Cardiology", CARDIOLOGY_REFERENCE_SET)
				.put("Endocrine, Nephrology, and Urology", ENDOCRINOLOGY_UROLOGY_NEPHROLOGY_REFERENCE_SET)
				.put("ENT, Gastrointestinal, Infectious Diseases", ENT_GASTROINTESTINAL_INFECTIOUS_DISEASES_REFERENCE_SET)
				.put("Hematology and Oncology", HEMATOLOGY_ONCOLOGY_REFERENCE_SET)
				.put("History and Family History", HISTORY_AND_FAMILY_HISTORY_REFERENCE_SET)
				.put("Injuries", INJURIES_REFERENCE_SET)
				.put("KP Problem List", KP_PROBLEM_LIST_REFERENCE_SET)
				.put("Mental Health", MENTAL_HEALTH_REFERENCE_SET)
				.put("Musculoskeletal", MUSCULOSKELETAL_REFERENCE_SET)
				.put("Neurology", NEUROLOGY_REFERENCE_SET)
				.put("Obstetrics and Gynecology", OBSTETRICS_AND_GYNECOLOGY_REFERENCE_SET)
				.put("Ophthalmology", OPHTHALMOLOGY_REFERENCE_SET)
				.put("Orthopedics", ORTHOPEDICS_REFERENCE_SET)
				.put("Primary Care", PRIMARY_CARE_REFERENCE_SET)
				.put("Skin/Dermatology and Respiratory", SKIN_RESPIRATORY_REFERENCE_SET)
				.build();
		
		public static final String SINGAPORE_UNIT_OF_MEASURE_REFERENCE_SET = "492227111000132107";
		public static final String SINGAPORE_EXTENSION_REFERENCE_SET = "843239231000132105";
		public static final String SDD_UNIT_OF_MEASURE_REFERENCE_SET = "62111000133108";
		public static final String SDD_SIMPLE_TYPE_REFERENCE_SET = "69511000133108";

		//concrete domain
		public static final String REFSET_BOOLEAN_DATATYPE = "759160691000154109";
		public static final String REFSET_DATETIME_DATATYPE = "492980241000154105";
		public static final String REFSET_INTEGER_DATATYPE = "373998411000154109";
		public static final String REFSET_FLOAT_DATATYPE = "744104701000154109";
		public static final String REFSET_STRING_DATATYPE = "513945551000154100";
		public static final String REFSET_CONCRETE_DOMAIN_TYPE = "289191171000154104";
		public static final String REFSET_DEFINING_TYPE = "384696201000154108";
		public static final String REFSET_MEASUREMENT_TYPE = "945726341000154109";

		public static final String REFSET_DRUG_TO_SOURCE_DRUG_SIMPLE_MAP = "776245861000133102";
		public static final String REFSET_DRUG_TO_GROUPER_SIMPLE_MAP = "499896751000133109";
		public static final String REFSET_DRUG_TO_PACKAGING_SIMPLE_MAP = "780548781000133105";
		
		//used for NEHTA AU AMT extension
		/**@deprecated For NEHTA only. {@value}*/
		@Deprecated public static final String REFSET_STRENGTH = "700000111000036105";
		/**@deprecated For NEHTA only. {@value}*/
		@Deprecated public static final String REFSET_UNIT_OF_USE_QUANTITY = "700000131000036101";
		/**@deprecated For NEHTA only. {@value}*/
		@Deprecated public static final String REFSET_UNIT_OF_USE_SIZE = "700000141000036106";
		/**@deprecated For NEHTA only. {@value}*/
		@Deprecated public static final String REFSET_SUBPACK_QUANTITY = "700000121000036103";
		/**@deprecated For NEHTA only. {@value}*/
		@Deprecated public static final String EQUAL_TO = "25311000036102";

		public static final String REFSET_RELATIONSHIP_REFINABILITY = "900000000000488004";
		
		public static final String REFSET_DESCRIPTION_ACCEPTABILITY_ACCEPTABLE = "900000000000549004";
		public static final String REFSET_DESCRIPTION_ACCEPTABILITY_PREFERRED = "900000000000548007";
		
		//for correlation attribute concept imports as it is not in the 0531 NEHTA (AU) RF2 SNOMED CT release
		public static final String REFSET_ATTRIBUTE = "900000000000457003";
		public static final String REFSET_CORRELATION_NOT_SPECIFIED = "447561005";
		
		// Inactivation indicator reference sets
		public static final String REFSET_CONCEPT_INACTIVITY_INDICATOR = "900000000000489007";
		public static final String REFSET_DESCRIPTION_INACTIVITY_INDICATOR = "900000000000490003";
		
		// MRCM related concepts
		public static final String RULE_STRENGTH_ROOT = "723573005";
		public static final String CONTENT_TYPE_ROOT = "723574004";
		
		//component incativation reasons
		public static final String LIMITED = "900000000000486000";
		public static final String DUPLICATE = "900000000000482003";
		public static final String OUTDATED = "900000000000483008";
		public static final String AMBIGUOUS = "900000000000484002";
		public static final String ERRONEOUS = "900000000000485001";
		public static final String MOVED_ELSEWHERE = "900000000000487009";
		public static final String INAPPROPRIATE = "900000000000494007";
		public static final String PENDING_MOVE = "900000000000492006";
		public static final String CONCEPT_NON_CURRENT = "900000000000495008";
		
		// Historical reference sets
		public static final String REFSET_ALTERNATIVE_ASSOCIATION = "900000000000530003";
		public static final String REFSET_MOVED_FROM_ASSOCIATION = "900000000000525002";
		public static final String REFSET_MOVED_TO_ASSOCIATION = "900000000000524003";
		public static final String REFSET_POSSIBLY_EQUIVALENT_TO_ASSOCIATION = "900000000000523009";
		public static final String REFSET_REFERS_TO_ASSOCIATION = "900000000000531004";
		public static final String REFSET_REPLACED_BY_ASSOCIATION = "900000000000526001";
		public static final String REFSET_SAME_AS_ASSOCIATION = "900000000000527005";
		public static final String REFSET_SIMILAR_TO_ASSOCIATION = "900000000000529008";
		public static final String REFSET_WAS_A_ASSOCIATION = "900000000000528000";
		
		//	simple map type refsets
		public static final String CTV3_SIMPLE_MAP_TYPE_REFERENCE_SET_ID = "900000000000497000";
		public static final String SNOMED_RT_SIMPLE_MAP_TYPE_REFERENCE_SET_ID = "900000000000498005";
		public static final String ICD_O_REFERENCE_SET_ID = "446608001";
		public static final String SDD_DRUG_REFERENCE_SET = "940626531000132107";
		public static final String DOSE_FORM_SYNONYM_PLURAL_REFERENCE_SET = "41181011000132103";
		
		//complex map type reference sets
		public static final String ICD_9_CM_REFERENCE_SET_ID = "447563008";
		public static final String ICD_10_REFERENCE_SET_ID = "447562003";
		public static final String ICD_10_CM_COMPLEX_MAP_REFERENCE_SET_ID = "6011000124106";

		// Concepts that require special care when classifying
		public static final String CONCEPT_MODEL_ATTRIBUTE = "410662002";
		public static final String PART_OF = "123005000";
		public static final String LATERALITY = "272741003";
		public static final String HAS_ACTIVE_INGREDIENT = "127489000";
		public static final String HAS_DOSE_FORM = "411116001";
		
		//	Australian specific concepts
		public static final String AUSTRALIAN_LANGUAGE_REFERENCE_SET = "32570271000036106";

		//numerical and unit type linkage concepts
		public static final String HAS_STRENGTH = "411117005";
		public static final String HAS_UNITS = "73298004";
		
		//complex map correlation and category
		public static final String MAP_CORRELATION_ROOT = "447247004";
		public static final String MAP_CATEGORY_ROOT = "447634004";
		public static final String MAP_CATEGORY_NOT_CLASSIFIED = "447638001";
		
		// Modules
		public static final String MODULE_ROOT = "900000000000443000";
		public static final String MODULE_SCT_CORE = "900000000000207008";
		public static final String MODULE_SCT_MODEL_COMPONENT = "900000000000012004";
		public static final String MODULE_B2I_EXTENSION = "636635721000154103";
		public static final String CORE_NAMESPACE = "373872000";
		public static final String B2I_NAMESPACE = "1000154";
		
		// UK modules
		public static final String UK_MAINTAINED_CLINICAL_MODULE = "999003121000000100";
		public static final String UK_EDITION_MODULE = "999000041000000102";
		public static final String UK_EDITION_REFERENCE_SET_MODULE = "999000031000000106";
		public static final String UK_CLINICAL_EXTENSION_MODULE = "999000011000000103";
		public static final String UK_CLINICAL_EXTENSION_REFERENCE_SET_MODULE = "999000021000000109";
		
		public static final String UK_MAINTAINED_PHARMACY_MODULE = "999000871000001102";
		public static final String UK_DRUG_EXTENSION_MODULE = "999000011000001104";
		public static final String UK_DRUG_EXTENSION_REFERENCE_SET_MODULE = "999000021000001108";
		
		public static final String UK_EXCLUDE_FROM_CLINICAL_RELEASE_MODULE = "15211000000101";
		public static final String UK_EXCLUDE_FROM_DRUG_EXTENSION_RELEASE_MODULE = "11000001102";
		
		public static final Set<String> UK_MODULES = ImmutableSet.of(
			UK_MAINTAINED_CLINICAL_MODULE, 
			UK_EDITION_MODULE,
			UK_EDITION_REFERENCE_SET_MODULE,
			UK_CLINICAL_EXTENSION_MODULE,
			UK_CLINICAL_EXTENSION_REFERENCE_SET_MODULE,
			UK_MAINTAINED_PHARMACY_MODULE,
			UK_DRUG_EXTENSION_MODULE,
			UK_DRUG_EXTENSION_REFERENCE_SET_MODULE
		);
		
		public static final Set<String> UK_MODULES_NOCLASSIFY = ImmutableSet.of(
			UK_MAINTAINED_CLINICAL_MODULE, 
			UK_EDITION_MODULE,
			UK_EDITION_REFERENCE_SET_MODULE,
			UK_CLINICAL_EXTENSION_REFERENCE_SET_MODULE,
			UK_MAINTAINED_PHARMACY_MODULE,
			UK_DRUG_EXTENSION_MODULE,
			UK_DRUG_EXTENSION_REFERENCE_SET_MODULE
		);

		// SG specific concepts
		public static final String GENERATED_SINGAPORE_MEDICINAL_PRODUCT = "551000991000133100";
		public static final String HAS_RELEASE_CHARACTERISTIC = "9141000132106";

		public static final String ABBREVIATION = "9271000132107";
		public static final String ABBREVIATION_PLURAL = "69721000132103";
		public static final String FULL_NAME = "9201000132100";
		public static final String FULL_NAME_PLURAL = "91991000132100";
		public static final String DISPLAY_NAME = "92011000132100";
		public static final String NOTE = "9291000132106";
		public static final String PREFERRED_PLURAL = "9281000132109";
		public static final String PRODUCT_TERM = "9231000132105";
		public static final String PRODUCT_TERM_PLURAL = "69701000132106";
		public static final String SEARCH_TERM = "9221000132108";
		public static final String SHORT_NAME = "9211000132103";
		public static final String HAS_PRODUCT_HIERARCHY_LEVEL = "9171000132101";
		public static final String SUBSTANCE = "105590001";
		public static final String HAS_COMPONENT = "246093002";
		public static final String HAS_SDD_CLASS = "8921000132109";

		public static final String DEFAULT_UNIT = "258666001";
		
		public static final String NAMESPACE_ROOT = "370136006";
		
		public static final String ACCEPTABILITY = "900000000000511003";
		public static final String REFINABILITY_VALUE = "900000000000226000";
		public static final String DESCRIPTION_INACTIVATION_VALUE = "900000000000493001";
		public static final String CONCEPT_INACTIVATION_VALUE = "900000000000481005";

	}
	
	// RF2 effective time format
	public static final String RF2_EFFECTIVE_TIME_FORMAT = "yyyyMMdd";
}
