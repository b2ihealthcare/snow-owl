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
package com.b2international.snowowl.snomed.api.rest;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.b2international.snowowl.snomed.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

/**
 * Holds constants used in multiple test classes.
 * 
 * @since 2.0
 */
public abstract class SnomedApiTestConstants {

	/**
	 * The context-relative base URL for the administrative controller. 
	 */
	public static final String ADMIN_API = "/admin";

	/**
	 * The context-relative base URL for SNOMED CT-related controllers.
	 */
	public static final String SCT_API = "/snomed-ct/v3";

	/**
	 * An acceptability map which specifies that the corresponding description is acceptable in the UK language reference set.
	 */
	public static final Map<String, Acceptability> UK_ACCEPTABLE_MAP = ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE);

	/**
	 * An acceptability map which specifies that the corresponding description is preferred in the UK language reference set.
	 */
	public static final Map<String, Acceptability> UK_PREFERRED_MAP = ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED);

	/**
	 * An acceptability map which specifies that the corresponding description is acceptable in the US language reference set.
	 */
	public static final Map<String, Acceptability> US_ACCEPTABLE_MAP = ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE);

	/**
	 * An acceptability map which specifies that the corresponding description is preferred in the US language reference set.
	 */
	public static final Map<String, Acceptability> US_PREFERRED_MAP = ImmutableMap.of(Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED);

	/**
	 * An acceptability map with an invalid language reference set identifier.
	 */
	public static final Map<String, Acceptability> INVALID_PREFERRED_MAP = ImmutableMap.of("11110000", Acceptability.PREFERRED);

	public static final long POLL_INTERVAL = TimeUnit.MILLISECONDS.toMillis(200L);

	public static final long POLL_TIMEOUT = TimeUnit.SECONDS.toMillis(30L);

	public static final String EXTENSION_PATH = "MAIN/2016-07-31/SNOMEDCT-B2I";

	public static final Joiner PATH_JOINER = Joiner.on('/');

	public static final String OWL_EXPRESSION_1 = 
			"SubClassOf(\n" +
				"ObjectIntersectionOf(\n" +
					"sct:73211009 Diabetes mellitus (disorder)\n" +
					"ObjectSomeValuesFrom(\n" +
						"sct:246075003 Causative agent (attribute)\n" +
						"sct:410942007 Drug or medicament (substance)\n" +
					")\n" +
				")\n" + 
				"sct:8801005 Secondary diabetes mellitus (disorder)\n" +
			")";
	
	public static final String OWL_EXPRESSION_2 =
			"SubClassOf(\n" +
				"ObjectIntersectionOf(\n" +
					"sct:73211009 Diabetes mellitus (disorder)\n" +
					"ObjectSomeValuesFrom(\n" +
						"sct:42752001 Due to (attribute)\n" +
						"sct:64572001 Disease (disorder)\n" +
					")\n" +
				")\n" + 
				"sct:8801005 Secondary diabetes mellitus (disorder)\n" +
			")";
	
	public static final String DOMAIN_CONSTRAINT = "<< 71388002 |Procedure (procedure)|";
	public static final String DOMAIN_CONSTRAINT_2 = "<< ^ 723264001 |Lateralizable body structure reference set (foundation metadata concept)|";
	public static final String PARENT_DOMAIN = "71388002 |Procedure (procedure)|";
	public static final String PROXIMAL_PRIMITIVE_CONSTRAINT = "<< 71388002 |Procedure (procedure)|";
	public static final String PROXIMAL_PRIMITIVE_CONSTRAINT_2 = "<< ^ 723264001 |Lateralizable body structure reference set (foundation metadata concept)|";
	public static final String PROXIMAL_PRIMITIVE_REFINEMENT = "[[1..*]] 260686004 |Method| = [+id(<< 129265001 |Evaluation - action|)]]";
	
	public static final String DOMAIN_TEMPLATE_FOR_PRECOORDINATION = "[[+id(<< 71388002 |Procedure (procedure)|)]]: [[0..*]] { "
			+ "[[1..*]] 260686004 |Method| = [+id(<< 129265001 |Evaluation - action|)]], "
			+ "[[0..1]] 246093002 |Component| = [[+id(<< 123037004 |Body structure (body structure)| "
				+ "OR << 363787002 |Observable entity (observable entity)| "
				+ "OR << 410607006 |Organism (organism)| "
				+ "OR << 105590001 |Substance (substance)| "
				+ "OR << 123038009 |Specimen (specimen)| "
				+ "OR << 260787004 |Physical object (physical object)| "
				+ "OR << 373873005 |Pharmaceutical / biologic product (product)| "
				+ "OR << 419891008 |Record artifact (record artifact)|)]], "
			+ "[[0..1]] 116686009 |Has specimen| = [[+id(<< 123038009 |Specimen (specimen)|)]], "
			+ "[[0..1]] 370129005 |Measurement method| = [[+id(<< 127789004 |Laboratory procedure categorized by method (procedure)|)]], "
			+ "[[0..1]] 370130000 |Property| = [[+id(<< 118598001 |Property of measurement (qualifier value)|)]], "
			+ "[[0..1]] 370132008 |Scale type| = [[+id(<< 30766002 |Quantitative| "
				+ "OR << 26716007 |Qualitative|  "
				+ "OR << 117363000 |Ordinal value| "
				+ "OR << 117365007 |Ordinal or quantitative value|  "
				+ "OR << 117362005 |Nominal value|  "
				+ "OR << 117364006 |Narrative value|  "
				+ "OR << 117444000 |Text value|)]], "
			+ "[[0..1]] 370134009 |Time aspect| = [[+id(<< 7389001 |Time frame (qualifier value)|)]], "
			+ "[[0..1]] 260507000 |Access| = [[+id(<< 309795001 |Surgical access values (qualifier value)|)]], "
			+ "[[0..1]] 363699004 |Direct device| = [[+id(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..1]] 363700003 |Direct morphology| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]], "
			+ "[[0..1]] 363701004 |Direct substance| = [[+id(<< 105590001 |Substance (substance)| "
				+ "OR << 373873005 |Pharmaceutical / biologic product (product)|)]], "
			+ "[[0..1]] 363702006 |Has focus| = [[+id(<< 404684003 |Clinical finding (finding)| "
				+ "OR << 71388002 |Procedure (procedure)|)]], "
			+ "[[0..1]] 363703001 |Has intent| = [[+id(<< 363675004 |Intents (nature of procedure values) (qualifier value)|)]], "
			+ "[[0..1]] 363710007 |Indirect device| = [[+id(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..1]] 363709002 |Indirect morphology| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]], "
			+ "[[0..1]] 260686004 |Method| = [[+id(<< 129264002 |Action (qualifier value)|)]], "
			+ "[[0..1]] 260870009 |Priority| = [[+id(<< 272125009 |Priorities (qualifier value)|)]], "
			+ "[[0..*]] 405815000 |Procedure device| = [[+id(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..*]] 405816004 |Procedure morphology| = [[+id(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]], "
			+ "[[0..*]] 363704007 |Procedure site| = [[+id(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], "
			+ "[[0..1]] 405813007 |Procedure site - Direct| = [[+id(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], "
			+ "[[0..1]] 405814001 |Procedure site - Indirect| = [[+id(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], "
			+ "[[0..1]] 370131001 |Recipient category| = [[+id(<< 125676002 |Person (person)| "
				+ "OR << 35359004 |Family (social concept)| "
				+ "OR << 133928008 |Community (social concept)| "
				+ "OR << 105455006 |Donor for medical or surgical procedure (person)| "
				+ "OR << 389109008 |Group (social concept)|)]], "
			+ "[[0..1]] 246513007 |Revision status| = [[+id(<< 261424001 |Primary operation (qualifier value)| "
				+ "OR << 255231005 |Revision - value (qualifier value)| "
				+ "OR << 257958009 |Part of multistage procedure (qualifier value)|)]], "
			+ "[[0..1]] 425391005 |Using access device| = [[+id(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..*]] 424226004 |Using device| = [[+id(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..1]] 424244007 |Using energy| = [[+id(<< 78621006 |Physical force (physical force)|)]], "
			+ "[[0..1]] 424361007 |Using substance| = [[+id(<< 105590001 |Substance (substance)|)]] }";
		
	public static final String DOMAIN_TEMPLATE_FOR_PRECOORDINATION_2 = "[[+id(<< ^ 723264001 |Lateralizable body structure reference set (foundation metadata concept)|)]]: "
			+ "[[0..1]] 272741003 |Laterality| = [[+id(<< 182353008 |Side (qualifier value)|)]], "
			+ "[[0..*]] 123005000 |Part of| = [[+id(<< 123037004 |Body structure (body structure)|)]]"; 
	
	public static final String DOMAIN_TEMPLATE_FOR_POSTCOORDINATION = "[[+scg(<< 71388002 |Procedure (procedure)|)]]: [[0..*]] { "
			+ "[[1..*]] 260686004 |Method| = [+id(<< 129265001 |Evaluation - action|)]], "
			+ "[[0..1]] 246093002 |Component| = [[+scg(<< 123037004 |Body structure (body structure)| "
				+ "OR << 363787002 |Observable entity (observable entity)| "
				+ "OR << 410607006 |Organism (organism)| "
				+ "OR << 105590001 |Substance (substance)| "
				+ "OR << 123038009 |Specimen (specimen)| "
				+ "OR << 260787004 |Physical object (physical object)| "
				+ "OR << 373873005 |Pharmaceutical / biologic product (product)| "
				+ "OR << 419891008 |Record artifact (record artifact)|)]], "
			+ "[[0..1]] 116686009 |Has specimen| = [[+scg(<< 123038009 |Specimen (specimen)|)]], "
			+ "[[0..1]] 370129005 |Measurement method| = [[+scg(<< 127789004 |Laboratory procedure categorized by method (procedure)|)]], "
			+ "[[0..1]] 370130000 |Property| = [[+scg(<< 118598001 |Property of measurement (qualifier value)|)]], "
			+ "[[0..1]] 370132008 |Scale type| = [[+scg(<< 30766002 |Quantitative| "
				+ "OR << 26716007 |Qualitative|  "
				+ "OR << 117363000 |Ordinal value| "
				+ "OR << 117365007 |Ordinal or quantitative value|  "
				+ "OR << 117362005 |Nominal value|  "
				+ "OR << 117364006 |Narrative value|  "
				+ "OR << 117444000 |Text value|)]], "
			+ "[[0..1]] 370134009 |Time aspect| = [[+scg(<< 7389001 |Time frame (qualifier value)|)]], "
			+ "[[0..1]] 260507000 |Access| = [[+scg(<< 309795001 |Surgical access values (qualifier value)|)]], "
			+ "[[0..1]] 363699004 |Direct device| = [[+scg(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..1]] 363700003 |Direct morphology| = [[+scg(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]], "
			+ "[[0..1]] 363701004 |Direct substance| = [[+scg(<< 105590001 |Substance (substance)| "
				+ "OR << 373873005 |Pharmaceutical / biologic product (product)|)]], "
			+ "[[0..1]] 363702006 |Has focus| = [[+scg(<< 404684003 |Clinical finding (finding)| OR << 71388002 |Procedure (procedure)|)]], "
			+ "[[0..1]] 363703001 |Has intent| = [[+scg(<< 363675004 |Intents (nature of procedure values) (qualifier value)|)]], "
			+ "[[0..1]] 363710007 |Indirect device| = [[+scg(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..1]] 363709002 |Indirect morphology| = [[+scg(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]], "
			+ "[[0..1]] 260686004 |Method| = [[+scg(<< 129264002 |Action (qualifier value)|)]], "
			+ "[[0..1]] 260870009 |Priority| = [[+scg(<< 272125009 |Priorities (qualifier value)|)]], "
			+ "[[0..*]] 405815000 |Procedure device| = [[+scg(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..*]] 405816004 |Procedure morphology| = [[+scg(<< 49755003 |Morphologically abnormal structure (morphologic abnormality)|)]], "
			+ "[[0..*]] 363704007 |Procedure site| = [[+scg(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], "
			+ "[[0..1]] 405813007 |Procedure site - Direct| = [[+scg(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], "
			+ "[[0..1]] 405814001 |Procedure site - Indirect| = [[+scg(<< 442083009 |Anatomical or acquired body structure (body structure)|)]], "
			+ "[[0..1]] 370131001 |Recipient category| = [[+scg(<< 125676002 |Person (person)| "
				+ "OR << 35359004 |Family (social concept)| "
				+ "OR << 133928008 |Community (social concept)| "
				+ "OR << 105455006 |Donor for medical or surgical procedure (person)| "
				+ "OR << 389109008 |Group (social concept)|)]], "
			+ "[[0..1]] 246513007 |Revision status| = [[+scg(<< 261424001 |Primary operation (qualifier value)| "
				+ "OR << 255231005 |Revision - value (qualifier value)| "
				+ "OR << 257958009 |Part of multistage procedure (qualifier value)|)]], "
			+ "[[0..1]] 425391005 |Using access device| = [[+scg(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..*]] 424226004 |Using device| = [[+scg(<< 49062001 |Device (physical object)|)]], "
			+ "[[0..1]] 424244007 |Using energy| = [[+scg(<< 78621006 |Physical force (physical force)|)]], "
			+ "[[0..1]] 424361007 |Using substance| = [[+scg(<< 105590001 |Substance (substance)|)]] }";
	
	public static final String DOMAIN_TEMPLATE_FOR_POSTCOORDINATION_2 = "[[+scg(<< ^ 723264001 |Lateralizable body structure reference set (foundation metadata concept)|)]]: "
			+ "[[0..1]] 272741003 |Laterality| = [[+id(<< 182353008 |Side (qualifier value)|)]]";
	
	public static final String EDITORIAL_GUIDE_REFERENCE = "http://snomed.org/dom386053000";
	
	public static final String DOMAIN_ID = "386053000";
	public static final String DOMAIN_ID_2 = "363787002";
	public static final String ATTRIBUTE_CARDINALITY = "0..*";
	public static final String ATTRIBUTE_CARDINALITY_2 = "0..1";
	public static final String ATTRIBUTE_IN_GROUP_CARDINALITY = "0..1";
	public static final String ATTRIBUTE_IN_GROUP_CARDINALITY_2 = "0..0";
	public static final String RULE_STRENGTH_ID = "723597001";
	public static final String RULE_STRENGTH_ID_2 = "723598006";
	public static final String CONTENT_TYPE_ID = "723596005";
	public static final String CONTENT_TYPE_ID_2 = "723593002";
	
	public static final String RANGE_CONSTRAINT = "<< 442083009 |Anatomical or acquired body structure (body structure)|";
	public static final String RANGE_CONSTRAINT_2 = "<< 49062001 |Device (physical object)|";
	public static final String ATTRIBUTE_RULE = "<< 71388002 |Procedure (procedure)|: [0..*] "
			+ "{ [0..1] 405814001 |Procedure site - Indirect| = << 442083009 |Anatomical or acquired body structure (body structure)| }";
	public static final String ATTRIBUTE_RULE_2 = "<< 71388002 |Procedure (procedure)|: [0..*] "
			+ "{ [0..1] 425391005 |Using access device| = << 49062001 |Device (physical object)| }"; 
			
	public static final String RULE_REFSET_ID = "723562003";
	public static final String RULE_REFSET_ID_2 = "723560006";
	
	private SnomedApiTestConstants() {
		throw new UnsupportedOperationException("This class is not supposed to be instantiated.");
	}
}
