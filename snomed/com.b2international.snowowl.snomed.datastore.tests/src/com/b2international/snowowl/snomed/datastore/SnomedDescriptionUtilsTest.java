/*******************************************************************************
 * Copyright (c) 2020-2021 B2i Healthcare. All rights reserved.
 *******************************************************************************/
package com.b2international.snowowl.snomed.datastore;

import static com.b2international.snowowl.snomed.datastore.CodeSystemResource.GB_LOCALE;
import static com.b2international.snowowl.snomed.datastore.CodeSystemResource.SG_LOCALE;
import static com.b2international.snowowl.snomed.datastore.CodeSystemResource.US_LOCALE;
import static com.b2international.snowowl.snomed.datastore.SnomedDescriptionUtils.indexBestPreferredByConceptId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.b2international.commons.http.ExtendedLocale;
import com.b2international.snowowl.core.branch.Branch;
import com.b2international.snowowl.core.domain.BranchContext;
import com.b2international.snowowl.core.repository.JsonSupport;
import com.b2international.snowowl.core.terminology.TerminologyRegistry;
import com.b2international.snowowl.snomed.common.SnomedConstants.Concepts;
import com.b2international.snowowl.snomed.core.domain.Acceptability;
import com.b2international.snowowl.snomed.core.domain.SnomedDescription;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext;
import com.b2international.snowowl.test.commons.snomed.TestBranchContext.Builder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @since 7.10.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SnomedDescriptionUtilsTest {

	private static List<SnomedDescription> descriptions;

	private static SnomedDescription gbPreferredDescription;
	private static SnomedDescription usPreferredDescription;
	private static SnomedDescription sgPreferredDescription;
	private static SnomedDescription englishAdditionalDescription;
	private static SnomedDescription sgAdditionalDescription;
	private static BranchContext context;

	@BeforeClass
	public static void setup() {
		Builder contextBuilder = TestBranchContext.on(Branch.MAIN_PATH)
				.with(ClassLoader.class, SnomedDescriptionUtilsTest.class.getClassLoader())
				.with(ObjectMapper.class, JsonSupport.getDefaultObjectMapper())
				.with(TerminologyRegistry.class, TerminologyRegistry.INSTANCE);
		
		CodeSystemResource.configureCodeSystem(contextBuilder);
		context = contextBuilder.build();
		
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
	}

	@Test
	public void testGBEnglishOrdering1() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(GB_LOCALE, US_LOCALE, SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testGBEnglishOrdering2() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(GB_LOCALE, SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testGBEnglishOrdering3() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(GB_LOCALE, US_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testGBEnglishOrdering4() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(GB_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering1() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(US_LOCALE, GB_LOCALE, SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering2() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(US_LOCALE, GB_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering3() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(US_LOCALE, SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testUSEnglishOrdering4() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(US_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering1() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(SG_LOCALE, US_LOCALE, GB_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering2() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(SG_LOCALE, GB_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering3() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(SG_LOCALE, US_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testSgEnglishOrdering4() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering1() {
		assertEquals(sgPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(new ExtendedLocale("en", "", "1234"), SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering2() {
		assertEquals(usPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(new ExtendedLocale("en", "", "1234"), US_LOCALE, SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering3() {
		assertEquals(gbPreferredDescription, indexBestPreferredByConceptId(context, descriptions, List.of(new ExtendedLocale("en", "", "1234"), GB_LOCALE, US_LOCALE, SG_LOCALE)).get(Concepts.ROOT_CONCEPT));
	}

	@Test
	public void testCustomLanguageRefsetOrdering4() {
		assertNull(indexBestPreferredByConceptId(context, descriptions, List.of(new ExtendedLocale("en", "", "1234"))).get(Concepts.ROOT_CONCEPT));
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

}
