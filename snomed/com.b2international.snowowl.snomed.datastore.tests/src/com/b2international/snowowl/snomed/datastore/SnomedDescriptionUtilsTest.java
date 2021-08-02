/*******************************************************************************
 * Copyright (c) 2020-2021 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils.indexBestPreferredByConceptId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.TerminologyResource;
import com.b2international.snowowl.core.codesystem.CodeSystem;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.common.SnomedTerminologyComponentConstants;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.snomed.datastore.config.SnomedLanguageConfig;
import com.b2international.snowowl.test.commons.SnomedContentRule;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext.Builder;
import com.b2international.snowowl.test.commons.validation.BaseValidationTest;

/**
 * @since 7.10.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedDescriptionUtilsTest extends BaseValidationTest {

	private static List<SnomedDescription> descriptions;

	private static final ExtendedLocale US_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_US);
	private static final ExtendedLocale GB_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_UK);
	private static final ExtendedLocale SG_LOCALE = new ExtendedLocale("en", "", Concepts.REFSET_LANGUAGE_TYPE_SG);

	private static SnomedDescription gbPreferredDescription;
	private static SnomedDescription usPreferredDescription;
	private static SnomedDescription sgPreferredDescription;
	private static SnomedDescription englishAdditionalDescription;

	private static SnomedDescription sgAdditionalDescription;

	@Override
	protected void configureContext(Builder context) {
		super.configureContext(context);

		gbPreferredDescription = createDescription("1", "first description",
				Map.of(
						Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.PREFERRED,
						Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE
						)
				);

		usPreferredDescription = createDescription("2", "second description",
				Map.of(
						Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE,
						Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.PREFERRED
						)
				);

		sgPreferredDescription = createDescription("3", "third description",
				Map.of(
						Concepts.REFSET_LANGUAGE_TYPE_SG, Acceptability.PREFERRED
						)
				);

		englishAdditionalDescription = createDescription("4", "fourth description",
				Map.of(
						Concepts.REFSET_LANGUAGE_TYPE_UK, Acceptability.ACCEPTABLE,
						Concepts.REFSET_LANGUAGE_TYPE_US, Acceptability.ACCEPTABLE
						)
				);

		sgAdditionalDescription = createDescription("5", "fifth description",
				Map.of(
						Concepts.REFSET_LANGUAGE_TYPE_SG, Acceptability.ACCEPTABLE
						)
				);

		descriptions = List.of(gbPreferredDescription, usPreferredDescription, englishAdditionalDescription, sgPreferredDescription, sgAdditionalDescription);

		SnomedLanguageConfig usConfig = new SnomedLanguageConfig(US_LOCALE.getLanguageTag(), Concepts.REFSET_LANGUAGE_TYPE_US);
		SnomedLanguageConfig ukConfig = new SnomedLanguageConfig(GB_LOCALE.getLanguageTag(), Concepts.REFSET_LANGUAGE_TYPE_UK);
		SnomedLanguageConfig sgConfig = new SnomedLanguageConfig(SG_LOCALE.getLanguageTag(), Concepts.REFSET_LANGUAGE_TYPE_SG);
		
		List<SnomedLanguageConfig> languages = List.of(usConfig, ukConfig, sgConfig);
		
		final CodeSystem cs = new CodeSystem();
		cs.setBranchPath(MAIN);
		cs.setId(SnomedContentRule.SNOMEDCT_ID);
		cs.setSettings(Map.of(SnomedTerminologyComponentConstants.CODESYSTEM_LANGUAGE_CONFIG_KEY, languages));

		context
		.with(TerminologyResource.class, cs);
	}



	@Test
	public void testGBEnglishOrdering1() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(GB_LOCALE, US_LOCALE, SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testGBEnglishOrdering2() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(GB_LOCALE, SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testGBEnglishOrdering3() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(GB_LOCALE, US_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testGBEnglishOrdering4() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(GB_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering1() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(US_LOCALE, GB_LOCALE, SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering2() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(US_LOCALE, GB_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering3() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(US_LOCALE, SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering4() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(US_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering1() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(SG_LOCALE, US_LOCALE, GB_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering2() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(SG_LOCALE, GB_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering3() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(SG_LOCALE, US_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering4() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering1() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(new ExtendedLocale("en", "", "1234"), SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering2() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(new ExtendedLocale("en", "", "1234"), US_LOCALE, SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering3() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(descriptions, List.of(new ExtendedLocale("en", "", "1234"), GB_LOCALE, US_LOCALE, SG_LOCALE), context()).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering4() {
		assertNull(indexBestPreferredByConceptId(descriptions, List.of(new ExtendedLocale("en", "", "1234")), context()).get(Concepts.ROOT_CONCEPT));
	}

	@SuppressWarnings("deprecation")
	private static SnomedDescription createDescription(String id, String term, Map<String, Acceptability> acceptabilityMap) {
		SnomedDescription description = new SnomedDescription(id);
		description.setTerm(term);
		description.setActive(true);
		description.setConceptId(Concepts.ROOT_CONCEPT);
		description.setAcceptabilityMap(acceptabilityMap);
		return description;
	}

	@Override
	protected Map<String, String> getTestCodeSystemPathMap() {
		return Map.of(SnomedContentRule.SNOMEDCT_ID, MAIN);
	}

}
